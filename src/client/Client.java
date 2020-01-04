package client;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

public class Client {

    public static void main(String[] args) throws IOException {
        int[] ports = new int[]{1234, 4321, 1324,5432};
//        String givenPorts = args[0];
//        InetAddress address = args[1];

        InetAddress address = InetAddress.getLocalHost();

        String text = "knock knock";

        byte[] queryBuff = String.valueOf(text).getBytes();
        DatagramSocket socket = new DatagramSocket();

        for(int i = 0; i < ports.length; i++){
            try {
                socket.send(new DatagramPacket(queryBuff, queryBuff.length, address, ports[i]));
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        System.out.println(text);

        byte[] buff = new byte[8192];
        DatagramPacket packet = new DatagramPacket(buff, buff.length);

        timeOut.start();
        socket.receive(packet);
        timeOut.interrupt();

        String str = new String(packet.getData(), 0, packet.getLength()).trim();

        System.out.println("INFO: I received: " + str);

//      receiving name and lengths of file as csv

        socket.receive(packet);

        System.out.println(str + " Creating a file...");

        str = new String(packet.getData(), 0, packet.getLength()).trim();
        String[] strArr = str.split(";");
        String nameOfFile = strArr[0];
        long lengthOfFile = Long.parseLong(strArr[1]);

        File file = new File("src//client//" + nameOfFile);

        try {
            if(!file.exists()) file.createNewFile();
        }catch (Exception e){
            e.printStackTrace();
        }

        socket.receive(packet);
        System.out.println("INFO: Received content of the file");

        str = new String(packet.getData(), 0, (int) lengthOfFile);
        System.out.println("INFO: Writing data to the file");

        try (
                var fileWriter = new FileWriter(file);
                var writer = new BufferedWriter(fileWriter);
        ) {

            writer.write(str);
        } catch (IOException e) {
            System.err.println("ERR: Failed to write data to the " + file);
        }


        socket.close();
    }

    private static Thread timeOut = new Thread(() -> {
        System.out.println("INFO: Trying to connect with the server...");

        try {
            for(int i = 1; i <= 10; i++){
                TimeUnit.MILLISECONDS.sleep(1000);
                System.out.println(i);
            }
            System.out.println("ERR: Could not reach the server");
            System.exit(0);
        } catch (InterruptedException e) {
            System.out.println("INFO: Connected to the server");
        }
    });

}
