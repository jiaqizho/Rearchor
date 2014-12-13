package net.smalinuxer.lucene.frame;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.smalinuxer.rebot.UFilter.StandardFilter.Substance;

/**
 * 面相Rebotor的接口,并且是个转接器,进行串行化
 */
public class IndexWriterQueue implements Runnable {

	/**
	 * 索引建立的DATA
	 * 
	 * @author user
	 */
	public static class IndexData {
		public String url;

		public String content;

		public String title;

		@Override
		public String toString() {
			return "url:" + url + "  " + "content:" + content + "  " + "title:"
					+ title;
		}

	}

	private final BlockingQueue<IndexData> queue;

	private static final String DEFLUAT_TITLE_NAME = "无标题";
	
	private Thread currentThread;

	public void add(Substance sub) {
		IndexData data = new IndexData();
		data.url = sub.url;
		data.content = sub.Content;
		queue.add(data);
	}

	public IndexWriterQueue() {
		queue = new LinkedBlockingQueue<IndexData>();
		currentThread = new Thread(this);
		currentThread.start();
	}

	public IndexWriterQueue(BlockingQueue<IndexData> queue) {
		this.queue = queue;
		currentThread = new Thread(this);
		currentThread.start();
	}

	@Override
	public void run() {
		RetinIndexBuilder builder = new RetinIndexBuilder();
		try {
			while (true) {
				IndexData data = queue.take();
				data.title = filterTitle(data.content);
				builder.addDoc(data);
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} 
	}

	private static String filterTitle(String content) {
		Pattern pattern = Pattern.compile("<title>(.*)</title>");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			return matcher.group(1);
		}
		return DEFLUAT_TITLE_NAME;
	}

	public void shutdown() {
		currentThread.interrupt();
	}
	
	/*
	 * public static void main(String[] args) throws MalformedURLException,
	 * IOException {
	 * 
	 * test title
	 * 
	 * HttpURLConnection conn = (HttpURLConnection) new
	 * URL("http://www.cqupt.edu.cn/").openConnection(); BufferedReader r = new
	 * BufferedReader(new InputStreamReader(conn.getInputStream()));
	 * StringBuffer str = new StringBuffer(); String line = ""; while( (line =
	 * r.readLine()) != null){ str.append(line); str.append("\r\n"); }
	 * System.out.println(filterTitle(str.toString())); }
	 */
}
