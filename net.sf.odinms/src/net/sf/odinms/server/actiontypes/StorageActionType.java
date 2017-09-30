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
public enum StorageActionType {

	C_TAKE_OUT(4),
	C_STORE(5),
	C_ARRANGE(6),
	C_MESO(7),
	C_CLOSE(8),
	S_TAKE_OUT(9),
	S_FULL_STORAGE(0x11),
	S_STORE_MESO(0x13),
	S_OPEN_STORAGE(0x16),
	S_STORE_ITEM(0x0D),
	UNDEFINED(-1);
	final byte code;

	private StorageActionType(int code) {
		this.code = (byte) code;
	}

	public byte getCode() {
		return code;
	}

	public static StorageActionType getById(int id) {
		for (StorageActionType act : StorageActionType.values()) {
			if (act.getCode() == id) {
				return act;
			}
		}
		return UNDEFINED;
	}
}