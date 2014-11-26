package net.smalinuxer.UFilter;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @see LabelRegular
 * @author user
 *
 */
public class RelaPathRegular extends StandardFilter{

	@Override
	public Substance filter(Substance obj) {
		URL dns = null; 
		try {
			dns = new URL(obj.url);
		} catch (MalformedURLException e) {
			// TODO 非常大的异常
			e.printStackTrace();
		}
		for(String url : obj.urls){
			if(isRela(url)){
				relative2Absolute(obj,url,dns);
			}
		}
		return obj;
	}

	/**
	 * 由于使用的是CopyOnWriteArrayList所以是尅添加的url的在遍历的时候
	 * @param obj
	 * @param url2
	 */
	private void relative2Absolute(Substance obj, String url2,URL absoluteUrl) {
		URL parseUrl = null; 
		try {
			parseUrl = new URL(absoluteUrl,url2);
		} catch (MalformedURLException e) {
			//TODO LOG 
			return ;
		}
		if(parseUrl != null){
			obj.urls.add(parseUrl.toString());
		}
	}

	private boolean isRela(String url) {
		Pattern pattern = Pattern.compile("javascript:.*");
		Matcher matcher = pattern.matcher(url);
		return !matcher.matches();
	}
}
