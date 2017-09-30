/*
@	Author : Raz
@
@	NPC = Tory
@	Map = Victoria Road <Henesys Park>
@	NPC MapId = 100000200
@	Function = Start HPQ
@
*/

var status = 0;
var minlvl = 0;
var maxlvl = 200;
var minplayers = 0;
var maxplayers = 6;
var time = 10;//Minutes
var open = true;

function start() {
	status = -1;
    	action(1, 0, 0);
	
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	}else if(mode == 0) {
		cm.sendOk("Ok, see you later then.");
		cm.dispose();
		}else{
		if (mode == 1)
			status++;
		else
			status--;
		if (cm.getMapId() == 100000200){
		if (status == 0) {
			cm.sendNext("Hi there! I'm Tory. Inside here is a beautiful hill where the primrose blooms. There's a tiger that lives in the hill, Growlie, and he seems to be looking for something to eat.");
		}else if (status == 1){
			cm.sendSimple("Would you like to head over to the hill of primrose and join forces with your party members to help Growlie out?\r\n#b#L0# Yes, I will go.#l");
		}else if (status == 2){
			var em = cm.getEventManager("HenesysPQ");
			var party;

			 if (hasParty() == false) {//NO PARTY
				cm.sendOk("Please form a party, then come talk to me");
				cm.dispose();
			}else if (isLeader() == false) {//NOT LEADER
				cm.sendOk("If you want to try the quest, please tell the #bleader of your party#k to talk to me.");
				cm.dispose();
                        }else if (checkPartyLevels() == false){//WRONG LEVELS
				cm.sendOk("Please check that all your party members are between the levels of #b" + minlvl + "~" + maxlvl)
				cm.dispose();
			}else if (checkPartySize() == false){//PARTY SIZE WRONG
				cm.sendOk("Check that your party contains #b" + minplayers + "~" + maxplayers + " players#k. Please come back when you have four party members.");
				cm.dispose();
			}else if (em == null){//EVENT ERROR
				cm.sendOk("ERROR IN EVENT");
				cm.dispose();
			}else if (open == false){//MANUALLY CLOSED
				cm.sendOk("The Henesys-PQ has been closed");
				cm.dispose();
			}else{//START EVENT		
					em.startInstance(cm.getParty(),cm.getChar().getMap());
					party = cm.getChar().getEventInstance().getPlayers();
					cm.removeFromParty(4001095, party);
					cm.removeFromParty(4001096, party);
					cm.removeFromParty(4001097, party);
					cm.removeFromParty(4001098, party);
					cm.removeFromParty(4001099, party);
					cm.removeFromParty(4001000, party);
					cm.removeFromParty(4001101, party);
					cm.dispose();
				}
			}
		}else{//LEAVE PQ
		if (status == 0){
		cm.sendYesNo("Would you like to leave this place?");
		}else if (status == 1){
		cm.removeAll(4001095);
		cm.removeAll(4001095);
		cm.removeAll(4001095);
		cm.removeAll(4001095);
		cm.removeAll(4001095);
		cm.removeAll(4001095);
		cm.removeAll(4001095);
		cm.warp(910010300);
		cm.dispose();
		}
	    }
	}
    }

function getPartySize(){
if(cm.getPlayer().getParty() == null){
return 0;
}else{
return (cm.getPlayer().getParty().getMembers().size());
}
}

function isLeader(){
if(cm.getParty() == null){
return false;
}else{
return cm.isLeader();
}
}

function checkPartySize(){
var size = 0;
if(cm.getPlayer().getParty() == null){
size = 0;
}else{
size = (cm.getPlayer().getParty().getMembers().size());
}
if(size < minplayers || size > maxplayers){
return false;
}else{
return true;
}
}

function checkPartyLevels(){
var pass = true;
var party = cm.getPlayer().getParty().getMembers();
if(cm.getPlayer().getParty() == null){
pass = false;
}else{
for (var i = 0; i < party.size() && pass; i++) {
if ((party.get(i).getLevel() < minlvl) || (party.get(i).getLevel() > maxlvl) || (party.get(i).getMapid() != cm.getMapId())) {
pass = false;
}
}
}
return pass;
}

function hasParty(){
if(cm.getPlayer().getParty() == null){
return false;
}else{
return true;
}
}
//Hi there! I'm Tory. Inside here is a beautiful hill where the primrose blooms. There's a tiger that lives in the hill, Growlie, and he seems to be looking for something to eat.
//Would you like to head over to the hill of primrose and join forces with your party members to help Growlie out?..#b#L0# Yes, I will go.#l
//I'm sorry, but someone in your party is under Level 10. Please adjust your party to make sure that your party consists of at least 3 members that are all at Level 10 or higher. Let me know when you're done


