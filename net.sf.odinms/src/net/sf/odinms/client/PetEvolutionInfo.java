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


package net.sf.odinms.client;

import java.util.List;

import net.sf.odinms.tools.Pair;

/**
 *
 * @author Raz
 */
public class PetEvolutionInfo {

    private List<Pair<Integer, Integer>> evolves;
    private int evolveNo;
    private int reqPetLevel;
    private int reqItemId;
    //chatBalloon
    //nameTag
    //hungry
    //life
    //autoReact
    
    public PetEvolutionInfo() {
	
    }

    public List<Pair<Integer, Integer>> getEvolves() {
	return evolves;
    }

    public void setEvolves(List<Pair<Integer, Integer>> evolves) {
	this.evolves = evolves;
    }
    
    public void addEvolve(Pair<Integer, Integer> evolve) {
	this.evolves.add(evolve);
    }

    public int getReqItemId() {
	return reqItemId;
    }

    public void setReqItemId(int reqItemId) {
	this.reqItemId = reqItemId;
    }

    public int getReqPetLevel() {
	return reqPetLevel;
    }

    public void setReqPetLevel(int reqPetLevel) {
	this.reqPetLevel = reqPetLevel;
    }

    public int getEvolveNo() {
	return evolveNo;
    }

    public void setEvolveNo(int evolveNo) {
	this.evolveNo = evolveNo;
    }   
}
