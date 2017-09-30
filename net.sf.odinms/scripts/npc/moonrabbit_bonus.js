/*
@	Author : Raz
@
@	NPC = Tommy
@	Map =  Hidden-Street <Shortcut>
@	NPC MapId = 910010100 | 910010200 | 910010300
@	Function = Warp out of PQ
@
*/


function start() {
    if (cm.getChar().getMap().getId() == 910010100) {
        if (cm.sendYesNo("Would you like to enter the #bBonus-Stage#k?")) {
            if (!cm.isLeader()) {
                cm.sendOk("Please tell your #bParty-Leader#k to come talk to me.");
                cm.dispose();
            } else {
                cm.sendOk("BLAH BLAH I NEED TO WARP TO BONUS");
                cm.dispose();
            }
        } else {
            cm.sendOk("Ok, see you later.")
            cm.dispose();
        }
    } else {
        cm.warpRandom(100000200);
        cm.dispose();
    }
}
