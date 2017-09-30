/*
@	Author : Raz
@
@	NPC = 9103002 - Pietri
@	Map =  Ludibrium - <Ludibrium Maze 16>
@	NPC MapId = 809050015
@	Function = LMPQ - Finish NPC
@	Gives = Warp
@
*/

function start() {
    if (!cm.isLeader()) {
        cm.sendOk("Great job escaping the maze! Did you collect the coupons from the monsters standing in your way at the maze?");
        cm.sendOk("Depending on how many coupons your party gathered, there may be a surprise bonus!");
        cm.sendOk("Please tell #byour party leader#k to speak to me after gathering all the coupons from the party members.");
        cm.dispose();
    } else {
        var count = cm.itemQuantity(4001007);
        if (cm.sendYesNo("So you have gathered up #b" + count + " coupons#k with your collective effort. Are these all that your party has collected?")) {
            if (cm.sendYesNo("Great work! If you gather up 30 Maze Coupons, then you'll receive a cool prize! Would you like to head to the exit?")) {
                cm.removeFromParty(4001007);
                cm.givePartyExp(count * 50, eim.getPlayers());
                if (count > 30) {
                    eim.finishPQ();
                } else {
                    eim.disbandParty();
                }
            } else {
                cm.sendOk("I am guessing you'd like to collect more coupons. Let me know if you wish to enter the Exit stage.");
            }
        } else {
            cm.sendOk("Please check once more, and let me know when you're ready.");
        }
    }
    cm.sendNext("Great job escaping the maze! Did you collect the coupons from the monsters standing in your way at the maze?");
    if (cm.isLeader()) {
        if (cm.itemQuantity(4001007) >= 30) {
            cm.sendOk("Good job! you collected #b" + cm.itemQuantity(4001007) + " #t4001007#'s\r\n#kYou may pass on to get your reward");
            var eim = cm.getChar().getEventInstance();
            if (eim != null) {
                cm.givePartyExp((cm.itemQuantity(4001007)* 50), eim.getPlayers());
                eim.finishPQ();
            }
            cm.dispose();
        } else {
            cm.sendOk("Please go collect more coupons.\r\nyou need at least #b30 Coupons");
            cm.dispose();
        }
    } else {
        cm.sendPrev("Please tell #byour party leader#k to speak to me after gathering all the coupons from the party members.");
        cm.dispose();
    }

}