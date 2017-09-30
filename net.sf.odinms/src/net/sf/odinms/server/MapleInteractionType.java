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

package net.sf.odinms.server;

import net.sf.odinms.net.IntValueHolder;

/**
 *
 * @author Raz
 */
public enum MapleInteractionType implements IntValueHolder {

	NULL(0),
	OMOK_GAME(1),
	MATCH_CARD_GAME(2),
	TRADE(3),
	PLAYER_SHOP(4);
	private int i;

	MapleInteractionType(int i) {
		this.i = i;
	}

	public static MapleInteractionType getById(int i) {
		for (MapleInteractionType t : MapleInteractionType.values()) {
			if (t.getValue() == i) {
				return t;
			}
		}
		return NULL;
	}

	@Override
	public int getValue() {
		return i;
	}
}
