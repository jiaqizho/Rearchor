package net.smalinuxer.rebot.UFilter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import net.smalinuxer.rebot.UFilter.StandardFilter.Substance;

/***
 *
 * chain not only can set the filter rule
 * to convince , you can set the analyzer if you make one extends UAnalyzer
 * 
 * the Process is:
 * 		 UReactChian -> set the filter or analyzer
 * 					   丨
 * 					 ↓
 * 		 		begin filter
 *                   丨
 * 					 ↓ 
 * 		     begin deduplication
 * 
 * deduplication is the all top abs filter write
 * we will iteration to find all top abs filter invoke deduplication
 * 
 * next step i should add the filter in poll
 * 
 * @author smalinuxer@gmail.com
 * @param <T> see the UFilter
 * @version 0.1 
 * 	 
 */
public class UReactChian<T> implements UFilter<T>{

	protected List<UFilter<T>> collection = null;
	
	protected UAnalyzer<? extends UFilter<T>> mAnalyzer = null;
	
	private volatile boolean reAble = true;	//允许重复规则 
	
	public UReactChian(){
		collection = new ArrayList<UFilter<T>>();
	}
	
	public UReactChian<T> add(UFilter<T> filter){
		collection.add(filter);
		return this;
	}
	
	@Override
	public T filter(T obj) throws RegularRepeatException {
		if(!hasRepeat(collection)){
			throw new RegularRepeatException();
		}
		for(UFilter<T> filter:collection){
			filter.filter(obj);
		}
		
		return deduplication(obj);
	}

	/**
	 * 重复过滤准则(deduplication)
	 * invoke all filter's superClass deduplication
	 * don't make the recursion 
	 */
	@Override
	public T deduplication(T obj) {
		HashSet<String> set = new HashSet<String>();
		List<Integer> list = new ArrayList<Integer>();
		for(int u = 0 ; u < collection.size() ; u++){
			UFilter<T> filter = collection.get(u);
			Class<?> clazz = filter.getClass();
			while(clazz.getSuperclass() != Object.class ){
				clazz = filter.getClass().getSuperclass();
			}
			if(hasDeduption(clazz.getName(),set)){
				list.add(u);
			}
			set.add(clazz.getName());
		}
		
		for(int r : list){
			obj = collection.get(r).deduplication(obj);
		}
		return obj;
	}
	
	private boolean hasDeduption(String name, HashSet<String> set) {
		Iterator<String> interator = set.iterator();
		while(interator.hasNext()){
			String str = interator.next();
			if(str.equals(name)){
				return false;
			}
		}
		return true;
	}

	public void setAnalyzer(UAnalyzer<? extends UFilter<T>> analyzer){
		if(!collection.addAll((Collection<? extends UFilter<T>>) analyzer.getList())){
			for(UFilter<T> filter :mAnalyzer.getList()){
				collection.add(filter);
			}
		}
	}

	/**
	 * 
	 * @param list
	 * @return true hasRepeat false noReapeat
	 */
	protected boolean hasRepeat(List<?> list){
		if(reAble){
			return true;
		} 
		HashSet<String> set=new HashSet<String>();
		for(Object i : list)
			set.add(i.getClass().getName());
		if(set.size() == list.size()){
			return true;
		} 
		return false;
	}
	
	/* 
	 * test
	 * and interface
	 **/ 
	public static void main(String[] args) throws Exception {
		UReactChian<Substance> chian = new UReactChian<Substance>();
		chian.setAnalyzer(StandardFilter.produceAnalyzer());
		Substance sub = new Substance();
		
		StringBuffer sb = new StringBuffer();
		URL url = new URL("http://www.baidu.com");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line = "";
		while((line = in.readLine()) != null){
			sb.append(line+"\r\n");
		}
		
		sub.url = url.toString();
		sub.Content = sb.toString();
		Substance sb2 = chian.filter(sub);
		sb2.printAllTarget();
	}
	
	/**
	 * don't use it
	 * @return alawys return null 
	 */
	@Deprecated
	@Override
	public UAnalyzer<T> invokeAnalyzer() {
		return null;
	}
	
}
