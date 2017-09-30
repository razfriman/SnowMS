/*
@	Author : Raz
@
@	NPC = Nella
@	Map = Victoria Road <Kerning City>
@	NPC MapId = 103000000
@	Function = Start KPQ
@
*/

var minlvl = 0;
var maxlvl = 200;
var minplayers = 0;
var maxplayers = 6;
var open = true;


function start() {
    var em = cm.getEventManager("KerningPQ");
    var party;
    if (!cm.hasParty()) {
        cm.sendOk("How about you and your party members collectively beating a quest? Here you'll find obstacles and problems where you won't be able to beat it unless with great teamwork. If you want to try it, please tell the #bleader of your party#k to talk to me.");
    } else if (!cm.isLeader()) {
        cm.sendOk("If you want to try the quest, please tell the #bleader of your party#k to talk to me.");
    } else if (!cm.checkPartyLevels(minlvl, maxlvl)) {
        cm.sendOk("Please check that all your party members are between the levels of #b" + minlvl + "~" + maxlvl)
    } else if (!cm.checkPartySize(minplayers, maxplayers)) {
        cm.sendOk("Check that your party contains #b" + minplayers + "~" + maxplayers + " players#k. Please come back when you have four party members.");
    } else if (em == null) {
        cm.sendOk("I cannot let you in for an unknown reason. Please try again in a bit.");
    } else if (!open) {
        cm.sendOk("The Kerning-PQ has been closed");
        cm.dispose();
    } else {
        cm.sendOk("The Kerning-PQ Is open!");
        em.startInstance(cm.getParty(),cm.getChar().getMap());
        party = cm.getChar().getEventInstance().getPlayers();
        cm.removeFromParty(4001008, party);
        cm.removeFromParty(4001007, party);
        cm.dispose();
    }
}