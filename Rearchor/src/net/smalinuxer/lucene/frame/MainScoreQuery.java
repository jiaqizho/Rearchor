package net.smalinuxer.lucene.frame;

import java.io.IOException;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.queries.CustomScoreProvider;
import org.apache.lucene.queries.CustomScoreQuery;
import org.apache.lucene.queries.function.FunctionQuery;
import org.apache.lucene.search.Query;

public class MainScoreQuery extends CustomScoreQuery {

	public MainScoreQuery(Query subQuery) {
		super(subQuery);
	}

	public MainScoreQuery(Query subQuery, FunctionQuery scoringQuery) {
		super(subQuery, scoringQuery);
	}
	
	public MainScoreQuery(Query subQuery, FunctionQuery[] scoringQueries) {
		super(subQuery, scoringQueries);
	}

	@Override
	protected CustomScoreProvider getCustomScoreProvider(
			AtomicReaderContext context) throws IOException {
		return new MainScoreProvider(context);
	}
	
}
