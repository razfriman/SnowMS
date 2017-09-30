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
import java.util.List;

import net.sf.odinms.client.MapleBuffStat;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleStat;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.AutobanManager;
import net.sf.odinms.server.attack.MonsterAttackInfo;
import net.sf.odinms.server.attack.PGMRInfo;
import net.sf.odinms.server.life.MapleLifeFactory;
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.server.life.MonsterSkill;
import net.sf.odinms.server.life.MonsterSkillFactory;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.Pair;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class TakeDamageHandler extends AbstractMaplePacketHandler {
    //Give this file a nice makeover :D
    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
	//
	MapleCharacter player = c.getPlayer();
	slea.readInt();
	int damagefrom = slea.readByte();
	int hit = 0;
	byte element = slea.readByte();// Element - 0x00 = elementless || 0x01 = ice || 0x02 = fire || 0x03 = lightning
	int damage = slea.readInt();
	int oid = 0;
	int monsteridfrom = 0;
	int nodamageid = 0;
	int job = c.getPlayer().getJob().getId();
	int fake = 0;
	boolean appliedDamage = false;
	MapleMonster attacker = null;
	PGMRInfo pgmr = null;
	MonsterAttackInfo attackInfo = null;
	//
	if (damagefrom != -2) {
	    monsteridfrom = slea.readInt();
	    oid = slea.readInt();
	    attacker = c.getPlayer().getMap().getMonsterByOid(oid);
	    if (attacker != null && attacker.getPvpOwner() != null) {
		//DONT TAKE DAMAGE THAT WOULD DEFEAT PURPOSE OF PVP w/Mob-Dummies
		return;
	    }
	}

	if (damagefrom != -1 && damagefrom != -2 && attacker != null) {
	    attackInfo = MonsterAttackInfo.loadFromData(attacker, damagefrom + 1);
	    if (attackInfo != null) {
		if (attackInfo.isDeadlyAttack()) {
		    player.addMP(-player.getMp() + 1);
		    player.addHP(-player.getHp() + 1);//Or do monsters just hit the damage?
		}
		if (attackInfo.getMpBurn() > 0) {
		    player.addMP(-attackInfo.getMpBurn());
		}
		if (attackInfo.getDisease() != 0 && attackInfo.getLevel() != 0) {
		    MonsterSkill skill = MonsterSkillFactory.getMonsterSkill(attackInfo.getDisease(), attackInfo.getLevel());
		    if (skill != null && damage > 0) {
			attacker.useSkill(player, skill);
		    }
		}
		attacker.setMp(attacker.getMp() - attackInfo.getMpConsume());//Reducde Monster MP
	    }
	}

	if (damage == -1) {
	    job = player.getJob().getId() / 10 - 40;
	    fake = 4020002 + (job * 100000);
	}

	if (damage < 0 || damage > 60000) {
	    AutobanManager.getInstance().addPoints(c, 1000, 60000, "Taking abnormal amounts of damge from " + monsteridfrom + ": " + damage);
	    return;
	}
	player.getCheatTracker().checkTakeDamage();

	if (damage > 0) {
	    player.getCheatTracker().setAttacksWithoutHit(0);
	    player.getCheatTracker().resetHPRegen();
	}
	if (damage > 0 && !player.isHidden()) {
	    if (damagefrom == -1) {
		Integer pguard = player.getBuffedValue(MapleBuffStat.POWERGUARD);
		if (pguard != null) {
		    // why do we have to do this? -.- the client shows the damage...
		    if (attacker != null && !attacker.isBoss()) {
			int bouncedamage = (int) (damage * (pguard.doubleValue() / 100));
			bouncedamage = Math.min(bouncedamage, attacker.getMaxHp() / 10);
			player.getMap().damageMonster(player, attacker, bouncedamage);
			damage -= bouncedamage;
			player.getMap().broadcastMessage(player, MaplePacketCreator.damageMonster(oid, bouncedamage), false, true);
		    }
		}
	    }
	    Integer mguard = player.getBuffedValue(MapleBuffStat.MAGIC_GUARD);
	    Integer mesoguard = player.getBuffedValue(MapleBuffStat.MESOGUARD);

	    if (player.getBuffedValue(MapleBuffStat.MORPH) != null) {
		player.cancelBuffStats(MapleBuffStat.MORPH);
	    }

	    if (mguard != null) {
		List<Pair<MapleStat, Integer>> stats = new ArrayList<Pair<MapleStat, Integer>>(2);
		int mploss = (int) (damage * (mguard.doubleValue() / 100.0));
		int hploss = damage - mploss;
		if (mploss > player.getMp()) {
		    hploss += mploss - player.getMp();
		    mploss = player.getMp();
		}

		player.addMPHP(-hploss, -mploss);
	    } else if (mesoguard != null) {
		damage = (damage % 2 == 0) ? damage / 2 : (damage / 2) + 1;
		int mesoloss = (int) (damage * (mesoguard.doubleValue() / 100.0));
		if (player.getMeso() < mesoloss) {
		    player.gainMeso(-player.getMeso(), false);
		    player.cancelBuffStats(MapleBuffStat.MESOGUARD);
		} else {
		    player.gainMeso(-mesoloss, false);
		}
		player.addHP(-damage);
	    } else {
		player.addHP(-damage);
	    }

	}
	if (!player.isHidden() && MapleLifeFactory.getMonster(monsteridfrom) != null) {
	    player.getMap().broadcastMessage(player, MaplePacketCreator.damagePlayer(damagefrom, monsteridfrom, player.getId(), damage), false);
	}
    }
}
