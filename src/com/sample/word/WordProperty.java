/**
 * 
 */
package com.sample.word;

import java.util.List;

/**
 * @author praveer
 * @license GPL - http://www.gnu.org/licenses/gpl.html
 *
 */
public class WordProperty {
	private String word;
	private int subWordCount;
	private List<String> subWordList;
	
	public int getSubWordCount() {
		return subWordCount;
	}
	public void setSubWordCount(int subWordCount) {
		this.subWordCount = subWordCount;
	}
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public List<String> getSubWordList() {
		return subWordList;
	}
	public void setSubWordList(List<String> subWordList) {
		this.subWordList = subWordList;
	}

}
