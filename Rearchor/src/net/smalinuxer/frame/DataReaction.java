package net.smalinuxer.frame;

import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.smalinuxer.frame.DataReaction.ReactData;

public class DataReaction extends AbsReaction<String,ReactData> implements Callable<ReactData>{

	/**
	 * 		Threalpool -> data -> index
	 */
	public final static class ReactData{
		
		public ReactData(String url){
			this.url = url;
		}
		
		public String url;
		
		public String content;
	}
	
	private ReactData data;	//the entity
	
	public DataReaction(String url) {
		super(url);
		data = new ReactData(url);
		data.url = url;
	}

	@Override
	public void fail2React(Reaction<String,ReactData> reaction) {
		//TODO
	}
	
	/**
	 * quick interrupt
	 */
	@Override
	public ReactData call() throws Exception {
		String content = handle();
		if(content == null){
			fail2React(this);
			return null;
		}
		data.content = content;
		return data;
	}

	@Override
	public String handle() {
		Lock lock = new ReentrantLock();
		Throwable throwable = null;
		StringBuffer sb = null;
		try {
			sb = react();
		} catch (InterruptedException e) {
			throwable = new Throwable(e.getMessage(),e.getCause());
//			System.out.println(e.getMessage());	//不知道为什么会有三次
		}
		
		lock.lock();
		if(throwable != null && sb == null){
			if(isRecall()){
				setRecall(false);
				lock.unlock();
				return handle();
			} 
		}
		lock.unlock();
		
		if(throwable != null && sb != null){
			//TODO
			return null;
		}
		
		return sb !=null ? sb.toString():null;
	}
}