package net.smalinuxer.rebot.UFilter;

import java.util.List;

/**
 * the example of the expand Filter substance  
 * when it's all done it will finished 
 */
public abstract class SingleSiteFilter implements UFilter<List<String>>{

	public abstract List<String> filter(String str);

	@Override
	public abstract List<String> filter(List<String> obj) ;

	@Override
	public abstract List<String> deduplication(List<String> obj) ;

	
}
