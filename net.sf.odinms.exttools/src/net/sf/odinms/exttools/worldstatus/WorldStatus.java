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

package net.sf.odinms.exttools.worldstatus;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.rmi.ssl.SslRMIClientSocketFactory;

import net.sf.odinms.net.world.remote.WorldRegistry;

/**
 *
 * @author Matze
 */
public class WorldStatus {

    public static void main(String[] args) {
	try {
	    System.out.println("<strong>Current game server status (updated every 5 minutes)</strong><br/>");
	    Registry registry = LocateRegistry.getRegistry(System.getProperty("net.sf.odinms.world.host"),
		    Registry.REGISTRY_PORT, new SslRMIClientSocketFactory());
	    WorldRegistry worldRegistry = (WorldRegistry) registry.lookup("WorldRegistry");
	    System.out.println(worldRegistry.getStatus());
	} catch (Exception e) {
	    System.out.println("OFFLINE");
	}
    }
}
