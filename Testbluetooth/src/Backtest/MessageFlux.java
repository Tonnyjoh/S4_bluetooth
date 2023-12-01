package Backtest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.StreamConnection;
public class MessageFlux implements Runnable {
    private StreamConnection connection;
    private String messageToSend;

    private InputStream inputStream = null;
    private OutputStream outputStream = null; // Initialize it here

    public MessageFlux(StreamConnection connection, String message) {
        this.connection = connection;
        System.out.println(outputStream);
        this.messageToSend = message;
        try {
            if (outputStream == null) {
                outputStream = connection.openOutputStream(); // Initialize outputStream if it's null
            }
        } catch (IOException e) {
            System.out.println("Failed to open output stream: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            if (outputStream == null) {
                System.out.println("Output stream is not open.");
                return; // Exit the method if the output stream is not open
            }

            System.out.println("Bluetooth Connection Established!");

            // Envoi de données
            String message = messageToSend;
            outputStream.write(message.getBytes());
            outputStream.flush();

            System.out.println("Data Sent: " + message);
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Fermeture des flux et de la connexion
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
