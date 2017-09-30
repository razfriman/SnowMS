/*
@	Author : Raz
@
@	NPC = Nella
@	Map = Victoria Road <Kerning City>
@	NPC MapId = 103000000
@	Function = Start KPQ
@
*/

var status = 0;
var minlvl = 0;
var maxlvl = 200;
var minplayers = 0;
var maxplayers = 6;
var open = true;


function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {//END
		cm.dispose();
	}else if (mode == 0){//NO
		cm.dispose();
	}else{
		if (mode == 1)
			status++;
		else
			status--;
		var em = cm.getEventManager("KerningPQ");
		var party;
		if (status == 0) {
			 if (hasParty() == false) {//NO PARTY
				cm.sendOk("How about you and your party members collectively beating a quest? Here you'll find obstacles and problems where you won't be able to beat it unless with great teamwork. If you want to try it, please tell the #bleader of your party#k to talk to me.");
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
				cm.sendOk("The Kerning-PQ has been closed");
				cm.dispose();
			}else{//START EVENT		
				cm.sendOk("The Kerning-PQ Is open!");
				}
			}else if (status == 1){//START EVENT
			em.startInstance(cm.getParty(),cm.getChar().getMap());
			party = cm.getChar().getEventInstance().getPlayers();
			cm.removeFromParty(4001008, party);
			cm.removeFromParty(4001007, party);
			cm.dispose();
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
return cm.isLeader();
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
var it = party.iterator();
while(it.hasNext()){
var cPlayer = it.next();
if(cPlayer.getLevel() < minlvl || cPlayer.getLevel() > maxlvl){
pass = false;
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

					