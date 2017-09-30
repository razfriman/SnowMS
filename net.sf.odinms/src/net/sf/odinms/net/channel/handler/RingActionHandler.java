/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.net.channel.handler;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Raz
 */

//7D 00 00 08 00 53 68 69 74 46 75 63 6B 01 2E 22 00 => Send

//7D 00 01 Cancel send?

//7D 00 03 84 83 3D 00 => Dropping engagement ring

//[7D 00 ] [02 ] [01 ] [(01 00) 4A] [01 00 00 00] - Engagement
//[HEADER] [ACT] [Y/N] [  SENDER  ] [    CID    ]
public class RingActionHandler extends AbstractMaplePacketHandler{
    private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RingActionHandler.class);
    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        byte mode = slea.readByte();
        System.out.println(slea.toString());
        switch (mode) {
            case 0 : //Send
                String partnerName = slea.readMapleAsciiString();
                if (partnerName.equalsIgnoreCase(c.getPlayer().getName())) {
                    c.getSession().write(net.sf.odinms.tools.MaplePacketCreator.serverNotice(1, "You cannot put your own name in it."));
                    return;
                }
                //c.getSession().write(net.sf.odinms.tools.MaplePacketCreator.serverNotice(1, partnerName));
                MapleCharacter partner = c.getChannelServer().getPlayerStorage().getCharacterByName(partnerName);
                if (partner == null) {
                    c.getSession().write(net.sf.odinms.tools.MaplePacketCreator.serverNotice(1, partnerName + " was not found on this channel. If you are both logged in, please make sure you are in the same channel."));
                    return;
                } if (partner.getGender() == c.getPlayer().getGender()) {
                    c.getSession().write(net.sf.odinms.tools.MaplePacketCreator.serverNotice(1, "Your partner is the same gender as you are."));
                    return;
                } else {
                    //NPCScriptManager.getInstance().start(partner.getClient(), "marriagequestion", 9201002, c.getPlayer());
                }

                break;
            case 1 : //Cancel send
                c.getSession().write(net.sf.odinms.tools.MaplePacketCreator.serverNotice(1, "You have cancelled the request."));
                break;
			case 2: //Reply to engagment
				break;
            case 3 : //Drop Ring
                //int rid = slea.readInt();
                try {
                    //Marriage.divorceEngagement(c.getPlayer().getId(), c.getPlayer().getGender());
                    c.getSession().write(net.sf.odinms.tools.MaplePacketCreator.serverNotice(1, "Your engagement has been broken up."));
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
                break;
		  default :
                log.info("Unhandled Ring Packet : " + slea.toString());
                break;
        }
    }
}
