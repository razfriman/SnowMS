/* 
Cody
Job-Advance NPC on All town maps
*/


importPackage(net.sf.odinms.client);
var status = 0;
var job;
var jobname;

function start() {
    status = -1;
    action(1, 0, 0);
}


function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else if (mode == 0) {
        cm.dispose();
    } else {
        if (mode == 1) {
            status++;
        } else {
            status--;
        }
        if (status == 0) {
            if (cm.getJob().equals(MapleJob.BEGINNER) &&
                cm.getLevel() >= 10) {
                cm.sendSimple("Welcome I'm the #bJob-Advancer#k!\r\nPlease select a job.#b\r\n#L0#Warrior#l\r\n#L1#Magician#l\r\n#L2#Bowman#l\r\n#L3#Thief#l\r\n#L4#Pirate#l#k");
            } else if (cm.getJob().equals(MapleJob.BEGINNER) &&
                cm.getLevel() == 8 ||
                cm.getLevel() == 9) {
                cm.sendSimple("Welcome I'm the #bJob-Advancer#k!\r\nPlease select a job.#b\r\n#L1#Magician#l#k");
            } else if (cm.getJob().equals(MapleJob.WARRIOR) &&
                cm.getLevel() >= 30) {
                cm.sendSimple("Welcome I'm the #bJob-Advancer#k!\r\nPlease select a job.#b\r\n#L5#Fighter#l\r\n#L6#Page#l\r\n#L7#Spearman#l#k");
            } else if (cm.getJob().equals(MapleJob.MAGICIAN) &&
                cm.getLevel() >= 30) {
                cm.sendSimple("Welcome I'm the #bJob-Advancer#k!\r\nPlease select a job.#b\r\n#L8#Ice-Lightning Wizard#l\r\n#L9#Fire-Poison Wizard#l\r\n#L10#Cleric#l#k");
            } else if (cm.getJob().equals(MapleJob.BOWMAN) &&
                cm.getLevel() >= 30) {
                cm.sendSimple("Welcome I'm the #bJob-Advancer#k!\r\nPlease select a job.#b\r\n#L11#Hunter#l\r\n#L12#Crossbow Man#l#k");
            } else if (cm.getJob().equals(MapleJob.THIEF) &&
                cm.getLevel() >= 30) {
                cm.sendSimple("Welcome I'm the #bJob-Advancer#k!\r\nPlease select a job.#b\r\n#L13#Assassin#l\r\n#L14#Bandit#l#k");
            } else if (cm.getJob().equals(MapleJob.PIRATE) &&
                cm.getLevel() >= 30) {
                cm.sendSimple("Welcome I'm the #bJob-Advancer#k!\r\nPlease select a job.#b\r\n#L15#Brawler#l\r\n#L16#Gunslinger#l#k");
            } else if (cm.getJob().equals(MapleJob.FIGHTER) &&
                cm.getLevel() >= 70) {
                cm.sendSimple("Welcome I'm the #bJob-Advancer#k!\r\nPlease select a job.#b\r\n#L17#Crusader#l#k");
            } else if (cm.getJob().equals(MapleJob.PAGE) &&
                cm.getLevel() >= 70) {
                cm.sendSimple("Welcome I'm the #bJob-Advancer#k!\r\nPlease select a job.#b\r\n#L18#Paladin#l#k");
            } else if (cm.getJob().equals(MapleJob.SPEARMAN) &&
                cm.getLevel() >= 70) {
                cm.sendSimple("Welcome I'm the #bJob-Advancer#k!\r\nPlease select a job.#b\r\n#L19#Dragon Knight#l#k");
            } else if (cm.getJob().equals(MapleJob.IL_WIZARD) &&
                cm.getLevel() >= 70) {
                cm.sendSimple("Welcome I'm the #bJob-Advancer#k!\r\nPlease select a job.#b\r\n#L20#Ice-Lightning Mage#l#k");
            } else if (cm.getJob().equals(MapleJob.FP_WIZARD) &&
                cm.getLevel() >= 70) {
                cm.sendSimple("Welcome I'm the #bJob-Advancer#k!\r\nPlease select a job.#b\r\n#L21#Fire-Poison Mage#l#k");
            } else if (cm.getJob().equals(MapleJob.CLERIC) &&
                cm.getLevel() >= 70) {
                cm.sendSimple("Welcome I'm the #bJob-Advancer#k!\r\nPlease select a job.#b\r\n#L22#Priest#l#k");
            } else if (cm.getJob().equals(MapleJob.HUNTER) &&
                cm.getLevel() >= 70) {
                cm.sendSimple("Welcome I'm the #bJob-Advancer#k!\r\nPlease select a job.#b\r\n#L23#Ranger#l#k");
            } else if (cm.getJob().equals(MapleJob.CROSSBOWMAN) &&
                cm.getLevel() >= 70) {
                cm.sendSimple("Welcome I'm the #bJob-Advancer#k!\r\nPlease select a job.#b\r\n#L24#Sniper#l#k");
            } else if (cm.getJob().equals(MapleJob.ASSASSIN) &&
                cm.getLevel() >= 70) {
                cm.sendSimple("Welcome I'm the #bJob-Advancer#k!\r\nPlease select a job.#b\r\n#L25#Hermit#l#k");
            } else if (cm.getJob().equals(MapleJob.BANDIT) &&
                cm.getLevel() >= 70) {
                cm.sendSimple("Welcome I'm the #bJob-Advancer#k!\r\nPlease select a job.#b\r\n#L26#Chief-Bandit#l#k");
            } else if (cm.getJob().equals(MapleJob.BRAWLER) &&
                cm.getLevel() >= 70) {
                cm.sendSimple("Welcome I'm the #bJob-Advancer#k!\r\nPlease select a job.#b\r\n#L27#Outlaw#l#k");
            } else if (cm.getJob().equals(MapleJob.GUNSLINGER) &&
                cm.getLevel() >= 70) {
                cm.sendSimple("Welcome I'm the #bJob-Advancer#k!\r\nPlease select a job.#b\r\n#L28#Marauder#l#k");
            } else if (cm.getJob().equals(MapleJob.CRUSADER) &&
                cm.getLevel() >= 120) {
                cm.sendSimple("Welcome I'm the #bJob-Advancer#k!\r\nPlease select a job.#b\r\n#L29#Hero#l#k");
            } else if (cm.getJob().equals(MapleJob.WHITEKNIGHT) &&
                cm.getLevel() >= 120) {
                cm.sendSimple("Welcome I'm the #bJob-Advancer#k!\r\nPlease select a job.#b\r\n#L30#Paladin#l#k");
            } else if (cm.getJob().equals(MapleJob.DRAGONKNIGHT) &&
                cm.getLevel() >= 120) {
                cm.sendSimple("Welcome I'm the #bJob-Advancer#k!\r\nPlease select a job.#b\r\n#L31#Dark-Knight#l#k");
            } else if (cm.getJob().equals(MapleJob.IL_MAGE) &&
                cm.getLevel() >= 120) {
                cm.sendSimple("Welcome I'm the #bJob-Advancer#k!\r\nPlease select a job.#b\r\n#L32#Ice-Lightning Arch Mage#l#k");
            } else if (cm.getJob().equals(MapleJob.FP_MAGE) &&
                cm.getLevel() >= 120) {
                cm.sendSimple("Welcome I'm the #bJob-Advancer#k!\r\nPlease select a job.#b\r\n#L33#Fire-Poison Arch Mage#l#k");
            } else if (cm.getJob().equals(MapleJob.PRIEST) &&
                cm.getLevel() >= 120) {
                cm.sendSimple("Welcome I'm the #bJob-Advancer#k!\r\nPlease select a job.#b\r\n#L34#Bishop#l#k");
            } else if (cm.getJob().equals(MapleJob.RANGER) &&
                cm.getLevel() >= 120) {
                cm.sendSimple("Welcome I'm the #bJob-Advancer#k!\r\nPlease select a job.#b\r\n#L35#Bowmaster#l#k");
            } else if (cm.getJob().equals(MapleJob.SNIPER) &&
                cm.getLevel() >= 120) {
                cm.sendSimple("Welcome I'm the #bJob-Advancer#k!\r\nPlease select a job.#b\r\n#L36#Marksman#l#k");
            } else if (cm.getJob().equals(MapleJob.HERMIT) &&
                cm.getLevel() >= 120) {
                cm.sendSimple("Welcome I'm the #bJob-Advancer#k!\r\nPlease select a job.#b\r\n#L37#Night-Lord#l#k");
            } else if (cm.getJob().equals(MapleJob.CHIEFBANDIT) &&
                cm.getLevel() >= 120) {
                cm.sendSimple("Welcome I'm the #bJob-Advancer#k!\r\nPlease select a job.#b\r\n#L38#Shadower#l#k");
            } else if (cm.getJob().equals(MapleJob.OUTLAW) &&
                cm.getLevel() >= 120) {
                cm.sendSimple("Welcome I'm the #bJob-Advancer#k!\r\nPlease select a job.#b\r\n#L39#Corsair#l#k");
            } else if (cm.getJob().equals(MapleJob.MARAUDER) &&
                cm.getLevel() >= 120) {
                cm.sendSimple("Welcome I'm the #bJob-Advancer#k!\r\nPlease select a job.#b\r\n#L40#Buccaneer#l#k");
            } else if (cm.getJob().equals(MapleJob.GM)) {
                cm.sendSimple("Welcome I'm the #bJob-Advancer#k!\r\nPlease select a job.#b\r\n#L41#Super-GM#l#k");
            } else if (cm.getJob().equals(MapleJob.SUPERGM)) {
                cm.sendSimple("Welcome I'm the #bJob-Advancer#k!\r\nPlease select a job.#b\r\n#L42#Beginner#l#k");
            } else {
                cm.sendOk("You have no Job-Advancements available, please keep training!");
                cm.dispose();
            }
        } else if (status == 1) {
            if (selection == 0) {
                job = MapleJob.WARRIOR;
                jobname = "Warrior";
            } else if (selection == 1) {
                job = MapleJob.MAGICIAN;
                jobname = "Magician";
            } else if (selection == 2) {
                job = MapleJob.BOWMAN;
                jobname = "Bowman";
            } else if (selection == 3) {
                job = MapleJob.THIEF;
                jobname = "Thief";
            } else if (selection == 4) {
                job = MapleJob.PIRATE;
                jobname = "Pirate";
            } else if (selection == 5) {
                job = MapleJob.FIGHTER;
                jobname = "Fighter";
            } else if (selection == 6) {
                job = MapleJob.PAGE;
                jobname = "Page";
            } else if (selection == 7) {
                job = MapleJob.SPEARMAN;
                jobname = "Spearman";
            } else if (selection == 8) {
                job = MapleJob.IL_WIZARD;
                jobname = "Ice-Lightning Wizard";
            } else if (selection == 9) {
                job = MapleJob.FP_WIZARD;
                jobname = "Fire-Poison Wizard";
            } else if (selection == 10) {
                job = MapleJob.CLERIC;
                jobname = "Cleric";
            } else if (selection == 11) {
                job = MapleJob.HUNTER;
                jobname = "Hunter";
            } else if (selection == 12) {
                job = MapleJob.CROSSBOWMAN;
                jobname = "Crossbow Man";
            } else if (selection == 13) {
                job = MapleJob.ASSASSIN;
                jobname = "Assassin";
            } else if (selection == 14) {
                job = MapleJob.BANDIT;
                jobname = "Bandit";
            } else if (selection == 15) {
                job = MapleJob.GUNSLINGER;
                jobname = "Gunslinger";
            } else if (selection == 16) {
                job = MapleJob.BRAWLER;
                jobname = "Brawler";
            } else if (selection == 17) {
                job = MapleJob.CRUSADER;
                jobname = "Crusader";
            } else if (selection == 18) {
                job = MapleJob.WHITEKNIGHT;
                jobname = "White-Knight";
            } else if (selection == 19) {
                job = MapleJob.DRAGONKNIGHT;
                jobname = "Dragon-Knight";
            } else if (selection == 20) {
                job = MapleJob.IL_MAGE;
                jobname = "Ice-Lightning Mage";
            } else if (selection == 21) {
                job = MapleJob.FP_MAGE;
                jobname = "Fire-Poison Mage";
            } else if (selection == 22) {
                job = MapleJob.PRIEST;
                jobname = "Priest";
            } else if (selection == 23) {
                job = MapleJob.RANGER;
                jobname = "Ranger";
            } else if (selection == 24) {
                job = MapleJob.SNIPER;
                jobname = "Sniper";
            } else if (selection == 25) {
                job = MapleJob.HERMIT;
                jobname = "Hermit";
            } else if (selection == 26) {
                job = MapleJob.CHIEFBANDIT;
                jobname = "Chief-Bandit";
            } else if (selection == 27) {
                job = MapleJob.OUTLAW;
                jobname = "Outlaw";
            } else if (selection == 28) {
                job = MapleJob.MARAUDER;
                jobname = "Marauder";
            } else if (selection == 29) {
                job = MapleJob.HERO;
                jobname = "Hero";
                cm.teachSkill(1120003, 0, 30);
                cm.teachSkill(1121006, 0, 30);
                cm.teachSkill(1121008, 0, 30);
            } else if (selection == 30) {
                job = MapleJob.PALADIN;
                jobname = "Paladin";
                cm.teachSkill(1221007, 0, 30);
                cm.teachSkill(1221009, 0, 30);
            } else if (selection == 31) {
                job = MapleJob.DARKKNIGHT;
                jobname = "Dark-Knight";
                cm.teachSkill(1321003, 0, 30);
                cm.teachSkill(1320006, 0, 30);
            } else if (selection == 32) {
                job = MapleJob.IL_ARCHMAGE;
                jobname = "Ice-Lightning Arch Mage";
                cm.teachSkill(2221003, 0, 30);
                cm.teachSkill(2221005, 0, 30);
                cm.teachSkill(2221006, 0, 30);
                cm.teachSkill(2221007, 0, 30);
            } else if (selection == 33) {
                job = MapleJob.FP_ARCHMAGE;
                jobname = "Fire-Poison Arch Mage";
                cm.teachSkill(2121003, 0, 30);
                cm.teachSkill(2121005, 0, 30);
                cm.teachSkill(2121006, 0, 30);
                cm.teachSkill(2121007, 0, 30);
            } else if (selection == 34) {
                job = MapleJob.BISHOP;
                jobname = "Bishop";
                cm.teachSkill(2321003, 0, 30);
                cm.teachSkill(2321005, 0, 30);
                cm.teachSkill(2321006, 0, 10);
                cm.teachSkill(2321007, 0, 30);
                cm.teachSkill(2321008, 0, 30);
            } else if (selection == 35) {
                job = MapleJob.BOWMASTER;
                jobname = "BowMaster";
                cm.teachSkill(3121003, 0, 30);
                cm.teachSkill(3121006, 0, 30);
                cm.teachSkill(3121008, 0, 30);
            } else if (selection == 36) {
                job = MapleJob.CROSSBOWMASTER;
                jobname = "Cross-Bow Master";
                cm.teachSkill(3221002, 0, 30);
                cm.teachSkill(3221003, 0, 30);
                cm.teachSkill(3220004, 0, 30);
                cm.teachSkill(3221005, 0, 30);
            } else if (selection == 37) {
                job = MapleJob.NIGHTLORD;
                jobname = "NightLord";
                cm.teachSkill(4121003, 0, 30);
                cm.teachSkill(4121007, 0, 30);
                cm.teachSkill(4121008, 0, 30);
            } else if (selection == 38) {
                job = MapleJob.SHADOWER;
                jobname = "Shadower";
                cm.teachSkill(4221001, 0, 30);
                cm.teachSkill(4221003, 0, 30);
                cm.teachSkill(4221006, 0, 30);
                cm.teachSkill(4221007, 0, 30);
            } else if (selection == 39) {
                job = MapleJob.CORSAIR;
                jobname = "Corsair";
            } else if (selection == 40) {
                job = MapleJob.BUCCANEER;
                jobname = "Buccaneer";
            } else if (selection == 41) {
                job = MapleJob.GM;
                jobname = "GM";
            } else if (selection == 42) {
                job = MapleJob.SUPERGM;
                jobname = "SuperGM";
            } else if (selection == 43) {
                job = MapleJob.BEGINNER;
                jobname = "Beginner";
            }
            if (selection == 0 && cm.getChar().getStr() < 35) {
                cm.sendOk("You don't have enough #rStr#k");
            } else if (selection == 1 &&
                cm.getLevel() < 8 && cm.getChar().getInt() < 20) {
                cm.sendOk("You don't have enough #rInt#k");
            } else if ((selection == 2 || selection == 3) &&
                cm.getChar().getDex() < 25) {
                cm.sendOk("You don't have enough #rDex#k");
            } else if (selection == 4 && cm.getChar().getDex() < 20) {
                cm.sendOk("You don't have enough #rDex#k");
            } else {
                cm.sendOk("Congratulations, you are now a #r" + jobname + "#k");
                cm.changeJob(job);
                cm.dispose();
            }
        } else if (status == 2) {
            cm.dispose();
        }
    }
}



/*
Selections:
0	Warrior
1	Magician
2	Bowman
3	Thief
4   Pirate
~~~~~~~~~~~~~~~~~~
5	Fighter
6	Page
7	Spearman

8	IL Wizard
9	FP Wizard
10	Cleric

11	Hunter
12	Crossbow-Man

13	Assassin
14	Bandit

15  Gunslinger
16  Brawler
~~~~~~~~~~~~~~~~~~
17	Crusader
18	White Knight
19	Dragon Knight

20	IL Mage
21	FP Mage
22	Priest

23	Ranger
24	Sniper

25	Hermit
26	Chief-Bandit

27  Outlaw
28  Marauder
~~~~~~~~~~~~~~~~~
29  Hero
30  Paladin
31  Dark Knight

32  Ice-Lightning Arch Mage
33  Fire-Poison Arch Mage
34  Bishop

35  BowMaster
36  CrossbowMaster

37  Night-Lord
38  Shadower

39  Corsair
40  Buccaneer
~~~~~~~~~~~~~~~~~
41	GM
42	Super-GM
43	Beginner
~~~~~~~~~~~~~~~~~
*/


