package server;

import java.io.IOException;
import java.net.*;

public class UDPPort {

    private Server server;
    private int port;
    private DatagramSocket socket;

    public UDPPort(int port, Server server) throws SocketException {
        this.port = port;
        this.server = server;
        socket = new DatagramSocket(port);
        service();
    }


    private void service() {
        System.out.println("Opening socket on port: " + port);

            new Thread(() -> {
                while(true) {
                        System.out.println(port);
                    byte[] buff = new byte[508];
                    final DatagramPacket datagram = new DatagramPacket(buff, buff.length);

                    try {
                        socket.receive(datagram);
                        if(port = server.p)
                        System.out.println("starting socket on port: " + socket.getLocalPort());

                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    System.out.println("starting socket on port: " + socket.getLocalPort());
                    String text = new String(datagram.getData(), 0, datagram.getLength());
                    System.out.println("I've got " + text);
    //                String response = "echo: " + text;
    //                byte[] respBuff = String.valueOf(response).getBytes();
    //                int clientPort = datagram.getPort();
    //                InetAddress clientAddress = datagram.getAddress();
    //                DatagramPacket resp = new DatagramPacket(respBuff, respBuff.length, clientAddress, clientPort);
    //                try {
    //                    server.send(resp);
    //                    System.out.println("I've sent " + response);
    //                } catch (IOException e) {
    //                    // do nothing
    //                }
                }
            }).start();
    }

}