package main;

import Data.Marshalling;
import Socket.UDPSocket;
import Services.*;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.HashMap;


public class ClientHandler {
    public static final int CHECK_FLIGHT_ID = 0;
    public static final int CHECK_FLIGHT_DETAILS = 1;
    public static final int MAKE_RESERVATION = 2;
    public static final int CANCEL_RESERVATION = 3;
    public static final int FLIGHT_MONITOR = 4;
    public static final int GET_FLIGHTS_BY_PRICE = 5;

    private UDPSocket socket = null;
    private int serverPortNumber;
    private InetAddress InetIpAddress = null;
    private int requestID; //ID OF REQUEST
    private int timeout;

    private HashMap<Integer, Service> idToServiceMap;

    public static final int BUFFER_SIZE = 2048;
    private byte[] buffer = new byte[BUFFER_SIZE];

    public ClientHandler(int portNo, String IPAddress, int timeout) throws UnknownHostException, SocketException {
        this.idToServiceMap = new HashMap<>();
        this.InetIpAddress = InetAddress.getByName(IPAddress);
        this.socket = new UDPSocket(new DatagramSocket());
        this.serverPortNumber = portNo;
        this.requestID = 0;
        this.timeout = timeout;
        this.socket.setTimeOut(timeout);
    }
    public int getRequestID() {
        return requestID++;
    }
    public void addService(int serviceId, Service service){
        idToServiceMap.put(serviceId, service);
    }
    public void execute(int id, Console console) throws IOException {
        if(idToServiceMap.containsKey(id)){
            Service service = this.idToServiceMap.get(id);
            service.executeRequest(console, this);
        }
    }
    public UDPSocket getSocket(){
        return this.socket;
    }
    public int getTimeout(){
        return this.timeout;
    }
    public void send(Marshalling packer) throws IOException{
        this.socket.send(packer, this.InetIpAddress, this.serverPortNumber);
    }
    public DatagramPacket receive() throws IOException{
        Arrays.fill(buffer,(byte) 0);
        DatagramPacket p = new DatagramPacket(buffer,buffer.length);
        this.socket.receive(p);
        return p;
    }
    public void printMenu(){
        Console.print("---------------------- REQUEST OPTIONS -----------------------------");
        for(Integer serviceId : idToServiceMap.keySet()){
            Console.print(String.format("%d: %s", serviceId, idToServiceMap.get(serviceId).ServiceName()));
        }
    }
}
