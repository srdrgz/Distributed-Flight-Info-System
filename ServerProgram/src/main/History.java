package main;

import Data.Marshalling;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import Data.*;
public class History {
    private ArrayList<Client> clientList;
    public static final int HISTORY_RECORD_SIZE = 10;
    public History(){
        clientList = new ArrayList<>();
    }

    public Client findClient(InetAddress address, int port){
        for(Client c : clientList){
            if(c.address.equals(address) && c.portNumber==port){
                return c;
            }
        }
        Client newClient = new Client(address, port);
        clientList.add(newClient);
        return newClient;
    }

    public class Client {
        private InetAddress address;
        private int portNumber;
        private HashMap<Integer, Marshalling> messageIdToReplyMap;
        private int[] historyRecord;
        private int count;

        public Client(InetAddress address, int portNumber) {
            this.address = address;
            this.portNumber = portNumber;
            this.messageIdToReplyMap = new HashMap<>();
            historyRecord = new int[HISTORY_RECORD_SIZE]; //keep 10 messages in history
            count = 0;
            Arrays.fill(historyRecord, -1);
        }

        public Marshalling searchForDuplicateRequest(int messageId) {
            Marshalling reply = this.messageIdToReplyMap.get(messageId);
            if (reply != null) {
                Console.print("Request already serviced. Resending reply");
            }
            return reply;
        }

        public void addServicedReqToMap(int messageId, Marshalling replyToServicedReq) {
            if (historyRecord[count] != -1) {
                messageIdToReplyMap.remove(historyRecord[count]);
            }
            this.messageIdToReplyMap.put(messageId, replyToServicedReq);
            historyRecord[count] = messageId;
            count = (count + 1) % HISTORY_RECORD_SIZE;

        }


    }
}
