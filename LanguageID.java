
/**
 * LING473 UW 2017, Project5
 * 
 * This program predicts the language that short samples of text are written in by summing the
 * log probs for each word in the sentence based on a count of the occurrence of the most words in a language. 
 * Smoothing algorithm assigns a sighting of 1 occurrence in a word count dictionary to each unknown word 
 * encountered in a sample when evaluating languages whose most frequent word is over a 'low frequency' threshold.
 * Otherwise, the smoothing algorithm splits a total of one occurrence across all unknown word counts.
 * 
 * Input files are expected to be in Latin 1 encoding.
 * 
 * @author rachellowy
 * 
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Scanner;

public class Project5 {

	// length of name in a file
	private static final int NAME_LENGTH = 3;
	// threshold for a low frequency language
	private static final int LOW_THRESHOLD = 10000;

	public static void main(String[] args) throws IOException {

		// directory with lang models
		File[] langDir = new File(args[0]).listFiles();
		Map<String, Dictionary> corpus = new HashMap<String, Dictionary>();

		// stores words and counts for common words in a Dictionary
		for (int lang = 0; lang < langDir.length; lang++) {
			Dictionary dict = new Dictionary(langDir[lang].getName().substring(0, NAME_LENGTH));
			dict.setWordCounts(langDir[lang], LOW_THRESHOLD);
			corpus.put(dict.getName(), dict);
		}

		// read in input file
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(args[1]), "ISO-8859-1"));
		String thisLine = br.readLine();

		while (thisLine != null) {
			String tag = getTag(thisLine);
			String text = cleanText(thisLine);

			System.out.println(tag + "\t" + text);

			// finds highest percent of
			findLangMatch(text, corpus);
			thisLine = br.readLine();
		}

		br.close();

	}

	/**
	 * Identifies the language in a given corpus with the highest percentage
	 * match with a text sample using a naive Bayes algorithm
	 * 
	 * @param sample
	 *            text sample to ID the language of
	 * @param corpus
	 *            corpus of words counts for common words in a language
	 */
	public static void findLangMatch(String sample, Map<String, Dictionary> corpus) {

		// calculate likelihood of each word given a language
		String bestLang = "none";
		double bestMatch = -999;

		for (String language : corpus.keySet()) {
			List<Double> matchByWord = getWordLogProbs(sample, corpus.get(language));

			Double langProb = 0.0;
			for (Double wordProb : matchByWord) {
				langProb += wordProb;
			}

			System.out.println(language + "\t" + langProb);

			// re-assign best match values
			if (langProb > bestMatch) {
				bestMatch = langProb;
				bestLang = language;
			}
		}
		System.out.println("result" + "\t" + bestLang);
	}

	// **************************//
	// ***** Log Probs **********//
	// **************************//

	/**
	 * Calculates log probability for each word in a sample
	 * 
	 * @param sample
	 *            sample to evaluate
	 * @param dict
	 *            Dictionary holding word counts for a language
	 * @return list of log probs for each word in a corpus
	 */
	public static List<Double> getWordLogProbs(String sample, Dictionary dict) {
		Scanner s = new Scanner(sample);
		List<Double> sampleWordCounts = new ArrayList<Double>();
		Map<String, Double> dictWordCounts = dict.getWordCounts();
		double numUnk = 0;

		String word;
		while (s.hasNext()) {
			word = s.next();

			// make a list of the count per word in corpus
			if (dictWordCounts.containsKey(word)) {
				sampleWordCounts.add(dictWordCounts.get(word));
			} else {
				// flag for unknown values
				sampleWordCounts.add(-999.0);
				numUnk += (1.0);
			}
		}

		s.close();
		smoothSample(sampleWordCounts, dict.getIsLowFreq(), numUnk);
		return getLogProbs(sampleWordCounts, dict.getIsLowFreq(), dict.getSize(), numUnk);

	}

	/**
	 * Calculates log probs for words. Languages whose highest frequency words
	 * are below LOW_THRESHOLD have a total of one added to their total word
	 * count. Others have the number of unknown words added to their totals.
	 * 
	 * @param wordCounts
	 *            a list of corpus word counts for each word that occurred in
	 *            the sample
	 * @param boolean
	 *            isLowFreq true if this language is low freq
	 * @param dictSize
	 *            total word count of words for this language
	 * @param numUnk
	 *            number of unknown words seen in this sample
	 * @return list of logProbsfor each word
	 */
	public static List<Double> getLogProbs(List<Double> wordCounts, boolean isLowFreq, double dictSize, Double numUnk) {
		List<Double> logProbs = new ArrayList<Double>();

		if (isLowFreq) {
			getLogProbs(logProbs, wordCounts, dictSize + 1);
		} else {
			getLogProbs(logProbs, wordCounts, dictSize + numUnk);
		}

		return logProbs;
	}

	/**
	 * Calculates log probs for a list of word counts
	 * 
	 * @param logProbs
	 *            List to store logProbs for each word
	 * @param wordCounts
	 *            list of word counts for each word occurring in the sample
	 * @param totalWords
	 *            total words in sample
	 * @precondition sample must be smoothed
	 */
	private static void getLogProbs(List<Double> logProbs, List<Double> wordCounts, Double totalWords) {
		// calculate probs
		for (Double wordCount : wordCounts) {
			double wordProb = wordCount / totalWords;
			double logProb = Math.log10(wordProb);
			logProbs.add(logProb);
		}

	}

	// **************************//
	// ********** SMOOTHING *****//
	// **************************//

	/**
	 * Runs algorithm to smooth a sample based on language frequency type.
	 * Languages are separated into low or high frequency to select algorithm.
	 * Low frequency samples are languages whose most frequent word occurs less
	 * often than the the LOW_THRESHOLD constant.
	 * 
	 * @param wordCounts
	 *            list of word counts in a language
	 * @param isLowFreqLang
	 *            true if this language is low frequency
	 * @param numUnk
	 *            number of unknown words detected in the sample
	 */
	public static void smoothSample(List<Double> wordCounts, boolean isLowFreqLang, Double numUnk) {
		if (isLowFreqLang) {
			smoothLowFreqLang(wordCounts.listIterator(), numUnk);
		} else {
			smoothHighFreqLang(wordCounts.listIterator());
		}

	}

	/**
	 * Smoothing algorithm for languages whose highest frequency words occur
	 * more than the LOW_THRESHOLD. Sets value of each unknown word to one.
	 * Unknown words are flagged with -999 value.
	 * 
	 * @param wordCounts
	 *            Iterator for list of word counts for each word a sample
	 */
	private static void smoothHighFreqLang(ListIterator<Double> wordCounts) {
		while (wordCounts.hasNext()) {
			double value = wordCounts.next();
			if (value < 0) {
				wordCounts.set(1.0);
			}
		}

	}

	/**
	 * Smoothing algorithm for languages whose highest frequency words occur
	 * more than the LOW_THRESHOLD. Distributes total frequency of one across
	 * all unknown words. Distribution avoids over-allocating probability to
	 * unknown words.
	 * 
	 * Unknown words are flagged with -999 value.
	 * 
	 * @param wordCounts
	 *            list of word counts for each word a sample
	 * @param numUnk
	 *            number of unknown words.
	 */
	private static void smoothLowFreqLang(ListIterator<Double> wordCounts, Double numUnk) {
		while (wordCounts.hasNext()) {
			double value = wordCounts.next();
			if (value < 0) {
				wordCounts.set(1 / numUnk);
			}
		}
	}

	// **************************//
	// ***** TEXT METHODS *****//
	// **************************//

	/**
	 * Removes non-alphabet characters and ID tag from a text string formatted:
	 * tag <tab> text
	 * 
	 * @param text
	 *            text to clean
	 * @return cleaned text
	 */
	public static String cleanText(String text) {
		text = text.substring(text.indexOf("\t") + 1);
		text = text.replaceAll("[.,!¡¥$£¿;:()\"\'¹²³«»\\—\\–\\-\\]]", "");

		return text;
	}

	/**
	 * Gets an identifying tag from text formatted: tag <tab> text
	 * 
	 * @param text
	 *            text to get tag from
	 * @return tag from text
	 */
	public static String getTag(String text) {
		String tag = text.substring(0, text.indexOf("\t"));
		return tag;
	}

	/**
	 * Prints all of the words in each language dictionary to a specified file
	 * 
	 * @param corpus
	 *            collection of all words each dictionary
	 * @param fileName
	 *            file to print to
	 * @throws FileNotFoundException
	 */
	public static void printCorpus(Map<String, Map<String, Integer>> corpus, String fileName)
			throws FileNotFoundException {
		PrintStream ps = new PrintStream(new File(fileName));
		for (String language : corpus.keySet()) {
			ps.append(corpus.get(language).toString());
		}
	}
}
