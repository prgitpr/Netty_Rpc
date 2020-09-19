package rpc.consumer;


import rpc.api.IRpcHelloService;
import rpc.api.IRpcService;
import rpc.consumer.proxy.RpcProxy;
import rpc.provider.RpcServiceImpl;

public class RpcConsumer {
    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException,
                                                  InstantiationException {
        IRpcHelloService rpcHello= RpcProxy.create(IRpcHelloService.class);
        System.out.println(rpcHello.hello("sb"));
        RpcServiceImpl rpcService1=(RpcServiceImpl) Class.forName("rpc.provider.RpcServiceImpl").newInstance();
        System.out.println( rpcService1.add(2,9));

        IRpcService rpcService=RpcProxy.create(IRpcService.class);
        System.out.println("8+2="+rpcService.add(8,2));
        System.out.println("8-2="+rpcService.sub(8,2));
        System.out.println("");

    }
}
