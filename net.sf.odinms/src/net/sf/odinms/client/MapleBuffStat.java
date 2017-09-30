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

package net.sf.odinms.client;

import net.sf.odinms.net.LongValueHolder;

public enum MapleBuffStat implements LongValueHolder {
	SLOW(0x1),
	MORPH(0x2),
	RECOVERY(0x4),
	MAPLE_WARRIOR(0x8),
	
	STANCE(0x10),
	SHARP_EYES(0x20),
	MANA_REFLECTION(0x40),
	SEDUCE(0x80),
	
	SHADOW_CLAW(0x100),
	INFINITY(0x200), 
	HOLY_SHIELD(0x400),
	HAMSTRING(0x800),
	
	BLIND(0x1000),
	CONCENTRATE(0x2000),//No-Op Buff
	UNKNOWN_8(0x4000),//Maybe
	ECHO_OF_HERO(0x8000),
      
	UNKNOWN_1(0x10000),
	GHOST_MORPH(0x20000),//Ghost Morph - NOT A SKILL//UNKNOWN
	AURA(0x40000),//AriantPQ thingy
	CRAZY_SKULL(0x80000),
	
	UNKNOWN_2(0x100000),//UNKNOWN
	UNKNOWN_3(0x200000),//UNKNOWN
	UNKNOWN_4(0x400000),//UNKNOWN
	UNKNOWN_5(0x800000),//UNKNOWN
	
	HIDE(0x1000000),
	UNKNOWN_6(0x2000000),//UNKNOWN
	UNKNOWN_7(0x4000000),//UNKNOWN
	ENERGY_CHARGE(0x8000000),//short x? int energypoints int skillid int time
	
	DASH_SPEED(0x10000000),
	DASH_JUMP(0x20000000),
	MONSTER_RIDING(0x40000000),
	SPEED_INFUSION(0x80000000),
	
	WATK(0x100000000l),
	WDEF(0x200000000l),
	MATK(0x400000000l),
	MDEF(0x800000000l),
	
	ACC(0x1000000000l),
	AVOID(0x2000000000l),
	HANDS(0x4000000000l),
	SPEED(0x8000000000l),
	
	JUMP(0x10000000000l),
	MAGIC_GUARD(0x20000000000l),
	DARKSIGHT(0x40000000000l),
	BOOSTER(0x80000000000l),
	
	POWERGUARD(0x100000000000l),
	HYPERBODYHP(0x200000000000l),
	HYPERBODYMP(0x400000000000l),
	INVINCIBLE(0x800000000000l),
	
	SOULARROW(0x1000000000000l),
	STUN(0x2000000000000l),
	POISON(0x4000000000000l),
	SEAL(0x8000000000000l),//ALSO RUSH??
	
	DARKNESS(0x10000000000000l),
	COMBO(0x20000000000000l),//HACK BUFF STAT
	SUMMON(0x20000000000000l),//HACK BUFF STAT
	WK_CHARGE(0x40000000000000l),
	DRAGONBLOOD(0x80000000000000l),
	
	HOLY_SYMBOL(0x100000000000000l),
	MESOUP(0x200000000000000l),
	SHADOWPARTNER(0x400000000000000l),
	PICKPOCKET(0x800000000000000l),//HACK BUFF STAT
	PUPPET(0x800000000000000l),//HACK BUFF STAT
	
	MESOGUARD(0x1000000000000000l),
	THAW(0x2000000000000000l),
	WEAKEN(0x4000000000000000l),
	CURSE(0x8000000000000000l),//Wrong?!?//UNKNOWN
	;
	private final long i;

	private MapleBuffStat(long i) {
		this.i = i;
	}
	
	public static MapleBuffStat getByType(long value) {
		for (MapleBuffStat l : MapleBuffStat.values()) {
			if (l.getValue() == value) {
				return l;
			}
		}
		return null;
	}
	
	public static MapleBuffStat getByName(String name) {
	    for (MapleBuffStat l : MapleBuffStat.values()) {
			if (l.name().equalsIgnoreCase(name)) {
				return l;
			}
	    }
	    return null;
	}

	@Override
	public long getValue() {
		return i;
	}
}