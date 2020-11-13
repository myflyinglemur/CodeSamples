import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Ling 473, UW Summer 2017, Project 2
 *
 * This program takes a directory of NYT articles, removes the formatting tags from them,
 * strips away text that is not part of a word, and outputs the number of occurrences of 
 * each word across all articles (ranked by descending usage order).
 *
 * @author rachellowy
 * with credit to Javin Paul from javarevisited.blogspot.com for sort-by-value map tutorial
 * and code 
 *
 */

public class Project2 {

	public static Map<String, Integer> WORD_COUNTS = new HashMap<String, Integer>();

	public static void main(String[] args) throws IOException {

		String dirName = "/corpora/LDC/LDC02T31/nyt/2000";
		File[] dir = new File(dirName).listFiles();

		for (int i = 0; i < dir.length; i++) {

			FileReader fr = new FileReader(dir[i]);
			BufferedReader br = new BufferedReader(fr);

			String thisLine = br.readLine();
			StringBuilder text = new StringBuilder();

			while (thisLine != null) {
				text.append(thisLine + " "); // space keeps newline word bounds
				thisLine = br.readLine();
			}

			// remove format tags & non-alpha characters
			String cleanText = cleanText(text.toString());

			// generates list of words
			String[] allWords = cleanText.split("\\s+");

			// counts word occurences
			getWordCounts(allWords);

		}

		WORD_COUNTS = sortByValues(WORD_COUNTS);

		printCounts();

	}

	/**
	 * Prints tallies for each word
	 */
	public static void printCounts() {
		for (String word : WORD_COUNTS.keySet()) {
			System.out.println(word + "\t" + WORD_COUNTS.get(word));
		}

	}

	/**
	 * Strips text of formatting codes and non-alpha, non-apostrophe characters
	 * 
	 * @param text
	 *            full text to be stripped
	 * @return
	 */
	public static String cleanText(String text) {

		text = text.replaceAll("<.*?>", " ");
		text = text.replaceAll("[^A-Za-z']", " ");
		text = text.replaceAll("[^A-Za-z]'|'(?![A-Za-z]')", " ");

		return text.toLowerCase();
	}

	/**
	 *
	 * Sorts maps by values.
	 *
	 * @author Javin Paul minor formatting edits by @rachellowy, but code is
	 *         primarily Javin Paul's
	 * 
	 *         Source accessed 08/2017:
	 *         http://javarevisited.blogspot.com/2012/12/how-to-sort-hashmap-java-by-key-and-value.html#ixzz4pkXS7JXm
	 * 
	 */

	public static Map<String, Integer> sortByValues(Map<String, Integer> wordCounts) {

		// list of keys and associated values
		List<Entry<String, Integer>> entries = new LinkedList<Map.Entry<String, Integer>>(wordCounts.entrySet());

		// implements a comparator sorting entries by value
		Collections.sort(entries, new Comparator<Map.Entry<String, Integer>>() {

			@Override
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}

		});

		// LinkedHashMap keeps entries in insertion order
		// Values are currently in value order, so insertion is value order
		Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();

		for (Map.Entry<String, Integer> entry : entries) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}

	/**
	 * Records number of occurrences of each unique word in list.
	 * 
	 * @param words
	 *            array of all occurrences of words to count
	 */
	public static void getWordCounts(String[] words) {

		for (int i = 0; i < words.length; i++) {
			String thisWord = words[i].toLowerCase();

			// skips empty strings
			if (!(thisWord.contains(" ") || thisWord.isEmpty())) {
				if (WORD_COUNTS.containsKey(thisWord)) {
					WORD_COUNTS.put(thisWord, WORD_COUNTS.get(thisWord) + 1);
				} else {
					WORD_COUNTS.put(thisWord, 1);
				}
			}
		}
	}

}
