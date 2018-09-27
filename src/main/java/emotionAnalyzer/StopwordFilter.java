package emotionAnalyzer;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

public class StopwordFilter {

	public StopwordFilter(List<String> stopwordList) {
		super();
		this.stopwordList = stopwordList;
	}

	public StopwordFilter(String path) {
		super();
		List<String> lines = new ArrayList<String>();
		try {
			BufferedReader in = Util.resource2BufferedReader(path);
			String line = null;
			while ((line = in.readLine()) != null) {
				lines.add(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("\nFailed to find StopwordFilter in jar. Looked at path " + Util.getJarPath(path) );
		}
		this.stopwordList = lines;
	}

	final private List<String> stopwordList;

	/**
	 * Maps to lower-case (case-folding) and removes stopwords (stopword list only
	 * contains lowercase entries).
	 * 
	 * @param document
	 * @return
	 */
	public ArrayList<String> filter(List<String> document) {
		ArrayList<String> filteredList = new ArrayList<String>();
		for (String token : document) {
			token = token.toLowerCase();
			if (!stopwordList.contains(token))
				filteredList.add(token);
		}
		return filteredList;
	}

}
