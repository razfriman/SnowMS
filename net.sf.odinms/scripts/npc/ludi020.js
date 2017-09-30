/*
@	Author : Raz
@
@	NPC = Assistant Cheng
@	Map = Ludibrium : Toy Factory <Process 1> Zone 1 | Hidden Stret: Toy Factory <Sector 4>
@	NPC MapId = 220020000 | 922000000
@	Function = Start Missing Mechanical Parts JQ
@
*/

importPackage(net.sf.odinms.server.pq);
importPackage(net.sf.odinms.client);

var status = 0;
var jqName = "Missing Mechanical Part Jump-Quest"
var minlvl = 0;
var maxlvl = 200;
var time = 20;//Minutes
var open = true;


function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {//END
        cm.dispose();
    }else if (mode == 0){//NO
        cm.sendOk("Ok, talk to me when you want to get out.");
        cm.dispose();
    }else{
        if (mode == 1)
            status++;
        else
            status--;
        var em = cm.getEventManager("MissingMechPartsJQ");
        if(cm.getChar().getMap().getId() == 922000000){
            if (status == 0){
                cm.sendSimple("What would you like to do?\r\n\r\n#b#L0#Finish the Jump-Quest#l\r\n#L1#Leave this place#l");
            }else if (status == 1){
                var option = selection;
                if (option == 0){//Finish
                    if(cm.itemQuantity(4031092) >= 10){
                        cm.sendOk("PASS");
                    }else{
                        cm.sendOk("FAIL");
                        cm.dispose();
                    }
                }else if (option == 1){//LEAVE
                    cm.sendYesNo("Are you sure you want to leave?");
                }
            }else if (status == 2){
                if(option == 0){
                    cm.sendOk("Nice! you actually did it, lets get out of here.");
                }else if(option == 1){
                    cm.sendNext("Ok, see you next time!");
                }
            }else if (status == 3){
                if(option == 0){
                    var rewards = MaplePQRewards.JQrewards1;
                    MapleReward.giveReward(rewards, cm.getPlayer());
                    cm.warpRandom(220020000);
                    cm.dispose();

                }else if(option == 1){
                    cm.warpRandom(220020000);
                    cm.dispose();
                }
            }


        }else{//Map 220020000
            if (status == 0) {
                cm.sendYesNo("Would you like to enter the #b" + jqName + "#k?");
            }else if (status == 1){
                if (!checkLevel()){//WRONG LEVELS
                    cm.sendOk("Please check that your level is between #b" + minlvl + "~" + maxlvl)
                    cm.dispose();
                }else if (em == null){//EVENT ERROR
                    cm.sendOk("ERROR IN EVENT");
                    cm.dispose();
                }else if (!open){//MANUALLY CLOSED
                    cm.sendOk("The " + jqName + " has been closed");
                    cm.dispose();
                }else{//START EVENT
                    cm.sendOk("The " + jqName + " is open!");
                }
            }else if (status == 2){//START EVENT
                em.startSoloInstance(cm.getChar());
                cm.removeAll(4031092);
                cm.dispose();
            }
        }
    }
}

function checkLevel(){
    var pass = true;
    if (cm.getChar().getLevel() < minlvl || cm.getChar().getLevel() > maxlvl) {
        pass = false;
    }
    return pass;
}


					