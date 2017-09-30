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
package net.sf.odinms.server.actiontypes;

/**
 *
 * @author Raz
 */
public enum PlayerInteractionType {

	CREATE(0),
	//1
	INVITE(2),
	DECLINE(3),
	VISIT(4),
	//5 - Enter Omok Game
	CHAT(6),
	//7
	//8
	//9
	EXIT(0xA),
	OPEN(0xB),
	//C
	//D
	SET_ITEMS(0xE),
	SET_MESO(0xF),
	CONFIRM(0x10),
	//11
	//12
	ADD_ITEM(0x13),
	BUY(0x14),
	//15
	//16
	//17
	REMOVE_ITEM(0x18), //slot(byte) bundlecount(short)

	REQUEST_TIE(0x2C),
	ANSWER_TIE(0x2D),
	GIVE_UP(0x2E),
	EXIT_AFTER_GAME(0x32),
	CANCEL_EXIT(0x33),
	READY(0x34),
	NOT_READY(0x35),
	START(0x37),
	SKIP(0x39),
	MOVE_OMOK(0x3A),
	SELECT_CARD(0x3E),
	UNDEFINED(-1);
	final byte code;

	private PlayerInteractionType(int code) {
		this.code = (byte) code;
	}

	public byte getCode() {
		return code;
	}

	public static PlayerInteractionType getById(int id) {
		for (PlayerInteractionType act : PlayerInteractionType.values()) {
			if (act.getCode() == id) {
				return act;
			}
		}
		return UNDEFINED;
	}
}