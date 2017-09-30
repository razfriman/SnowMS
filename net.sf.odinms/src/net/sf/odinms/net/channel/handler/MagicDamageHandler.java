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

import java.util.List;

import net.sf.odinms.client.ISkill;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.SkillFactory;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.server.MapleStatEffect;
import net.sf.odinms.server.attack.AttackInfo;
import net.sf.odinms.server.attack.AttackInfo.AttackType;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.Pair;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class MagicDamageHandler extends AbstractDealDamageHandler {
	@Override
	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {

		AttackInfo attack = parseDamage(c, slea, AttackType.MAGIC);
		MapleCharacter player = c.getPlayer();
		
		MaplePacket packet = MaplePacketCreator.magicAttack(player.getId(), attack);
		player.getMap().broadcastMessage(player, packet, false, true);

		MapleStatEffect effect = attack.getAttackEffect(c.getPlayer());
		int maxdamage;
		// // TODO better magic damage calculation + calculate the elemental modifier
		// double elementalMod = 1.5;
		// maxdamage = (int) ((magic * 3.3 + magic * magic * 0.003365 + int_ * 0.5) *
		// ((effect.getMatk() * ampMod) * 0.01) * elementalMod) + 10;
		// } else {
		// maxdamage = 8000;
		// }
		// TODO fix magic damage calculation
		maxdamage = 40000;
		
		
		applyAttack(attack, player, maxdamage, effect.getAttackCount());
		
		// MP Eater
		for (int i = 1; i <= 3; i++) {
			ISkill eaterSkill = SkillFactory.getSkill(2000000 + i * 100000);
			int eaterLevel = player.getSkillLevel(eaterSkill);
			if (eaterLevel > 0) {
				for (Pair<Integer, List<Integer>> singleDamage : attack.getAllDamage()) {
					eaterSkill.getEffect(eaterLevel).applyPassive(player, player.getMap().getMapObject(singleDamage.getLeft()), 0);
				}
				break;
			}
		}
	}
}
