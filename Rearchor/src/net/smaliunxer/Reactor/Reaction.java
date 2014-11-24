package net.smaliunxer.Reactor;

/**
* 
* 添加进反应堆的Reaction
* 他可能需要如下的约束
* 	· �?��反应�?��的处理时�?
*  · �?��反应处理的方�?
*  · �?��的反应处理数�?线程池不处理,我只�?��把信号量放在abs抽象里面)
*  · 没有处理成功的回调方�?
* 
* @author user
*
*/
public interface Reaction {
	
	//TODO 有待测试
	public static final int DEFUALT_REACT_TIME = 1000; 
	
	public void react();
	
	public void fail2React(Reaction reaction);
	
}
