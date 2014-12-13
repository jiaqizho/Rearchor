package net.smalinuxer.lucene.frame;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import net.smalinuxer.lucene.frame.IndexWriterQueue.IndexData;
import net.smalinuxer.lucene.utils.LuceneConfig;
import net.smalinuxer.lucene.utils.MMSegAnalyzer;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class RetinIndexBuilder {
	
	//Single
	private static IndexWriter writer;
	
	public RetinIndexBuilder() {
		synchronized(new Object()){
			if(writer == null){
				String path = System.getProperty("user.dir") + LuceneConfig.LUCENE_STORE_DIR;
				File file = new File(path);
				if(!file.exists()){
					file.mkdirs();
				}
				
				try {
					/*if(IndexWriter.isLocked(FSDirectory.open(file))){
						IndexWriter.unlock(FSDirectory.open(file));
		            }*/
					IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_2, new MMSegAnalyzer());
					config.setWriteLockTimeout(100000);
					config.setOpenMode(OpenMode.CREATE_OR_APPEND);
					writer = new IndexWriter(FSDirectory.open(file), config);
				} catch (IOException e) {
					e.printStackTrace();
				} finally{
					
				}
			} 
		}
	}
	
	public void addDoc(IndexData data){
		if(writer != null){
			try{
				Document doc = new Document();
				doc.add(new StringField("url", data.url , Field.Store.YES));
				doc.add(new TextField("content", removeHtmlTag(data.content) , Field.Store.YES));
				doc.add(new StringField("title", data.title , Field.Store.YES));
				doc.add(new IntField("score", 1, Field.Store.YES));
				doc.add(new IntField("readnum", 0, Field.Store.YES));
				doc.add(new StringField("date", 
						DateTools.dateToString(new java.util.Date(),DateTools.Resolution.MILLISECOND), Field.Store.YES));
				writer.addDocument(doc);
			} catch (IOException e) {
				e.printStackTrace();
			} finally{
				try {
					writer.commit();
				} catch (IOException e) {
					try {
						writer.rollback();
					} catch (IOException e1) {
					}
				}
			}
		} else {
			System.err.println("RetinIndexBuilder.java");
		}
	}
	
	public synchronized void modifyDoc(IndexData oldData,IndexData newData) throws IOException{
		if(writer != null){
			delete(new Term("url",oldData.url));
			addDoc(newData);
		} else {
			System.err.println("RetinIndexBuilder.java");
		}
	}
	
	/**
	 * 建议用这个
	 * 因为别的delete相对会比较不适合
	 * @param url
	 * @throws IOException
	 */
	public synchronized void delete(String url) throws IOException {
		this.delete(new Term("url",url));
	}
	
	public synchronized void delete(Term term) throws IOException {
		if(writer != null){
			try{
				writer.deleteDocuments(term);	
				forceDelete();
			} finally{
				writer.commit();
			}
		} else {
			System.err.println("RetinIndexBuilder.java");
		}
	}
	
	public synchronized void delete(Term... term) throws IOException {
		
		if(writer != null){
			try{
				writer.deleteDocuments(term);	
				forceDelete();
			} finally{
				writer.commit();
			}
		} else {
			System.err.println("RetinIndexBuilder.java");
		}
	}
	
	private void forceDelete() throws IOException {
		writer.forceMergeDeletes();
		writer.commit();
	}
	
	private void stop() {
		try {
			if(writer != null){
				writer.close();
				writer = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	

	/**
	 * 删除Html标签
	 * 
	 * @param inputString
	 * @return
	 */
	public static String removeHtmlTag(String inputString) {
		if (inputString == null)
			return null;
		String htmlStr = inputString; // 含html标签的字符串
		String textStr = "";
		java.util.regex.Pattern p_script;
		java.util.regex.Matcher m_script;
		java.util.regex.Pattern p_style;
		java.util.regex.Matcher m_style;
		java.util.regex.Pattern p_html;
		java.util.regex.Matcher m_html;
		java.util.regex.Pattern p_special;
		java.util.regex.Matcher m_special;
		try {
			// 定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>
			String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";
			String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>";
			String regEx_html = "<[^>]+>";
			String regEx_special = "\\&[a-zA-Z]{1,10};";
			p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
			m_script = p_script.matcher(htmlStr);
			htmlStr = m_script.replaceAll(""); // 过滤script标签
			p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
			m_style = p_style.matcher(htmlStr);
			htmlStr = m_style.replaceAll(""); // 过滤style标签
			p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
			m_html = p_html.matcher(htmlStr);
			htmlStr = m_html.replaceAll(""); // 过滤html标签
			p_special = Pattern
					.compile(regEx_special, Pattern.CASE_INSENSITIVE);
			m_special = p_special.matcher(htmlStr);
			htmlStr = m_special.replaceAll(""); // 过滤特殊标签
			textStr = htmlStr;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return textStr;// 返回文本字符串
	}
	
	
/*	
	*//**
	 *  测试
	 * @param args
	 *//*
	public static void main(String[] args) {
		RetinIndexBuilder builder = new RetinIndexBuilder();
		
		 * 添加测试
		 * IndexData data = new IndexData();
		data.url = "http://www.baidu.com";
		data.title = "百度十下,你也不知道";
		data.content = "fuck you!";
		builder.addDoc(data);
	
		 删除测试
		try {
			builder.delete(new Term("url","http://www.baidu.com"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		测试修稿		
		IndexData data = new IndexData();
		data.url = "http://www.baidu.com";
		data.title = "百度十下,你也不知道";
		data.content = "fuck you!";
		builder.addDoc(data);
		
		IndexData data2 = new IndexData();
		data2.url = "http://www.baiduabc.com";
		data2.title = "百度十下,你也不知道";
		data2.content = "fuck youddddddddddd!";
		builder.addDoc(data2);
		
		IndexData data3 = new IndexData();
		data3.url = "http://www.google.com";
		data3.title = "谷歌一下,你就知道";
		data3.content = "hello";
	
		readIndexNum();
		
		try {
			builder.modifyDoc(data,data3);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		readIndexNum();
	}
	
	
	
	
	/**
	 * 测试
	 * @deprecated
	 
	private static void readIndexNum() {
		IndexReader reader = null;
		try {
			String path = System.getProperty("user.dir") + LuceneConfig.LUCENE_STORE_DIR;
			Directory directory = FSDirectory.open(new File(path));
			reader = IndexReader.open(directory);
			System.out.println("numDocs : " + reader.numDocs());	//可使用
			System.out.println("maxDoc : " + reader.maxDoc());	//最大
			System.out.println("numDeletedDocs : " + reader.numDeletedDocs());	//被删除
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try{
			if(reader != null)
				reader.close();
			} catch(IOException e){
			}
		}
	}*/
}