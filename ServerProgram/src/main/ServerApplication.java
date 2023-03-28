package main;

import FlightSystem.*;
import Services.*;
import Socket.*;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;

public class ServerApplication {
    private static AtLeastOnceServer server;
    private static FlightSystem flightSystem;
    private static CallbackHandler callbackHandler;
    private static InetAddress address;
    private static UDPSocket socket;
    private static int portNo;

    public static void main(String[] args) {
        Console console = new Console(new Scanner(System.in));
        try {
            System.out.println("Starting server");
            flightSystem = new FlightSystem();
            flightSystem.addFlightToSystem("Madrid", "London", "17:00", 150.50);
            flightSystem.addFlightToSystem("Tokyo", "Singapore", "7:30", 90.70);
            flightSystem.addFlightToSystem("Barcelona", "Munich", "11:50", 35.00);
            flightSystem.addFlightToSystem("Barcelona", "Munich", "9:00", 30.00);
            //SET PARAMETERS TO START SERVER
            String addressInput = console.inputString("Input IP address hosting server on:");
            address = InetAddress.getByName(addressInput);
            portNo = console.inputInt("Input port number for server to listen at:");
            socket = new UDPSocket(new DatagramSocket(portNo,address));

            //CHOOSE TYPE OF SERVER
            int serverChoice = console.inputInt(1, 2, "Select Server type: \n1)At-Least-Once\n2)At-Most-Once");
            if(serverChoice==1){
                server = new AtLeastOnceServer(socket); //at-least-once server
            }
            else if(serverChoice==2){
                server = new AtMostOnceServer(socket); //at-most-once server
            }

            /*Specify what type of socket to use*/
            int socketType = console.inputInt(1, 4, "Select Socket Type: \n1)Normal Socket\n2)Socket with Sending Loss" +
                    "\n3)Socket with Receiving Loss\n4)Socket with Sending and Receiving Loss\n");
            if(socketType==2){
                double probability = (1 - console.inputDouble(0.0, 1.0, "Probability of packet loss (in decimals) :"));
                server.useCorruptedSocketSending(probability);
            }
            else if(socketType==3){
                double probability = (1 - console.inputDouble(0.0, 1.0, "Probability of packet loss (in decimals) :"));
                server.useCorruptedSocketReceiving(probability);
            }
            else if(socketType==4){
                double probability = (1 - console.inputDouble(0.0, 1.0, "Probability of packet loss (in decimals) :"));
                server.useCorruptedSocket(probability);
            }

            //THREAD TO HANDLE REMOVAL OF EXPIRED USERS
            callbackHandler = new CallbackHandler(socket);
            Thread validityCheck = new Thread(callbackHandler);
            validityCheck.start();
            //Add services
            server.addService(0, new CheckFlightID(callbackHandler));
            server.addService(1, new CheckFlightDetails(callbackHandler));
            server.addService(2, new MakeReservation(callbackHandler));
            server.addService(3, new CancelReservation(callbackHandler));
            server.addService(4,new RegisterCallbackService(callbackHandler));
            //start server
            server.start();
        }catch (SocketException e) {

            e.printStackTrace();
        } catch (IOException e) {
            Console.print("Server error");
            e.printStackTrace();
        }

    }
}
