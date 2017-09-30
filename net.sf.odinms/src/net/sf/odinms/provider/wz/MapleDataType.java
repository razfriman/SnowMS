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

package net.sf.odinms.provider.wz;

import net.sf.odinms.net.IntValueHolder;

public enum MapleDataType implements IntValueHolder{
	NONE,
	IMG_0x00(0),
	SHORT(2),
	INT(3),
	FLOAT(4),
	DOUBLE(5),
	STRING(8),
	EXTENDED(9),
	PROPERTY,
	CANVAS,
	VECTOR,
	CONVEX,
	SOUND,
	UOL,
	UNKNOWN_TYPE,
	UNKNOWN_EXTENDED_TYPE;
	
	private int type = -1;
	
	MapleDataType(int type) {
	    this.type = type;
	}
	
	MapleDataType() {
	    
	}
	
	@Override
	public int getValue() {
	    return type;
	}
	
	public String getWzName() {
	    switch(this) {
		case PROPERTY:
		    return "Property";
		case CANVAS:
		    return "Canvas";
		case VECTOR:
		    return "Shape2D#Vector2D";
		case CONVEX:
		    return "Shape2D#Convex2D";
		case SOUND:
		    return "Sound_DX8";
		case UOL:
		    return "UOL";
		default:
			return null;
	    }
	}
}