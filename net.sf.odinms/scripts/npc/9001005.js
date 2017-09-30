/*
	Ring/Hat/Monster Summon/Stat Reset/Fame Sell/Level Lottery NPC for Donators E: 
	Written by Mooblar - Sloppy code? Kiss my ass!
*/

importPackage(net.sf.odinms.server.life);
importPackage(net.sf.odinms.tools);



var status = 0;
var donatorstatus = 0;
var ring = Array(1112112, 1112223, 1112101, 1112102, 1112202, 1112205, 1112209, 1112106, 1112207, 1112215, 1112104, 1112108, 1112113, 1112224, 1112002, 1112118, 1112228, 1112119, 1112229, 1112120, 1112230, 1112210, 1112213, 1112216, 1112111, 1112221, 1112115, 1112808, 1112217, 1112114, 1112225, 1112211, 1112214, 1112200, 1112204, 1112201, 1112105, 1112206, 1112117, 1112227, 1112222, 1112109, 1112219, 1112212, 1112110, 1112220, 1112107, 1112208, 1112218, 1112203, 1112103, 1112116, 1112226, 1112100);

function start() {
	status = -1;
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
		if (status == 0) {
			if(cm.getChar().isDonator() == false) {
				cm.sendOk("Donator access only! E:");
				cm.dispose();
			} else {
				if(cm.getChar().isDonator() == true) {
					cm.sendSimple("Select the option you'd like. (More might be added later E:).\r\n#L0#Ring + T.Hat#l\r\n#L1#Spawn a special monster#l\r\n#L2#Stat Reset#l\r\n#L3#Return to the normal world#l\r\n#L4#Level lottery!#l\r\n#L5#I wonder what happens if I click here..#l\r\n#L6#Purchase fame for 10,000,000 meso#l");
				}
			}
		} else if (status == 1) {
				var cal = java.util.Calendar.getInstance();
				var time = cal.getTimeInMillis();
				var lastSM = cm.grabSM();
			if (selection == 0 && cm.grabRing() > 0) {
				cm.sendOk("You can only get a ring / transparent hat once!");
				cm.dispose();
			} else if (selection == 1 && ((time - (1000 * 60 * 4320)) <= lastSM)) {
				cm.sendOk("You can only spawn a special monster once every three days.");
				cm.dispose();
			} else if (selection == 4 && cm.grabLottery() > 0) {
				cm.sendOk("Sorry, you can only gamble your current level once.");
				cm.dispose();
			} else if (selection == 0) {
				donatorstatus = 1;
				cm.sendSimple("\r\n#L0#Beach Label Ring#l\r\n#L1#Beach Quote Ring#l\r\n#L2#Blue Label Ring#l\r\n#L3#Blue Label Ring 2#l\r\n#L4#Blue Quote Ring#l\r\n#L5#Blue-Flowered Quote Ring#l\r\n#L6#Blue-Hearted Quote Ring#l\r\n#L7#Blue-Ribboned Label Ring#l\r\n#L8#Blue-Ribboned Quote Ring#l\r\n#L9#Blue Marine Quote Ring#l\r\n#L10#Bubbly Label Ring#l\r\n#L11#Butterfly Label Ring#l\r\n#L12#Chocolate Label Ring#l\r\n#L13#Chocolate Quote Ring#l\r\n#L15#Coke Label Ring#l\r\n#L16#Coke Quote Ring#l\r\n#L17#Coke(Red) Label Ring#l\r\n#L18#Coke(Red) Quote Ring#l\r\n#L19#Coke(White) Label Ring#l\r\n#L20#Coke(White) Quote Ring#l\r\n#L21#Gold-Yellow Quote Ring#l\r\n#L22#Gold-Yellow Quote Ring 2#l\r\n#L23#Kitty Quote Ring#l\r\n#L24#KTF Basketball Team Label Ring#l\r\n#L25#KTF Basketball Team Quote Ring#l\r\n#L26#MapleBowl Label Ring#l\r\n#L27#MapleBowl Quote Ring#l\r\n#L28#Paw-Print Quote Ring#l\r\n#L29#Pink Candy Label Ring#l\r\n#L30#Pink Candy Quote Ring#l\r\n#L31#Pink Lady Quote Ring#l\r\n#L32#Pink Lady Quote Ring 2#l\r\n#L33#Pink Quote Ring#l\r\n#L34#Pink-Flowered Quote Ring#l\r\n#L35#Pink-Hearted Quote Ring#l\r\n#L36#Pink-Ribboned Label Ring#l\r\n#L37#Pink-Ribboned Quote Ring#l\r\n#L38#Rainbow Label Ring#l\r\n#L39#Rainbow Quote Ring#l\r\n#L40#Sakura Quote Ring#l\r\n#L41#Scoreboard Label Ring#l\r\n#L42#Scoreboard Quote Ring#l\r\n#L43#Silver-Blue Quote Ring#l\r\n#L44#SK Basketball Team Label Ring#l\r\n#L45#SK Basketball Team Quote Ring#l\r\n#L46#Skull Label Ring#l\r\n#L47#Skull Quote Ring#l\r\n#L48#Teddy Bear Quote Ring#l\r\n#L49#The Golden Fly Ring (Quote)#l\r\n#L50#The Legendary Gold Ring (Label)#l\r\n#L51#White Cloud Label Ring#l\r\n#L52#White Cloud Quote Ring#l\r\n#L53#White Label Ring#l");
			} else if (selection == 1) {
				donatorstatus = 2;
				cm.sendSimple("\r\n#L0#NX Slime#l\r\n#L1#Snowman#l\r\n#L2#Loki Box#l");
			} else if (selection == 3) {
				cm.warp(100000103, 0);
				cm.dispose();
			} else if (selection == 5) {
				cm.c.getSession().write(MaplePacketCreator.getPacketFromHexString("97 00 66 00 00 00 05 04 87 01 00 00 00 00 00 6F FF 12 01 05 00 00 00 00 FE FF"));
				cm.dispose();
			} else if (selection == 4) {
				donatorstatus = 3;
				cm.sendYesNo("Are you absolutely sure you want to gamble a level?");
			} else if (selection == 6) {
				var statup = new java.util.ArrayList();
				var p = cm.c.getPlayer();
				var totFame = p.getFame();
				if (cm.getMeso() > 100000000) {
				p.addFame(1);
				statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.FAME, java.lang.Integer.valueOf(totFame + 1)));
				p.getClient().getSession().write (net.sf.odinms.tools.MaplePacketCreator.updatePlayerStats(statup));
				cm.gainMeso(5);
				} else if (cm.getMeso() < 10000000) {
				cm.sendOk("You do not have enough meso to purchase any fame.");
				cm.dispose();
			} else if (selection == 2) {
				var statup = new java.util.ArrayList();
				var p = cm.c.getPlayer();
				var totAp = p.getRemainingAp() + p.getStr() + p.getDex() + p.getInt() + p.getLuk();
				if (cm.getJob().equals(net.sf.odinms.client.MapleJob.THIEF && cm.getLevel() >= 10)) {
				p.setStr(4);
				p.setDex(25);
				p.setInt(4);
				p.setLuk(4);
				p.setRemainingAp (totAp - 37);
				statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.STR, java.lang.Integer.valueOf(4)));
				statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.DEX, java.lang.Integer.valueOf(25)));
				statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.LUK, java.lang.Integer.valueOf(4)));
				statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.INT, java.lang.Integer.valueOf(4)));
				statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.AVAILABLEAP, java.lang.Integer.valueOf(p.getRemainingAp())));
				p.getClient().getSession().write (net.sf.odinms.tools.MaplePacketCreator.updatePlayerStats(statup));
				} else { if (cm.getJob().equals(net.sf.odinms.client.MapleJob.ASSASSIN)) {
				p.setStr(4);
				p.setDex(25);
				p.setInt(4);
				p.setLuk(4);
				p.setRemainingAp (totAp - 37);
				statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.STR, java.lang.Integer.valueOf(4)));
				statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.DEX, java.lang.Integer.valueOf(25)));
				statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.LUK, java.lang.Integer.valueOf(4)));
				statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.INT, java.lang.Integer.valueOf(4)));
				statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.AVAILABLEAP, java.lang.Integer.valueOf(p.getRemainingAp())));
				p.getClient().getSession().write (net.sf.odinms.tools.MaplePacketCreator.updatePlayerStats(statup));
				} else { if (cm.getJob().equals(net.sf.odinms.client.MapleJob.HERMIT)) {
				p.setStr(4);
				p.setDex(25);
				p.setInt(4);
				p.setLuk(4);
				p.setRemainingAp (totAp - 37);
				statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.STR, java.lang.Integer.valueOf(4)));
				statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.DEX, java.lang.Integer.valueOf(25)));
				statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.LUK, java.lang.Integer.valueOf(4)));
				statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.INT, java.lang.Integer.valueOf(4)));
				statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.AVAILABLEAP, java.lang.Integer.valueOf(p.getRemainingAp())));
				p.getClient().getSession().write (net.sf.odinms.tools.MaplePacketCreator.updatePlayerStats(statup));
				} else { if (cm.getJob().equals(net.sf.odinms.client.MapleJob.BANDIT)) {
				p.setStr(4);
				p.setDex(25);
				p.setInt(4);
				p.setLuk(4);
				p.setRemainingAp (totAp - 37);
				statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.STR, java.lang.Integer.valueOf(4)));
				statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.DEX, java.lang.Integer.valueOf(25)));
				statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.LUK, java.lang.Integer.valueOf(4)));
				statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.INT, java.lang.Integer.valueOf(4)));
				statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.AVAILABLEAP, java.lang.Integer.valueOf(p.getRemainingAp())));
				p.getClient().getSession().write (net.sf.odinms.tools.MaplePacketCreator.updatePlayerStats(statup));
				} else { if (cm.getJob().equals(net.sf.odinms.client.MapleJob.CHIEFBANDIT)) {
				p.setStr(4);
				p.setDex(25);
				p.setInt(4);
				p.setLuk(4);
				p.setRemainingAp (totAp - 37);
				statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.STR, java.lang.Integer.valueOf(4)));
				statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.DEX, java.lang.Integer.valueOf(25)));
				statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.LUK, java.lang.Integer.valueOf(4)));
				statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.INT, java.lang.Integer.valueOf(4)));
				statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.AVAILABLEAP, java.lang.Integer.valueOf(p.getRemainingAp())));
				p.getClient().getSession().write (net.sf.odinms.tools.MaplePacketCreator.updatePlayerStats(statup));
				} else { if (cm.getLevel() >= 10) {
				p.setStr(4);
				p.setDex(4);
				p.setInt(4);
				p.setLuk(4);
				p.setRemainingAp (totAp - 16);
				statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.STR, java.lang.Integer.valueOf(4)));
				statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.DEX, java.lang.Integer.valueOf(4)));
				statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.LUK, java.lang.Integer.valueOf(4)));
				statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.INT, java.lang.Integer.valueOf(4)));
				statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.AVAILABLEAP, java.lang.Integer.valueOf(p.getRemainingAp())));
				p.getClient().getSession().write (net.sf.odinms.tools.MaplePacketCreator.updatePlayerStats(statup));
								}
							}
						}
					}
				}
			}
		}
	}
}
		else if (status == 2){
				var statup = new java.util.ArrayList();
				var p = cm.c.getPlayer();
				var totAp = p.getRemainingAp();
				var totSp = p.getRemainingSp();
				var totExp = p.getExp();
				var rand = 1 + Math.floor(Math.random() * 3);
				var newexp = cm.c.getPlayer().getExp();
				var totLevel = p.getLevel();
			if (donatorstatus == 1) {
				cm.updateRing();
				cm.gainItem(ring[selection], 1);
				cm.gainItem(1002186, 1);
				}
			if (donatorstatus == 2 && selection == 0) {
				cm.updateSM();
		mob = MapleLifeFactory.getMonster(9400202);
		cm.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, cm.getPlayer().getPosition());	
				}
			if (donatorstatus == 2 && selection == 1) {
				cm.updateSM();
		mob = MapleLifeFactory.getMonster(9400708);
		cm.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, cm.getPlayer().getPosition());	
				}
			if (donatorstatus == 2 && selection == 2) {
				cm.updateSM();
		mob = MapleLifeFactory.getMonster(9400566);
		cm.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, cm.getPlayer().getPosition());
			}
			if (donatorstatus == 3) {
				if (rand == 1) {
				cm.updateLottery();
				p.setPlayerLevel (totLevel + 1);
				p.setRemainingAp (totAp + 5);
				p.setRemainingSp (totSp + 3);
				statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.LEVEL, java.lang.Integer.valueOf(totLevel + 1)));
				statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.AVAILABLEAP, java.lang.Integer.valueOf(totAp + 5)));
				statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.AVAILABLESP, java.lang.Integer.valueOf(totSp + 3)));
				p.getClient().getSession().write (net.sf.odinms.tools.MaplePacketCreator.updatePlayerStats(statup));
				if (newexp < 0) {
					cm.c.getPlayer().gainExp(-newexp, false, false);
				}
				cm.sendOk("Congratulations! You have gained a level.");
				cm.dispose();
			} else if (rand == 2) {
				cm.updateLottery();
				p.setPlayerLevel (totLevel - 1);
				if (newexp < 0) {
					cm.c.getPlayer().gainExp(-newexp, false, false);
				}
				p.setRemainingAp (totAp - 5);
				p.setRemainingSp (totSp - 3);
				statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.LEVEL, java.lang.Integer.valueOf(totLevel - 1)));
				statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.AVAILABLEAP, java.lang.Integer.valueOf(totAp - 5)));
				statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.AVAILABLESP, java.lang.Integer.valueOf(totSp - 3)));
				p.getClient().getSession().write (net.sf.odinms.tools.MaplePacketCreator.updatePlayerStats(statup));
				cm.sendOk("Awww.. it would seem you were unlucky and lost a level.");
				cm.dispose();
			} else if (rand == 3) {
				cm.updateLottery();
				cm.sendOk("Awww.. it would seem you were unlucky and neither lost nor gained a level. Oh well, think of it as not losing anything?");
				cm.dispose();
				}
			}
		}
	}
}