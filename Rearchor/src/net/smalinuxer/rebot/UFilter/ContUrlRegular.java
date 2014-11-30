package net.smalinuxer.rebot.UFilter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 抓取内容中的url
 * @author user
 */
public class ContUrlRegular extends StandardFilter implements UFilter<StandardFilter.Substance>{

	@Override
	public Substance filter(Substance obj) {
		String content = obj.Content;
		Pattern pattern = Pattern.compile("((?:http|https)(?::\\/{2}[\\w]+)(?:[\\/|\\.]?)(?:[^\\s\"/\u4E00-\u9FA5<]*))");
		Matcher matcher = pattern.matcher(content);
		while(matcher.find()){
			obj.urls.add(matcher.group(1));
		}
		return obj;
	}
	
	/*
	public static void main(String[] args) {
		//[\u4E00-\u9FA5|\\s|<]+
		Pattern pattern = Pattern.compile("((?:http|https)(?::\\/{2}[\\w]+)(?:[\\/|\\.]?)(?:[^\\s\"/\u4E00-\u9FA5<]*))");
		Matcher matcher = pattern.matcher("<p>http://dev.umeng.com.cn</p>");
		while(matcher.find()){
			System.out.println(matcher.group(1));
		}
	}*/
}
