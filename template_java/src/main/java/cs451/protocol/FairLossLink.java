package cs451.protocol;

import cs451.Message;
import cs451.Host;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.UUID;

public class FairLossLink extends UnderlyingProtocol implements Sender {

    private DatagramSocket socket;
    private List<Host> hosts;

    public FairLossLink(String ip, int port, List<Host> hosts) {
        try {
            this.socket = new DatagramSocket(port, InetAddress.getByName(ip));
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.hosts = hosts;
    }


    @Override
    public void send(Message m, String dstIp, int dstPort) {

        ByteBuffer bb = ByteBuffer.allocate(24);
        bb.putInt(m.seq).putInt(m.senderId);
        //bb.putLong(m.uuid.getMostSignificantBits());
        //bb.putLong(m.uuid.getLeastSignificantBits());
        byte[] buf = bb.array();

        try {
            DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getByName(dstIp), dstPort);
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void receive() {
        byte[] buf = new byte[24];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        try {
            socket.receive(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ByteBuffer bb = ByteBuffer.wrap(packet.getData());
        int seq = bb.getInt();
        int senderId = bb.getInt();
        //long uuid1 = bb.getLong();
        //long uuid2 = bb.getLong();

        Message m = new Message(seq, senderId);
        //m.setUuid(new UUID(uuid1, uuid2));

        int sourceId = 0;
        for (Host h : hosts) {
            if (h.getIp().equals(packet.getAddress().getHostAddress()) && h.getPort() == packet.getPort()) {
                sourceId = h.getId();
            }
        }

        listener.deliver(m, sourceId);
    }

}
