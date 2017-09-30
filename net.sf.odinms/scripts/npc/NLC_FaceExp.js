/* Nerbit
	NLC Random Eye Change.
*/

importPackage(net.sf.odinms.client);

var status = 0;
var beauty = 0;
var price = 1000000;
var mface = Array(20000, 20001, 20002, 20003, 20004, 20005, 20006, 20007, 20008, 20012);
var fface = Array(21001, 21002, 21003, 21004, 21005, 21006, 21008, 21012, 21014, 21016);
var facenew = Array();

function start() {
    var selection = cm.sendSimple("Hi, I pretty much shouldn't be doing this, but with a #b#t5152033##k, I will do it anyways for you. But don't forget, it will be random!\r\n#L1#I would like to buy a #b#t5152033##k for " + price + " mesos, please!#l\r\n\#L2#I already have a Coupon!#l");
    if (selection == 1) {
        if(cm.getMeso() >= price) {
            cm.gainMeso(-price);
            cm.gainItem(5152033, 1);
            cm.sendOk("Enjoy!");
            cm.dispose();
        } else {
            cm.sendOk("You don't have enough mesos to buy a coupon!");
            cm.dispose();
        }
    } else if (selection == 2) {
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
        if (cm.sendYesNo("If you use the regular coupon, your face may transform into a random new look...do you still want to do it using #b#t5152033##k?") == 1) {
            if (cm.haveItem(5152033)){
                cm.gainItem(5152033, -1);
                cm.setFace(facenew[Math.floor(Math.random() * facenew.length)]);
                cm.sendOk("Enjoy your new and improved face!");
            } else {
                cm.sendOk("Hmm ... it looks like you don't have the coupon specifically for this place. Sorry to say this, but without the coupon, there's no plastic surgery for you...");
            }
        }
        cm.dispose();
    }
}