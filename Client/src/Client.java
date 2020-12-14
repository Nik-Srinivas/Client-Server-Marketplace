//////////////////////////////////////////////////
/*
422C Final Coding Project
Bidding service with client and server

Client handles GUI creation, including logic for buttons and placing bids/recieving notifications

Created by: Nik Srinivas
Date: 11/30/20
Class ID:16160

Contact: niksrinivas23@gmail.com // (972)-816-8424
 */



import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;

public class Client {
    private static JTextArea incoming;
    private static JTextField outgoing;
    private static BufferedReader reader;
    private static PrintWriter writer;
    private static JLabel description = new JLabel();
    private static JList list;
    private static String biddingUpdate = "";
    private static String userHistory = "";
    private static String spike = "";
    private static String mac = "";
    private static String five = "";
    private static String corvette = "";
    private static String rocket = "";
    private static String Username = "";
    private static JLabel errorMessage = new JLabel("");
    private static JLabel updates = new JLabel("Notification will appear here");


    public static void run(String username) throws Exception {
        setUpNetworking();
        initView(username);
    }



    // Client jFX creation, using a swing version of a borderframe to get 5 distinct areas on my stage
    private static void initView(String username) {
        JFrame frame = new JFrame(username + "'s Bidding Client");
        frame.add( new JFXPanel());
        String musicFile = "clientMusic.mp3";

        Media sound = new Media(new File(musicFile).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();

        Username = username;
        JPanel mainPanel = new JPanel();
        incoming = new JTextArea(23, 50);
        incoming.setLineWrap(true);
        incoming.setWrapStyleWord(true);
        incoming.setEditable(false);
        JScrollPane qScroller = new JScrollPane(incoming);
        qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        outgoing = new JTextField(20);
        outgoing.setName("$");
        JButton sendButton = new JButton("Submit new bid");
        sendButton.addActionListener(new SendButtonListener());

        JPanel sidePanel = new JPanel();
        String month[]= { "Market Bidding Summary", "User Bidding Summary", "SpikeBall Set", "MacBook Pro with M1 Chip",
                "Five Dollar Bill", "Corvette Z1 Wide Body", "Rocket League Black Market Decal"};
        list = new JList(month);
        list.addListSelectionListener(new ListListener());

        JPanel sidePanel2 = new JPanel();
        description.setText("Summary of current bid prices and buy now values");
        description.setForeground(Color.black);

        JPanel titlePanel = new JPanel();
        JLabel title = new JLabel();
        title.setText("EBAY 422c Version");
        title.setForeground(Color.black);
        title.setFont(new Font("Verdana", Font.BOLD, 50));
        titlePanel.add(title);
        titlePanel.setBackground(Color.lightGray);

        JPanel notifPanel = new JPanel();
        updates.setForeground(Color.ORANGE);
        updates.setFont(new Font("Verdana", Font.BOLD, 15));
        notifPanel.add(updates);
        notifPanel.setBackground(Color.darkGray);

        description.setFont(new Font("Verdana", Font.ITALIC, 15));


        sidePanel.add(list);
        sidePanel.setBackground(Color.DARK_GRAY);

        sidePanel2.add(description);
        sidePanel2.setBackground(Color.lightGray);

        mainPanel.add(qScroller);
        mainPanel.add(outgoing);
        mainPanel.add(sendButton);
        mainPanel.setBackground(Color.GRAY);
        mainPanel.add(errorMessage);


        frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
        frame.getContentPane().add(BorderLayout.WEST, sidePanel);
        frame.getContentPane().add(BorderLayout.SOUTH, sidePanel2);
        frame.getContentPane().add(BorderLayout.NORTH, titlePanel);
        frame.getContentPane().add(BorderLayout.EAST, notifPanel);
        frame.setSize(1200, 550);


        frame.setVisible(true);

    }

    // Access to backend
    private static void setUpNetworking() throws Exception {
        @SuppressWarnings("resource")
        Socket sock = new Socket("127.0.0.1", 2399);
        InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
        reader = new BufferedReader(streamReader);
        writer = new PrintWriter(sock.getOutputStream());
        System.out.println("networking established");
        Thread readerThread = new Thread(new IncomingReader());
        readerThread.start();
    }

    // Button checks for bids placed by client and sends it to backend,
    // also keeps track of their personal bid history
    static class SendButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            if (!outgoing.getText().isEmpty()) {


                errorMessage.setText("");
                if (list.getSelectedIndex() == 2){
                userHistory = userHistory + "New bid for Spikeball Set for $" + outgoing.getText() + "\n";
                writer.println("spike " + Username + " " + outgoing.getText());
                writer.flush();
                outgoing.setText("");
                outgoing.requestFocus();
                }
                if (list.getSelectedIndex() == 3){
                    userHistory = userHistory + "New bid for M1 MacBook for $" + outgoing.getText() + "\n";
                    writer.println("mac " + Username + " " + outgoing.getText());
                    writer.flush();
                    outgoing.setText("");
                    outgoing.requestFocus();
                }
                if (list.getSelectedIndex() == 4){
                    userHistory = userHistory + "New bid for $5 bill for $" + outgoing.getText() + "\n";
                    writer.println("five " + Username + " " + outgoing.getText());
                    writer.flush();
                    outgoing.setText("");
                    outgoing.requestFocus();
                }
                if (list.getSelectedIndex() == 5){
                    userHistory = userHistory + "New bid for Corvette for $" + outgoing.getText() + "\n";
                    writer.println("corvette " + Username + " " + outgoing.getText());
                    writer.flush();
                    outgoing.setText("");
                    outgoing.requestFocus();
                }
                if (list.getSelectedIndex() == 6){
                    userHistory = userHistory + "New bid for Rocket League decal for $" + outgoing.getText() + "\n";
                    writer.println("rocket " + Username + " " + outgoing.getText());
                    writer.flush();
                    outgoing.setText("");
                    outgoing.requestFocus();
                }
            }
            else {
                errorMessage.setText("Please enter a valid amount");
            }
        }
    }

    // This list is important for changing the view they see in the text area,
    // clear text area and replaces it every time they switch tabs
    static class ListListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (list.getSelectedIndex() == 0){

                writer.println("biddingUpdate ");
                writer.flush();
                outgoing.setText("");
                outgoing.requestFocus();

                description.setText("Summary of current bid prices and buy now values");
                incoming.selectAll();
                incoming.replaceSelection("");
                incoming.append(biddingUpdate);
            }
            if (list.getSelectedIndex() == 1){
                description.setText("Summary of user's bid history");
                incoming.selectAll();
                incoming.replaceSelection("");
                incoming.append(userHistory);
            }
            if (list.getSelectedIndex() == 2){
                description.setText("Competitive yard game");
                incoming.selectAll();
                incoming.replaceSelection("");
                incoming.append(spike);
            }
            if (list.getSelectedIndex() == 3){
                description.setText("A laptop with an incredible chip blowing AMD and Intel out of the water");
                incoming.selectAll();
                incoming.replaceSelection("");
                incoming.append(mac);
            }
            if (list.getSelectedIndex() == 4){
                description.setText("5 US Dollars, standard tendy");
                incoming.selectAll();
                incoming.replaceSelection("");
                incoming.append(five);
            }
            if (list.getSelectedIndex() == 5){
                description.setText("The most thiqq corvette there is");
                incoming.selectAll();
                incoming.replaceSelection("");
                incoming.append(corvette);
            }
            if (list.getSelectedIndex() == 6){
                description.setText("Increases game skill x100 with new camo");
                incoming.selectAll();
                incoming.replaceSelection("");
                incoming.append(rocket);
            }
        }
    }

    // Runnable handles sending the right flags depending on what tab the user has selected
    static class IncomingReader implements Runnable {

        public void run() {
            String message;
            try {
                while ((message = reader.readLine()) != null) {

                    String[] array = message.split(" ", 2);

                    if (array[0].equals("clear")){
                        biddingUpdate = "";
                        incoming.selectAll();
                        incoming.replaceSelection("");
                    }
                    if (array[0].equals("updates")){
                        updates.setText(array[1]);
                        try {
                            String musicFile = "notificationNoise.mp3";

                            Media sound = new Media(new File(musicFile).toURI().toString());
                            MediaPlayer mediaPlayer = new MediaPlayer(sound);
                            mediaPlayer.play();
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    if (array[0].equals("biddingUpdate")){
                        biddingUpdate = biddingUpdate + array[1] + "\n";
                    }
                    if (array[0].equals("spike")){
                        spike = spike + array[1] + "\n";
                    }
                    if (array[0].equals("mac")){
                        mac = mac + array[1] + "\n";
                    }
                    if (array[0].equals("five")){
                        five = five + array[1] + "\n";
                    }
                    if (array[0].equals("corvette")){
                        corvette = corvette + array[1] + "\n";
                    }
                    if (array[0].equals("rocket")){
                        rocket = rocket + array[1] + "\n";
                    }
                    if (array[0].equals("error")){
                        errorMessage.setText(array[1]);
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
