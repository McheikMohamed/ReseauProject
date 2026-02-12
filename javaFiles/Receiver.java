import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Receiver {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java Receiver <port> <output_file_path>");
            return;
        }else
        {
            int port = Integer.parseInt(args[0]);
            String outputFilePath = args[1];

            try {
                Receiver receiver = new Receiver();
                receiver.connectToSender(port, outputFilePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
    }

    private void connectToSender(int port, String outputFilePath) throws Exception {
        DatagramSocket socket = new DatagramSocket(port);
        System.out.println("Receiver is listening on port " + port);
        byte buffer[] = new byte[Packet.MAX_PACKET_SIZE];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        while (true) {
            socket.receive(packet);
            Packet receivedPacket = Packet.fromBytes(packet.getData(), packet.getLength());
            System.out.println("Received packet with sequence number: " + receivedPacket.getSequenceNumber());
            // Here you would handle the packet, send ACKs, and write data to the output file
            //Check if it's a SYN packet and respond with an SYN+ACK
            if (receivedPacket.isSyn()) {               
                System.out.println("Received SYN " + receivedPacket.getSequenceNumber() + ". Sending SYN+ACK...");
                Packet synAckPacket = new Packet(receivedPacket.getSequenceNumber(), null, true, true, false, false);
                sendPacket(socket, synAckPacket, packet.getAddress(), packet.getPort());
            }

            socket.receive(packet);
            Packet ackPacket = Packet.fromBytes(packet.getData(), packet.getLength());
            if (ackPacket.isAck()){
                System.out.println("Received ACK " + ackPacket.getSequenceNumber() + " for SYN+ACK " + receivedPacket.getSequenceNumber() + ". Connection established.");
            }
        }

    }

    private void sendPacket(DatagramSocket socket, Packet packet, java.net.InetAddress address, int port) throws IOException {
        byte[] data = packet.toBytes();
        DatagramPacket datagram = new DatagramPacket(data, data.length, address, port);
        socket.send(datagram);
    }
    
}
