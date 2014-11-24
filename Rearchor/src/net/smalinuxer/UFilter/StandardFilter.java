package net.smalinuxer.UFilter;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.smalinuxer.UFilter.UAnalyzer.Orderful;

/**
 * 如果我获得所有正确url
 * 	1.直接超链接抓取比如<a herf="http://www.baidu.com">
 * 	2.间接<a herf="../../abchtml">需要被重新组合
 * ------------
 *  3.直接出现的网页http://baidu.com/woijdwoiqjdoqfd-wdqwdwqqwf/dwqndiqwbiqwqndwqn
 *  	然后结尾很难过滤出来
 *  4.无http开头,直接www.abc.com(舍弃)
 * @author user
 *
 */
public abstract class StandardFilter implements UFilter<StandardFilter.Substance> {

	public static class Substance{
		
		public String url;
		
		public String Content;
		
		public List<String> urls = new CopyOnWriteArrayList<String>();	//add urls while for each
		
		public void printAllTarget() {
			for(String str : urls){
				System.out.println(str);
			}
		};
		
	}

	@Override
	public abstract Substance filter(Substance obj);

	/**
	 * Only the top absFilter can write	
	 * 
	 */
	@Override
	public final Substance deduplication(Substance obj){
		List<String> list = new CopyOnWriteArrayList<String>();
		//TODO Log
		HashSet<String> set = new HashSet<String>();
		for(String content : obj.urls){
			if(isLegalUrl(content)){
				if(!set.contains(content)){
					list.add(content);
				}
				set.add(content);
			}
		}
		obj.urls = list;
		return obj;
	}
	
	protected boolean isLegalUrl(String str){
	/*	String regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]" ;
		Pattern patt = Pattern.compile(regex );
		Matcher matcher = patt.matcher(str);
		boolean isMatch = matcher.matches();
		if (!isMatch) {
			return false;
		} else {
		    return true; 
		}*/
		if(str.substring(0, 1).equals("h") | str.substring(0, 1).equals("H")){
			return true;
		}
		return false;
	}
	
	@Override
	public UAnalyzer<StandardFilter> invokeAnalyzer() {
		return AnalyzerFactory.produceAnalyzer();
	}
	
	public static UAnalyzer<StandardFilter> produceAnalyzer(){
		return AnalyzerFactory.produceAnalyzer();
	}
	
	public static class AnalyzerFactory {
		
		public static UAnalyzer<StandardFilter> produceAnalyzer(){
			UAnalyzer<StandardFilter> analyzer = new UAnalyzer<StandardFilter>(){

				@Override
				public void leaderOrder() throws ReflectiveOperationException {
					if(getmOrder() == Orderful.NOORDER){
						return ;
					} else {
						throw new ReflectiveOperationException();
					}
				}

				@Override
				public void init(List<StandardFilter> list) {
					list.add(new LabelRegular(new RelaPathRegular()));
					list.add(new ContUrlRegular());
				}
				
			};
			analyzer.setmOrder(Orderful.NOORDER);
			return analyzer;
		}
	}
}
