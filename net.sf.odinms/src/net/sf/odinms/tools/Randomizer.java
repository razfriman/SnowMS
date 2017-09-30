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
package net.sf.odinms.tools;

import java.util.Random;

/**
 *
 * @author Raz
 */
public class Randomizer {

	private static Random rand = new Random();

	public static int randomInt() {
		return rand.nextInt();
	}

	/**
	 * Get a random integer with a minimum and maximum value
	 * @param min
	 * @param max
	 * @return A random integer between min and max
	 */
	public static int randomInt(int min, int max) {
		return rand.nextInt(max) + min;
	}

	/**
	 * Gets a random integer with a base
	 * @param base
	 * @return A random integer in base's range
	 */
	public static int randomInt(int base) {
		return rand.nextInt(base);
	}

	/**
	 * Gets a random double
	 * @return A random double
	 */
	public static double randomDouble() {
		return rand.nextDouble();
	}

	/**
	 * Fills a byte array with random bytes
	 * @param bytes
	 * @return bytes filled with randomized values
	 */
	public static byte[] randomBytes(byte[] bytes) {
		rand.nextBytes(bytes);
		return bytes;
	}

	/**
	 * Gets a random double
	 * @return a random gaussian
	 */
	public static double randomGaussian() {
		return rand.nextGaussian();
	}

	/**
	 * Gets a random boolean
	 * @return a random boolean
	 */
	public static boolean randomBoolean() {
		return rand.nextBoolean();
	}

	/**
	 * Random boolean using an integer and a max value as a chance
	 * @param num
	 * @param max
	 * @return num is >= a random integer with a base of max
	 */
	public static boolean randomBoolean(int num, int max) {
		return num >= randomInt(max + 1);
	}

	/**
	 * Random boolean using a double as a chance
	 * @param num
	 * @return num is >= a random double
	 */
	public static boolean randomBoolean(double num) {
		return num >= randomDouble();
	}

	/**
	 * Gets a random float
	 * @return A random float
	 */
	public static float randomFloat() {
		return rand.nextFloat();
	}

	/**
	 * Gets a random long
	 * @return A random long
	 */
	public static long randomLong() {
		return rand.nextLong();
	}

	/**
	 * Selects an integer from a random selection
	 * @param numbers
	 * @return the value of a random integer an array of integers
	 */
	public static int randomSelection(int[] numbers) {
		return numbers[randomInt(0, numbers.length)];
	}

	public static Object randomSelection(Object[] objects) {
		return objects[randomInt(0, objects.length)];
	}
}
