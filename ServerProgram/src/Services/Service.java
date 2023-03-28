package Services;

import Data.*;
import Socket.*;
import java.io.IOException;
import java.net.InetAddress;
public abstract class Service {
    private Unmarshalling unmarshaller;
    protected static final String SERVICE_ID = "serviceId";
    private static final String REQUEST_ID = "requestId";
    protected static final String STATUS = "status";
    protected static final String REPLY = "reply";

    public Service(Unmarshalling unmarshaller){
        this.setUnmarshaller(new Unmarshalling.Builder()
                .setType(SERVICE_ID, Unmarshalling.TYPE.ONE_BYTE_INT)
                .setType(getRequestId(), Unmarshalling.TYPE.INTEGER)
                .build()
                .defineComponents(unmarshaller));
    }

   public Marshalling generateReply(OneByteInt status, int requestId, String reply){
       Marshalling replyMessage = new Marshalling.Builder()
               .setProperty(STATUS, status)
               .setProperty(getRequestId(), requestId)
               .setProperty(REPLY, reply)
               .build();
       return replyMessage;
    }
    public static String getStatus(){
        return STATUS;
    }
    public abstract Marshalling handleService(InetAddress clientAddr, int clientPortNo, byte[] clientData, UDPSocket socket) throws IOException, NullPointerException;
    public abstract String ServiceName();
    public static String getRequestId() {
        return REQUEST_ID;
    }

    public void setUnmarshaller(Unmarshalling unmarshaller) {
        this.unmarshaller = unmarshaller;
    }
    public Unmarshalling getUnmarshaller() {
        return unmarshaller;
    }
}
