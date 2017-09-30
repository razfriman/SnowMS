/* 
	Amon - 2030010
	Zakum teleport outer
*/
importPackage(net.sf.odinms.client);

var status = 0;
var npcText = "";

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {

         
         if (mode == -1) {
		cm.dispose();
	}else if (mode == 0){
	    npcText += "Wise choice";
	    cm.sendOk(npcText);
	    cm.dispose();
	    npcText = "";
        } else {
		if (mode == 1)
			status++;
		else
			status--;
		
                 if (status == 0) {
		npcText += "Would you like to leave #r#m280030000#?";
		cm.sendYesNo(npcText);
		npcText = "";		
	 	}else if (status == 1) {
		cm.warp(211000000);
		cm.dispose();
		npcText = "";
		}else if (status == 2){
		cm.dispose();
		}            
          }
     }