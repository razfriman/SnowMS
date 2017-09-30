/*
@	Author : Raz
@
@	NPC = Jeff
@	Map = El-Nath <Ice Valley II>
@	NPC MapId = 211040200
@	Function = Warp to Dungeon
@
*/

function start() {
    cm.addText("Hey, you look like you want to go farther and deeper past this place. Over there, though, you'll find yourself surrounded by aggressive, dangerous monsters, so even if you feel that you're ready to go, please be careful. Long ago, a few brave men from our town went in wanting to eliminate anyone threatening the town, but never came back out...");
    if (cm.getPlayer().getLevel() < 50) {
        cm.addText(" you haven't reached Level 50 yet. I can't let you in, then, so forget it.");
        cm.sendPrev();
        cm.dispose();
    } else {
        cm.addText("! You look pretty strong. All right, do you want to go in?");
        if (cm.sendYesNo()) {
            cm.warp(211040300);
        } else {
            cm.sendNext("Even if your level's high it's hard to actually go in there, but if you ever change your mind please find me. After all, my job is to protect this place.");
        }
    }
    cm.dispose();
}