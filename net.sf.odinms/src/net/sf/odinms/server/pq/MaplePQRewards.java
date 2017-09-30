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
package net.sf.odinms.server.pq;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleReward;

/**
 *
 * @author Raz
 */
public class MaplePQRewards {

	//ID--------QUANTITY-------CHANCE\\
	public static final MapleReward LMPQrewards[] = {//Ludi-Maze PQ
		new MapleReward(2000005, 1, 1),//USE ITEMS
		new MapleReward(2000006, 100, 1),
		new MapleReward(2001002, 5, 1),
		new MapleReward(2020008, 20, 1),
		new MapleReward(2020007, 100, 1),
		new MapleReward(2020010, 20, 1),
		new MapleReward(2030008, 20, 1),
		new MapleReward(2030010, 20, 1),
		new MapleReward(2030009, 20, 1),
		new MapleReward(2020009, 100, 1),
		new MapleReward(2022000, 50, 1),
		new MapleReward(2001000, 50, 1),
		new MapleReward(2000004, 5, 1),
		new MapleReward(1302016, 1, 1),//EQUIPS
		new MapleReward(1032013, 1, 1),
		new MapleReward(1442017, 1, 1),
		new MapleReward(1322025, 1, 1),
		new MapleReward(2041020, 1, 1),//SCROLLS
		new MapleReward(2041017, 1, 1),
		new MapleReward(2040905, 1, 1),
		new MapleReward(2040904, 1, 1),
		new MapleReward(2040901, 1, 1),
		new MapleReward(2040902, 1, 1),
		new MapleReward(2040602, 1, 1),
		new MapleReward(2040601, 1, 1),
		new MapleReward(2040604, 1, 1),
		new MapleReward(2040605, 1, 1),
		new MapleReward(2040405, 1, 1),
		new MapleReward(2040404, 1, 1),
		new MapleReward(2040401, 1, 1),
		new MapleReward(2040402, 1, 1),
		new MapleReward(2040504, 1, 1),
		new MapleReward(2040505, 1, 1),
		new MapleReward(2040510, 1, 1),
		new MapleReward(2040511, 1, 1),
		new MapleReward(2041027, 1, 1),
		new MapleReward(2041026, 1, 1),
		new MapleReward(2041029, 1, 1),
		new MapleReward(2041028, 1, 1),
		new MapleReward(2041005, 1, 1),
		new MapleReward(2041001, 1, 1),
		new MapleReward(2041002, 1, 1),
		new MapleReward(2041004, 1, 1)
	};
	public static final MapleReward LPQrewards[] = {//Ludi PQ
		new MapleReward(2000005, 1, 1),//USE ITEMS
		new MapleReward(2000006, 100, 1),
		new MapleReward(2001002, 5, 1),
		new MapleReward(2020008, 20, 1),
		new MapleReward(2020007, 100, 1),
		new MapleReward(2020010, 20, 1),
		new MapleReward(2030008, 20, 1),
		new MapleReward(2030010, 20, 1),
		new MapleReward(2030009, 20, 1),
		new MapleReward(2020009, 100, 1),
		new MapleReward(2022000, 50, 1),
		new MapleReward(2001000, 50, 1),
		new MapleReward(2000004, 5, 1),
		new MapleReward(1302016, 1, 1),//EQUIPS
		new MapleReward(1032013, 1, 1),
		new MapleReward(1442017, 1, 1),
		new MapleReward(1322025, 1, 1),
		new MapleReward(2041020, 1, 1),//SCROLLS
		new MapleReward(2041017, 1, 1),
		new MapleReward(2040905, 1, 1),
		new MapleReward(2040904, 1, 1),
		new MapleReward(2040901, 1, 1),
		new MapleReward(2040902, 1, 1),
		new MapleReward(2040602, 1, 1),
		new MapleReward(2040601, 1, 1),
		new MapleReward(2040604, 1, 1),
		new MapleReward(2040605, 1, 1),
		new MapleReward(2040405, 1, 1),
		new MapleReward(2040404, 1, 1),
		new MapleReward(2040401, 1, 1),
		new MapleReward(2040402, 1, 1),
		new MapleReward(2040504, 1, 1),
		new MapleReward(2040505, 1, 1),
		new MapleReward(2040510, 1, 1),
		new MapleReward(2040511, 1, 1),
		new MapleReward(2041027, 1, 1),
		new MapleReward(2041026, 1, 1),
		new MapleReward(2041029, 1, 1),
		new MapleReward(2041028, 1, 1),
		new MapleReward(2041005, 1, 1),
		new MapleReward(2041001, 1, 1),
		new MapleReward(2041002, 1, 1),
		new MapleReward(2041004, 1, 1)
	};
	public static final MapleReward KPQrewards[] = {//Kerning-{Q
		new MapleReward(2040502, 1, 1),//Scrolls
		new MapleReward(2040505, 1, 1),
		new MapleReward(2040514, 1, 1),
		new MapleReward(2040517, 1, 1),
		new MapleReward(2040802, 1, 1),
		new MapleReward(2040805, 1, 1),
		new MapleReward(2040002, 1, 1),
		new MapleReward(2040402, 1, 1),
		new MapleReward(2040602, 1, 1),
		new MapleReward(2040902, 1, 1),
		new MapleReward(2044502, 1, 1),
		new MapleReward(2044702, 1, 1),
		new MapleReward(2044602, 1, 1),
		new MapleReward(2043302, 1, 1),
		new MapleReward(2043102, 1, 1),
		new MapleReward(2043202, 1, 1),
		new MapleReward(2043002, 1, 1),
		new MapleReward(2044402, 1, 1),
		new MapleReward(2044302, 1, 1),
		new MapleReward(2044102, 1, 1),
		new MapleReward(2044202, 1, 1),
		new MapleReward(2044002, 1, 1),
		new MapleReward(2000001, 100, 100),//Use
		new MapleReward(2000002, 75, 1),
		new MapleReward(2000003, 100, 1),
		new MapleReward(2000006, 45, 1),
		new MapleReward(2000004, 20, 1),
		new MapleReward(2000005, 10, 1),
		new MapleReward(2001000, 35, 1),
		new MapleReward(2001001, 30, 1),
		new MapleReward(2002006, 10, 1),
		new MapleReward(2002007, 10, 1),
		new MapleReward(2002008, 10, 1),
		new MapleReward(2002010, 10, 1),
		new MapleReward(1032000, 1, 1),//Equip
		new MapleReward(1032009, 1, 1),
		new MapleReward(1032004, 1, 1),
		new MapleReward(1032005, 1, 1),
		new MapleReward(1032006, 1, 1),
		new MapleReward(1032007, 1, 1),
		new MapleReward(1032010, 1, 1),
		new MapleReward(1032008, 1, 1),
		new MapleReward(1002026, 1, 1),
		new MapleReward(1002089, 1, 1),
		new MapleReward(1002090, 1, 1),
		new MapleReward(4010000, 15, 1),//Etc
		new MapleReward(4010001, 15, 1),
		new MapleReward(4010002, 15, 1),
		new MapleReward(4010003, 15, 1),
		new MapleReward(4010004, 15, 1),
		new MapleReward(4010005, 15, 1),
		new MapleReward(4010006, 8, 1),
		new MapleReward(4020000, 15, 1),
		new MapleReward(4020001, 15, 1),
		new MapleReward(4020002, 15, 1),
		new MapleReward(4020003, 15, 1),
		new MapleReward(4020004, 15, 1),
		new MapleReward(4020005, 15, 1),
		new MapleReward(4020006, 15, 1),
		new MapleReward(4020007, 8, 1),
		new MapleReward(4020007, 5, 1),
		new MapleReward(4003000, 20, 1)
	};
	public static final MapleReward JQrewards0[] = {
		new MapleReward(4010000, 2, 1),//Rock-Ores
		new MapleReward(4010001, 2, 1),
		new MapleReward(4010002, 2, 1),
		new MapleReward(4010003, 2, 1),
		new MapleReward(4010004, 2, 1),
		new MapleReward(4010005, 2, 1)
	};
	public static final MapleReward JQrewards1[] = {
		new MapleReward(4020000, 2, 1),//Crystal-Ores
		new MapleReward(4020001, 2, 1),
		new MapleReward(4020002, 2, 1),
		new MapleReward(4020003, 2, 1),
		new MapleReward(4020004, 2, 1),
		new MapleReward(4020005, 2, 1),
		new MapleReward(4020006, 2, 1)
	};
	public static final MapleReward JQrewards2[] = {
		new MapleReward(4020006, 2, 1),//Rare-Ores
		new MapleReward(4020007, 2, 1),
		new MapleReward(4020008, 2, 1)
	};
	public static final MapleReward MissingMechJQrewards[] = {
		new MapleReward(2040707, 2, 1),//Shoes Scrolls (60% 10%)
		new MapleReward(2040708, 2, 1),
		new MapleReward(2040704, 2, 1),
		new MapleReward(2040705, 2, 1)
	};
	public static final MapleReward ZPQrewards[] = {
		new MapleReward(4001017, 1, 1),//Zakum PQ
		new MapleReward(4031179, 1, 1),
		new MapleReward(2022108, 1, 1),
		new MapleReward(2022273, 5, 1),
		new MapleReward(2070005, 1, 1),
		new MapleReward(2070006, 1, 1),
		new MapleReward(1002574, 1, 1),
		new MapleReward(2040013, 1, 1),
		new MapleReward(2040015, 1, 1),
		new MapleReward(2040305, 1, 1),
		new MapleReward(2040307, 1, 1),
		new MapleReward(2040407, 1, 1),
		new MapleReward(2040411, 1, 1),
		new MapleReward(2040509, 1, 1),
		new MapleReward(2040519, 1, 1),
		new MapleReward(2040521, 1, 1),
		new MapleReward(2040611, 1, 1),
		new MapleReward(2040713, 1, 1),
		new MapleReward(2040715, 1, 1),
		new MapleReward(2040717, 1, 1),
		new MapleReward(2040809, 1, 1),
		new MapleReward(2040811, 1, 1),
		new MapleReward(2040815, 1, 1),
		new MapleReward(2040907, 1, 1),
		new MapleReward(2040909, 1, 1),
		new MapleReward(2040917, 1, 1),
		new MapleReward(2040922, 1, 1),
		new MapleReward(2041035, 1, 1),
		new MapleReward(2041037, 1, 1),
		new MapleReward(2041039, 1, 1),
		new MapleReward(2041041, 1, 1),
		new MapleReward(2043005, 1, 1),
		new MapleReward(2043105, 1, 1),
		new MapleReward(2043205, 1, 1),
		new MapleReward(2043305, 1, 1),
		new MapleReward(2043705, 1, 1),
		new MapleReward(2043805, 1, 1),
		new MapleReward(2044005, 1, 1),
		new MapleReward(2044105, 1, 1),
		new MapleReward(2044205, 1, 1),
		new MapleReward(2044305, 1, 1),
		new MapleReward(2044305, 1, 1),
		new MapleReward(2044505, 1, 1),
		new MapleReward(2044605, 1, 1),
		new MapleReward(2044705, 1, 1)
	};

	public static void giveLMPQReward(MapleCharacter chr) {
		MapleReward.giveReward(LMPQrewards, chr);
	}

	public static void giveLPQReward(MapleCharacter chr) {
		MapleReward.giveReward(LPQrewards, chr);
	}

	public static void giveKPQReward(MapleCharacter chr) {
		MapleReward.giveReward(KPQrewards, chr);
	}

	public static void giveZPQReward(MapleCharacter chr) {
		MapleReward.giveReward(ZPQrewards, chr);
	}

	public static void giveJQ0Reward(MapleCharacter chr) {
		MapleReward.giveReward(JQrewards0, chr);
	}

	public static void giveJQ1Reward(MapleCharacter chr) {
		MapleReward.giveReward(JQrewards1, chr);
	}

	public static void giveJQ2Reward(MapleCharacter chr) {
		MapleReward.giveReward(JQrewards2, chr);
	}

	public static void giveMissingMechJQReward(MapleCharacter chr) {
		MapleReward.giveReward(MissingMechJQrewards, chr);
	}
}
