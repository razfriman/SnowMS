/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.console.irc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jibble.pircbot.Colors;

/**
 *
 * @author Raz
 */
public class TriviaQuestion {

    private String question;
    private String answer;
    private TriviaQuestionType type;
    private long timeAsked;
    private String questionFormatted;
    private TriviaUser answerer;

    public TriviaQuestion(String question, String answer, TriviaQuestionType type) {
	this.question = question;
	this.answer = answer;
	this.type = type;
    }

    public String getAnswer() {
	return answer;
    }

    public void setAnswer(String answer) {
	this.answer = answer;
    }

    public String getQuestion() {
	return question;
    }

    public void setQuestion(String question) {
	this.question = question;
    }

    public TriviaQuestionType getType() {
	return type;
    }

    public void setType(TriviaQuestionType type) {
	this.type = type;
    }

    public long getTimeAsked() {
	return timeAsked;
    }

    public void setTimeAsked(long timeAsked) {
	this.timeAsked = timeAsked;
    }

    public TriviaUser getAnswerer() {
	return answerer;
    }

    public void setAnswerer(TriviaUser answerer) {
	this.answerer = answerer;
    }

    public String getQuestionFormatted() {
	if (questionFormatted == null) {
	    StringBuffer buffer = new StringBuffer();
	    switch (type) {
		case FILL_IN_THE_BLANK: {
		    buffer.append(Colors.GREEN + question);
		    int index = buffer.indexOf("*");
		    if (index == -1) {
			index = buffer.length() - 1;
		    }
		    buffer.replace(index, index + 1, getAnswerBlank());
		    break;
		}
		case UNSCRAMBLE: {
		    buffer.append(Colors.RED + "Unscramble the following: ");
		    buffer.append(Colors.GREEN + Colors.UNDERLINE + getScrambledAnswer());
		    break;
		}
		case NORMAL_QUESTION: {
		    buffer.append(Colors.GREEN + question);
		    break;
		}
	    }
	    questionFormatted = buffer.toString();
	}
	return questionFormatted;
    }

    public String getAnswerBlank() {
	String blanks = "";
	for (int i = 0; i < answer.length(); i++) {
	    blanks += "_";
	}
	return blanks;
    }

    public String getScrambledAnswer() {
	String scrambledAnswer = answer;
	scrambledAnswer = scrambledAnswer.toLowerCase();
	String[] scrambledWords = scrambledAnswer.split(" ");
	scrambledAnswer = "";
	for (String scrambledWord : scrambledWords) {
	    List<Integer> numbers = new ArrayList<Integer>();
	    for (int i = 0; i < scrambledWord.length(); i++) {
		numbers.add(i);
	    }
	    Collections.shuffle(numbers);
	    while (numbers.size() > 0) {
		scrambledAnswer += scrambledWord.charAt(numbers.remove(0));
	    }
	    scrambledAnswer += " ";
	}

	return scrambledAnswer.substring(0, scrambledAnswer.length() - 1);
    }

    public static enum TriviaQuestionType {

	FILL_IN_THE_BLANK,
	UNSCRAMBLE,
	NORMAL_QUESTION,
    }
}
