package client;

import server.UDP;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

public class Client {

    public static void main(String[] args) throws IOException {

        int[] ports = new int[]{1234, 4321, 1324,5432};
        InetAddress address = InetAddress.getLocalHost();
//        InetAddress address = args[0];
//        String givenPorts = args[1];

        String[] strPorts;
        boolean clientStarted = false;

        while(!clientStarted){
            try{
//                address = InetAddress.getByName(args[0]);
//                strPorts = args[1].split(",");
//                ports = new int[strPorts.length];
//
//                for (int i = 0; i < strPorts.length; i++){
//                    ports[i] = Integer.parseInt(strPorts[i]);
//                }
                clientStarted = true;
            }catch (Exception e){
                System.out.println("To start client write \"java Client serverIpAddress knockPort1,knockPort2,...,knockPortn\"");
            }
        }

        int port;
        byte[] buff = new byte[UDP.MAX_DATAGRAM_SIZE];
        DatagramPacket packet = new DatagramPacket(buff, buff.length);



        String text = "knock";

        byte[] queryBuff = String.valueOf(text).getBytes();
        DatagramSocket socket = new DatagramSocket();
        System.out.println("INFO: Trying to reach the server...");
        timeOut.start();
        String str;

        for(int i = 0; i < ports.length; i++){
            socket.send(new DatagramPacket(queryBuff, queryBuff.length, address, ports[i]));
            System.out.println("INFO: knock on: " + ports[i]);
            try {
//                According to: https://gamedev.stackexchange.com/questions/77549/is-there-a-maximum-delay-an-udp-packet-can-have
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        socket.receive(packet);
        timeOut.interrupt();

        str = new String(packet.getData(), 0, packet.getLength()).trim();

        socket.receive(packet);

        System.out.println(str + " \nINFO: Creating a file...");
        String[] strArr = str.split(" ");

        port = Integer.parseInt(strArr[5]);

        str = new String(packet.getData(), 0, packet.getLength()).trim();
        strArr = str.split(";");

        String nameOfFile = strArr[0];
        long lengthOfFile = Long.parseLong(strArr[1]);

        File file = new File("src//client//files//" + nameOfFile);

        try {
            if(!file.exists()) file.createNewFile();
        }catch (Exception e){
            e.printStackTrace();
        }

//        Receiving content of the files
        System.out.println("INFO: Receiving content of the files");

        byte[] data;
        byte[] store = new byte[(int)lengthOfFile];
        int position = 0;
        int counter = 0;
        while(position < lengthOfFile) {
            counter++;
            socket.receive(packet);
            data = packet.getData();
            System.arraycopy(data, 0, store, position, packet.getLength());
            position += packet.getLength();
            socket.send(new DatagramPacket(queryBuff, queryBuff.length, address, port));
        }

        if(counter == 0){
            System.out.println("ERR: Server did not send anything...");
            System.exit(0);
        }

        System.out.println("INFO: Writing data to the files");
        try (
                var fos = new FileOutputStream(file);
                var fileWriter = new DataOutputStream(fos)
        ) {

            fileWriter.write(store);
            fileWriter.flush();
        } catch (IOException e) {
            System.err.println("ERR: Failed to write data to the " + file);
        }

        System.out.println("INFO: Received " + counter + " packages");
        socket.close();
    }

    private static Thread timeOut = new Thread(() -> {
        try {
            for(int i = 1; i <= 10; i++){
                TimeUnit.MILLISECONDS.sleep(1000);
        }
            System.out.println("ERR: Could not reach the server");
            System.exit(0);
        } catch (InterruptedException e) {
            //do nothing
        }
    });

}
