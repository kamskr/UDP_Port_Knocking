package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

public class Client {
    public static void main(String[] args) throws IOException {
        int[] ports = new int[]{1234, 4321, 1324};
//        public String givenPorts = args[0];

        InetAddress address = InetAddress.getLocalHost();

        String text = "knock knock";

        byte[] queryBuff = String.valueOf(text).getBytes();
        DatagramPacket query = new DatagramPacket(queryBuff, queryBuff.length, address, ports[0]);
        DatagramPacket query2 = new DatagramPacket(queryBuff, queryBuff.length, address, ports[1]);
        DatagramPacket query3 = new DatagramPacket(queryBuff, queryBuff.length, address, ports[2]);

        DatagramSocket socket = new DatagramSocket();


        try {
            socket.send(query);
            TimeUnit.MILLISECONDS.sleep(100);
            socket.send(query2);
            TimeUnit.MICROSECONDS.sleep(100);
            socket.send(query3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



        System.out.println("Wyslalem text " + text);

        byte[] buff = new byte[508];
        DatagramPacket packet = new DatagramPacket(buff, buff.length);

        socket.receive(packet);

        String str = new String(packet.getData(), 0, packet.getLength()).trim();

        System.out.println("Dostalem odpowiedź: " + str);

        socket.close();
    }
}
