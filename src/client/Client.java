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

        byte[] buff = new byte[2000];
        DatagramPacket packet = new DatagramPacket(buff, buff.length);

        InetAddress address = InetAddress.getLocalHost();

        String text = "knock knock";

        byte[] queryBuff = String.valueOf(text).getBytes();
        DatagramSocket socket = new DatagramSocket();

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

        System.out.println("INFO: Writing data to the file");

        try (
                var fileWriter = new FileOutputStream(file);
                var writer = new BufferedOutputStream(fileWriter);
        ) {

            writer.write(packet.getData(), 0, (int)lengthOfFile);
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
