/* KIN
	GM-NPC  (9900000)
	Ellinia (180000000).
    Script(GmTool)
*/

importPackage(java.util);
importPackage(net.sf.odinms.client);
importPackage(net.sf.odinms.server);
importPackage(net.sf.odinms.net.channel);

var npcText = "";
var option;
var maps = Array(102000000, 101000000, 100000000, 103000000, 200000000, 230000000, 211000000, 105040300, 220000000, 221000000, 680000000, 250000000, 600000000, 251000000, 800000000, 801000000);
var selectmap = 0;
var mbeauty = 0;
var mhaircolor = Array();
var mskin = Array(0, 1, 2, 3, 4);
var mhair = Array(30000, 30010, 30020, 30030, 30040, 30050, 30060, 30070, 30080, 30090, 30100, 30110, 30120, 30130, 30140, 30150, 30160, 30170, 30180, 30190, 30200, 30210, 30220, 30230, 30240, 30250, 30260, 30270, 30280, 30290, 30300, 30310, 30320, 30330, 30340, 30350, 30360, 30370, 30400, 30410, 30420, 30430, 30440, 30450, 30460, 30470, 30480, 30490, 30510, 30520, 30530, 30540, 30550, 30560, 30570, 30580, 30590, 30600, 30610, 30620, 30630, 30640, 30650, 30660, 30670, 30680, 30690, 30700, 30710, 30720, 30730, 30740, 30750, 30760, 30770, 30780, 30790, 30800, 30810, 30820);
var mhairnew = Array();
var mface = Array(20000, 20001, 20002, 20003, 20004, 20005, 20006, 20007, 20008, 20009, 20010, 20011, 20012, 20013, 20014, 20016, 20017, 20018, 20019, 20020, 20021, 20022, 20023, 20024, 20025);
var mfacenew = Array();
var mcolors = Array();
var fbeauty = 0;
var fhaircolor = Array();
var fskin = Array(0, 1, 2, 3, 4);
var fhair = Array(31000, 31010, 31020, 31030, 31040, 31050, 31060, 31070, 31080, 31090, 31100, 31110, 31120, 31130, 31140, 31150, 31160, 31170, 31180, 31190, 31200, 31210, 31220, 31230, 31240, 31250, 31260, 31270, 31280, 31290, 31300, 31310, 31320, 31330, 31340, 31350, 31400, 31410, 31420, 31430, 31440, 31450, 31460, 31470, 31480, 31490, 31510, 31520, 31530, 31540, 31550, 31560, 31570, 31580, 31590, 31600, 31610, 31620, 31630, 31640, 31650, 31660, 31670, 31680, 31690, 31700, 31710, 31720, 31730, 31740, 31750, 31760, 31770, 31790, 31800);
var fhairnew = Array();
var fface = Array(21000, 21001, 21002, 21003, 21004, 21005, 21006, 21007, 21008, 21009, 21010, 21011, 21012, 21013, 21014, 21016, 21017, 21018, 21019, 21020, 21022, 21023, 21024);
var ffacenew = Array();
var fcolors = Array();
var targets = new Array();
var sendTarget;
var i;

function start() {
    if (!cm.c.getPlayer().isGM()) {
        cm.sendOk("Welcome to MapleStory!");
        cm.dispose();
    }
    npcText += "Welcome to the GM-Helper!#b\r\n";
    npcText += "#L0#Teleports#l\r\n";
    npcText += "#L1#Stylist#l\r\n";
    npcText += "#L2#GM-Shop#l\r\n";
    npcText += "#L3#Warp<>Players#l\r\n";
    npcText += "#L4#Get Player IP#l\r\n";
    option = cm.sendSimple(npcText);
    npcText = "";
    if (option == 0) { //Teleports
        npcText += "Choose a map\r\n";
        npcText += "#b";
        for (var i = 0; i < maps.length; i++) {
            npcText += "#L";
            npcText += i;
            npcText += "##m";
            npcText += maps[i];
            npcText += "##l\r\n";
        }
        selectmap = maps[cm.sendSimple(npcText)];
        cm.warp(selectmap, 0);
        cm.dispose();
    } else if (option == 1) {//Hair
        npcText += "Hey there! I could change the way you look! What would you like to change?#b\r\n";
        npcText += "#L0#Skin#l\r\n";
        npcText += "#L1#Hair#l\r\n";
        npcText += "#L2#Hair Color#l\r\n";
        npcText += "#L3#Eye#l\r\n";
        npcText += "#L4#Eye Color#l";
        var v1 = cm.sendSimple(npcText);
        var v2;
        var current;

        if (cm.getChar().getGender() == MapleGender.MALE) {
            if (v1 == 0) {
                mbeauty = 1;
                v2 = cm.sendStyle("Pick one?", mskin);
            } else if (v1 == 1) {
                mbeauty = 2;
                mhairnew = Array();
                for(i = 0; i < mhair.length; i++) {
                    mhairnew.push(mhair[i] + parseInt(cm.getChar().getHair() % 10));
                }
                v2 = cm.sendStyle("Pick one?", mhairnew);
            } else if (v1 == 2) {
                mbeauty = 3;
                mhaircolor = Array();
                current = parseInt(cm.getChar().getHair() / 10) * 10;
                for(i = 0; i < 8; i++) {
                    mhaircolor.push(current + i);
                }
                v2 = cm.sendStyle("Pick one?", mhaircolor);
            } else if (v1 == 3) {
                mbeauty = 4;
                mfacenew = Array();
                for(i = 0; i < mface.length; i++) {
                    mfacenew.push(mface[i] + cm.getChar().getFace() % 1000 - (cm.getChar().getFace() % 100));
                }
                v2 = cm.sendStyle("Pick one?", mfacenew);
            } else if (v1 == 4) {
                mbeauty = 5;
                current = cm.getChar().getFace() % 100 + 20000;
                mcolors = Array();
                mcolors = Array(current , current + 100, current + 200, current + 300, current +400, current + 500, current + 600, current + 700);
                v2 = cm.sendStyle("Pick one?", mcolors);

            }
        } else if (cm.getChar().getGender() == MapleGender.FEMALE) {
            if (v1 == 0) {
                fbeauty = 1;
                v2 = cm.sendStyle("Pick one?", fskin);
            } else if (v1 == 1) {
                fbeauty = 2;
                fhairnew = Array();
                for(i = 0; i < fhair.length; i++) {
                    fhairnew.push(fhair[i] + parseInt(cm.getChar().getHair() % 10));
                }
                v2 = cm.sendStyle("Pick one?", fhairnew);
            } else if (v1 == 2) {
                fbeauty = 3;
                fhaircolor = Array();
                current = parseInt(cm.getChar().getHair() / 10) * 10;
                for(i = 0; i < 8; i++) {
                    fhaircolor.push(current + i);
                }
                v2 = cm.sendStyle("Pick one?", fhaircolor);
            } else if (v1 == 3) {
                fbeauty = 4;
                ffacenew = Array();
                for(i = 0; i < fface.length; i++) {
                    ffacenew.push(fface[i] + cm.getChar().getFace() % 1000 - (cm.getChar().getFace() % 100));
                }
                v2 = cm.sendStyle("Pick one?", ffacenew);
            } else if (v1 == 4) {
                fbeauty = 5;
                current = cm.getChar().getFace() % 100 + 21000;
                fcolors = Array();
                fcolors = Array(current , current + 100, current + 200, current + 300, current +400, current + 500, current + 600, current + 700);
                v2 = cm.sendStyle("Pick one?", fcolors);
            }
        }

        if (mbeauty == 1) {
            cm.setSkin(mskin[v2]);
        }
        if (mbeauty == 2) {
            cm.setHair(mhairnew[v2]);
        }
        if (mbeauty == 3) {
            cm.setHair(mhaircolor[v2]);
        }
        if (mbeauty == 4) {
            cm.setFace(mfacenew[v2]);
        }
        if (mbeauty == 5) {
            cm.setFace(mcolors[v2]);
        }
        if (fbeauty == 1) {
            cm.setSkin(fskin[v2]);
        }
        if (fbeauty == 2) {
            cm.setHair(fhairnew[v2]);
        }
        if (fbeauty == 3) {
            cm.setHair(fhaircolor[v2]);
        }
        if (fbeauty == 4) {
            cm.setFace(ffacenew[v2]);
        }
        if (fbeauty == 5) {
            cm.setFace(fcolors[v2]);
        }
        cm.dispose();
    } else if (option == 2) {// GM-Shop
        cm.openShop(56);
        cm.dispose();
    } else if (option == 3) { // Warp Players
        npcText += "Please select a channel a character to warp to\r\n";
        npcText += "  #e#d(CH" + cm.getPlayer().getClient().getChannel() + ")#n#k\r\n";
        var iter = cm.getPlayer().getClient().getChannelServer().getPlayerStorage().getAllCharacters().iterator();
        i = 0;
        targets = new Array();
        while (iter.hasNext()) {
            var curChar = iter.next();
            if(curChar.isGM()) {
                npcText += "\r\n#L" + i + "##r" + curChar.getName() + "#l";
            } else {
                npcText += "\r\n#L" + i + "##b" + curChar.getName() + "#l";
            }
            targets[i] = curChar;
            i++;
        }
        sendTarget = targets[cm.sendSimple(npcText)];
        cm.warp(sendTarget.getMap().getId());
        cm.dispose();
    } else if (option == 4) { // Player IP
        npcText += "Please enter a Player-Name";
        var name = cm.sendGetText(npcText);
        npcText = "";
        var chr = cm.getChr(name);
        if (chr != null) {
            npcText += "#b" + chr.getName() + "'s#b#k IP Address: #e"
            npcText += cm.getChr(name).getClient().getSession().getRemoteAddress();
        } else {
            npcText += "#b" +  name  + "#k is #rOffline#k";
        }
        cm.sendOk(npcText);
        cm.dispose();
    }
}