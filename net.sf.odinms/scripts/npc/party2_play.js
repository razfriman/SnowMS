/*
@	Author : Raz
@
@	NPC = Arturo
@	Map =  Hidden-Street <Abandoned Tower <Determine to adventure> >
@	NPC MapId = 922011100
@	Function = Warp out of LPQ + Give Reward
@
*/

importPackage(net.sf.odinms.server.pq);
importPackage(net.sf.odinms.client);

function start() {
    var mapid = cm.getMapId();

    if (mapid == 922011100) {
        party2_reward();
    }

    if (!cm.isLeader()) {
        party2_help();
    } else {
        if (mapid == 922010100) {
            party2_stage1();
        } else if (mapid == 922010200) {
            party2_stage2();
        } else if (mapid == 922010300) {
            party2_stage3();
        } else if (mapid == 922010400) {
            party2_stage4();
        } else if (mapid == 922010500) {
            party2_stage5();
        } else if (mapid == 922010600) {
            party2_stage6();
        } else if (mapid == 922010700) {
            party2_stage7();
        } else if (mapid == 922010800) {
            party2_stage8();
        } else if (mapid == 922010900) {
            party2_stage9();
        } else if (mapid == 922011000) {
            party2_help();
        }
    }
}

function party2_reward() {
    if (cm.sendYesNo("Congratulations, would you like to collect your reward?")) {
        var eim = cm.getChar().getEventInstance();
        var rewards = MaplePQRewards.LPQrewards;
        if(eim != null){
            cm.giveReward(rewards, cm.getChar());
            eim.leftParty(cm.getChar());
        } else {
            cm.warp(922010000);
        }
    } else {
        cm.sendOk("Ok, come talk to me again, when you want your reward");
    }
}

function party2_stage1() {
    var eim = cm.getPlayer().getEventInstance();
    var stage = eim.getProperty("stage");
    if (stage == null) {
        eim.setProperty("stage", "1");
        cm.sendOk("Hello! Welcome to the 1st stage. Move the map and find different types of monsters wandering the site. Defeat all, collect #b25 #t4001022#s#k bring to me. Join the #t4001022#s collected and delivered to the leader of his group, which, in turn, deliver to me. You may be familiar with these monsters, but they can be more powerful than expected. So be careful!");
        return;
    }
    if (stage != "1") {
        cm.sendOk("Wow! Congratulations on completing the tasks of this training. Please use the portal you see ahead and proceed to the next stage. Good luck to you!");
        return;
    }

    if (cm.itemQuantity(4001022) < 25) {
        cm.sendOk("Hello! Welcome to the 1st stage. Move the map and find different types of monsters wandering the site. Defeat all, collect #b25 #t4001022#s#k bring to me. Join the #t4001022#s collected and delivered to the leader of his group, which, in turn, deliver to me. You may be familiar with these monsters, but they can be more powerful than expected. So be careful!");
    } else {
        cm.sendOk("Good job defeating all monsters and collecting #b25 #t4001022#s#k. Very impressive!");
        cm.removeAll(4001022);
        cm.clear();
        cm.effectObject("gate");
        cm.lockPortal("next00", 0);
        eim.setProperty("stage", "2");
        cm.givePartyExp(3000, eim.getPlayers());
        cm.sendOk("Hurry, go to the next stage, the portal is open!");
        return;
    }
}

function party2_stage2() {
    if (check_stage("2", 1) == 0) {
        return;
    }

    var eim = cm.getPlayer().getEventInstance();

    if (cm.itemQuantity(4001022) < 15) {
        cm.sendOk("Hello! Welcome to the 2nd stage. Move around the map and find the boxes throughout the site. Break a box and you will be sent to another map or rewarded with a #t4001022#. Look at each box, collect #b15 #t4001022#s#k to bring to me. Join the #t4001022#s collected and deliver them to the leader of your party, which, in turn, will deliver them to me. Good luck!");
    } else {
        cm.sendOk("Good job defeating all monsters and collecting #b15 #t4001022#s#k. Very impressive!");
        cm.removeAll(4001022);
        cm.clear();
        cm.effectObject("gate");
        cm.lockPortal("next00", 0);
        eim.setProperty("stage", "3");
        cm.givePartyExp(3600, eim.getPlayers());
        cm.sendOk("Hurry, go to the next stage, the portal is open!");
    }
}

function party2_stage3() {
    if (check_stage("3", 1) == 0) {
        return;
    }

    var eim = cm.getPlayer().getEventInstance();

    if (cm.itemQuantity(4001022) < 32) {
        cm.sendOk("Hello! Welcome to the 3rd stage. Here you'll see a bunch of monsters and boxes. If you defeat the monsters, they will deliver #b#t4001022##k, equal to the monsters from another dimension. If you break the box, a monster will appear and he will also drop #b#t4001022##k. \r\nThe number of #b#t4001022#s#k you need to collect will be determined by the answer that will lead to your party. The answer to that question will determine the number of #b#t4001022#s#k you will need to collect. So I do question the leader of the party, he can discuss the answers with the members. There is the question. Fill in the blanks? \r\n\r\n#b The HP of a Character, Level 1 (minimum level required to advance in the career of the magician (the minimum level necessary to make progress in their careers as thiev is = ? # k");
    } else {
        cm.sendOk("Good job defeating all monsters and collecting #b32 #t4001022#s#k. Very impressive!");
        cm.removeAll(4001022);
        cm.clear();
        cm.effectObject("gate");
        cm.lockPortal("next00", 0);
        eim.setProperty("stage", "4");
        cm.givePartyExp(4200, eim.getPlayers());
        cm.sendOk("Hurry, go to the next stage, the portal is open!");
    }
}

function party2_stage4() {
    if (check_stage("4", 1) == 0) {
        return;
    }

    var eim = cm.getPlayer().getEventInstance();

    if (cm.itemQuantity(4001022) < 6) {
        cm.sendOk("Hello! Welcome to the 4th stage. Here you will find a space created by the black slit dimensional. Inside, you will find a monster called #b#o9300008##k hidden in darkness. For that, you can barely see it even with eyes wide open. Defeat the monsters and collect #b6 #t4001022#s#k. \r\nThe leader of your party must collect all #b#t4001022#s#k. Like I said, #b#o9300008##k can't be seen, unless its eyes are open. It is a different type of monster that melts quietly into the darkness. Good luck!");
    } else {
        cm.sendOk("Good job defeating all monsters and collecting #b6 #t4001022#s#k. Very impressive!");
        cm.removeAll(4001022);
        cm.clear();
        cm.effectObject("gate");
        cm.lockPortal("next00", 0);
        eim.setProperty("stage", "5");
        cm.givePartyExp(4800, eim.getPlayers());
        cm.sendOk("Hurry, go to the next stage, the portal is open!");
    }
}

function party2_stage5() {
    if (check_stage("5", 1) == 0) {
        return;
    }

    var eim = cm.getPlayer().getEventInstance();

    if (cm.itemQuantity(4001022) < 24) {
        cm.sendOk("Hello! Welcome to the 5th stage. Here you will find many areas and, within them, will find some monsters. Your duty is to collect the group #b24 #t4001022#s#k. This is the explanation: There will be cases where you need to be of a particular profession or you can not collect #b#t4001022##k. So be careful. Here's a clue. There is a monster called #b#o9300013##k that is unbeatable. Only a thief can reach the other side of the monster. There is also a route that only magicians can take. Discover it with you. Good luck!");
    } else {
        cm.sendOk("Good job defeating all monsters and collecting #b24 #t4001022#s#k. Very impressive!")
        cm.removeAll(4001022);
        cm.clear();
        cm.effectObject("gate");
        cm.lockPortal("next00", 0);
        eim.setProperty("stage", "7");
        cm.givePartyExp(5400, eim.getPlayers());
        cm.sendOk("Hurry, go to the next stage, the portal is open!");
    }
}

function party2_stage6() {
    var qr = cm.getQuestRecord();
    var val = qr.get(7011);
    var eim = cm.getPlayer().getEventInstance();

    if (eim.getPlayers().size == cm.getPlayer().getMap().getPlayerCount()) {
        cm.sendOk("I think that not all members of your group are present. You will need to bring each member of the old stage to continue. Please, find members who are missing.");
        return;
    }

    if (val == null) {
        qr.set(7011, "1");
        cm.sendOk("Hello! Welcome to the 6th stage. Here, you'll see boxes with numbers written, and if you stay on top of the box correct pressing the UP ARROW, you carry to the next box. I will give the leader of the party a clue about how to pass this stage #bonly twice#k it is the duty of the leader pointed out to the runway and the right step, one at a time. \r\nOnce you reach the top, you will find the portal to the next stage. When all of your group have passed the portal, the stage is completed. Everything will depend to remember the correct boxes. There goes the runway. Do not miss! \r\n\r\n #b A, 3, 3, 2, half, 1 three, 3, 3, left, two, 3, 1, a,?#k");
        
    } else if (val == "1") {
        qr.set(7011, "e");
        cm.sendOk("Hello! Welcome to the 6th stage. Here, you'll see boxes with numbers written, and if you stay on top of the box correct pressing the UP ARROW, you carry to the next box. I will give the leader of the party a clue about how to pass this stage #bonly twice#k it is the duty of the leader pointed out to the runway and the right step, one at a time. \r\nOnce you reach the top, you will find the portal to the next stage. When all of your group have passed the portal, the stage is completed. Everything will depend to remember the correct boxes. There goes the runway. Do not miss! \r\n\r\n #b A, 3, 3, 2, half, 1 three, 3, 3, left, two, 3, 1, a,?#k");
    } else {
        cm.sendOk("Hello! Welcome to the 6th stage. Here, you'll see boxes with numbers written, and if you stay on top of the box correct pressing the UP ARROW, you carry to the next box. I will give the leader of the party a clue about how to pass this stage #bonly twice#k it is the duty of the leader pointed out to the runway and the right step, one at a time. \r\nOnce you reach the top, you will find the portal to the next stage. When all of your group have passed the portal, the stage is completed. Everything will depend to remember the correct boxes. I gave a track #btwice#k more can not help you from there. Good luck!");
    }
}

function party2_stage7() {
    if (check_stage("7", 1) == 0) {
        return;
    }

    var eim = cm.getPlayer().getEventInstance();

    if (cm.itemQuantity(4001022) < 3) {
        cm.sendOk("Hello! Welcome to the 7th stage. Here you will find a ridiculously powerful monster called #b#o9300010##k. Defeat the monster and find #b#t4001022##k to proceed to the next stage. Please collect #b3 #t4001022#s#k. \r\nTo end the monster, defeat it by far. The only way to tackle it from a long distance, but ... ah, yes, be careful #o9300010# is very dangerous. Sure you will get hurt if not careful. Good luck!");
    } else {
        cm.sendOk("Good job defeating all monsters and collecting #b3 #t4001022#s#k. Very impressive!")
        cm.removeAll(4001022);
        cm.clear();
        cm.effectObject("gate");
        cm.lockPortal("next00", 0);
        eim.setProperty("stage", "8");
        cm.givePartyExp(6600, eim.getPlayers());
        cm.sendOk("Hurry, go to the next stage, the portal is open!");
    }
}

function party2_stage8() {
    if (check_stage("8", 1) == 0) {
        return;
    }

    var eim = cm.getPlayer().getEventInstance();
    var question = eim.getProperty("ans");
    if (question == null) {
        eim.setProperty("ans", cm.shuffleCombo("1 1 1 1 1 0 0 0 0"));
        cm.sendOk("Welcome to the 8th stage. Here you will find many platforms for climbing. #b5#k of them are connected to the #bportal that leads to the next stage#k. To pass, put #b5 members of your party on the correct platform#k. \r\nA warning: You will need to stay firm in the center of the platform to count as a valid answers. Remember also that only 5 members may be on the platform. When this happens, the leader of the group should #bclick twice on me to see if the answer is correct or not#k. Good luck!");
        return;
    }

    if (cm.getPlayer().isGM()) {
            cm.sendOk("The combo is: " + question);
    }

    var answer = cm.checkAreas(5, question);

    if (answer == -1) {
        cm.sendOk("I think you have not met the 5 correct platforms. Think of a different number. Remember that you must have 5 members of his group on the platform in the center, so the answer is valid. Keep trying!");    
    } else if (answer == 0) {
        cm.wrong();
    } else if (answer == 1) {
        cm.clear();
        cm.effectObject("gate");
        cm.lockPortal("next00", 0);
        eim.setProperty("stage", "9");
        cm.givePartyExp(7200, eim.getPlayers());
        cm.sendOk("Hurry, go to the next stage, the portal is open!");
    }
}

function party2_stage9() {
    if (check_stage("9", 1) == 0) {
        return;
    }

    var eim = cm.getPlayer().getEventInstance();

    if (cm.itemQuantity(4001023) < 1) {
        cm.sendOk("You managed to get here. Now is your chance to finally put your hands in the true culprit. Go to the right and you'll see a monster. Defeat it to find a monstrous #b#o9300012##k appearing from nowhere. He will be very agitated by the presence of their group, be careful. \r\n Your task is to defeat him, collect the #b#t4001023##k and he has to bring to me, If you can get the key to the monster, there is no way to be a dimensional door opens again. I have faith in you. Good luck!");
    } else {
        cm.sendOk("Good job defeating all monsters and collecting #b#t4001022##k. Very impressive!");
        cm.removeAll(4001022);
        cm.clear();
        cm.effectObject("gate");
        eim.setProperty("stage", "clear");
        cm.givePartyExp(8500, eim.getPlayers());
        var map = eim.getMapInstance(922011000);
        var portal = map.getPortal("st00");
        party = eim.getPlayers();
        cm.warpMembers(map, portal, party);
        cm.getChar().getEventInstance().schedule("startBonus", (1 * 60000));
        cm.getChar().getMap().broadcastMessage(net.sf.odinms.tools.MaplePacketCreator.getClock(60));
        cm.sendOk("Hurry, go to the next stage, the portal is open!");
    }
}

function party2_help() {
    var mapid = cm.getMapId();
    if (mapid == 922010100) {
        cm.sendOk("Here is information on the 1st stage. You will see monsters in different parts of the map. These monsters have an item called #b#t4001022##k, which opens the entrance to another dimension. With it, you can take a step closer to the top of the Eos Tower, where the door to another dimension will open, and finally you will find to blame for everything. \r\nDefeat the monsters, collect ##a25 t4001022#s#k delivered to the leader of his group, which, in turn, deliver to me. This will take you to the next stage. Good luck!");
    } else if (mapid == 922010200) {
        cm.sendOk("Here is information on the 2nd stage. You'll see boxes around the map. Break a box and you will be sent to another map or rewarded with a #t4001022#. Look at each box, collect #b15 #t4001022#s#k all to bring me. Join the #t4001022#s collected, all delivered to the leader of his group, which in turn will deliver me. \r\nMoreover, even if you are sent to another place, another box will find there. For this, simply do not leave the place where it was strange to teleport if you just get out, may not need to go back and start the mission from the beginning. Good luck!");
    } else if (mapid == 922010300) {
        cm.sendOk("Here is information on the 3rd stage. Here you'll see a bunch of monsters and boxes. If you defeat the monsters, they will deliver #b#t4001022##k, equal to the monsters from another dimension. If you break the box, a monster will appear and he will also #b#t4001022##k. \r\nThe number of #b#t4001022#s#k you need to collect will be determined by the answer of the question that will lead to the your group. The answer to that question will determine the number of #b#t4001022#s#k you will need to collect. So I do question the leader of the group, he can discuss the answers with the members. Good luck!");
    } else if (mapid == 922010400) {
        cm.sendOk("Here is information on the 4th stage. Here you will find a space created by the black slit dimensional. Inside, you will find a monster called #b#o9300008##k hidden in darkness. For that, you can barely see it if not with the eyes open. Defeat the monsters and collect #b6 #t4001022#s#k. \r\n As I said, #b#o9300008##k can not be seen, unless her eyes are wide open. It is a different type of monster that melts quietly into the darkness. Good luck!");
    } else if (mapid == 922010500) {
        cm.sendOk("Here is information on the 5th stage. Here you will find many areas and, within them, will find some monsters. Your duty is to collect the group #b24#t4001022#s#k. This is the description: There will be cases where you need to be of a particular profession, or may not collect #b#t4001022##k. So be careful. Here's a clue. There is a monster called #b#oo9300013##k that is unbeatable. Only a thief can reach the other side of the monster. There is also a route that only witches can take. Discover it with you. Good luck!");
    } else if (mapid == 922010600) {
        cm.sendOk("Here is information on the 6th stage. Here, you'll see boxes with numbers written, and if you stay on top of the box correct pressing the UP ARROW, will move to the next box. I will give the group leader of a clue about how to pass this stage twice #b and it is the duty of the leader#k pointed out to the runway and the right step, one at a time. \r\nOnce you reach the top, you will find the portal to the next stage. When all of your group have passed the portal, the stage is completed. Everything will depend to remember the correct boxes. Good luck!");
    } else if (mapid == 922010700) {
        cm.sendOk("Here is information on the 7th stage. Here you will find a ridiculously powerful monster called #b#o9300010##k. Defeat the monster and find #b#t4001022##k to proceed to the next stage. Please collect #b3#t4001022#s#k. \r\nTo stop the monster, defeat it by far. The only way to tackle it from a long distance, but ... ah, yes, be careful #o9300010# is very dangerous. Sure you will get hurt if not careful. Good luck!");
    } else if (mapid == 922010800) {
        cm.sendOk("Here is information on the 8th stage. Here you will find many platforms for climbing. #b5#k of them are connected to the #bportal that leads to the next stage#k. To pass, put #b5 members of his group in the correct platform#k. \r\nA warning: You will need to stay firm in the center of the platform to count as correct answers. Remember also that only 5 members may be in the platform. When this happens, the leader of the group should #bclick twice on me to see if the answer is correct or not#k. Good luck!");
    } else if (mapid == 922010900) {
        cm.sendOk("Here is information on the 9th stage. Now is your chance to finally put their hands in the true culprit. Go to the right and you'll see a monster. Defeat it to find a monstrous #b#o9300012##k appearing from nowhere. He will be very agitated by the presence of their group, be careful. \r\nYour task is to defeat him, collect the #b#t4001023##k and he has to bring to me. If you can get the key to the monster, there is no way to be a dimensional door opens again. I have faith in you. Good luck!");
    } else if (mapid == 922011000) {
        cm.sendOk("Welcome to the bonus stage. I can not believe you really defeated the #b#o9300012##k! Amazing! But we do not have much time, so I will just point to. There are many boxes here. Your task is to break the boxes within a time limit and get the items from it. If you are lucky, you can even take off a great item here and there. If this does not excite you, I do not know what will. Good luck!");
    }
}

function check_stage(st, checkall) {
    var eim = cm.getPlayer().getEventInstance();
    var stage = eim.getProperty("stage");
    if (stage == null || stage != st) {
        cm.sendOk("Hurry, go to the next stage, the portal is open!");
        return 0;
    }

    if (checkall == 1 && eim.getPlayers().size() != cm.getPlayer().getMap().getPlayerCount()) {
        cm.sendOk("I think that not all members of your group are present. You will need to bring each member of the old stage to continue. Please, find members who are missing.");
        return 0;
    }
    return 1;
}