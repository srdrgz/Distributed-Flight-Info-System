package Socket;

import Data.Marshalling;
import main.Console;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPSocket implements Socket{

    private DatagramSocket socket;

    public UDPSocket(DatagramSocket socket){
        this.socket = socket;
    }


    public void send(Marshalling msg, InetAddress address, int port) throws IOException {
      //  Console.print("Sending message");
        byte[] message = msg.getByteArray();
        DatagramPacket p = new DatagramPacket(message, message.length,address, port);
        send(p);
        return;
    }

    public void receive (DatagramPacket p) throws IOException {
        this.socket.receive(p);
       // Console.print("Receiving message from server");
        return;
    }


    public void close() {
        Console.print("Closing socket");
        this.socket.close();
        return;
    }


    public void setTimeOut(int timeout) throws SocketException {
        this.socket.setSoTimeout(timeout);
    }

    public DatagramSocket getSocket() {
        return socket;
    }

    public void setSocket(DatagramSocket socket) {
        this.socket = socket;
    }

    public void send(DatagramPacket p) throws IOException{
        this.socket.send(p);
    }

}
