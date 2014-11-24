package net.smalinuxer.UFilter;

import java.util.List;

public abstract class SingleSiteFilter implements UFilter<List<String>>{

	public abstract List<String> filter(String str);

	@Override
	public abstract List<String> filter(List<String> obj) ;

	@Override
	public abstract List<String> deduplication(List<String> obj) ;

	
}
