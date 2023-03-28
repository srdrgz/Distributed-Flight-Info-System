package Services;

import Data.Marshalling;
import Data.OneByteInt;
import Data.Unmarshalling;
import FlightSystem.FlightSystem;
import FlightSystem.Flight;
import Socket.UDPSocket;

import java.io.IOException;
import java.net.InetAddress;

public class CancelReservation extends Service{
    private CallbackHandler callbackHandler;
    protected final static String FLIGHT_ID = "FlightID";
    protected final static String SEATS = "seats";

    public CancelReservation(CallbackHandler callbackHandler) {
        super(new Unmarshalling.Builder()
                .setType(FLIGHT_ID, Unmarshalling.TYPE.INTEGER)
                .setType(SEATS, Unmarshalling.TYPE.INTEGER).build());
        this.callbackHandler = callbackHandler;
    }

    @Override
    public Marshalling handleService(InetAddress clientAddr, int clientPortNo, byte[] clientData, UDPSocket socket) throws IOException, NullPointerException {
        Unmarshalling.UnmarshalledMSG unpackedMsg = this.getUnmarshaller().parseByteArray(clientData);
        int requestID = unpackedMsg.getInteger(super.getRequestId());
        int flightId = unpackedMsg.getInteger(FLIGHT_ID);
        int requested_seats = unpackedMsg.getInteger("seats");
        OneByteInt status = new OneByteInt(0);
        String reply = "";
        String replyForSubscribers = "";
        int result = FlightSystem.cancelReservation(flightId, requested_seats);
        if(result == -1){
            reply ="Invalid Flight ID.";
        } else if (result == -2) {
            Flight flight = FlightSystem.getFlightByID(flightId);
            int NoBookedSeats = flight.getNoBookedSeats();
            reply = String.format("ERROR: There are only " + NoBookedSeats + " seats booked for this flight.");
        }
        else{
            Flight flight = FlightSystem.getFlightByID(flightId);
            reply = String.format("The reservation was cancelled successfully. There are " + flight.getNoAvailableSeats()+" available seats.");
            replyForSubscribers = String.format("There are " + flight.getNoAvailableSeats() + " available seats for flight ID: " + flightId);
            Marshalling replyMsgSubscribers = super.generateReply(status,requestID, replyForSubscribers);
            callbackHandler.broadcast(replyMsgSubscribers, flightId);
        }
        return super.generateReply(status,requestID, reply);
    }

    @Override
    public String ServiceName() {
        return "Cancel Reservation";
    }
}
