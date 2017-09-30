/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.net.channel.handler;

import java.util.ArrayList;
import java.util.List;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleStat;
import net.sf.odinms.client.anticheat.CheatingOffense;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.Pair;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Raz
 */
public class AutoDistributeAPHandler extends AbstractMaplePacketHandler {

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        c.getPlayer().getCheatTracker().inspectActionTime(slea.readInt(), 500);
        int amount = slea.readInt();
        int maxAP = c.getChannelServer().getMaxAP();
        List<Pair<MapleStat, Integer>> statupdate = new ArrayList<Pair<MapleStat, Integer>>(amount);

        for (int i = 0; i < amount; i++) {
            int type = slea.readInt();
            int value = slea.readInt();
            MapleStat stat = MapleStat.getByValue(type);

            if (value < 0 || c.getPlayer().getRemainingAp() < value) {
                c.getPlayer().getCheatTracker().registerOffense(CheatingOffense.PACKET_EDIT);
                c.getSession().write(MaplePacketCreator.enableActions());
                return;
            }

            switch (stat) {
                case STR:
                case DEX:
                case INT:
                case LUK:
                    if (c.getPlayer().getStat(stat) >= maxAP) {
                        break;
                    }
                    c.getPlayer().setStat(stat, c.getPlayer().getStat(stat) + value);
                    statupdate.add(new Pair<MapleStat, Integer>(stat, c.getPlayer().getStat(stat)));
                    break;
            }
            c.getPlayer().setRemainingAp(c.getPlayer().getRemainingAp() - value);
        }
        statupdate.add(new Pair<MapleStat, Integer>(MapleStat.AVAILABLEAP, c.getPlayer().getRemainingAp()));
        c.getSession().write(MaplePacketCreator.updatePlayerStats(statupdate, true));
    }
}
