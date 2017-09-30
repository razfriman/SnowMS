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

package net.sf.odinms.scripting.npc;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.scripting.npc.NPCConversationManager.NPCStateInfo;

/**
 *
 * @author Raz
 */
public class NPCScriptInfo {

    private Queue<NPCEvent> npcEventQueue = new LinkedList<NPCEvent>();
    private List<NPCStateInfo> previousStates = new ArrayList<NPCStateInfo>();
    private int npc;
    private String script;
    private NPCEvent lastEvent = null;
    private String text;
    private int state;
    private MapleClient client;

    public NPCScriptInfo(int npc, String script, MapleClient client) {
        this.npc = npc;
        this.client = client;
        this.state = 0;
    }

    public NPCEvent getLastEvent() {
        return lastEvent;
    }

    public void setLastEvent(NPCEvent lastEvent) {
        this.lastEvent = lastEvent;
    }

    public int getNpc() {
        return npc;
    }

    public void setNpc(int npc) {
        this.npc = npc;
    }

    public String getScript() {
	  return script;
    }

    public void setScript(String script) {
	  this.script = script;
    }

    public List<NPCStateInfo> getPreviousStates() {
        return previousStates;
    }

    public void setPreviousStates(List<NPCStateInfo> previousStates) {
        this.previousStates = previousStates;
    }

    public void addPreviousState(NPCStateInfo previousState) {
        previousStates.add(previousState);
    }

    public Queue<NPCEvent> getNpcEventQueue() {
        return npcEventQueue;
    }

    public void setNpcEventQueue(Queue<NPCEvent> npcEventQueue) {
        this.npcEventQueue = npcEventQueue;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public MapleClient getClient() {
        return client;
    }

    public void setClient(MapleClient client) {
        this.client = client;
    }
}
