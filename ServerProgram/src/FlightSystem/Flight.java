package FlightSystem;

import java.util.HashMap;

public class Flight {

    private String departure_time;
    private final int max_seats = 100;
    private int available_seats;
    private String source;
    private String destination;
    private double airfare;
    private HashMap<Integer, Integer> ClientRecords;
    public Flight(String departure_time, String source, String destination, double airfare){
        this.airfare = airfare;
        this.departure_time = departure_time;
        this.available_seats = max_seats;
        this.source = source;
        this.destination = destination;
        this.ClientRecords = new HashMap<>();
    }

    public String getDeparture_time(){
        return this.departure_time;
    }
    public String getSource(){
        return this.source;
    }
    public String getDestination(){
        return this.destination;
    }
    public double getAirfare(){
        return this.airfare;
    }
    public int getNoBookedSeats(){
        return max_seats - available_seats;
    }
    public boolean reserveSeats(int requested_seats){
        boolean success = false;
        if(requested_seats <= available_seats){
            available_seats = available_seats-requested_seats;
            success = true;
        }
        return success;
    }
    public boolean cancelSeats (int requestedSeats){
        boolean success = true;
        if(requestedSeats > getNoBookedSeats()){
            success = false;
        } else{
            available_seats = available_seats+requestedSeats;
        }
        return success;
    }
    public int getNoAvailableSeats (){
        return available_seats;
    }
}
