/*
@	Author : Raz
@
@	NPC = Cloto
@	Map = 1st Accompaniment <ALL Stages>
@	NPC MapId = 103000800 | 103000801 | 103000802 | 103000803 | 103000804
@	Function = Handle KPQ
@
*/

importPackage(net.sf.odinms.tools);
importPackage(net.sf.odinms.server.life);
importPackage(java.awt);
importPackage(net.sf.odinms.server.pq);
importPackage(net.sf.odinms.client);

var status;
var curMap;
var playerStatus;
  var questions = Array("Here's the question. Collect the same number of coupons as the minimum level required to make the first job advancement as warrior.",
			"Here's the question. Collect the same number of coupons as the minimum amount of STR needed to make the first job advancement as a warrior.",
			"Here's the question. Collect the same number of coupons as the minimum amount of INT needed to make the first job advancement as a magician.",
			"Here's the question. Collect the same number of coupons as the minimum amount of DEX needed to make the first job advancement as a bowman.",
			"Here's the question. Collect the same number of coupons as the minimum amount of DEX needed to make the first job advancement as a thief.",
			"Here's the question. Collect the same number of coupons as the minimum level required to advance to 2nd job.");
var qanswers = Array(10, 35, 20, 25, 25, 30);
var party;
var preamble;

var stage2combos = Array(Array(0,1,1,1),Array(1,0,1,1),Array(1,1,0,1),Array(1,1,1,0));

var stage3combos = Array(Array(0,0,1,1,1),Array(0,1,0,1,1),Array(0,1,1,0,1),
			Array(0,1,1,1,0),Array(1,0,0,1,1),Array(1,0,1,0,1),
			Array(1,0,1,1,0),Array(1,1,0,0,1),Array(1,1,0,1,0),
			Array(1,1,1,0,0));

var stage4combos = Array(Array(0,0,0,1,1,1),Array(0,0,1,0,1,1),Array(0,0,1,1,0,1),
			Array(0,0,1,1,1,0),Array(0,1,0,0,1,1),Array(0,1,0,1,0,1),
			Array(0,1,0,1,1,0),Array(0,1,1,0,0,1),Array(0,1,1,0,1,0),
			Array(0,1,1,1,0,0),Array(1,0,0,0,1,1),Array(1,0,0,1,0,1),
			Array(1,0,0,1,1,0),Array(1,0,1,0,0,1),Array(1,0,1,0,1,0),
			Array(1,0,1,1,0,0),Array(1,1,0,0,0,1),Array(1,1,0,0,1,0),
			Array(1,1,0,1,0,0),Array(1,1,1,0,0,0));

function start() {
	status = -1;
	mapId = cm.getChar().getMapId();
	if (mapId == 103000800)
		curMap = 1;
	else if (mapId == 103000801)
		curMap = 2;
	else if (mapId == 103000802)
		curMap = 3;
	else if (mapId == 103000803)
		curMap = 4;
	else if (mapId == 103000804)
		curMap = 5;
	playerStatus = cm.isLeader();
	preamble = null;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (mode == 0 && status == 0) {
			cm.dispose();
			return;
		}
		if (mode == 1)
			status++;
		else
			status--;
		if (curMap == 1) { // First Stage.
			if (playerStatus) { // party leader
				if (status == 0) {
					var eim = cm.getChar().getEventInstance();
					party = eim.getPlayers();
					preamble = eim.getProperty("leader1stpreamble");
					if (preamble == null) {
						cm.sendNext("Hello. Welcome to the first stage. Look around and you'll see Ligators wandering around. When you defeat them, they will cough up a #bcoupon#k. Every member of the party other than the leader should talk to me, geta  question, and gather up the same number of #bcoupons#k as the answer to the question I'll give to them.\r\nIf you gather up the right amount of #bcoupons#k, I'll give the #bpass#k to that player. Once all the party members other than the leader gather up the #bpasses#k and give them to the leader, the leader will hand over the #bpasses#k to me, clearing the stage in the process. The faster you take care of the stages, the more stages you'll be able to challenge. So I suggest you take care of things quickly and swiftly. Well then, best of luck to you.");
						eim.setProperty("leader1stpreamble","done");
						cm.dispose();
					}
					else { // check how many they have compared to number of party members
                        			// check for stage completed
                        			var complete = eim.getProperty(curMap.toString() + "stageclear");
                        			if (complete != null) {
                        				cm.sendNext("Please hurry on to the next stage, the portal opened!");
                        				cm.dispose();
                        			}
                        			else {
							var numpasses = party.size()-1;
							var passes = cm.haveItem(4001008,numpasses);
							var strpasses = "#b" + numpasses.toString() + " passes#k";
							if (!passes) {
								cm.sendNext("I'm sorry, but you are short on the number of passes. You need to give me the right number of passes; it should be the number of members of your party minus the leader, " + strpasses + " to clear the stage. Tell your party members to solve the questions, gather up the passes, and give them to you.");
								cm.dispose();
							}
							else {
								cm.sendNext("You gathered up " + strpasses + "! Congratulations on clearing the stage! I'll make the portal that sends you to the next stage. There's a time limit on getting there, so please hurry. Best of luck to you all!");
								clear(1,eim,cm);
								cm.givePartyExp(100, party);
								cm.gainItem(4001008,-numpasses);
								cm.dispose();
							}
						}
					}
				}
				/*debug
				cm.sendNext("Hello.  This is just a test.");
				cm.dispose();*/
			}
			else { // non leader
				var eim = cm.getChar().getEventInstance();
				pstring = "member1stpreamble" + cm.getChar().getId().toString();
				preamble = eim.getProperty(pstring);
				if (status == 0 && preamble == null) {
					var qstring = "member1st" + cm.getChar().getId().toString();
					var question = eim.getProperty(qstring);
					if (question == null) {
						// Select a random question to ask the player.
						var questionNum = Math.floor(Math.random() * questions.length);
						eim.setProperty(qstring, questionNum.toString());
					}
					cm.sendNext("Here, you need to collect #bcoupons#k by defeating the same number of Ligators as the answer to the questions asked individually.");
					
				}
				else if (status == 0) {// otherwise
                        		// check for stage completed
                        		var complete = eim.getProperty(curMap.toString() + "stageclear");
                        		if (complete != null) {
                        			cm.sendNext("Please hurry on to the next stage, the portal opened!");
                        			cm.dispose();
                        		}
                        		else {
						// else correct/incorrect response to question here
						var qstring = "member1st" + cm.getChar().getId().toString();
						var numcoupons = qanswers[parseInt(eim.getProperty(qstring))];
						var qcorr = cm.haveItem(4001007,(numcoupons+1));
						var enough = false;
						if (!qcorr) { // not too many
							qcorr = cm.haveItem(4001007,numcoupons);
							if (qcorr) { // just right
								cm.sendNext("That's the right answer! For that you have just received a #bpass#k. Please hand it to the leader of the party.");
								cm.gainItem(4001007,-numcoupons);
								cm.gainItem(4001008,1);
								enough = true;
							}
						}
						if (!enough) {
							cm.sendNext("I'm sorry, but that is not the right answer! Please have the correct number of coupons in your inventory.");
						}
						cm.dispose();
					}
				}
				else if (status == 1) {
					if (preamble == null) {
						var qstring = "member1st" + cm.getChar().getId().toString();
						var question = parseInt(eim.getProperty(qstring));
						cm.sendNextPrev(questions[question]);
					}
					else { // shouldn't happen, if it does then just dispose
						cm.dispose();
					}
						
				}
				else if (status == 2) { // preamble completed
					eim.setProperty(pstring,"done");
					cm.dispose();
				}
				else { // shouldn't happen, but still...
					eim.setProperty(pstring,"done"); // just to be sure
					cm.dispose();
				}
			}
		} // end first map scripts
		
		else if (2 <= curMap && 4 >= curMap) {
			rectanglestages(cm);
		}
		else if (curMap == 5) { // Final stage
			var eim = cm.getChar().getEventInstance();
			var stage5done = eim.getProperty("5stageclear");
			if (stage5done == null) {
				if (playerStatus) { // Leader
					var map = eim.getMapInstance(cm.getChar().getMapId());
					var passes = cm.haveItem(4001008,10);
					if (passes) {
						// clear
						cm.sendNext("Here's the portal that leads you to the last, bonus stage. It's a stage that allows you to defeat regular monsters a little easier. You'll be given a set amount of time to hunt as much as possible, but you can always leave the stage in the middle of it through the NPC. Again, congratulations on clearing all the stages. Take care...");
						party = eim.getPlayers();
						cm.gainItem(4001008,-10);
						clear(5,eim,cm);
						cm.givePartyExp(1500, party);
						cm.dispose();
					}
					else { // not done yet
						cm.sendNext("Hello. Welcome to the 5th and final stage. Walk around the map and you'll be able to find some Boss monsters. Defeat all of them, gather up #bthe passes#k, and please get them to me. Once you earn your pass, the leader of your party will collect them, and then get them to me once the #bpasses#k are gathered up. The monsters may be familiar to you, but they may be much stronger than you think, so please be careful. Good luck!\r\nAs a result of complaints, it is now mandatory to kill all the Slimes! Do it!");
					}
					cm.dispose();
				}
				else { // Members
					cm.sendNext("Welcome to the 5th and final stage.  Walk around the map and you will be able to find some Boss monsters.  Defeat them all, gather up the #bpasses#k, and give them to your leader.  Once you are done, return to me to collect your reward.");
					cm.dispose();
				}
			}
			else { // give rewards and warp out
				if (status == 0) {
					cm.sendNext("Incredible! You cleared all the stages to get to this point. Here's a small prize for your job well done. Before you accept it, however, please make sure your use and etc. inventories have empty slots available.\r\n#bYou will not receive a prize if you have no free slots!#k");
				}
				if (status == 1) {
					getPrize(eim,cm);
					cm.dispose();
				}
			}
		}
		// etc
                else { // no map found
                        cm.sendNext("Invalid map, this means the stage is incomplete.");
                        cm.dispose();
                }
	}
}


function clear(stage, eim, cm) {
	eim.setProperty(stage.toString() + "stageclear","true");
	cm.clear();
	cm.gate();
	var mf = eim.getMapFactory();
	var map = mf.getMap(103000800 + stage);
	//eim.addMapInstance(103000800 + stage,map);
	var nextStage = eim.getMapInstance(103000800 + stage);
	var portal = nextStage.getPortal("next00");
	if (portal != null) {
		portal.setScriptName("kpq" + (stage+1).toString());
	}
}

function failstage(eim, cm) {
	cm.wrong();
}

function rectanglestages (cm) {
	// debug makes these stages clear without being correct
	var debug = false;
	var eim = cm.getChar().getEventInstance();
	if (curMap == 2) {
		var nthtext = "2nd";
		var nthobj = "ropes";
		var nthverb = "hang";
		var nthpos = "hang on the ropes too low";
		var curcombo = stage2combos;
		var currect = cm.getChar().getMap().getAreas();
		var objset = [0,0,0,0];
	}
	else if (curMap == 3) {
		var nthtext = "3rd";
		var nthobj = "platforms";
		var nthverb = "stand";
		var nthpos = "stand too close to the edges";
		var curcombo = stage3combos;
		var currect = cm.getChar().getMap().getAreas();
		var objset = [0,0,0,0,0];
	}
	else if (curMap == 4) {
		var nthtext = "4th";
		var nthobj = "barrels";
		var nthverb = "stand";
		var nthpos = "stand too close to the edges";
		var curcombo = stage4combos;
		var currect = cm.getChar().getMap().getAreas();
		var objset = [0,0,0,0,0,0];
	}
        if (playerStatus) { // leader
                if (status == 0) {
                        // check for preamble
                     
                        party = eim.getPlayers();
                        preamble = eim.getProperty("leader" + nthtext + "preamble");
                        if (preamble == null) {
                                cm.sendNext("Hi. Welcome to the " + nthtext + " stage. Next to me, you'll see a number of " + nthobj + ". Out of these " + nthobj + ", #b3 are connected to the portal that sends you to the next stage#k. All you need to do is have #b3 party members find the correct " + nthobj + " and " + nthverb + " on them.#k\r\nBUT, it doesn't count as an answer if you " + nthpos + "; please be near the middle of the " + nthobj + " to be counted as a correct answer. Also, only 3 members of your party are allowed on the " + nthobj + ". Once they are " + nthverb + "ing on them, the leader of the party must #bdouble-click me to check and see if the answer's correct or not#k. Now, find the right " + nthobj + " to " + nthverb + " on!");
                                eim.setProperty("leader" + nthtext + "preamble","done");
                                var sequenceNum = Math.floor(Math.random() * curcombo.length);
                                eim.setProperty("stage" + nthtext + "combo",sequenceNum.toString());
                                cm.dispose();
                        }
                        else {
                        	// otherwise
                        	// check for stage completed
                        	var complete = eim.getProperty(curMap.toString() + "stageclear");
                        	if (complete != null) {	
                        		var mapClear = curMap.toString() + "stageclear";
                        		eim.setProperty(mapClear,"true"); // Just to be sure
                        		cm.sendNext("Please hurry on to the next stage, the portal opened!");
                        	}
                        	// check for people on ropes
                        	else { 
                        	        // check for people on ropes(objset)
                        	        var totplayers = 0;
                        	        for (i = 0; i < objset.length; i++) {
                        	                for (j = 0; j < party.size(); j++) {
                        	                       var present = currect.get(i).contains(party.get(j).getPosition());
                        		                        if (present) {
                        	                                objset[i] = objset[i] + 1;
                        	                                totplayers = totplayers + 1;
                        	                        }
                        	                }
                        	        }
                        	        // compare to correct
                        	        // first, are there 3 players on the objset?
                        	        if (totplayers == 3 || debug) {
                        	                var combo = curcombo[parseInt(eim.getProperty("stage" + nthtext + "combo"))];
                        	                // debug
                        	                // combo = curtestcombo;
                        	                var testcombo = true;
                        	                for (i = 0; i < objset.length; i++) {
                        	                	if (combo[i] != objset[i])
                        	                		testcombo = false;
                        	                }
                        	                if (testcombo || debug) {
                        	                        // do clear
                        	                        clear(curMap,eim,cm);
                        	                        var exp = (Math.pow(2,curMap) * 50);
                        	                        cm.givePartyExp(exp, party);
                        	                        cm.dispose();
                        	                }
                        	                else { // wrong
                        	                        // do wrong
                        	                        failstage(eim,cm);
                        	                        cm.dispose();
                        	                }
                        	        }
                        	        else {
                        	                // oops, it doesn't display fail if you're not on the ropes
                        	                // failstage(eim,cm);
                        	                if (debug) {
                        	               		var outstring = "Objects contain:"
                        	               		for (i = 0; i < objset.length; i++) {
                        	               			outstring += "\r\n" + (i+1).toString() + ". " + objset[i].toString();
                        	               		}
                        	                	cm.sendNext(outstring); 
                        	                }
                        	                else
							cm.sendNext("It looks like you haven't found the 3 " + nthobj + " just yet. Please think of a different combination of " + nthobj + ". Only 3 are allowed to " + nthverb + " on " + nthobj + ", and if you " + nthpos + " it may not count as an answer, so please keep that in mind. Keep going!");
                        	                
                        	                cm.dispose();
                        	        }
                        	}
                        }
                        // just in case.
                }
                else {
                	var complete = eim.getProperty(curMap.toString() + "stageclear");
                       	if (complete != null) {	
                		var target = eim.getMapInstance(103000800 + curMap);
				var targetPortal = target.getPortal("st00");
                		cm.getChar().changeMap(target, targetPortal);
                	}
                	cm.dispose();
                }
        }
        else { // not leader
        	if (status == 0) {
        	        var complete = eim.getProperty(curMap.toString() + "stageclear");
        	        if (complete != null) {
        	        	cm.sendNext("Please hurry on to the next stage, the portal opened!");
        	        }
        	        else {
        	        	cm.sendNext("Please have the party leader talk to me.");
        	        	cm.dispose();
        	        }
        	}
		else {
                	var complete = eim.getProperty(curMap.toString() + "stageclear");
		       	if (complete != null) {	
				var target = eim.getMapInstance(103000800 + curMap);
				var targetPortal = target.getPortal("st00");
                		cm.getChar().changeMap(target, targetPortal);
			}
                	cm.dispose();
                }
        }
}


function getPrize(eim,cm) {
	var rewards = MaplePQRewards.KPQrewards;
	MapleReward.giveReward(rewards, cm.getPlayer());
	var map = eim.getMapInstance(103000805);
	var portal = map.getPortal("sp");
	cm.getPlayer().changeMap(map,portal);
}