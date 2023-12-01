package Backtest;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BluetoothConnectionExample {
    public static void main(String[] args) {
        StreamConnection streamConnection = null;
        OutputStream outputStream = null;
        InputStream inputStream = null;

        try {
            // Adresse MAC de l'appareil Bluetooth
            String deviceAddress = "B4CD27956939";

            // Canal ou port de service Bluetooth
            int channel = 1;

            // Construction de l'URL de connexion Bluetooth
            String connectionURL = "btspp://" +deviceAddress+":1";

            // Ouverture de la connexion Bluetooth
            streamConnection = (StreamConnection) Connector.open(connectionURL);
            System.out.println("Connexion Bluetooth établie avec succès !");

            // Obtention des flux d'entrée et de sortie
            outputStream = streamConnection.openOutputStream();
            inputStream = streamConnection.openInputStream();

            // Utilisation de la connexion Bluetooth
            // ...

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Fermeture des flux et de la connexion Bluetooth
            try {
                if (outputStream != null)
                    outputStream.close();
                if (inputStream != null)
                    inputStream.close();
                if (streamConnection != null)
                    streamConnection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
