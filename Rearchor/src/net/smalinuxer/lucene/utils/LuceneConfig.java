package net.smalinuxer.lucene.utils;

import java.io.File;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class LuceneConfig {
	
	//lucene内容存储
	public static final String LUCENE_STORE_DIR = File.separator + "store";
	
	//lucene间隔时间不能执行openIfChanged方法
	public static final long LUCENE_REFRESH_READER = 60 * 60 * 1000;
	
	//lucene开始定时任务的时间,如果时间小于现实时间,会计算少执行的次数并执行少执行的次数,否则定时任务不进行执行
	@Deprecated
	public static final String LUCENE_REFRESH_START_TIME = "2014/12/8 17:40:00";
	
	public static final Date LUCENE_REFRESH_START_DATE_NOW = new Date();
	
	public static final int LUCENE_SHOW_NUM = 20;
	
}
