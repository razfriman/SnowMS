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

import java.util.regex.Pattern;

public class MapleCharacterUtil {
	private static Pattern namePattern = Pattern.compile("[a-zA-Z0-9_-]{3,12}");
	
	private MapleCharacterUtil() {
		// whoosh
	}

        public static boolean isValidChar(byte classType, byte gender, int face, int hair, int hairColor, int skinColor, int top, int bottom, int shoes, int weapon) {
            switch (classType) {
                case 0:
                case 1:
                case 2:
                    //TODO set valid items here
                    return true;
            }
            return true;
        }
	
	public static int canCreateChar(String name, int world) {
		if (!isNameLegal(name)) {
			return 3;
		}
		if (MapleCharacter.getIdByName(name, world) != -1) {
			return 1;
		}
		return 0;
	}
	
	public static boolean isNameLegal (String name) {
		if (name.length() <= 3 || name.length() > 12) // || whatever.nameExists(name)
		{
			return false;
		}
		
		return namePattern.matcher(name).matches();
	}

	public static String makeMapleReadable(String in) {
		String wui = in.replace('I', 'i');
		wui = wui.replace('l', 'L');
		wui = wui.replace("rn", "Rn");
		wui = wui.replace("vv", "Vv");
		wui = wui.replace("VV", "Vv");
		return wui;
	}
}
