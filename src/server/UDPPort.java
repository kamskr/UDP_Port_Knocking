package server;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.TimeUnit;

public class UDPPort {

    private Server server;
    private int port;
    private DatagramSocket socket;
    private int id;
    private boolean sendMessage;

    public UDPPort(int port, Server server) throws SocketException {
        this.port = port;
        this.server = server;
        socket = new DatagramSocket(port);
        server.id++;
        id = server.id;
        service();
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public DatagramSocket getSocket() {
        return socket;
    }

    public void setSocket(DatagramSocket socket) {
        this.socket = socket;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isSendMessage() {
        return sendMessage;
    }

    public void setSendMessage(boolean sendMessage) {
        this.sendMessage = sendMessage;
    }

    private void service() {
        System.out.println("Opening socket on port: " + port);

            new Thread(() -> {
                while(true) {
                    byte[] buff = new byte[508];
                    final DatagramPacket datagram = new DatagramPacket(buff, buff.length);

                    try {
                        socket.receive(datagram);
                        String text = new String(datagram.getData(), 0, datagram.getLength());
                        System.out.println("I've got " + text + " on socket " + id);

                        if(!server.knockCheckers.containsKey(datagram.getAddress())){
                            System.out.println("Creating new checker");
                            server.knockCheckers.put(datagram.getAddress(), new KnockChecker(datagram.getAddress(), server));
                        }
                        if(server.knockCheckers.containsKey(datagram.getAddress())) {
                            System.out.println("Checking");
                            server.knockCheckers.get(datagram.getAddress()).check(this);
                        }
                        TimeUnit.SECONDS.sleep(1);
                    }catch (IOException e){
                        e.printStackTrace();
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }


                    if(sendMessage){
                        System.out.println("\n\n\n\n" +"INFO: socket " + id + " Ready to send message " + "\n\n\n\n");
                        String response = "connected on a port: " + port;
                        byte[] respBuff = String.valueOf(response).getBytes();
                        int clientPort = datagram.getPort();
                        InetAddress clientAddress = datagram.getAddress();
                        DatagramPacket resp = new DatagramPacket(respBuff, respBuff.length, clientAddress, clientPort);
                        try {
                            socket.send(resp);
                            System.out.println("I've sent information that we're " + response);
                        } catch (IOException e) {
                            // do nothing
                        }

                        sendMessage = false;
                    }
                }
            }).start();
    }

}