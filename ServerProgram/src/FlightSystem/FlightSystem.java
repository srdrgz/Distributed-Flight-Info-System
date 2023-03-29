package FlightSystem;

import main.Console;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FlightSystem {
    public static HashMap<Integer, Flight> flights;
    public static int id = 0;
    public FlightSystem(){
        flights = new HashMap<Integer, Flight>();
    }

    //Add flight to flight "database"
    public static void addFlightToSystem(String source, String destination, String departure_time, double airfare){
        Flight flight = new Flight(departure_time, source, destination, airfare);
        flights.put(id++, flight);
    }

    //Returns true if there are any flights match the source and destination places
    public static boolean matchingFlights (String destination,String source) {
        boolean matching = false;
        for (Map.Entry<Integer, Flight> entry : flights.entrySet()) {
            String source_map = entry.getValue().getSource();
            String destination_map = entry.getValue().getDestination();
            if ((source_map).equalsIgnoreCase(source) && (destination_map).equalsIgnoreCase(destination)){
                    matching = true;
                }
            }
        return matching;
    }

    //Get all flights matching source and destination places
    public static String getMatchingFlightIds (String destination,String source ) {
        String result = "";
        for (Map.Entry<Integer, Flight> entry : flights.entrySet()) {
            String source_map = entry.getValue().getSource();
            String destination_map = entry.getValue().getDestination();
            if ((source_map).equalsIgnoreCase(source) && (destination_map).equalsIgnoreCase(destination)){
                result = String.format(result + "ID: " + entry.getKey() + "  Source: " + entry.getValue().getSource() + "    Destination: " + entry.getValue().getDestination()+ "   Departure time: "+ entry.getValue().getDeparture_time()+ "\n");
            }
        }
        return String.format(result);
    }

    //Returns flight object matching flight id
    public static Flight getFlightById(Integer flightID){
        Flight result =null;
        for (Map.Entry<Integer,Flight> entry : flights.entrySet()) {
            if (Objects.equals(entry.getKey(), flightID)) {
                result = entry.getValue();
                break;
            }
        }
        return result;
    }
    //If reply is -1 : invalid ID ; if reply is -2: Not enough available seats
    public static int makeReservation(int flightID, int requested_seats) {
        int reply;
        Flight flight = getFlightByID(flightID);
        if(getFlightById(flightID) == null) reply =-1;
        else if(!flight.reserveSeats(requested_seats)) reply = -2;
        else{
            reply = 1;
        }
        return reply;
    }
    public static int cancelReservation(int flightID, int requested_seats) {
        int reply = 1;
        Flight flight = getFlightByID(flightID);
        if(getFlightById(flightID) == null) {
            reply =-1;
        }
        else if(!(flight.cancelSeats(requested_seats))){
            reply = -2;}

        return reply;
    }
    public static Flight getFlightByID(int flightID){
        Flight result = null;
        for (Map.Entry<Integer,Flight> entry : flights.entrySet()) {
            if (entry.getKey() == flightID) {
                result = entry.getValue();
            }
        }
        return result;
    }
    public static String getFlightsByPriceRange(double max, double min){
        String result = "";
        for (Map.Entry<Integer, Flight> entry : flights.entrySet()) {
            double price = entry.getValue().getAirfare();
            if (min <= price && price <= max){
                result = String.format(result + "ID: " + entry.getKey() + "  Source: " + entry.getValue().getSource() + "    Destination: " + entry.getValue().getDestination()+ "   Departure time: "+ entry.getValue().getDeparture_time()+ " Air Fare: $" + price + "\n");
            }
        }
        return String.format(result);
    }
    public static boolean matchingPriceRange (double max,double min) {
        boolean matching = false;
        for (Map.Entry<Integer, Flight> entry : flights.entrySet()) {
            double price = entry.getValue().getAirfare();
            if (min <= price && price <= max){
                matching = true;
            }
        }
        return matching;
    }

    }


