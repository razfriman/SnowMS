/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.console.irc;

import org.jibble.pircbot.User;

/**
 *
 * @author Raz
 */
public class TriviaUser {

    private User ircUser;
    private int points;
    private int streak;

    public TriviaUser(User ircUser) {
	this.ircUser = ircUser;
	this.points = 0;
	this.streak = 0;
    }

    public User getIrcUser() {
	return ircUser;
    }

    public int getPoints() {
	return points;
    }

    public void setPoints(int points) {
	this.points = points;
    }
    
    public void addPoint() {
	this.points++;
    }

    public int getStreak() {
	return streak;
    }

    public void setStreak(int streak) {
	this.streak = streak;
    }
    
    public void addStreak() {
	this.streak++;
    }
    
    
}
