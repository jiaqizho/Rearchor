package net.smalinuxer.rebot.UFilter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * 分析器,一个分析器可鞥具有
 * ·所有必需的过滤规则
 * ·需要被添加的过滤规则
 * ·过滤规则是否有顺序
 * @param <T>
 */
public abstract class UAnalyzer<T>{
	
	public UAnalyzer() {
		init(list);
	}
	
	public static enum Orderful{
		PRIORUTY,
		NOORDER,
		RANDOM
	}
	 
	protected Orderful mOrder = Orderful.NOORDER;

	protected List<T> list  = new CopyOnWriteArrayList<T>();
	
	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	public Orderful getmOrder() {
		return mOrder;
	}

	public void setmOrder(Orderful mOrder) {
		this.mOrder = mOrder;
	}
	
	/**
	 * 规则重排序
	 * @throws ReflectiveOperationException 
	 */
	public abstract void leaderOrder() throws ReflectiveOperationException;
	
	public abstract void init(List<T> list);
	
}