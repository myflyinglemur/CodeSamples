
/**
 * Ling473, Project 3
 * 
 * Reads in a file of Thai text and inserts spaces at word breaks based on the following 
 * FSM: [V1]C1[C2][V2][T][V3][C3]
 * 
 * Skeleton from UW Ling 473 site.
 * 
 * @author rachellowy
 * 
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Project3 {

	public enum State {
		ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE
	};

	public static void main(String[] args) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader("../dropbox/17-18/473/project3/fsm-input.utf8.txt"));
		//BufferedReader reader = new BufferedReader(new FileReader("../input/fsm-input.utf8.txt"));

		String line;

		BufferedWriter bw = new BufferedWriter(new FileWriter("output.html"));

		bw.write("<html><meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />");

		while ((line = reader.readLine()) != null) {
			bw.write(FST.process(line));
			bw.write("<br/>");
		}

		bw.write("</body></html>");

		bw.close();
		reader.close();

	}

	/***********************************/
	/******** FSM Class ****************/
	/***********************************/

	/**
	 * Controls FSM for a subset of Thai FSM: [V1]C1[C2][V2][T][V3][C3]
	 * 
	 * @author rachellowy
	 *
	 */
	public static class FST {
		public static final String V1 = "\u0E40\u0E41\u0E42\u0E43\u0E44";
		public static final String C1 = "\u0e01\u0e02\u0e03\u0e04\u0e05\u0e06\u0e07\u0e08\u0e09\u0e0A\u0e0B\u0e0C\u0e0D\u0e0E\u0e0F\u0e10\u0e11\u0e12\u0e13\u0e14\u0e15\u0e16\u0e17\u0e18\u0e19\u0e1A\u0e1B\u0e1C\u0e1D\u0e1E\u0e1F\u0e20\u0e21\u0e22\u0e23\u0e24\u0e25\u0e26\u0e27\u0e28\u0e29\u0e2A\u0e2B\u0e2C\u0e2D\u0e2E";
		public static final String C2 = "\u0e19\u0e21\u0e23\u0e25\u0e27";
		public static final String V2 = "\u0e31\u0e34\u0e35\u0e36\u0e37\u0e38\u0e39\u0e47";
		public static final String T = "\u0e48\u0e49\u0e4A\u0e4B";
		public static final String V3 = "\u0e22\u0e27\u0e2D\u0e32";
		public static final String C3 = "\u0e01\u0e07\u0e14\u0e19\u0e1A\u0e21\u0e22\u0e27";

		/**
		 * Finds word boundaries and inserts spaces.
		 * 
		 * @param line
		 *            to process
		 * @return line with spaces between word
		 */
		public static String process(String line) {
			StringBuilder output = new StringBuilder();
			// String output = "";
			State state = State.ZERO;

			for (int i = 0; i < line.length(); i++) {
				String c = "" + line.charAt(i);

				switch (state) {
				case ZERO:
				case ONE:
					state = entryStateTrans(c, state);
					break;

				case TWO:
					state = transTWO(c, state);
					break;

				case THREE:
					state = transTHREE(c, state);
					break;

				case FOUR:
					state = transFOUR(c, state);
					break;

				case FIVE:
					state = transFIVE(c, state);
					break;

				case SIX:
					state = transSIX(c, state);
					break;

				default:
					break;

				}

				getOutput(state, output, c);

				if (isFinalState(state)) {
					state = finalStateTrans(state);
				}

			}

			return output.toString();

		}

		/**
		 * Checks whether FSM is in a final state
		 * 
		 * @param state
		 *            current machine state
		 * @return true if this state is a final state
		 */
		public static boolean isFinalState(State state) {
			boolean isFinal = false;

			if (state == State.SEVEN || state == State.EIGHT || state == State.NINE) {
				isFinal = true;
			}

			return isFinal;
		}

		/**
		 * Checks whether string contains a second string
		 * 
		 * @param c
		 *            string containing character to look for
		 * @return true if state contains 'c'
		 */
		static boolean contains(String state, String c) {
			return state.contains(c);
		}

		/**
		 * Generates output for each state
		 * 
		 * @param state
		 *            current state of FSM
		 * @param output
		 *            text to output
		 * @param c
		 *            character to add to output
		 */
		public static void getOutput(State state, StringBuilder output, String c) {
			if (state == State.SEVEN || state == State.EIGHT) {
				output.append(" " + c);
			} else if (state == State.NINE) {
				output.append(c + " ");
			} else {
				output.append(c);
			}
		}

		/***********************************/
		/******** State Transitions ********/
		/***********************************/

		/**
		 * Advances machine for states 0 and 1
		 * 
		 * @param s
		 *            current machine state
		 * @return new state
		 * @throws IllegalArgumentException
		 *             if string is ungrammatical
		 * @precondition string must be legal in current state
		 */
		public static State entryStateTrans(String s, State state) {

			if (contains(V1, s)) {
				state = State.ONE;
			} else if (contains(C1, s)) {
				state = State.TWO;
			} else {
				throw new IllegalArgumentException("Illegal grammar");
			}

			return state;
		}

		/**
		 * Advances machine for state 2
		 * 
		 * @param s
		 *            current machine state
		 * @return new state
		 * @throws IllegalArgumentException
		 *             if string is ungrammatical
		 * @precondition string must be legal in current state
		 */
		public static State finalStateTrans(State state) {
			if (state == State.SEVEN) {
				state = State.ONE;
			} else if (state == State.EIGHT) {
				state = State.TWO;
			} else if (state == State.NINE) {
				state = State.ZERO;
			} else {
				throw new IllegalArgumentException("Illegal grammar");
			}

			return state;
		}

		/**
		 * Advances machine for state 2
		 * 
		 * @param s
		 *            current machine state
		 * @return new state
		 * @throws IllegalArgumentException
		 *             if string is ungrammatical
		 * @precondition string must be legal in current state
		 */
		public static State transTWO(String s, State state) {
			if (contains(C2, s)) {
				state = State.THREE;
			} else if (contains(V2, s)) {
				state = State.FOUR;
			} else if (contains(T, s)) {
				state = State.FIVE;
			} else if (contains(V3, s)) {
				state = State.SIX;
			} else if (contains(C3, s)) {
				state = State.NINE;
			} else if (contains(V1, s)) {
				state = State.SEVEN;
			} else if (contains(C1, s)) {
				state = State.EIGHT;
			} else {
				throw new IllegalArgumentException("Illegal grammar");
			}

			return state;
		}

		/**
		 * Advances machine for state 3
		 * 
		 * @param s
		 *            current machine state
		 * @return new state
		 * @throws IllegalArgumentException
		 *             if string is ungrammatical
		 * @precondition string must be legal in current state
		 */
		public static State transTHREE(String s, State state) {
			if (contains(V2, s)) {
				state = State.FOUR;
			} else if (contains(T, s)) {
				state = State.FIVE;
			} else if (contains(V3, s)) {
				state = State.SIX;
			} else if (contains(C3, s)) {
				state = State.NINE;
			} else {
				throw new IllegalArgumentException("Illegal grammar");
			}

			return state;
		}

		/**
		 * Advances machine for state 4
		 * 
		 * @param s
		 *            current machine state
		 * @return new state
		 * @throws IllegalArgumentException
		 *             if string is ungrammatical
		 * @precondition string must be legal in current state
		 */
		public static State transFOUR(String s, State state) {
			if (contains(T, s)) {
				state = State.FIVE;
			} else if (contains(V3, s)) {
				state = State.SIX;
			} else if (contains(C3, s)) {
				state = State.NINE;
			} else if (contains(V1, s)) {
				state = State.SEVEN;
			} else if (contains(C1, s)) {
				state = State.EIGHT;
			} else {
				throw new IllegalArgumentException("Illegal grammar");
			}

			return state;
		}

		/**
		 * Advances machine for state 5
		 * 
		 * @param s
		 *            current machine state
		 * @return new state
		 * @throws IllegalArgumentException
		 *             if string is ungrammatical
		 * 
		 * @precondition string must be legal in current state
		 */
		public static State transFIVE(String s, State state) {
			if (contains(V3, s)) {
				state = State.SIX;
			} else if (contains(C3, s)) {
				state = State.NINE;
			} else if (contains(V1, s)) {
				state = State.SEVEN;
			} else if (contains(C1, s)) {
				state = State.EIGHT;
			} else {
				throw new IllegalArgumentException("Illegal grammar");
			}

			return state;
		}

		/**
		 * Advances machine for state 6
		 * 
		 * @param s
		 *            current machine state
		 * @return new state
		 * @throws IllegalArgumentException
		 *             if string is ungrammatical
		 * @precondition string must be legal in current state
		 */
		public static State transSIX(String s, State state) {
			if (contains(C3, s)) {
				state = State.NINE;
			} else if (contains(V1, s)) {
				state = State.SEVEN;
			} else if (contains(C1, s)) {
				state = State.EIGHT;
			} else {
				throw new IllegalArgumentException("Illegal grammar");
			}

			return state;
		}

	}
}
