package net.smalinuxer.rebot;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import net.smalinuxer.lucene.frame.IndexWriterQueue;
import net.smalinuxer.rebot.UFilter.RegularRepeatException;
import net.smalinuxer.rebot.UFilter.StandardFilter;
import net.smalinuxer.rebot.UFilter.StandardFilter.Substance;
import net.smalinuxer.rebot.UFilter.UAnalyzer;
import net.smalinuxer.rebot.UFilter.UFilter;
import net.smalinuxer.rebot.UFilter.UReactChian;
import net.smalinuxer.rebot.frame.DataReaction;
import net.smalinuxer.rebot.frame.DataReaction.ReactData;
import net.smalinuxer.rebot.frame.ReactorExecutors;
import net.smalinuxer.rebot.frame.ReactorShutDownException;
import net.smalinuxer.rebot.frame.TimeCallable;

/**
 * expand : 
 * 	1.Generics
 * 
 * 
 * 		TimeCallable<ReactData> callable = new TimeCallable<ReactData>(new DataReaction("http://www.baidu.com"),new ReactorExecutors<ReactData>());
		try {
			Future<ReactData> future = callable.start();
			ReactData data = future.get();
			System.out.println(data != null ? data.content : "null");
		} catch (ReactorShutDownException | InterruptedException | ExecutionException e1) {
			//inposiable
			e1.printStackTrace();
		} 
 * 
 * 
 * UReactChian<Substance> chian = new UReactChian<Substance>();
		chian.setAnalyzer(StandardFilter.produceAnalyzer());
		Substance sub = new Substance();
		Substance sb2 = chian.filter(sub);
		sb2.printAllTarget();
 *
 *
 *
 *http://www.jxgxyz.com/
 *
 *
 *
 *1.二进制文件
 *2.矫正
 *3.类似voll的东西
 *
 *
 *
 */
public class Rebotor implements Searchor {
	
	private RAMQuickSet<String> set;
	
	private ReactorExecutors<ReactData> threadPool = null;
	
	private UReactChian<Substance> mChain = null;
	
	public Rebotor(ReactorExecutors<ReactData> threadPool,
			UReactChian<Substance> mChain) {
		this.threadPool = threadPool;
		this.mChain = mChain;
		init();
	}

	public Rebotor() {
		this(new ReactorExecutors<ReactData>(),new UReactChian<Substance>());
	}
	
	/**
	 * 
	 * init
	 * test 方法 
	 * 
	 */
	private void init() {
		set = new RAMQuickSet<String>();
		mChain.setAnalyzer(StandardFilter.produceAnalyzer());
		mChain.add(new UFilter<StandardFilter.Substance>() {
			
			@Override
			public UAnalyzer<?> invokeAnalyzer() {
				return null;
			}
			
			@Override
			public Substance filter(Substance obj) throws RegularRepeatException {
				for(String str : obj.urls){
					URL url = null;
					try {
						url = new URL(str);
					} catch (MalformedURLException e) {
						try {
							url = new URL("http://www.smalinuxer.net/");
						} catch (MalformedURLException e1) {
						}
					}
					if(loadIndex(str) || !url.getHost().equals("www.smalinuxer.net") || isfrag(str)){
						obj.urls.remove(str);
					}
				}
				return obj;
			}
			
			@Override
			public Substance deduplication(Substance obj) {
				return obj;
			}
		});
		
	}
	
	
	protected static boolean isfrag(String str) {
		for(int i = 0 ; i < str.length() ; i++){
			if(str.charAt(i) == '#'){
				return true;
			}
		}
		return false;
	}

	protected Substance add(String url){
		TimeCallable<ReactData> callable = new TimeCallable<ReactData>(new DataReaction(url),threadPool);
		Substance sub = null;
		try {
			Future<ReactData> future = callable.start();
			ReactData data = future.get();
			if(data != null && data.content != null){
				sub = new Substance();
				sub.url = url;
				sub.Content = data.content;
				sub = mChain.filter(sub);
			} else {
				
			}
		} catch (ReactorShutDownException | InterruptedException | ExecutionException e1) {
			System.err.println(url);
		} catch (RegularRepeatException e) {
			e.printStackTrace();
		} catch (CancellationException e){
			System.err.println(url);
		}finally{
			buildIndex(url);
		}
		return sub;
	}

	private boolean loadIndex(String str){
		return set.contains(str);
	}
	
	/**
	 * fail
	 * @param url
	 */
	private void buildIndex(String url) {
		set.add(url);
		// TODO Auto-generated method stub
	}

	/**
	 * succes
	 * @param data
	 */
	private void buildIndex(ReactData data) {
		set.add(data.url);
		// TODO Auto-generated method stub
	}
	
	static Rebotor r = new Rebotor();
	
	public static void main(String[] args) {
//		long l = System.currentTimeMillis();
		Substance sub = r.add("http://www.smalinuxer.net/");
		new Rebotor().test(sub);
//		System.out.println("finish: time:" + (System.currentTimeMillis() - l));
		
	}

	/**
	 * 
	 * 一定需要被调用
	 */
	private void stop(){
		try {
			set.store();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private IndexWriterQueue queue = new IndexWriterQueue(); 
	
	void test(Substance sub){
		if(sub == null){
			return ;
		}
		
		if(sub.url != null && sub.Content != null){
			queue.add(sub);
		}
		
		for(int i = 0 ; i < sub.urls.size() ; i++){
			test(r.add(sub.urls.get(i)));
		}
		
	}
}
