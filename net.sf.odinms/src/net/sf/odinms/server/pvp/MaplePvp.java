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

import net.sf.odinms.client.MapleCharacter;

/**
 *
 * @author Raz
 */
public class MaplePvp {

	private MaplePvpHandler pvpHandler;
	private MapleCharacter player;
	private MaplePvpGameType gameType;
	private int kills;
	private int deaths;
	private int team;
	private boolean flag;
	private boolean berserk;

	public MaplePvp(MapleCharacter player, MaplePvpGameType gameType, MaplePvpHandler pvpHandler) {
		this.player = player;
		this.gameType = gameType;
		this.kills = 0;
		this.deaths = 0;
		this.team = -1;
		this.flag = false;
		this.berserk = false;
		this.pvpHandler = pvpHandler;
		this.pvpHandler.addPlayer(player);

	}

	public void setPlayer(MapleCharacter player) {
		this.player = player;
	}

	public void setGameType(MaplePvpGameType gameType) {
		this.gameType = gameType;
	}

	public void setKills(int kills) {
		this.kills = kills;
	}

	public void addKill() {
		this.kills += 1;
	}

	public void addKills(int kills) {
		this.kills += kills;
	}

	public void setDeaths(int deaths) {
		this.deaths = deaths;
	}

	public void addDeath() {
		this.deaths += 1;
	}

	public void addDeaths(int deaths) {
		this.deaths += deaths;
	}

	public void setTeam(int team) {
		this.team = team;
	}

	public MapleCharacter getPlayer() {
		return player;
	}

	public MaplePvpGameType getGameType() {
		return gameType;
	}

	public int getKills() {
		return kills;
	}

	public int getDeaths() {
		return deaths;
	}

	public int getTeam() {
		return team;
	}

	public MaplePvpHandler getPvpHandler() {
		return pvpHandler;
	}

	public void setPvpHandler(MaplePvpHandler pvpHandler) {
		this.pvpHandler = pvpHandler;
	}
}