/*
@	Author : Raz
@
@	NPC = Amos the Strong
@	Map = Hidden Street <Amorian Challenge [Stage 1-3] >
@	NPC MapId = 670010200
@	Function = Handle APQ
@
*/

/* 
	Amos the Strong		     (9201043)-(9201044)-(9201045)-(9201046)-(9201048)
	Amos' Training Ground	     (670010000)
	Entrance of Amorian Challenge(670010100)
	Stage 1 - Magik Mirror	     (670010100)
	Stage 2 - Heart Strings	     (670010100)
	Stage 3 - Twisted Switcher   (670010100)
	Stage 4 - Last Man Standing  (670010100)
	Stage 5 - Fluttering Hearts  (670010100)
	Stage 6 - Love Hurts	     (670010100)
	Stage 7 - Amos' Vault	     (670010100)
*/


//STAGE 1 + 2 + 3
//4031596 - hammer
//4031595 - shard
importPackage(net.sf.odinms.tools);
importPackage(net.sf.odinms.server.life);
importPackage(java.awt);
importPackage(net.sf.odinms.server.pq);
importPackage(net.sf.odinms.client);

var curMap;
var preamble;
var party;
var mapId;
var gaveItems;
var summonedFairy;
var eim;

function start() {
    mapId = cm.getMapId();
    if (mapId == 670010200)
        curMap = 1;
    else if (mapId == 670010300)
        curMap = 2;
    else if (mapId == 670010301)
        curMap = 2;
    else if (mapId == 670010302)
        curMap = 2;
    else if (mapId == 670010400)
        curMap = 3;
    else if (mapId == 670010500)
        curMap = 4;
    else if (mapId == 670010600)
        curMap = 5;
    else if (mapId == 670010700)
        curMap = 7;

    preamble = null;
    eim = cm.getChar().getEventInstance();

    if(mapId == 670010200) { //Stage 1 - Magick Mirror
        var stage1 = eim.getProperty("stage1_clare");
        var val = eim.getProperty("stage1down");
        var hour = cm.getHour();
        var transmap;
        if (hour <= 8) transmap = eim.getMapInstance(670010302);// night
        else if(hour > 8 && hour <= 16) transmap = eim.getMapInstance(670010300);// day
        else if (hour > 16 && hour <= 24) transmap = eim.getMapInstance(670010301);// dusk

        if (stage1 == "1") { // upstairs    => 1st quest done
            cm.sendOk("Great job completing the first stage. I'll now take you to the second stage.");
        } else { // upstairs	 => if ( stage1 == "" )
            if (val == "ing") {
                cm.sendOk("Now now, you may want to think it over again. See which portal will open...");
                cm.effectObject("gate0");
                cm.effectObject("gate1");
                cm.effectObject("gate2");

                var rn = cm.random(0, 2);
                if (rn == 0) {
                    cm.blockPortal("go00", 0);
                    cm.blockPortal("go01", 1);
                    cm.blockPortal("go02", 1);
                } else if (rn == 0) {
                    cm.blockPortal("go01", 0);
                    cm.blockPortal("go01", 1);
                    cm.blockPortal("go02", 1);
                } else {
                    cm.blockPortal("go02", 0);
                    cm.blockPortal("go00", 1);
                    cm.blockPortal("go01", 1);
                }
            } else {
                if (cm.isPartyLeader()) {
                    cm.sendOk("Well, for the first part, I'd like you to meet a friend mine below, The Glimmer Man. He'll tell you more about how to move on! ");
                    cm.sendOk("In order for you to descend, you'll need to select one of 3 portals that are featured here. One important note: you'll really have to think and carefully choose the portal you'll want to use. Good luck.");
                    eim.setProperty("stage1down", "ing");
                    cm.dispose();
                } else {
                    cm.sendOk("Hey, I need the leader of your party to talk to me, no one else.");
                    cm.dispose();
                }
            }
        }
    } else if (mapId == 670010300 || 670010301 || 670010302) { //Stage 2 - Heart Strings - day/dusk/night
        var stage1 = eim.getProperty("stage1_clear");
        var stage2 = eim.getProperty("stage2_clear");
        //stage1 = "1";			// setting up previous quest
        if (stage2 == "") {
            if (cm.isPartyLeader()) {
                if (stage1 == "1") {
                    PartyAmoria_help(cm);
                    eim.setProperty("stage2_clear", "s");
                    cm.dispose();
                } else {
                    cm.sendOk("Please clear the mission first, and THEN talk to me.");
                    cm.dispose();
                }
            } else {
                PartyAmoria_help(cm);
            }
        } else if (stage2 == "s") {
            if (eim.getProperty("ans1") == "") {
                PartyAmoria_SMSQuizAns(eim, cm);
            }

            var area1 = field.countUserInArea ( "1" );
            var area2 = field.countUserInArea ( "2" );
            var area3 = field.countUserInArea ( "3" );

            if (area1 + area2 + area3 != 5) {
                cm.sendOk("You'll need to have 5 people hanging on the ropes.");
                cm.dispose();
            }

            var answer = eim.getProperty("ans1") + eim.getProperty("ans2") + eim.getProperty("ans3");
            if ( cm.getJob().getId() >= 500 ) {
                cm.sendOk("The answer is : " + answer);
            } else {
                var co = 0;
                if (area1 == parseInt(eim.getProperty("ans1"))) co++;
                if (area2 == parseInt(eim.getProperty("ans2"))) co++;
                if (area3 == parseInt(eim.getProperty("ans3"))) co++;

                if (co < 3) {
                    var nNum = parseInt(eim.getProperty("try")) + 1;
                    var tried = parseInt(eim.getProperty("try"));
                    tried++;
                    eim.setProperty("try", tried);
                    if (nNum == 6) {
                        cm.wrong();
                        if ( co == 0 ) {
                            cm.sendOk("This is your attempt number " + nNum + ".\r\n All these steps weigh different.\r\nYou have one attempt remaining, so please be careful.");
                        } else {
                            cm.sendOk("This is your attempt number " + nNum + ".\r\nAll " + co + " steps weigh the same.\r\nYou have one attempt remaining, so please be careful.");
                        }
                    } else if (nNum >= 7) {
                        cm.wrong();
                        eim.setProperty("try", "0");
                        eim.setProperty("stage2_clear", "");
                        mobSummon_3(cm);
                        cm.mapMessage(6, "You have failed at solving this. Consequently, a number of intimidating monsters have been summoned.");
                        cm.dispose();
                    } else {
                        cm.wrong();
                        if ( co == 0 ) {
                            cm.sendOk("This is your attempt number " + nNum + ".\r\nAll the steps weigh different." );
                        } else {
                            cm.sendOk("This is your attempt number " + nNum + ".\r\nAll " + co + " steps weigh the same." );
                        }
                    }
                    cm.dispose();
                } else {
                    if (cm.isPartyLeader()) {
                        cm.sendOk("That's the right answer. Here's the portal to the next stage. Good luck!");
                        cm.clear();
                        eim.setProperty("stage2_clear", "1");
                        cm.effectObject("gate");
                        cm.blockPortal("next00", 0);
                        cm.givePartyExp(4000, eim.getPlayers());
                    } else {
                        cm.sendOk("Hey, I need the leader of your party to talk to me, no one else.");
                    }
                }
            }
        } else if (stage2 == "1") {
            cm.sendOk("I don't think there's anything else to do here in this place. Please progress to the next stage.");
            cm.dispose();
        }
    } else if(curMap == 3) { //Stage 3 - Twisted Switcher
        //////////////////////////////@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
        var stage2 = eim.getProperty("stage2_clear");
        var stage3 = eim.getProperty("stage3_clear");
        //stage2 = "1";		// setting up previous quest

        if (stage3 == "") {
            if (cm.isPartyLeader()) {
                if (stage2 == "1") {
                    PartyAmoria_help(cm);
                    eim.setProperty("stage3_clear", "s");
                    var question = "";
                    var answer = "";
                    cm.dispose();
                } else {
                    cm.sendOk("Please clear the mission first, and THEN talk to me.");
                    cm.dispose();
                }
            } else {
                PartyAmoria_help(cm);
            }
        } else if (stage3 == "s") {
            question = eim.getProperty("answer3");
            if (question == "") {
                eim.setProperty("answer3", "TODO");
                eim.setProperty("question", eim.getProperty("answer3"));
            }

            answer = area_check(9, 5);
            var strike = 0;

            if (answer != "") {//PARTYGL LN536
                for(var i = 0; i < 9; i++) {

                }
            }
        }
    }
}

function PartyAmoria_help(cm) {
    if ( cm.getMapId() == 670010300 ) cm.sendOk("Oh, don't worry about the mirror, he has a bunch. This one is a little trickier. It's the 2nd stage. See those ropes with the same symbols under them? Good. Pick two of the right ropes and you can move on to the next area. If you pick the wrong one...well, just have your weapon ready. Remember, you all have to pick one rope! Picking two isn't recommended.For you to make your way up there, you'll see portals at the bottom of each of these flowers. Use them wisely, and you will be able to easily move your way up there. Good luck! ");
    else if ( cm.getMapId() == 670010301 ) cm.sendOk("Oh, don't worry about the mirror, he has a bunch. Time sure flies here, because the sun is setting as we speak. Now this one is a little trickier. It's the 2nd stage. See those ropes with the same symbols under them? Good. Pick two of the right ropes and you can move on to the next area. If you pick the wrong one...well, just have your weapon ready. Remember, you all have to pick one rope! Picking two isn't recommended. For you to make your way up there, you'll see portals at the bottom of each of these flowers. Use them wisely, and you will be able to easily move your way up there. Good luck! ");
    else if ( cm.getMapId() == 670010302 ) cm.sendOk("Oh, don't worry about the mirror, he has a bunch. Hmmm, look at this, it's already dark outside. Okay, now this one is a little trickier. It's the 2nd stage. See those ropes with the same symbols under them? Good. Pick two of the right ropes and you can move on to the next area. If you pick the wrong one...well, just have your weapon ready. Remember, you all have to pick one rope! Picking two isn't recommended. For you to make your way up there, you'll see portals at the bottom of each of these flowers. Use them wisely, and you will be able to easily move your way up there. Good luck! ");
    else if ( cm.getMapId() == 670010400 ) cm.sendOk("Bet you'll never forget the time again! HA! Ok, enough jokes. Welcome to the 3rd stage. As you can see, there are a few switches with different letters and numbers on them. The portal is only going to open when you have the right combination of 5. Shouldn't take more than a few tries for you geniuses. Hope to it!");
    else if ( cm.getMapId() == 670010500 ) cm.sendOk("Ok, this next part is sure to get your adrenaline pumping again... The exit door has three bolts on it. You will need 50 Cupid Code Fragments to unlock the door. There are three different kinds of monsters in this area. Wipe all kinds of a monster, and you should find a Cupid Code Fragment. Simple, right? Just one thing to keep in mind?the clock is ticking. Hop to it!");
    else if ( cm.getMapId() == 670010700 ) cm.sendOk("I must commend your strength, you have reached the final stage! Now, remember that Balrog I fought? Well, something weird happened afterwards. He transformed into something cunning and ferocious... a tough nut to crack. I had a lot of trouble luring him here! It's going to take effort and skill to bring him down. You must bring me his Geist Fang to complete the Amorian Challenge. When you're ready, head below. Good luck!");
    else if ( cm.getMapId() == 670010750 ) cm.sendOk("Ok, this next part is sure to get your adrenaline pumping again... The exit door has three bolts on it. You will need 100 Cupid Code Fragments to unlock the door. There are three different kinds of monsters in this area. Wipe all kinds of a monster, and you should find a Cupid Code Fragment. Simple, right? Just one thing to keep in mind?the clock is ticking. Hop to it!");
    else if ( cm.getMapId() == 670010800 ) cm.sendOk("Chance favors the swift weapon...you have one minute to grab everything you can! Go!" );
}

function PartyAmoria_SMSQuizAns(eim, cm) {
    var ans2_1 = cm.random(0, 5);
    var ans2_2 = cm.random(0, 5 - answ_1);
    var ans2_3 = 5 - ans2_1 - ans2_2;
    var rand_anw = cm.random(1, 6);
    if ( rand_anw == 1 ) {
        eim.setProperty("ans1", ans2_1);
        eim.setProperty("ans2", ans2_2);
        eim.setProperty("ans3", ans2_3);
    } else if ( rand_anw == 2 ) {
        eim.setProperty("ans1", ans2_1);
        eim.setProperty("ans3", ans2_2);
        eim.setProperty("ans2", ans2_3);
    } else if ( rand_anw == 3 ) {
        eim.setProperty("ans2", ans2_1);
        eim.setProperty("ans1", ans2_2);
        eim.setProperty("ans3", ans2_3);
    } else if ( rand_anw == 4 ) {
        eim.setProperty("ans2", ans2_1);
        eim.setProperty("ans3", ans2_2);
        eim.setProperty("ans1", ans2_3);
    } else if ( rand_anw == 5 ) {
        eim.setProperty("ans3", ans2_1);
        eim.setProperty("ans1", ans2_2);
        eim.setProperty("ans2", ans2_3);
    } else {
        eim.setProperty("ans3", ans2_1);
        eim.setProperty("ans2", ans2_2);
        eim.setProperty("ans1", ans2_3);
    }
}

function mobSummon(cm) {
    var field = cm.getMapId();
    cm.spawnMob(field, 2101043, 1120, 192);
    cm.spawnMob(field, 2101043, 1301, 215);
    cm.spawnMob(field, 2101043, 1290, 126);
    cm.spawnMob(field, 2101043, 1413, 239);
    cm.spawnMob(field, 2101043, 1467, 100);
    cm.spawnMob(field, 2101043, 1558, 256);
    cm.spawnMob(field, 2101043, 1726, 225);
}

function mobSummmon3(cm) {
    var field = cm.getMapId();
    cm.spawnMob(field, 2101044, -709, -1042);
    cm.spawnMob(field, 2101044, -709, -1042);
    cm.spawnMob(field, 2101044, -709, -1042);
    cm.spawnMob(field, 2101044, -709, -1042);
    cm.spawnMob(field, 2101044, -709, -1042);
    cm.spawnMob(field, 2101044, -709, -1042);
    cm.spawnMob(field, 2101044, -709, -1042);
    cm.spawnMob(field, 2101044, -709, -1042);
    cm.spawnMob(field, 2101044, -709, -1042);
    cm.spawnMob(field, 2101044, -709, -1042);
    cm.spawnMob(field, 2101044, -709, -1042);
    cm.spawnMob(field, 2101044, -709, -1042);
    cm.spawnMob(field, 2101044, -709, -1042);
    cm.spawnMob(field, 2101044, -709, -1042);
    cm.spawnMob(field, 2101044, -709, -1042);
    cm.spawnMob(field, 2101044, -709, -1042);
    cm.spawnMob(field, 2101044, -709, -1042);
    cm.spawnMob(field, 2101044, -709, -1042);
    cm.spawnMob(field, 2101044, -709, -1042);
    cm.spawnMob(field, 2101044, -709, -1042);
}

function mobSummon_2(strike, cm) {
    var field = cm.getMapId();
    var rn = cm.random(0, 3);
    var MobNum;

    if (rn == 0) {
        MobNum = 2101045;
    } else if (rn == 1) {
        MobNum = 2101046;
    } else if (rn == 2) {
        MobNum = 2101047;
    } else {
        MobNum = 2101048;
    }

    if (strike == 1) {
        cm.spawnMob(field, MobNum, 1715, -45);
    } else if (strike == 2) {
        cm.spawnMob(field, MobNum, 1571, -50);
        cm.spawnMob(field, MobNum, 1889, -37);
    } else if (strike == 3) {
        cm.spawnMob(field, MobNum, 1523, -34);
        cm.spawnMob(field, MobNum, 1664, -20);
        cm.spawnMob(field, MobNum, 1834, 7);
    } else if (strike == 4) {
        cm.spawnMob(field, MobNum, 1523, -34);
        cm.spawnMob(field, MobNum, 1664, -20);
        cm.spawnMob(field, MobNum, 1834, 7);
        cm.spawnMob(field, MobNum, 1993, 3);
    }
}