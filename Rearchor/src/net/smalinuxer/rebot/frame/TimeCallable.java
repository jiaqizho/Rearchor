package net.smalinuxer.rebot.frame;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.smalinuxer.rebot.frame.DataReaction.ReactData;

public class TimeCallable<T> {
	
	private Reaction<?,T> callAble;
	
	private ReactorExecutors<T> pool;
	
	private Timer mTimer;

	private static final int DEFULAT_TIME_TIMEOUT = 5000; 
	
	public TimeCallable(Reaction<?,T> callAble,ReactorExecutors<T> threadPool){
		this.pool = threadPool;
		this.callAble = callAble;
		this.mTimer = new Timer();
	}
	
	public Future<T> start() throws ReactorShutDownException{
		final Future<T> future = this.pool.reactInPoolforResult(callAble);
		mTimer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				/**
				 * the problem is if waiting then using the cancel.
				 */
				if(!future.isCancelled()){
					future.cancel(true);
				}
				
				if(callAble.isRecall()){
					future.cancel(true);
				}
			}
			
		}, DEFULAT_TIME_TIMEOUT);
		return future;
	}
	
	/**
	 * no test
	 * @return
	 * @throws ReactorShutDownException
	 */
	public T start2() throws ReactorShutDownException{
		final Future<T> future = this.pool.reactInPoolforResult(callAble);
		try {
			return future.get(DEFULAT_TIME_TIMEOUT,TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		} finally {
			if(future.isCancelled()){
				future.cancel(true);
			}
		}
		return null;
	}
	
	/**
	 * test 
	 * @param args
	 */
	public static void main(String[] args) {
		TimeCallable<ReactData> callable = new TimeCallable<ReactData>(new DataReaction("http://www.baidu.com"),new ReactorExecutors<ReactData>());
		try {
			Future<ReactData> future = callable.start();
			ReactData data = future.get();
			System.out.println(data != null ? data.content : "null");
		} catch (ReactorShutDownException | InterruptedException | ExecutionException e1) {
			//inposiable
			e1.printStackTrace();
		} 
		
		try {
			callable.start();
		} catch (ReactorShutDownException e) {
			e.printStackTrace();
		}
	}
}
