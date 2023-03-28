package main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;

import Data.Marshalling;
import Data.Unmarshalling;
import Socket.*;
import Services.*;
import main.History.Client;
public class AtMostOnceServer extends AtLeastOnceServer{
    private History history;

    public AtMostOnceServer(UDPSocket socket) throws SocketException {
        super(socket);
        history = new History();
    }
    public void start(){
        while(true){
            try{
                DatagramPacket p = receive(); /*Create DatagramPacket to receive requests from clients, assumes that it has no problem receiving.*/
                if(p.getLength()!=0){
                    byte[] data = p.getData();
                    InetAddress clientAddress = p.getAddress();
                    int clientPortNumber = p.getPort();
                    //Service ID from client is the first byte in the byte array sent from client
                    int serviceRequested = data[0];
                    Service service = null;


                    if(idToServiceMap.containsKey(serviceRequested)){
                        service = idToServiceMap.get(serviceRequested);
                        Unmarshalling.UnmarshalledMSG unpackedMsg = service.getUnmarshaller().parseByteArray(data);
                        int messageId = unpackedMsg.getInteger(Service.getRequestId());
                        Client client = history.findClient(clientAddress, clientPortNumber);
                        Marshalling replyToServicedReq = client.searchForDuplicateRequest(messageId);
                        if(replyToServicedReq == null){
                            replyToServicedReq = service.handleService(clientAddress, clientPortNumber, data, this.socket);
                            client.addServicedReqToMap(messageId, replyToServicedReq);
                        }
                        this.socket.send(replyToServicedReq, clientAddress, clientPortNumber);
                    }
                }
            }catch(IOException e){
                e.printStackTrace();
            }catch(NullPointerException e){
                e.printStackTrace();
            }finally{
                continue;
            }
        }
    }
}
