package Services;

import Data.Marshalling;
import Data.OneByteInt;
import Data.Unmarshalling;
import main.ClientHandler;
import main.Console;

import java.io.IOException;

public class FlightByPriceRange extends Service{
    public FlightByPriceRange() {
        super(null);
    }

    @Override
    public void executeRequest(Console console, ClientHandler client) throws IOException {
        Console.print("---------------------------GET FLIGHT IN SPECIFIED PRICE RANGE------------------------------\n");
        //Properties
        double maxPrice = Console.inputDouble("Enter maximum price:");
        double minPrice = Console.inputDouble("Enter minimum price");
        int requestID = client.getRequestID();
        Marshalling packer = new Marshalling.Builder()
                .setProperty("ServiceId", new OneByteInt(client.GET_FLIGHTS_BY_PRICE))
                .setProperty("requestID", requestID)
                .setProperty("maxPrice", maxPrice)
                .setProperty("minPrice", minPrice)
                .build();
        client.send(packer);
        Unmarshalling.UnpackedMsg unpackedMsg = receiveProcedure(client, packer, requestID);
        if(checkStatus(unpackedMsg)){
            String reply = unpackedMsg.getString(Service.REPLY);
            Console.print(reply);
        }
        else{
            Console.print("Request failed.");
        }
    }

    @Override
    public String ServiceName() {
        return "Get Flight in Specified Price Range";
    }
}
