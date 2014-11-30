package net.smalinuxer.lucene.utils;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;

import com.chenlb.mmseg4j.Dictionary;
import com.chenlb.mmseg4j.MaxWordSeg;
import com.chenlb.mmseg4j.Seg;
import com.chenlb.mmseg4j.analysis.ComplexAnalyzer;
import com.chenlb.mmseg4j.analysis.MaxWordAnalyzer;
import com.chenlb.mmseg4j.analysis.SimpleAnalyzer;

/**
 * 
 */
public class MMSegAnalyzer extends Analyzer {

	protected Dictionary dic;

	public MMSegAnalyzer() {
		dic = Dictionary.getInstance();
	}

	public MMSegAnalyzer(String path) {
		dic = Dictionary.getInstance(path);
	}
	public MMSegAnalyzer(File path) {
		dic = Dictionary.getInstance(path);
	}

	public MMSegAnalyzer(Dictionary dic) {
		super();
		this.dic = dic;
	}

	protected Seg newSeg() {
		return new MaxWordSeg(dic);
	}

	public Dictionary getDict() {
		return dic;
	}

	@Override
	protected TokenStreamComponents createComponents(String fieldName, java.io.Reader reader) {
		return new TokenStreamComponents(new MMSegTokenizer(newSeg(), reader));
	}

}