package ch.epfl.bigdata.ts.ga.util;

import java.util.Random;

public class Util {
	public static Random R = new Random(System.currentTimeMillis());
	
	public static class Range {
		private double upper, lower;
		
		public Range(double lower, double upper) {
			this.upper = upper;
			this.lower = lower;
		}
		
		public double getLower() {
			return lower;
		}
		
		public double getUpper() {
			return upper;
		}
	}
}
