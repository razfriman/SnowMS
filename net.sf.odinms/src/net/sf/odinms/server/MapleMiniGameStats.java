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

import net.sf.odinms.client.MapleCharacter;

/**
 *
 * @author Raz
 */
public class MapleMiniGameStats {

	private int wins_O = 0;
	private int ties_O = 0;
	private int losses_O = 0;
	private int wins_MC = 0;
	private int ties_MC = 0;
	private int losses_MC = 0;
	private int points = 2000;

	public MapleMiniGameStats() {
	}

	public MapleMiniGameStats(int wins_O, int ties_O, int losses_O, int wins_MC, int ties_MC, int losses_MC, int points) {
		this.wins_O = wins_O;
		this.ties_O = ties_O;
		this.losses_O = losses_O;
		this.wins_MC = wins_MC;
		this.ties_MC = ties_MC;
		this.losses_MC = losses_MC;
		this.points = points;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public int getTies(MapleInteractionType gameType) {
		if (gameType == MapleInteractionType.OMOK_GAME) {
			return ties_O;
		} else if (gameType == MapleInteractionType.MATCH_CARD_GAME) {
			return ties_MC;
		} else {
			return 0;
		}
	}

	public void setLosses(int losses, MapleInteractionType gameType) {
		if (gameType == MapleInteractionType.OMOK_GAME) {
			this.losses_O = losses;
		} else if (gameType == MapleInteractionType.MATCH_CARD_GAME) {
			this.losses_MC = losses;
		}
	}

	public int getLosses(MapleInteractionType gameType) {
		if (gameType == MapleInteractionType.OMOK_GAME) {
			return losses_O;
		} else if (gameType == MapleInteractionType.MATCH_CARD_GAME) {
			return losses_MC;
		} else {
			return 0;
		}
	}

	public void setTies(int ties, MapleInteractionType gameType) {
		if (gameType == MapleInteractionType.OMOK_GAME) {
			this.ties_O = ties;
		} else if (gameType == MapleInteractionType.MATCH_CARD_GAME) {
			this.ties_MC = ties;
		}
	}

	public int getWins(MapleInteractionType gameType) {
		if (gameType == MapleInteractionType.OMOK_GAME) {
			return wins_O;
		} else if (gameType == MapleInteractionType.MATCH_CARD_GAME) {
			return wins_MC;
		} else {
			return 0;
		}
	}

	public void setWins(int wins, MapleInteractionType gameType) {
		if (gameType == MapleInteractionType.OMOK_GAME) {
			this.wins_O = wins;
		} else if (gameType == MapleInteractionType.MATCH_CARD_GAME) {
			this.wins_MC = wins;
		}
	}

	public void saveToDb(MapleCharacter chr) {
	}

	public static MapleMiniGameStats loadFromDb(MapleCharacter chr) {
		return null;
	}
}
