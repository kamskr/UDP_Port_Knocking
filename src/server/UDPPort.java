package server;

import java.io.*;
import java.net.*;
import java.util.concurrent.TimeUnit;

public class UDPPort {

    private Server server;
    private int port;
    private DatagramSocket socket;
    private int clientPort;
    private InetAddress clientAddress;
    private int id;
    byte[] buff = new byte[508];
    private DatagramPacket datagram = new DatagramPacket(buff, buff.length);
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

    private void service() {

            new Thread(() -> {
                while(true) {

                    try {
                        socket.receive(datagram);
                        String text = new String(datagram.getData(), 0, datagram.getLength());
                        System.out.println("INFO: I've got " + text + " on socket " + id);

                        clientPort = datagram.getPort();
                        clientAddress = datagram.getAddress();


                        if(!server.knockCheckers.containsKey(datagram.getAddress())){
                            System.out.println("INFO: Creating new checker");
                            server.knockCheckers.put(datagram.getAddress(), new KnockChecker(datagram.getAddress(), server));
                        }

                        if(server.knockCheckers.containsKey(datagram.getAddress())) {
                            server.knockCheckers.get(datagram.getAddress()).check(this);
                        }



                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }).start();
    }

    public void sendFile(){
        new Thread(() -> {
            System.out.println("\n\n\n" +"INFO: socket " + id + " Ready to send message " + "\n\n\n");

            try {
                String response = "INFO: connected on a port: " + port;
                byte[] respBuff = String.valueOf(response).getBytes();

                DatagramPacket resp = new DatagramPacket(respBuff, respBuff.length, clientAddress, clientPort);

                socket.send(resp);

                System.out.println("INFO: Sending port number");

                File file = new File("src//server//testFile.txt");


                response = file.getName() + ";" + file.length();
                respBuff = String.valueOf(response).getBytes();
                resp = new DatagramPacket(respBuff, respBuff.length, clientAddress, clientPort);
                socket.send(resp);
                System.out.println("INFO: Sending name of file and size");

                DataInputStream inputFromFile = new DataInputStream(new FileInputStream(file));
                resp = new DatagramPacket(inputFromFile.readAllBytes(), (int)file.length(), clientAddress, clientPort);
                socket.send(resp);
                System.out.println("INFO: Sending content of file");
            } catch (IOException e) {
                System.out.println("File doesn't exist");
            }catch (Exception e){
                e.printStackTrace();
            }
        }).start();
    }


}