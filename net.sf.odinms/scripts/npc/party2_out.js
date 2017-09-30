/*
@	Author : Raz
@
@	NPC = Sgt.Anderson
@	Map =  Abandoned Tower <Stage 1>
@	NPC MapId = 922010100
@	NPC Exit-MapId = 221024500
@
*/

function start() {
    if (cm.getMapId() == 922010000) {
        cm.getQuestRecord().remove(7011);
        cm.removeAll(4001022);
        cm.removeAll(4001023);
        cm.warp(221024500);
    } else {
        if (cm.sendYesNo("You will need to start from scratch if you want to risk it on this mission after leaving the stage. Are you sure you want to leave this map?")) {
            cm.getQuestRecord().remove(7011);
            if(cm.isLeader()) {
                var eim = cm.getPlayer().getEventInstance();
                eim.disbandParty();
                cm.removeFromParty(4001022, eim.getPlayers());
                cm.removeFromParty(4001023, eim.getPlayers());
            } else {
                eim.leftParty(cm.getPlayer());
                cm.removeAll(4001022);
                cm.removeAll(4001023);
            }
        } else {
            cm.sendOk("Understood. Add the strength of the members of your group and strives more!");
        }
    }
}