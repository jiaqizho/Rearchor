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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.regex.Pattern;

import javax.activation.MimetypesFileTypeMap;

import net.smalinuxer.lucene.frame.IndexWriterQueue;
import net.smalinuxer.lucene.frame.IndexWriterQueue.IndexData;
import net.smalinuxer.lucene.frame.RetinReaderFinder;
import net.smalinuxer.lucene.utils.LuceneConfig;

public class HttpFrontServerHandler extends
		SimpleChannelInboundHandler<FullHttpRequest> {

	private RetinReaderFinder reader;
	
	public HttpFrontServerHandler(RetinReaderFinder reader){
		this.reader = reader;
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
		
		String uri = request.getUri().toLowerCase();
		uri = URLDecoder.decode(uri,"UTF-8");
		if (uri.equals("/searcher") || uri.equals("/searcher/")) {
			sendContent(ctx);
		} else {
			if(uri.substring(1, 8).equals("result?") && uri.substring(8, 10).equals("q=")){
				String search = uri.substring(10, uri.length());
				List<IndexWriterQueue.IndexData> list = handler(search);
				sendResutlt(ctx,list);
			} else{
				sendError(ctx, FORBIDDEN);
			}
		}
	}
	
	private void sendResutlt(ChannelHandlerContext ctx,List<IndexData> list) {
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);
		response.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");
		StringBuilder buf = new StringBuilder();
		buf.append("<!DOCTYPE html>\r\n");
		buf.append("<html><head><title>");
		buf.append("某个搜索");
		buf.append("</title></head><body>\r\n");
		if(list.size() == 0){
			buf.append("<div>\r\n");
			buf.append("抱歉,您搜索的内容不存在.请尽量减少关键词\r\n");
			buf.append("</div>\r\n");
		}
		for(IndexData data :list){
			buf.append("<div>\r\n");
			buf.append(data + "\r\n");
			buf.append("</div>\r\n");
		}
		buf.append("</body></html>\r\n");
		ByteBuf buffer = Unpooled.copiedBuffer(buf, CharsetUtil.UTF_8);
		response.content().writeBytes(buffer);
		buffer.release();
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}

	private List<IndexData> handler(String string) {
//		return reader.search0(new InputToken().iteratorToken(string), LuceneConfig.LUCENE_SHOW_NUM);
		return reader.search0(string, LuceneConfig.LUCENE_SHOW_NUM);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
//		cause.printStackTrace();
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
		buf.append("   <p>searcher : <input type=\"text\" name=\"q\" /></p>\r\n");
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
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		System.out.println(URLDecoder.decode("%E4%B8%AD%E6%96%87".toLowerCase(),"UTF-8"));
	}
}
