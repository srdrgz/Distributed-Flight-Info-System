package Services;

import Data.Marshalling;
import Data.OneByteInt;
import Data.Unmarshalling;
import main.ClientHandler;
import main.Console;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketTimeoutException;
/*
* */
public abstract class Service {
    private final Unmarshalling unmarshaller;
    protected static final String STATUS = "status";
    protected static final String SERVICE_ID = "serviceId";
    protected static final String REQUEST_ID = "requestID";
    protected static final String REPLY = "reply";

    protected Service(Unmarshalling unmarshalling){
        this.unmarshaller = new Unmarshalling.Builder()
                .setType(STATUS, Unmarshalling.TYPE.ONE_BYTE_INT)
                .setType(REQUEST_ID, Unmarshalling.TYPE.INTEGER)
                .setType(REPLY, Unmarshalling.TYPE.STRING)
                .build()
                .defineComponents(unmarshalling);
    }
    // receiveProcedure() : receives the information from the socket through the client function receive() and
    public final Unmarshalling.UnpackedMsg receiveProcedure(ClientHandler client, Marshalling marshaller, int requestID ) throws IOException {
        while(true){
            try{
                DatagramPacket reply = client.receive();
                Unmarshalling.UnpackedMsg unpackedMsg = this.getUnmarshaller().parseByteArray(reply.getData());
                if(checkMsgId(requestID,unpackedMsg)){
                    return unpackedMsg;
                }
            }catch (SocketTimeoutException e){
                Console.print("Socket timeout.");
                client.send(marshaller);
            }
        }
    }

    public Unmarshalling getUnmarshaller() {
        return unmarshaller;
    }
    public final boolean checkMsgId(Integer requestID, Unmarshalling.UnpackedMsg unpackedMsg){
        Integer return_requestID = unpackedMsg.getInteger(REQUEST_ID);
        if(return_requestID != null){
            return requestID == return_requestID;
        }
        return false;
    }
    public final boolean checkStatus(Unmarshalling.UnpackedMsg unpackedMsg){
        OneByteInt status = unpackedMsg.getOneByteInt(STATUS);
        if(status.getValue()==0)return true; //0 means no error? okay.
        return false;
    }
    public final boolean checkStatus(Unmarshalling.UnpackedMsg unpackedMsg, int replyStatus){
        OneByteInt status = unpackedMsg.getOneByteInt(STATUS);
        if(status.getValue()==replyStatus)return true;
        return false;
    }

    //METHODS TO BE IMPLEMENTED BY SERVICE INSTANCES
    public abstract void executeRequest(Console console, ClientHandler client) throws IOException;
    public abstract String ServiceName();
}
