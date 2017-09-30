/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc> 
                       Matthias Butz <matze@odinms.de>
                       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation. You may not use, modify
    or distribute this program under any other version of the
    GNU Affero General Public License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package net.sf.odinms.net.channel.handler;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.scripting.npc.NPCConversationManager;
import net.sf.odinms.scripting.npc.NPCConversationManager.NPCDialogType;
import net.sf.odinms.scripting.npc.NPCEvent;
import net.sf.odinms.scripting.npc.NPCScriptManager;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Matze
 */
public class NPCMoreTalkHandler extends AbstractMaplePacketHandler {

    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        NPCConversationManager cm = NPCScriptManager.getInstance().getCM(c);

        byte lastMsg = slea.readByte();
        byte action = slea.readByte();
        String text = cm.getText();
        cm.resetText();
        NPCDialogType dialog = NPCDialogType.getByValue(lastMsg);


        if (dialog == NPCDialogType.NORMAL) {
            if (action == 0) {//PREV
                if (c.getPlayer().getNpcScriptInfo().getState() == 0) {
                    return;
                }
                c.getPlayer().getNpcScriptInfo().setState(c.getPlayer().getNpcScriptInfo().getState() - 1);
                cm.sendDialog(c.getPlayer().getNpcScriptInfo().getPreviousStates().get(c.getPlayer().getNpcScriptInfo().getState()));
            } else if (action == 1) {//NEXT
                c.getPlayer().getNpcScriptInfo().setState(c.getPlayer().getNpcScriptInfo().getState() + 1);
                if (c.getPlayer().getNpcScriptInfo().getState() < c.getPlayer().getNpcScriptInfo().getPreviousStates().size()) {
                    // Usage of "next" button after the "back" button
                    cm.sendDialog(c.getPlayer().getNpcScriptInfo().getPreviousStates().get(c.getPlayer().getNpcScriptInfo().getState()));
                } else {
                    NPCEvent event = new NPCEvent(c, action, lastMsg, 0, null, text, false, false);
                    performAction(cm, event);
                }
            } else {
                cm.dispose();
            }
        } else if (dialog == NPCDialogType.YES_NO || dialog == NPCDialogType.ACCEPT_DECLINE || dialog == NPCDialogType.ACCEPT_DECLINE_NO_EXIT) {
            if (action == 0) {
                performAction(cm, new NPCEvent(c, action, lastMsg, 0, null, text, false, false));
            } else if (action == 1) {
                performAction(cm, new NPCEvent(c, action, lastMsg, 1, null, text, false, false));
            } else {
                cm.dispose();
            }
        } else if (dialog == NPCDialogType.GET_TEXT) {
            if (action != 0) {
                performAction(cm, new NPCEvent(c, action, lastMsg, -1, slea.readMapleAsciiString(), text, false, false));
            } else {
                cm.dispose();
            }
        } else if (dialog == NPCDialogType.GET_NUMBER) {
            if (action == 1) {
                performAction(cm, new NPCEvent(c, action, lastMsg, slea.readInt(), null, text, false, false));
            } else {
                cm.dispose();
            }
        } else if (dialog == NPCDialogType.SIMPLE) {
            if (action != 0) {
                performAction(cm, new NPCEvent(c, action, lastMsg, slea.readInt(), null, text, false, false));
            } else {
                cm.dispose();
            }
        } else if (dialog == NPCDialogType.QUIZ) {
            if (action != 0) {
                performAction(cm, new NPCEvent(c, action, lastMsg, slea.readInt(), null, text, false, false));
            } else {
                cm.dispose();
            }
        } else if (dialog == NPCDialogType.STYLE) {
            if (action != 0) {
                performAction(cm, new NPCEvent(c, action, lastMsg, slea.readInt(), null, text, false, false));
            } else {
                cm.dispose();
            }
        } else {
            cm.dispose();
        }
    }

    private void performAction(NPCConversationManager cm, NPCEvent event) {
        cm.getPlayer().getNpcScriptInfo().getNpcEventQueue().add(event);
    }
}
