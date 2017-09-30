/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.server.movement;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author raz
 */
public class MovementPath {

    private List<LifeMovementFragment> res = new ArrayList<LifeMovementFragment>();
    private Point startPos;
    private Rectangle movementRect;

    public MovementPath() {
        
    }

    public List<LifeMovementFragment> getRes() {
        return res;
    }

    public void setRes(List<LifeMovementFragment> res) {
        this.res = res;
    }

    public void addRes(LifeMovementFragment fragment) {
        res.add(fragment);
    }

    public Point getStartPos() {
        return startPos;
    }

    public void setStartPos(Point startPos) {
        this.startPos = startPos;
    }

    public Rectangle getMovementRect() {
        return movementRect;
    }

    public void setMovementRect(Rectangle movementRect) {
        this.movementRect = movementRect;
    }
}
