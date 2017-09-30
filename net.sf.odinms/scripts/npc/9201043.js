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
var status = 0;
var minlvl = 0;
var maxlvl = 200;
var minplayers = 0;
var maxplayers = 6;
var needBoy = false;
var needGirl = false;
var time = 30;//Minutes
var open = true;


function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	}else if (mode == 0){
		cm.sendOk("Please come talk to me when you are ready to go.");
		cm.dispose();
	} else {

		if (mode == 1)
			status++;
		else
			status--;
		
	    var em = cm.getEventManager("AmoriaPQ");

		if (status == 0) {
		cm.sendYesNo("I am #p" + cm.getNpc() + "#! The warrior who once defeated a Balrog with nothing but my trust sword'and wits! I have a challenge for your groupd shoul you be up for it! What do you say?");
		}else if (status == 1){
		cm.sendNext("Stellar! Let me warn you-my challenges are not for those with weak weapons and puny mins! I built this hunting ground as a testament for those to protect thier loved ones. To do this, you must be strong! O will put you to the test! Please talk to me again.");
		}else if (status == 2){
		if (cm.itemQuantity(4031592) > 0){
		cm.sendOk("Good, you have a #b#t4031592##k so lets go!");
		}else{
		cm.sendNext("I want you to gather 10 #t4031593#s to prove yourself worthy of entry. You might want to try hunting the Indigo Eyes, they seem to like the look of them. After that, I'll let you in to see what you're made of!");
		}
		}else if (status == 3){
			 if (!isMarried()){
				cm.sendNext("I admire your bravery, however, you must be married to brave the dnagers of the Amorian Challenge. When you get married, venture back and see me!");
				cm.dispose();
			}else if (cm.itemQuantity(4031592) > 0){
				cm.gainItem(4031592, -1);
				cm.warp(670010100);
				cm.dispose();
			}else if (cm.itemQuantity(4031593) < 10){
				cm.sendNextPrev("Let's see, 1,2,3... not 10. My brother may be the wise one, but I'm no slouch either. You need 10 before I'll give you the Amorian Challen Entrance Ticket");
				cm.dispose();
			}else{//CAN START EVENT		
				cm.sendNextPrev("Ah! A worthy warrior and his party! Here's the Ticket. \r\n Good luck");
				cm.gainItem(4031592, 1);
				cm.gainItem(4031593, -10);
				cm.dispose();
			}
			}
		}
	}


function isMarried(){
return true;
}