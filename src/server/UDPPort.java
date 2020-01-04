package server;

import java.io.*;
import java.net.*;
import java.sql.SQLOutput;
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



    public int getPort() {
        return port;
    }

    public void setSendMessage(boolean sendMessage) {
        this.sendMessage = sendMessage;
    }

    private void service() {
        System.out.println("INFO: Opening socket on port: " + port);

            new Thread(() -> {
                while(true) {
                    byte[] buff = new byte[508];
                    final DatagramPacket datagram = new DatagramPacket(buff, buff.length);

                    try {
                        socket.receive(datagram);
                        String text = new String(datagram.getData(), 0, datagram.getLength());
                        System.out.println("INFO: I've got " + text + " on socket " + id);

                        if(!server.knockCheckers.containsKey(datagram.getAddress())){
                            System.out.println("INFO: Creating new checker");
                            server.knockCheckers.put(datagram.getAddress(), new KnockChecker(datagram.getAddress(), server));
                        }
                        if(server.knockCheckers.containsKey(datagram.getAddress())) {
                            System.out.println("INFO: Checking");
                            server.knockCheckers.get(datagram.getAddress()).check(this);
                        }
                        TimeUnit.SECONDS.sleep(1);
                    }catch (IOException e){
                        e.printStackTrace();
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }


                    if(sendMessage){

                        new Thread(() -> {
                            System.out.println("\n\n\n" +"INFO: socket " + id + " Ready to send message " + "\n\n\n");
                            String response = "INFO: connected on a port: " + port;
                            byte[] respBuff = String.valueOf(response).getBytes();
                            int clientPort = datagram.getPort();
                            InetAddress clientAddress = datagram.getAddress();
                            DatagramPacket resp = new DatagramPacket(respBuff, respBuff.length, clientAddress, clientPort);
                            try {
                                socket.send(resp);

                                System.out.println("INFO: Sent port number");
//                            here got to make sending a name and length of file, then the content
                                File file = new File("src//server//testFile.txt");
//                            try {
//                                file.createNewFile();
//                            }catch (Exception e){
//                                e.printStackTrace();
//                            }

                                response = file.getName() + ";" + file.length();
                                respBuff = String.valueOf(response).getBytes();
                                resp = new DatagramPacket(respBuff, respBuff.length, clientAddress, clientPort);
                                socket.send(resp);

                                System.out.println("INFO: Sent name of file and size");
                                DataInputStream inputFromCache = new DataInputStream(new FileInputStream(file));
                                resp = new DatagramPacket(inputFromCache.readAllBytes(), (int)file.length(), clientAddress, clientPort);
                                socket.send(resp);

                                System.out.println("INFO: Sent content of file");
                            } catch (IOException e) {
                                // do nothing
                            }
                        }).start();

                        sendMessage = false;
                    }
                }
            }).start();
    }



}