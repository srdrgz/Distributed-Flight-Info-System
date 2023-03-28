package Services;

import main.ClientHandler;
import main.Console;
import Data.Marshalling;
import Data.Unmarshalling;
import Data.OneByteInt;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketTimeoutException;

public class FlightMonitorRegistration extends Service{

    protected final static String TIMEOUT = "timeout";
    protected final static String FLIGHT_ID = "flightID";
    public FlightMonitorRegistration(){
        super(null);
    }

    @Override
    public void executeRequest(Console console, ClientHandler client) throws IOException {
        Console.print("---------------------Register Auto-monitoring---------------------------------");
        int interval = Console.inputInt("Enter monitor interval (minutes):");
        int flightID = Console.inputInt("Enter flight ID to monitor: ");
        int requestID = client.getRequestID();	/*This should only be called once for each executeRequest as the message_id will be incremented each time  this method is called*/
        Marshalling packer = new Marshalling.Builder()
                .setProperty(Service.SERVICE_ID, new OneByteInt(ClientHandler.FLIGHT_MONITOR))
                .setProperty(Service.REQUEST_ID, requestID)
                .setProperty(TIMEOUT, interval)
                .setProperty(FLIGHT_ID,flightID)
                .build();
        client.send(packer);

        //Wait for reply from server that says callback registered, then enter auto monitoring state
        Unmarshalling.UnpackedMsg unpackedMsg = receiveProcedure(client, packer, requestID);
        if(checkStatus(unpackedMsg)){
            String reply = unpackedMsg.getString(Service.REPLY);
            Console.print(reply);

            /*
             * Inside here, have while loop that runs infinitely,
             * call receive receive receive until 1 msg that has status 4 which means auto-monitoring expired.
             * */
        while(true){
                client.getSocket().setTimeOut(0);
                Unmarshalling.UnpackedMsg callbackMsg = callbackUpdatesHandler(client, requestID, super.getUnmarshaller());
                String callbackMsgReply = callbackMsg.getString(Service.REPLY);
                Console.print(callbackMsgReply);
                if(checkStatus(callbackMsg,4)){
                    client.getSocket().setTimeOut(client.getTimeout());
                    break;
                }
            }
        }

    }
    public Unmarshalling.UnpackedMsg callbackUpdatesHandler(ClientHandler client, int requestID, Unmarshalling unpacker) throws IOException{
        while(true){
            try{
                DatagramPacket reply = client.receive();
                Unmarshalling.UnpackedMsg unpackedMsg = unpacker.parseByteArray(reply.getData());
                if(checkMsgId(requestID,unpackedMsg)) return unpackedMsg;
            }catch (SocketTimeoutException e){
                //If socket receive function timeout, catch exception, resend request. Stays here until reply received? okay.
                Console.print("Socket timeout exception in callbackUpdates handler");

            }
        }

    }

    @Override
    public String ServiceName() {
        return "Flight Seat Monitor Registration";
    }




}
