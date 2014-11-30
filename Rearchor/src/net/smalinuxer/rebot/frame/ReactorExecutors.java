package net.smalinuxer.rebot.frame;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * insteal
 */
public class ReactorExecutors<T>{
	
	private ExecutorService handleReactor = Executors.newCachedThreadPool();
	
	protected boolean status = false;
	
	private static final int DEFUALT_WAIT_TIMEOUT = 1000;
	
	private Lock lock;
	
	protected Semaphore mSemaphore;
	
	private static final int MAX_REACT_NUM = 1000;
	
	public ReactorExecutors(){
		this.mSemaphore = new Semaphore(MAX_REACT_NUM);
		lock = new ReentrantLock();
		turnOn();
	}
	
	public synchronized void turnOn(){
		status = true;
	}
	
	public void shutdown(){
		lock.lock();	//由于这里sychronized不太好用
		if(status != false){
			status = false;
			lock.unlock();
			try {
				if(handleReactor.awaitTermination(DEFUALT_WAIT_TIMEOUT, TimeUnit.MILLISECONDS)){
					handleReactor.shutdownNow();
				}
			} catch (InterruptedException e) {
				handleReactor.shutdownNow();
				//TODO 日志记录
			}
			//TODO 关闭其他
		}
	}
	
	public synchronized boolean isRunning(){
		return status;
	}
	
	/*
	 * 
	 * 通过聚合类控制,信号量应该在runnable中进行控制?
	private Semaphore mSemaphore = null;
	
	public ReactorExecutors(){
		
	}
	*/
	public void reactInPool(Runnable commend) throws ReactorShutDownException{
		try {
			cherkStatus();
		} catch (ReactorShutDownException e) {
			throw e;
		}
		try {
			mSemaphore.acquire();
		} catch (InterruptedException e) {
			//党被打断的时候就是退出的时候
		}
		handleReactor.execute(commend);
		mSemaphore.release();
	}
	
	/**
	 * @param commend
	 * @return This Future object can be used to check if the Runnable as finished executing.
	 * @throws ReactorShutDownException 
	 */
	public Future<?> reactInPoolforResult(Runnable commend) throws ReactorShutDownException{
		try {
			cherkStatus();
		} catch (ReactorShutDownException e) {
			throw e;
		}
		try {
			mSemaphore.acquire();
		} catch (InterruptedException e) {
			//TODO 党被打断的时候就是退出的时候
		}
		Future<?> future = handleReactor.submit(commend);
		mSemaphore.release();
		return future;
	}
	
	public Future<T> reactInPoolforResult(Callable<T> commend) throws ReactorShutDownException{
		try {
			cherkStatus();
		} catch (ReactorShutDownException e) {
			throw e;
		}
		try {
			mSemaphore.acquire();
		} catch (InterruptedException e) {
			//TODO 当被打断的时候就是退出的时候
		}
		Future<T> future = handleReactor.submit(commend);
		mSemaphore.release();
		return future;
	}
	
	@Deprecated
	public List<Future<T>> reactInPoolforResult(Collection<? extends Callable<T>> commends) throws ReactorShutDownException{
		try {
			cherkStatus();
		} catch (ReactorShutDownException e) {
			throw e;
		}
		
		try {
			return handleReactor.invokeAll(commends);
		} catch (InterruptedException e) {
			//TODO LOG
			shutdown();
			return null;
		}
	}
	
	protected void cherkStatus() throws ReactorShutDownException{
		if(!isRunning()){
			throw new ReactorShutDownException();
		}
	}
}
