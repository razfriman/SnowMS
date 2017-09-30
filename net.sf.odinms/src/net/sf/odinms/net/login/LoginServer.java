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

package net.sf.odinms.net.login;

import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.rmi.ssl.SslRMIClientSocketFactory;

import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.net.MapleServerHandler;
import net.sf.odinms.net.PacketProcessor;
import net.sf.odinms.net.login.remote.LoginWorldInterface;
import net.sf.odinms.net.mina.MapleCodecFactory;
import net.sf.odinms.net.world.MapleWorld;
import net.sf.odinms.net.world.MapleWorld.WorldStatusType;
import net.sf.odinms.net.world.remote.WorldLoginInterface;
import net.sf.odinms.net.world.remote.WorldRegistry;
import net.sf.odinms.server.TimerManager;
import net.sf.odinms.tools.PropertyTool;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoAcceptor;
import org.apache.mina.common.SimpleByteBufferAllocator;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.meyling.console.Console;
import com.meyling.console.ConsoleFactory;
import com.meyling.console.ConsoleForegroundColor;
import net.sf.odinms.net.MapleServer;

public class LoginServer implements Runnable, LoginServerMBean {
	public static final int PORT = 8484;
	private IoAcceptor acceptor;
	private Console console = ConsoleFactory.getConsole();
	static final Logger log = LoggerFactory.getLogger(LoginServer.class);
	private static WorldRegistry worldRegistry = null;
	private Map<Integer, String> channelServer = new HashMap<Integer, String>();
	private LoginWorldInterface lwi;
	private WorldLoginInterface wli;
	private Properties prop = new Properties();
	private Properties initialProp = new Properties();
	private Boolean worldReady = Boolean.TRUE;
	private Properties subnetInfo = new Properties();
	private Map<Integer, Integer> load = new HashMap<Integer, Integer>();
	private int pinEnabled;
	private String serverName;
	private String eventMessage;
	private int testReg;
	private int wrongPasswords;
	private int userLog;
	private int testServer;
	int userLimit;
	int loginInterval;
	private long rankingInterval;
	private PropertyTool propTool = new PropertyTool(new Properties());
	private List<MapleWorld> worlds = new ArrayList<MapleWorld>();

	private static LoginServer instance = new LoginServer();
	
	static {
		MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
		try {
			mBeanServer.registerMBean(instance, new ObjectName("net.sf.odinms.net.login:type=LoginServer,name=LoginServer"));
		} catch (Exception e) {
			log.error("MBEAN ERROR", e);
			// not that bad...
		}
	}

	private LoginServer() {
	}

	public static LoginServer getInstance() {
		return instance;
	}
	
	public Set<Integer> getChannels() {
		return channelServer.keySet();
	}
	
	public void addChannel(int channel, String ip) {
		channelServer.put(channel, ip);
		load.put(channel, 0);
	}
	
	public void removeChannel(int channel) {
		channelServer.remove(channel);
		load.remove(channel);
	}
	
	public String getIP(int channel) {
		return channelServer.get(channel);
	}

	public int getPossibleLogins() {
		int ret = 0;
		try {
			Connection con = DatabaseConnection.getConnection();
			PreparedStatement limitCheck = con
				.prepareStatement("SELECT COUNT(*) FROM accounts WHERE loggedin > 1 AND gm=0");
			ResultSet rs = limitCheck.executeQuery();
			if (rs.next()) {
				int usersOn = rs.getInt(1);
				// log.info("userson: " + usersOn + ", limit: " + userLimit);
				if (usersOn < userLimit) {
					ret = userLimit - usersOn;
				}
			}
			rs.close();
			limitCheck.close();
		} catch (Exception ex) {
			log.error("loginlimit error", ex);
		}
		return ret;
	}
	
	public boolean showUserLog() {
	    return userLog > 0;
	}
	
	public void reconnectWorld() {
		// check if the connection is really gone
		try {
			wli.isAvailable();
		} catch (RemoteException ex) {
			synchronized (worldReady) {
				worldReady = Boolean.FALSE;
			}
			synchronized (lwi) {
				synchronized (worldReady) {
					if (worldReady) return;
				}
				log.warn("Reconnecting to world server");
				synchronized (wli) {
					// completely re-establish the rmi connection
					try {
						FileReader fileReader = new FileReader(System.getProperty("net.sf.odinms.login.config"));
						initialProp.load(fileReader);
						fileReader.close();
						Registry registry = LocateRegistry.getRegistry(initialProp.getProperty("net.sf.odinms.world.host"), 
							Registry.REGISTRY_PORT, new SslRMIClientSocketFactory());
						worldRegistry = (WorldRegistry) registry.lookup("WorldRegistry");
						lwi = new LoginWorldInterfaceImpl();
						wli = worldRegistry.registerLoginServer(initialProp.getProperty("net.sf.odinms.login.key"), lwi);
						Properties dbProp = new Properties();
						fileReader = new FileReader("db.properties");
						dbProp.load(fileReader);
						fileReader.close();
						DatabaseConnection.setProps(dbProp);
						DatabaseConnection.getConnection();
						prop = wli.getWorldProperties();
						propTool = new PropertyTool(prop);
						serverName = propTool.getSettingStr("net.sf.odinms.server.name", "OdinMS");
						eventMessage = propTool.getSettingStr("net.sf.odinms.login.eventmessage", "");
						pinEnabled = propTool.getSettingInt("net.sf.odinms.login.pinenabled", 0);
						userLimit = propTool.getSettingInt("net.sf.odinms.login.userlimit", 50);
						testReg = propTool.getSettingInt("net.sf.odinms.login.testReg", 0);
						wrongPasswords = propTool.getSettingInt("net.sf.odinms.login.wrongpasswords", -1);
						userLog = propTool.getSettingInt("net.sf.odinms.login.userlog", 0);
						testServer = propTool.getSettingInt("net.sf.odinms.login.testserver", 0);
						try {
							fileReader = new FileReader("subnet.properties");
							subnetInfo.load(fileReader);
							fileReader.close();
						} catch(Exception e) {
							log.info("Could not load subnet configuration, falling back to world defaults", e);
						}
					} catch (Exception e) {
						log.error("Reconnecting failed", e);
					}
					worldReady = Boolean.TRUE;
				}
			}
			synchronized (worldReady) {
				worldReady.notifyAll();
			}
		}

	}

	@Override
	public void run() {
		try {
			FileReader fileReader = new FileReader(System.getProperty("net.sf.odinms.login.config"));
			initialProp.load(fileReader);
			fileReader.close();
			Registry registry = LocateRegistry.getRegistry(initialProp.getProperty("net.sf.odinms.world.host"), 
				Registry.REGISTRY_PORT, new SslRMIClientSocketFactory());
			worldRegistry = (WorldRegistry) registry.lookup("WorldRegistry");
			lwi = new LoginWorldInterfaceImpl();
			wli = worldRegistry.registerLoginServer(initialProp.getProperty("net.sf.odinms.login.key"), lwi);
			Properties dbProp = new Properties();
			fileReader = new FileReader("db.properties");
			dbProp.load(fileReader);
			fileReader.close();
			DatabaseConnection.setProps(dbProp);
			DatabaseConnection.getConnection(); 
			prop = wli.getWorldProperties();
			propTool = new PropertyTool(prop);
			serverName = propTool.getSettingStr("net.sf.odinms.server.name", "OdinMS");
			eventMessage = propTool.getSettingStr("net.sf.odinms.login.eventmessage", "");
			pinEnabled = propTool.getSettingInt("net.sf.odinms.login.pinenabled", 0);
			userLimit = propTool.getSettingInt("net.sf.odinms.login.userlimit", 50);
			testReg = propTool.getSettingInt("net.sf.odinms.login.testReg", 0);
			wrongPasswords = propTool.getSettingInt("net.sf.odinms.login.wrongpasswords", -1);
			userLog = propTool.getSettingInt("net.sf.odinms.login.userlog", 0);
			testServer = propTool.getSettingInt("net.sf.odinms.login.testserver", 0);
			loadWorlds();
			try {
				fileReader = new FileReader("subnet.properties");
				subnetInfo.load(fileReader);
				fileReader.close();
			} catch(Exception e) {
				log.trace("Could not load subnet configuration, falling back to world defaults", e);
			}
		} catch (Exception e) {
			throw new RuntimeException("Could not connect to world server.", e);
		}
		
		ByteBuffer.setUseDirectBuffers(false);
		ByteBuffer.setAllocator(new SimpleByteBufferAllocator());

		acceptor = new SocketAcceptor();

		SocketAcceptorConfig cfg = new SocketAcceptorConfig();
		// cfg.setThreadModel(ThreadModel.MANUAL);
		// cfg.getFilterChain().addLast("logger", new LoggingFilter());
		// Loginserver is still on the default threadmodel so no executor filter here...
		// ExecutorService executor = new ThreadPoolExecutor(4, 8, 60, TimeUnit.SECONDS,
		// new LinkedBlockingQueue<Runnable>());
		// cfg.getFilterChain().addLast("executor", new ExecutorFilter(executor));
		cfg.getFilterChain().addLast("codec", new ProtocolCodecFilter(new MapleCodecFactory()));
		
		TimerManager tMan = TimerManager.getInstance();
		tMan.start();
		loginInterval = Integer.parseInt(prop.getProperty("net.sf.odinms.login.interval"));
		tMan.register(LoginWorker.getInstance(), loginInterval);
		rankingInterval = Long.parseLong(prop.getProperty("net.sf.odinms.login.ranking.interval"));
		tMan.register(new RankingWorker(), rankingInterval);

		try {
			acceptor.bind(new InetSocketAddress(PORT), new MapleServerHandler(PacketProcessor
				.getProcessor(PacketProcessor.Mode.LOGINSERVER)), cfg);
			console.setForegroundColor(ConsoleForegroundColor.LIGHT_GREEN);
			System.out.println("Listening on port " + PORT);
			console.resetColors();
			
		} catch (IOException e) {
			console.setForegroundColor(ConsoleForegroundColor.LIGHT_RED);
			log.error("Binding to port {} failed", PORT, e);
			console.resetColors();
		}
	}
	
	public void shutdown() {
		log.info("Shutting down...");
		try {
			worldRegistry.deregisterLoginServer(lwi);
		} catch (RemoteException e) {
			// doesn't matter we're shutting down anyway
		}
		TimerManager.getInstance().stop();
		System.exit(0);
	}
	
	public void loadWorlds() {
	    try {
		Connection con = DatabaseConnection.getConnection();
		PreparedStatement ps = con.prepareStatement("SELECT * FROM worlds");
		ResultSet rs = ps.executeQuery();
		
		while(rs.next()) {
		    MapleWorld world = new MapleWorld();
		    world.setId(rs.getInt("id"));
		    world.setName(rs.getString("name"));
		    world.setMaxCharacters(rs.getInt("maxchars"));
		    world.setWorldStatusType(WorldStatusType.getByName(rs.getString("statustype")));
			world.setRecommendedMessage(rs.getString("recommendedMessage"));
		    worlds.add(world);
		}
	    } catch(SQLException sqle) {
		log.error("error loading worlds", sqle);
	    }
	}
	
	public List<MapleWorld> getWorlds() {
	    return Collections.unmodifiableList(worlds);
	}

	public List<MapleWorld> getRecommendedWorlds() {
	    List<MapleWorld> worlds = new ArrayList<MapleWorld>();
		for(MapleWorld world : getWorlds()) {
			if (!world.getRecommendedMessage().equals("")) {
				worlds.add(world);
			}
		}
		return worlds;
	}
	
	public MapleWorld getWorldById(int id) {
		for (MapleWorld world : worlds) {
			if (world.getId() == id) {
				return world;
			}
		}
		return null;
	}
	
	public WorldLoginInterface getWorldInterface() {
		synchronized (worldReady) {
			while (!worldReady) {
				try {
					worldReady.wait();
				} catch (InterruptedException e) {}
			}
		}
		return wli;
	}

	public static void main(String args[]) {
		System.out.println("Snow's MapleStory Login-Server V" + MapleServer.MAPLE_VERSION);
		try {
			LoginServer.getInstance().run();
		} catch (Exception e) {
			log.error("Error initializing loginserver", e);
		}
	}

	public int getLoginInterval() {
		return loginInterval;
	}

	public Properties getSubnetInfo() {
		return subnetInfo;
	}

	public int getUserLimit() {
		return userLimit;
	}
	
	public boolean allowTestReg() {
	    return testReg > 0;
	}
	
	public int wrongPasswordsAllowed() {
	    return wrongPasswords;
	}
	
	public boolean isTestServer() {
	    return testServer > 0;
	}
	
	public boolean allowPins() {
	    return pinEnabled > 0;
	}

	@Override
	public String getServerName() {
		return serverName;
	}

	@Override
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	
	public String getEventMessage() {
	    return eventMessage;
	}
	
	public void setEventMessage(String eventMessage) {
	    this.eventMessage = eventMessage;
	}

	public Map<Integer, Integer> getLoad() {
		return load;
	}

	public void setLoad(Map<Integer, Integer> load) {
		this.load = load;
	}

	@Override
	public int getNumberOfSessions() {
		return acceptor.getManagedSessions(new InetSocketAddress(PORT)).size();
	}

	@Override
	public void setUserLimit(int newLimit) {
		userLimit = newLimit;
	}
}
