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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Matze
 */
public class MapleNPCStats {

    private String name;
    private String function;
    private String script;
    private int trunkPut;
    private int guildRank;
    private List<Integer> maps = new ArrayList<Integer>();

    public MapleNPCStats() {
    }

    public MapleNPCStats(String name) {
	  this.name = name;
    }

    public MapleNPCStats(String name, String function) {
	  this.name = name;
	  this.function = function;
    }

    public String getFunction() {
	  return function;
    }

    public void setFunction(String function) {
	  this.function = function;
    }

    public String getName() {
	  return name;
    }

    public void setName(String name) {
	  this.name = name;
    }

    public List<Integer> getMaps() {
	  return maps;
    }

    public void setMaps(List<Integer> maps) {
	  this.maps = maps;
    }

    public boolean addMap(Integer e) {
	  return maps.add(e);
    }

    public String getScript() {
	  return script;
    }

    public void setScript(String script) {
	  this.script = script;
    }

    public int getTrunkPut() {
	  return trunkPut;
    }

    public void setTrunkPut(int trunkPut) {
	  this.trunkPut = trunkPut;
    }

    public int getGuildRank() {
	  return guildRank;
    }

    public void setGuildRank(int guildRank) {
	  this.guildRank = guildRank;
    }
}
