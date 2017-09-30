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

/* Konpei (9120015)
 * (Option to go back to Mushroom Shrine removed. Reason: There's a portal to get back now.)
  - Gives information about the Hideout.
  - Takes you to the Hideout.
  - Tells you off for being a nub.
*/

importPackage(net.sf.odinms.server.maps);

var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 1)
            status++;
        else
            status--;
        
            if (status == 0) {
                cm.sendSimple ("What do you want from me?\r\n#b#L0#Gather up some information on the hideout.#l\r\n#L1#Take me to the hideout.#l\r\n#L2#Nothing.#l#k");
            }
                        if (status == 1) {    
                            if (selection == 0) {
                              cm.sendNext("I can take you to the hideout, but the place is infested with thugs looking for trouble. You'll need to be both incredibly strong and brave to enter the premise. At the hideaway, you'll find the Boss that controls all the other bosses around this area. It's easy to get to the hideout, but the room on the top floor of the place can only be entered ONCE a day. The Boss's room is not a place to mess around. I suggest you don't stay there for too long; you'll need to swiftly take care of the business once inside. The Boss himself is a difficult foe, but you'll run into some incredibly powerful enemies on your way to meeting the boss! It ain't going to be easy.");
                               cm.dispose();
                              }
                            if (selection == 1) {
                                cm.sendNext("Oh, the brave one. I've been awaiting your arrival. If those thugs are left unchecked, there's no telling what's going to happen in thsi neighborhood. Before that happens, I hope you can take care of all of them and beat The Boss, who resides on the 5th floor. You'll need to be on alert at all times, since the boss is too tough for even the wisemen to handle. Looking at your eyes, however, I can see the eye of the tiger, the eyes that tell me you can do this. Let's go!");
                              }
                            if (selection == 2) {
                              cm.sendOk("I'm a busy person! Leave me alone if that's all you need!");
                              cm.dispose();
                           }
                            
                     }
                     if (status == 2) {
                         cm.warp(801040000, 1);
                         cm.dispose();
                     }
                 }
}  
