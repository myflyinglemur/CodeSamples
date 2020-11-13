import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Ling 473, Summer 2017: Project 1
 * 
 * Reviews text files tagged using Pennbank and reports amount of different phrase types (sentence,
 * noun phrase, verb phrase, ditransitive verb phrase, and intransitive verb phrase). Ditransitive and
 * intransitive verb phrases are also counted in the overall 'verb phrase' count. Noun phrases inside
 * of ditransitive verb phrases are counted again in the overall 'noun phrase' count.
 * 
 * 
 * @author rachellowy
 *
 */

public class Project1 {

	// Cell storage values
	private static final int PHRASE_TYPES = 5;
	private static final int S = 0;
	private static final int NP = 1;
	private static final int VP = 2;
	private static final int DVP = 3;
	private static final int IVP = 4;

	// Phrase search patterns
	private static final Pattern SENTENCE = Pattern.compile("\\(S .*");
	private static final Pattern NOUNP = Pattern.compile("\\(NP .*\\)");
	private static final Pattern INVP = Pattern.compile("\\(VP .*\\)");
	private static final Pattern VERBP = Pattern.compile("\\(VP .*");
	
	// count holder
	private static int[] COUNTER = new int[PHRASE_TYPES];

	public static void main(String[] args) throws IOException {
		
		// directory information
		String dirName = "/corpora/LDC/LDC99T42/RAW/parsed/prd/wsj/14";
		File[] dir = new File(dirName).listFiles();
		
		// search all files in directory
		for (int i = 0; i < dir.length; i++) {

			FileReader fr = new FileReader(dir[i]);
			BufferedReader br = new BufferedReader(fr);

			String thisLine = br.readLine();

			//finds matches per phrase type 
			while (thisLine != null) {
				findPhrase(SENTENCE.matcher(thisLine), S);
				findPhrase(NOUNP.matcher(thisLine), NP);

				//finds and increments verb types
				if (INVP.matcher(thisLine).find()){
					COUNTER[IVP] ++;
					COUNTER[VP] ++;
				} else if (VERBP.matcher(thisLine).find()) {
					if (isDVP(br)) {
						COUNTER[DVP]++;
					}

					COUNTER[VP]++;
				}
				thisLine = br.readLine();
			}
			
		}
		
		//reports values
		printCounts();
	}

	/**
	 * Finds and counts an instance of a phrase corresponding to regex in the matcher
	 * 
	 * @param m matcher for this phrase type
	 * @param type cell number to increment if phrase is found
	 */
	public static void findPhrase(Matcher m, int type) {
		if (m.find()) {
			COUNTER[type]++;
		}
	}
	
	/**
	 * Explores whether a verb phrase is a ditransitive verb phrase.
	 * Ditranstive verb is one verb phrase immediately followed by two noun phrases.
	 * 
	 * @param br buffered reader
	 * @return true if phrase is DVP, false otherwise
	 * @throws IOException if buffered reader is not configured properly
	 */
	public static boolean isDVP(BufferedReader br) throws IOException {
		boolean isDVP = true;
		int count = 0;
		String thisLine;

		//reads up to two lines following a verb phrase
		while (isDVP && count < 2) {
			if ((thisLine = br.readLine()) != null) {
				isDVP = isNP(thisLine);
			}
			count++;
		}

		return isDVP;
	}

	/**
	 * Helper method for DVP. Reports if text contains an NP.
	 * Increments values for S, NP, and VP phrases found.
	 * 
	 * @param thisLine text to search
	 * @param counter array holding values for each phrase type 
	 * @return true if noun phrase is fond, false otherwise
	 */
	private static boolean isNP(String thisLine) {
		boolean isNP = false;

		if (NOUNP.matcher(thisLine).find()) {
			COUNTER[NP] += 1;
			isNP = true;
		} else if (SENTENCE.matcher(thisLine).find()) {
			COUNTER[S] += 1;
			isNP = false;
		} else if (VERBP.matcher(thisLine).find()) {
			COUNTER[VP] += 1;
			isNP = false;
		}

		return isNP;
	}

	/**
	 * Prints number of matches for each phrase type
	 */
	public static void printCounts() {
		printLine("Sentences", COUNTER[S]);
		printLine("Noun Phrases", COUNTER[NP]);
		printLine("Verb Phrases", COUNTER[VP]);
		printLine("Ditranstive Verb Phrases", COUNTER[DVP]);
		printLine("Intranstive Verb Phrases", COUNTER[IVP]);
		System.out.println();
	}

	/**
	 * Helper method for printCounts
	 * 
	 * @param phrase phrase type descriptor (Sentence, Noun Phrase, etc)
	 * @param value number of phrases found
	 */
	private static void printLine(String phrase, int value) {
		System.out.println(phrase + ": " + value);
	}
}
