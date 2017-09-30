/* Miranda
	NLC Skin Change.
*/

var price = 1000000;
var skin = Array(0, 1, 2, 3, 4);

function start() {
    var selection = cm.sendSimple("Well, hello! Welcome to the NLC Skin-Care! Would you like to have a firm, tight, healthy looking skin like mine?  With #b#t5153009##k, you can let us take care of the rest and have the kind of skin you've always wanted~!\r\n#L1#I would like to buy a #b#t5153009##k for " + price + " mesos, please!#l\r\n\#L2#I already have a Coupon!#l");
	if (selection == 1) {
		if(cm.getMeso() >= price) {
			cm.gainMeso(-price);
            cm.gainItem(5153009, 1);
			cm.sendOk("Enjoy!");
		} else {
			cm.sendOk("You don't have enough mesos to buy a coupon!");
		}
		cm.dispose();
	} else if (selection == 2) {
		selection = cm.sendStyle("With our specialized machine, you can see the way you'll look after the treatment PRIOR to the procedure. What kind of a look are you looking for? Go ahead and choose the style of your liking~!", skin);
        if (cm.haveItem(5153009) == true){
			cm.gainItem(5153009, -1);
			cm.setSkin(skin[selection]);
			cm.sendOk("Enjoy your new and improved skin!");
		} else {
			cm.sendOk("Um...you don't have the skin-care coupon you need to receive the treatment. Sorry, but I am afraid we can't do it for you...");
		}
        cm.dispose();
	}
}