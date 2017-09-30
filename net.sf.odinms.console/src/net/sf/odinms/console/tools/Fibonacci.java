/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.console.tools;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Raz
 */
public class Fibonacci {

    public static void main(String args[]) {
		short ver = (short) 70;
		//return ((((packet[0] ^ iv[2]) & 0xFF) == ((mapleVersion >> 8) & 0xFF)) && (((packet[1] ^ iv[3]) & 0xFF) == (mapleVersion & 0xFF)));
		System.out.println(((ver >> 8) & 0xFF));
		System.out.println((ver & 0xFF));
		System.exit(0);
	System.out.println("Snow's Fibonacci Sequence");
	while(true) {
	    System.out.println("Enter a Length");
	    int len = -1;
	    try {
		len = Integer.parseInt(System.console().readLine());
	    } catch (Exception e) {
		System.out.println("Invalid Number");
		continue;
	    }
	    List<Integer> fibSeq = getFibonacci(len);
	    for(long fib : fibSeq) {
		System.out.print(fib + " ");
	    }
	    System.out.println();
	}
    }
    
    public static List<Integer> getFibonacci(int len) {
	List<Integer> ret = new ArrayList<Integer>(len);
	if (len == 0) {
	    return ret;
	}
	if (len > 0) {
	    ret.add(0);//Starting Values
	} else if (len > 1) {
	ret.add(1);//Starting Values
	for(int i = 0; i < len; i++) {
	    if (ret.size() == len) {
		break;
	    }
	    ret.add(ret.get(i) + ret.get(i + 1));//New Values
	}
	}
	return ret;
    }
}
