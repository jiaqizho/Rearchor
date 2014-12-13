package net.smalinuxer.rebot;

import io.netty.handler.codec.http.HttpServerCodec;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import net.smalinuxer.lucene.frame.IndexWriterQueue;
import net.smalinuxer.lucene.frame.RetinReaderFinder;
import net.smalinuxer.netty.front.HttpFontServer;
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
	
	private IndexWriterQueue queue = new IndexWriterQueue(); 
	
	private static HttpFontServer httpServer = null;
	
	static {
		httpServer = new HttpFontServer(Searchor.LOCAL_PORT_SERVER);	//开启了font
	}
	
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
	 * init
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
				//最好不要再这个方法里面执行,因为会增加循环次数
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
	 * @param url
	 */
	private void buildIndex(String url) {
		set.add(url);
	}


	public static void main(String[] args) {
//		long l = System.currentTimeMillis();
		Rebotor re = new Rebotor();
		re.recurAdd(re,re.add("http://www.smalinuxer.net/"));
		re.reCycle();
//		System.out.println("finish: time:" + (System.currentTimeMillis() - l));
		System.out.println();
	}

	/**
	 * 结束状态
	 */
	private void stop(){
		try {
			set.store();
			threadPool.shutdown();
			queue.shutdown();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static int count = 1;
	
	private void reCycle() {
		stop();
		final Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				System.out.println("times : " + count++);
				Rebotor re = new Rebotor();
				re.recurAdd(re,re.add("http://www.smalinuxer.net/"));
				re.reCycle();
				timer.cancel();
			}
		}, Searchor.LONG_TIME_TO_REBUILD);
	}
	
	void recurAdd(Rebotor re, Substance sub){
		if(sub == null){
			return ;
		}
		
		if(sub.url != null && sub.Content != null){
			queue.add(sub);
		}
	
		for(int i = 0 ; i < sub.urls.size() ; i++){
			recurAdd(re,re.add(sub.urls.get(i)));
		}
		
	}
}
