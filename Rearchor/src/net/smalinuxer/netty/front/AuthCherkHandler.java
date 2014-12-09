package net.smalinuxer.netty.front;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AuthCherkHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
	
	private Map<String, Boolean> nodeCheck = new ConcurrentHashMap<String, Boolean>();
	
	//白名单
//	private IpString[] whitekList = {new IpString("127.0.0.1"), new IpString("192.168.1.104")};
	
	//允许所有ip访问
	private IpString[] whitekList = {new IpString("*")};
	
	/*
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
		nodeCheck.remove(ctx.channel().remoteAddress().toString()); // 删除缓存
		ctx.close();
		ctx.fireExceptionCaught(cause);
	}
	*/

	@Override
	protected void messageReceived(ChannelHandlerContext ctx,
			FullHttpRequest msg) throws Exception {
		InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
		String ip = address.getAddress().getHostAddress();
		boolean isOK = false;
		for (IpString WIP : whitekList) {
			if (WIP.equals(ip)) {
				isOK = true;
				break;
			}
		}
		if(isOK){
			ctx.fireChannelRead(msg);
		} else {
			sendError(ctx, FORBIDDEN);
			//TODO
		}
	}
	
	private static void sendError(ChannelHandlerContext ctx,
			HttpResponseStatus status) {
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1,
				status, Unpooled.copiedBuffer("Failure: " + status.toString()
						+ "\r\n", CharsetUtil.UTF_8));
		response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}
	
}
