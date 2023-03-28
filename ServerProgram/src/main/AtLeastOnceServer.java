package main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import Data.Marshalling;
import Socket.*;
import Services.*;
public class AtLeastOnceServer {
    protected UDPSocket socket;
    protected int portNumber;
    protected String ipAddress;
    protected final int bufferSize = 2048;
    protected byte[] buffer;

    protected HashMap<Integer, Service> idToServiceMap;

    public AtLeastOnceServer(UDPSocket socket) {
        this.socket = socket;
        this.buffer = new byte[bufferSize];
        this.idToServiceMap = new HashMap<>();
    }

    public void addService(int id, Service service) {
        if (!this.idToServiceMap.containsKey(id)) {
            this.idToServiceMap.put(id, service);
            System.out.println("Service added successfully");
        }        else{
            System.out.printf("There is no existing service using service id %d, please use a different id.\n",id);
        }
    }

    public void start() {
        while (true) {
            try {
                DatagramPacket request = receive();
                if(request.getLength()!=0){
                    byte[] data =request.getData();
                    InetAddress clientAddress = request.getAddress();
                    int clientPortNo = request.getPort();
                    int serviceRequested = data[0];
                    Service service = null;
                    if(idToServiceMap.containsKey(serviceRequested)){
                        service = idToServiceMap.get(serviceRequested);
                        System.out.println("Service Requested: " + service.ServiceName());
                        Marshalling replyToRequest = service.handleService(clientAddress,clientPortNo, data, this.socket);
                        this.socket.send(replyToRequest, clientAddress, clientPortNo);
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();

            } catch (NullPointerException e) {
                Console.print("Received corrupted data");
                e.printStackTrace();
            }
        }
    }
    public DatagramPacket receive() throws IOException{
        Arrays.fill(buffer, (byte) 0);	//empty buffer
        DatagramPacket request = new DatagramPacket(buffer, buffer.length);
        this.socket.receive(request);
        return request;
    }

}
