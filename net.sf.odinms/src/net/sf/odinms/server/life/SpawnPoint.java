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

import java.awt.Point;
import java.util.concurrent.atomic.AtomicInteger;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.server.maps.MapleMap;

public class SpawnPoint {
	private MapleMonster monster;
	private Point pos;
	private long nextPossibleSpawn;
	private int mobTime;
	private AtomicInteger spawnedMonsters = new AtomicInteger(0);
		
	/**
	 * Wether the spawned monster is immobile
	 */
	private boolean immobile;
	
	public Point getPosition(){
	    return pos;
	}
	
	public void reset(){
		this.nextPossibleSpawn = System.currentTimeMillis();
		spawnedMonsters.set(0);
	}
	
	public SpawnPoint(MapleMonster monster, Point pos, int mobTime) {
		super();
		this.monster = monster;
		this.pos = new Point(pos);
		this.mobTime = mobTime;
		this.immobile = !monster.isMobile();
		this.nextPossibleSpawn = System.currentTimeMillis();
	}

	public boolean shouldSpawn(MapleMap map) {
		return shouldSpawn(System.currentTimeMillis(), map);
	}
	
	// intentionally package private
	boolean shouldSpawn(long now, MapleMap map) {
		if (mobTime < 0) {
			return false;
		}

        if (map.getBlockedMonsterSpawns().contains(monster.getId())) {
            return false;
        }
		// regular spawnpoints should spawn a maximum of 3 monsters; immobile spawnpoints or spawnpoints with mobtime a
		// maximum of 1
		if (((mobTime != 0 || immobile) && spawnedMonsters.get() > 0) || spawnedMonsters.get() > 2) {
			return false;
		}
		return nextPossibleSpawn <= now;
	}

	/**
	 * Spawns the monster for this spawnpoint. Creates a new MapleMonster instance for that and returns it.
	 * 
	 * @param mapleMap
	 * @return the monster spawned
	 */
	public MapleMonster spawnMonster(MapleMap mapleMap) {
		MapleMonster mob = new MapleMonster(monster);
		mob.setPosition(new Point(pos));
		spawnedMonsters.incrementAndGet();
		mob.addListener(new MonsterListener() {
			@Override
			public void monsterKilled(MapleMonster monster, MapleCharacter highestDamageChar) {
				nextPossibleSpawn = System.currentTimeMillis();
				if (mobTime > 0) {
					nextPossibleSpawn += mobTime * 1000;
				} else {
					nextPossibleSpawn += monster.getAnimationTime("die1");
				}
				spawnedMonsters.decrementAndGet();
			}
		});
		mapleMap.spawnMonster(mob);
		if (mobTime == 0) {
			nextPossibleSpawn = System.currentTimeMillis() + 5000;
		}
		return mob;
	}
}
