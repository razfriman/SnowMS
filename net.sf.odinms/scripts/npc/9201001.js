/*
Nana(H)
Maple-Ranking
*/
importPackage(net.sf.odinms.client);

var status = 0;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {

         
         if (mode == -1) {
		cm.dispose();
	} else {
		if (mode == 1)
			status++;
		else
			status--;
		
                 if (status == 0) {
		cm.sendNext("#fUI/UIWindow.img/ContextMenu/BtInfo/normal/0#\r\n" + "#bName: #k" + (cm.c.getPlayer().getName()) + "\r\n#bLevel:  #k#B" + (cm.getLevel() / 2 ) + "# (200)\r\n#bFame: #k#B" + (cm.c.getPlayer().getFame() / 300) + "# (30000)\r\n#bHP:      #k#B" + (cm.c.getPlayer().getMaxHp() / 300) + "# (30000)\r\n#bMP:      #k#B" + (cm.c.getPlayer().getMaxMp() / 300) + "# (30000)");
		
		}else if (status == 1) {
		cm.sendPrev("#fUI/UIWindow.img/ContextMenu/BtInfo/normal/0#\r\n" + "#bStr:   #k#B" + (cm.getChar().getStr() / 10) + "# (999)\r\n#bDex: #k#B" + (cm.getChar().getDex() / 10) + "# (999)\r\n#bInt:   #k#B" + (cm.getChar().getInt() / 10) + "# (999)\r\n#bLuk: #k#B" + (cm.getChar().getLuk() / 10) + "# (999)");
		cm.dispose();
		}
                 
                 }
                 }