/*
@	Author : Raz
@
@	NPC = Amos the Strong
@	Map = Hidden Street <STAGE 4/5/6/7>
@	NPC MapId = 670010500
@	Function = Handle APQ
@
*/

/* 
	Amos the Strong		     (9201043)-(9201044)-(9201045)-(9201046)-(9201048)
	Amos' Training Ground	     (670010000)
	Entrance of Amorian Challenge(670010100)
	Stage 1 - Magik Mirror	     (670010100)
	Stage 2 - Heart Strings	     (670010100)
	Stage 3 - Twisted Switcher   (670010100)
	Stage 4 - Last Man Standing  (670010100)
	Stage 5 - Fluttering Hearts  (670010100)
	Stage 6 - Love Hurts	     (670010100)
	Stage 7 - Amos' Vault	     (670010100)
*/
importPackage(net.sf.odinms.client);
importPackage(net.sf.odinms.server.maps);
importPackage(net.sf.odinms.server.life);
importPackage(java.awt);

//STAGE 4 + 5 + 6 + 7
var status = 0;
var mapId;
var curMap;
var preamble;
var gaveItems;
var nthtext = "";
var party;
var eim;
function start() {
	status = -1;

	mapId = cm.getChar().getMap().getId();
	if (mapId == 670010200)
		curMap = 1;
	else if (mapId == 670010300)
		curMap = 2;
	else if (mapId == 670010301)
		curMap = 2;
	else if (mapId == 670010302)
		curMap = 2;
	else if (mapId == 670010400)
		curMap = 3;
	else if (mapId == 670010500)
		curMap = 4;
	else if (mapId == 670010600)
		curMap = 5;
	else if (mapId == 670010700)
		curMap = 6;

	preamble = null;
    
   
	action(1, 0, 0);
}

function action(mode, type, selection) {

         
         if (mode == -1) {
		cm.dispose();//ExitChat
	}else if (mode == 0){
		cm.dispose();//No
	}else{		    //Regular Talk
		if (mode == 1)
			status++;
		else
			status--;

	if(curMap == 4){
		 eim = cm.getChar().getEventInstance(); 
		 nthtext = "4th";
		 party = eim.getPlayers();
		 if(isLeader())
		    preamble = eim.getProperty("leader" + nthtext + "preamble");
		 else if(!isLeader())
		    preamble = eim.getProperty("member" + nthtext + "preamble"); 
                 
		  if (status == 0) {
		
                        if (preamble == null) {
                                cm.sendNext("Hi. Welcome to the " + nthtext + " stage.");
                                eim.setProperty("leader" + nthtext + "preamble","done");
                                cm.dispose();
                        }else{
		 if(!isLeader()){
		 if(gaveItems == null){
		 cm.sendOk("Please tell your #bParty-Leader#k to come talk to me");
		 cm.dispose();
		 }else{
		  cm.sendOk("Hurry, goto the next stage, the portal is open!");
		  cm.dispose();
		 }
		}else{
		if(gaveItems == null){
		if(cm.itemQuantity(4031597) >= 50){
		cm.sendOk("Good job! you have collected all 50 #b#t4031597#'s#k");
		cm.removeAll(4031597);
		}else{
		cm.sendOk("Sorry you don't have all 50 #b#t4031597#'s#k");
		cm.dispose();
		}
		}else{
		cm.sendOk("Hurry, goto the next stage, the portal is open!");
		cm.dispose();
		}
		}}
		}else if (status == 1){
		cm.sendOk("You may continue to the next stage!");
		cm.gate();
		cm.clear();
		cm.givePartyExp(8000, party);
		eim.setProperty("4stageclear","true");
		eim.setProperty("leader" + nthtext + "gaveItems","done");
		cm.dispose();
		}            
		
       }else if(curMap == 5){
       eim = cm.getChar().getEventInstance();
       nthtext = "5th";
       if (status == 0) {
		party = eim.getPlayers();
                if(!isLeader()){
		cm.sendOk("Congrats, your almost done, just tell you #bParty-Leader#k to come talk to me, so we can move on to the next and final stage");
		cm.dispose();
		}else{
		cm.sendOk("Congrats, your doing great! Only one more stage left, let me take you and your party over there");
		}
		}else if (status == 1){
		cm.clear();
		cm.givePartyExp(9000, party);
		cm.warpMembers(eim.getMapInstance(670010700), party);
		cm.dispose();

		}            
       }else if (curMap == 6){
	eim = cm.getChar().getEventInstance();
	nthtext = "6th";
	preamble = eim.getProperty("leader" + nthtext + "preamble");
	var summonedMonster = eim.getProperty("leader" + nthtext + "summonedMonster");
	gaveItems = eim.getProperty("leader" + nthtext + "gaveItems"); 
	if(!isLeader()){
	if(status == 0){
	cm.sendOk("Wow! You made it to the last stage, just tell your #bParty-Leader#k to talk to me then we'll get started.");
	cm.dispose();
	}
	}else{
	if(preamble == null){//DIDNT GET PREAMBLE YET
	if(status == 0){
	cm.sendOk("Wow! You made it to the last stage. talk to me again for your instructions on how to pass this stage");
	eim.setProperty("leader" + nthtext + "preamble","done");
	cm.dispose();
	}
	}else if(summonedMonster == null){//DIDNT SUMMON MOB YET
	if(status == 0){
	cm.sendOk("Im am going to summon a #b#o9400536##k now, prepare to kill it!");
	}else if(status == 1){
	eim.setProperty("leader" + nthtext + "summonedMonster","done");
	//SUMMON MOB
	var mobId = 9400536;
	var mob = MapleLifeFactory.getMonster(mobId);
	var pos = Point(850, 570);
	mob.setPosition(pos);
	mob.setBoss(false);
	eim.getMapInstance(670010700).spawnMonsterOnGroundBelow(mob, pos);
	//Port palyer to random portal
	portRandom(eim);
	cm.dispose();
	}
	}else if(gaveItems == null){
	if(status == 0){
	if(cm.itemQuantity(4031594) < 10){
	cm.sendOk("Sorry you don't have all 10 #b#t4031594#'s#k");
	cm.dispose();
	}else{
	cm.sendOk("Good job! you have collected all 10 #b#t4031594#'s#k");
	}
	}else if(status == 1){
	cm.sendOk("Very nice! You have finished the #bAmorian-Challenge#k");
	cm.clear();
	party = eim.getPlayers();
	cm.givePartyExp(11000, party);
	eim.setProperty("leader" + nthtext + "gaveItems","done");
	cm.removeAll(4031594);
	cm.dispose();
	}
	
	}else{
	if(status == 0){
	cm.sendOk("Congrats on completeting the #bAmorian-Challenge#k");
	}else if (status == 1){
	cm.sendOk("Lets go to the #bBonus-Stage#k you and your party deserve it!");
	}else if (status == 2){
	warpOut(eim);//WarpOut
	cm.dispose();
	}
	}
	    }   
	}  
    }
}
     
     
function isLeader(){
if(cm.getParty() == null){
return false;
}else{
return cm.isLeader();
}
}


function clear(stage, eim, cm) {
	eim.setProperty(stage.toString() + "stageclear","true");
	cm.clear();
	cm.gate();
}

function failstage(eim, cm) {
	cm.wrong();
}

function isMarried(){
return false;
}

function portRandom(eim){
var rand;
var chr;
for(var i = 0; i < eim.getPlayers().size(); i++){
rand = Math.floor(Math.random()*16) + 2;
chr = eim.getPlayers().get(i);
cm.portToPort(chr, rand);
}}

function warpOut(eim) {
	var map = eim.getMapInstance(670010800);
	var portal = map.getPortal("st00");
	party = eim.getPlayers();
	cm.warpMembers(map, "st00", party);
	cm.getChar().getEventInstance().schedule("startBonus", (1 * 60000));
	cm.getChar().getMap().broadcastMessage(net.sf.odinms.tools.MaplePacketCreator.getClock(60));
}