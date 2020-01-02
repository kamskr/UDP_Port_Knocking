package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {
    public static void main(String[] args) throws IOException {
        int[] ports = new int[]{1234, 4321, 1324};
//        public String givenPorts = args[0];

        InetAddress address = InetAddress.getLocalHost();
        int port = 1234;

        String text = "knock knock";

        byte[] queryBuff = String.valueOf(text).getBytes();
        DatagramPacket query = new DatagramPacket(queryBuff, queryBuff.length, address, ports[1]);
        DatagramPacket query2 = new DatagramPacket(queryBuff, queryBuff.length, address, ports[2]);
        DatagramPacket query3 = new DatagramPacket(queryBuff, queryBuff.length, address, ports[0]);

        DatagramSocket socket = new DatagramSocket();

        socket.send(query);
        socket.send(query2);
        socket.send(query3);


        System.out.println("Wyslalem text " + text);

        byte[] buff = new byte[508];
        DatagramPacket packet = new DatagramPacket(buff, buff.length);

        socket.receive(packet);

        String str = new String(packet.getData(), 0, packet.getLength()).trim();

        System.out.println("Dostalem odpowiedź: " + str);

        socket.close();
    }
}
