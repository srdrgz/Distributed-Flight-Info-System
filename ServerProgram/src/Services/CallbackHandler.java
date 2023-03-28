package Services;


import main.Console;
import Data.Marshalling;
import Data.OneByteInt;
import Socket.UDPSocket;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class CallbackHandler implements Runnable {
    private UDPSocket socket;
    private static ArrayList<Subscriber> allTheSubscribers;

    public CallbackHandler(UDPSocket socket){
        this.socket = socket;
        allTheSubscribers = new ArrayList<>();

    }

    public void registerSubscriber(InetAddress address, int portNumber, int requestID, int interval, int flightID){
        Console.print("Timeout interval in register subscriber class: "+ interval);
        Subscriber subscriber = new Subscriber(address, portNumber, requestID, interval, flightID);

        //Check if client has already subscribed to callbackService or not
        if(checkExisting(address,portNumber,requestID,interval)){
            allTheSubscribers.add(subscriber);
            Console.print("New subscriber added");
            subscriber.printSubscriberInfo();
        }
        else{
            Console.print("Client exists in list of subscribers");
        }

    }

    public boolean checkExisting(InetAddress address, int portNumber, int requestID, int interval){
        //Console.debug("Check Existing");
        boolean DoesNotExists = true; //set to true, means no such subscriber
        for(Subscriber s: allTheSubscribers){
            s.printSubscriberInfo();
            if(s.address.equals(address) && s.portNumber==portNumber && s.requestID == requestID){
                DoesNotExists = false;
                break;
            }
        }
        return DoesNotExists;
    }

    public void checkValidity() throws IOException{
        Date now = new GregorianCalendar().getTime();
        ArrayList<Subscriber> temp = new ArrayList<>();
        for (Subscriber s: allTheSubscribers){
            if(now.after(s.expireTime.getTime())){
                Console.print("Removing subscriber:");
                s.printSubscriberInfo();
                OneByteInt status = new OneByteInt(4);
                sendTerminationMessage(s,status);
                temp.add(s);
            }
        }
        for(Subscriber x: temp){
            if(allTheSubscribers.contains(x)){
                allTheSubscribers.remove(x);
            }
        }
    }


    public void sendTerminationMessage(Subscriber s,OneByteInt status) throws IOException{
        Console.print("Sending termination message");
        String reply = "Monitoring Interval Time expired.";
        //System.out.println("subscriber messageId: " + s.messageId);
        Marshalling replyMessage = new Marshalling.Builder()
                .setProperty(Service.STATUS, status)
                .setProperty(Service.getRequestId(), s.requestID)
                .setProperty(Service.REPLY, reply)
                .build();
        socket.send(replyMessage, s.address, s.portNumber);
    }

    public void broadcast(Marshalling msg, int flightID){
        try {
            checkValidity();
            if(((OneByteInt)msg.getPropToValue().get(Service.getStatus())).getValue()==0){ //Only if reply status is 0, then broadcast out.
                if(!allTheSubscribers.isEmpty()){
                    Console.print("Sending packets to subscribers:");
                    for (Subscriber s: allTheSubscribers){
                        msg.getPropToValue().put(Service.getRequestId(), s.requestID); //replace msgId of reply to whoever that made the action of with msgId of subscriber.
                        if(s.flightID == flightID){
                            socket.send(msg, s.address, s.portNumber);
                        }
                    }
                }

            }
        } catch (IOException e) {

            System.out.println("Error broadcasting");
            e.printStackTrace();
        }


    }

    @Override
    public void run() {
        while(true){
            try {
                //Check validity every 10s on separate thread.
                checkValidity();
                Thread.sleep(10000);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }


    public static class Subscriber{
        private InetAddress address;
        private int portNumber;
        private Calendar expireTime;
        private int requestID;
        private int flightID;

        public Subscriber(InetAddress address, int portNumber, int requestID, int timeLimit, int flightID){
            this.address = address;
            this.portNumber = portNumber;
            this.requestID = requestID;
            this.flightID = flightID;
            //Calculate time to remove subscriber.
            expireTime = Calendar.getInstance();
            expireTime.add(Calendar.MINUTE, timeLimit);
            Console.print("timeLimit: " + timeLimit);
        }


        public void printSubscriberInfo(){
            Console.print("Flight ID: "+ flightID + ", Address: " + address.toString() + ", portNumber: " + portNumber + ", requestID: " + requestID + ", expireTime: " + expireTime.getTime());
        }
    }





}
