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

package net.sf.odinms.server.pvp;

/**
 *
 * @author Raz
 */
public enum MaplePvpGameType {

	UNDEFINED(0),
	TEAM_DEATHMATCH(1),
	CAPTURE_THE_FLAG(2),
	FFA_DEATHMATCH(3),
	ASSAULT(4),
	ONE_VS_ONE(5),
	KING_OF_THE_HILL(6);
	final byte type;

	private MaplePvpGameType(int type) {
		this.type = (byte) type;
	}

	public byte getType() {
		return type;
	}

	public short getBitfieldEncoding() {
		return (short) (2 << type);
	}

	public static MaplePvpGameType getByType(byte type) {
		for (MaplePvpGameType l : MaplePvpGameType.values()) {
			if (l.getType() == type) {
				return l;
			}
		}
		return null;
	}
}