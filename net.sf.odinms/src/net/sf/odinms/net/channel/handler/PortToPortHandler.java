package net.sf.odinms.net.channel.handler;

import java.awt.Point;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.MaplePortal;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class PortToPortHandler extends AbstractMaplePacketHandler {

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
	  slea.readByte();//type??
	  MaplePortal portal = c.getPlayer().getMap().getPortal(slea.readMapleAsciiString());
	  Point toPos = slea.readPoint();
	  Point fromPos = slea.readPoint();
	  c.getPlayer().setPosition(toPos);
	  c.getPlayer().getMap().movePlayer(c.getPlayer(), toPos);
    }
}
