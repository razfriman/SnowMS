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

import net.sf.odinms.client.MapleClient;

/**
 *
 * @author Raz
 */
public class NPCEvent {

	private MapleClient client;
	private byte mode;
	private byte type;
	private int selection;
	private String returnText;
    private String text;
    private boolean prev;
    private boolean next;

    public NPCEvent(MapleClient client, byte mode, byte type, int selection, String returnText, String text, boolean prev, boolean next) {
        this.client = client;
        this.mode = mode;
        this.type = type;
        this.selection = selection;
        this.returnText = returnText;
        this.text = text;
        this.prev = prev;
        this.next = next;
    }

    public MapleClient getClient() {
        return client;
    }

    public byte getMode() {
        return mode;
    }

    public boolean isNext() {
        return next;
    }

    public boolean isPrev() {
        return prev;
    }

    public String getReturnText() {
        return returnText;
    }

    public int getSelection() {
        return selection;
    }

    public String getText() {
        return text;
    }

    public byte getType() {
        return type;
    }
}
