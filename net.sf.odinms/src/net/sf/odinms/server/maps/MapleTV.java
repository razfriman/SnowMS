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

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.net.world.remote.WorldChannelInterface;
import net.sf.odinms.server.TimerManager;
import net.sf.odinms.tools.MaplePacketCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Raz
 */
public class MapleTV {

	private static Logger log = LoggerFactory.getLogger(MapleTV.class);
	private final static MapleTV instance = new MapleTV();
	private Queue<MapleTVMessage> messages = new LinkedList<MapleTVMessage>();

	private MapleTV() {
	}

	public static MapleTV getInstance() {
		return instance;
	}

	public void addMessage(MapleCharacter user, MapleCharacter partner, int type, List<String> message, int ticks) {
		MapleTVMessage tvMessage = new MapleTVMessage(user, partner, type, message, ticks);
		messages.add(tvMessage);
		if (messages.size() == 1) {
			broadcastTV(true);
		}
	}

	public boolean hasMessage() {
		return !messages.isEmpty();
	}

	public MapleTVMessage getCurrentMessage() {
		return messages.peek();
	}

	private void scheduleCancel() {
		getCurrentMessage().setStartTime(System.currentTimeMillis());
		TimerManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				broadcastTV(false);
				messages.remove();
				if (hasMessage()) {
					broadcastTV(true);
				}
			}
		}, getCurrentMessage().getDelay() * 1000);
	}

	public void broadcastTV(boolean start) {
		WorldChannelInterface wci = getCurrentMessage().user.getClient().getChannelServer().getWorldInterface();
		try {
			if (start) {
				wci.broadcastMessage(null, getCurrentMessage().getPacket().getBytes());
				scheduleCancel();
			} else {
				wci.broadcastMessage(null, MaplePacketCreator.cancelTvSmega().getBytes());
			}

		} catch (RemoteException re) {
			log.error("Remote Exception Broadcasting TV", re);
			getCurrentMessage().user.getClient().getChannelServer().reconnectWorld();
		}
	}

	public class MapleTVMessage {

		private MapleCharacter user;
		private MapleCharacter partner;
		private int type;
		private List<String> message = new LinkedList<String>();
		private int ticks;
		private int delay;
		private long startTime;

		public MapleTVMessage(MapleCharacter user, MapleCharacter partner, int type, List<String> message, int ticks) {
			this.user = user;
			this.partner = partner;
			this.type = type;
			this.message = message;
			this.ticks = ticks;
			calculateDelay();
		}

		public List<String> getMessage() {
			return message;
		}

		public MaplePacket getPacket() {
			return MaplePacketCreator.sendTV(user, message, type, partner, getTimeRemaining() >= getDelay() * 1000 ? (int) getTimeRemaining() / 1000 : getDelay());
		}

		public long getTimeRemaining() {
			return System.currentTimeMillis() - startTime;
		}

		public MapleCharacter getPartner() {
			return partner;
		}

		public int getTicks() {
			return ticks;
		}

		public int getType() {
			return type;
		}

		public MapleCharacter getUser() {
			return user;
		}

		public int getDelay() {
			return delay;
		}

		public void setStartTime(long startTime) {
			this.startTime = startTime;
		}

		public long getStartTime() {
			return startTime;
		}

		public void calculateDelay() {
			switch (type) {
				case 0:
				case 3:
					delay = 15;
					break;
				case 1:
				case 4:
					delay = 30;
					break;
				case 2:
				case 5:
					delay = 60;
			}
		}
	}
}
