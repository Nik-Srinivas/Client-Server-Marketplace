//////////////////////////////////////////////////
/*
422C Final Coding Project
Bidding service with client and server

Client extension class that creates login GUI, provides password encryption for safe storage in back-end

Created by: Nik Srinivas
Date: 11/30/20
Class ID:16160

Contact: niksrinivas23@gmail.com // (972)-816-8424
 */







import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import static javafx.geometry.HPos.RIGHT;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.*;
import java.net.Socket;
import java.util.Base64;



public class Login extends Application {

    private static Text actiontarget = new Text();
    private static String ClientName = null;
    private static BufferedReader reader;
    private static PrintWriter writer;

    // Login stage creation
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Bidder Marketplace Login");
        primaryStage.setWidth(400);
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text scenetitle = new Text("Bidder");
        scenetitle.setId("welcome-text");
        grid.add(scenetitle, 1, 0, 2, 1);

        Label userName = new Label("Username:");
        grid.add(userName, 0, 1);

        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        Label pw = new Label("Password:");
        grid.add(pw, 0, 2);

        PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 2);

        Button sign_in = new Button("Sign in");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(sign_in);
        grid.add(hbBtn, 1, 4);

        Button new_user = new Button("Create New User");
        HBox hBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_LEFT);
        hbBtn.getChildren().add(new_user);
        grid.add(hBtn, 4, 4);

        grid.add(actiontarget, 0, 6);
        grid.setColumnSpan(actiontarget, 2);
        grid.setHalignment(actiontarget, RIGHT);
        actiontarget.setId("actiontarget");




        // SIGN - IN Attempt
        sign_in.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                Base64.Encoder encoder = Base64.getEncoder();
                ClientName = userTextField.getText();
                writer.println("signin" + " " + userTextField.getText() + " " + encoder.encodeToString(pwBox.toString().getBytes()));
                writer.flush();
                actiontarget.setText("");

                String musicFile = "login.mp3";

                Media sound = new Media(new File(musicFile).toURI().toString());
                MediaPlayer mediaPlayer = new MediaPlayer(sound);
                mediaPlayer.play();
            }
        });


        // NEW USER Creation
        new_user.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                Base64.Encoder encoder = Base64.getEncoder();
                writer.println("newuser" + " " + userTextField.getText() + " " + encoder.encodeToString(pwBox.toString().getBytes()));
                writer.flush();
                actiontarget.setText("");
            }
        });



        Scene scene = new Scene(grid, 300, 275);
        primaryStage.setScene(scene);
        scene.getStylesheets().add(Login.class.getResource("Login.css").toExternalForm());
        primaryStage.show();
    }


    //Thread Creation, each login box must be its own thread since it makes
    // unique calls to the backend to verify password
    private static void setUpNetworking() throws Exception {
        @SuppressWarnings("resource")
        Socket sock = new Socket("127.0.0.1", 2399);
        InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
        reader = new BufferedReader(streamReader);
        writer = new PrintWriter(sock.getOutputStream());
        System.out.println("networking established");
        Thread readerThread = new Thread(new Login.IncomingReader());
        readerThread.start();
    }

    // Runnable to recieve backend input regarding the validity of their login attempt
    static class IncomingReader implements Runnable {
        public void run() {
            String message;
            try {
                while ((message = reader.readLine()) != null) {
                    String[] array = message.split(" ", 2);
                    if (array[0].equals("login")){
                        actiontarget.setText(array[1]);
                        if (array[1].contains("Success!")){
                            try {
                                Client.run(ClientName);
                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }
                        }
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
       public static void main(String[] args) throws Exception {
        setUpNetworking();
        launch(args);
    }

}