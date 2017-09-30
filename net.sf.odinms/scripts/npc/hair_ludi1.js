/* Miyu
	Ludibrium VIP Hair/Hair Color Change.
*/

importPackage(net.sf.odinms.client);

var status = 0;
var beauty = 0;
var hairprice = 1000000;
var haircolorprice = 1000000;
var mhair = Array(30030, 30020, 30000, 30250, 30190, 30150, 30050, 30280, 30240, 30300, 30160);
var fhair = Array(31040, 31000, 31150, 31280, 31160, 31120, 31290, 31270, 31030, 31230, 31010);
var hairnew = Array();

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
			cm.sendSimple("Welcome, welcome, welcome to the Ludibrium Hair Salon! Do you, by any chance, have a #b#t5150007##k or a #b#t5151007##k? If so, how about letting me take care of your hair? Please choose what you want to do with it...\r\n#L0#I want to buy a coupon!#l\r\n#L1#Haircut: #i5150007##t5150007##l\r\n#L2#Dye your hair: #i5151007##t5151007##l");						
		} else if (status == 1) {
			if (selection == 0) {
				beauty = 0;
				cm.sendSimple("Which coupon would you like to buy?\r\n#L0#Haircut for " + hairprice + " mesos: #i5150007##t5150007##l\r\n#L1#Dye your hair for " + haircolorprice + " mesos: #i5151007##t5151007##l");
			} else if (selection == 1) {
				beauty = 1;
				hairnew = Array();
				if (cm.getChar().getGender() == MapleGender.MALE) {
					for(var i = 0; i < mhair.length; i++) {
						hairnew.push(mhair[i] + parseInt(cm.getChar().getHair()
 % 10));
					}
				} 
				if (cm.getChar().getGender() == MapleGender.FEMALE) {
					for(var i = 0; i < fhair.length; i++) {
						hairnew.push(fhair[i] + parseInt(cm.getChar().getHair()
 % 10));
					}
				}
				cm.sendStyle("I can completely change the look of your hair. Aren't you ready for a change? With #b#t5150007##k, I'll take care of the rest for you. Choose the style of your liking!", hairnew);
			} else if (selection == 2) {
				beauty = 2;
				haircolor = Array();
				var current = parseInt(cm.getChar().getHair()
/10)*10;
				for(var i = 0; i < 8; i++) {
					haircolor.push(current + i);
				}
				cm.sendStyle("I can completely change the color of your hair. Aren't you ready for a change? With #b#t5151007##k, I'll take care of the rest. Choose the color of your liking!", haircolor);
			}
		}
		else if (status == 2){
			cm.dispose();
			if (beauty == 1){
				if (cm.haveItem(5150007) == true){
					cm.gainItem(5150007, -1);
					cm.setHair(hairnew[selection]);
					cm.sendOk("Enjoy your new and improved hairstyle!");
				} else {
					cm.sendOk("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't give you a haircut without it. I'm sorry...");
				}
			}
			if (beauty == 2){
				if (cm.haveItem(5151007) == true){
					cm.gainItem(5151007, -1);
					cm.setHair(haircolor[selection]);
					cm.sendOk("Enjoy your new and improved haircolor!");
				} else {
					cm.sendOk("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't dye your hair without it. I'm sorry...");
				}
			}
			if (beauty == 0){
				if (selection == 0 && cm.getMeso() >= hairprice) {
					cm.gainMeso(-hairprice);
					cm.gainItem(5150007, 1);
					cm.sendOk("Enjoy!");
				} else if (selection == 1 && cm.getMeso() >= haircolorprice) {
					cm.gainMeso(-haircolorprice);
					cm.gainItem(5151007, 1);
					cm.sendOk("Enjoy!");
				} else {
					cm.sendOk("You don't have enough mesos to buy a coupon!");
				}
			}
		}
	}
}
