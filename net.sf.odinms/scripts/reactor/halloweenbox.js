/*
 * This file is part of the OdinMS Maple Story Server
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

function act() {
        rm.mapMessage("Nothing happens...");
        //SCRIPT(Halloween_Box)
        var rn = Math.floor(Math.random()*10);
        if (rn == 0) {
            Random_Box0();
        } else if (rn == 1) {
            Random_Box1();
        } else if (rn == 2) {
            Random_Box2();
        } else if (rn == 3) {
            Random_Box3();
        } else if (rn == 4) {
            Random_Box4();
        } else if (rn == 5) {
            Random_Box5();
        } else if (rn == 6) {
            Random_Box6();
        } else if (rn == 7) {
            Random_Box7();
        } else if (rn == 8) {
            Random_Box8();
        } else if (rn == 9) {
            Random_Box9();
        }
}

function Random_Box0() {
    cm.spawnMonster(2101073, -1128, 305);
}

function Random_Box1() {
    cm.spawnMonster(2101073, -1128, 305);
}

function Random_Box2() {
    cm.spawnMonster(2101073, -1128, 305);
}

function Random_Box3() {
    cm.spawnMonster(2101073, -1128, 305);
}

function Random_Box4() {
    cm.spawnMonster(2101073, -1128, 305);
}

function Random_Box5() {
    cm.spawnMonster(2101073, -1128, 305);
}

function Random_Box6() {
    cm.spawnMonster(2101073, -1128, 305);
}

function Random_Box7() {
    cm.spawnMonster(2101073, -1128, 305);
}

function Random_Box8() {
    cm.spawnMonster(2101073, -1128, 305);
}

function Random_Box9() {
    cm.spawnMonster(2101073, -1128, 305);
}