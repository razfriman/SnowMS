/*
@	Author : Raz
@
@	NPC = 9103003 - Rolly
@	Map =  Ludibrium - <Exit of the Maze> [LEFT SIDE]
@	NPC MapId = 809050017
@	Function = LMPQ - Quit NPC
@
*/

function start() {
    cm.sendNext("Tough luck there. Hope you try again!");
    var count = cm.itemQuantity(4001007);
    if (count > 0) {
        cm.removeAll(4001007);
    }
    cm.warpRandom(220000000);
}