package rpc.registry;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class Socketserver {
    public static void main(String[] args) throws IOException {
        int port = 5050;
        ServerSocket serverSocket=new ServerSocket(port);
        Socket socket=serverSocket.accept();
        InputStream inputStream=socket.getInputStream();
        byte[] bytes=new byte[1024];
        int len;
        StringBuilder sb=new StringBuilder();
        while((len=inputStream.read(bytes))!=-1){
            sb.append(new String(bytes,0,len,"UTF-8"));

        }
        System.out.println("in");
        OutputStream outputStream=socket.getOutputStream();
        RegistryHandler registryHandler=new RegistryHandler();
        ConcurrentHashMap<String, Object> msp= registryHandler.getRegistryMap();
       /* InvokerProtocol request = (InvokerProtocol) ;
        if (msp.containsKey(request.getClassName())) {
            Object clazz = msp.get(request.getClassName());
            Method method = clazz.getClass().getMethod(request.getMethodName(), request.getParams());
            result = method.invoke(clazz, request.getValues());
        }*/

       // outputStream.write();
    }
}
