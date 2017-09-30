package net.sf.odinms.console.tools;

import java.util.concurrent.*;
import java.io.*;
import java.net.*;

public class LinkFinder implements Runnable {

	public static final int NUM_WORKERS = 100;
	public static final long DELAY_START = 50;

	public static final String PART_LEFT = "http://rapidshare.com/files/";
	public static final String PART_RIGHT = "29891/sql.rar";

	public static final String[] FAILSTRINGS = new String[] {
		"The file could not be found",
		"This file has been removed from the server"
	};

	public static final boolean IS_PADDED = true;
	public static final int NUM_PADDING = 4;

	public static final int NUM_MIN = 1000;
	public static final int NUM_MAX = 9999;

	public static void main(String[] args) {
		new LinkFinder();
	}

	public static String getLeftPaddedStr(String in, char padchar, int length) {
		StringBuilder builder = new StringBuilder(length);
		for (int x = in.length(); x < length; x++) {
			builder.append(padchar);
		}
		builder.append(in);
		return builder.toString();
	}

	//===============================================================

	private Semaphore wmon = new Semaphore(NUM_WORKERS);

	private Thread me = new Thread(this);

	public LinkFinder() {
		me.start();
	}

	public void run() {
		System.out.println("LinkFinder -- Made by Demod");
		System.out.println("Maximum Worker Threads: "+NUM_WORKERS);
		System.out.println("Delay between Workers: "+DELAY_START);
		System.out.println("Current Format: " + PART_LEFT+getLeftPaddedStr("",'X',NUM_PADDING)+PART_RIGHT);
		System.out.println("Going from "+(IS_PADDED?getLeftPaddedStr(""+NUM_MIN,'0',NUM_PADDING):NUM_MIN)+
						" to "+(IS_PADDED?getLeftPaddedStr(""+NUM_MAX,'0',NUM_PADDING):NUM_MAX));
		System.out.println("Fail strings:");
		for (String fs : FAILSTRINGS)
			System.out.println("     \""+fs+"\"");
		System.out.print("Press enter key to start...");
		try {
			System.in.read();
		} catch (IOException e1) {e1.printStackTrace();}
		System.out.println("Starting...");
		mlfWorker.factory(me,wmon,NUM_MIN);
		try {
			while (true)
				Thread.sleep(1000);
		}
		catch (InterruptedException e) {}
	}
}

class mlfWorker implements Runnable {
	int num;
	String snum;
	Thread oper;
	Semaphore wmon;
	public mlfWorker(Thread o, Semaphore wm, int n) {
		if (n>LinkFinder.NUM_MAX) {
			o.interrupt();
		}
		oper = o;
		wmon = wm;
		num = n;
		snum = ""+((LinkFinder.IS_PADDED)?LinkFinder.getLeftPaddedStr(""+num,'0',LinkFinder.NUM_PADDING):num);
		try {
			Thread.sleep(LinkFinder.DELAY_START);//This isnt a DoS
		} catch (InterruptedException e) {e.printStackTrace();}
	}
	public void run() {
		if ((num>LinkFinder.NUM_MAX)||oper.isInterrupted()||!oper.isAlive()) return;
		try {
			wmon.acquire();
			if (oper.isInterrupted()||!oper.isAlive()) return;
			factory(oper,wmon,num+1);
			long tmr = System.currentTimeMillis();
			//modified snow copypasta
			URL url = new URL(LinkFinder.PART_LEFT + snum + LinkFinder.PART_RIGHT);
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String line = null;
			boolean foundError = false;
			while ((line = in.readLine()) != null) {
				for (String fs : LinkFinder.FAILSTRINGS)
					if(line.contains(fs)) {
						foundError = true;
						break;
					}
			}
			in.close();
			double count = (System.currentTimeMillis()-tmr)/(1000.0);
			if (oper.isInterrupted()||!oper.isAlive()) return;
			if (!foundError) {
				System.out.println(snum+" Found IT["+count+"]");
				System.out.println(snum+" Link: "+LinkFinder.PART_LEFT+snum+LinkFinder.PART_RIGHT);
				oper.interrupt();
				try {Thread.sleep(500);} catch (Exception e) {}
				wmon.release(LinkFinder.NUM_WORKERS-wmon.availablePermits());
			}
			else System.out.println(snum+" FAIL["+count+"]");
		}
		catch (Exception e) {
			System.out.println(snum+" "+e);
			oper.interrupt();
		}
		finally {
			if (LinkFinder.NUM_WORKERS-wmon.availablePermits()>0) wmon.release();
		}
	}

	public static Thread factory(Thread o, Semaphore wm, int n) {
		Thread ret = new Thread(new mlfWorker(o,wm,n));
		ret.start();
		return ret;
	}
}