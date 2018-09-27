package emotionAnalyzer;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;



import stanford_lemmatizer.StanfordLemmatizer;

public class Tests {
	final EmotionVector vectorAIDS = new EmotionVector(-3.67, 0.0, -1.45);
	final EmotionVector vectorCalm = new EmotionVector(1.89, -3.33, 2.44);
	final EmotionVector vectorLobotomy = new EmotionVector(-2.55, 0.32, -3.0);
	final EmotionVector vectorLovable = new EmotionVector(3.26, 0.41, 1.83);
	final EmotionVector testVector = new EmotionVector(4, 5, 6); // to test the
																	// calculated
																	// emotion
																	// vector of
																	// testFile.txt
																	// when
																	// using
																	// testLexicon.txt
	final EmotionVector testVectorNormalized = new EmotionVector(1.0,
			5.0 / 4.0, 6.0 / 4.0);
	final EmotionVector testVector2 = new EmotionVector(-8.43, -3.75, -7.04);
	final EmotionVector testVectorNormalized2 = new EmotionVector(-8.43 / 6.0,
			-3.75 / 6.0, -7.04 / 6.0); // this vector should be the normalized
										// (divided by number of found lexicon
										// entries) document vector of
										// testFile2.txt using defaultLexicon.
	final StanfordLemmatizer lemmatizer = new StanfordLemmatizer();

	@Test
	public void testLemmatizeToken() {
		String input = "pancake";
		assertEquals("pancake", lemmatizer.lemmatizeToken(input));
		input = "pancakes";
		assertEquals("pancake", lemmatizer.lemmatizeToken(input));
	}

	
	
	@Test
	public void testTfidf(){
		int tf1 = 5;
		int tf2 = 12;
		int N = 351;
		int df1 = 50;
		int df2 = 7;
		double actual1 = Util.tfidf(tf1, N, df1);
		double actual2 = Util.tfidf(tf2, N, df2);
		double expected1 = 4.23168556065;
		double expected2 = 20.4025089174;
		//had some problems with implicit type cast...
//		System.out.println(Math.log10(100000));
//		System.out.println(tf1*(Math.log10(N/df1)));
//		System.out.print((double)N/(double)df1);
		assertEquals(expected1, actual1, 0.0000001);
		assertEquals(expected2, actual2, 0.0000001);
	}
	
	
	/**
	 * Tests functions of EmotionVector class: addition, equals, getters,
	 */
	@Test
	public void testEmotionVector() {
		EmotionVector vector1 = new EmotionVector(1, 2, 3);
		EmotionVector vector2 = new EmotionVector(3, 2, 1);
		// adding vector1 and 2
		vector1.addVector(vector2);
		// checking addition
		assertEquals(4, vector1.getValence(), 0001);
		assertEquals(4, vector1.getArousal(), 0.001);
		assertEquals(4, vector1.getDominance(), 0.001);
		// checking .equals-method
		assertEquals(true, vector1.equals(new EmotionVector(4, 4, 4)));
		// checking getLength (sqrt(48) = length of vector 4,4,4)
		assertEquals(Math.sqrt(48), vector1.getLength(), 0.001);
		// tests normalization
		vector1.normalize(4);
		assertEquals(1, vector1.getValence(), 0001);
		assertEquals(1, vector1.getArousal(), 0.001);
		assertEquals(1, vector1.getDominance(), 0.001);
		assertEquals(Math.sqrt(48) / 4, vector1.getLength(), 0.001);
		// test multiplication
		vector1.multiplyWithConstant(3);
		assertEquals(Math.sqrt(48) / 4 * 3, vector1.getLength(), 0.001);
		// checks if equals-method of EmotionVector-class works (also with
		// double roundoff errors)
		assertTrue(new EmotionVector(1, 2, 3)
				.equals(new EmotionVector(1, 2, 3)));
		assertTrue(new EmotionVector(1, 2, 3).equals(new EmotionVector(
				1.000000001, 1.999999999, 3.0000000005)));
		// again testing normalization
		testVector.normalize(4);
		assertTrue(testVector.equals(testVectorNormalized));
		vector1 = new EmotionVector(1, 2, 3);
		vector2 = new EmotionVector(3, 2, 1);
		List<EmotionVector> vectorList = Arrays.asList(new EmotionVector[] {
				vector1, vector2 });
		// mean
		assertEquals(true, new EmotionVector(2, 2, 2).equals(EmotionVector
				.calculateMean(vectorList)));
		// sd
		assertEquals(true, new EmotionVector(1, 0, 1).equals(EmotionVector
				.calculateStandardDeviation(vectorList,
						EmotionVector.calculateMean(vectorList))));
		assertEquals(true, new EmotionVector(1, 0, 1).equals(EmotionVector
				.calculateStandardDeviation(vectorList)));
		// min
		assertEquals(true, new EmotionVector(1, 2, 1).equals(EmotionVector
				.calculateMinimumVector(vectorList)));
		// max
		assertEquals(true, new EmotionVector(3, 2, 3).equals(EmotionVector
				.calculateMaximumVector(vectorList)));

	}

	/**
	 * Test the functionality of the EmotionLexicon class, especially the
	 * correct look-up.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testEmotionLexicon() throws IOException {
		EmotionLexicon lexicon = new EmotionLexicon(Util.DEFAULTLEXICON,
				lemmatizer, Util.defaultSettings);
		assertTrue(
		// (vectorAIDS.equals(lexicon.lookUp("AIDS"))) && //to to caps-folding
		// and lemma-folding, "AIDS" is no longer in lexicon
		(vectorCalm.equals(lexicon.lookUp("calm")))
				&& (vectorLobotomy.equals(lexicon.lookUp("lobotomy")))
				&& (vectorLovable.equals(lexicon.lookUp("lovable")))
				&& (lexicon.lookUp("this is not in lexikon") == null));
	}

	/**
	 * Tests the method .file2String of File2BagOfWords_Processor.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testFile2String() throws IOException {
		String str;
		str = Util.readfile2String(TestUtil.TESTFILE);

		assertEquals("File was read incorrectly.",
				"fish fish fish.\ntest.\nThisIsNotInLexicon.\ni it", str);
	}

		@Test
	public void testUtilStdev() {
		double[] sample = { 1, 2, 3, 4, 5, 6, 7, 8, 9, };
		double expected = 2.5819888975;
		assertEquals(expected, Util.stdev(sample), 0.00000001);
	}

	@Test
	public void testUtilAverage() {
		double[] sample = { 1, 2, 3, 4, 5, 6, 7, 8, 9, };
		double expected = 5;
		assertEquals(expected, Util.average(sample), 0.000000001);
	}

	@Test
	public void testUtilVariance() {
		double[] sample = { 1, 2, 3, 4, 5, 6, 7, 8, 9, };
		double expected = 6.666666666666666;
		assertEquals(expected, Util.var(sample), 0.000000001);
	}

	/**
	 * Tests the whole tool.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testNothingInLexicon() throws Exception {
		EmotionAnalyzer analyzer = new EmotionAnalyzer(Util.defaultSettings, Util.DEFAULTLEXICON);
		MemoryContainer[] containers;	
		InputStream testData = Util.resource2Stream(TestUtil.TESTFILENOTHINGINLEXICON);
		containers = analyzer.analyze(testData);

		MemoryContainer container = containers[0];

		assertEquals(false,container==null);
		assertEquals(true,
				container.documentEmotionVector.equals(new EmotionVector(0.0,
						0.0, 0.0)));
		assertEquals(true,
				container.standardDeviationVector.equals(new EmotionVector(0.0,
						0.0, 0.0)));
		assertEquals(1, container.tokenCount);
		assertEquals(0, container.numberCount);
		assertEquals(1, container.alphabeticTokenCount);
		assertEquals(1, container.non_stopword_tokenCount);
		assertEquals(0, container.recognizedTokenCount);
	}


	/**
	 * Tests the whole tool.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testInLexiconWithNumbers() throws Exception {
		EmotionAnalyzer analyzer = new EmotionAnalyzer(Util.defaultSettings, Util.DEFAULTLEXICON);
		MemoryContainer[] containers;	
		InputStream testData = Util.resource2Stream(TestUtil.TESTFILE4);
		containers = analyzer.analyze(testData);

		MemoryContainer container0 = containers[0];
		assertEquals(false,container0==null);
		assertEquals(true, container0.documentEmotionVector.equals(new EmotionVector(2.5016, 0.0616666, 1.4799999)));
		assertEquals(true, container0.standardDeviationVector.equals(new EmotionVector(0.301118, 0.485632, 0.577581)));
		assertEquals(8, container0.tokenCount);
		assertEquals(7, container0.alphabeticTokenCount);
		assertEquals(0, container0.numberCount);
		assertEquals(4, container0.non_stopword_tokenCount);
		assertEquals(3, container0.recognizedTokenCount);

		MemoryContainer container1 = containers[1];
		assertEquals(false,container1==null);
		assertEquals(true, container1.documentEmotionVector.equals(new EmotionVector(0.0, 0.0, 0.0)));
		assertEquals(true, container1.standardDeviationVector.equals(new EmotionVector(0.0, 0.0, 0.0)));
		assertEquals(5, container1.tokenCount);
		assertEquals(0, container1.alphabeticTokenCount);
		assertEquals(3, container1.numberCount);
		assertEquals(0, container1.non_stopword_tokenCount);
		assertEquals(0, container1.recognizedTokenCount);
	}


	/**
	 * Tests the whole tool.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSingle() throws Exception {
		EmotionAnalyzer analyzer = new EmotionAnalyzer(Util.defaultSettings, Util.DEFAULTLEXICON);
		MemoryContainer[] containers;	
		InputStream testData = Util.resource2Stream(TestUtil.TESTFILE3);
		containers = analyzer.analyze(testData);

		MemoryContainer container0 = containers[0];
		assertEquals(false,container0==null);
		assertEquals(true, container0.documentEmotionVector.equals(new EmotionVector(1.99, -0.22, 1.01333)));
		assertEquals(true, container0.standardDeviationVector.equals(new EmotionVector(1.0480776053, 1.071105348, 0.8490124983)));
		assertEquals(8, container0.tokenCount);
		assertEquals(7, container0.alphabeticTokenCount);
		assertEquals(0, container0.numberCount);
		assertEquals(4, container0.non_stopword_tokenCount);
		assertEquals(3, container0.recognizedTokenCount);

	}

	/**
	 * Tests method which removes tokens which do not include a letter.
	 */
	@Test
	public void testFilterNonAlphabetics() {
		NonAlphabeticFilter filter = new NonAlphabeticFilter();
		String[] input = { "123", "123a", "a-b", ".", "djfkdjf", "apple",
				"tree", "mushroom", "Mr. President" };
		String[] expected = { "a-b", "djfkdjf", "apple", "tree", "mushroom",
				"Mr. President" };
		List<String> inputList = Arrays.asList(input);
		List<String> actualList = filter.filter(inputList);
		String[] actual = actualList.toArray(new String[actualList.size()]);
		assertArrayEquals(expected, actual);
	}

	@Test
	public void testNumberFilter() {
		List<String> testList = Arrays.asList(new String[] { "a", "b", "test",
				"133", "1", "foo", "bar", "000", "$12", "$12.50", "£12",
				"£12.50", "€12.50", "€12foo.50", "123.9382", "145.246345%",
				"23/45/2424" });
		List<String> actualList = new NumberFilter().filter(testList);
		List<String> expectedList = Arrays.asList(new String[] { "a", "b",
				"test", "foo", "bar", "€12foo.50" });
		assertArrayEquals(
				expectedList.toArray(new String[expectedList.size()]),
				actualList.toArray(new String[actualList.size()]));
	}

}