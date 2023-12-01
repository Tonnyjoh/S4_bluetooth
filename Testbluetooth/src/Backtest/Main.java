package Backtest;

import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
//import java.util.UUID;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Hello world!");
        
        if (LocalDevice.isPowerOn()) {
            LocalDevice myDevice = LocalDevice.getLocalDevice();
            String adressDevice = myDevice.getBluetoothAddress();
            String nameDevice = myDevice.getFriendlyName();
            System.out.println(" Adresse de mon appareil: " + adressDevice);
            System.out.println(" Nom de mon appareil: " + nameDevice);
            DiscoveryAgent discoAgent = myDevice.getDiscoveryAgent();
            DiscoveryListener discoListener = new discoveryListenerDevice();
            boolean started =discoAgent.startInquiry(DiscoveryAgent.GIAC, discoListener);
            if (started) {
                System.out.println("Inquiry started...");
            } else {
                System.out.println("Failed to start inquiry");
            }
            //appareil bluetooth deja decouvert
            RemoteDevice[] remoteDevices= myDevice.getDiscoveryAgent().retrieveDevices(DiscoveryAgent.CACHED);
            for(RemoteDevice device: remoteDevices){
                System.out.println(" Adresse de l appareil: " + device.getBluetoothAddress());
                System.out.println(" Nom de l appareil: " + device.getFriendlyName(false));

                /*String sppUUID = "0000110100001000800000805F9B34FB";
                UUID serviceUUID = new UUID(sppUUID, false);
                UUID[] uuidArray = new UUID[]{serviceUUID};
                discoAgent.searchServices(null, uuidArray, device, discoListener);*/
            }
            synchronized (discoListener){
                discoListener.wait();
            }
        }
    }
}
//implementation anle listener
class discoveryListenerDevice implements DiscoveryListener {

    @Override
    public void deviceDiscovered(RemoteDevice remoteDevice, DeviceClass deviceClass) {
        String deviceName = null;
        StreamConnection streamConnection = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            deviceName = remoteDevice.getFriendlyName(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //String sppUUID = "00001101-0000-1000-8000-00805F9B34FB";
        String deviceAddress = remoteDevice.getBluetoothAddress();
        // Formater l'adresse MAC avec des séparateurs ":"
        StringBuilder formattedAddress = new StringBuilder();
        for (int i = 0; i < deviceAddress.length(); i += 2) {
            if (i > 0) {
                formattedAddress.append(":");
            }
            formattedAddress.append(deviceAddress.substring(i, i + 2));
        }

        //deviceAddress= formattedAddress.toString();
            if(remoteDevice.isAuthenticated()){
                System.out.println("OK");
            }
            // UUID pour RFCOMM (Service Serial Port Profile)
            //UUID uuid = new UUID("00001101-0000-1000-8000-00805F9B34FB", false);

            // Crée l'URL de connexion RFCOMM
            System.out.println("Appareil découvert : " + deviceName + " [" + deviceAddress + "]");
            //String connectionURL = "btspp://"+deviceAddress+":1";
            String channel="2"; 
            String connectionURL = "btspp://" + deviceAddress + ":" + channel + ";authenticate=false;encrypt=false;master=false";


        try {
            System.out.println("Creation connexion");
            streamConnection = (StreamConnection) Connector.open(connectionURL);
            // Vérification de l'état de la connexion

        } catch (Exception e) {
            e.getMessage();
            e.printStackTrace();
        }
        System.out.println("test connexion");
        if (streamConnection!=null) {
            System.out.println("La connexion Bluetooth est établie !");
        } else {
            System.out.println("Échec de la connexion Bluetooth.");
            return;
        }
        try {
                // Obtention des flux de données d'entrée et de sortie
                inputStream = streamConnection.openInputStream();
                outputStream = streamConnection.openOutputStream();
                // Envoi de données à l'appareil
                String message = "Hello, World!";
                outputStream.write(message.getBytes());
                outputStream.flush();
                System.out.println("Données envoyées : " + message);

                // Réception de données de l'appareil
                byte[] buffer = new byte[1024];
                int bytesRead = inputStream.read(buffer);
                String receivedData = new String(buffer, 0, bytesRead);
                System.out.println("Données reçues : " + receivedData);

            }catch (Exception e){
                e.printStackTrace();
            }finally {
                // Fermeture des flux et de la connexion
                try {
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    streamConnection.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    @Override
    public void servicesDiscovered(int i, ServiceRecord[] servRecord) {
/*
        for (ServiceRecord record : servRecord) {
            String serviceName = record.getAttributeValue(0x0100).getValue().toString(); // Nom du service
            String serviceURL = record.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false); // URL de connexion

            System.out.println("Service découvert : " + serviceName);
            System.out.println("URL de connexion : " + serviceURL);
        }*/
    }

    @Override
    public void serviceSearchCompleted(int i, int i1) {

    }

    @Override
    public void inquiryCompleted(int i) {
        //vita le recherche
        synchronized (this){
            this.notify();
        }
    }
}