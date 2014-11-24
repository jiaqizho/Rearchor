package net.smalinuxer.UFilter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LabelRegular extends StandardFilter implements UFilter<StandardFilter.Substance> {

	private StandardFilter mRelaPathFilter = null;
	
	public LabelRegular(StandardFilter relaPathFilter){
		mRelaPathFilter = relaPathFilter;
	}
	
	public LabelRegular(){
		mRelaPathFilter = new RelaPathRegular();
	}
	
	@Override
	public Substance filter(Substance obj) {
		String content = obj.Content;
		Pattern pattern = Pattern.compile("<a[^>]+href=\"([^>\"]+)[^>]*\">");
		Matcher matcher = pattern.matcher(content);
		while(matcher.find()){
			obj.urls.add(matcher.group(1));
		}
		return mRelaPathFilter.filter(obj);
	}
}
