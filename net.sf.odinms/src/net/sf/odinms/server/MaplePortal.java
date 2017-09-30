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

import java.awt.Point;

import net.sf.odinms.client.MapleClient;

public interface MaplePortal {
	public final int MAP_PORTAL = 2;
	public final int DOOR_PORTAL = 6;
	
	int getType();
	int getId();
	Point getPosition();
	String getName();
	String getTarget();
	String getScriptName();
	void setScriptName(String newName);
	int getTargetMapId();
	int getMapId();
	void enterPortal(MapleClient c);
	boolean isLocked();
	int getLocked();
	void setLocked(int locked);
}
