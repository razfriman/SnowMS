/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.world.WorldServer;
import net.sf.odinms.tools.MaplePacketCreator;

import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;

/**
 *
 * @author Raz
 */
public class MapleIRC extends PircBot {

	private ChannelServer cserv;
	private String serverName;
	private String server;
	private String channel;
	private final Map<Integer, MapleCharacter> players = new HashMap<Integer, MapleCharacter>();

	public MapleIRC(ChannelServer cserv) {
		try {
			this.cserv = cserv;
			Properties props = WorldServer.getInstance().getWorldProp();
			serverName = props.getProperty("net.sf.odinms.server.name", "MapleStory");
			setName(serverName + "-" + cserv.getChannel());
			server = props.getProperty("net.sf.odinms.irc.server");
			channel = props.getProperty("net.sf.odinms.irc.channel");
			connect(server);
			joinChannel(channel);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ChannelServer getCserv() {
		return cserv;
	}

	public void setCserv(ChannelServer cserv) {
		this.cserv = cserv;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	@Override
	protected void onNickChange(String oldNick, String login, String hostname, String newNick) {
		broadcastMessage(MaplePacketCreator.serverNotice(5, "[IRC] " + oldNick + " is now known as " + newNick));
	}

	@Override
	protected void onJoin(String channel, String sender, String login, String hostname) {
		broadcastMessage(MaplePacketCreator.serverNotice(5, "[IRC] " + sender + " has joined " + channel));
	}

	@Override
	protected void onPart(String channel, String sender, String login, String hostname) {
		broadcastMessage(MaplePacketCreator.serverNotice(5, "[IRC] " + sender + " has left " + channel));
	}

	@Override
	protected void onAction(String sender, String login, String hostname, String target, String action) {
		broadcastMessage(MaplePacketCreator.serverNotice(6, "[IRC]* " + sender + " " + action));
	}

	@Override
	protected void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason) {
		broadcastMessage(MaplePacketCreator.serverNotice(5, "[IRC] " + sourceNick + " has quit"));
	}

	@Override
	protected void onKick(String channel, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason) {
		broadcastMessage(MaplePacketCreator.serverNotice(5, "[IRC] " + recipientNick + " has been kicked by " + kickerNick + "(" + reason + ")"));
	}

	@Override
	protected void onMessage(String channel, String sender, String login, String hostname, String message) {
		User user = getUser(channel, sender);
		if (user != null) {
			broadcastMessage(MaplePacketCreator.serverNotice(6, "[IRC] <" + user.toString() + "> " + message));
		} else {
			broadcastMessage(MaplePacketCreator.serverNotice(6, "[IRC] <" + sender + "> " + message));
		}
	}

	public boolean sendUserMessage(String name, String message) {
		if(isConnected()) {
			sendMessage(channel, name + ": " + message);
			broadcastMessage(MaplePacketCreator.serverNotice(6, "[" + serverName + "-" + "IRC] <" + name + "> " + message));
			return true;
		}
		return false;
	}

	public User getUser(String channel, String name) {
		for (User user : getUsers(channel)) {
			if (user.getNick().equalsIgnoreCase(name) ||user.getNick().substring(1).equalsIgnoreCase(name)) {
				return user;
			}
		}
		return null;
	}

	public void listUsers(MapleCharacter player) {
		String text = "";
		boolean blue = true;
		for (User user : getUsers(channel)) {
			text += blue ? "#b" : "#d";
			text += user.toString() + "\r\n";
			blue = !blue;
		}
		player.getClient().getSession().write(MaplePacketCreator.serverMessage(7, -1, text, false, false, 1052013));
	}

	public boolean register(MapleCharacter chr) {
		if (!players.containsKey(chr.getId())) {
			players.put(chr.getId(), chr);
			return true;
		}
		return false;
	}

	public boolean deRegister(MapleCharacter chr) {
		if (players.containsKey(chr.getId())) {
			players.remove(chr.getId());
			return true;
		}
		return false;
	}

	public void broadcastMessage(MaplePacket packet) {
		for (MapleCharacter chr : players.values()) {
			if (chr != null) {
			chr.getClient().getSession().write(packet);
			}
		}
	}
}
