/* Noma
	Mu Lung Random/VIP Eye Change.
*/

importPackage(net.sf.odinms.client);

var status = 0;
var beauty = 0;
var regprice = 1000000;
var vipprice = 1000000;
var mface = Array(20000, 20001, 20002, 20003, 20004, 20005, 20006, 20007, 20008, 20012, 20014, 20009, 20010);
var fface = Array(21000, 21001, 21002, 21003, 21004, 21005, 21006, 21007, 21008, 21012, 21014, 21009, 21011);
var facenew = Array();

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
			cm.sendSimple("Hey, I'm Noma, and I am assiting Pata in changing faces into beautiful things here in Mu Lung. With #b#t5152027##k or #b#t5152028##k, I can change the way you look. Now, what would you like to use?\r\n#L0#I want to buy a coupon!#l\r\n#L1#Plastic Surgery: #i5152027##t5152027##l\r\n#L2#Plastic Surgery: #i5152028##t5152028##l");						
		} else if (status == 1) {
			if (selection == 0) {
				beauty = 0;
				cm.sendSimple("Which coupon would you like to buy?\r\n#L0#Plastic Surgery for " + regprice + " mesos: #i5152027##t5152027##l\r\n#L1#Plastic Surgery for " + vipprice + " mesos: #i5152028##t5152028##l");
			} else if (selection == 1) {
				beauty = 1;
				facenew = Array();
				if (cm.getChar().getGender() == MapleGender.MALE) {
					for(var i = 0; i < mface.length; i++) {
						facenew.push(mface[i] + cm.getChar().getFace() % 1000 - (cm.getChar().getFace() % 100));
					}
				}
				if (cm.getChar().getGender() == MapleGender.FEMALE) {
					for(var i = 0; i < fface.length; i++) {
						facenew.push(fface[i] + cm.getChar().getFace() % 1000 - (cm.getChar().getFace() % 100));
					}
				}
				cm.sendYesNo("If you use the regular coupon, your face may transform into a random new look...do you still want to do it using #b#t5152027##k?");
			} else if (selection == 2) {
				beauty = 2;
				if (cm.getChar().getGender() == MapleGender.MALE) {
					for(var i = 0; i < mface.length; i++) {
						facenew.push(mface[i] + cm.getChar().getFace() % 1000 - (cm.getChar().getFace() % 100));
					}
				}
				if (cm.getChar().getGender() == MapleGender.FEMALE) {
					for(var i = 0; i < fface.length; i++) {
						facenew.push(fface[i] + cm.getChar().getFace() % 1000 - (cm.getChar().getFace() % 100));
					}
				}
				cm.sendStyle("I can totally transform your face into something new... how about giving us a try? For #b#t5152028##k, you can get the face of your liking...take your time in choosing the face of your preference.", facenew);
			}
		}
		else if (status == 2){
			cm.dispose();
			if (beauty == 1){
				if (cm.haveItem(5152027) == true){
					cm.gainItem(5152027, -1);
					cm.setFace(facenew[Math.floor(Math.random() * facenew.length)]);
					cm.sendOk("Enjoy your new and improved face!");
				} else {
					cm.sendOk("I'm sorry, but I don't think you have our plastic surgery coupon with you right now. Without the coupon, I'm afraid I can't do it for you..");
				}
			}
			if (beauty == 2){
				if (cm.haveItem(5152028) == true){
					cm.gainItem(5152028, -1);
					cm.setFace(facenew[selection]);
					cm.sendOk("Enjoy your new and improved face!");
				} else {
					cm.sendOk("I'm sorry, but I don't think you have our plastic surgery coupon with you right now. Without the coupon, I'm afraid I can't do it for you..");
				}
			}
			if (beauty == 0){
				if (selection == 0 && cm.getMeso() >= regprice) {
					cm.gainMeso(-regprice);
					cm.gainItem(5152012, 1);
					cm.sendOk("Enjoy!");
				} else if (selection == 1 && cm.getMeso() >= vipprice) {
					cm.gainMeso(-vipprice);
					cm.gainItem(5152028, 1);
					cm.sendOk("Enjoy!");
				} else {
					cm.sendOk("You don't have enough mesos to buy a coupon!");
				}
			}
		}
	}
}
