package Socket;
import Data.Marshalling;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import Data.Marshalling;
import main.*;

public interface Socket {
    void send(Marshalling msg ,InetAddress address, int port) throws IOException;
    void receive(DatagramPacket p) throws IOException;
    void close();
    void setTimeOut(int timeout) throws SocketException;
}
