/*
@	Author : Raz
@
@	NPC = Growlie
@	Map = Hidden-Street <Primrose Hill>
@	NPC MapId = 910010000
@	Function = Handle HPQ
@
*/
importPackage(net.sf.odinms.server.maps);
importPackage(net.sf.odinms.tools);
importPackage(net.sf.odinms.server.life);
importPackage(java.awt);
importPackage(net.sf.odinms.server.pq);
importPackage(net.sf.odinms.client);

var status = 0;
var choice;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {

         
         if(mode == -1) {
	    cm.dispose();
	 }else if (mode == 0){
	    cm.sendOk("Ok, see you later");
	    cm.dispose();
	 }else{
		if (mode == 1)
			status++;
		else
			status--;
		
                 if (status == 0) {
		cm.sendSimple("Growl! I am Growlie, always ready to protect this place. What brought you here?\r\n#b#L0# Please tell me what this place is all about.#l\r\n#L1# I have brought #t4001101#.#l\r\n#L2# I would like to leave this place.#l"); 	

		

		}else if (status == 1) {
		choice = selection;
		    if (choice == 0){//PQ INFO
		cm.sendNext("This place can be best described as the prime spot where you can taste the delicious rice cakes made by Moon Bunny every full moon.");
		cm.dispose();
		}else if (choice == 1){//WANT TO PASS
		if(isLeader()){
		cm.sendOk("Oh... isn't this rice cake made by Moon Bunny? Please hand me the rice cake.");
		}else{
		cm.sendOk("Tell your party-leader to come talk to me.");
		cm.dispose();
		}
		}else if (choice == 2){//LEAVE PQ
		cm.sendYesNo("Are you sure you would like to leave?\r\nyour party needs you!");
		}
		


		}else if (status == 2){
		if(choice ==  1){
		if (cm.itemQuantity(4001101) >= 10){//PASS PQ
		cm.sendOk("this is delicious. Please come see me next time for more #b#t4001101##k. Have a safe trip home!");
		cm.clear();
		}else{  
		cm.sendOk("I advise you to check and make sure that you have indeed gathered up #b10 #t4001101#s#k.");
		cm.dispose();
		}
		}else if(choice ==  2){//QUIT PQ
		var eim = cm.getChar().getEventInstance();
		if(eim != null){
		if(isLeader() == true){
		eim.disbandParty();
		}else{
		eim.leftParty(cm.getChar());
		}}
		cm.dispose();
		}
		

		}else if (status == 3){
		if(choice == 1){
		var eim = cm.getChar().getEventInstance();
		if(eim != null){
		var party = cm.getChar().getEventInstance().getPlayers();
		cm.removeFromParty(4001095, party);
		cm.removeFromParty(4001096, party);
		cm.removeFromParty(4001097, party);
		cm.removeFromParty(4001098, party);
		cm.removeFromParty(4001099, party);
		cm.removeFromParty(4001000, party);
		cm.removeFromParty(4001101, party);
		eim.finishPQ();
		}
		cm.dispose();
		}
		}
          }
     }
    
function spawnMonster(eim,mapId,monsterId,location) {
	var mob = MapleLifeFactory.getMonster(monsterId);
	var map = eim.getMapInstance(mapId);
	map.spawnMonsterOnGroundBelow(mob,location);
}


function isLeader(){
if(cm.getParty() == null){
return false;
}else{
return cm.isLeader();
}
}

function hasParty(){
if(cm.getPlayer().getParty() == null){
return false;
}else{
return true;
}
}
