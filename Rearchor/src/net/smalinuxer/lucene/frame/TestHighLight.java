package net.smalinuxer.lucene.frame;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Random;

import net.smalinuxer.lucene.utils.MMSegAnalyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.IntField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;


public class TestHighLight {
	
	private static final String STORE_DIR = "C:\\Users\\user\\Desktop\\android开源\\me.smali.opensource\\Lucene\\test_store";

	private static final String INDEXED_DIR = "C:\\Users\\user\\Desktop\\android开源\\me.smali.opensource\\Lucene\\test_chinese";
	
	public TestHighLight(){
		
		try {
			directory = FSDirectory.open(new File(STORE_DIR));
			reader = DirectoryReader.open(directory);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private DirectoryReader reader;
	
	private Directory directory;
	
	private IndexSearcher getSearcher() {
		try{
			if(reader != null){
				DirectoryReader nr = DirectoryReader.openIfChanged(reader);
				if(nr != null){
					reader.close();
					reader = nr;
				}
			} else {
				reader = DirectoryReader.open(directory);
			}
		} catch(IOException e){
			e.printStackTrace();
		}
		IndexSearcher searcher = new IndexSearcher(reader);
		return searcher;
	}
	
	/**
	 * ��������
	 * 
	 */
	public void index() {
		IndexWriter writer = null;
		try {
			// ���������
			directory = FSDirectory.open(new File(STORE_DIR));
			Analyzer an = new MMSegAnalyzer();
			
			// �������������ļ�
			IndexWriterConfig config = new IndexWriterConfig(
					Version.LUCENE_4_10_2, an);
			config.setOpenMode(OpenMode.CREATE);
			
			// ��������д��
			writer = new IndexWriter(directory, config);
			File indexedDir = new File(INDEXED_DIR);
			//��STORE_DIR Ŀ¼�µ��ļ�����������,���ұ��浽INDEXED_DIRĿ¼��
			
			for (File file : indexedDir.listFiles()) {
				Document doc = new Document();
				doc.add(new Field("Content", readFile(file), Field.Store.YES,Index.ANALYZED));
				doc.add(new Field("fileName", file.getName(), Field.Store.YES,Index.NOT_ANALYZED));
				doc.add(new Field("Path", file.getAbsolutePath(),Field.Store.YES, Index.NOT_ANALYZED));
				doc.add(new IntField("score", new Random().nextInt(100),Field.Store.YES));
				writer.addDocument(doc);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			try {
				if (writer != null)
					writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	

	public String readFile(File file) throws IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		StringBuffer sb = new StringBuffer();
		String line = null;
		while((line = br.readLine()) != null){
			sb.append(line);
			sb.append("\r\n");
		}
		return sb.toString();
	}
	
	
	/**
	 * ����
	 * @param analyzer
	 * @param searcher
	 * @throws IOException
	 * @throws InvalidTokenOffsetsException
	 */
	public void searToHighlighterCss(Analyzer analyzer,IndexSearcher searcher) throws IOException, InvalidTokenOffsetsException{  
        Term term =new Term("Content", new String("免费".getBytes(),"GBK"));//��ѯ��������˼����Ҫ�����Ա�Ϊ���������  
        TermQuery query =new TermQuery(term);  
        TopDocs docs =searcher.search(query, 10);//����  
          
        /**�Զ����ע�����ı���ǩ*/  
        SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<span class=\"hightlighterCss\">","</span>");  
        /**����QueryScorer*/  
        QueryScorer scorer=new QueryScorer(query);  
        /**����Fragmenter*/  
        Fragmenter fragmenter = new SimpleSpanFragmenter(scorer);  
        Highlighter highlight=new Highlighter(formatter,scorer);  
        highlight.setTextFragmenter(fragmenter);  
        
        for(ScoreDoc doc:docs.scoreDocs){//��ȡ���ҵ��ĵ����������
            Document document =searcher.doc(doc.doc);
            String value = document.getField("Content").toString();
            TokenStream tokenStream = analyzer.tokenStream("Content", new StringReader(value));    
            String str1 = highlight.getBestFragment(tokenStream, value);    
            System.out.println(str1);
        }  
    }
	
	/***
	 * 
	 * doc.add(new Field("Content", new FileReader(file)));
				doc.add(new Field("fileName", file.getName(), Field.Store.YES,Index.NOT_ANALYZED));
				doc.add(new Field("Path", file.getAbsolutePath(),Field.Store.YES, Index.NOT_ANALYZED));
				doc.add(new IntField("score", new Random().nextInt(100),Field.Store.YES));
	 * 
	 * 
	 * @param args
	 * @throws InvalidTokenOffsetsException 
	 * @throws IOException 
	 */
	public static void main(String[] args) {
		new TestHighLight().index();
		try {
			new TestHighLight().searToHighlighterCss(new MMSegAnalyzer(), new TestHighLight().getSearcher());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidTokenOffsetsException e) {
			e.printStackTrace();
		}
	}
}
