package Services;

import Data.Marshalling;
import Data.OneByteInt;
import Data.Unmarshalling;
import FlightSystem.Flight;
import FlightSystem.FlightSystem;
import Socket.UDPSocket;
import main.Console;

import java.io.IOException;
import java.net.InetAddress;

public class CheckFlightDetails extends Service {
    protected final static String FLIGHT_ID = "flightId";
    public CheckFlightDetails() {
        super(new Unmarshalling.Builder().setType(FLIGHT_ID, Unmarshalling.TYPE.INTEGER).build());


    }

    @Override
    public Marshalling handleService(InetAddress clientAddr, int clientPortNo, byte[] clientData, UDPSocket socket) throws IOException, NullPointerException {
        Unmarshalling.UnmarshalledMSG unpackedMsg = this.getUnmarshaller().parseByteArray(clientData);
        int flightID = unpackedMsg.getInteger(FLIGHT_ID);
        int requestID = unpackedMsg.getInteger(super.getRequestId());
        Flight result = FlightSystem.getFlightById(flightID);
        String reply = "";
        OneByteInt status = new OneByteInt(0);
        if(result == null){
            reply = "The flight ID does not match any flights in the system.";
        }
        else{
            reply = String.format("Flight ID: " + flightID + "\n" + "Departure Time: " + result.getDeparture_time() + "\n" +
                    "Source: " + result.getSource() + "\nDestination: " + result.getDestination() + "\nAirfare: $" + result.getAirfare() + "\nNumber of available seats: " + result.getNoAvailableSeats());

        }

        return super.generateReply(status,requestID, reply);
    }

    @Override
    public String ServiceName() {
        return "Check Flight Details";
    }
}
