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

package net.sf.odinms.client;

import net.sf.odinms.net.IntValueHolder;

/**
 *
 * @author Raz
 */
public enum MapleGender implements IntValueHolder {
    NULL(-1),
    MALE(0),
    FEMALE(1),
    ;
    private int i;
    
    MapleGender(int i) {
	this.i = i;
    }
    
    public static MapleGender getById(int i) {
	for(MapleGender g : MapleGender.values()) {
	    if(g.getValue() == i)
		return g;
	}
	return NULL;
    }
    
    @Override
    public int getValue() {
	return i;
    }
}
