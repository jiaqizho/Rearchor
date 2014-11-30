package net.smalinuxer.lucene.frame;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
/**
 * 高级搜索扩展
 */
public class KeyWordQueryParse extends QueryParser{
	
	public KeyWordQueryParse(String f, Analyzer a) {
		super(f, a);
	}
	
	@Override
	protected org.apache.lucene.search.Query getFuzzyQuery(String field,
			String termStr, float minSimilarity) throws ParseException {
		throw new ParseException("no use FuzzyQuery");
	}
	
	@Override
	protected org.apache.lucene.search.Query getWildcardQuery(String field,
			String termStr) throws ParseException {
		throw new ParseException("no use WildcardQuery");
	}
	
}
