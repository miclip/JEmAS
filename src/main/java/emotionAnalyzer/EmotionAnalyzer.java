package emotionAnalyzer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

//import porterStemmer.PorterStemmerWrapper;
import stanford_lemmatizer.StanfordLemmatizer;

public class EmotionAnalyzer {


	private EmotionLexicon lexicon;
	final private StanfordLemmatizer lemmatizer;
	final private StopwordFilter stopwordfilter;
	final private NonAlphabeticFilter nonAlphabeticFilter;
	final private NumberFilter numberFilter;
//	final private PorterStemmerWrapper stemmer;
	
	/**
	 * Will only be assingned if a passed DocumentContainer requires stemming as preprocessing.
	 */
	private EmotionLexicon stemmedLexicon;
	
	/**
	 * Collection of files which should be processed
	 */
	private File[] corpus;
	
	private Vocabulary vocabulary;
	private Settings settings;
	public Vocabulary getVocabulary() {
		return vocabulary;
	}

	int[] documentTermVectors;
	private MemoryContainer[] containersMemory;	
	private File documentTermVectorFolder;

	private int[] vocabularyLexiconVector; // used for lexiconProjection. Components are 0 if the word
											// represented by this index is not in the lexicon
	
	private double[][] vocabularyEmotionMatrix; //represents the emotion values of the words in
												// the lexicon. A row is (0	0 0) if the word is not in the lexicon.
	

	
		
	
	/**
	 * Constructor for EmotionAnalyzer loads EmotionLexicon (for recycling purposes) and the compounts File2TokenReader and Token2Vectorizer.
	 * @param givenLexiconPath
	 * @throws IOException
	 */
	public EmotionAnalyzer(Settings givenSettings,String givenLexiconPath) throws IOException{
		this.lemmatizer = new StanfordLemmatizer();
//		this.stemmer = new PorterStemmerWrapper();
		this.nonAlphabeticFilter = new NonAlphabeticFilter();
		this.stopwordfilter = new StopwordFilter(Util.STOPWORDLIST); 
		this.numberFilter = new NumberFilter();
		this.settings = givenSettings;
		this.lexicon = new EmotionLexicon(givenLexiconPath, this.lemmatizer, this.settings);
	}
		
	public MemoryContainer[] analyze(InputStream givenCorpus)
	throws Exception{
		this.vocabulary = null;	
		this.documentTermVectors = null;

		List<String> lines = Util.readStream2List(givenCorpus);
		List<String> normalizedText = new ArrayList<String>();

		this.containersMemory = new MemoryContainer[lines.size()];
			for (int index = 0; index < lines.size(); index++) {
				this.containersMemory[index] = new MemoryContainer(
						lines.get(index), this.documentTermVectors);
			}

			for (MemoryContainer cont : containersMemory) {	
				//wenn man Lemmatisierung als Vorverarbeitung ausgewählt hat.
				if (this.settings.usedPreprocessing.equals(Preprocessing.LEMMATIZE)) {
					normalizedText = lemmatizer.lemmatize(cont.document);
				}
				//wenn man KEINE Vorverarbeitung ausgewählt hat (ie, Lexikon und Dokumente sind schon lemmatisiert)
				else if (this.settings.usedPreprocessing.equals(Preprocessing.NONE)){
					normalizedText.add(cont.document);
				}
			
			//measure token count
			cont.tokenCount = normalizedText.size();
			//Zahlen entfernen und Anzahl berechnen.
			normalizedText = numberFilter.filter(normalizedText);
			cont.numberCount = cont.tokenCount - normalizedText.size();
			normalizedText = nonAlphabeticFilter.filter(normalizedText);
			cont.alphabeticTokenCount = normalizedText.size();
			//no case-folding/stopword removal wenn keine Vorverarbeitung gewähtl wurde.
			if (this.settings.usedPreprocessing.equals(Preprocessing.LEMMATIZE)){
				normalizedText = stopwordfilter.filter(normalizedText);
				}
			
			cont.non_stopword_tokenCount = normalizedText.size();
			cont.normalizedDocument = normalizedText;

		}

		System.err.println("Building vocabulary...");
		this.vocabulary = collectVocabularyMemory();
		
		//calculate Vocabulary-Emotion-Matrix and vocabulary-lexicon-vector
		this.vocabularyEmotionMatrix = new double[this.vocabulary.size][3];
		this.vocabularyLexiconVector = new int[this.vocabulary.size];
		for (int i=0; i< this.vocabulary.size; i++){
			EmotionVector currentEmotionVector = this.lexicon.lookUp(this.vocabulary.getStringByIndex(i));
			if (currentEmotionVector!=null){
				this.vocabularyEmotionMatrix[i][0] = currentEmotionVector.getValence();
				this.vocabularyEmotionMatrix[i][1] = currentEmotionVector.getArousal();
				this.vocabularyEmotionMatrix[i][2] = currentEmotionVector.getDominance();
				this.vocabularyLexiconVector[i] = 1;
			}
			else{
				this.vocabularyEmotionMatrix[i][0] = 0;
				this.vocabularyEmotionMatrix[i][1] = 0;
				this.vocabularyEmotionMatrix[i][2] = 0;
				this.vocabularyLexiconVector[i] = 0;
			}
		}

		/**
		 * Dependend on the weight function used:
		 */
		//termFrequency weights...
		if (this.settings.weightFunction.equals("absolute")){
			//Dokument-Term-Vektoren erheheben, dictionary look-ups durchführen, Emotionsvektoren berechnen.
			//für jedes Dokument dictionnnary - look-up durchführen und Emotionsvektoren berechnen 
			//(D-T-Vektor in Liste von Wortemotionsvektoren umwandeln (diese NICHT speichern!), 
			//mittelwert berechnen (ist gleichzeitig Dokumentenemotionsvektor), Standardabweichung berechnen, 
			//diese Kennwerte festhalten.
			System.err.println("Calculating Document-Term-Vectors, Performing "
					+ "dictionary look-ups and calculating document emotion...");
			for (MemoryContainer container: containersMemory){
				int[] documentTermVector = calculateDocumentTermVector(container);
				container.recognizedTokenCount = Util.sumOverVector(lexikonProjection(documentTermVector));
				calculateEmotionVector(container, documentTermVector);
			}
		}
		//tfidf weights...
		else if (this.settings.weightFunction.equals("tfidf")){
			System.err.println("Calculating Document-Term-Vectors, Performing "
					+ "dictionary look-ups and calculating document emotion...");
			this.documentTermVectorFolder.mkdir();
			//calculate and save document term vectors
			for (MemoryContainer container: containersMemory){
				int[] documentTermVector = calculateDocumentTermVector(container);
				container.documentTermVectors = documentTermVector;
				container.recognizedTokenCount = Util.sumOverVector(lexikonProjection(documentTermVector));
			}
			//calculate document frequencies
			int[] documentFrequencies = this.calculateDocumentFrequencies();
			//calculate tf-idf measures and emotion vectors
			for (MemoryContainer container: containersMemory){				
				float[] tfidfVector = calculateTfidf(container, container.documentTermVectors,
						documentFrequencies);
				calculateEmotionVector(container, tfidfVector);
			}

		}
		else
			throw new Exception("Illegal weight function chosen!");
			
		//Rückgabe
		return containersMemory;
	}

	
	/**
	 * Calculates the number of documents each word of the vocabulary is in.
	 * @return
	 * @throws IOException
	 */
	private int[] calculateDocumentFrequencies() throws IOException{
		int[] docuementFrequencies = new int[this.vocabulary.size];
		//for every document term vector
		for (MemoryContainer container: this.containersMemory){
			int[] termFrequencies = container.documentTermVectors;
			//for every entry
			for (int i = 0; i < this.vocabulary.size; i++){
				//if entry is greater than 0 than increment the corresponding entry of the
				//document frequencies
				if (termFrequencies[i] > 0)
					docuementFrequencies[i]++;
			}		
		}
		return docuementFrequencies;
	}

	private float[] calculateTfidf(MemoryContainer container, int[] documentTermVector,
			int[] documentFrequencies ){
		float[] tfidfWeights = new float[this.vocabulary.size];
		for (int i = 0; i < this.vocabulary.size; i++)
			tfidfWeights[i] = (float) Util.tfidf(documentTermVector[i], this.corpus.length, documentFrequencies[i]);
		return tfidfWeights;
	}
	
	private void calculateEmotionVector(MemoryContainer container, int[] documentTermVector) throws IOException {
		float[] fArray = new float[documentTermVector.length];
		for (int i = 0; i < documentTermVector.length; i++)
			fArray[i] = (float) documentTermVector[i];
		calculateEmotionVector(container, fArray);
	}
	
	private void calculateEmotionVector(MemoryContainer container, float[] weightVector) throws IOException {
		float[] projectedWeightVector = lexiconProjection(weightVector);
		double valence = 0;
		double arousal = 0;
		double dominance = 0;
		double sumOfWeights = 0;
		for (int i = 0; i < this.vocabulary.size; i++){
			valence += projectedWeightVector[i]*this.vocabularyEmotionMatrix[i][0];
			arousal += projectedWeightVector[i]*this.vocabularyEmotionMatrix[i][1];
			dominance += projectedWeightVector[i]*this.vocabularyEmotionMatrix[i][2];
			sumOfWeights +=  projectedWeightVector[i];
		}
//		container.recognizedTokenCount = sumOfWeights; // Das musste ich hier leider rausnehmen, weil 
														// das bei tfidf keinen sinn mehr gemacht hätte,
														// stattdessen wird das jetzt woanders berechnet...
		// if the number of recognized tokens is 0, normalisation is not possivle
		if (sumOfWeights==0){
			container.documentEmotionVector = new EmotionVector(0,0,0);
			container.standardDeviationVector = new EmotionVector(0,0,0);
		}
		else{
		EmotionVector emotionVector = new EmotionVector(valence, arousal, dominance);
		emotionVector.normalize(sumOfWeights);
		container.documentEmotionVector = emotionVector;
		
		//TODO Weiß nicht, ob das mit tfidf wirklich sinn macht...
		//calcualte standarddev vector
		//squared Deviation Valence
		double sqDevValence = 0;
		//squared Deviation Arousl
		double sqDevArousal = 0;
		//squared Deviation Dominance
		double sqDevDominance = 0;
		//calculate summed squared deviation from mean for VAD (which is the standard dev of all recognized emotion vectors)
		for (int i = 0; i < this.vocabulary.size; i++){
			if(vocabularyLexiconVector[i] == 1){
				sqDevValence += projectedWeightVector[i] * Math.pow((emotionVector.getValence() - vocabularyEmotionMatrix[i][0]), 2);
				sqDevArousal += projectedWeightVector[i] * Math.pow((emotionVector.getArousal() - vocabularyEmotionMatrix[i][1]), 2);
				sqDevDominance += projectedWeightVector[i] * Math.pow((emotionVector.getDominance() - vocabularyEmotionMatrix[i][2]), 2);
			}
		}
		EmotionVector sdVector = new EmotionVector(Math.sqrt(sqDevValence/sumOfWeights),Math.sqrt(sqDevArousal/sumOfWeights),Math.sqrt(sqDevDominance/sumOfWeights));
		container.standardDeviationVector = sdVector;
		}
	}
		
		

	/**
	 * Sets all compounents of the document-term-vector to 0, if they represent a word not in
	 * the lexicon.
	 * @param givenDocumentTermVector
	 * @return
	 */
	private float[] lexiconProjection(float[] givenDocumentTermVector){
		for (int i=0; i<givenDocumentTermVector.length; i++){
			givenDocumentTermVector[i] = givenDocumentTermVector[i]*(float)this.vocabularyLexiconVector[i];			
		}
		return givenDocumentTermVector;
	}
	
	private float[] lexikonProjection(int[] givenDoucemntTermVector){
		float[] fArray = new float[this.vocabulary.size];
		for (int i = 0; i < this.vocabulary.size; i++)
			fArray[i] = (float) givenDoucemntTermVector[i];
		return lexiconProjection(fArray);
	}

	private int[] calculateDocumentTermVector(MemoryContainer container) throws IOException {
		int index;
		int[] documentTermVector = new int[this.vocabulary.size];
		List<String> normalizedDocument = container.normalizedDocument;
		for (String str: normalizedDocument){
			index = this.vocabulary.getIndexByString(str);
			documentTermVector[index]++;
		}
		return documentTermVector;
	}

	void showLexicon(){
		this.lexicon.printLexicon();
	}
	
	void showStemmedLexicon(){
		this.stemmedLexicon.printLexicon();
	}
	
	private Vocabulary collectVocabularyMemory() throws IOException{
		
		Set<String> vocabularySet= new HashSet<String>();
		//for every normalized document
		for (MemoryContainer container: this.containersMemory){			
			List<String> normalizedDocument = container.normalizedDocument;
			//for every word in normalized document
			for (String line : normalizedDocument){
				vocabularySet.add(line);
			}
		}
		int vocabularySize =  0;
		BiMap<String, Integer> indexMap = HashBiMap.create();
		List<String> vocabularyList = new ArrayList<String>();
		for (String str: vocabularySet){
			indexMap.put(str, vocabularySize);
			vocabularyList.add(str);
			vocabularySize++;	
		}
		Vocabulary voc = new Vocabulary(vocabularySize, indexMap, vocabularyList);
		return voc;
	}

}
