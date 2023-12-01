package Backtest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.bluetooth.BluetoothConnectionException;
import javax.bluetooth.DataElement;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

public class Main2 {
    public static void main(String[] args) throws IOException, InterruptedException,BluetoothConnectionException {
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
}

class MyDiscoveryListener implements DiscoveryListener {
    @Override
    public void deviceDiscovered(RemoteDevice remoteDevice, DeviceClass deviceClass) {
        try {
            String deviceName = remoteDevice.getFriendlyName(false);
            String deviceAddress = remoteDevice.getBluetoothAddress();

            System.out.println("Device Discovered: " + deviceName + " [" + deviceAddress + "]");  
            if(deviceAddress.equals("0022060193E0"))
            	connectToDevice(remoteDevice);
            
            
       
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
                System.out.println("service not  found");
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
        int channel=1;
		String connectionURL = "btspp://" + deviceAddress + ":" + channel + ";authenticate=false;encrypt=false;master=false";

		StreamConnection streamConnection = null;
		InputStream inputStream = null;
		OutputStream outputStream = null;

		try {
		    // Établir la connexion Bluetooth
			System.out.println("Try to establish connection");
		    streamConnection = (StreamConnection) Connector.open(connectionURL);
		    inputStream = streamConnection.openInputStream();
		    outputStream = streamConnection.openOutputStream();

		    System.out.println("Bluetooth Connection Established!");

		    // Envoi de données
		    String message = "Hello, Bluetooth World!";
		    outputStream.write(message.getBytes());
		    outputStream.flush();

		    System.out.println("Data Sent: " + message);

		    // Réception de données
		    byte[] buffer = new byte[1024];
		    int bytesRead = inputStream.read(buffer);
		    String receivedData = new String(buffer, 0, bytesRead);
		    System.out.println("Data Received: " + receivedData);
		}	
		catch (BluetoothConnectionException e) {
			System.out.println("Bluetooth Connection Exception: " + e.getMessage());
			e.printStackTrace();
		}
		catch (IOException e) {
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
		        if (streamConnection != null) {
		            streamConnection.close();
		        }
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}
    }
}
