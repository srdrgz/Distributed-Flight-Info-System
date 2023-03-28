package Socket;


import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;
import Data.*;
import main.*;

public class CorruptedSocketSending extends UDPSocket {
    private final Random random;
    private final double probability;
    public CorruptedSocketSending(UDPSocket socket, double probability) {
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
    public void send(Marshalling msg, InetAddress address, int port) throws IOException {
        if(random.nextDouble()<probability){
            super.send(msg, address, port);
        }
        else{
            try{
                Thread.sleep(1000);
                Console.print("Simulating Packet loss when Sending");
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }


}
