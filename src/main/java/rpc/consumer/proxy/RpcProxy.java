package rpc.consumer.proxy;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import rpc.protocol.InvokerProtocol;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class RpcProxy {
     public static<T> T create(Class<?> clazz){
         MethodProxy proxy=new MethodProxy(clazz);
         Class<?>[] interfaces=clazz.isInterface()?new Class[]{clazz}:clazz.getInterfaces();
         T result=(T) Proxy.newProxyInstance(clazz.getClassLoader(),interfaces,proxy);
         return result;
     }
    public static class MethodProxy implements InvocationHandler {

        private Class<?> clazz;

        public MethodProxy(Class<?> clazz) {
            this.clazz = clazz;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (Object.class.equals(method.getDeclaredAnnotations())) {
                try {
                    return method.invoke(this, args);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            } else {
                return rpcInvoke(proxy, method, args);
            }
            return null;
        }

        public Object rpcInvoke(Object proxy, Method method, Object[] args) {
            System.out.println();
            InvokerProtocol msg = new InvokerProtocol();
            msg.setClassName(this.clazz.getName());
            msg.setMethodName(method.getName());
            msg.setValues(args);
            msg.setParams(method.getParameterTypes());
            System.out.println("type"+method.getParameterTypes().length);
            final RpcProxyHandler rpcProxyHandler = new RpcProxyHandler();
            EventLoopGroup group = new NioEventLoopGroup();
            try {
                Bootstrap b = new Bootstrap();
                b.group(group)
                 .channel(NioSocketChannel.class)
                 .option(ChannelOption.TCP_NODELAY, true)
                 .handler(new ChannelInitializer<SocketChannel>() {
                     @Override
                     protected void initChannel(SocketChannel ch) throws Exception {
                         ChannelPipeline channelPipeline = ch.pipeline();
                         channelPipeline.addLast("frameDecoder",
                                                 new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                         channelPipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
                         channelPipeline.addLast("encoder", new ObjectEncoder());
                         channelPipeline.addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE,
                                                                              ClassResolvers.cacheDisabled(null)));
                         channelPipeline.addLast("handler", rpcProxyHandler);
                         /*System.out.println("d");
                         ChannelFuture channelFuture = b.connect("localhost", 8080).sync();
                         System.out.println("1");
                         channelFuture.channel().writeAndFlush(msg).sync();
                         System.out.println("2");
                         channelFuture.channel().closeFuture().sync();
                         System.out.println("3");*/
                     }
                 });
                ChannelFuture channelFuture=b.connect("localhost",8080).sync();
                channelFuture.channel().writeAndFlush(msg).sync();
                channelFuture.channel().closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return rpcProxyHandler.getResponse();
        }
    }


}
