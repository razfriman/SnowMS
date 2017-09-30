/*
@	Author : Raz
@
@	NPC = Amos the Strong
@	Map = Hidden Street <Amos' Training Ground>
@	NPC MapId = 670010000
@	Function = Start APQ
@
*/
//ENTRANCE
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
var pqName = "Amoria-PQ";
var minlvl = 0;
var maxlvl = 200;
var minplayers = 0;
var maxplayers = 6;
var needBoy = false;
var needGirl = false;
var time = 30;//Minutes
var open = true;


function start() {
    var qr = cm.getQuestRecord();
    var val = qr.get(8883);
    var valT = qr.get(8884);

    if (cm.getMapId() == 670010000) {
        if (val != null && val == "end") {
            cm.sendOk("Do you have the ticket with you? Okay, now I'll take you to the entrance of the Amoria Party Quest. Your fellow members of the party should be there waiting for you!");
            if (cm.itemQuantity(4031592) > 0) {
                cm.gainItem(4031592, -1);
                cm.warp(670010100, "st00");
                qr.set(8883, null);
            } else {
                cm.sendOk("Don't you have the Entrance Ticket? Oh no. I'm sorry, but I'll have to ask you to reacquire 10 Lip Lock Keys and give them to me. Then, and only then, will I give you another ticket.");
            }
        } else if (val != null && val == "ing") {
            if (cm.getPlayer().isMarried()) {
                if (cm.getPlayer().getLevel() < 40) {
                    cm.sendOk("I see a fine fighting spirit in you, my friend. Sadly, it not fully developed. You'll need to be at least Level 40 to enter my Hunting ground!");
                } else {
                    cm.sendOk("I want you to gather 10 Lip Lock Keys to prove yourself worthy of entry. You might want to try hunting the Indigo Eyes, they seem to like the look of them. After that, I'll let you in to see what you're made of!");
                    if (cm.itemQuantity(4031593) < 10) {
                        cm.sendOk("Let's see, 1,2,3... not 10. My brother may be the wise one, but I'm no slouch either. You need 10 before I'll give you the Amorian Challenge Entrance Ticket.");
                    } else {
                        cm.gainItem(4031593, -10);
                        cm.gainItem(4031592, 1);
                        cm.sendOk("Ah! A worthy warrior and his party! Here's the Ticket. Good luck!");
                        qr.set(8883, "end");
                        qr.set(8884, "");//TODO: Current time
                    }
                }
            } else {
                cm.sendOk("I admire your bravery, however, you must be married to brave the dangers of the Amorian Challenge. When you get married, venture back and see me!");
            }
        } else {
            if (cm.sendYesNo("I am Amos the Strong! The warrior who once defeated a Balrog with nothing but my trusty sword'and wits! I have a challenge for your group should you be up for it! What do you say?")) {
                cm.sendOk("Stellar! Let me warn you-my challenges are not for those with weak weapons and puny minds! I built this hunting ground as a testament for those to protect their loved ones. To do this, you must be strong! I will put you to the test! Please talk to me again. ");
                qr.set(8883, "ing"); // TODO only set if time has passed
            } else {
                cm.sendOk("Can't say I blame you, friend. Come on back when you're good and strong, I'll be waiting.");
            }
        }
    }
}