package net.smalinuxer.frame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;

/**
 * @param Callable<V> the <V> is the executor Future.get();
 * @param Reactor<K> the <K> is the handle() back data;
 */
public abstract class AbsReaction<K,V> implements Reaction<K,V>,Callable<V>{
	
	private volatile boolean isRecall = true;	//是否重新传输	
	
	private String mUrl = null;
	
	public AbsReaction(String url){
		this.mUrl = url;
	}
	
	public boolean isRecall() {
		return isRecall;
	}

	public void setRecall(boolean isRecall) {
		this.isRecall = isRecall;
	}
	
	@Override
	public final StringBuffer react() throws InterruptedException{
		StringBuffer sb = new StringBuffer();
		BufferedReader reader = null;
		HttpURLConnection conn = null;
		try {
			URL url = new URL(mUrl);
			conn = (HttpURLConnection)url.openConnection();
			conn.setConnectTimeout(2000);
			reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			Thread.sleep(0);
			String line = null;
			while((line = reader.readLine()) != null){
				sb.append(line);
			}
		} catch (MalformedURLException e) {
			throw new InterruptedException("url wrong");
		} catch (IOException e) {
			throw new InterruptedException("can't reach");
		} finally{
			try {
				if(reader != null){
					reader.close();
				}
			} catch (IOException e) {
			}
			conn.disconnect();
		}
		return sb;
	}
	
}