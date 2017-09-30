/*
@	Author : Raz
@
@	NPC = 
@	Map = 
@	NPC MapId = 
@	Function = Start PQ
@
*/
var pqName = "PQ-NAME";
var status = 0;
var minlvl = 0;
var maxlvl = 200;
var minplayers = 0;
var maxplayers = 6;
var time = 10;//Minutes
var open = false;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	}else if (mode == 0){
		cm.dispose();
	}else{

		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {

			var em = cm.getEventManager("EVENT-NAME");
			var party;

			 if (hasParty() == false) {//NO PARTY
				cm.sendOk("Would you like to try out the #b" + pqName + "#k then please create a party first");
				cm.dispose();
			} else if (isLeader() == false) {//NOT LEADER
				cm.sendOk("If you want to try the quest, please tell the #bleader of your party#k to talk to me.");
				cm.dispose();
                        } else if (checkPartyLevels() == false) {//WRONG LEVELS
				cm.sendOk("Please check that all your party members are between the levels of #b" + minlvl + "~" + maxlvl)
				cm.dispose();
			} else if (checkPartySize() == false) {//PARTY SIZE WRONG
				cm.sendOk("Check that your party contains #b" + minplayers + "~" + maxplayers + " players#k. Please come back when you have four party members.");
				cm.dispose();
			} else if (em == null) {//EVENT ERROR
				cm.sendOk("Error in event");
				cm.dispose();
			} else if (open == false){//MANUALLY CLOSED
				cm.sendOk("The #b" + pqName + "#k has been #rclosed#k");
				cm.dispose();
			} else {//START EVENT		
				cm.sendOk("The #b" + pqName + "#k Is #gopen#k!");
			}
			} else if (status == 1) {//START EVENT
			em.startInstance(cm.getParty(),cm.getChar().getMap());
			party = cm.getChar().getEventInstance().getPlayers();
			cm.removeFromParty(ITEMID, party);//??? need itemId's'
			cm.dispose();
			}
		}
	}

function getPartySize() {
if(cm.getPlayer().getParty() == null){
return 0;
} else {
return (cm.getPlayer().getParty().getMembers().size());
}
}

function isLeader() {
if(cm.getParty() == null){
return false;
} else {
return cm.isLeader();
}
}

function checkPartySize() {
var size = 0;
if(cm.getPlayer().getParty() == null){
size = 0;
} else {
size = (cm.getPlayer().getParty().getMembers().size());
}
if(size < minplayers || size > maxplayers) {
return false;
} else {
return true;
}
}

function checkPartyLevels() {
var pass = true;
var party = cm.getPlayer().getParty().getMembers();
if(cm.getPlayer().getParty() == null){
pass = false;
} else {
for (var i = 0; i < party.size() && pass; i++) {
if ((party.get(i).getLevel() < minlvl) || (party.get(i).getLevel() > maxlvl) || (party.get(i).getMapid() != cm.getMapId())) {
pass = false;
}
}
}
return pass;
}

function hasParty() {
if(cm.getPlayer().getParty() == null) {
return false;
} else {
return true;
}
}

					