/* 
	DEFUALT (BALLOON = 2040036)
*/

function start() {
    cm.addText("Would you like to bring all your party to this map?");
    cm.sendYesNo();
    if (cm.getParty() != null) {
    cm.addText("Ok your party should all be here now!");
    cm.sendOk();
    cm.c.getPlayer().getParty().WarpMembers(100000000, cm.c);
    } else {
    cm.addText("You arent in a party.");
    cm.sendOk();
    }
    cm.dispose();
}