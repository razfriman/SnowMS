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

package net.sf.odinms.exttools.wzinfo;

/**
 *
 * @author Raz
 */
public enum WzInfoType {

    UNDEFINED(-1),
    ALL(0),
    ITEM(1),
    JOB(2),
    MAP(3),
    MOB(4),
    NPC(5),
    PET(6),
    PORTAL_SCRIPT(7),
    SKILL(8),
	COMPARE_ALL(9);
    
    final byte type;
    
    private WzInfoType(int type) {
	this.type = (byte)type;
    }

    public byte getType() {
	return type;
    }
	
    public short getBitfieldEncoding() {
	return (short) (2 << type);
    }

    public static WzInfoType getByType(byte type) {
	for (WzInfoType l : WzInfoType.values()) {
	    if (l.getType() == type) {
		return l;
	    }
	}
	return null;
    }

    public static WzInfoType getByWZName(String name) {
	/*if (name.equals("Install")) return SETUP;
	else if (name.equals("Consume")) return USE;
	else if (name.equals("Etc")) return ETC;
	else if (name.equals("Cash")) return CASH;
	else if (name.equals("Pet")) return CASH;*/
	return UNDEFINED;
    }
}
