package Services;

import Data.Marshalling;
import Data.OneByteInt;
import Data.Unmarshalling;
import FlightSystem.Flight;
import FlightSystem.FlightSystem;
import Socket.UDPSocket;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

public class CheckFlightID extends Service{
    protected final static String SOURCE = "flightSource";
    protected final static String DESTINATION = "flightDestinaton";
    public CheckFlightID() {
        super(new Unmarshalling.Builder().setType(SOURCE, Unmarshalling.TYPE.STRING).setType(DESTINATION,Unmarshalling.TYPE.STRING).build());
        //Create Unmarshallling class and set types for main variables to be sent, then build
    }

    @Override
    public Marshalling handleService(InetAddress clientAddr, int clientPortNo, byte[] clientData, UDPSocket socket) throws IOException, NullPointerException {
        Unmarshalling.UnmarshalledMSG unpackedMsg = this.getUnmarshaller().parseByteArray(clientData);
        String destination = unpackedMsg.getString(DESTINATION);
        String source = unpackedMsg.getString(SOURCE);
        int requestID = unpackedMsg.getInteger(getRequestId());
        OneByteInt status = new OneByteInt(0);
        boolean matching = FlightSystem.matchingFlights(destination, source);
        String r = FlightSystem.getMatchingFlightIds(destination,source);
        String reply = "";
        if(!matching){
            reply = "No flights match the source and destination places";
        }
        else{
            reply = String.format("The following flights match source and destination places: \n" + r);

        }
        return super.generateReply(status,requestID, reply);
    }

    @Override
    public String ServiceName() {
        return "Get Flight Information by ID";
    }
}
