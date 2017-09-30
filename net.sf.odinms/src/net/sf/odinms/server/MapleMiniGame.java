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

import java.util.ArrayList;
import java.util.List;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.IntValueHolder;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.server.maps.AbstractMapleMapObject;
import net.sf.odinms.server.maps.MapleMapObjectType;
import net.sf.odinms.tools.MaplePacketCreator;

/**
 *
 * @author Raz
 */
public class MapleMiniGame extends AbstractMapleMapObject {

    private String description;
    private String password;
    private MapleInteractionType gameType;
    private MapleMiniGamePieceType pieceType;
    private MapleMiniGameResultType resultType;
    private MapleCharacter[] players = new MapleCharacter[2];
    private MapleCharacter owner;
    private MapleCharacter visitor;
    private boolean inProgress;
    boolean withPassword;
    private boolean ready;
    private int matchesToWin;
    
    public MapleMiniGame(MapleCharacter owner, String description, String password) {
		this.owner = owner;
		this.addPlayer(owner);
		this.inProgress = false;
		this.ready = false;
		this.description = description;
		this.password = password;
	}

	public MapleInteractionType getGameType() {
		return gameType;
	}

	public void setGameType(MapleInteractionType gameType) {
		this.gameType = gameType;
	}

	public MapleMiniGameResultType getResultType() {
		return resultType;
	}

	public void setResultType(MapleMiniGameResultType resultType) {
		this.resultType = resultType;
	}

	public MapleMiniGamePieceType getPieceType() {
		return pieceType;
	}

	public void setPieceType(MapleMiniGamePieceType pieceType) {
		this.pieceType = pieceType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isWithPassword() {
		return password != null;
	}

	public MapleCharacter getOwner() {
		return owner;
	}

	public void setOwner(MapleCharacter owner) {
		this.owner = owner;
	}

	public boolean isOwner(MapleCharacter chr) {
		if (owner == null) {
			return false;
		}
		return owner.getId() == chr.getId();
	}

	public boolean isVisitor(MapleCharacter chr) {
		if (visitor == null) {
			return false;
		}
		return visitor.getId() == chr.getId();
	}

	public boolean inGame(MapleCharacter chr) {
		return isOwner(chr) || isVisitor(chr);
	}

	public MapleCharacter[] getPlayers() {
		return players;
	}

	public List<MapleCharacter> getPlayersSafe() {
		List<MapleCharacter> ret = new ArrayList<MapleCharacter>();
		for (MapleCharacter player : players) {
			if (player != null) {
				ret.add(player);
			}
		}
		return ret;
	}

	public MapleCharacter getPlayer(int index) {
		return players[index];
	}

	public void setPlayers(List<MapleCharacter> players) {
		this.players = (MapleCharacter[]) players.toArray();
	}

	public void setPlayers(MapleCharacter[] players) {
		this.players = players;
	}

	public void addPlayer(MapleCharacter player) {
		if (players[0] == null) {
			this.players[0] = player;
			this.owner = player;
		} else if (players[1] == null) {
			this.players[1] = player;
		}
	}

	public int getPlayerCount() {
		int count = 0;
		for (MapleCharacter player : players) {
			if (player != null) {
				count++;
			}
		}
		return count;
	}

	public boolean hasFreeSlot() {
		return getPlayerCount() < 2;
	}

	public boolean isInProgress() {
		return inProgress;
	}

	public void setInProgress(boolean inProgress) {
		this.inProgress = inProgress;
	}

	public void switchInProgress() {
		inProgress = !inProgress;
	}

	public boolean isReady() {
		return ready;
	}

	public void setReady(boolean ready) {
		this.ready = ready;
	}

	public void switchReady() {
		ready = !ready;
	}

	public MapleCharacter getVisitor() {
		return visitor;
	}

	public void setVisitor(MapleCharacter visitor) {
		this.visitor = visitor;
	}

	public int getMatchesToWin() {
		return matchesToWin;
	}

	public void setMatchesToWin(int matchesToWin) {
		this.matchesToWin = matchesToWin;
	}

	public void broadcast(MaplePacket packet) {
		broadcast(null, packet, false);
	}

	public void broadcast(MapleCharacter source, MaplePacket packet, boolean repeatToSource) {
		if (source == null) {
			for (MapleCharacter player : getPlayersSafe()) {
				if (player != null) {
					player.getClient().getSession().write(packet);
				}
			}
		} else {
			for (MapleCharacter player : getPlayersSafe()) {
				if (player != null && player.getId() != source.getId()) {
					player.getClient().getSession().write(packet);
				}
			}
			if (repeatToSource) {
				source.getClient().getSession().write(packet);
			}
		}
	}

	public void chat(MapleCharacter chr, String chat) {
		broadcast(MaplePacketCreator.getPlayerShopChat(chr, chat, isOwner(chr)));
	}

	public void openGame(MapleClient c) {
		c.getSession().write(MaplePacketCreator.getMiniGameEntrance(this, c.getPlayer()));
	}

	@Override
	public void sendDestroyData(MapleClient client) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void sendSpawnData(MapleClient client) {
		throw new UnsupportedOperationException();
	}

	@Override
	public MapleMapObjectType getType() {
		return MapleMapObjectType.INTERACTION;
	}

	public static enum MapleMiniGamePieceType implements IntValueHolder {

		MATCH_CARD_4x3(0),
		MATCH_CARD_5x4(1),
		MATCH_CARD_6x5(2),
		OMOK(0);
		int type;

		private MapleMiniGamePieceType(int type) {
			this.type = type;
		}

		public static MapleMiniGamePieceType getById(int id, MapleInteractionType gameType) {
			for (MapleMiniGamePieceType pieceType : MapleMiniGamePieceType.values()) {
				if (pieceType.name().equals("OMOK") && gameType == MapleInteractionType.OMOK_GAME) {
					if (pieceType.getValue() == id) {
						return pieceType;
					}
				} else if (pieceType.name().startsWith("MATCH") && gameType == MapleInteractionType.MATCH_CARD_GAME) {
					if (pieceType.getValue() == id) {
						return pieceType;
					}
				}
			}
			return null;
		}

		@Override
		public int getValue() {
			return type;
		}
	}

	public static enum MapleMiniGameResultType implements IntValueHolder {

		WIN(0),
		TIE(1),
		FORFIET(2);
		int type;

		private MapleMiniGameResultType(int type) {
			this.type = type;
		}

		public static MapleMiniGameResultType getById(int id, MapleInteractionType gameType) {
			for (MapleMiniGameResultType resultType : MapleMiniGameResultType.values()) {
				if (resultType.getValue() == id) {
					return resultType;
				}
			}
			return null;
		}

		@Override
		public int getValue() {
			return type;
		}
	}
}