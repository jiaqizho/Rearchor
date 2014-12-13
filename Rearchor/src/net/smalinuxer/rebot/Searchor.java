package net.smalinuxer.rebot;

/**
 *  the core of the Sercher;
 *  
 *  1.Robot.txt:
 *  	User-agent: Rearchor
 *  	Disallow: /
 *  
 *  您必须将该文件保存到网站的顶级目录下（或网域的根目录下），
 *  robots.txt文件必须命名为robots.txt。
 *  
 *  2.no same rule
 *  	--deal with index
 *  
 *  3.deal with the bad site
 *  
 */
public interface Searchor {

	//是否使用QUICK_SET
	public static final boolean USE_QUICK_SET = true;
	
	//多久时间放弃原有存储
	public static final long UPDATE_STORE_DUP_MILLSECORD =  1000 * 60 * 60 * 24 * 2;
	
	//爬虫多久重新爬
	public static final long LONG_TIME_TO_REBUILD = 1000 * 60 * 60 * 24;//1000 * 60 * 60 * 24
	
	//Server端口
	public static final int LOCAL_PORT_SERVER = 10086; 
	
	public static final String LOCAL_IP = "127.0.0.1";
	
}
