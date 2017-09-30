/*
@	Author : Raz
@
@	NPC = Amos the Strong
@	Map = Hidden Street <Entrance of Amorian Challenge>
@	NPC MapId = 670010100
@	Function = Start APQ
@
*/

importPackage(net.sf.odinms.client);

//ENTRANCE

/* 
	Amos the Strong		     (9201043)-(9201044)-(9201045)-(9201046)-(9201048)
	Amos' Training Ground	     (670010000)
	Entrance of Amorian Challenge(670010100)
	Stage 1 - Magik Mirror	     (670010100)
	Stage 2 - Heart Strings	     (670010100)
	Stage 3 - Twisted Switcher   (670010100)
	Stage 4 - Last Man Standing  (670010100)
	Stage 5 - Fluttering Hearts  (670010100)
	Stage 6 - Love Hurts	     (670010100)
	Stage 7 - Amos' Vault	     (670010100)
*/
var pqName = "Amoria-PQ";
var status = 0;
var minlvl = 0;
var maxlvl = 200;
var minplayers = 0;
var maxplayers = 6;
var needBoy = false;
var needGirl = false;
var time = 30;//Minutes
var open = true;




function start() {
    var em = cm.getEventManager("AmoriaPQ");
        
    var ret = cm.sendSimple("Okay. What would you like to do?\r\n#b#L0# I'd like to start the Party Quest.\r\n#L1# Please get us out of here!#l#k");
    if (ret == 1) {
        cm.sendOk("Hmm... Well, see you next time. Bye~!");
    //registerTransferField( 670010000, "st00" );
    } else if (ret == 0) {
        //if (!cm.isPartyLeader()) {
        if (false) {
            cm.sendOk("How about you and your party members collectively beating a quest? Here you'll find obstacles and problems where you won't be able to beat it unless with great teamwork. If you want to try it, please tell the #bleader of your party#k to talk to me.");
        } else {
            cm.sendOk("Good, the leader of the party is here. Now, are you and your party members ready for this? I'll send you guys now to the entrance of the Amoria Party Quest. Best of luck to each and every one of you!");
            var res = enterPartyQuest();
            if (res == -1) {
                cm.sendOk("Due to unknown reason, I can't let your party go in. Please try again later.");
            } else if (res == 1) {
                cm.sendOk("You are not in the party. You only can do this quest when you are in the party.");
            } else if (res == 2) {
                cm.sendOk("Your party is not a party of 6. Please come back when you have 6 party members.");
            } else if (res == 3) {
                cm.sendOk("Someone in your party does not have a level over 40. Please double-check.");
            //} else if (res == 4) {
            //cm.sendOk("Some other party has already gotten in to try clearing the quest. Please try again later.");
            } else {
            takeAwayItem();
            em.startInstance(cm.getParty(), cm.getChar().getMap());
            var party = cm.getPlayer().getEventInstance().getPlayers();
            }
        }
    }
}



function enterPartyQuest()  {
    return 0;
    if (cm.getParty() == null) {
        return 1;
    } else if (!checkPartySize()) {
        return 2;
    } else if(!checkPartyLevels()) {
        return 3;
    } else {
        return 0;
    }
}

    function takeAwayItem() {
        //DO NOTHIGN FOR NOW
    }

    function isMarried() {
        return true;
    }

    function checkPartySize() {
        var size = 0;
        if(cm.getPlayer().getParty() == null) {
            size = 0;
        } else {
            size = (cm.getPlayer().getParty().getMembers().size());
        }
        return size < minplayers || size > maxplayers;
    }

    function checkPartyLevels(){
        var pass = true;
        var party = cm.getPlayer().getParty().getMembers();
        if(cm.getPlayer().getParty() == null) {
            pass = false;
        } else {
            for (var i = 0; i < party.size() && pass; i++) {
                if ((party.get(i).getLevel() < minlvl) || (party.get(i).getLevel() > maxlvl) || (party.get(i).getMapid() != cm.getMapId())) {
                    pass = false;
                }
            }
        }
        return pass;
    }

    function hasParty() {
        return cm.getPlayer().getParty() == null;
    }

    function checkGender() {
        var boy = true;
        var girl = true;
        if(needBoy)
            boy = false;
        if(needGirl)
            girl = false;

        if(cm.getPlayer().getParty() == null){
            return false;
        }else{
            var party = cm.getChar().getParty().getMembers();
            for(var i = 0; i < party.size(); i++){
                var player = party.get(i);
                if(player.getChar().getGender() == MapleGender.MALE){
                    boy = true;
                }else if (player.getChar().getGender() == MapleGender.FEMALE){
                    girl = true;
                }
            }
            if(boy && girl){
                return true;
            }else{
                return false;
            }

        }
    }
					