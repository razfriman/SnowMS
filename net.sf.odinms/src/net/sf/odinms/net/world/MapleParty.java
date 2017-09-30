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

package net.sf.odinms.net.world;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.server.maps.MapleMap;

public class MapleParty implements Serializable {

	private static final long serialVersionUID = 9179541993413738569L;
	private MaplePartyCharacter leader;
	private List<MaplePartyCharacter> members = new LinkedList<MaplePartyCharacter>();
	private int id;

	public MapleParty(int id, MaplePartyCharacter chrfor) {
		this.leader = chrfor;
		this.members.add(this.leader);
		this.id = id;
	}

	public boolean containsMembers(MaplePartyCharacter member) {
		return members.contains(member);
	}

	public void addMember(MaplePartyCharacter member) {
		members.add(member);
	}

	public void removeMember(MaplePartyCharacter member) {
		members.remove(member);
	}

	public void updateMember(MaplePartyCharacter member) {
		for (int i = 0; i < members.size(); i++) {
			MaplePartyCharacter chr = members.get(i);
			if (chr.equals(member)) {
				members.set(i, member);
			}
		}
	}

	public void silentPartyUpdate() {
		for (MaplePartyCharacter pchr : members) {
			MapleCharacter chr = pchr.getChar();
			chr.silentPartyUpdate();
		}
	}

	public MaplePartyCharacter getMemberById(int id) {
		for (MaplePartyCharacter chr : members) {
			if (chr.getId() == id) {
				return chr;
			}
		}
		return null;
	}

	public Collection<MaplePartyCharacter> getMembers() {
		return Collections.unmodifiableList(members);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void WarpMembers(int mapid, MapleClient c) {
		ChannelServer cserv = c.getChannelServer();
		MapleMap target = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(mapid);

		for (MaplePartyCharacter chr : members) {
			MapleCharacter PMember = cserv.getPlayerStorage().getCharacterByName(chr.getName());
			PMember.changeMap(target, target.getPortal(0));
		}

	}

	public MaplePartyCharacter getLeader() {
		return leader;
	}

	public void setLeader(MaplePartyCharacter leader) {
		this.leader = leader;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final MapleParty other = (MapleParty) obj;
		if (id != other.id) {
			return false;
		}
		return true;
	}

	public void listParty() {
		for (MaplePartyCharacter mpc : this.getMembers()) {
			System.out.println("Character Name: " + mpc.getName());
			System.out.println("Character CID: " + mpc.getId());
			System.out.println("Character Channel: " + mpc.getChannel());
			System.out.println("Character MapID: " + mpc.getMapid());
			System.out.println();
		}
	}
}

