package Services;

import Data.Marshalling;
import Data.OneByteInt;
import Data.Unmarshalling;
import FlightSystem.FlightSystem;
import Socket.UDPSocket;

import java.io.IOException;
import java.net.InetAddress;

public class FlightByPriceRange extends Service{
    protected final static String MAX_PRICE = "maxPrice";
    protected final static String MIN_PRICE = "minPrice";

    public FlightByPriceRange() {
        super(new Unmarshalling.Builder()
                .setType(MAX_PRICE, Unmarshalling.TYPE.DOUBLE)
                .setType(MIN_PRICE, Unmarshalling.TYPE.DOUBLE)
                .build());
    }

    @Override
    public Marshalling handleService(InetAddress clientAddr, int clientPortNo, byte[] clientData, UDPSocket socket) throws IOException, NullPointerException {

        Unmarshalling.UnmarshalledMSG unpackedMsg = this.getUnmarshaller().parseByteArray(clientData);
        int requestID = unpackedMsg.getInteger(super.getRequestId());
        double maxPrice = unpackedMsg.getDouble(MAX_PRICE);
        double minPrice = unpackedMsg.getDouble(MIN_PRICE);
        String result = FlightSystem.getFlightsByPriceRange(maxPrice,minPrice);
        OneByteInt status = new OneByteInt(0);
        String reply = "";
        if(!FlightSystem.matchingPriceRange(maxPrice,minPrice)){
            reply = "There are no flights in the specified price range";
        }
        else{
            reply = String.format("The following flights match the specified price range: \n" + result);

        }
        return super.generateReply(status,requestID, reply);
    }

    @Override
    public String ServiceName() {
        return "Get Flights By Specified Price Range";
    }
}
