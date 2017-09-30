/*
@	Author : Raz
@
@	NPC = 9103001 - Rolly
@	Map =  Ludibrium - <Ludibrium>
@	NPC MapId = 220000000
@	Function = Start LMPQ
@
*/

var status = 0;
var minlvl = 0;
var maxlvl = 200;
var minplayers = 0;
var maxplayers = 6;
var time = 15;
var open = true;

function start() {
    if (!open) {
        cm.sendOk("Hi there! This is the entrance to the Ludibrium Maze but no one is allowed in at this point. I suggest you come back when the time is right.");
        cm.dispose();
    } else {
        var v1 = cm.sendSimple("This is the entrance to the Ludibrium Maze. Enjoy!\r\n#b#L0# Enter the Ludibrium Maze.#l\r\n#b#L1# What is the Ludibrium Maze?#l#k");
        if (v1 == 0) {
            if (!cm.isLeader()) {
                cm.sendOk("Try taking on the Maze Quest with your party. If you DO decide to tackle it, please have your Party Leader notify me!...");
                cm.dispose();
            }
            var em = cm.getEventManager("LudiMazePQ");
            if (v1 == 0) {//ENTER THE PQ
                if (!cm.hasParty()) {
                    cm.sendOk("Hmm...you're currently not affiliated with any party. You need to be in a party in order to tackle this maze.");
                } else if (!cm.checkPartySize(minplayers, maxplayers)) {
                    cm.sendOk("Your party needs to consist of at least 3 members in order to tackle this maze.");
                } else if (!cm.checkPartyLevels(minlvl, maxlvl)) {
                    cm.sendOk("One of the members of your party is not the required Level of 51 ~ 70. Please organize your party to match the required level.");
                } else if (em == null) {//EVENT ERROR
                    cm.sendOk("I cannot let you in for an unknown reason. Please try again in a bit.");
                } else {
                    em.startInstance(cm.getParty(), cm.getChar().getMap());
                    var party = cm.getChar().getEventInstance().getPlayers();
                    cm.removeFromParty(4001007, party);
                }
                cm.dispose();
            } else if(v1 == 1) {
                cm.sendOk("This maze is available to all parties of 3 or more members, and all participants must be between Level 51~70.  You will be given 15 minutes to escape the maze.  At the center of the room, there will be a Warp Portal set up to transport you to a different room.  These portals will transport you to other rooms where you'll (hopefully) find the exit.  Pietri will be waiting at the exit, so all you need to do is talk to him, and he'll let you out.  Break all the boxes located in the room, and a monster inside the box will drop a coupon.  After escaping the maze, you will be awarded with EXP based on the coupons collected.  Additionally, if the leader possesses at least 30 coupons, then a special gift will be presented to the party.  If you cannot escape the maze within the allotted 15 minutes, you will receive 0 EXP for your time in the maze.  If you decide to log off while you're in the maze, you will be automatically kicked out of the maze.  Even if the members of the party leave in the middle of the quest, the remaining members will be able to continue on with the quest.  If you are in critical condition and unable to hunt down the monsters, you may avoid them to save yourself.  Your fighting spirit and wits will be tested!  Good luck!");
                cm.dispose();
            }
        }
    }
}