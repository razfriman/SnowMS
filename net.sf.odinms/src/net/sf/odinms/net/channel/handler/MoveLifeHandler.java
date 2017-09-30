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


import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.server.life.MonsterSkill;
import net.sf.odinms.server.maps.MapleMapObject;
import net.sf.odinms.server.maps.MapleMapObjectType;
import net.sf.odinms.server.movement.MovementPath;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoveLifeHandler extends AbstractMovementPacketHandler {

    private static Logger log = LoggerFactory.getLogger(MoveLifeHandler.class);

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
	int objectid = slea.readInt();
	short moveid = slea.readShort();
	MapleMapObject mmo = c.getPlayer().getMap().getMapObject(objectid);
	if (mmo == null || mmo.getType() != MapleMapObjectType.MONSTER) {
	    return;
	}
	MapleMonster monster = (MapleMonster) mmo;


	int useSkill = slea.readByte();//Use Skill/No Skills
	int skill_1 = slea.readByte();//HMMM O.o?
	int skillId = slea.readByte() & 0xFF;//SkillId (unsigned)
	int skillLevel = slea.readByte();//Level
	int skillDelay = slea.readShort();//Delay
	MonsterSkill skillToUse = null;
	slea.skip(5);
        slea.readPoint();
        slea.readPoint();

        MovementPath movementPath = parseMovement(slea);

	monster.setMoveid(moveid);

        if (useSkill == 1 && monster.getSkillEntrySize() > 0) {//CHOOSE A SKILL FOR THE MONSTER TO USE
	    skillToUse = monster.chooseSkill();
	}

	if ((skillId >= 100 && skillId <= 200) && monster.hasSkillEntry(skillId, skillLevel)) {
	    MonsterSkill skill = monster.getSkill(skillId, skillLevel);
	    skill.setDelay(skillDelay);
	    monster.useSkill(c.getPlayer(), skill);
	}

	if (monster.getController() != c.getPlayer()) {
	    if (monster.isAttackedBy(c.getPlayer())) { // aggro and controller change
		monster.switchController(c.getPlayer(), true);
	    } else {
		return;
	    }
	} else {
	    if (skill_1 == -1 && monster.isControllerKnowsAboutAggro() && !monster.isMobile() && !monster.isAutoAggro()) {
		monster.setControllerHasAggro(false);
		monster.setControllerKnowsAboutAggro(false);
	    }
	}
	boolean aggro = monster.isControllerHasAggro();
	if (skillToUse != null) {
	    c.getSession().write(MaplePacketCreator.moveMonsterResponse(objectid, moveid, monster.getMp(), aggro, skillToUse.getSkillId(), skillToUse.getSkillLevel()));
	} else {
	    c.getSession().write(MaplePacketCreator.moveMonsterResponse(objectid, moveid, monster.getMp(), aggro));
	}

	if (aggro) {
	    monster.setControllerKnowsAboutAggro(true);
	}

	if (movementPath != null) {
	    MaplePacket packet = MaplePacketCreator.moveMonster(useSkill, skill_1, skillId, skillLevel, skillDelay, objectid, movementPath);

	    c.getPlayer().getMap().broadcastMessage(c.getPlayer(), packet, monster.getPosition());
	    updatePosition(movementPath, monster, -1);
	    c.getPlayer().getMap().moveMonster(monster, monster.getPosition());
	    c.getPlayer().getCheatTracker().checkMoveMonster(monster.getPosition());
	}
    }
}
