package Services;

import Data.Marshalling;
import Data.OneByteInt;
import Data.Unmarshalling;
import main.ClientHandler;
import main.Console;

import java.io.IOException;

public class CheckFlightID extends Service{
    protected final static String SOURCE = "source";
    protected final static String DESTINATION = "destination";
    public CheckFlightID(){
        super(null);
    }

    @Override
    public void executeRequest(Console console, ClientHandler client) throws IOException {
        Console.print("--------------------------------CHECK FLIGHT ID--------------------------------\n");
        int requestID = client.getRequestID();
        String source = Console.inputString("Enter flight departure place: ");
        String destination = Console.inputString("Enter flight destination place: ");
        Marshalling packer = new Marshalling.Builder()
                .setProperty("serviceID", new OneByteInt(client.CHECK_FLIGHT_ID))
                .setProperty("requestID", requestID)
                .setProperty(SOURCE, source)
                .setProperty(DESTINATION, destination)
                .build();
        client.send(packer);

        Unmarshalling.UnpackedMsg unpackedMsg = receiveProcedure(client, packer, requestID);
        if(checkStatus(unpackedMsg)){
            String reply = unpackedMsg.getString(Service.REPLY);
            Console.print(reply);
        }
        else{
            Console.print("Flight check failed");
        }
        //Console.print("Message_id: "+ requestID);
    }

    @Override
    public String ServiceName() {
        return "Get flight IDs of matching Source/Destination";
    }
}
