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

package net.sf.odinms.tools.performance;

import java.io.IOException;
import java.io.Writer;
import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * Provides a profiler based on CPU usage through sampling.
 * 
 * @author frzme
 * @since Revision 980
 * @version 1.0
 */
public class CPUSampler {
	private List<String> included = new LinkedList<String>();
	private static CPUSampler instance = new CPUSampler();
	private long interval = 5;
	private SamplerThread sampler = null;
	private Map<StackTrace, Integer> recorded = new HashMap<StackTrace, Integer>();
	private int totalSamples = 0;

	/**
	 * Singleton instantiator.
	 */
	private CPUSampler() {
	}

	/**
	 * Singleton function.
	 * 
	 * @return Gets the current CPUSampler instance.
	 */
	public static CPUSampler getInstance() {
		return instance;
	}

	/**
	 * Sets the interval the CPU usage is sampled.
	 * 
	 * @param millis Milliseconds between samples.
	 */
	public void setInterval(long millis) {
		interval = millis;
	}

	/**
	 * Includes an element in the sample.
	 * 
	 * @param include The element to include for sampling.
	 */
	public void addIncluded(String include) {
		for (String alreadyIncluded : included) {
			if (include.startsWith(alreadyIncluded)) {
				return;
			}
		}
		included.add(include);
	}

	/**
	 * Resets all stored samples.
	 */
	public void reset() {
		recorded.clear();
		totalSamples = 0;
	}

	/**
	 * Starts sampling.
	 */
	public void start() {
		if (sampler == null) {
			sampler = new SamplerThread();
			sampler.start();
		}
	}

	/**
	 * Stops sampling.
	 */
	public void stop() {
		if (sampler != null) {
			sampler.stop();
			sampler = null;
		}
	}

	/**
	 * Gets the top consumers of CPU usage as a stacktrace list.
	 * 
	 * @return The stacktraces of the top CPU consumers.
	 */
	public SampledStacktraces getTopConsumers() {
		List<StacktraceWithCount> ret = new ArrayList<StacktraceWithCount>();
		Set<Entry<StackTrace, Integer>> entrySet = recorded.entrySet();
		for (Entry<StackTrace, Integer> entry : entrySet) {
			ret.add(new StacktraceWithCount(entry.getValue(), entry.getKey()));
		}
		Collections.sort(ret);
		return new SampledStacktraces(ret, totalSamples);
	}

	/**
	 * Saves CPU sample information to a Writer <code>writer</code>.
	 * 
	 * @param writer The writer to write information to.
	 * @param minInvocations The minimum invocations for a stack trace to be written.
	 * @param topMethods The number of top methods to write.
	 * @throws IOException
	 */
	public void save(Writer writer, int minInvocations, int topMethods) throws IOException {
		SampledStacktraces topConsumers = getTopConsumers();
		StringBuilder builder = new StringBuilder(); // build our summary :o
		builder.append("Top Methods:\n");
		for (int i = 0; i < topMethods && i < topConsumers.getTopConsumers().size(); i++) {
			builder.append(topConsumers.getTopConsumers().get(i).toString(topConsumers.getTotalInvocations(), 1));
		}
		builder.append("\nStack Traces:\n");
		writer.write(builder.toString());
		writer.write(topConsumers.toString(minInvocations));
		writer.flush();
	}

	/**
	 * Processes the stack traces for each thread in <code>traces</code>
	 * 
	 * @param traces A <code>Map</code> of threads to a stack trace to process.
	 */
	private void consumeStackTraces(Map<Thread, StackTraceElement[]> traces) {
		for (Entry<Thread, StackTraceElement[]> trace : traces.entrySet()) {
			int relevant = findRelevantElement(trace.getValue());
			if (relevant != -1) {
				StackTrace st = new StackTrace(trace.getValue(), relevant, trace.getKey().getState());
				Integer i = recorded.get(st);
				totalSamples++;
				if (i == null) {
					recorded.put(st, Integer.valueOf(1));
				} else {
					recorded.put(st, Integer.valueOf(i.intValue() + 1));
				}
			}
		}
	}

	/**
	 * Finds a relevant element in the stack trace <code>trace</code> and returns its index.
	 * 
	 * @param trace The stack trace to search through.
	 * @return Index of the relevant element, -1 on error.
	 */
	private int findRelevantElement(StackTraceElement[] trace) {
		if (trace.length == 0) {
			return -1;
		} else if (included.size() == 0) {
			return 0;
		}
		int firstIncluded = -1;
		for (String myIncluded : included) {
			for (int i = 0; i < trace.length; i++) {
				StackTraceElement ste = trace[i];
				if (ste.getClassName().startsWith(myIncluded)) {
					if (i < firstIncluded || firstIncluded == -1) {
						firstIncluded = i;
						break;
					}
				}
			}
		}
		if (firstIncluded >= 0 &&
			trace[firstIncluded].getClassName().equals("net.sf.odinms.tools.performance.CPUSampler$SamplerThread")) { // don't
			// sample
			// us
			return -1;
		}
		return firstIncluded;
	}

	/**
	 * Represents a stack trace.
	 * 
	 * @author frzme
	 * @since Revision 980
	 * @version 1.0
	 */
	private static class StackTrace {
		private StackTraceElement[] trace;
		private State state;

		/**
		 * Class constructor.
		 * 
		 * @param trace The list of StackTraceElements that this stack trace represents.
		 * @param startAt Index in <code>trace</code> to start at.
		 * @param state State of the thread that this StackTrace represents.
		 */
		public StackTrace(StackTraceElement[] trace, int startAt, State state) {
			this.state = state;
			if (startAt == 0) {
				this.trace = trace;
			} else {
				this.trace = new StackTraceElement[trace.length - startAt];
				System.arraycopy(trace, startAt, this.trace, 0, this.trace.length);
			}
		}

		/**
		 * Checks whether the given StackTrace equals this object.
		 * 
		 * @return <code>True</code> if the StackTraces are equivalent, <code>false</code> otherwise.
		 */
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof StackTrace)) {
				return false;
			}
			StackTrace other = (StackTrace) obj;
			if (other.trace.length != trace.length) {
				return false;
			}
			if (!(other.state == this.state)) {
				return false;
			}
			for (int i = 0; i < trace.length; i++) {
				if (!trace[i].equals(other.trace[i])) {
					return false;
				}
			}
			return true;
		}

		/**
		 * Gets the hash code of this StackTrace.
		 * 
		 * @return Hash code of this StackTrace.
		 */
		@Override
		public int hashCode() {
			int ret = 13 * trace.length + state.hashCode();
			for (StackTraceElement ste : trace) {
				ret ^= ste.hashCode();
			}
			return ret;
		}

		/**
		 * Gets the list of trace elements that this object represents.
		 * 
		 * @return The list of elements that this object represents.
		 */
		public StackTraceElement[] getTrace() {
			return trace;
		}

		/**
		 * Gets a string representing every element in this stack trace.
		 * 
		 * @returns The stack trace as a string.
		 */
		@Override
		public String toString() {
			return toString(-1);
		}

		/**
		 * Gets a string representing <code>traceLength</code> number of elements in this stack trace.
		 * 
		 * @param traceLength The length of the trace to turn into a string. Negative to include all elements.
		 * @return The stack trace as a string.
		 */
		public String toString(int traceLength) {
			StringBuilder ret = new StringBuilder("State: ");
			ret.append(state.name());
			if (traceLength > 1) {
				ret.append("\n");
			} else {
				ret.append(" ");
			}
			int i = 0;
			for (StackTraceElement ste : trace) {
				i++;
				if (i > traceLength) {
					break;
				}
				ret.append(ste.getClassName());
				ret.append("#");
				ret.append(ste.getMethodName());
				ret.append(" (Line: ");
				ret.append(ste.getLineNumber());
				ret.append(")\n");
			}
			return ret.toString();
		}
	}

	/**
	 * Provides a thread implementation to sample CPU usage data.
	 * 
	 * @author frzme
	 * @since Revision 980
	 * @version 1.0
	 */
	private class SamplerThread implements Runnable {
		private boolean running = false;
		private boolean shouldRun = false;
		private Thread rthread;

		/**
		 * Starts the sampler thread.
		 */
		public void start() {
			if (!running) {
				shouldRun = true;
				rthread = new Thread(this, "CPU Sampling Thread");
				rthread.start();
				running = true;
			}
		}

		/**
		 * Stops the sampler thread.
		 */
		public void stop() {
			this.shouldRun = false;
			rthread.interrupt();
			try {
				rthread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		/**
		 * Runs the sampler thread.
		 */
		@Override
		public void run() {
			while (shouldRun) {
				consumeStackTraces(Thread.getAllStackTraces());
				try {
					Thread.sleep(interval);
				} catch (InterruptedException e) {
					return;
				}
			}
		}
	}

	/**
	 * Represents a stack trace with a count of sampled invocations.
	 * 
	 * @author frzme
	 * @version 1.0
	 * @since 980
	 */
	public static class StacktraceWithCount implements Comparable<StacktraceWithCount> {
		private int count;
		private StackTrace trace;

		/**
		 * Class constructor.
		 * 
		 * @param count The number of sampled invocations this stack trace represents.
		 * @param trace The stack trace to represent.
		 */
		private StacktraceWithCount(int count, StackTrace trace) {
			super();
			this.count = count;
			this.trace = trace;
		}

		/**
		 * Gets the number of invocations this stack trace represents.
		 * 
		 * @return The number of invocations this stack trace represents.
		 */
		public int getCount() {
			return count;
		}

		/**
		 * Gets the underlying stack trace.
		 * 
		 * @return The underlying stack trace.
		 */
		public StackTraceElement[] getTrace() {
			return trace.getTrace();
		}

		/**
		 * Compares this counted stack trace to another.
		 * 
		 * @return the value <code>0</code> if the count of this <code>StacktraceWithCount</code> is equal to the
		 *         count of the argument <code>StacktraceWithCount</code>; a value greater than 0 if the count of
		 *         this <code>StacktraceWithCount</code> is numerically less than the count of the argument
		 *         <code>StacktraceWithCount</code>; and a value greater than 0 if count of this
		 *         <code>StacktraceWithCount</code> is numerically greater than the count of the argument
		 *         <code>StacktraceWithCount</code> (signed comparison).
		 */
		@Override
		public int compareTo(StacktraceWithCount o) {
			return -Integer.valueOf(count).compareTo(Integer.valueOf(o.count));
		}

		/**
		 * Turns this counted stack trace into a string.
		 * 
		 * @return This counted stack trace as a string.
		 */
		@Override
		public String toString() {
			return count + " Sampled Invocations\n" + trace.toString();
		}

		/**
		 * Gets the percentage of the sampled invocations to the <code>total</code> total invocations.
		 * 
		 * @param total The number of total invocations.
		 * @return The percent that sampled invocations represents of the total.
		 */
		private double getPercentage(int total) {
			return Math.round((((double) count) / total) * 10000.0) / 100.0;
		}

		/**
		 * Turns this counted stack trace into a detailed string.
		 * 
		 * @param totalInvoations The total number of invocations to compare to.
		 * @param traceLength The length of the desired stack trace.
		 * @return This counted stack trace as a string.
		 */
		public String toString(int totalInvoations, int traceLength) {
			return count + "/" + totalInvoations + " Sampled Invocations (" + getPercentage(totalInvoations) + "%) " +
				trace.toString(traceLength);
		}
	}

	/**
	 * Represents a collection of sampled stack traces.
	 * 
	 * @author frzme
	 * @since Revision 980
	 * @version 1.0
	 */
	public static class SampledStacktraces {
		List<StacktraceWithCount> topConsumers;
		int totalInvocations;

		/**
		 * Class constructor.
		 * 
		 * @param topConsumers The top consumers of CPU.
		 * @param totalInvocations The total menthod invocations.
		 */
		public SampledStacktraces(List<StacktraceWithCount> topConsumers, int totalInvocations) {
			super();
			this.topConsumers = topConsumers;
			this.totalInvocations = totalInvocations;
		}

		/**
		 * Gets the list of top CPU consumers.
		 * 
		 * @return The list of top CPU consumers.
		 */
		public List<StacktraceWithCount> getTopConsumers() {
			return topConsumers;
		}

		/**
		 * Gets the number of total method invocations this collection represents.
		 * 
		 * @return The number of total method invocations this collection represents.
		 */
		public int getTotalInvocations() {
			return totalInvocations;
		}

		/**
		 * Turns this collection into a string.
		 * 
		 * @return This stack trace collection as a string.
		 */
		@Override
		public String toString() {
			return toString(0);
		}

		/**
		 * Turns this collection into a string.
		 * 
		 * @param minInvocation The minimum invocations needed to be displayed.
		 * @return This stack trace collection as a string.
		 */
		public String toString(int minInvocation) {
			StringBuilder ret = new StringBuilder();
			for (StacktraceWithCount swc : topConsumers) {
				if (swc.getCount() >= minInvocation) {
					ret.append(swc.toString(totalInvocations, Integer.MAX_VALUE));
					ret.append("\n");
				}
			}
			return ret.toString();
		}
	}
}
