/*
@	Author : Raz
@
@	NPC = Amos the Strong
@	Map = Hidden Street <BONUS STAGE> (Amos' Vault) [NOT COUPLES]
@	NPC MapId = 670010800
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


//Stage 7 (BONUS)

function start() {
    if (cm.sendYesNo("Would you like to leave the bonus?")) {
		if(cm.isLeader()) {
            cm.sendOk("Ok, Your loss");
            var map = cm.getMap(670010100);
    		var party = eim.getPlayers();
    		cm.warpMembers(map, "st00", party);
    		//STOP TIMER
    		//WARP PLAYERS OUT OF BONUS
    		cm.dispose();
		} else {
            cm.sendOk("Ask your #bParty-Leader#k to come talk to me.");
            cm.dispose();
		}
    } else {
        cm.sendOk("Wise choice, who wouldn't want free mesos and some items from the #bBonus-Stage#k.");
        cm.dispose();
    }
}