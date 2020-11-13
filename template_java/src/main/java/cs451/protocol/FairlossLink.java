package cs451.protocol;

import cs451.DeliveredMessage;
import cs451.Host;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.List;

public class FairlossLink extends UnderlyingProtocol implements Sender {

    private DatagramSocket socket;

    public FairlossLink(String ip, int port) {
        try {
            this.socket = new DatagramSocket(port, InetAddress.getByName(ip));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void send(Integer seq, String dstIp, int dstPort) {

        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.putInt(seq);
        byte[] buf = bb.array();

        try {
            DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getByName(dstIp), dstPort);
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void receive(List<Host> hosts) {
        byte[] buf = new byte[4];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        try {
            socket.receive(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ByteBuffer bb = ByteBuffer.wrap(packet.getData());
        int seq = bb.getInt();
        int senderId = 0;

        for (Host h : hosts) {
            if (h.getIp().equals(packet.getAddress().getHostAddress()) && h.getPort() == packet.getPort()) {
                senderId = h.getId();
            }
        }

        DeliveredMessage m = new DeliveredMessage(seq, senderId);
        listener.deliver(m, packet.getAddress().getHostAddress(), packet.getPort());
    }

}
