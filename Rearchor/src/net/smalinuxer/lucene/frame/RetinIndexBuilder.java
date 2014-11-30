package net.smalinuxer.lucene.frame;

import java.io.File;
import java.io.IOException;

import net.smalinuxer.lucene.frame.IndexWriterQueue.IndexData;
import net.smalinuxer.lucene.utils.LuceneConfig;
import net.smalinuxer.lucene.utils.MMSegAnalyzer;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
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
					//不知道有啥用
					if(IndexWriter.isLocked(FSDirectory.open(file))){
						IndexWriter.unlock(FSDirectory.open(file));
		            }
					IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_2, new MMSegAnalyzer());
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
		System.out.println(data);
		if(writer != null){
			try{
				Document doc = new Document();
				doc.add(new StringField("url", data.url , Field.Store.YES));
				doc.add(new StringField("content", data.content , Field.Store.YES));
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