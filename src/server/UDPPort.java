package server;

import java.io.*;
import java.net.*;
import java.util.concurrent.TimeUnit;

public class UDPPort {

    private File file = new File("src//server//files//Mamamia_demo.mp3");
//    private File files = new File("src//server//testFile.txt");
    private Server server;
    private int port;
    private DatagramSocket socket;
    private int clientPort;
    private InetAddress clientAddress;
    byte[] buff = new byte[UDP.MAX_DATAGRAM_SIZE];
    private DatagramPacket datagram = new DatagramPacket(buff, buff.length);

    public UDPPort(int port, Server server) throws SocketException {
        this.port = port;
        this.server = server;
        socket = new DatagramSocket(port);
    }

    public UDPPort(Server server, InetAddress clientAddress, int clientPort) throws SocketException{
        this.server = server;
        socket = new DatagramSocket();
        port = socket.getLocalPort();
        this.clientAddress = clientAddress;
        this.clientPort = clientPort;
    }

    public int getClientPort() {
        return clientPort;
    }

    public InetAddress getClientAddress() {
        return clientAddress;
    }

    public int getPort() {
        return port;
    }

    public void service() {

            new Thread(() -> {
                while(true) {

                    try {
                        socket.receive(datagram);
                        String text = new String(datagram.getData(), 0, datagram.getLength());
                        System.out.println("INFO: I've got " + text + " on port " + port);

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

    //    public void sendFile(){
//        new Thread(() -> {
//            System.out.println("\n\n\n" +"INFO: socket " + id + " Ready to send message " + "\n\n\n");
//
//            try {
//                String response = "INFO: connected on a port: " + port;
//                byte[] respBuff = String.valueOf(response).getBytes();
//
//                DatagramPacket resp = new DatagramPacket(respBuff, respBuff.length, clientAddress, clientPort);
//
//                socket.send(resp);
//
//                System.out.println("INFO: Sending port number");
//
//                File files = new File("src//server//testFile.txt");
//
//
//                response = files.getName() + ";" + files.length();
//                respBuff = String.valueOf(response).getBytes();
//                resp = new DatagramPacket(respBuff, respBuff.length, clientAddress, clientPort);
//                socket.send(resp);
//                System.out.println("INFO: Sending name of files and size");
//
//                DataInputStream inputFromFile = new DataInputStream(new FileInputStream(files));
//                resp = new DatagramPacket(inputFromFile.readAllBytes(), (int)files.length(), clientAddress, clientPort);
//                socket.send(resp);
//                System.out.println("INFO: Sending content of files");
//            } catch (IOException e) {
//                System.out.println("File doesn't exist");
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }).start();
//    }

    public void sendFile(){
        new Thread(() -> {
            System.out.println("\n\n\n" +"INFO: socket on port " + port + " Ready to send message " + "\n\n\n");

            try {
                String response = "INFO: connected on a port: " + port;
                byte[] respBuff = String.valueOf(response).getBytes();

                DatagramPacket resp = new DatagramPacket(respBuff, respBuff.length, clientAddress, clientPort);

                socket.send(resp);

                System.out.println("INFO: Sending port number");



                System.out.println("INFO: Sending name of files and size");
                response = file.getName() + ";" + file.length();
                respBuff = String.valueOf(response).getBytes();
                resp = new DatagramPacket(respBuff, respBuff.length, clientAddress, clientPort);
                socket.send(resp);

//                Sending content of the files in chunks

                try(
                        FileInputStream fis = new FileInputStream(file);
                        DataInputStream inputFromFile = new DataInputStream(fis)
                ) {

                    System.out.println("INFO: Sending content of files");
                    int fileSize = (int) file.length();
                    int chunkSize = UDP.MAX_DATAGRAM_SIZE;
                    int read, readLength = chunkSize;
                    byte[] byteChunk;

                    while (fileSize > 0) {
                        if (fileSize <= chunkSize) {
                            readLength = fileSize;
                        }

                        byteChunk = new byte[readLength];
                        read = inputFromFile.read(byteChunk);

                        fileSize -= read;
                        assert (read == byteChunk.length);
                        resp = new DatagramPacket(byteChunk, readLength, clientAddress, clientPort);
                        socket.send(resp);
                        socket.receive(datagram);
                    }
                    System.out.println("Socket on port " + port + " closed ");
                    socket.close();
                    server.openedPorts.remove(port);
                }catch (Exception e){
                    socket.close();
                    server.openedPorts.remove(port);
                    System.out.println("Socket on port " + port + " closed ");
                    e.printStackTrace();
                    return;
                }
            } catch (IOException e) {
                socket.close();
                server.openedPorts.remove(port);
                System.out.println("Socket on port " + port + " closed ");
                System.out.println("File doesn't exist");
            }catch (Exception e){
                socket.close();
                server.openedPorts.remove(port);
                System.out.println("Socket on port " + port + " closed ");
                e.printStackTrace();
            }
        }).start();
    }


}