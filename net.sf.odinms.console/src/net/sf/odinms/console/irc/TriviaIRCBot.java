/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.console.irc;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sf.odinms.console.irc.TriviaQuestion.TriviaQuestionType;
import net.sf.odinms.provider.MapleData;
import net.sf.odinms.provider.MapleDataProvider;
import net.sf.odinms.provider.MapleDataProviderFactory;
import net.sf.odinms.provider.MapleDataTool;

import org.jibble.pircbot.Colors;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;

import ch.ubique.inieditor.IniEditor;
import net.sf.odinms.exttools.wzextract.XmlUtil;

/**
 *
 * @author Raz
 */
public class TriviaIRCBot extends PircBot {
    private String server;
    private String channel;
    private IniEditor settings = new IniEditor();
    private String owner;
    private List<TriviaQuestion> questions = new ArrayList<TriviaQuestion>();
    private TriviaQuestion currentQuestion;
    private TriviaQuestion previousQuestion;
    private int questionNumber;
    private List<TriviaUser> triviaUsers = new ArrayList<TriviaUser>();
    private boolean colors;
    private static char commandChar = '!';

    public TriviaIRCBot() {
	setName("Quiggle_V2");
	
	try {
	    settings.load(new File("settings.ini"));
	} catch (Exception e) {
	    System.out.println("Unable to find settings.properties");
	    return;
	}
	server = settings.get("TRIVIA", "SERVER");
	channel = settings.get("TRIVIA", "CHANNEL");
	owner = settings.get("TRIVIA", "OWNER");
	setName(settings.get("TRIVIA", "NAME"));
	colors = Integer.parseInt(settings.get("TRIVIA", "COLORS")) > 0;
	
	askQuestion();
    }
    
    public void sendMessageColor(String target, String message) {
	if (!colors) {
	    message = Colors.removeFormattingAndColors(message);
	}
	sendMessage(target, message);
    }
    
    @Override
    public void onMessage(String channel, String sender, String login, String hostname, String message) {
	long messageTime = System.currentTimeMillis();
	if (message.charAt(0) != commandChar) {//NORMAL TEXT
	    if (message.equalsIgnoreCase(currentQuestion.getAnswer())) {
		if (getTriviaUser(sender) == null) {
		    TriviaUser triviaUser = new TriviaUser(getUser(channel, sender));
		    triviaUsers.add(triviaUser);
		}
		TriviaUser triviaUser = getTriviaUser(sender);
		currentQuestion.setAnswerer(triviaUser);
		triviaUser.addPoint();
		if (previousQuestion != null && previousQuestion.getAnswerer() != null && previousQuestion.getAnswerer().equals(triviaUser)) {
		    triviaUser.addStreak();
		} else {
		    triviaUser.setStreak(1);
		}
		String winMessage = Colors.RED + "Winner: " + Colors.GREEN + Colors.UNDERLINE + sender;
		winMessage += Colors.UNDERLINE + Colors.RED + " Anwer: " + Colors.GREEN + Colors.UNDERLINE + currentQuestion.getAnswer();
		int secondsTaken = (int) (messageTime - currentQuestion.getTimeAsked()) / 1000;
		winMessage += Colors.UNDERLINE + Colors.RED + " Time: " + Colors.GREEN + Colors.UNDERLINE + secondsTaken;
		winMessage += Colors.UNDERLINE + Colors.RED + " Streak: " + Colors.GREEN + Colors.UNDERLINE + triviaUser.getStreak();
		winMessage += Colors.UNDERLINE + Colors.RED + " Points: " + Colors.GREEN + Colors.UNDERLINE + triviaUser.getPoints();
		winMessage += Colors.UNDERLINE + Colors.RED + " WPM: " + Colors.GREEN + Colors.UNDERLINE + getWPM(currentQuestion.getAnswer(), secondsTaken);//TODO
		winMessage += Colors.UNDERLINE + Colors.RED + " Rank: " + Colors.GREEN + Colors.UNDERLINE + "1st";//TODO
		winMessage += Colors.UNDERLINE + Colors.RED + " PreviousRank: " + Colors.GREEN + Colors.UNDERLINE + "2nd";//TODO
		sendMessageColor(channel, winMessage);
		askQuestion();
	    }
	} else {//COMMANDS
	    String[] splitted = message.substring(1).split(" ");
	    if (splitted[0].equalsIgnoreCase("answer")) {
		if (previousQuestion != null) {
		    sendMessageColor(channel, "The previous answer was: " + previousQuestion.getAnswer());//COLOR CODE
		} else {
		    sendMessageColor(channel, "There is no previous answer");
		}
	    } else if (splitted[0].equalsIgnoreCase("pquestion")) {
		if (previousQuestion != null) {
		    sendMessageColor(channel, "The previous question was: " + previousQuestion.getQuestionFormatted());//COLOR CODE
		} else {
		    sendMessageColor(channel, "There is no previous question");
		}
	    } else if (splitted[0].equalsIgnoreCase("question")) {
		if (currentQuestion != null) {
		    sendMessageColor(channel, "The current question is: " + currentQuestion.getQuestionFormatted());//COLOR CODE
		} else {
		    sendMessageColor(channel, "There is no current question");
		}
	    } else if (splitted[0].equalsIgnoreCase("hint")) {
		//[08:04] <!Qwibble> 4Here's a hint,3 1_
		//[08:06] <!Qwibble> 4Here's a hint,3 Master _____ _____
		sendMessage(channel, "Here's a hint, " + currentQuestion.getAnswer().charAt(0));
	    } else if (splitted[0].equalsIgnoreCase("endgame")) {
		
	    } else if (splitted[0].equalsIgnoreCase("startgame")) {
		//[08:04] <!Qwibble> 4Starting the trivia. Round of3 Unlimited4 questions.3 !strivia4 to stop. Total:3 359
	    } else if (splitted[0].equalsIgnoreCase("cheat") && isOwner(sender)) {
		sendMessageColor(channel, "DUMBASS... " + currentQuestion.getAnswer());
		askQuestion();
	    } else if (splitted[0].equalsIgnoreCase("cheatme") && isOwner(sender)) {
		sendMessage(sender, currentQuestion.getAnswer());
	    }
	}
    }
    
    public User getUser(String channel, String name) {
	for (User user : getUsers(channel)) {
	    if (user.getNick().equals(name)) {
		return user;
	    }
	}
	return null;
    }

    public TriviaUser getTriviaUser(String name) {
	for (TriviaUser triviaUser : triviaUsers) {
	    if (triviaUser.getIrcUser().getNick().equals(name)) {
		return triviaUser;
	    }
	}
	return null;
    }

    public String getServerName() {
	return server;
    }

    public String getChannel() {
	return channel;
    }
    
    public boolean isOwner(String user) {
	return owner.equals(user);
    }
    
    public int getWPM(String text, int seconds) {
	return 1;
    }

    public static void main(String[] args) {
	TriviaIRCBot bot = new TriviaIRCBot();
	bot.setVerbose(true);
	try {
	    bot.connect(bot.getServerName());
	} catch (Exception e) {
	    System.out.println("Unable to connect to: " + bot.getServerName());
	    return;
	}
	bot.joinChannel(bot.getChannel());
    }
    
    public void loadQuestions() {
	//Example
	questions.add(new TriviaQuestion("Maple*", "Story", TriviaQuestionType.FILL_IN_THE_BLANK));
	questions.add(new TriviaQuestion("*Story", "Maple", TriviaQuestionType.FILL_IN_THE_BLANK));
	
	//MAPS
	questions.add(new TriviaQuestion("", "Aquarium", TriviaQuestionType.UNSCRAMBLE));
	questions.add(new TriviaQuestion("", "Henesys", TriviaQuestionType.UNSCRAMBLE));
	questions.add(new TriviaQuestion("", "Ellinia", TriviaQuestionType.UNSCRAMBLE));
	questions.add(new TriviaQuestion("", "Kerning City", TriviaQuestionType.UNSCRAMBLE));
	questions.add(new TriviaQuestion("", "Lith Harbor", TriviaQuestionType.UNSCRAMBLE));
	questions.add(new TriviaQuestion("", "Southperry", TriviaQuestionType.UNSCRAMBLE));
	questions.add(new TriviaQuestion("", "Perion", TriviaQuestionType.UNSCRAMBLE));
	questions.add(new TriviaQuestion("", "Amherst", TriviaQuestionType.UNSCRAMBLE));
	questions.add(new TriviaQuestion("", "Mushroom Village", TriviaQuestionType.UNSCRAMBLE));
	questions.add(new TriviaQuestion("", "Sleepywood", TriviaQuestionType.UNSCRAMBLE));
	questions.add(new TriviaQuestion("", "Aqua Road", TriviaQuestionType.UNSCRAMBLE));
	questions.add(new TriviaQuestion("", "El Nath", TriviaQuestionType.UNSCRAMBLE));
	questions.add(new TriviaQuestion("", "Korean Folk Town", TriviaQuestionType.UNSCRAMBLE));
	questions.add(new TriviaQuestion("", "Ludibrium", TriviaQuestionType.UNSCRAMBLE));
	questions.add(new TriviaQuestion("", "Eos Tower", TriviaQuestionType.UNSCRAMBLE));
	questions.add(new TriviaQuestion("", "Mu Lung", TriviaQuestionType.UNSCRAMBLE));
	questions.add(new TriviaQuestion("", "Herb Town", TriviaQuestionType.UNSCRAMBLE));
	questions.add(new TriviaQuestion("", "Orbis", TriviaQuestionType.UNSCRAMBLE));
	questions.add(new TriviaQuestion("", "Omega Sector", TriviaQuestionType.UNSCRAMBLE));
	questions.add(new TriviaQuestion("", "Leafre", TriviaQuestionType.UNSCRAMBLE));
	questions.add(new TriviaQuestion("", "New Leaf City", TriviaQuestionType.UNSCRAMBLE));
	questions.add(new TriviaQuestion("", "Amoria", TriviaQuestionType.UNSCRAMBLE));
	questions.add(new TriviaQuestion("", "Singapore", TriviaQuestionType.UNSCRAMBLE));
	questions.add(new TriviaQuestion("", "Nautilus Port", TriviaQuestionType.UNSCRAMBLE));
	//JOBS
	questions.add(new TriviaQuestion("", "Beginner", TriviaQuestionType.UNSCRAMBLE));
	questions.add(new TriviaQuestion("", "Warrior", TriviaQuestionType.UNSCRAMBLE));
	questions.add(new TriviaQuestion("", "Magician", TriviaQuestionType.UNSCRAMBLE));
	questions.add(new TriviaQuestion("", "Bowman", TriviaQuestionType.UNSCRAMBLE));
	questions.add(new TriviaQuestion("", "Thief", TriviaQuestionType.UNSCRAMBLE));
	questions.add(new TriviaQuestion("", "Pirate", TriviaQuestionType.UNSCRAMBLE));
	questions.add(new TriviaQuestion("", "Fighter", TriviaQuestionType.UNSCRAMBLE));
	questions.add(new TriviaQuestion("", "Page", TriviaQuestionType.UNSCRAMBLE));
	questions.add(new TriviaQuestion("", "Spearman", TriviaQuestionType.UNSCRAMBLE));
	questions.add(new TriviaQuestion("", "Wizard", TriviaQuestionType.UNSCRAMBLE));
	questions.add(new TriviaQuestion("", "Cleric", TriviaQuestionType.UNSCRAMBLE));
	questions.add(new TriviaQuestion("", "Hunter", TriviaQuestionType.UNSCRAMBLE));
	questions.add(new TriviaQuestion("", "Crossbow Man", TriviaQuestionType.UNSCRAMBLE));
	questions.add(new TriviaQuestion("", "Assassin", TriviaQuestionType.UNSCRAMBLE));
	questions.add(new TriviaQuestion("", "Bandit", TriviaQuestionType.UNSCRAMBLE));
	questions.add(new TriviaQuestion("", "Brawler", TriviaQuestionType.UNSCRAMBLE));
	questions.add(new TriviaQuestion("", "Gunslinger", TriviaQuestionType.UNSCRAMBLE));
	
	MapleDataProvider stringWz = MapleDataProviderFactory.getWzFile("String.wz");

	/*
	//MONSTERS
	for(MapleData mobData : stringWz.getData("Mob.img").getChildren()) {
	    String name = MapleDataTool.getString("name", mobData);
	    if (name != null && isAskableQuestion(name)) {
		questions.add(new TriviaQuestion("", name, TriviaQuestionType.UNSCRAMBLE));
	    }
	}
	
	//NPCS
	for(MapleData npcData : stringWz.getData("Npc.img").getChildren()) {
	    try {
		String name = MapleDataTool.getString("name", npcData);
		if (name != null && isAskableQuestion(name)) {
		    questions.add(new TriviaQuestion("", name, TriviaQuestionType.UNSCRAMBLE));
		}
	    } catch (Exception e) {
		//Error with NPC
	    }
	}
	
	//PETS
	for(MapleData petData : stringWz.getData("Pet.img").getChildren()) {
	    String name = MapleDataTool.getString("name", petData);
	    if (name != null && isAskableQuestion(name)) {
		questions.add(new TriviaQuestion("", name, TriviaQuestionType.UNSCRAMBLE));
	    }
	}*/
	
	questions.add(new TriviaQuestion("Which Skill allows a Magician to temporarily replace damage with MP instead of HP?", "Magic Guard", TriviaQuestionType.NORMAL_QUESTION));
	questions.add(new TriviaQuestion("Which Skill allows a White Knight to cancel out enemy's magical defense up skill?", "Magic Crash", TriviaQuestionType.NORMAL_QUESTION));
	questions.add(new TriviaQuestion("Which Skill allows an Ice/Lightning to temporarily boost up the magic attack of party members?", "Meditation", TriviaQuestionType.NORMAL_QUESTION));
	questions.add(new TriviaQuestion("Which Skill allows a 2nd Job Magician to soak up a monsters MP after a magical attack?", "MP Eater", TriviaQuestionType.NORMAL_QUESTION));
	
	//questions.add(new TriviaQuestion("", "", TriviaQuestionType.UNSCRAMBLE));
	//questions.add(new TriviaQuestion("", "", TriviaQuestionType.NORMAL_QUESTION));
	//questions.add(new TriviaQuestion("", "", TriviaQuestionType.FILL_IN_THE_BLANK));
	Collections.shuffle(questions);
	questionNumber = 0;
    }
    
    public boolean isAskableQuestion(String answer) {
	if (answer == null) {
	    return false;
	}
	answer = XmlUtil.unsanitizeText(answer);
	char[] letters = answer.toCharArray();
	for(char chr : letters) {
	    if (chr > 128) {
		return false;
	    }
	}
	return true;
    }
    
    public void askQuestion() {
	if (questions.size() == 0) {
	    loadQuestions();
	    sendMessageColor(channel, "Loading and Shuffling Questions");
	}
	questionNumber ++;
	TriviaQuestion question = questions.remove(0);
	sendMessageColor(channel, Colors.RED + questionNumber + ". " + question.getQuestionFormatted());
	question.setTimeAsked(System.currentTimeMillis());
	previousQuestion = currentQuestion;
	currentQuestion = question;
    }
}
