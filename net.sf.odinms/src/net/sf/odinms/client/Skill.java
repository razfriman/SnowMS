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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.Map;
import net.sf.odinms.provider.MapleData;
import net.sf.odinms.provider.MapleDataTool;
import net.sf.odinms.server.MapleStatEffect;
import net.sf.odinms.server.life.Element;
import net.sf.odinms.tools.StringUtil;

public class Skill implements ISkill {
	private int id;
	private List<MapleStatEffect> effects = new ArrayList<MapleStatEffect>();
	private Element element;
	private int animationTime;
	private boolean charge = false;
	private Map<Integer, Integer> requiredSkillLevels = new HashMap<Integer, Integer>();

	private Skill(int id) {
		super();
		this.id = id;
	}

	@Override
	public int getId() {
		return id;
	}

	/**
	 * Loads a skill from the data
	 * @param id skill's id
	 * @param data skill's data
	 * @return A Skill loaded from the data.
	 */
	public static Skill loadFromData(int id, MapleData data) {
		Skill ret = new Skill(id);
		boolean isBuff = false;
		int skillType = MapleDataTool.getInt("skillType", data, -1);
		String elem = MapleDataTool.getString("elemAttr", data, null);
		if (elem != null) {
			ret.element = Element.getFromChar(elem.charAt(0));
		} else {
			ret.element = Element.NEUTRAL;
		}
		// unfortunatly this is only set for a few skills so we have to do some more to figure out if it's a buff �.o
		MapleData effect = data.getChildByPath("effect");
		if (skillType != -1) {
			if (skillType == 2) {
				isBuff = true;
			}
		} else {
			MapleData action = data.getChildByPath("action");
			MapleData hit = data.getChildByPath("hit");
			MapleData ball = data.getChildByPath("ball");
			MapleData keydown = data.getChildByPath("keydown");
			if (keydown != null) {
			    ret.charge = true;
			}
			isBuff = effect != null && hit == null && ball == null;
			isBuff |= action != null && MapleDataTool.getString("0", action, "").equals("alert2");
			switch (id) {
				case 2301002: // heal is alert2 but not overtime...
				case 2111003: // poison mist
				case 2111002: // explosion
				case 4211001: // chakra
				case 2121001: // Big Bang
				case 2221001: // Big Bang
				case 2321001: // Big Bang
				case 1121006: // Rush
				case 1221007: // Rush
				case 1321003: // Rush
				case 1311005: // Sacrifice
				case 3110001: // Mortal Blow
				case 3210001: // Mortal Blow
				case 2311001: // Dispel
				case 9101000: // Dispel + Heal(GM)
					isBuff = false; 
					break;
				case 1111002: // combo
				case 4211003: // pickpocket
				case 4111001: // mesoup
				case 1004: // monster riding
				case 5111005: // Transformation (Buccaneer)
				case 5121003: // Super Transformation (Viper)
					isBuff = true;
					break;
			}
		}
		for (MapleData level : data.getChildByPath("level")) {
			MapleStatEffect statEffect = MapleStatEffect.loadSkillEffectFromData(level, id, isBuff);
			ret.effects.add(statEffect);
		}

        MapleData reqDataRoot = data.getChildByPath("req");
        if (reqDataRoot != null) {
            for(MapleData reqData : reqDataRoot.getChildren()) {
                ret.requiredSkillLevels.put(Integer.parseInt(reqData.getName()), MapleDataTool.getIntConvert(reqData, 0));
            }
        }

		ret.animationTime = 0;
		if (effect != null) {
			for (MapleData effectEntry : effect) {
				ret.animationTime += MapleDataTool.getIntConvert("delay", effectEntry, 0);
			}
		}
		return ret;
	}

	@Override
	public MapleStatEffect getEffect(int level) {
		return effects.get(level - 1);
	}

	@Override
	public int getMaxLevel() {
		return effects.size();	
	}

	@Override
	public boolean canBeLearnedBy(MapleJob job) {
		int jid = job.getId();
		int skillForJob = id / 10000;
		if (jid / 100 != skillForJob / 100 && skillForJob / 100 != 0) { // wrong job
			return false;
		}
		if ((skillForJob / 10) % 10 > (jid / 10) % 10) { // wrong 2nd job
			return false;
		}
		if (skillForJob % 10 > jid % 10) { // wrong 3rd/4th job
			return false;
		}
		return true;
	}
	@Override
	public MapleJob getJob() {
	    String idStr = StringUtil.getLeftPaddedStr(Integer.toString(id), '0', 7);
	    idStr = idStr.substring(0, 3);
	    int jobId = Integer.parseInt(idStr);
	    return MapleJob.getById(jobId);
	}

	@Override
	public boolean isFourthJob() {
		return ((id / 10000) % 10) == 2;
	}

	@Override
	public Element getElement() {
		return element;
	}

	@Override
	public int getAnimationTime() {
		return animationTime;
	}

    @Override
    public boolean hasRequiredSkillLevels() {
        return requiredSkillLevels.size() > 0;
    }

    @Override
    public Map<Integer, Integer> getRequiredSkillLevels() {
        return requiredSkillLevels;
    }

    @Override
    public boolean hasCharge() {
	  return charge;
    }

}
