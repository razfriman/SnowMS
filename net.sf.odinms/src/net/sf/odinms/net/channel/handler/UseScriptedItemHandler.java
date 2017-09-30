/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.net.channel.handler;

import net.sf.odinms.client.IItem;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.anticheat.CheatingOffense;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Raz
 */
public class UseScriptedItemHandler extends AbstractMaplePacketHandler {

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
	  MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
	  c.getPlayer().getCheatTracker().inspectActionTime(slea.readInt(), 200);
	  byte slot = (byte) slea.readShort();
	  int itemId = slea.readInt();
	  IItem item = c.getPlayer().getInventory(ii.getInventoryType(itemId)).getItem(slot);

	  if (item == null || item.getItemId() != itemId || item.getQuantity() <= 0) {
		c.getPlayer().getCheatTracker().registerOffense(CheatingOffense.PACKET_EDIT);
		c.getSession().write(MaplePacketCreator.enableActions());
		return;
	  }
	  ii.getItemEffect(itemId).applyTo(c.getPlayer());
    }
}
