package Socket;


import Data.Marshalling;
import main.Console;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import java.net.SocketTimeoutException;
import java.net.InetAddress;
import java.util.Random;

public class CorruptedSocketReceiving extends UDPSocket {
    private final Random random;
    private final double probability;
    public CorruptedSocketReceiving(UDPSocket socket, double probability) {
        super(socket.getSocket());
        this.random = new Random();
        this.probability = probability;
        // TODO Auto-generated constructor stub
    }

    /**
     * Use probability to simulate packetloss
     * higher prob --> higher probability of sending. lower packetloss
     *
     */
    public void receive(DatagramPacket p) throws IOException, SocketTimeoutException{
        if(random.nextDouble()<this.probability){
            super.receive(p);
        }
        else{
            try {
                Thread.sleep(2000);
                Console.print("Simulating Packet loss when receiving for 2 seconds");
                Console.print("Throwing error message");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }throw new SocketTimeoutException();
        }
    }


}
