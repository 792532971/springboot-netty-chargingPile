package com.example.springbootnettyclient.netty;

import com.example.springbootnettyserver.pojo.HeartBeatInfo;
import com.example.springbootnettyserver.pojo.MyProtocolBean;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Member;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * @author pjmike
 * @create 2018-10-25 17:15
 */
@Component
@Slf4j
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    private NettyClient nettyClient = new NettyClient();

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        /**
         * 这一部分是当客户端定时心跳的时候才用到，我这个demo用的是服务端心跳。所以这里就没有用处了。
         * 想用的话，配合IdleStateHandler方法哦
         */
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state().equals(IdleState.READER_IDLE)) {
                System.out.println("长期没收到服务器推送数据");
                //可以选择重新连接
            } else if (event.state().equals(IdleState.WRITER_IDLE)) {
                System.out.println("长期未向服务器发送数据");
                //发送心跳包
                HeartBeatInfo heartBeatInfo = new HeartBeatInfo();
                heartBeatInfo.setStatus(0);
                heartBeatInfo.setName("客户端采集器");
                ctx.writeAndFlush(heartBeatInfo);
            } else if (event.state().equals(IdleState.ALL_IDLE)) {
                System.out.println("ALL");
            }
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //if (msg instanceof ByteBuf) {
        //    ByteBuf byteBuf = (ByteBuf) msg;
        //    byte[] data = new byte[byteBuf.readableBytes()];
        //    System.out.println("data = " + data);
        //    // 使用获取到的二进制数据进行处理
        //    processBinaryData(data);
        //}
        //log.info("我是客户端-服务端消息进来了"+msg);
        if (msg instanceof MyProtocolBean){
            byte commandCode = 0x58;  // 命令代码为 0xA
            String message = "这个是客户端的消息";
            byte sequenceNumber = ((MyProtocolBean) msg).getSequenceNumber();

            System.out.println("0x" + Integer.toHexString(sequenceNumber & 0xFF));

            byte[] data = message.getBytes();  // 将消息转换为字节数组作为数据域
            System.out.println("123:::::"+new String(((MyProtocolBean) msg).getData(), StandardCharsets.UTF_8));

            MyProtocolBean bean = new MyProtocolBean((byte) 0x58, (byte) 1, commandCode, data);
            ctx.writeAndFlush(bean);
        }

        super.channelRead(ctx, msg);
    }

    private void processBinaryData(byte[] data) {
        // 这里是对二进制数据的处理逻辑，仅作示例
        System.out.println("Received binary data: " + bytesToHexString(data));
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            String hex = String.format("%02X", b);
            sb.append(hex);
        }
        return sb.toString();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        byte commandCode = 0xA;  // 命令代码为 0xA
        String message = "这个是客户端的消息";
        byte[] data = message.getBytes();  // 将消息转换为字节数组作为数据域

        MyProtocolBean bean = new MyProtocolBean((byte) 0x80, (byte) 1, commandCode, data);
        ctx.writeAndFlush(bean);
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //如果运行过程中服务端挂了,执行重连机制
        try{
            System.out.println("服务器挂了......");
            EventLoop eventLoop = ctx.channel().eventLoop();
            eventLoop.schedule(() -> nettyClient.start(), 2, TimeUnit.SECONDS);
            super.channelInactive(ctx);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("捕获的异常：{}", cause.getMessage());
//        ctx.channel().close();
    }
}
