package net.smalinuxer.lucene.frame;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 面相Rebotor的接口,并且是个转接器,进行串行化
 */
public class IndexWriterQueue implements Runnable{
	
	/**
	 * 索引建立的DATA
	 * @author user
	 */
	public static class IndexData{
		public String url;
		
		public String content;
		
		public String title;
		
		@Override
		public String toString() {
			return "url:" + url + "  " + "content:" + content  + "  "  +  "title:" + title;
		}
		
	}
	
	private final BlockingQueue<IndexData> queue;
	
	private static final String DEFLUAT_TITLE_NAME = "无标题"; 
	
	public IndexWriterQueue() {
		queue = new LinkedBlockingQueue<IndexData>();
		new Thread(this).start();
	}
	
	public IndexWriterQueue(BlockingQueue<IndexData> queue) {
		this.queue = queue;
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		RetinIndexBuilder builder = new RetinIndexBuilder();
		try {
			while(true){
				IndexData data = queue.take();
				data.title = filterTitle(data.content);
				builder.addDoc(data);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally{
			
		}
	}
	
	private static String filterTitle(String content){
		Pattern pattern = Pattern.compile("<title>(.*)</title>");
		Matcher matcher = pattern.matcher(content);
		while(matcher.find()){
			return matcher.group(1);
		}
		return DEFLUAT_TITLE_NAME;
	}
	
	
/*	
	public static void main(String[] args) throws MalformedURLException, IOException {

 * 		test title 
 * 
 * 		HttpURLConnection conn = (HttpURLConnection) new URL("http://www.cqupt.edu.cn/").openConnection();
		BufferedReader r = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		StringBuffer str = new StringBuffer();
		String line = "";
		while( (line = r.readLine()) != null){
			str.append(line);
			str.append("\r\n");
		}
		System.out.println(filterTitle(str.toString()));
	}*/
}
