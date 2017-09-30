/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc> 
                       Matthias Butz <matze@odinms.de>
                       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation. You may not use, modify
    or distribute this program under any other version of the
    GNU Affero General Public License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package net.sf.odinms.net.channel.handler;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sf.odinms.client.ISkill;
import net.sf.odinms.client.MapleBuffStat;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleJob;
import net.sf.odinms.client.SkillFactory;
import net.sf.odinms.client.anticheat.CheatingOffense;
import net.sf.odinms.client.status.MonsterStatus;
import net.sf.odinms.client.status.MonsterStatusEffect;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.AutobanManager;
import net.sf.odinms.server.MapleStatEffect;
import net.sf.odinms.server.TimerManager;
import net.sf.odinms.server.attack.AttackInfo;
import net.sf.odinms.server.attack.AttackInfo.AttackType;
import net.sf.odinms.server.life.Element;
import net.sf.odinms.server.life.ElementalEffectiveness;
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.server.maps.MapleMapItem;
import net.sf.odinms.server.maps.MapleMapObject;
import net.sf.odinms.server.maps.MapleMapObjectType;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.Pair;
import net.sf.odinms.tools.data.input.LittleEndianAccessor;

public abstract class AbstractDealDamageHandler extends AbstractMaplePacketHandler {

    protected void applyAttack(AttackInfo attack, MapleCharacter player, int maxDamagePerMonster, int attackCount) {
        player.getCheatTracker().resetHPRegen();
        player.getCheatTracker().checkAttack(attack.getSkill());

        ISkill theSkill = null;
        MapleStatEffect attackEffect = null;
        if (attack.getSkill() != 0) {
            theSkill = SkillFactory.getSkill(attack.getSkill());
            attackEffect = attack.getAttackEffect(player, theSkill);
            if (attackEffect == null) {
                AutobanManager.getInstance().autoban(player.getClient(),
                        "Using a skill he doesn't have (" + attack.getSkill() + ")");
            }
            if (attack.getSkill() != 2301002) {
                // heal is both an attack and a special move (healing)
                // so we'll let the whole applying magic live in the special move part
                if (player.isAlive()) {
                    attackEffect.applyTo(player);
                } else {
                    player.getClient().getSession().write(MaplePacketCreator.enableActions());
                }
            }
        }
        if (!player.isAlive()) {
            player.getCheatTracker().registerOffense(CheatingOffense.ATTACKING_WHILE_DEAD);
            return;
        }
        // meso explosion has a variable bullet count
        if (attackCount != attack.getNumDamage() && attack.getSkill() != 4211006) {
            player.getCheatTracker().registerOffense(CheatingOffense.MISMATCHING_BULLETCOUNT,
                    attack.getNumDamage() + "/" + attackCount);
        }
        int totDamage = 0;
        MapleMap map = player.getMap();

        if (attack.getSkill() == 4211006) { // meso explosion
            for (Pair<Integer, List<Integer>> oned : attack.getAllDamage()) {
                MapleMapObject mapobject = map.getMapObject(oned.getLeft().intValue());

                if (mapobject != null && mapobject.getType() == MapleMapObjectType.ITEM) {
                    MapleMapItem mapitem = (MapleMapItem) mapobject;
                    if (mapitem.getMeso() > 0) {
                        synchronized (mapitem) {
                            if (mapitem.isPickedUp()) {
                                return;
                            }
                            map.removeMapObject(mapitem);
                            map.broadcastMessage(MaplePacketCreator.removeItemFromMap(mapitem.getObjectId(), 4, 0), mapitem.getPosition());
                            mapitem.setPickedUp(true);
                        }
                    } else if (mapitem.getMeso() == 0) {
                        player.getCheatTracker().registerOffense(CheatingOffense.ETC_EXPLOSION);
                        return;
                    }
                } else if (mapobject != null && mapobject.getType() != MapleMapObjectType.MONSTER) {
                    player.getCheatTracker().registerOffense(CheatingOffense.EXPLODING_NONEXISTANT);
                    return; // etc explosion, exploding nonexistant things, etc.
                }
            }
        }

        for (Pair<Integer, List<Integer>> oned : attack.getAllDamage()) {
            MapleMonster monster = map.getMonsterByOid(oned.getLeft().intValue());

            if (!monster.getStats().isUndead() && attack.getSkill() == 2301002) {
                player.getCheatTracker().registerOffense(CheatingOffense.WZ_EDIT);
                return;
            }

            if (monster != null) {
                int totDamageToOneMonster = 0;
                for (Integer eachd : oned.getRight()) {
                    totDamageToOneMonster += eachd.intValue();
                }
                totDamage += totDamageToOneMonster;

                Point playerPos = player.getPosition();
                if (totDamageToOneMonster > attack.getNumDamage() + 1) {
                    int dmgCheck = player.getCheatTracker().checkDamage(totDamageToOneMonster);
                    if (dmgCheck > 5) {
                        player.getCheatTracker().registerOffense(CheatingOffense.SAME_DAMAGE, dmgCheck + " times: "
                                + totDamageToOneMonster);
                    }
                }
                checkHighDamage(player, monster, attack, theSkill, attackEffect, totDamageToOneMonster, maxDamagePerMonster);
                double distance = playerPos.distanceSq(monster.getPosition());
                if (distance > 360000.0) { // 600^2, 550 is approximatly the range of ultis
                    player.getCheatTracker().registerOffense(CheatingOffense.ATTACK_FARAWAY_MONSTER, Double.toString(Math.sqrt(distance)));
                    // if (distance > 1000000.0)
                    // AutobanManager.getInstance().addPoints(player.getClient(), 50, 120000, "Exceeding attack
                    // range");
                }
                if (!monster.isControllerHasAggro()) {
                    if (monster.getController() == player) {
                        monster.setControllerHasAggro(true);
                    } else {
                        monster.switchController(player, true);
                    }
                }
                // only ds, sb, assaulter, normal (does it work for thieves, bs, or assasinate?)
                if ((attack.getSkill() == 4001334 || attack.getSkill() == 4201005 || attack.getSkill() == 0 || attack.getSkill() == 4211002 || attack.getSkill() == 4211004)
                        && player.getBuffedValue(MapleBuffStat.PICKPOCKET) != null) {
                    handlePickPocket(player, monster, oned);
                }
                if (attack.getSkill() == 4101005) { // drain
                    ISkill drain = SkillFactory.getSkill(4101005);
                    int gainhp = (int) ((double) totDamageToOneMonster
                            * (double) drain.getEffect(player.getSkillLevel(drain)).getX() / 100.0);
                    gainhp = Math.min(monster.getMaxHp(), Math.min(gainhp, player.getMaxHp() / 2));
                    player.addHP(gainhp);
                }

                if (player.getJob().isA(MapleJob.WHITEKNIGHT)) {
                    int[] charges = new int[]{1211005, 1211006};
                    for (int charge : charges) {
                        ISkill chargeSkill = SkillFactory.getSkill(charge);

                        if (player.isBuffFrom(MapleBuffStat.WK_CHARGE, chargeSkill)) {
                            final ElementalEffectiveness iceEffectiveness = monster.getEffectiveness(Element.ICE);
                            if (totDamageToOneMonster > 0 && iceEffectiveness == ElementalEffectiveness.NORMAL || iceEffectiveness == ElementalEffectiveness.WEAK) {
                                MapleStatEffect chargeEffect = chargeSkill.getEffect(player.getSkillLevel(chargeSkill));
                                MonsterStatusEffect monsterStatusEffect = new MonsterStatusEffect(Collections.singletonMap(MonsterStatus.FREEZE, 1), chargeSkill, false);
                                monster.applyStatus(player, monsterStatusEffect, false, chargeEffect.getY() * 2000);
                            }
                            break;
                        }
                    }
                }

                if (totDamageToOneMonster > 0 && attackEffect != null && attackEffect.getMonsterStati().size() > 0) {
                    if (attackEffect.makeChanceResult()) {
                        MonsterStatusEffect monsterStatusEffect = new MonsterStatusEffect(attackEffect.getMonsterStati(), theSkill, false);
                        monster.applyStatus(player, monsterStatusEffect, attackEffect.isPoison(), attackEffect.getDuration());
                    }
                }
                if (monster.getPvpOwner() == null) {
                    map.damageMonster(player, monster, totDamageToOneMonster);
                }
            }
        }
        if (totDamage > 1) {
            player.getCheatTracker().setAttacksWithoutHit(player.getCheatTracker().getAttacksWithoutHit() + 1);
            final int offenseLimit;
            if (attack.getSkill() != 3121004) {
                offenseLimit = 100;
            } else {
                offenseLimit = 300;
            }
            if (player.getCheatTracker().getAttacksWithoutHit() > offenseLimit) {
                player.getCheatTracker().registerOffense(CheatingOffense.ATTACK_WITHOUT_GETTING_HIT,
                        Integer.toString(player.getCheatTracker().getAttacksWithoutHit()));
            }
        }
    }

    private void handlePickPocket(MapleCharacter player, MapleMonster monster, Pair<Integer, List<Integer>> oned) {
        ISkill pickpocket = SkillFactory.getSkill(4211003);
        int delay = 0;
        int maxmeso = player.getBuffedValue(MapleBuffStat.PICKPOCKET).intValue();
        int reqdamage = 20000;
        Point monsterPosition = monster.getPosition();

        for (Integer eachd : oned.getRight()) {
            if (pickpocket.getEffect(player.getSkillLevel(pickpocket)).makeChanceResult()) {
                double perc = (double) eachd / (double) reqdamage;

                final int todrop = Math.min((int) Math.max(perc * (double) maxmeso, (double) 1),
                        maxmeso);
                final MapleMap tdmap = player.getMap();
                final Point tdpos = new Point((int) (monsterPosition.getX() + (Math.random() * 100) - 50),
                        (int) (monsterPosition.getY()));
                final MapleMonster tdmob = monster;
                final MapleCharacter tdchar = player;

                TimerManager.getInstance().schedule(new Runnable() {

                    public void run() {
                        tdmap.spawnMesoDrop(todrop, todrop, tdpos, tdmob, tdchar, false);
                    }
                }, delay);

                delay += 200;
            }
        }
    }

    private void checkHighDamage(MapleCharacter player, MapleMonster monster, AttackInfo attack, ISkill theSkill,
            MapleStatEffect attackEffect, int damageToMonster, int maximumDamageToMonster) {
        int elementalMaxDamagePerMonster;
        Element element = Element.NEUTRAL;
        if (theSkill != null) {
            element = theSkill.getElement();
        }
        if (player.getBuffedValue(MapleBuffStat.WK_CHARGE) != null) {
            int chargeSkillId = player.getBuffSource(MapleBuffStat.WK_CHARGE);
            switch (chargeSkillId) {
                case 1211003:
                case 1211004:
                    element = Element.FIRE;
                    break;
                case 1211005:
                case 1211006:
                    element = Element.ICE;
                    break;
                case 1211007:
                case 1211008:
                    element = Element.LIGHTING;
                    break;
                case 1221003:
                case 1221004:
                    element = Element.HOLY;
                    break;
            }
            ISkill chargeSkill = SkillFactory.getSkill(chargeSkillId);
            maximumDamageToMonster *= chargeSkill.getEffect(player.getSkillLevel(chargeSkill)).getDamage() / 100.0;
        }
        if (element != Element.NEUTRAL) {
            double elementalEffect;
            if (attack.getSkill() == 3211003 || attack.getSkill() == 3111003) { // inferno and blizzard
                elementalEffect = attackEffect.getX() / 200.0;
            } else {
                elementalEffect = 0.5;
            }
            switch (monster.getEffectiveness(element)) {
                case IMMUNE:
                    elementalMaxDamagePerMonster = 1;
                    break;
                case NORMAL:
                    elementalMaxDamagePerMonster = maximumDamageToMonster;
                    break;
                case WEAK:
                    elementalMaxDamagePerMonster = (int) (maximumDamageToMonster * (1.0 + elementalEffect));
                    break;
                case STRONG:
                    elementalMaxDamagePerMonster = (int) (maximumDamageToMonster * (1.0 - elementalEffect));
                    break;
                default:
                    throw new RuntimeException("Unknown enum constant");
            }
        } else {
            elementalMaxDamagePerMonster = maximumDamageToMonster;
        }
        if (damageToMonster > elementalMaxDamagePerMonster) {
            player.getCheatTracker().registerOffense(CheatingOffense.HIGH_DAMAGE);
            // log.info("[h4x] Player {} is doing high damage to one monster: {} (maxdamage: {}, skill:
            // {})",
            // new Object[] { player.getName(), Integer.valueOf(totDamageToOneMonster),
            // Integer.valueOf(maxDamagePerMonster), Integer.valueOf(attack.getSkill()) });
            if (damageToMonster > elementalMaxDamagePerMonster * 3) { // * 3 until implementation of lagsafe pingchecks for buff expiration
                AutobanManager.getInstance().autoban(player.getClient(), damageToMonster
                        + " damage (level: " + player.getLevel() + " watk: " + player.getTotalWatk()
                        + " skill: " + attack.getSkill() + ", monster: " + monster.getId() + " assumed max damage: "
                        + elementalMaxDamagePerMonster + ")");
            }
        }
    }

    public AttackInfo parseDamage(MapleClient c, LittleEndianAccessor lea, AttackType attackType) {
        AttackInfo ret = new AttackInfo();
        ret.setPlayer(c.getPlayer());
        ret.setAttackType(attackType);
        lea.readByte();//TICK
        ret.setNumAttackedAndDamage(lea.readByte());
        ret.setNumAttacked((ret.getNumAttackedAndDamage() >>> 4) & 0xF); // guess why there are no skills damaging more than 15 monsters...
        ret.setNumDamage(ret.getNumAttackedAndDamage() & 0xF); // how often each single monster was attacked o.o
        ret.setAllDamage(new ArrayList<Pair<Integer, List<Integer>>>());
        ret.setSkill(lea.readInt());
        ISkill skill = SkillFactory.getSkill(ret.getSkill());
        int imgFileSize = lea.readInt();
        ret.setCharge(skill != null && skill.hasCharge() ? lea.readInt() : -1);
        lea.readInt();//ticks??
        ret.setProjectileDisplay(lea.readByte());
        lea.readByte(); //new???
        ret.setStance(lea.readByte());//Direction/Animation
        lea.skip(1);//Weapon Subclass
        ret.setWSpeed(lea.readByte());//Weapon Speed
        if (ret.getSkill() == 4211006) {
            return parseMesoExplosion(lea, ret);
        }

        switch (ret.getAttackType()) {
            case RANGED:
                lea.readByte();
                ret.setDirection(lea.readByte()); // contains direction on some 4th job skills
                lea.skip(7);
                // hurricane and pierce and rapidfire have extra 4 bytes :/
                if (ret.getSkill() == 3121004 || ret.getSkill() == 3221001 || ret.getSkill() == 5221004) {
                    lea.skip(4);
                }
                break;
            default:
                lea.skip(4); //tick count
                break;
        }


        // TODO we need information if an attack was a critical hit, this requires syncing a random generator between the client and server

        for (int i = 0; i < ret.getNumAttacked(); i++) {
            int oid = lea.readInt();
            lea.skip(14);//POSITION INFO

            List<Integer> allDamageNumbers = new ArrayList<Integer>();
            for (int j = 0; j < ret.getNumDamage(); j++) {
                int damage = lea.readInt();
                //if(ret.setskill == 3221007) damage += 0x80000000;//Cricitcal Damage = 0x80000000 + damage || Snipe always crit
                allDamageNumbers.add(Integer.valueOf(damage));
            }
            lea.readInt();
            ret.addAllDamage(new Pair<Integer, List<Integer>>(Integer.valueOf(oid), allDamageNumbers));
        }

        return ret;
    }

    public AttackInfo parseMesoExplosion(LittleEndianAccessor lea, AttackInfo ret) {

        if (ret.getNumAttackedAndDamage() == 0) {
            lea.skip(8);

            int bullets = lea.readByte();
            for (int j = 0; j < bullets; j++) {
                int mesoid = lea.readInt();
                lea.skip(1);
                ret.addAllDamage(new Pair<Integer, List<Integer>>(Integer.valueOf(mesoid), null));
            }
            return ret;

        } else {
            lea.skip(4);
        }

        for (int i = 0; i < ret.getNumAttacked() + 1; i++) {

            int oid = lea.readInt();

            if (i < ret.getNumAttacked()) {
                lea.skip(12);
                int bullets = lea.readByte();

                List<Integer> allDamageNumbers = new ArrayList<Integer>();
                for (int j = 0; j < bullets; j++) {
                    int damage = lea.readInt();
                    allDamageNumbers.add(Integer.valueOf(damage));
                }
                ret.addAllDamage(new Pair<Integer, List<Integer>>(Integer.valueOf(oid), allDamageNumbers));

            } else {

                int bullets = lea.readByte();
                for (int j = 0; j < bullets; j++) {
                    int mesoid = lea.readInt();
                    lea.skip(1);
                    ret.addAllDamage(new Pair<Integer, List<Integer>>(Integer.valueOf(mesoid), null));
                }
            }
        }

        return ret;
    }
}
