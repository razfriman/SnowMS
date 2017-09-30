/* Ellie
	Ludibrium VIP Eye Change.
*/

importPackage(net.sf.odinms.client);

var status = 0;
var beauty = 0;
var price = 1000000;
var mface = Array(20000, 20001, 20002, 20003, 20004, 20005, 20006, 20007, 20008, 20012, 20014);
var fface = Array(21000, 21001, 21002, 21003, 21004, 21005, 21006, 21007, 21008, 21012, 21014);
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
			cm.sendSimple("Well, hello! Welcome to the Ludibrium Plastic Surgery! Would you like to transform your face into something new? With a #b#t5152007##k, you can let us take care of the rest and have the face you've always wanted~!\r\n#L1#I would like to buy a #b#t5152007##k for " + price + " mesos, please!#l\r\n\#L2#I already have a Coupon!#l");
			} else if (status == 1) {
			if (selection == 1) {
				if(cm.getMeso() >= price) {
					cm.gainMeso(-price);
					cm.gainItem(5152007, 1);
					cm.sendOk("Enjoy!");
				} else {
					cm.sendOk("You don't have enough mesos to buy a coupon!");
				}
				cm.dispose();
			} else if (selection == 2) {
				facenew = Array();
				if (cm.getChar().getGender() == MapleGender.MALE) {
					for(var i = 0; i < mface.length; i++) {
						facenew.push(mface[i] + cm.getChar().getFace()
 % 1000 - (cm.getChar().getFace()
 % 100));
					}
				}
				if (cm.getChar().getGender() == MapleGender.FEMALE) {
					for(var i = 0; i < fface.length; i++) {
						facenew.push(fface[i] + cm.getChar().getFace()
 % 1000 - (cm.getChar().getFace()
 % 100));
					}
				}
				cm.sendStyle("Let's see... I can totally transform your face into something new. Don't you want to try it? For #b#t5152007##k, you can get the face of your liking. Take your time in choosing the face of your preference.", facenew);
			}
		}
		else if (status == 2){
			cm.dispose();
			if (cm.haveItem(5152007) == true){
				cm.gainItem(5152007, -1);
				cm.setFace(facenew[selection]);
				cm.sendOk("Enjoy your new and improved face!");
			} else {
				cm.sendOk("Hmm ... it looks like you don't have the coupon specifically for this place. Sorry to say this, but without the coupon, there's no plastic surgery for you...");
			}
		}
	}
}
