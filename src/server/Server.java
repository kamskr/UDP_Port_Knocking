package server;

import java.net.BindException;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {
    private List<UDPPort> sockets = new ArrayList<>();
    private int numOfPorts;
    public List<Integer> ports = new ArrayList<>();
    public  boolean[] check = new boolean[]{false, false, false};
    public int id = 0;
    public Map<InetAddress,KnockChecker> knockCheckers = new HashMap<>();

    private String givenPorts;


    public Server(String givenPorts) throws SocketException {
        this.givenPorts = givenPorts;
        initializeServer();
    }

//    Opening UDP sockets on ports provided by user
//    Sockets will wait for clients to make request

    private void initializeServer() throws SocketException {
        String[] portsAsString = givenPorts.split(",");
        numOfPorts = portsAsString.length;

        for(int i = 0; i < numOfPorts ; i++){
            int port = Integer.parseInt(portsAsString[i]);
            try {
                sockets.add(new UDPPort(port, this));
                System.out.println("adding port " + port);
                ports.add(port);
            }catch (BindException e){
                System.out.println("Port " + port + " already in use");
            }

        }
        System.out.print("INFO Server listens on ports: ");
        for(int port : ports){
            System.out.print(port + ", ");
        }
        System.out.println("\n");
    }

    public static void main(String[] args) {
        try{
            new Server("1234,4321,1324,5432");
        }catch (SocketException e){
            e.printStackTrace();
        }
    }
}
