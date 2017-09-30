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

package net.sf.odinms.client.status;

import java.io.Serializable;

import net.sf.odinms.net.IntValueHolder;

public enum MonsterStatus implements IntValueHolder, Serializable {
	WATK(0x1),
	WDEF(0x2),
	MATK(0x4), 
 	MDEF(0x8), 
 	
	ACC(0x10), 
 	AVOID(0x20), 
	SPEED(0x40),
	STUN(0x80), //this is possibly only the bowman stun
	
	FREEZE(0x100),
	POISON(0x200),
	SEAL(0x400),
	TAUNT(0x800),
	
	WEAPON_ATTACK_UP(0x1000), 
 	WEAPON_DEFENSE_UP(0x2000), 
 	MAGIC_ATTACK_UP(0x4000), 
	MAGIC_DEFENSE_UP(0x8000),
	
	DOOM(0x10000),
	SHADOW_WEB(0x20000),
	WEAPON_IMMUNITY(0x40000),
	MAGIC_IMMUNITY(0x80000),
	
	UNKNOWN_1(0x100000),
	UNKNOWN_2(0x200000),
	NINJA_AMBUSH(0x400000),
	UNKNOWN_3(0x800000),
	
	VENOMOUS_WEAPON(0x1000000),
	UNKNOWN_4(0x2000000),
	UNKNOWN_5(0x4000000),
	EMPTY(0x8000000),
	
	HYPNOTIZE(0x10000000),
	WEAPON_DAMAGE_REFLECT(0x20000000),
	MAGIC_DAMAGE_REFLECT(0x40000000),
	UNKNOWN_6(0x80000000),
	;
	
	static final long serialVersionUID = 0L;
	private final int i;

	private MonsterStatus(int i) {
		this.i = i;
	}

    public static MonsterStatus getByName(String name) {
	    for (MonsterStatus l : MonsterStatus.values()) {
			if (l.name().equalsIgnoreCase(name)) {
				return l;
			}
	    }
	    return null;
	}

	@Override
	public int getValue() {
		return i;
	}
}

/*<imgdir name="9501000">
		<string name="name" value="Sealed"/>
	</imgdir>
	<imgdir name="9501001">
		<string name="name" value="Darkness"/>
	</imgdir>
	<imgdir name="9501002">
		<string name="name" value="Weakness"/>
	</imgdir>
	<imgdir name="9501003">
		<string name="name" value="Knocked Out"/>
	</imgdir>
	<imgdir name="9501004">
		<string name="name" value="Cursed"/>
	</imgdir>
	<imgdir name="9501005">
		<string name="name" value="Poisoned"/>
	</imgdir>
	<imgdir name="9501006">
		<string name="name" value="Slow"/>
	</imgdir>
	<imgdir name="9501007">
		<string name="name" value="Disable Buff"/>
	</imgdir>
	<imgdir name="9501008">
		<string name="name" value="Seduce"/>
	</imgdir>
	<imgdir name="9501009">
		<string name="name" value="Immune to Weapon"/>
	</imgdir>
	<imgdir name="9501010">
		<string name="name" value="Immune to Magic"/>
	</imgdir>
	<imgdir name="9501011">
		<string name="name" value="Cancel Element"/>
	</imgdir>
	<imgdir name="9501012">
		<string name="name" value="Reduce Element"/>
	</imgdir>
	<imgdir name="9501013">
		<string name="name" value="Expand Element"/>
	</imgdir>
	<imgdir name="9501014">
		<string name="name" value="Undead"/>
	</imgdir>
	<imgdir name="9501015">
		<string name="name" value="Boss"/>
	</imgdir>
	<imgdir name="9501016">
		<string name="name" value="Monster Book Test"/>
	</imgdir>*/