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

package net.sf.odinms.net.channel;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.rmi.ssl.SslRMIClientSocketFactory;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.messages.CommandProcessor;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.net.MapleServerHandler;
import net.sf.odinms.net.PacketProcessor;
import net.sf.odinms.net.channel.remote.ChannelWorldInterface;
import net.sf.odinms.net.mina.MapleCodecFactory;
import net.sf.odinms.net.world.guild.MapleGuild;
import net.sf.odinms.net.world.guild.MapleGuildCharacter;
import net.sf.odinms.net.world.guild.MapleGuildSummary;
import net.sf.odinms.net.world.remote.WorldChannelInterface;
import net.sf.odinms.net.world.remote.WorldRegistry;
import net.sf.odinms.provider.MapleDataProviderFactory;
import net.sf.odinms.scripting.event.EventScriptManager;
import net.sf.odinms.server.AutobanManager;
import net.sf.odinms.server.MapleTrade;
import net.sf.odinms.server.ShutdownServer;
import net.sf.odinms.server.TimerManager;
import net.sf.odinms.server.maps.MapleMapFactory;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.PropertyTool;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.CloseFuture;
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
import net.sf.odinms.server.MapleIRC;
import net.sf.odinms.server.MapleShutdownHook;

public class ChannelServer implements Runnable, ChannelServerMBean {
	private static int uniqueID = 1;
	private Console console = ConsoleFactory.getConsole();
	private int port = 7575;
	private static Properties initialProp;
	private static final Logger log = LoggerFactory.getLogger(ChannelServer.class);
	private static WorldRegistry worldRegistry;
	private PlayerStorage players = new PlayerStorage();
	private String serverMessage;
	private int expRate;
	private int mesoRate;
	private int dropRate;
	private int petExpRate;
	private int dropUndroppables;
	private int moreThanOne;
	private int multiLevel;
	private int expLoss;
	private int channel;
	private int cashShop;
	private int autoban;
	private int faekChar;
	private int irc;
	private int reports;
	private int maxAP;
	private String wzPath;
	private String key;
	private Properties props = new Properties();
	private ChannelWorldInterface cwi ;
	private WorldChannelInterface wci = null;
	private IoAcceptor acceptor;
	private String ip;
	private boolean shutdown = false;
	private boolean finishedShutdown = false;

	private MapleMapFactory mapFactory;
	private EventScriptManager eventSM;
	private static Map<Integer, ChannelServer> instances = new HashMap<Integer, ChannelServer>();
	private static Map<String, ChannelServer> pendingInstances = new HashMap<String, ChannelServer>();
	private Map<Integer, MapleGuildSummary> gsStore = new HashMap<Integer, MapleGuildSummary>();
	private PropertyTool propTool = new PropertyTool(new Properties());
	private Boolean worldReady = true;
	private MapleIRC mapleIRC;

	private ChannelServer(String key) {
		mapFactory = new MapleMapFactory(MapleDataProviderFactory.getWzFile("Map.wz"), MapleDataProviderFactory.getWzFile("String.wz"));
		this.key = key;
	}
	
	public static WorldRegistry getWorldRegistry() {
		return worldRegistry;
	}
	
	public void reconnectWorld() {
		// check if the connection is really gone
		try {
			wci.isAvailable();
		} catch (RemoteException ex) {
			synchronized (worldReady) {
				worldReady = false;
			}
			synchronized (cwi) {
				synchronized (worldReady) {
					if (worldReady) return;
				}
				log.warn("Reconnecting to world server");
				synchronized (wci) {
					// completely re-establish the rmi connection
					try {
						initialProp = new Properties();
						FileReader fr = new FileReader(System.getProperty("net.sf.odinms.channel.config"));
						initialProp.load(fr);
						fr.close();
						Registry registry = LocateRegistry.getRegistry(initialProp.getProperty("net.sf.odinms.world.host"), 
							Registry.REGISTRY_PORT, new SslRMIClientSocketFactory());
						worldRegistry = (WorldRegistry) registry.lookup("WorldRegistry");
						cwi = new ChannelWorldInterfaceImpl(this);
						wci = worldRegistry.registerChannelServer(key, cwi);
						props = wci.getGameProperties();
						propTool = new PropertyTool(props);
						expRate = propTool.getSettingInt("net.sf.odinms.world.exp", 1);
						mesoRate = propTool.getSettingInt("net.sf.odinms.world.meso", 1);
						dropRate = propTool.getSettingInt("net.sf.odinms.world.drop", 1);
						petExpRate = propTool.getSettingInt("net.sf.odinms.world.petExp", 1);
						serverMessage = propTool.getSettingStr("net.sf.odinms.world.servermessage", "Welcome to OdinMS");
						dropUndroppables = propTool.getSettingInt("net.sf.odinms.world.alldrop", 1);
						moreThanOne = propTool.getSettingInt("net.sf.odinms.world.morethanone", 0);
						multiLevel = propTool.getSettingInt("net.sf.odinms.world.multiLevel", 0);
						cashShop = propTool.getSettingInt("net.sf.odinms.world.cashshop", 0);
						expLoss = propTool.getSettingInt("net.sf.odinms.world.expLoss", 0);
						autoban = propTool.getSettingInt("net.sf.odinms.world.autoban", 1);
						faekChar = propTool.getSettingInt("net.sf.odinms.world.faekchar", 0);
						irc = propTool.getSettingInt("net.sf.odinms.irc", 0);
						maxAP = propTool.getSettingInt("net.sf.odinms.world.maxap", 999);
						reports = propTool.getSettingInt("net.sf.odinms.world.reports", 1);
						wzPath = propTool.getSettingStr("net.sf.odinms.world.wzpath", "") + "/";
						Properties dbProp = new Properties();
						fr = new FileReader("db.properties");
						dbProp.load(fr);
						fr.close();
						DatabaseConnection.setProps(dbProp);
						DatabaseConnection.getConnection();
						wci.serverReady();
					} catch (Exception e) {
						log.error("Reconnecting failed", e);
					}
					worldReady = true;
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
			cwi = new ChannelWorldInterfaceImpl(this);
			wci = worldRegistry.registerChannelServer(key, cwi);
			props = wci.getGameProperties();
			propTool = new PropertyTool(props);
			expRate = propTool.getSettingInt("net.sf.odinms.world.exp", 1);
			mesoRate = propTool.getSettingInt("net.sf.odinms.world.meso", 1);
			dropRate = propTool.getSettingInt("net.sf.odinms.world.drop", 1);
			petExpRate = propTool.getSettingInt("net.sf.odinms.world.petExp", 1);
			serverMessage = propTool.getSettingStr("net.sf.odinms.world.servermessage", "Welcome to OdinMS");
			dropUndroppables = propTool.getSettingInt("net.sf.odinms.world.alldrop", 0);
			moreThanOne = propTool.getSettingInt("net.sf.odinms.world.morethanone", 0);
			multiLevel = propTool.getSettingInt("net.sf.odinms.world.multiLevel", 0);
			cashShop = propTool.getSettingInt("net.sf.odinms.world.cashshop", 0);
			expLoss = propTool.getSettingInt("net.sf.odinms.world.expLoss", 0);
			autoban = propTool.getSettingInt("net.sf.odinms.world.autoban", 1);
			faekChar = propTool.getSettingInt("net.sf.odinms.world.faekchar", 0);
			irc = propTool.getSettingInt("net.sf.odinms.irc", 0);
			maxAP = propTool.getSettingInt("net.sf.odinms.world.maxap", 999);
			reports = propTool.getSettingInt("net.sf.odinms.world.reports", 1);
			wzPath = propTool.getSettingStr("net.sf.odinms.world.wzpath", "") + "/";
			eventSM = new EventScriptManager(this, propTool.getSettingStr("net.sf.odinms.channel.events").split(" "));
			Properties dbProp = new Properties();
			FileReader fileReader = new FileReader("db.properties");
			dbProp.load(fileReader);
			fileReader.close();
			DatabaseConnection.setProps(dbProp);
			DatabaseConnection.getConnection();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		port = propTool.getSettingInt("net.sf.odinms.channel.net.port");
		ip = propTool.getSettingStr("net.sf.odinms.channel.net.interface") + ":" + port;
		
		ByteBuffer.setUseDirectBuffers(false);
		ByteBuffer.setAllocator(new SimpleByteBufferAllocator());

		acceptor = new SocketAcceptor();

		SocketAcceptorConfig cfg = new SocketAcceptorConfig();
		cfg.getFilterChain().addLast("codec", new ProtocolCodecFilter(new MapleCodecFactory()));
		TimerManager tMan = TimerManager.getInstance();
		tMan.start();
		tMan.register(AutobanManager.getInstance(), 60000);
		if (allowIRC()) {
		mapleIRC = new MapleIRC(this);
		}

		try {
			MapleServerHandler serverHandler = new MapleServerHandler(PacketProcessor.getProcessor(PacketProcessor.Mode.CHANNELSERVER), channel);
			acceptor.bind(new InetSocketAddress(port), serverHandler, cfg);
			console.setForegroundColor(ConsoleForegroundColor.LIGHT_GREEN);
			System.out.println("Channel " + getChannel() + " Listening on port " + port);
			console.resetColors();
			wci.serverReady();
			eventSM.init();
		} catch (IOException e) {
			console.setForegroundColor(ConsoleForegroundColor.LIGHT_RED);
			log.error("Binding to port " + port + " failed (ch: " + getChannel() + ")", e);
			console.resetColors();
		}
	}

	public void shutdown() {
		// dc all clients by hand so we get sessionClosed...
		shutdown = true;
		List<CloseFuture> futures = new LinkedList<CloseFuture>();
		Collection<MapleCharacter> allchars = players.getAllCharacters();
		MapleCharacter chrs[] = allchars.toArray(new MapleCharacter[allchars.size()]);
		for (MapleCharacter chr : chrs) {
			if (chr.getTrade() != null) {
				MapleTrade.cancelTrade(chr);
			}
			if (chr.getEventInstance() != null) {
				chr.getEventInstance().playerDisconnected(chr);
			}
			chr.saveToDB(true);
			if (chr.getCheatTracker() != null)
				chr.getCheatTracker().dispose();
			removePlayer(chr);
		}
		for (MapleCharacter chr : chrs) {
			futures.add(chr.getClient().getSession().close());
		}
		for (CloseFuture future : futures) {
			future.join(500);
		}
		finishedShutdown = true;
		
		wci = null;
		cwi = null;
	}
	
	public void unbind() {
		acceptor.unbindAll();
	}
	
	public boolean hasFinishedShutdown() {
		return finishedShutdown;
	}

	public MapleMapFactory getMapFactory() {
		return mapFactory;
	}
	
	public static ChannelServer newInstance(String key) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException, MalformedObjectNameException {
		ChannelServer instance = new ChannelServer(key);
		MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
		mBeanServer.registerMBean(instance, new ObjectName("net.sf.odinms.net.channel:type=ChannelServer,name=ChannelServer" + uniqueID++));
		pendingInstances.put(key, instance);
		return instance;
	}

	public static ChannelServer getInstance(int channel) {
		return instances.get(channel);
	}

	public void addPlayer(MapleCharacter chr) {
		players.registerPlayer(chr);
		chr.getClient().getSession().write(MaplePacketCreator.serverMessage(serverMessage));
	}

	public IPlayerStorage getPlayerStorage() {
		return players;
	}

	public void removePlayer(MapleCharacter chr) {
		players.deregisterPlayer(chr);
	}

	public int getConnectedClients() {
		return players.getAllCharacters().size();
	}

	@Override
	public String getServerMessage() {
		return serverMessage;
	}

	@Override
	public void setServerMessage(String serverMessage) {
		this.serverMessage = serverMessage;
		broadcastPacket(MaplePacketCreator.serverMessage(serverMessage));
	}

	public void broadcastPacket(MaplePacket data) {
		for (MapleCharacter chr : players.getAllCharacters()) {
			chr.getClient().getSession().write(data);
		}
	}
	
	public void broadcastGMPacket(MaplePacket data) {
		for (MapleCharacter chr : players.getAllCharacters()) {
			if(chr.isGM())
			    chr.getClient().getSession().write(data);
		}
	}

	public int getExpRate() {
		return expRate;
	}
	
	public void setExpRate(int expRate) {
		this.expRate = expRate;
	}
	
	public int getPetExpRate() {
		return petExpRate;
	}
	
	public void setPetExpRate(int petExpRate) {
		this.petExpRate = petExpRate;
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		if (pendingInstances.containsKey(key))
			pendingInstances.remove(key);
		if (instances.containsKey(channel))
			instances.remove(channel);
		instances.put(channel, this);
		this.channel = channel;
		this.mapFactory.setChannel(channel);
	}
	
	public static Collection<ChannelServer> getAllInstances() {
		return Collections.unmodifiableCollection(instances.values());
	}
	
	public String getIP() {
		return ip;
	}
	
	public String getIP(int channel) {
		try {
			return getWorldInterface().getIP(channel);
		} catch (RemoteException e) {
			log.error("Lost connection to world server", e);
			throw new RuntimeException("Lost connection to world server");
		}
	}
	
	public WorldChannelInterface getWorldInterface() {
		synchronized (worldReady) {
			while (!worldReady) {
				try {
					worldReady.wait();
				} catch (InterruptedException e) {}
			}
		}
		return wci;
	}
	
	public String getProperty(String name) {
		return props.getProperty(name);
	}

	public boolean isShutdown() {
		return shutdown;
	}
	
	@Override
	public void shutdown(int time) {
		broadcastPacket(MaplePacketCreator.serverNotice(0, "The world will be shut down in " + (time / 60000) +	" minutes, please log off safely"));
		TimerManager.getInstance().schedule(new ShutdownServer(getChannel()), time);
	}
	
	@Override
	public void shutdownWorld(int time) {
		try {
			getWorldInterface().shutdown(time);
		} catch (RemoteException e) {
			reconnectWorld();
		}
	}
	
	public int getLoadedMaps() {
		return mapFactory.getLoadedMaps();
	}

	public EventScriptManager getEventSM() {
		return eventSM;
	}
	
	public void reloadEvents() {
		eventSM.cancel();
		eventSM = new EventScriptManager(this, propTool.getSettingStr("net.sf.odinms.channel.events").split(" "));
		eventSM.init();
	}

	public int getMesoRate() {
		return mesoRate;
	}

	public void setMesoRate(int mesoRate) {
		this.mesoRate = mesoRate;
	}
	
	@Override
	public int getDropRate() {
		return dropRate;
	}

	@Override
	public void setDropRate(int dropRate) {
		this.dropRate = dropRate;
	}
	
	public void setUndroppablesDrop(boolean allow) {
	    this.dropUndroppables = allow ? 1 : 0;
	}
	
	public void setMoreThanOne(boolean allow) {
	    this.moreThanOne = allow ? 1 : 0;
	}
	
	public void setMultiLevel(boolean allow) {
	    this.multiLevel = allow ? 1 : 0;
	}

	public void setReports(boolean allow) {
	    this.reports = allow ? 1 : 0;
	}
	
	public boolean allowUndroppablesDrop() {
		return dropUndroppables > 0;
	}
	
	public boolean allowMoreThanOne() {
		return moreThanOne > 0;
	}
	
	public boolean allowMultiLevel() {
	    return multiLevel > 0;
	}
		
	public boolean allowExpLoss() {
	    return expLoss > 0;
	}
	
	public boolean allowCashShop() {
	    return cashShop > 0;
	}
	
	public boolean allowAutoban() {
	    return autoban > 0;
	}

	public boolean allowFaekChar() {
		return faekChar > 0;
	}

	public boolean allowIRC() {
		return irc > 0;
	}

	public boolean allowReports() {
	    return reports > 0;
	}
	
	public String getWzPath() {
	    return wzPath;
	}
	
	public MapleGuild getGuild(MapleGuildCharacter mgc) {
		int gid = mgc.getGuildId();
		MapleGuild g = null;
		try {
			g = this.getWorldInterface().getGuild(gid, mgc);
		} catch (RemoteException re) {
			log.error("RemoteException while fetching MapleGuild.", re);
			return null;
		}
		
		if (gsStore.get(gid) == null)
			gsStore.put(gid, new MapleGuildSummary(g));
		
		return g;
	}
	
	public MapleGuildSummary getGuildSummary(int gid) {
		if (gsStore.containsKey(gid)) {
			return gsStore.get(gid);
		} else {		//this shouldn't happen much, if ever, but if we're caught
			//without the summary, we'll have to do a worldop
			try {
				MapleGuild g = this.getWorldInterface().getGuild(gid, null);
				if (g != null)
					gsStore.put(gid, new MapleGuildSummary(g));
				return gsStore.get(gid);	//if g is null, we will end up returning null
			}
			catch (RemoteException re) {
				log.error("RemoteException while fetching GuildSummary.", re);
				return null;
			}
		}
	}
	
	public void updateGuildSummary(int gid, MapleGuildSummary mgs) {
		gsStore.put(gid, mgs);
	}
	
	public void reloadGuildSummary() {
		try {
			MapleGuild g;
			for (int i : gsStore.keySet()) {
				g = this.getWorldInterface().getGuild(i, null);
				if (g != null) {
					gsStore.put(i, new MapleGuildSummary(g));
				} else {
					gsStore.remove(i);
				}
			}
		} catch (RemoteException re) {
			log.error("RemoteException while reloading GuildSummary.", re);
		}
	}

    @Override
    public int getMaxAP() {
        return maxAP;
    }

    @Override
    public void setMaxAP(int maxAP) {
        this.maxAP = maxAP;
    }

	public MapleIRC getMapleIRC() {
		return mapleIRC;
	}

	public static void main(String args[]) throws FileNotFoundException, IOException, NotBoundException, InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException, MalformedObjectNameException {
		System.out.println("Snow's MapleStory Channel-Server V" + MapleServer.MAPLE_VERSION);
		initialProp = new Properties();
		initialProp.load(new FileReader(System.getProperty("net.sf.odinms.channel.config")));
		Registry registry = LocateRegistry.getRegistry(initialProp.getProperty("net.sf.odinms.world.host"), Registry.REGISTRY_PORT, new SslRMIClientSocketFactory());
		worldRegistry = (WorldRegistry) registry.lookup("WorldRegistry");
		for (int i = 0; i < Integer.parseInt(initialProp.getProperty("net.sf.odinms.channel.count", "0")); i++) {
			newInstance(initialProp.getProperty("net.sf.odinms.channel." + i + ".key")).run();
		}
		DatabaseConnection.getConnection(); // touch - so we see database problems early...
		CommandProcessor.registerMBean();
		Runtime.getRuntime().addShutdownHook(new MapleShutdownHook());
	}
	
	
}