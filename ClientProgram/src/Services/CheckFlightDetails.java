package Services;

import Data.Marshalling;
import main.ClientHandler;
import main.Console;
import Data.*;
import java.io.IOException;

public class CheckFlightDetails extends Service{
    public CheckFlightDetails(){
        super(null);
    }

    @Override
    public void executeRequest(Console console, ClientHandler client) throws IOException {
        Console.print("--------------------------------CHECK FLIGHT DETAILS--------------------------------\n");
        //Properties
        int flight_id = Console.inputInt("Enter flight identifier:");

        int requestID = client.getRequestID();

       // Console.print("Message_id: "+ requestID+ " ");
       // Console.print("Flight_id: "+ flight_id+ "\n ");

        Marshalling packer = new Marshalling.Builder()
                .setProperty("ServiceId", new OneByteInt(client.CHECK_FLIGHT_DETAILS))
                .setProperty("requestID", requestID)
                .setProperty("flightId", flight_id)
                .build();
        client.send(packer);

        Unmarshalling.UnpackedMsg unpackedMsg = receiveProcedure(client, packer, requestID);
        if(checkStatus(unpackedMsg)){
            String reply = unpackedMsg.getString(Service.REPLY);
            Console.print(reply);
        }
        else{
            Console.print("Transfer failed");
        }
    }

    @Override
    public String ServiceName() {
        return "Get Flight Information by Flight ID";
    }
}
