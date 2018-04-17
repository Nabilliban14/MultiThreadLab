/* MULTITHREADING <Theater.java>
 * EE422C Project 6 submission by
 * Replace <...> with your actual data.
 * <Nabil Khan>
 * <nk7742>
 * <Unique No. 16275>
 * Slip days used: <0>
 * Fall 2017
 */
package assignment6;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class Theater {
    private int numRows;
    private int seatsPerRow;
    private int nextBestRow;
    private int nextBestSeatInRow;
    private boolean soldOut = false;
    private String show;
    private ArrayList<Ticket> soldTickets;


    public boolean getSoldOut() {
        return soldOut;
    }
    public void setSoldOut(boolean value) {
        soldOut = value;
    }

    public int getNumRows() {
        return numRows;
    }

    public int getSeatsPerRow() {
        return seatsPerRow;
    }

    /*
     * Represents a seat in the theater
     * A1, A2, A3, ... B1, B2, B3 ...
     */
    static class Seat {
        private int rowNum;
        private int seatNum;

        public Seat(int rowNum, int seatNum) {
            this.rowNum = rowNum;
            this.seatNum = seatNum;
        }

        public int getSeatNum() {
            return seatNum;
        }

        public int getRowNum() {
            return rowNum;
        }

        @Override
        public String toString() {
            // TODO: Implement this method to return the full Seat location ex: A1
            String letters = "";
            letters = recursiveRowLetters(letters,rowNum);
            letters += Integer.toString(seatNum);
            return letters;
        }

        /*
         * Uses recursion to convert the row number into a string of characters
         */
        private static String recursiveRowLetters(String letters, int rowNum) {
            if (rowNum == 0 ) {
                return "";
            }
            else {
                int letterPos = rowNum % 26;

                /*
                 * Edge case for Z letter case
                 */
                if (letterPos == 0) {
                    letters += recursiveRowLetters(letters, rowNum/26 - 1);
                    letters += "Z";
                }

                /*
                 * Calls recursion again, then adds letter*/
                else {
                    char start = 'A';
                    start+=(letterPos - 1);
                    letters += recursiveRowLetters(letters, rowNum/26);
                    letters += Character.toString(start);
                }
                return letters;
            }
        }
    }

    /*
       * Represents a ticket purchased by a client
       */
    static class Ticket {
        private String show;
        private String boxOfficeId;
        private Seat seat;
        private int client;

        public Ticket(String show, String boxOfficeId, Seat seat, int client) {
            this.show = show;
            this.boxOfficeId = boxOfficeId;
            this.seat = seat;
            this.client = client;
        }

        public Seat getSeat() {
            return seat;
        }

        public String getShow() {
            return show;
        }

        public String getBoxOfficeId() {
            return boxOfficeId;
        }

        public int getClient() {
            return client;
        }

        @Override
        public String toString() {

            /*
             * Created a way to store print statements to later return as string.
             * I store the current output in a variable, and then I will reset it later
             */
            ByteArrayOutputStream consoleStorage = new ByteArrayOutputStream();
            PrintStream orig = System.out;
            System.setOut(new PrintStream(consoleStorage));

            System.out.println("-------------------------------");
            System.out.print("| Show: " + show);
            printSpaces(31 - (8 + show.length()));
            System.out.print("| Box Office ID: " + boxOfficeId);
            printSpaces(31 - (17 + boxOfficeId.length()));
            System.out.print("| Seat: " + seat);
            printSpaces(31 - (8 + seat.toString().length()));
            System.out.print("| Client: " + client);
            printSpaces(31 - (10 + Integer.toString(client).length()));
            System.out.println("-------------------------------");

            //Put string into dummy variable
            String dummy = consoleStorage.toString();

            //Set the output back to how it is
            System.setOut(orig);

            return dummy;
        }

        //prints the spaces for the Ticket.toString() method
        private static void printSpaces(int num) {
            for (int i = 0; i < num - 1; i++) {
                System.out.print(" ");
            }
            System.out.println("|");
        }
    }

    public Theater(int numRows, int seatsPerRow, String show) {
        this.numRows = numRows;
        this.seatsPerRow = seatsPerRow;
        this.show = show;
        nextBestRow = 1;
        nextBestSeatInRow = 1;
        soldTickets = new ArrayList<>();
        // TODO: Implement this constructor
    }

    /*
     * Calculates the best seat not yet reserved
     *
      * @return the best seat or null if theater is full
   */
    public Seat bestAvailableSeat() {
        return bestAvailableSeatSynchronized();
    }

    //synchronized version of bestAvailableSeat
    private synchronized Seat bestAvailableSeatSynchronized() {
        Seat bestSeat = new Seat(nextBestRow,nextBestSeatInRow);
        nextBestSeatInRow++;
        if (nextBestSeatInRow > seatsPerRow) {
            nextBestSeatInRow = 1;
            nextBestRow++;
        }
        return bestSeat;
    }

    /*
     * Prints a ticket for the client after they reserve a seat
   * Also prints the ticket to the console
     *
   * @param seat a particular seat in the theater
   * @return a ticket or null if a box office failed to reserve the seat
   */
    public Ticket printTicket(String boxOfficeId, Seat seat, int client) {
        return printTicketSynchronized(boxOfficeId, seat, client);
    }

    //Synchronized version of printTicket
    private synchronized Ticket printTicketSynchronized(String boxOfficeId, Seat seat, int client) {
        seat = bestAvailableSeat();
        if (seat.getRowNum() > numRows) {
            return null;
        }
        Ticket newTicket = new Ticket(this.show, boxOfficeId, seat, client);
        for (int i = 0; i < soldTickets.size(); i++)  {

            //if the seat has already been sold, return null
            if (seat.getRowNum() == soldTickets.get(i).getSeat().getRowNum()
                    && seat.getSeatNum() == soldTickets.get(i).getSeat().getSeatNum()) {
                return null;
            }
        }
        soldTickets.add(newTicket);
        System.out.println(newTicket);

        //Wait for 50ms
        try {
            wait(50);
        }
        catch (InterruptedException Ie) {
            Thread.currentThread().interrupt();
            System.out.println("Something went wrong!");
        }
        return newTicket;
    }

    /*
     * Lists all tickets sold for this theater in order of purchase
     *
   * @return list of tickets sold
   */
    public List<Ticket> getTransactionLog() {
        return soldTickets;
    }
}
