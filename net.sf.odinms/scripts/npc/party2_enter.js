/*
@	Author : Raz
@
@	NPC = Red Sign
@	Map = Ludibrium <Eos Tower [101st Floor]>
@	NPC MapId = 221024500
@	Function = Start LPQ
@
*/

var minlvl = 0;
var maxlvl = 200;
var minplayers = 0;
var maxplayers = 6;
var time = 60;//Minutes
var open = true;


function start() {
    if (!cm.isLeader()) {
        cm.sendOk("From here on above, this place is full of dangerous objects and monsters, so I can't let you make your way up anymore. If you're interested in saving us and bring peace back into Ludibrium, however, that's a different story. If you want to defeat a powerful creature residing at the very top, then please gather up your party members. It won't be easy. but ... I think you can do it");
        cm.dispose();
    }
    var em = cm.getEventManager("LudiPQ");
    if (!cm.checkPartyLevels(minlvl, maxlvl)) {
        cm.sendOk("One of the members of your party is not the required Level of 35 ~ 50. Please organize your party to match the required level.");
    } else if (!cm.checkPartySize(minplayers, maxplayers)) {
        cm.sendOk("Your party needs to consist of at least 6 members in order to tackle this maze.");
    } else if (em == null) {
        cm.sendOk("I cannot let you in for an unknown reason. Please try again in a bit.");
    } else if (!open) {
        cm.sendOk("Hi there! This is the entrance to the Ludibrium Party-Quest but no one is allowed in at this point. I suggest you come back when the time is right.");
    } else {
        cm.sendOk("The Ludi-PQ Is #gopen!#k");
        em.startInstance(cm.getParty(), cm.getChar().getMap());
        var eim = cm.getChar().getEventInstance();
        var party = eim.getPlayers();
        cm.removeFromParty(4001022, party);
        cm.removeFromParty(4001023, party);
    }

}	