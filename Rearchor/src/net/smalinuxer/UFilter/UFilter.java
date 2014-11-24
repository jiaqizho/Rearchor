package net.smalinuxer.UFilter;


public interface UFilter<T> {
	/**
	 * 过滤规则
	 * @param obj
	 * @return
	 * @throws RegularRepeatException 
	 */
	public abstract T filter(T obj) throws RegularRepeatException;
	
	/**
	 * 去重
	 * @param obj
	 * @return 
	 */
	public abstract T deduplication(T obj);

	/**
	 * 
	 * @return
	 */
	public abstract UAnalyzer<?> invokeAnalyzer();
}
