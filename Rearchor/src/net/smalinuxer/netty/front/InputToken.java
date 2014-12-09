package net.smalinuxer.netty.front;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import net.smalinuxer.lucene.utils.MMSegAnalyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.Term;

@Deprecated
public class InputToken {

	public List<String> iteratorToken(String str , Analyzer analyzer) {
		TokenStream stream = null;
		List<String> list = new ArrayList<String>();
		try{
			stream = analyzer.tokenStream("Null", new StringReader(str));
			CharTermAttribute attr = stream.addAttribute(CharTermAttribute.class);
			//http://tharindu-rusira.blogspot.com/2013/12/apache-lucene-46-tokenstream-contract.html
			//stream.reset();
			while(stream.incrementToken()){
				System.out.print("[" + attr.toString() + "]  ");
				list.add(attr.toString());
			}
			//stream.end();
		}catch(IOException e){
			e.printStackTrace();
		} finally{
			try{
				if(stream != null){
					stream.close();
					stream = null;
				}
			} catch(IOException e){
			}
		}
		return list;
	}
	
	public List<String> iteratorToken(String str) {
		return iteratorToken(str,new MMSegAnalyzer());
	}
	
/*	public static void main(String[] args) {
		String str = "你好啊 今天天气不错啊 ";
		new InputToken().iteratorToken(str, new MMSegAnalyzer());
	}*/
}
