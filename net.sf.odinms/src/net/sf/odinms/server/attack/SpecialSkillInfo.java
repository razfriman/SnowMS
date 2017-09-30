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

package net.sf.odinms.server.attack;

/**
 *
 * @author Raz
 */
 public class SpecialSkillInfo {
	public int skillId;
	public byte level;
	public byte direction;
	public byte wSpeed;
	
	public SpecialSkillInfo() {
	this.skillId = 0;
	this.level = 0;
	this.direction = 0;
	this.wSpeed = 0;
	}
	
	public SpecialSkillInfo(int skillId, byte level, byte direction, byte wSpeed) {
	    this.skillId = skillId;
	    this.level = level;
	    this.direction = direction;
	    this.wSpeed = wSpeed;
	}
	
	public int getSkillId() {
	    return skillId; 
	}
	
	public byte getLevel() {
	    return level;
	}
	
	public byte getDirection() {
	    return direction;
	}
	
	public byte getWSpeed() {
	    return wSpeed;
	}
	
	public void setSkillId(int skillId) {
	    this.skillId = skillId;
	}
	
	public void setLevel(byte level) {
	    this.level = level;
	}
	
	public void setDirection(byte direction) {
	    this.direction = direction;
	}
	
	public void setWSpeed(byte wSpeed) {
	    this.wSpeed = wSpeed;
	}
    }
