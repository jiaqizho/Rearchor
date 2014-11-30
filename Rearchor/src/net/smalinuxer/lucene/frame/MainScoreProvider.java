package net.smalinuxer.lucene.frame;

import java.io.IOException;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.queries.CustomScoreProvider;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.FieldCache.Ints;

/**
 * url
 * content
 * title
 * score
 * readnum
 * date
 * 操作评分
 */
public class MainScoreProvider extends CustomScoreProvider {

	Ints scores;
	
	public MainScoreProvider(AtomicReaderContext context) {
		super(context);
		try {
			scores = FieldCache.DEFAULT.getInts(context.reader(), "score", true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * subQueryScore: mmseg给的评分
	 * valSrcScore: no use
	 */
	@Override
	public float customScore(int doc, float subQueryScore, float valSrcScore)
			throws IOException {
		//如何更具doc获取相应的field值
		int score = scores.get(doc);
		
//		System.out.println("subQueryScore:" + subQueryScore + "----score:" + score);
		return super.customScore(doc, subQueryScore, valSrcScore);
	}
	
	
	private int calcKMP(String father ,String sub) {
		int count = 0;
		int start = 0;
		while (father.indexOf(sub, start) >= 0 && start < father.length()) {
			count++;
			start = father.indexOf(sub, start) + sub.length();
		}
		return count;
	}
}
