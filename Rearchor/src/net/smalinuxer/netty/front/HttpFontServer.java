package net.smalinuxer.netty.front;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

public class HttpFontServer {
	private static final String DEFAULT_URI = "/src/";
	
	public void run(final int port,final String uri) {
		EventLoopGroup bossGroup = null;
		EventLoopGroup workerGroup = null;
		try {
			bossGroup = new NioEventLoopGroup();
			workerGroup = new NioEventLoopGroup();
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup,workerGroup)
				.channel(NioServerSocketChannel.class)
				.option(ChannelOption.SO_BACKLOG, 1024)
				.childHandler(new ChannelInitializer<SocketChannel>() {

					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast("http-decoder",new HttpRequestDecoder());
						ch.pipeline().addLast("http-aggregator",new HttpObjectAggregator(65536));
						ch.pipeline().addLast("http-encoder",new HttpResponseEncoder());
						ch.pipeline().addLast("http-chunked",new ChunkedWriteHandler());
						ch.pipeline().addLast("fileServerHandler",new HttpFrontServerHandler(uri));
					}
					
				});
			
			//绑定端口,同步等待成功
			ChannelFuture future = b.bind(port).sync();
			
			//等待服务器监听端口关闭
			future.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally{
			if(workerGroup != null){
				workerGroup.shutdownGracefully();
			}
			if(bossGroup != null){
				bossGroup.shutdownGracefully();
			}
		}
		
	}
	
	public static void main(String[] args) {
		int port = 10087;
		new HttpFontServer().run(port, DEFAULT_URI);
	}
	
}
