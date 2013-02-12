package com.sample.word;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree;
import com.googlecode.concurrenttrees.radix.RadixTree;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharSequenceNodeFactory;
import com.googlecode.concurrenttrees.radix.node.concrete.voidvalue.VoidValue;

/**
 * @author praveer
 * @license GPL - http://www.gnu.org/licenses/gpl.html
 *
 */
public class LongestConcatWord {
	Set<String> wordHashSet; // Hash set of set of all words
	SortedSet<String> sortedWordSet; // descending order starting with longest
										// word
	RadixTree<VoidValue> wordRadixTree; // Radix tree representation of words
	Map<String, WordProperty> subWordLookup;
	int smallestWordLength; // To store smallest word length

	/**
	 * Constructor
	 */
	public LongestConcatWord() {
		this.sortedWordSet = new TreeSet<String>(new Comparator<String>() {
			public int compare(String str1, String str2) {
				if (str1.length() == str2.length()) {
					return 1;
				} else {
					return (str2.length() - str1.length());
				}
			}
		});
		this.smallestWordLength = Integer.MAX_VALUE;
	}

	/**
	 * Reads given file with list of words and constructs required data-structures
	 * 
	 * @param fileName
	 */
	public void readWordListFile(String fileName) {
		try {
			FileInputStream fStream = new FileInputStream(fileName);
			DataInputStream dataInputStream = new DataInputStream(fStream);
			BufferedReader bReader = new BufferedReader(new InputStreamReader(
					dataInputStream));
			String word;
			while ((word = bReader.readLine()) != null) { // Each word is in
															// single line
				if (word.length() > 0) {
					sortedWordSet.add(word);
					if (word.length() < smallestWordLength) {
						smallestWordLength = word.length();
					}
				}
			}
			bReader.close();
			// Construct Hash Set of words
			wordHashSet = new HashSet<String>(sortedWordSet.size());
			for (String wordEntry : sortedWordSet) {
				wordHashSet.add(wordEntry);
			}
			// Construct Radix Tree
			wordRadixTree = new ConcurrentRadixTree<VoidValue>(
					new DefaultCharSequenceNodeFactory());
			for (String wordEntry : sortedWordSet) {
				wordRadixTree.put(wordEntry, VoidValue.SINGLETON);
			}
			// Lets create subWordLookup with initial capacity as half the size
			// of original list for full list of long words
			// this.subWordLookup = new HashMap<String,WordProperty>(sortedWordSet.size()/2);
			// Not required for finding just the longest word
			this.subWordLookup = new HashMap<String, WordProperty>();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Given an input word find number of sub-words in it.
	 *  Returns 0 if no such sub-words are found
	 * 
	 * @param word
	 * @return
	 */
	public int getNumberOfSubWords(String word) {
		String prefixSubWord, remainWord;
		Set<CharSequence> prefixSubWordSet;
		int subWordCount = 0, remainWordCount = 0;
		WordProperty wordProperty = null;
		// System.out.println("Input word: " + word);
		if (subWordLookup.containsKey(word)) {
			// If the word is already present in the sub-word lookup table
			// return the subword count
			return subWordLookup.get(word).getSubWordCount();
		}
		prefixSubWord = word.substring(0, this.smallestWordLength);
		prefixSubWordSet = this.wordRadixTree
				.getKeysStartingWith(prefixSubWord);
		if (!prefixSubWordSet.isEmpty()) {
			for (CharSequence subWord : prefixSubWordSet) {
				if (subWord.length() <= word.length()
						&& subWord.equals(word.substring(0,
								subWord.length()))) {
					// Add current subWord
					subWordCount = subWordCount + 1;
					if (subWord.length() == word.length()) {
						// System.out.println("End processing for word: " +
						// word);
						return 0;
					}
					// Find further subwords
					// System.out.println("SubWord: " + subWord +
					// " Length: " + subWord.length());
					remainWord = word.substring(subWord.length(),
							word.length());
					// System.out.println("Rest of the Word: " +
					// remainWord);
					remainWordCount = this.getNumberOfSubWords(remainWord);
					subWordCount = subWordCount + remainWordCount;
					// System.out.println("SubWordCount: " + subWordCount);
					if (remainWordCount == 0) {
						if (this.wordHashSet.contains(remainWord)) {
							subWordCount = subWordCount + 1;
							// Update sub-word lookup table
							wordProperty = new WordProperty();
							wordProperty.setWord(word);
							wordProperty.setSubWordCount(subWordCount);
							List<String> subWordList = new ArrayList<String>();
							subWordList.add(subWord.toString());
							subWordList.add(remainWord);
							wordProperty.setSubWordList(subWordList);
							this.subWordLookup.put(word, wordProperty);
							return subWordCount;
						} else {
							subWordCount = 0;
						}
					} else if (remainWordCount > 0) {
						// Update sub-word lookup table
						wordProperty = new WordProperty();
						wordProperty.setWord(word);
						wordProperty.setSubWordCount(subWordCount);
						List<String> subWordList = new ArrayList<String>();
						subWordList.add(subWord.toString());
						subWordList.addAll(this.subWordLookup.get(
								remainWord).getSubWordList());
						wordProperty.setSubWordList(subWordList);
						this.subWordLookup.put(word, wordProperty);
						return subWordCount;
					}
				}
			}
		}
		return 0;
	}

	/**
	 * This method returns list of WordProperty objects in the descending order
	 * of word length starting with the longest word.
	 * 
	 *  For answering the given problem only the longest word is constructed.
	 * 
	 * @return
	 */
	public List<WordProperty> getLongestConcatWordList() {
		List<WordProperty> wordPropertyList = new ArrayList<WordProperty>();
		WordProperty wordProperty = null;
		int subWordCount;
		for (String word : this.sortedWordSet) {
			subWordCount = this.getNumberOfSubWords(word);
			if (subWordCount > 1) {
				wordProperty = this.subWordLookup.get(word);
				System.out.println("Word: " + word + "\tSub-Word Count: "
						+ subWordCount + "\tSub-Word List: "
						+ wordProperty.getSubWordList());
				// Found Longest word
				wordPropertyList.add(wordProperty);
				break;
			}
		}
		return wordPropertyList;
	}

	public static void main(String[] args) {
		long lStartTime = new Date().getTime(); // start time
		LongestConcatWord longestConcatWord = new LongestConcatWord();
		longestConcatWord.readWordListFile("input.txt");
		System.out
				.println("Longest word consisting of other words from the list: ");
		longestConcatWord.getLongestConcatWordList();
		long lEndTime = new Date().getTime(); // end time
		long difference = lEndTime - lStartTime; // check difference
		System.out.println("Time (in ms) taken for above: " + difference);
	}

}
