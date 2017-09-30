/* J.J.
	NLC VIP Eye Color Change.
*/

importPackage(net.sf.odinms.client);

var status = 0;
var price = 1000000;
var colors = Array();

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
			cm.sendSimple("Hey, there~! I'm J.J.! I'm in charge of the cosmetic lenses here at NLC Shop! If you have a #b#t5152036##k, I can get you the best cosmetic lenses you have ever had! Now, what would you like to do?\r\n#L1#I would like to buy a #b#t5152036##k for " + price + " mesos, please!#l\r\n\#L2#I already have a Coupon!#l");						
		} else if (status == 1) {
			if (selection == 1) {
				if(cm.getMeso() >= price) {
					cm.gainMeso(-price);
					cm.gainItem(5152036, 1);
					cm.sendOk("Enjoy!");
				} else {
					cm.sendOk("You don't have enough mesos to buy a coupon!");
				}
				cm.dispose();
			} else if (selection == 2) {
				if (cm.getChar().getGender() == MapleGender.MALE) {
					var current = cm.getChar().getFace()
 % 100 + 20000;
				}
				if (cm.getChar().getGender() == MapleGender.FEMALE) {
					var current = cm.getChar().getFace()
 % 100 + 21000;
				}
				colors = Array();
				colors = Array(current , current + 100, current + 200, current + 300, current +400, current + 500, current + 600, current + 700);
				cm.sendStyle("With our specialized machine, you can see yourself after the treatment in advance. What kind of lens would you like to wear? Choose the style of your liking.", colors);
			}
		}
		else if (status == 2){
			cm.dispose();
			if (cm.haveItem(5152036) == true){
				cm.gainItem(5152036, -1);
				cm.setFace(colors[selection]);
				cm.sendOk("Enjoy your new and improved cosmetic lenses!");
			} else {
				cm.sendOk("I'm sorry, but I don't think you have our cosmetic lens coupon with you right now. Without the coupon, I'm afraid I can't do it for you..");			
			}
		}
	}
}
