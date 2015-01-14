/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.ipmi.protocol.packet.ipmi.command;

import java.io.EOFException;
import java.io.File;
import java.nio.ByteBuffer;
import org.anarres.ipmi.protocol.packet.ipmi.session.IpmiContext;
import org.anarres.ipmi.protocol.packet.ipmi.session.IpmiSessionManager;
import org.anarres.ipmi.protocol.packet.rmcp.RmcpPacket;
import org.anarres.ipmi.protocol.packet.rmcp.RspRmcpPacket;
import org.junit.Before;
import org.junit.Test;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.UdpPacket;
import org.pcap4j.packet.namednumber.UdpPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author shevek
 */
public class IpmiPacketTest {

    private static final Logger LOG = LoggerFactory.getLogger(IpmiPacketTest.class);
    private final IpmiContext context = new IpmiSessionManager();

    @Before
    public void setUp() throws Exception {
        UdpPort.register(new UdpPort((short) RmcpPacket.PORT, "RMCP/IPMI"));
        UdpPort.register(new UdpPort((short) RspRmcpPacket.PORT, "RMCP/RSP"));
    }

    @Test
    public void testPackets() throws Exception {
        File file = new File("../src/misc/ipmi-lanplus-enc0.tcpdump");
        PcapHandle handle = Pcaps.openOffline(file.getAbsolutePath());
        try {
            for (;;) {
                Packet packet = handle.getNextPacketEx();
                LOG.info("Read:\n" + packet);
                UdpPacket udpPacket = packet.get(UdpPacket.class);

                byte[] data = udpPacket.getPayload().getRawData();

                RmcpPacket rmcp = new RmcpPacket();
                rmcp.fromWire(context, ByteBuffer.wrap(data));

                LOG.info("Read:\n" + rmcp);

            }
        } catch (EOFException e) {
        }
    }
}
