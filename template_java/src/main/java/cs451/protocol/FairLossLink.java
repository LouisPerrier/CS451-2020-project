package cs451.protocol;

import cs451.Message;
import cs451.Host;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.List;

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

        ByteBuffer bb = ByteBuffer.allocate(8);
        bb.putInt(m.seq).putInt(m.senderId);
        byte[] buf = bb.array();

        try {
            DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getByName(dstIp), dstPort);
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void receive() {
        byte[] buf = new byte[8];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        try {
            socket.receive(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ByteBuffer bb = ByteBuffer.wrap(packet.getData());
        int seq = bb.getInt();
        int senderId = bb.getInt();

        Message m = new Message(seq, senderId);

        int sourceId = 0;
        for (Host h : hosts) {
            if (h.getIp().equals(packet.getAddress().getHostAddress()) && h.getPort() == packet.getPort()) {
                sourceId = h.getId();
            }
        }

        listener.deliver(m, sourceId);
    }

}
