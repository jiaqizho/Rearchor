package net.smalinuxer.netty.front;

/**
 * 有个小细节就是
 * 	192.168.1* 也可以这么写
 */
public class IpString {
	
	private String ip;

	private int equalsLength;
	
	private String selfString;
	
	public IpString(String ip) {
		this.ip = ip;
		equalsLength = calcLen(ip);
		selfString = ip.substring(0, equalsLength);
	}

	public String getIp() {
		return ip;
	}
	
	@Override
	public boolean equals(Object obj) {
		String e = (String)obj;
		return selfString.equals(e.substring(0,equalsLength));
	}
	
	protected int calcLen(String ip){
		for(int i = 0 ; i < ip.length() ; i++){
			char c = ip.charAt(i);
			if(c == '*'){
				return i;
			}
		}
		return ip.length();
	}
	
	
	/* test	
	public static void main(String[] args) {
		System.out.println(new IpString("192.168.1.*").equals("192.168.1.1"));;
	}*/
}
