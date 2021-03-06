
package emotionAnalyzer;


import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;


/**
 * contains document path, settings for the processing and its results and
 * statistics
 * 
 * @author buechel
 * 
 */
public class MemoryContainer {

	final String document;
	public List<String> normalizedDocument;

	/**
	 * Number of "letter tokens" (Tokens which purely of letters and can
	 * therefore be regarded as "real words". This deviation may be important in
	 * this context to interprete the difference between token count and count
	 * of identified tokens during look-up because especially in annual reports,
	 * many tokens may be numbers.)
	 */

	int tokenCount;
	int alphabeticTokenCount;
	int numberCount;
	/**
	 * alphabetic non-stopwords
	 */
	int non_stopword_tokenCount;
	/**
	 * Number of word vectors which contribute to the document vector. Unlike in
	 * prior versions, only the words which can be found in the lexicon
	 * contribute to the vector count (unidentified words will be evaluated as
	 * null vecotor and not neutral vector anymore.)
	 */
	int recognizedTokenCount;
	int[] documentTermVectors;

	EmotionVector documentEmotionVector;
	EmotionVector standardDeviationVector;

	public EmotionVector getDocumentEmotionVector(){
		return documentEmotionVector;
	}

	public EmotionVector getStandardDeviationVector(){
		return standardDeviationVector;
	}

	public MemoryContainer(String givenDocument, int[] givenDocumentTermVectors ) {
		// initialize final fields for files of the document, the normalized
		// document and the document-term-vector
		this.document = givenDocument;
		this.non_stopword_tokenCount = -1; 
		this.tokenCount = -1;
		this.documentTermVectors = givenDocumentTermVectors;
	}

	/**
	 * Prints the results of the lemmatization. With the momentarily definition,
	 * the vector count is identical to the normalization paremter. The pieces
	 * of information printed include the file name, the the type of the report,
	 * the originating stock markte index, the enterprise, the year, the values
	 * of the vector itself, the lenght of the vector, the token count, the
	 * letter token count and the vector count (tokens mapped successfully to a
	 * lexicon entry).
	 */
	public void printData() {
		
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
		otherSymbols.setDecimalSeparator('.');
		otherSymbols.setGroupingSeparator(','); 
		DecimalFormat df = new DecimalFormat("#.#####", otherSymbols); // Anzahl der Dezimalstellen festlegen.
		System.out.println(df.format(this.documentEmotionVector.getValence()) + "\t"
				+ df.format(this.documentEmotionVector.getArousal()) + "\t"
				+ df.format(this.documentEmotionVector.getDominance()) + "\t"
				+ df.format(this.standardDeviationVector.getValence()) + "\t"
				+ df.format(this.standardDeviationVector.getArousal()) + "\t"
				+ df.format(this.standardDeviationVector.getDominance()) + "\t"
				+ df.format(this.tokenCount) + "\t"  
				+ df.format(this.alphabeticTokenCount) + "\t"
				+ df.format(this.non_stopword_tokenCount) + "\t"
				+ df.format(this.recognizedTokenCount)	+	"\t"
				+ df.format(this.numberCount));
	}

	public int getTokenCount() {
		return tokenCount;
	}

	public int getAlphabeticTokenCount() {
		return alphabeticTokenCount;
	}

	public int getNonStopwordTokenCount() {
		return non_stopword_tokenCount;
	}

	public int getNumberCount(){
		return numberCount;
	}

	public int getRecognizedTokenCount() {
		return recognizedTokenCount;
	}
}
