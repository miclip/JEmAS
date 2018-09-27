package emotionAnalyzer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import com.google.common.collect.BiMap;


public class Vocabulary {
	public Vocabulary(int size, BiMap<String, Integer> indexMap,
			List<String> vocabulary) {
		super();
		this.size = size;
		this.indexMap = indexMap;
		this.vocabulary = vocabulary;
	}
	final int size;
	/**
	 * Maps a element of the vocabulary to the index of the corresponding component of the document term vector. And the other way round (uniqueness in both directions).
	 */
	final private BiMap<String, Integer> indexMap;
	final List<String> vocabulary;
	
	public int getIndexByString(String str){
		return this.indexMap.get(str);
	}
	
	public String getStringByIndex(int index){
		return this.indexMap.inverse().get(index);
	}
	
	public String[] asArray(){
		return vocabulary.toArray(new String[0]);
	}
}