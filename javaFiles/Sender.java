import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;


public class Sender {
    
    public static final int WINDOW_SIZE = 256; // Example window size
    private static final int MAX_RETRIES = 3; // Maximum number of retries for sending a packet
    private static final int TIMEOUT_MS = 2000; // Timeout in milliseconds for waiting for ACK

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java Sender <receiver_ip> <receiver_port> <file_path>");
            return;
        }
        String receiverIp = args[0];
        int receiverPort = Integer.parseInt(args[1]);
        String filePath = args[2];

        try {
            Sender sender = new Sender();
            sender.connectToReceiver(receiverIp, receiverPort);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    private void connectToReceiver(String receiverIp, int receiverPort) throws Exception {
        // Code to connect to the receiver
        InetAddress address = InetAddress.getByName(receiverIp);
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(TIMEOUT_MS);

        System.out.println("Starting sender...");
        System.out.println("Connecting to receiver at " + receiverIp + ":" + receiverPort);

        int sequenceNumber = new Random().nextInt(1000);
        boolean connected = false;

        for(int attempt = 0; attempt < MAX_RETRIES; attempt++) {
           try{
                System.out.println("Attempt " + (attempt + 1) + ": Sending SYN packet...");
                Packet synPacket = new Packet(sequenceNumber, null, true, false, false ,false);
                sendPacket(socket, synPacket, address, receiverPort);

                Packet response = receivePacket(socket);
                if (response != null && response.isAck() && response.getSequenceNumber() == sequenceNumber) {
                    System.out.println("Received ACK for SYN. Connection established.");
                    connected = true;
                    break;
                } else {
                    System.out.println("Did not receive valid ACK. Retrying...");
                }
                Packet synPacket2 = new Packet(sequenceNumber, null, true, false, false ,false);
                sendPacket(socket, synPacket2, address, receiverPort);
              } catch (IOException e) {
                System.out.println("Timeout waiting for ACK. Retrying...");
           }

           if (!connected) {
                System.out.println("Failed to establish connection after " + MAX_RETRIES + " attempts.");
                socket.close();
                return;
            }
            
           }
        }

    private void sendPacket(DatagramSocket socket, Packet packet, InetAddress address, int port) throws IOException {
        // Code to send a packet to the receiver
        byte[] data = packet.toBytes();
        DatagramPacket datagram = new DatagramPacket(data, data.length, address, port);
        socket.send(datagram);
    }

    private Packet receivePacket(DatagramSocket socket) throws IOException {
        byte[] buffer = new byte[Packet.MAX_PACKET_SIZE];
        DatagramPacket datagram = new DatagramPacket(buffer, buffer.length);
        socket.receive(datagram);
        return Packet.fromBytes(datagram.getData(), datagram.getLength());  
    }

}
