/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc> 
					   Matthias Butz <matze@odinms.de>
					   Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

importPackage(net.sf.odinms.server.life);
importPackage(java.awt);
/* @Author Lerk
 * 
 * 9201002.js: Guild Quest - Ergoth Reactor
 * 
*/

function act() {
	rm.changeMusic("Bgm10/Eregos");
	var mob = MapleLifeFactory.getMonster(9300028);
	mob.setBoss(false);//Only 1 Drop
	rm.getReactor().getMap().spawnMonsterOnGroundBelow(mob, rm.getReactor().getPosition());
	rm.spawnMonster(9300031, 130, 90);
	rm.spawnMonster(9300032, 540, 90);
	rm.spawnMonster(9300029, 130, 150);
	rm.spawnMonster(9300030, 540, 150);
}