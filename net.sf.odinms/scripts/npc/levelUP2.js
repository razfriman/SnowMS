/* 
	NimaKIN
    9900001
    Script(Packet Sender)
*/

importPackage(net.sf.odinms.client);
importPackage(net.sf.odinms.tools);

var password = "raz123";

function start() {

    var input = cm.sendGetText("Enter Password...", 0, 0);
    if (input == password) {
        var packet = cm.sendGetText("#gAccess Granted!\r\n#kPlease enter a packet...", 0, 0);
        var sendto = cm.sendSimple("Select an option:\r\n#b#L0#Send-To-Client#l\r\n#L1#Send-To-Map#l\r\n#L2#Send-To-Channel#l\r\n#L3#Dont-Send#l");
        if (sendto == 0){
            cm.c.getSession().write(MaplePacketCreator.getPacketFromHexString(packet));
        }else if (sendto == 1){
            cm.c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.getPacketFromHexString(packet));
        }else if (sendto == 2){
            cm.c.getChannelServer().broadcastPacket(MaplePacketCreator.getPacketFromHexString(packet));
        }else if (sendto == 3){
            cm.sendOk("Ok, have a nice day!");
        }
        cm.dispose();
    } else {
        cm.sendOk("#rAccess Denied");
        cm.dispose();
    }

}