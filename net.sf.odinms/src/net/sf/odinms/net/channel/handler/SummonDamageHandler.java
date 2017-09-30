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

package net.sf.odinms.net.channel.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sf.odinms.client.ISkill;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.SkillFactory;
import net.sf.odinms.client.anticheat.CheatingOffense;
import net.sf.odinms.client.status.MonsterStatusEffect;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.AutobanManager;
import net.sf.odinms.server.MapleStatEffect;
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.server.maps.MapleSummon;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SummonDamageHandler extends AbstractMaplePacketHandler {

    private static Logger log = LoggerFactory.getLogger(SummonDamageHandler.class);

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
	//System.out.println("\nPacket:\n" + slea.toString() + "\n");
	//int summonSkillId = slea.readInt();
	int oid = slea.readInt();

	MapleCharacter player = c.getPlayer();
	Collection<MapleSummon> summons = player.getSummons().values();
	MapleSummon summon = null;
	for (MapleSummon sum : summons) {
	    if (sum.getObjectId() == oid) {
		summon = sum;
	    }
	}
	if (summon == null) {
	    log.info(MapleClient.getLogMessage(c, "Using summon attack without a summon"));
	    return; // attacking with a nonexistant summon
	}
	ISkill summonSkill = SkillFactory.getSkill(summon.getSkill());
	MapleStatEffect summonEffect = summonSkill.getEffect(summon.getSkillLevel());
	slea.skip(5);
	List<SummonAttackEntry> allDamage = new ArrayList<SummonAttackEntry>();
	int numAttacked = slea.readByte();
	player.getCheatTracker().checkSummonAttack();
	for (int x = 0; x < numAttacked; x++) {
	    int monsterOid = slea.readInt(); // attacked oid
	    slea.skip(14); // who knows
	    int damage = slea.readInt();

	    allDamage.add(new SummonAttackEntry(monsterOid, damage));
	}

	if (!player.isAlive()) {
	    player.getCheatTracker().registerOffense(CheatingOffense.ATTACKING_WHILE_DEAD);
	    return;
	}
	player.getMap().broadcastMessage(player, MaplePacketCreator.summonAttack(player.getId(), summon.getSkill(), 4, allDamage), summon.getPosition());
	for (SummonAttackEntry attackEntry : allDamage) {
	    int damage = attackEntry.getDamage();
	    MapleMonster target = player.getMap().getMonsterByOid(attackEntry.getMonsterOid());

	    if (target != null) {
		if (damage > 0 && summonEffect.getMonsterStati().size() > 0) {
		    if (summonEffect.makeChanceResult()) {
			MonsterStatusEffect monsterStatusEffect = new MonsterStatusEffect(summonEffect.getMonsterStati(), summonSkill, false);

			target.applyStatus(player, monsterStatusEffect, summonEffect.isPoison(), 4000);
		    }
		}

		player.getMap().damageMonster(player, target, damage);
		if (damage > 60000) {
		    AutobanManager.getInstance().autoban(c, "High Summon Damage (" + damage + " to " + target.getId() + ")");
		}
	    }
	}
    }
    
    public class SummonAttackEntry {

	private int monsterOid;
	private int damage;

	public SummonAttackEntry(int monsterOid, int damage) {
	    super();
	    this.monsterOid = monsterOid;
	    this.damage = damage;
	}

	public int getMonsterOid() {
	    return monsterOid;
	}

	public int getDamage() {
	    return damage;
	}
    }
}
