/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.ipmi.protocol.packet.ipmi;

import java.nio.ByteBuffer;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import org.anarres.ipmi.protocol.packet.common.AbstractWireable;
import org.anarres.ipmi.protocol.packet.common.Code;
import org.anarres.ipmi.protocol.packet.ipmi.payload.IpmiPayload;
import org.anarres.ipmi.protocol.packet.ipmi.session.IpmiSession;
import org.anarres.ipmi.protocol.packet.rmcp.RmcpData;
import org.anarres.ipmi.protocol.packet.rmcp.RmcpMessageClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [IPMI2] Section 13.6, page 132, table 13-8.
 *
 * Hands the entire show off to the {@link IpmiSessionWrapper} for encoding.
 *
 * @author shevek
 */
public class IpmiSessionData extends AbstractWireable implements RmcpData {

    private static final Logger LOG = LoggerFactory.getLogger(IpmiSessionData.class);
    private IpmiSession ipmiSession;
    private IpmiSessionWrapper ipmiSessionWrapper;
    private Integer ipmiSessionSequenceNumber;
    private IpmiPayload ipmiPayload;

    @Override
    public RmcpMessageClass getMessageClass() {
        return RmcpMessageClass.IPMI;
    }

    @CheckForNull
    public IpmiSession getIpmiSession() {
        return ipmiSession;
    }

    public void setIpmiSession(@CheckForNull IpmiSession ipmiSession) {
        this.ipmiSession = ipmiSession;
    }

    @Nonnull
    public IpmiSessionWrapper getIpmiSessionWrapper() {
        return ipmiSessionWrapper;
    }

    public void setIpmiSessionWrapper(IpmiSessionWrapper ipmiSessionWrapper) {
        this.ipmiSessionWrapper = ipmiSessionWrapper;
    }

    @Nonnull
    public IpmiPayload getIpmiPayload() {
        return ipmiPayload;
    }

    public void setIpmiPayload(@Nonnull IpmiPayload ipmiPayload) {
        this.ipmiPayload = ipmiPayload;
    }

    @Override
    public int getWireLength() {
        return getIpmiSessionWrapper().getWireLength(getIpmiSession(), getIpmiPayload());
    }

    @Override
    protected void toWireUnchecked(ByteBuffer buffer) {
        getIpmiSessionWrapper().toWire(buffer, getIpmiSession(), getIpmiPayload());
    }

    @Override
    protected void fromWireUnchecked(ByteBuffer buffer) {
        byte authenticationTypeByte = buffer.get(buffer.position());
        IpmiSessionAuthenticationType authenticationType = Code.fromByte(IpmiSessionAuthenticationType.class, authenticationTypeByte);
        // LOG.info("IpmiSessionAuthenticationType is " + authenticationType);
        if (authenticationType == IpmiSessionAuthenticationType.RMCPP)
            ipmiSessionWrapper = new Ipmi20SessionWrapper();
        else
            ipmiSessionWrapper = new Ipmi15SessionWrapper();
        getIpmiSessionWrapper().fromWire(buffer, null, this);
    }

    @Override
    public void toStringBuilder(StringBuilder buf, int depth) {
        appendHeader(buf, depth, "IpmiSessionData");
        depth++;
        appendValue(buf, depth, "Session", getIpmiSession());
        appendValue(buf, depth, "SessionWrapper", getIpmiSessionWrapper());
        appendChild(buf, depth, "IpmiPayload", getIpmiPayload());
    }
}
