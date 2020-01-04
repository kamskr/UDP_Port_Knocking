package server;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class KnockChecker {
    private InetAddress ipAdress;
    private Server server;
    private int howManySockets;
    private int counter = 0;
    public List<Boolean> check = new ArrayList<>();
    private List<UDPPort> sockets = new ArrayList<>();



    public KnockChecker(InetAddress ipAdress, Server server) {
        this.ipAdress = ipAdress;
        this.server = server;
        howManySockets = server.getNumOfPorts();
        for(int i = 0; i < howManySockets; i++){
            check.add(false);
        }
    }

    public void check(UDPPort udpPort) {


        new Thread(() -> {
            System.out.println("INFO: Trying to connect with the server...");

            try {
                for(int i = 1; i <= 2; i++){
                    TimeUnit.MILLISECONDS.sleep(1000);
                    System.out.println(i);
                }
                System.out.println("INFO: System timeout, declining access!");
                counter = howManySockets - 1;
            } catch (InterruptedException e) {
                //do nothing
            }
        }).start();

        if(!check.get(counter) && udpPort.getPort() == server.ports.get(counter)){
            check.set(counter,true);
            System.out.println("INFO: Port number " + (counter + 1) + " is correct");
            sockets.add(udpPort);
        }else{
            System.out.println("INFO: Incorrect port!");
            counter = howManySockets - 1;
        }

        if(check.contains(false) && counter == howManySockets -1 ){
            counter = 0;
            System.out.println("\n\n\nINFO: Wrong knock sequence sorry\n" + check + "\n\n");
            check.clear();
            for(int i = 0; i < howManySockets; i++){
                check.add(false);
            }
            return;
        }

        if (!check.contains(false) && counter == howManySockets - 1){
            System.out.println("\n\n\n" +check + "\n");
            sockets.get(new Random().nextInt( howManySockets)).setSendMessage(true);
            counter = 0;
            check.clear();
            for(int i = 0; i < howManySockets; i++){
                check.add(false);
            }
            return;
        }

        counter++;
    }


}
