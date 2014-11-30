package net.smalinuxer.rebot.frame;

import java.util.concurrent.Callable;

/**
* 
* 添加进反应堆的Reaction
* 他可能需要如下的约束
* 	· 一个反应最长的处理时间
*  · 一个反应处理的方法
*  · 最大的反应处理数目(线程池不处理,我只需要把信号量放在abs抽象里面)
*  · 没有处理成功的回调方法
* 
* @author user
* @param <K> handle返回的泛型
*/
public interface Reaction<K,V> extends Callable<V>{
	
	//TODO 有待测试
	public static final int DEFUALT_REACT_TIME = 1000; 
	
	public StringBuffer react() throws InterruptedException;
	
	public K handle(); 
	
	public void fail2React(Reaction<K,V> reaction);
	
	public boolean isRecall() ;

	public void setRecall(boolean isRecall) ;
}
