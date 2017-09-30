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


import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.anticheat.CheatingOffense;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.server.movement.AbsoluteLifeMovement;
import net.sf.odinms.server.movement.LifeMovementFragment;
import net.sf.odinms.server.movement.MovementPath;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;


public class MovePlayerHandler extends AbstractMovementPacketHandler {

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        byte curFieldKey = slea.readByte();
        slea.readInt();
        MovementPath movementPath = parseMovement(slea);
        // TODO more validation of input data
        if (movementPath != null) {
            MapleCharacter player = c.getPlayer();
            if (!player.isHidden()) {
                MaplePacket packet = MaplePacketCreator.movePlayer(player.getId(), movementPath);
                c.getPlayer().getMap().broadcastMessage(player, packet, false);
            }

            if (CheatingOffense.FAST_MOVE.isEnabled() || CheatingOffense.HIGH_JUMP.isEnabled()) {
                checkMovementSpeed(c.getPlayer(), movementPath);
            }
            updatePosition(movementPath, c.getPlayer(), 0);
            c.getPlayer().getMap().movePlayer(c.getPlayer(), c.getPlayer().getPosition());

        }
    }

    private static void checkMovementSpeed(MapleCharacter chr, MovementPath movementPath) {
        // boolean wasALM = true;
        // Point oldPosition = new Point (c.getPlayer().getPosition());
        double playerSpeedMod = chr.getSpeedMod() + 0.005;
        // double playerJumpMod = c.getPlayer().getJumpMod() + 0.005;
        boolean encounteredFh = false;
        for (LifeMovementFragment lmf : movementPath.getRes()) {
            if (lmf.getClass() == AbsoluteLifeMovement.class) {
                final AbsoluteLifeMovement alm = (AbsoluteLifeMovement) lmf;
                double speedMod = Math.abs(alm.getPixelsPerSecond().x) / 125.0;
                if (speedMod > playerSpeedMod) {
                    if (alm.getFh() == 0) { // to prevent FJ fucking us
                        encounteredFh = true;
                    }
                    if (!encounteredFh) {
                        if (speedMod > playerSpeedMod) {
                            chr.getCheatTracker().registerOffense(CheatingOffense.FAST_MOVE);
                        }
                    }
                }
            }
        }
    }
}
