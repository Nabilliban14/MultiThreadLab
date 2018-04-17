/* MULTITHREADING <BookingClient.java>
 * EE422C Project 6 submission by
 * Replace <...> with your actual data.
 * <Nabil Khan>
 * <nk7742>
 * <Unique No. 16275>
 * Slip days used: <0>
 * Fall 2017
 */
package assignment6;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.Thread;

public class BookingClient {
    Map<String, Integer> office;
    ArrayList<Integer> IDStartVal;
    Theater theater;
    /*
     * @param office maps box office id to number of customers in line
     * @param theater the theater where the show is playing
     */
    public BookingClient(Map<String, Integer> office, Theater theater) {
        this.office = office;
        this.theater = theater;

        IDStartVal = new ArrayList<>();

        int start = 1;
        for (Map.Entry<String, Integer> entry: office.entrySet()) {
            IDStartVal.add(start);
            start += entry.getValue();
        }
    }

    /*
     * Starts the box office simulation by creating (and starting) threads
     * for each box office to sell tickets for the given theater
     *
     * @return list of threads used in the simulation,
     *         should have as many threads as there are box offices
     */
    public List<Thread> simulate() {
        List<Thread> runningThreads = new ArrayList<>();

        //tracks the start clientID of each box office
        int startIDCounter = 0;

        //iterate through the map
        for (Map.Entry<String, Integer> entry: office.entrySet()) {
           //creates a new box office thread
            BoxOfficeOpens opening = new BoxOfficeOpens(entry.getKey(), entry.getValue(),
                    IDStartVal.get(startIDCounter), theater);
            //go to next box office start ClientID
            startIDCounter++;

            //start the thread. Execute thread's run() method
            opening.start();

            //add thread to list
            runningThreads.add(opening);
        }
        return runningThreads;
    }

    public static class BoxOfficeOpens extends Thread {
        private String boxOffice;
        private int numofClients;
        private int startID;
        private Theater theater;

        public BoxOfficeOpens(String boxOffice, int numOfClients, int startID, Theater theater) {
            this.boxOffice = boxOffice;
            this.numofClients = numOfClients;
            this.startID = startID;
            this.theater = theater;
        }

        @Override
        public void run() {

            //for every client
            for (int i = 0; i < this.numofClients; i++) {

                //set the seat to null. It will be found in the printTicket function
                //the purpose of doing this is to improve synchronization
                Theater.Seat bestSeat = null;
                Theater.Ticket ticket = theater.printTicket(boxOffice, bestSeat, startID);

                //stops thread executions if no more tickets are left
                if (theater.getSoldOut() == true) {
                    break;
                }

                //if the seat is available
                if (ticket != null) {

                    //if it is the last ticket, break and set flag
                    if (ticket.getSeat().getRowNum() == theater.getNumRows() &&
                            ticket.getSeat().getSeatNum() == theater.getSeatsPerRow()) {
                        theater.setSoldOut(true);
                        System.out.println("Sorry, we are sold out!");
                        break;
                    }

                    //go to next client
                    else {
                        startID++;
                    }
                }
            }
        }
    }

    public static void main (String[] args) {
        //Initialize office
        Map<String, Integer> office = new HashMap<>();
        office.put("BX1", 34);
        office.put("BX3", 34);
        office.put("BX2", 34);
       // office.put("BX5", 3);
        //office.put("BX4", 3);

        //initialize theater
        Theater theater = new Theater(3,33,"Ouija");

        //initialize bookingclient
        BookingClient example = new BookingClient(office, theater);

        List<Thread> runningThreads = example.simulate();
    }
}
