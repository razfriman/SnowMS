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

package net.sf.odinms.server.life;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.server.MapleShop;
import net.sf.odinms.server.MapleShopFactory;
import net.sf.odinms.server.maps.MapleMapObjectType;
import net.sf.odinms.tools.MaplePacketCreator;

public class MapleNPC extends AbstractLoadedMapleLife {

	private MapleNPCStats stats;
	private boolean custom = false;

	public MapleNPC(int id, MapleNPCStats stats) {
		super(id);
		this.stats = stats;
	}

	public boolean hasShop() {
		return MapleShopFactory.getInstance().getShopForNPC(getId()) != null;
	}

	public void sendShop(MapleClient c) {
		MapleShop shop = MapleShopFactory.getInstance().getShopForNPC(getId());
		shop.sendShop(c);
	}

	@Override
	public void sendSpawnData(MapleClient client) {
		client.getSession().write(MaplePacketCreator.spawnNPC(this, true, true));
	}

	@Override
	public void sendDestroyData(MapleClient client) {
		client.getSession().write(MaplePacketCreator.spawnNPC(this, false, false));
	}

	@Override
	public MapleMapObjectType getType() {
		return MapleMapObjectType.NPC;
	}

	public boolean isCustom() {
		return custom;
	}

	public void setCustom(boolean custom) {
		this.custom = custom;
	}

	public String getName() {
		return stats.getName();
	}

	public MapleNPCStats getStats() {
		return stats;
	}
}
