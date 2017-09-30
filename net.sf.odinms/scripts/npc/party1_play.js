/*
@	Author : Raz
@
@	NPC = Cloto
@	Map = 1st Accompaniment <ALL Stages>
@	NPC MapId = 103000800 | 103000801 | 103000802 | 103000803 | 103000804
@	Function = Handle KPQ
@
*/

function start() {
    var eim = cm.getChar().getEventInstance();
    var mapid = cm.getMapId();
    if(eim == null) {
	  cm.sendOk("Please join the event legally");
	  cm.dispose();
    }

    if (eim.getProperty("stage") != null && eim.getProperty("stage") == "clear") {
	  party1_reward();
    }

    if (!cm.isLeader()) {
	  if (mapid == 103000800) {
		party1_personal();
	  } else {
		party1_help();
	  }
    } else {
	  if (mapid == 103000800) {
		party1_stage1();
	  } else if (mapid == 103000801) {
		party1_stage2();
	  } else if (mapid == 103000802) {
		party1_stage3();
	  } else if (mapid == 103000803) {
		party1_stage4();
	  } else if (mapid == 103000804) {
		party1_stage5();
	  }
    }
}

function action(mode, type, selection) {

    cm.sendNext("Hello. Welcome to the first stage. Look around and you'll see Ligators wandering around. When you defeat them, they will cough up a #bcoupon#k. Every member of the party other than the leader should talk to me, geta  question, and gather up the same number of #bcoupons#k as the answer to the question I'll give to them.\r\nIf you gather up the right amount of #bcoupons#k, I'll give the #bpass#k to that player. Once all the party members other than the leader gather up the #bpasses#k and give them to the leader, the leader will hand over the #bpasses#k to me, clearing the stage in the process. The faster you take care of the stages, the more stages you'll be able to challenge. So I suggest you take care of things quickly and swiftly. Well then, best of luck to you.");
					
    cm.sendNext("Please hurry on to the next stage, the portal opened!");
                        			
    cm.sendNext("I'm sorry, but you are short on the number of passes. You need to give me the right number of passes; it should be the number of members of your party minus the leader, " + strpasses + " to clear the stage. Tell your party members to solve the questions, gather up the passes, and give them to you.");
								
    cm.sendNext("You gathered up " + strpasses + "! Congratulations on clearing the stage! I'll make the portal that sends you to the next stage. There's a time limit on getting there, so please hurry. Best of luck to you all!");
					
    cm.sendNext("Here, you need to collect #bcoupons#k by defeating the same number of Ligators as the answer to the questions asked individually.");
			
    cm.sendNext("Please hurry on to the next stage, the portal opened!");
                        		
    cm.sendNext("That's the right answer! For that you have just received a #bpass#k. Please hand it to the leader of the party.");
						
    cm.sendNext("I'm sorry, but that is not the right answer! Please have the correct number of coupons in your inventory.");
						
    cm.sendNextPrev(questions[question]);
					
    cm.sendNext("Here's the portal that leads you to the last, bonus stage. It's a stage that allows you to defeat regular monsters a little easier. You'll be given a set amount of time to hunt as much as possible, but you can always leave the stage in the middle of it through the NPC. Again, congratulations on clearing all the stages. Take care...");
						
    cm.sendNext("Hello. Welcome to the 5th and final stage. Walk around the map and you'll be able to find some Boss monsters. Defeat all of them, gather up #bthe passes#k, and please get them to me. Once you earn your pass, the leader of your party will collect them, and then get them to me once the #bpasses#k are gathered up. The monsters may be familiar to you, but they may be much stronger than you think, so please be careful. Good luck!\r\nAs a result of complaints, it is now mandatory to kill all the Slimes! Do it!");
					
    cm.sendNext("Welcome to the 5th and final stage.  Walk around the map and you will be able to find some Boss monsters.  Defeat them all, gather up the #bpasses#k, and give them to your leader.  Once you are done, return to me to collect your reward.");
	
    cm.sendNext("Incredible! You cleared all the stages to get to this point. Here's a small prize for your job well done. Before you accept it, however, please make sure your use and etc. inventories have empty slots available.\r\n#bYou will not receive a prize if you have no free slots!#k");

    cm.sendNext("Invalid map, this means the stage is incomplete.");
     
    cm.sendNext("Please hurry on to the next stage, the portal opened!");
                        
    cm.sendNext(outstring);
                        
    cm.sendNext("It looks like you haven't found the 3 " + nthobj + " just yet. Please think of a different combination of " + nthobj + ". Only 3 are allowed to " + nthverb + " on " + nthobj + ", and if you " + nthpos + " it may not count as an answer, so please keep that in mind. Keep going!");
}


function party1_reward() {
    var eim = cm.getChar().getEventInstance();
    var rewards = MaplePQRewards.KPQrewards;
    MapleReward.giveReward(rewards, cm.getPlayer());
    var map = eim.getMapInstance(103000805);
    var portal = map.getPortal(0);
    cm.getPlayer().changeMap(map,portal);
}

function party1_personal() {
    var eim = cm.getChar().getEventInstance();

    var charName = cm.getPlayer().getName() + "_";
    var prob = eim.getProperty(charName);

    if (prob != null && prob == "clear") {

	  return;
    }

    var q = -1;
    var desc = "";
    var ans = -1;
    if (prob == null) {
	  q = Math.floor(Math.random() * 8);
    } else {
	  q = parseInt(prob);
    }

    if (q == 0) {
	  desc = "Here's the question. Collect the same number of coupons as the minimum level required to make the first job advancement as warrior.";

    } else if (q == 1) {
	  desc = "Here's the question. Collect the same number of coupons as the minimum amount of STR needed to make the first job advancement as a warrior.";
	  ans = 10;
    } else if (q == 2) {
	  desc = "Here's the question. Collect the same number of coupons as the minimum amount of INT needed to make the first job advancement as a magician.";
	  ans = 35;
    } else if (q == 3) {
	  desc = "Here's the question. Collect the same number of coupons as the minimum amount of DEX needed to make the first job advancement as a bowman.";
	  ans = 20;
    } else if (q == 4) {
	  desc = "Here's the question. Collect the same number of coupons as the minimum amount of DEX needed to make the first job advancement as a thief.";
	  ans = 25;
    } else if (q == 5) {
	  desc = "Here's the question. Collect the same number of coupons as the minimum level required to advance to 2nd job.";
	  ans = 30;
    }

    if (prob == null) {
	  eim.setProperty(charName, q);
	  cm.sendNext("Here, you need to collect #bcoupons#k by defeating the same number of Ligators as the answer to the questions asked individually.");
	  cm.sendNextPrev(desc);
	  return;
    }
	
    if (cm.itemQuantity(4001007) == ans) {
	  cm.removeAll(4001007);
	  cm.gainItem(4001008, 1);
	  eim.setProperty(charName, "clear");
	  cm.sendNext("That's the right answer! For that you have just received a #bpass#k. Please hand it to the leader of the party.");
    } else {
	  cm.sendNext("I'm sorry, but that is not the right answer! Please have the correct number of coupons in your inventory.");
    }
}

function party1_help() {
    var mapid = cm.getMapId();
    if (mapid == 103000801 ) {
	  cm.sendNext("Hi. Welcome to the 2nd stage. Next to me, you'll see a number of ropes. Out of these ropes, #b3 are connected to the portal that sends you to the next stage#k. All you need to do is have #b3 party members find the correct ropes and hang on them.#k\r\nBUT, it doesn't count as an answer if you hang on the ropes too low; please be near the middle of the ropes to be counted as a correct answer. Also, only 3 members of your party are allowed on the ropes. Once they are hanging on them, the leader of the party must #bdouble-click me to check and see if the answer's correct or not#k. Now, find the right ropes to hang on!");
    } else if (mapid == 103000802) {
	  cm.sendNext("Hi. Welcome to the 3rd stage. Next to me, you'll see a number of platforms. Out of these platforms, #b3 are connected to the portal that sends you to the next stage#k. All you need to do is have #b3 party members find the correct platforms and stand on them.#k\r\nBUT, it doesn't count as an answer if you stand too close to the edges; please be near the middle of the platforms to be counted as a correct answer. Also, only 3 members of your party are allowed on the platforms. Once they are standing on them, the leader of the party must #bdouble-click me to check and see if the answer's correct or not#k. Now, find the right platforms to stand on!");
    } else if (mapid == 103000803) {
	  cm.sendNext("Hi. Welcome to the 4th stage. Next to me, you'll see a number of barrels. Out of these barrels, #b3 are connected to the portal that sends you to the next stage#k. All you need to do is have #b3 party members find the correct barrels and stand on them.#k\r\nBUT, it doesn't count as an answer if you stand too close to the edges; please be near the middle of the barrels to be counted as a correct answer. Also, only 3 members of your party are allowed on the barrels. Once they are standinging on them, the leader of the party must #bdouble-click me to check and see if the answer's correct or not#k. Now, find the right barrels to stand on!");
    }
}

function party1_stage1() {
    var eim = cm.getPlayer().getEventInstance();
    var stage = eim.getProperty("stage");
    if (stage == null) {
	  eim.setProperty("stage", "1");
	  cm.sendOk();
	  return;
    }
    if (stage != "1") {
	  cm.sendOk();
	  return;
    }
    var users = eim.getPlayers().size() - 1; // exclude the leader of the party

    if (cm.itemQuantity(4001008) < users) {
	  cm.sendOk();
    } else {
	  cm.sendOk();
	  cm.removeAll(4001008);
	  cm.clear();
	  cm.effectObject("gate");
	  cm.lockPortal("next00", 0);
	  eim.setProperty("stage", "2");
	  cm.givePartyExp(100, eim.getPlayers());
    }
}

function party1_stage2() {
    if (check_stage("2", 1) == 0) {
	  return;
    }

    var eim = cm.getPlayer().getEventInstance();
    var question = eim.getProperty("ans2");

    if (question == null) {
	  eim.setProperty("ans2", cm.shuffleCombo("1 1 1 0"));
	  cm.sendOk();
	  return;
    }

    var answer = cm.checkAreas(2, question);

    if (answer == -1) {
	  cm.sendOk();
    } else if (answer == 0) {
	  cm.wrong();
    } else if (answer == 1) {
	  cm.clear();
	  cm.effectObject("gate");
	  cm.lockPortal("next00", 0);
	  eim.setProperty("stage", "3");
	  cm.givePartyExp(200, eim.getPlayers());
    }
}

function party1_stage3() {
    if (check_stage("3", 1) == 0) {
	  return;
    }

    var eim = cm.getPlayer().getEventInstance();
    var question = eim.getProperty("ans3");

    if (question == null) {
	  eim.setProperty("ans3", cm.shuffleCombo("1 1 1 0 0"));
	  cm.sendOk();
	  return;
    }

    var answer = cm.checkAreas(3, question);

    if (answer == -1) {
	  cm.sendOk();
    } else if (answer == 0) {
	  cm.wrong();
    } else if (answer == 1) {
	  cm.clear();
	  cm.effectObject("gate");
	  cm.lockPortal("next00", 0);
	  eim.setProperty("stage", "4");
	  cm.givePartyExp(400, eim.getPlayers());
    }
}

function party1_stage4() {
    if (check_stage("4", 1) == 0) {
	  return;
    }

    var eim = cm.getPlayer().getEventInstance();
    var question = eim.getProperty("ans4");

    if (question == null) {
	  eim.setProperty("ans4", cm.shuffleCombo("1 1 1 0 0 0"));
	  cm.sendOk();
	  return;
    }

    var answer = cm.checkAreas(3, question);

    if (answer == -1) {
	  cm.sendOk();
    } else if (answer == 0) {
	  cm.wrong();
    } else if (answer == 1) {
	  cm.clear();
	  cm.effectObject("gate");
	  cm.lockPortal("next00", 0);
	  eim.setProperty("stage", "5");
	  cm.givePartyExp(800, eim.getPlayers());
    }
}

function party1_stage5() {
    if (check_stage("5", 1) == 0) {
	  return;
    }

    var eim = cm.getPlayer().getEventInstance();
    if (cm.itemQuantity(4001008) < 10) {
	  cm.sendOk();
    } else {
	  cm.sendOk();
	  cm.clear();cm.effectObject("gate");
	  cm.lockPortal("next00", 0);
	  eim.setProperty("stage", "clear");
	  cm.givePartyExp(1500, eim.getPlayers());
	  cm.sendNext("Please hurry on to the next stage, the portal opened!");
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