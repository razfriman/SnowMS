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
import java.awt.Rectangle;

import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.maps.AnimatedMapleMapObject;
import net.sf.odinms.server.movement.AbsoluteLifeMovement;
import net.sf.odinms.server.movement.ChangeEquipSpecialAwesome;
import net.sf.odinms.server.movement.JumpDownMovement;
import net.sf.odinms.server.movement.LifeMovement;
import net.sf.odinms.server.movement.LifeMovementFragment;
import net.sf.odinms.server.movement.MovementPath;
import net.sf.odinms.server.movement.RelativeLifeMovement;
import net.sf.odinms.server.movement.TeleportMovement;
import net.sf.odinms.tools.data.input.LittleEndianAccessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMovementPacketHandler extends AbstractMaplePacketHandler {

    private static Logger log = LoggerFactory.getLogger(AbstractMovementPacketHandler.class);

    protected MovementPath parseMovement(LittleEndianAccessor lea) {
        MovementPath movementPath = new MovementPath();

        Point startPos = lea.readPoint();

        movementPath.setStartPos(startPos);

        int numCommands = lea.readByte();
        for (int i = 0; i < numCommands; i++) {
            int command = lea.readByte();
            switch (command) {
                case 0x00: //NORMAL MOVE
                case 0x05: //PET MOVE?
                case 0x11: //FLOAT - 2 (Falling)
                {
                    int xpos = lea.readShort(); // X
                    int ypos = lea.readShort(); // Y
                    int xwobble = lea.readShort();// VX
                    int ywobble = lea.readShort(); // VY
                    int fh = lea.readShort(); // FH
                    int newstate = lea.readByte();
                    int duration = lea.readShort();
                    AbsoluteLifeMovement alm = new AbsoluteLifeMovement(command, new Point(xpos, ypos), fh, newstate, duration);
                    alm.setPixelsPerSecond(new Point(xwobble, ywobble));
                    movementPath.addRes(alm);
                    break;
                }
                case 0x01: //JUMPING
                case 0x02: //JUMPING/KNOCKBACK
                case 0x06: //FLASH JUMP
                case 0x0C: //HORNTAIL KNOCKBACK
                case 0x0D: //STEP BACK
                case 0x10: //FLOAT - 1 (Jumping)
                case 0x14: //ARAN?
                {
                    int xmod = lea.readShort();
                    int ymod = lea.readShort();
                    int newstate = lea.readByte();
                    int duration = lea.readShort();
                    RelativeLifeMovement rlm = new RelativeLifeMovement(command, new Point(xmod, ymod), duration, newstate);
                    movementPath.addRes(rlm);
                    break;
                }
                case 0x03:
                case 0x04: //TELEPORT
                case 0x07: //ASSUALTER
                case 0x08: //ASSASINATE
                case 0x09: //RUSH
                case 0x0B: //CHAIR
                case 0x0E: //JUMP DOWN - 1
                {
                    int xpos = startPos.x;
                    int ypos = startPos.y;
                    int xwobble = lea.readShort();
                    int ywobble = lea.readShort();
                    int fh = lea.readShort();
                    int newstate = lea.readByte();
                    int duration = lea.readShort();
                    TeleportMovement tm = new TeleportMovement(command, new Point(xpos, ypos), fh, newstate, duration);
                    tm.setPixelsPerSecond(new Point(xwobble, ywobble));
                    movementPath.addRes(tm);
                    break;
                }
                case 0x0A: //CHANGE EQUIP
                {
                    int stat = lea.readByte();
                    ChangeEquipSpecialAwesome cesa = new ChangeEquipSpecialAwesome(stat);
                    movementPath.addRes(cesa);
                    break;
                }
                case 0x0F: //JUMP DOWN - 2
                {
                    int xpos = lea.readShort();
                    int ypos = lea.readShort();
                    int xwobble = lea.readShort();
                    int ywobble = lea.readShort();
                    int fh1 = lea.readShort();
                    int fh2 = lea.readShort();
                    int newstate = lea.readByte();
                    int duration = lea.readShort();
                    JumpDownMovement jdm = new JumpDownMovement(command, new Point(xpos, ypos), fh1, duration, newstate);
                    jdm.setPixelsPerSecond(new Point(xwobble, ywobble));
                    jdm.setFh2(fh2);
                    movementPath.addRes(jdm);
                    break;
                }
                default: {
                    System.out.println("Unknown Movement Command-" + command + " Size-" + numCommands + " Length-" + lea.available());
                    return null;
                }
            }
        }

        byte unkByte = lea.readByte();
        byte unkByte2 = 0x00;
        int unkInt = 0;
        for (int i = unkByte; unkInt < i; unkInt++) {
            if (unkInt % 2 > 0) {
                unkByte2 >>= 4;
            } else {
                unkByte2 = lea.readByte();
            }
            char c = (char) (unkByte2 & 0x0F);
            //Insert into KeyPadState
        }

        int rcLeft = lea.readShort();
        int rcTop = lea.readShort();
        int rcRight = lea.readShort();
        int rcBottom = lea.readShort();

        movementPath.setMovementRect(new Rectangle(rcLeft, rcTop, rcLeft - rcRight, rcTop - rcBottom));
        
        if (numCommands != movementPath.getRes().size()) {
            log.warn("numCommands ({}) does not match the number of deserialized movement commands ({})", numCommands, movementPath.getRes().size());
        }
        return movementPath;
    }

    protected void updatePosition(MovementPath movementPath, AnimatedMapleMapObject target, int yoffset) {
        for (LifeMovementFragment move : movementPath.getRes()) {
            if (move instanceof LifeMovement) {
                if (move instanceof AbsoluteLifeMovement) {
                    Point position = ((LifeMovement) move).getPosition();
                    position.y += yoffset;
                    target.setPosition(position);
                }
                target.setStance(((LifeMovement) move).getNewstate());
            }
        }
    }
}
