package Services;

import Data.Marshalling;
import Data.OneByteInt;
import Data.Unmarshalling;
import main.ClientHandler;
import main.Console;

import java.io.IOException;

public class MakeReservation extends Service{
    protected final static String FLIGHT_ID = "flightID";
    protected final static String SEATS = "seats";
    public MakeReservation(){
        super(null);
    }

    @Override
    public void executeRequest(Console console, ClientHandler client) throws IOException {
        Console.print("--------------------------------MAKE RESERVATION--------------------------------\n");
        int flight_id = Console.inputInt("Enter flight identifier:");
        int seats = Console.inputInt("Enter number of seats to reserve: ");
        int requestID = client.getRequestID();
        Marshalling packer = new Marshalling.Builder()
                .setProperty("serviceID", new OneByteInt(client.MAKE_RESERVATION))
                .setProperty("requestID", requestID)
                .setProperty(FLIGHT_ID, flight_id)
                .setProperty(SEATS, seats)
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
    }

    @Override
    public String ServiceName() {
        return "Make Reservation";
    }
}
