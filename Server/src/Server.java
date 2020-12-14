//////////////////////////////////////////////////
/*
422C Final Coding Project
Bidding service with client and server

Server handles logins, back-end logic to check for valid bids and error handling

Created by: Nik Srinivas
Date: 11/30/20
Class ID:16160

Contact: niksrinivas23@gmail.com // (972)-816-8424
 */




import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;



import  com.google.gson.Gson;

public class Server {
    private ArrayList<PrintWriter> clientOutputStreams;
    static Integer totalClients = 0;
    public static Item[] auctions;
    public static ArrayList<User> userDatabase = new ArrayList<User>();
    public static Boolean userExistsFlag = false;
    private static String spike = "";
    private static String mac = "";
    private static String five = "";
    private static String corvette = "";
    private static String rocket = "";

    // Launches server
    public static void main(String[] args) {
        populateItems();
        try {
            new Server().setUpNetworking();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Creates list of items from JSON file
    static void populateItems() {
        Gson gson = new Gson();
        try {
            Reader reader = new FileReader("items.json");
            auctions = gson.fromJson(reader, Item[].class);

        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
        }
        for (int i = 0; i < auctions.length; i++) {
            auctions[i].history = new ArrayList<String>();
        }
    }

    // Creates ability to accept incoming threads as new sockets
    private void setUpNetworking() throws Exception {
        clientOutputStreams = new ArrayList<PrintWriter>();
        System.out.println("Server is waiting for new connections");
        @SuppressWarnings("resource")
        ServerSocket serverSock = new ServerSocket(2399);
        while (true) {
            Socket clientSocket = serverSock.accept();
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
            clientOutputStreams.add(writer);
            System.out.println(clientOutputStreams.size());

            Thread t = new Thread(new ClientHandler(clientSocket));
            t.start();
            totalClients++;
            System.out.println("Got new connection - total clients: " + totalClients);

            // Init Text
            writer.println(" ");
            writer.println("biddingUpdate DIRECTIONS:");
            writer.println("biddingUpdate Bid on item by clicking item in left column and entering price below in xx.x'");

            writer.println("biddingUpdate ");
            writer.println("biddingUpdate Current Items Available:");

            for (Item item : auctions) {
                if (item.sold == false) {
                    writer.println("biddingUpdate " + item.name);
                    writer.println("biddingUpdate " + "                       =>  current price: $" + item.bidPrice);
                    writer.println("biddingUpdate " + "                       =>  buy now price: $" + item.buyPrice);
                    notifyClients("biddingUpdate ");
                    writer.flush();
                }

            }
        }
    }

    // Same thing as writing to a client, but sends message to all clients simultaneously - primarily used for bulk update
    private void notifyClients(String message) {

        for (PrintWriter writer : clientOutputStreams) {
            writer.println(message);
            writer.flush();
        }
    }

    // Runnable method is most important, handles all inputs from each client live
    class ClientHandler implements Runnable {
        private BufferedReader reader;
        private PrintWriter writer;


        public ClientHandler(Socket clientSocket) throws IOException {
            Socket sock = clientSocket;
            reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            writer = new PrintWriter(sock.getOutputStream());
        }

        public void run() {
            String message;
            try {
                while ((message = reader.readLine()) != null) {


                    ///////////////////////////////////////////////////////////////////////
                    //////////                LOGIN LOGIC             /////////////////////
                    ///////////////////////////////////////////////////////////////////////


                    String[] array = message.split(" ", 3);
//                    System.out.println(array[0]);
//                    System.out.println(array[1]);
//                    System.out.println(array[2]);
                    if (!array[0].isEmpty() && !array[0].isEmpty() && !array[0].isEmpty()) {
                        if (array[0].equals("signin")) {
                            User newLogin = new User(array[1], array[2]);
                            for (User user : userDatabase) {
                                if (user.username.equals(newLogin.username)) {
                                    if (user.encryptedPassword.equals(newLogin.encryptedPassword)) {
                                        writer.println("login Success!");
                                        writer.flush();
                                        userExistsFlag = true;
                                    }
                                }
                            }

                            if (userExistsFlag == false) {
                                writer.println("login Username and password NOT found!");
                                writer.flush();
                            }
                            userExistsFlag = false;
                        }

                        if (array[0].equals("newuser")) {
                            User newLogin = new User(array[1], array[2]);
                            for (User user : userDatabase) {
                                if (user.username.equals(newLogin.username)) {
                                    writer.println("login User already exists!");
                                    writer.flush();
                                    userExistsFlag = true;
                                }
                            }
                            if (userExistsFlag == false) {
                                userDatabase.add(newLogin);
                                writer.println("login User Created! Please login now");
                                writer.flush();

                            }
                            userExistsFlag = false;
                        }


                        ///////////////////////////////////////////////////////////////////////
                        //////////                BIDDING LOGIC             ///////////////////
                        ///////////////////////////////////////////////////////////////////////


                        // update bidding summary
                        if (message.toLowerCase().contains("biddingUpdate")) {
                            notifyClients("clear ");
                            writer.println("biddingUpdate ");
                            writer.println("biddingUpdate Price Summary:");
                            writer.flush();

                            notifyClients("biddingUpdate Current Items Available:");
                            notifyClients("biddingUpdate ");
                            for (Item item : auctions) {
                                if (item.sold == false) {
                                    notifyClients("biddingUpdate " + item.name);
                                    notifyClients("biddingUpdate " + "                       =>  current price: $" + item.bidPrice);
                                    notifyClients("biddingUpdate " + "                       =>  buy now price: $" + item.buyPrice);
                                    notifyClients("biddingUpdate ");
                                }
                            }
                        }

                        // Valid bids here
                        if (message.toLowerCase().contains("spike") || message.toLowerCase().contains("mac") || message.toLowerCase().contains("five") || message.toLowerCase().contains("corvette") || message.toLowerCase().contains("rocket")) {
                            for (Item item : auctions) {
                                if (item.messName.contains(array[0])) {
                                    Double bid = Double.parseDouble(array[2]);
                                    if (bid > item.bidPrice && bid < item.buyPrice) {
                                        notifyClients(item.messName + " ");
                                        notifyClients(item.messName + " " + array[1] + " bid on " + item.name + " for $" + array[2]);
                                        item.highestBidder = array[1];
                                        item.bidPrice = bid;
                                        notifyClients("clear ");
                                        notifyClients("biddingUpdate Current Items Available:");
                                        notifyClients("biddingUpdate ");
                                        notifyClients("updates " + array[1] + " bid on " + item.name + " for $" + array[2]);
                                        for (Item itemz : auctions) {
                                            notifyClients("biddingUpdate " + itemz.name);
                                            notifyClients("biddingUpdate " + "                       =>  current price: $" + itemz.bidPrice);
                                            notifyClients("biddingUpdate " + "                       =>  buy now price: $" + itemz.buyPrice); }
                                            notifyClients("biddingUpdate ");
                                    }
                                    else if (bid >= item.buyPrice) {
                                        item.highestBidder = array[1];
                                        notifyClients("clear ");
                                        notifyClients(item.messName + " " + item.name + " was sold for $" + bid + " to " + item.highestBidder);
                                        notifyClients("updates " + item.name + " was sold for $" + bid + " to " + item.highestBidder);
                                        item.sold = true;
                                        notifyClients("clear ");
                                        notifyClients("biddingUpdate Current Items Available:");
                                        notifyClients("biddingUpdate ");
                                        for (Item itemz : auctions) {
                                            if (itemz.sold == false) {
                                                notifyClients("biddingUpdate " + itemz.name);
                                                notifyClients("biddingUpdate " + "                       =>  current price: $" + itemz.bidPrice);
                                                notifyClients("biddingUpdate " + "                       =>  buy now price: $" + itemz.buyPrice);
                                                notifyClients("biddingUpdate ");
                                                }
                                        }
                                    } else {
                                        writer.println("error ");
                                        writer.println("error Enter a higher bid for " + item.name);
                                        writer.flush();
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
