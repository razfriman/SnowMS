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
public enum MapleDueyAction {

	SEND_ITEM(0x02),
	CLOSE_DUEY(0x07),
	RECEIVED_PACKAGE_MSG(0x1B),
	CLAIM_RECEIVED_PACKAGE(0x04),
	SUCCESSFULLY_RECEIVED(0x17),
	SUCCESSFULLY_SENT(0x18),
	ERROR_SENDING(0x12),
	OPEN_DUEY(0x08);
	final byte code;

	private MapleDueyAction(int code) {
		this.code = (byte) code;
	}

	public byte getCode() {
		return code;
	}

	public static MapleDueyAction getById(int id) {
		for (MapleDueyAction a : MapleDueyAction.values()) {
			if (a.getCode() == id) {
				return a;
			}
		}
		return null;
	}
}