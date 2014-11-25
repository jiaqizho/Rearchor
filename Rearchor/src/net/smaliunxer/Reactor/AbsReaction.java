package net.smaliunxer.Reactor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;

/**
 * 
 * @param <V> usually is the String
 */
public abstract class AbsReaction<V> implements Reaction,Callable<V>{
	
	private String mUrl = null;
	
	public AbsReaction(String url){
		this.mUrl = url;
	}
	
	@Override
	public void react() {
		StringBuffer sb = new StringBuffer();
		BufferedReader reader = null;
		HttpURLConnection conn = null;
		try {
			URL url = new URL(mUrl);
			conn = (HttpURLConnection)url.openConnection();
			reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = null;
			while((line = reader.readLine()) != null){
				sb.append(line);
			}
		} catch (MalformedURLException e) {
			return;
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				if(reader != null){
					reader.close();
				}
			} catch (IOException e) {
			}
		}
		
		if(sb.toString() != null){
			//TODO
		}
		
	}
}