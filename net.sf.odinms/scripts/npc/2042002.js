/*
@	Author : Raz
@
@	NPC = Spiegelmann
@	Map = Many
@	NPC MapId = Many
@	Function = Warp/Information about MCPQ
@
*/

var status = 0;
var option = 0;
var foption = 0;
var boption = 0;
var soption = 0;
var warrior1I = Array(1302004, 1402006, 1302009, 1402007, 1302010, 1402003, 1312006, 1412004, 1312007, 1412005, 1312008, 1312003);
var warrior2I = Array(1322015, 1422008, 1322016, 1422007, 1322017, 1422005, 1432003, 1442003, 1432005, 1442009, 1442005, 1432004);
var magicianI = Array(1372001, 1382018, 1372012, 1382019, 1382001, 1372007);
var bowmanI = Array(1452006, 1452007, 1452008, 1462005, 1462006, 1452007);
var thiefI = Array(1472013, 1472017, 1472021, 1332014, 1332031, 1332011, 1332016, 1332003);
var pirateI = Array(1482005, 1482006, 1852007, 1492005, 1492006, 1492007);
var warriorC = Array(7, 7, 10, 10, 20, 20, 7, 7, 10, 10, 20, 20);
var magicianC = Array(7, 7, 10, 10, 20, 20);
var bowmanC = Array(7, 10, 20, 7, 10, 20);
var thiefC = Array(7, 10, 20, 7, 10, 10, 20, 20);
var pirateC = Array(7, 10, 20,7, 10, 20);

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
//MODE = YES/NO
//TYPE = Last Message Type
//SELECTION = Selected Option

    if (mode == -1) {//ExitChat
		cm.dispose();

	} else if (mode == 0) {//No
		cm.dispose();

	} else {//Regular Talk
		if (mode == 1)
			status++;
		else
			status--;

        if (status == 0) {
            cm.sendSimple("What would you like to do? If you have never participated in the Monster Carnival, you'll need to know a thing or two about it before joining.\r\n" + "#b#L0# Go to the Monster Carnival Field.#l\r\n" + "#L1# Learn about the Monster Carnival.#l\r\n" + "#L2# Trade #t4001129#.#l");
		} else if (status == 1) {
            option = selection;
            if (option == 0) {
                if (cm.getLevel() > 50 || cm.getLevel() < 30) {
                    cm.sendOk("I'm sorry, but only the users within Level 30~50 may participate in Monster Carnival.");
                    cm.dispose();
                } else {
                    //save loc
                    //warp
                    cm.dispose();
                }
            } else if (option == 1) {
                cm.sendSimple("What do you want to do?\r\n" + "#b#L0# What's a Monster Carnival?#l\r\n" + "#L1# General overview of the Monster Carnival#l\r\n" + "#L2# Detailed instructions about the Monster Carnival#l\r\n" + "#L3# Nothing, really. I've changed my mind.#l");
            } else if (option == 2) {
                cm.sendSimple("Remember, if you have Maple Coins, you can trade them in for items. Please make sure you have enough Maple Coins for the item you want. Select the item you'd like to trade for! \r\n" + "#b#L0# #t1122007#(50 coins)#l\r\n" + "#L1# #t2041211#(40 coins)#l\r\n" + "#L2# Weapon for Warriors#l\r\n" + "#L3# Weapon for Magicians#l\r\n" + "#L4# Weapon for Bowmen#l\r\n" + "#L5# Weapon for Thieves#l\r\n" + "#L6# Weapon for Pirates#l");
            }
	 	} else if (status == 2) {
            if (option == 1) {
                foption = selection;
                if (foption == 0) {
                    cm.sendNext("Haha! I'm Spiegelmann, the leader of this traveling carnival. I started the 1st ever #bMonster Carnival#k here, waiting for travelers like you to participate in this extravaganza!");
                } else if (foption == 1) {
                    cm.sendNext("#bMonster Carnival#k consists of 2 parties entering the battleground, and hunting the monsters summoned by the other party. It's a #bcombat quest that determines the victor by the amount of Carnival Points (CP) earned#k.");
                } else if (foption == 2) {
                    cm.sendNext("Once you enter the Carnival Field, you'll see a Monster Carnival window appear. All you have to do is #bselect the ones you want to use, and press OK#k. Pretty easy, right?");
                } else if (foption == 3) {
                    cm.dispose();
                }
            } else if (option == 2) {
             boption = selection;
             if (boption == 0 || boption == 1) {
                 //Necklace Garbage
             } else if (boption == 2) {

             } else if (boption == 3) {

             } else if (boption == 4) {

             } else if (boption == 5) {

             } else if (boption == 6) {

             }
            }
        } else if (status == 3) {
            if (option == 1) {
                if (foption == 1) {

                } else if (foption == 1) {

                } else if (foption == 2) {

                }
            } else if (option == 2) {
                soption = selection;
                if (boption == 2) {
                    if (soption == 12) {

                    } else {

                    }
                } else {///

                }
            }
        } else if (status == 4) {
            if (option == 1) {
                if (foption == 0) {

                } else if (foption == 1) {

                } else if (foption == 2) {

                }
            } else if (option == 2) {
                if (boption == 2) {
                   soption = selection;
                   if (soption == 12) {

                   } else {

                   }
                }
            }
        } else if (status == 5) {
            if (option == 1) {
                if (foption == 0) {
                    cm.sendNextPrev("Of course, it's not as simple as that. There are different ways to prevent the other party from hunting monsters, and it's up to you to figure out how. What do you think? Interested in a little friendly (or not-so-friendly) competition?");
                } else if (foption == 1) {
                    cm.sendNextPrev("Please remember this, though. It's never a good idea to save up CP just for the sake of it. #bThe CP's you've used will also help determine the winner and the loser of the carnival#k.");
                } else if (foption == 2) {
                    cm.sendNextPrev("#bSkill#k is an option of using skills such as Darkness, Weakness, and others to prevent the opposing party from defeating the monsters. It requires a lot of CP, but it's well worth it. The only problem is that it doesn't last that long. Use this tactic wisely!");
                }
            }
        } else if (status == 6) {
            if (option == 1) {
                if (foption == 2) {

                } else {
                    cm.sendSimple("What do you want to do?\r\n" + "#b#L0# What's a Monster Carnival?#l\r\n" + "#L1# General overview of the Monster Carnival#l\r\n" + "#L2# Detailed instructions about the Monster Carnival#l\r\n" + "#L3# Nothing, really. I've changed my mind.#l");
                }
            }
        } else if (status == 7) {
            if (option == 1) {
                cm.sendNextPrev("Lastly, while you're in the Monster Carnival, #byou cannot use the recovery items/potions that you carry around with you.#k However, the monsters will drop those items every once in a while, and #bas soon as you pick it up, the item will activate immediately#k. That's why it's just as important to know WHEN to pick up those items.");
                //setstate 1
            }
        } else if (status == 8) {
            if (option == 1) {
               cm.sendSimple("What do you want to do?\r\n" + "#b#L0# What's a Monster Carnival?#l\r\n" + "#L1# General overview of the Monster Carnival#l\r\n" + "#L2# Detailed instructions about the Monster Carnival#l\r\n" + "#L3# Nothing, really. I've changed my mind.#l");
               //setstate 1
            }
        }
    }
}