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
package net.sf.odinms.scripting.reactor;

import java.awt.Point;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.sf.odinms.client.Equip;
import net.sf.odinms.client.IItem;
import net.sf.odinms.client.Item;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleInventoryType;
import net.sf.odinms.client.MapleQuestStatus.Status;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.scripting.AbstractPlayerInteraction;
import net.sf.odinms.server.DropEntry;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.life.MapleLifeFactory;
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.server.maps.MapleReactor;

/**
 * @author Lerk
 */
public class ReactorActionManager extends AbstractPlayerInteraction {
	// private static final Logger log = LoggerFactory.getLogger(ReactorActionManager.class);

	private MapleReactor reactor;

	public ReactorActionManager(MapleClient c, MapleReactor reactor) {
		super(c);
		this.reactor = reactor;
	}

	public MapleReactor getReactor() {
		return reactor;
	}

	public void dropMeso(int mesoChance, int minMeso, int maxMeso) {
		if (Math.random() < (1 / (double) mesoChance)) {
			int range = maxMeso - minMeso;
			int displayDrop = (int) (Math.random() * range) + minMeso;
			int mesoDrop = (int) (displayDrop * ChannelServer.getInstance(getClient().getChannel()).getExpRate());
			reactor.getMap().spawnMesoDrop(mesoDrop, displayDrop, reactor.getPosition(), reactor, getPlayer(), true);
		}
	}

	// only used for meso = false, really. No minItems because meso is used to fill the gap
	public void dropItems() {
		dropItems(false, 0, 0, 0, 0, true);
	}

	public void dropItems(boolean meso, int mesoChance, int minMeso, int maxMeso) {
		dropItems(meso, mesoChance, minMeso, maxMeso, 0, true);
	}

	public void dropItems(boolean meso, int mesoChange, int minMeso, int maxMeso, int minItems) {
		dropItems(meso, mesoChange, minMeso, maxMeso, minItems, true);
	}

	public void dropItems(boolean meso) {
		dropItems(meso, 0, 0, 0, 0, true);
	}

	public void dropItemsExpire(boolean expire) {
		dropItems(false, 0, 0, 0, 0, expire);
	}

	public void dropItems(boolean meso, int mesoChance, int minMeso, int maxMeso, int minItems, boolean expire) {
		List<DropEntry> chances = getDropChances();
		List<DropEntry> items = new LinkedList<DropEntry>();
		int numItems = 0;

		if (meso && Math.random() < (1 / (double) mesoChance)) {
			items.add(new DropEntry(0, 0, mesoChance, 1));
		}

		// narrow list down by chances
		Iterator<DropEntry> iter = chances.iterator();
		// for (DropEntry d : chances){
		while (iter.hasNext()) {
			DropEntry d = (DropEntry) iter.next();
			if (Math.random() < (1 / (double) d.getChance())) {
				if (d.getQuestId() > 0 && getQuestStatus(d.getQuestId()) != Status.STARTED) {
					continue;
				} else {
					numItems++;
					items.add(d);
				}
			}
		}

		// if a minimum number of drops is required, add meso
		while (items.size() < minItems) {
			items.add(new DropEntry(0, 0, mesoChance, 1));
			numItems++;
		}

		// randomize drop order
		java.util.Collections.shuffle(items);

		final Point dropPos = reactor.getPosition();

		dropPos.x -= (12 * numItems);

		for (DropEntry d : items) {
			if (d.getItemId() == 0) {
				int range = maxMeso - minMeso;
				int displayDrop = (int) (Math.random() * range) + minMeso;
				int mesoDrop = (int) (displayDrop * ChannelServer.getInstance(getClient().getChannel()).getMesoRate());
				reactor.getMap().spawnMesoDrop(mesoDrop, displayDrop, dropPos, reactor, getPlayer(), true);
			} else {
				IItem drop;
				MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
				if (ii.getInventoryType(d.getItemId()) != MapleInventoryType.EQUIP) {
					drop = new Item(d.getItemId(), (byte) 0, (short) 1);
				} else {
					drop = ii.randomizeStats((Equip) ii.getEquipById(d.getItemId()));
				}
				reactor.getMap().spawnItemDrop(reactor, getPlayer(), drop, dropPos, false, expire);
			}
			dropPos.x += 25;

		}
	}

	private List<DropEntry> getDropChances() {
		return ReactorScriptManager.getInstance().getDrops(reactor.getId());
	}


	// summon one monster on reactor location
	public void spawnMonster(int id) {
		spawnMonster(id, 1, getPosition(), false, 0);
	}

    public void spawnMonsterEffect(int id, int effect) {
		spawnMonster(id, 1, getPosition(), false, effect);
	}

	public void spawnMonster(int id, boolean dropsDisabled) {
		spawnMonster(id, 1, getPosition(), dropsDisabled, 0);
	}

	// summon one monster, remote location
	public void spawnMonster(int id, int x, int y) {
		spawnMonster(id, 1, new Point(x, y), false, 0);
	}

    public void spawnMonsterEffect(int id, int x, int y, int effect) {
		spawnMonster(id, 1, new Point(x, y), false, effect);
	}

	// multiple monsters, reactor location
	public void spawnMonster(int id, int qty) {
		spawnMonster(id, qty, getPosition(), false, 0);
	}

    public void spawnMonsterEffect(int id, int qty, int effect) {
		spawnMonster(id, qty, getPosition(), false, effect);
	}

	public void spawnMonster(int id, int qty, boolean dropsDisabled) {
		spawnMonster(id, qty, getPosition(), dropsDisabled, 0);
	}

	// multiple monsters, remote location
	public void spawnMonster(int id, int qty, int x, int y) {
		spawnMonster(id, qty, new Point(x, y), false, 0);
	}

	public void spawnMonster(int id, int qty, Point pos) {
		spawnMonster(id, qty, pos, false, 0);
	}

    public void spawnMonster(int id, int qty, Point pos, boolean dropsDisabled) {
		spawnMonster(id, qty, pos, dropsDisabled, 0);
	}

	// handler for all spawnMonster
	public void spawnMonster(int id, int qty, Point pos, boolean dropsDisabled, int effect) {
		for (int i = 0; i < qty; i++) {
			MapleMonster mob = MapleLifeFactory.getMonster(id);
            if (effect > 0) {
                mob.setSummonEffect(effect);
            }
			mob.setDropsEnabled(!dropsDisabled);
			reactor.getMap().spawnMonsterOnGroundBelow(mob, pos);
		}
	}

	public void spawnZakum(int x, int y) {
		spawnFakeMonster(8800000, x, y);
		spawnMonster(8800003, x, y);
		spawnMonster(8800004, x, y);
		spawnMonster(8800005, x, y);
		spawnMonster(8800006, x, y);
		spawnMonster(8800007, x, y);
		spawnMonster(8800008, x, y);
		spawnMonster(8800009, x, y);
		spawnMonster(8800010, x, y);
	}

	// returns slightly above the reactor's position for monster spawns
	private Point getPosition() {
		Point pos = reactor.getPosition();
		pos.y -= 10;
		return pos;
	}

	//reset the reactor to state 0
	public void reset() {
		reactor.reset();
	}

	public void spawnFakeMonster(int id) {
		spawnFakeMonster(id, 1, getPosition());
	}

	// summon one monster, remote location
	public void spawnFakeMonster(int id, int x, int y) {
		spawnFakeMonster(id, 1, new Point(x, y));
	}

	// multiple monsters, reactor location
	public void spawnFakeMonster(int id, int qty) {
		spawnFakeMonster(id, qty, getPosition());
	}

	// multiple monsters, remote location
	public void spawnFakeMonster(int id, int qty, int x, int y) {
		spawnFakeMonster(id, qty, new Point(x, y));
	}

	// handler for all spawnFakeMonster
	private void spawnFakeMonster(int id, int qty, Point pos) {
		for (int i = 0; i < qty; i++) {
			MapleMonster mob = MapleLifeFactory.getMonster(id);
			reactor.getMap().spawnFakeMonsterOnGroundBelow(mob, pos);
		}
	}
}
	
