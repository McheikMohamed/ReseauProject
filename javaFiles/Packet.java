public class Packet {
    
    // Taille de l'en-tête : 2 (taille) + 2 (seq) + 1 (flags) = 5 octets
    public static final int HEADER_SIZE = 5;
    public static final int MAX_PACKET_SIZE = 1024;
    public static final int MAX_DATA_SIZE = MAX_PACKET_SIZE - HEADER_SIZE;
    
    // Masques pour les flags
    public static final int SYN_FLAG = 0b1000;
    public static final int ACK_FLAG = 0b0100;
    public static final int FIN_FLAG = 0b0010;
    public static final int RST_FLAG = 0b0001;
    
    private int sequenceNumber;
    private byte[] data;
    private boolean syn;
    private boolean ack;
    private boolean fin;
    private boolean rst;
    
    public Packet(int sequenceNumber, byte[] data, boolean syn, boolean ack, boolean fin, boolean rst) {
        this.sequenceNumber = sequenceNumber;
        this.data = data;
        this.syn = syn;
        this.ack = ack;
        this.fin = fin;
        this.rst = rst;
    }
    
    // Convertit le paquet en tableau d'octets pour l'envoi
    public byte[] toBytes() {
        int dataLength = (data != null) ? data.length : 0;
        byte[] buffer = new byte[HEADER_SIZE + dataLength];
        
        // Taille totale des données - 16 bits (2 octets)
        buffer[0] = (byte) ((dataLength >> 8) & 0xFF);
        buffer[1] = (byte) (dataLength & 0xFF);
        
        // Numéro de séquence - 16 bits (2 octets)
        buffer[2] = (byte) ((sequenceNumber >> 8) & 0xFF);
        buffer[3] = (byte) (sequenceNumber & 0xFF);
        
        // Flags - 1 octet
        int flags = 0;
        if (syn) flags |= SYN_FLAG;
        if (ack) flags |= ACK_FLAG;
        if (fin) flags |= FIN_FLAG;
        if (rst) flags |= RST_FLAG;
        buffer[4] = (byte) flags;
        
        // Données
        if (data != null && dataLength > 0) {
            System.arraycopy(data, 0, buffer, HEADER_SIZE, dataLength);
        }
        
        return buffer;
    }
    
    // Construit un paquet à partir d'un tableau d'octets reçu
    public static Packet fromBytes(byte[] buffer, int length) {
        if (length < HEADER_SIZE) {
            return null;
        }
        
        // Taille totale des données - 16 bits
        int dataLength = ((buffer[0] & 0xFF) << 8) | (buffer[1] & 0xFF);
        
        // Numéro de séquence - 16 bits
        int seqNum = ((buffer[2] & 0xFF) << 8) | (buffer[3] & 0xFF);
        
        // Flags
        int flags = buffer[4] & 0xFF;
        boolean syn = (flags & SYN_FLAG) != 0;
        boolean ack = (flags & ACK_FLAG) != 0;
        boolean fin = (flags & FIN_FLAG) != 0;
        boolean rst = (flags & RST_FLAG) != 0;
        
        // Données
        byte[] data = null;
        if (dataLength > 0 && length >= HEADER_SIZE + dataLength) {
            data = new byte[dataLength];
            System.arraycopy(buffer, HEADER_SIZE, data, 0, dataLength);
        }
        
        return new Packet(seqNum, data, syn, ack, fin, rst);
    }
    
    // Getters
    public int getSequenceNumber() {
        return sequenceNumber;
    }
    
    public byte[] getData() {
        return data;
    }
    
    public boolean isSyn() {
        return syn;
    }
    
    public boolean isAck() {
        return ack;
    }
    
    public boolean isFin() {
        return fin;
    }
    
    public boolean isRst() {
        return rst;
    }
    
    // Setters
    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }
    
    public void setData(byte[] data) {
        this.data = data;
    }
    
    public void setSyn(boolean syn) {
        this.syn = syn;
    }
    
    public void setAck(boolean ack) {
        this.ack = ack;
    }
    
    public void setFin(boolean fin) {
        this.fin = fin;
    }
    
    public void setRst(boolean rst) {
        this.rst = rst;
    }
}
