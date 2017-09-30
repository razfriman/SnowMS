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

package net.sf.odinms.server.maps;

import java.awt.Point;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.anticheat.CheatingOffense;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.scripting.portal.PortalScriptManager;
import net.sf.odinms.server.MaplePortal;
import net.sf.odinms.tools.MaplePacketCreator;

public class MapleGenericPortal implements MaplePortal {

    private String name;
    private String target;
    private Point position;
    private int targetmap;
    private int map;
    private int type;
    private int id;
    private String scriptName;
    private boolean onlyOnce;
    private int locked;

    public MapleGenericPortal(int type) {
        this.type = type;
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Point getPosition() {
        return position;
    }

    @Override
    public String getTarget() {
        return target;
    }

    @Override
    public int getTargetMapId() {
        return targetmap;
    }

    @Override
    public int getMapId() {
        return map;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public boolean isLocked() {
        return locked > 0;
    }

    @Override
    public int getLocked() {
        return locked;
    }

    @Override
    public void setLocked(int locked) {
        this.locked = locked;
    }

    @Override
    public String getScriptName() {
        return scriptName;
    }

    public boolean isOnlyOnce() {
        return onlyOnce;
    }

    public void setOnlyOnce(boolean onlyOnce) {
        this.onlyOnce = onlyOnce;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean canUsePortal(MapleCharacter chr) {
        if (!onlyOnce) {
            return true;
        } else if (!chr.isPortalUsed(this)) {
            return true;
        }
        return false;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public void setTargetMapId(int targetmapid) {
        this.targetmap = targetmapid;
    }

    public void setMapId(int mapid) {
        this.map = mapid;
    }

    @Override
    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    @Override
    public void enterPortal(MapleClient c) {
        MapleCharacter player = c.getPlayer();
        double distanceSq = getPosition().distanceSq(player.getPosition());
        if (distanceSq > 22500) {
            player.getCheatTracker().registerOffense(CheatingOffense.USING_FARAWAY_PORTAL, "D" + Math.sqrt(distanceSq));
        }

        boolean changed = false;
        if (getScriptName() != null && canUsePortal(c.getPlayer()) && !isLocked()) {
            changed = PortalScriptManager.getInstance().executePortalScript(this, c);
            c.getPlayer().addUsedPortal(this);

        } else if (getTargetMapId() != 999999999) {
            MapleMap to;
            if (player.getEventInstance() == null) {
                to = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(getTargetMapId());
            } else {
                to = player.getEventInstance().getMapInstance(getTargetMapId());
            }
            MaplePortal pto = to.getPortal(getTarget());
            if (pto == null) { // fallback for missing portals - no real life case anymore - interesting for not implemented areas
                pto = to.getPortal(0);
            }
            if (!isLocked()) {
                c.getPlayer().changeMap(to, pto); //late resolving makes this harder but prevents us from loading the whole world at once
                changed = true;
            } else {
                c.getSession().write(MaplePacketCreator.blockPortal(getLocked(), true));
            }
        }
        if (!changed) {
            c.getSession().write(MaplePacketCreator.enableActions());
        }
    }
}
