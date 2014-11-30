package net.smalinuxer.netty.front;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaders.Names.LOCATION;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static io.netty.handler.codec.http.HttpResponseStatus.FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpResponseStatus.METHOD_NOT_ALLOWED;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

import javax.activation.MimetypesFileTypeMap;

import net.smalinuxer.lucene.frame.IndexWriterQueue;
import net.smalinuxer.lucene.frame.IndexWriterQueue.IndexData;

public class HttpFrontServerHandler extends
		SimpleChannelInboundHandler<FullHttpRequest> {
	
	private final String url;

	public HttpFrontServerHandler(String url) {
		this.url = url;
	}

	/**
	 * chrome 测试 会调用两次
	 * ie测试只会调用一次
	 * ----------------
	 * 原因：
	 * 	chrome会默认请求/favicon.ico
	 * 	以获得网页的图标,而ie会根据设置而为
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
	}
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx,
			FullHttpRequest request) throws Exception {
		if (!request.getDecoderResult().isSuccess()) {
			sendError(ctx, BAD_REQUEST);
			return;
		}
		if (request.getMethod() != GET) {
			sendError(ctx, METHOD_NOT_ALLOWED);
			return;
		}
		
		
		final String uri = request.getUri();
		System.out.println(uri);
		
		if (uri.equals("/searcher") || uri.equals("/searcher/")) {
			sendContent(ctx);
		} else {
			if(uri.substring(1, 7).equals("result")){
				//TODO  把参数拿出来,干脆写死
				List<IndexWriterQueue.IndexData> list = handler("xxxxx");
				sendResutlt(list);
			} else{
				sendError(ctx, FORBIDDEN);
			}
			
		}
	}
	
	private void sendResutlt(List<IndexData> list) {
		
	}

	private List<IndexData> handler(String string) {
		return null;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
		if (ctx.channel().isActive()) {
			sendError(ctx, INTERNAL_SERVER_ERROR);
		}
	}

	private static final Pattern ALLOWED_FILE_NAME = Pattern.compile("[A-Za-z0-9][-_A-Za-z0-9\\.]*");
	
	private static void sendContent(ChannelHandlerContext ctx) {
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);
		response.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");
		StringBuilder buf = new StringBuilder();
		buf.append("<!DOCTYPE html>\r\n");
		buf.append("<html><head><title>");
		buf.append("某个搜索");
		buf.append("</title></head><body>\r\n");
		buf.append("<form action=\"/result\" method=\"get\">\r\n");
		buf.append("   <p>searcher : <input type=\"text\" name=\"fname\" /></p>\r\n");
		buf.append("   <input type=\"submit\" value=\"Submit\" />\r\n");
		buf.append("</form></body></html>\r\n");
		ByteBuf buffer = Unpooled.copiedBuffer(buf, CharsetUtil.UTF_8);
		response.content().writeBytes(buffer);
		buffer.release();
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}

	private static void sendRedirect(ChannelHandlerContext ctx, String newUri) {
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, FOUND);
		response.headers().set(LOCATION, newUri);
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}

	private static void sendError(ChannelHandlerContext ctx,
			HttpResponseStatus status) {
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1,
				status, Unpooled.copiedBuffer("Failure: " + status.toString()
						+ "\r\n", CharsetUtil.UTF_8));
		response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}

	private static void setContentTypeHeader(HttpResponse response, File file) {
		MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
		response.headers().set(CONTENT_TYPE,
				mimeTypesMap.getContentType(file.getPath()));
	}
}
