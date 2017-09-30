/*
@	Author : Raz
@
@	NPC = Growlie
@	Map = Hidden-Street <Primrose Hill>
@	NPC MapId = 910010000
@	Function = Handle HPQ
@
*/

function start() {
    var eim = cm.getChar().getEventInstance();
    
    if (eim != null && eim.getProperty("clear") == "1") {
        if (cm.isLeader()) {
            cm.sendOk("I'm just looking for more rice cakes! Have a good trip!")
            eim.finishPQ();
        } else {
            cm.sendOk("Please let me talk to the leader of your party");
            cm.dispose();
        }
    } else {
        var selection = cm.sendSimple("Growl! I am Growlie, always ready to protect this place. What brought you here?\r\n#b#L0# Please tell me what this place is all about.#l\r\n#L1# I have brought #t4001101#.#l\r\n#L2# I would like to leave this place.#l");
        if (selection == 0) { // PQ INFO
            cm.sendNext("This place can be best described as the prime spot where you can taste the delicious rice cakes made by Moon Bunny every full moon.");
            cm.dispose();
        } else if (selection == 1) { // GIVE THE CAKES
            if(cm.isLeader()) {
                cm.sendOk("Oh... isn't this rice cake made by Moon Bunny? Please hand me the rice cake.");
                cm.getPlayer().getMap().setProtectMobDamagedByMob(true);
                if (cm.itemQuantity(4001101) >= 10) {//PASS PQ
                    cm.sendOk("this is delicious. Please come see me next time for more #b#t4001101##k. Have a safe trip home!");
                    cm.clear();
                    cm.getPlayer().getMap().setSpawnEnabled(false);
                    cm.killAll();
                    
                    if (eim != null) {
                        var party = cm.getChar().getEventInstance().getPlayers();
                        cm.removeFromParty(4001095, party);
                        cm.removeFromParty(4001096, party);
                        cm.removeFromParty(4001097, party);
                        cm.removeFromParty(4001098, party);
                        cm.removeFromParty(4001099, party);
                        cm.removeFromParty(4001000, party);
                        cm.removeFromParty(4001101, party);
                        cm.givePartyExp(1600, eim.getPlayers());
                        eim.setProperty("clear", "1");
                    }
                    cm.dispose();
                } else {
                    cm.getPlayer().getMap().setProtectMobDamagedByMob(false);
                    cm.sendOk("I advise you to check and make sure that you have indeed gathered up #b10 #t4001101#s#k.");
                    cm.dispose();
                }

            } else {
                cm.sendOk("Tell your party-leader to come talk to me.");
                cm.dispose();
            }
        } else if (selection == 2) { // LEAVE PQ
            if (cm.sendYesNo("Are you sure you would like to leave?\r\nyour party needs you!")) {
                if (eim != null) {
                    if (cm.isLeader()) {
                        eim.disbandParty();
                    } else {
                        eim.leftParty(cm.getChar());
                    }
                }
                cm.dispose();
            } else {
                cm.sendOk("Ok, see you later");
                cm.dispose();
            }
        }
    }
}