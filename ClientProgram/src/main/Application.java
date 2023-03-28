package main;

import Services.*;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;

public class Application {
    public static void main(String[] args) {

        Console console = new Console(new Scanner(System.in));

        String serverIPAddress = console.inputString("Enter server IP address: ");
        int port = console.inputInt("Enter server port number: ");
        int timeout = console.inputInt("Enter socket timeout (s): ");
        try{

            ClientHandler client = new ClientHandler(port,serverIPAddress, timeout*1000);

            client.addService(0, new CheckFlightDetails());
            client.addService(1, new CheckFlightID());
            client.addService(2, new MakeReservation());
            client.addService(3, new CancelReservation());
            client.addService(4, new RegisterCallbackService());

            while(true){
                client.printMenu();
                int serviceNumber = console.inputInt("Enter service request or enter '-1' to terminate: ");
                if(serviceNumber ==-1) break;
                client.execute(serviceNumber, console);
            }

        }catch (UnknownHostException | SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
