package Services;

import Data.Marshalling;
import Data.Unmarshalling;
import Data.OneByteInt;
import FlightSystem.FlightSystem;
import Socket.Socket;
import main.Console;
import Socket.UDPSocket;
import java.net.InetAddress;

/**
 * This class handles requests to subscribe to the callback service.
 * @author Shide
 *
 */
public class RegisterCallbackService extends Service {

    protected final static String TIMEOUT = "timeout";
    protected final static String FLIGHT_ID = "flightID";
    private CallbackHandler callbackHandler;
    public RegisterCallbackService(CallbackHandler callbackHandler) {
        super(new Unmarshalling.Builder()
                .setType(TIMEOUT, Unmarshalling.TYPE.INTEGER)
                .setType(FLIGHT_ID, Unmarshalling.TYPE.INTEGER)
                .build());
        this.callbackHandler = callbackHandler;

    }

    @Override
    public Marshalling handleService(InetAddress clientAddress, int clientPortNumber, byte[] dataFromClient,
                                    UDPSocket socket) {

        Unmarshalling.UnmarshalledMSG unpackedMsg = this.getUnmarshaller().parseByteArray(dataFromClient);
        int requestID = unpackedMsg.getInteger(Service.getRequestId());
        int timeout = unpackedMsg.getInteger(TIMEOUT);
        int flightID = unpackedMsg.getInteger(FLIGHT_ID);
        //Create subscriber object
        callbackHandler.registerSubscriber(clientAddress, clientPortNumber, requestID, timeout, flightID);
        OneByteInt status = new OneByteInt(0);
        String reply = String.format("Monitoring seat availability of flight ID:  " + flightID + ". There are " + FlightSystem.getFlightById(flightID).getNoAvailableSeats() +" available seats"+", waiting for updates...");
        Marshalling replyMessage = super.generateReply(status, requestID, reply);
        return replyMessage;

    }

    @Override
    public String ServiceName() {
        return "Register Callback";
    }

}