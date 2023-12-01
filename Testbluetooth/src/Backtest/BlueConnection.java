package Backtest;

import java.io.IOException;
import java.io.OutputStream;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DataElement;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

public class BlueConnection {
    private StreamConnection streamConnection;
    private OutputStream outputStream;
    
    public BlueConnection() throws BluetoothStateException, InterruptedException {
        System.out.println("Go amzayyyy");
        
        if (LocalDevice.isPowerOn()) {
            LocalDevice myDevice = LocalDevice.getLocalDevice();
            String deviceAddress = myDevice.getBluetoothAddress();
            String deviceName = myDevice.getFriendlyName();
            System.out.println("Ty nenah");
            System.out.println("Device Address: " + deviceAddress);
            System.out.println("Device Name: " + deviceName);

            DiscoveryAgent discoveryAgent = myDevice.getDiscoveryAgent();
            DiscoveryListener discoveryListener = new MyDiscoveryListener();

            boolean started = discoveryAgent.startInquiry(DiscoveryAgent.GIAC, discoveryListener);

            if (started) {
                System.out.println("Inquiry started...");
            } else {
                System.out.println("Failed to start inquiry");
            }

            synchronized (discoveryListener) {
                discoveryListener.wait();
            }
        }
    }
    
    public void sendMess(String message) {
    	
        if (streamConnection != null) {
            try {
                if (outputStream == null) {
                    outputStream = streamConnection.openOutputStream();
                }

                // Envoi de données
                String messageToSend = message;
                outputStream.write(messageToSend.getBytes());
                outputStream.flush();

                System.out.println("Data Sent: " + messageToSend);
            } catch (IOException e) {
                System.out.println("IO Exception: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Bluetooth connection is not established. Cannot send message.");
        }
    }

    class MyDiscoveryListener implements DiscoveryListener {
        @Override
        public void deviceDiscovered(RemoteDevice remoteDevice, DeviceClass deviceClass) {
            try {
                String deviceName = remoteDevice.getFriendlyName(false);
                String deviceAddress = remoteDevice.getBluetoothAddress();

                System.out.println("Device Discovered: " + deviceName + " [" + deviceAddress + "]");
                if (deviceAddress.equals("48FDA3154AAD")) {//le redmi note7
                    connectToDevice(remoteDevice);
                }
            } catch (IOException e) {
                System.out.println("Erreur");
                e.printStackTrace();
            }
        }

        @Override
        public void servicesDiscovered(int transID, ServiceRecord[] serviceRecords) {
            for (ServiceRecord record : serviceRecords) {
                DataElement serviceName = record.getAttributeValue(0x0100); // Nom du service
                String serviceUUID = record.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false); // UUID du service
                if (serviceName != null) {
                    System.out.println("Service Discovered: " + serviceName + " [" + serviceUUID + "]");
                } else {
                    System.out.println("service not found");
                }
            }
        }

        @Override
        public void serviceSearchCompleted(int transID, int respCode) {
            if (respCode == DiscoveryListener.SERVICE_SEARCH_COMPLETED) {
                System.out.println("Service search completed.");
            } else {
                System.out.println("Service search failed.");
            }
        }

        @Override
        public void inquiryCompleted(int discType) {
            synchronized (this) {
                this.notify();
            }
        }

        private void connectToDevice(RemoteDevice remoteDevice) throws IOException {
            String deviceAddress = remoteDevice.getBluetoothAddress();
            int channel = 2;
            String connectionURL = "btspp://" + deviceAddress + ":" + channel + ";authenticate=false;encrypt=false;master=false";

            // Établir la connexion Bluetooth
            System.out.println("Try to establish connection");
            streamConnection = (StreamConnection) Connector.open(connectionURL);
            if(streamConnection != null) {
            	System.out.println("Connected to device: "+remoteDevice.getFriendlyName(false));
            }
        }
    }
}
