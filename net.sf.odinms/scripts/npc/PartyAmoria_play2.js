/*
@	Author : Raz
@
@	NPC = The Glimmer Man
@	Map =  Hidden Street <Exit>
@	NPC MapId = 670011000
@	NPC Exit-MapId = 670010000
@
*/

function start() {
    if(cm.getMapId() == 670011000) {
        cm.sendNext("Goodbye! see you next time");
        cm.warp(670010000);
        cm.dispose();
    } else {
        if (cm.sendYesNo("Are you sure you want to leave the Quest?")) {
            cm.sendNext("Ok, Bye!");
            var eim = cm.getChar().getEventInstance();
            if(eim == null){
                cm.sendOk("Wait, Hey! how'd you get here?\r\nOh well you can leave anyways");
                cm.warp(670010000);
                cm.removeAll(4001022);
                cm.dispose();
            } else {
                if(cm.isLeader()){
                    eim.disbandParty();
                    cm.removeFromParty(4001008, eim.getPlayers());//CHANGE ITEM ID HERE
                } else {
                    eim.leftParty(cm.getChar());
                    cm.removeAll(4001022);//CHANGE ITEM ID HERE
                }
                cm.dispose();
            }
        }
    }
}