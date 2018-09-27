package emotionAnalyzer;

import java.util.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Set;
import com.google.common.collect.HashMultiset;

public class TestUtil {
	
	public static final String TESTFILE ="emotionAnalyzer/testFile.txt";
	public static final String TESTFILE2 ="emotionAnalyzer/testFile2.txt"; //(not normalized) Document vector should be (-8.43, -3.75, -7.04) using warriners (default) lexicon
	public static final String TESTFILE5 ="emotionAnalyzer/testFile5.txt";
	public static final String TESTFILE3 ="emotionAnalyzer/testFile3.txt";
	public static final String TESTLEXICON="emotionAnalyzer/testLexicon.txt";
	public static final String TESTFILE_LEMMA = "emotionAnalyzer/test.test.test.testFile_Lemma.txt";
	public static final String TESTLEXICON_LEMMA = "emotionAnalyzer/testLexicon_Lemma.txt";
	public static final String TESTFILE4 = "emotionAnalyzer/testFile4.txt";
	public static final String EXPECTEDOUTPUT ="emotionAnalyzer/expectedTestOutput.txt";
	public static final String TARGETFOLDER = "target/";
	public static final String TESTFILENOTHINGINLEXICON = "emotionAnalyzer/testFileNothingInLexicon.txt";
}
