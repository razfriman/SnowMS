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

package net.sf.odinms.net.world;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.rmi.ssl.SslRMIServerSocketFactory;

import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.net.MapleServer;
import net.sf.odinms.tools.StringUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Matze
 */
public class WorldServer {

	private static WorldServer instance = null;
	private static Logger log = LoggerFactory.getLogger(WorldServer.class);
	private int worldId;
	private Properties dbProp = new Properties();
	private Properties worldProp = new Properties();
	
	/**
	 * Creates a new instance of WorldServer
	 */
	private WorldServer() {
		try {
			InputStreamReader is = new FileReader("db.properties");
			dbProp.load(is);
			is.close();
			DatabaseConnection.setProps(dbProp);
			updateSQL();
			is = new FileReader("world.properties");
			worldProp.load(is);
			is.close();
		} catch (Exception e) {
			log.error("Could not configuration", e);
		}
	}
	
	/**
	 * 
	 * @return The instance of WorldServer
	 */
	public synchronized static WorldServer getInstance() {
		if (instance == null) instance = new WorldServer();
		return instance;
	}
	
	/**
	 * Updates the Database
	 */
	public void updateSQL() {
	    try {
		List<String> queries = new ArrayList<String>();
		Connection con = DatabaseConnection.getConnection();
		PreparedStatement ps = con.prepareStatement("SELECT * FROM odin_info");
		ResultSet rs = ps.executeQuery();
		int version = 0;
		rs.next();
		version = rs.getInt("version");
		int updates = 0;
		while (true) {
		    File sqlFile = new File("sql/OdinMS_" + StringUtil.getLeftPaddedStr(Integer.toString(version + 1), '0', 3) + ".sql");
		    if (!sqlFile.exists()) {
			if (updates > 0) {
			    ps = con.prepareStatement("UPDATE odin_info SET version=" + version);
			    ps.executeUpdate();
			    ps.close();
			    System.out.println("New DB Version: " + version + " | Updates: " + updates);
			}
			break;
		    } else {
			BufferedReader br = new BufferedReader(new FileReader(sqlFile));
			String str;
			StringBuffer sb = new StringBuffer();

			String comment = br.readLine();
			while (true) {
			    str = br.readLine();
			    if (str == null) {
				break;
			    } else if (str.startsWith("--") || str.equals("")) {
				continue;
			    } else {
				if (str.startsWith("INSERT") || str.startsWith("UPDATE") || str.startsWith("DELETE")) {
				    if (sb.toString().length() > 0) {
					queries.add(sb.toString());
					sb = new StringBuffer();
				    }
				    sb.append(str + "\r\n");
				} else {
				    sb.append(str + "\r\n");
				}
			    }
			}
			queries.add(sb.toString());
			br.close();
			System.out.println("DB Update" + StringUtil.getLeftPaddedStr(Integer.toString(version + 1), '0', 3) + ": " + comment);
			for (String query : queries) {
			    ps = con.prepareStatement(query);
			    ps.executeUpdate();
			}
			updates++;
			version++;
		    }
		}
		System.out.println("Database: Updated");
	    } catch (Exception e) {
		log.error("Error updating database");
		e.printStackTrace();
	    }
	}

	public int getWorldId() {
		return worldId;
	}

	public Properties getDbProp() {
		return dbProp;
	}

	public Properties getWorldProp() {
		return worldProp;
	}
	
	public static void main(String[] args) {
		System.out.println("Snow's MapleStory World-Server V" + MapleServer.MAPLE_VERSION);
		try {
			Registry registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT, new SslRMIClientSocketFactory(), new SslRMIServerSocketFactory());
			registry.rebind("WorldRegistry", WorldRegistryImpl.getInstance());
		} catch (RemoteException ex) {
			log.error("Could not initialize RMI system", ex);
		}
	}
	
}
