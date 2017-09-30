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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.odinms.client.ISkill;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleInventoryType;
import net.sf.odinms.client.SkillFactory;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.provider.MapleData;
import net.sf.odinms.provider.MapleDataTool;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.Randomizer;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;
	
public class UseSkillBookHandler extends AbstractMaplePacketHandler {


	private static Map<Integer, SkillBook> loadedBooks = new HashMap<Integer, SkillBook>();
	
	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c){
		if (!c.getPlayer().isAlive()) {
			c.getSession().write(MaplePacketCreator.enableActions());
			return;
		}

		c.getPlayer().getCheatTracker().inspectActionTime(slea.readInt(), 200);
		byte slot = (byte) slea.readShort();
		int itemid = slea.readInt();
		SkillBook book = loadedBooks.get(itemid);

        if (book == null) {
			book = new SkillBook();
			book.itemid = itemid;
			if(c.getPlayer().getInventory(MapleInventoryType.USE).getItem(slot).getItemId() != book.itemid) {
				return;//HACKING
			}
			MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
			MapleData data = ii.getItemData(book.itemid).getChildByPath("info");
			book.masterLevel = MapleDataTool.getIntConvert("masterLevel", data, -1);
			book.success = MapleDataTool.getIntConvert("success", data, -1);
			book.reqSkillLevel = MapleDataTool.getIntConvert("reqSkillLevel", data, -1);
			for(MapleData skillData : data.getChildByPath("skill").getChildren()) {
				book.skills.add(MapleDataTool.getIntConvert(skillData));
			}
			loadedBooks.put(itemid, book);
		}

		boolean success = book.success >= Randomizer.randomInt(100);
		boolean canUse = true;

		for(int skillid : book.skills) {
		    ISkill skill = SkillFactory.getSkill(skillid);
		    if(!skill.canBeLearnedBy(c.getPlayer().getJob()))
			canUse = false;
		    if(c.getPlayer().getSkillLevel(skill) < book.reqSkillLevel)
			canUse = false;
		    if(c.getPlayer().getMasterLevel(skill) >= book.masterLevel)
			canUse = false;
		    if(canUse) {
			c.getPlayer().changeSkillLevel(skill, c.getPlayer().getSkillLevel(skill), book.masterLevel);
			MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
		    }
		}
		c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.useSkillBook(c.getPlayer(), book.skills, book.masterLevel, canUse, success));//1 = new max level
	}

	public class SkillBook {
	    public int itemid;
	    public int masterLevel;
	    public int success;
	    public int reqSkillLevel;
	    public List<Integer> skills = new ArrayList<Integer>();

	}
}
