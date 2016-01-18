package emotionAnalyzer;

import java.io.File;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class EmotionAnalyzer_UI {
	
	public static void main (String[] args) throws Exception{
		if (args.length > 0){
			switch (args[0]){
			
			case "-help":
				printHelp();
				break;
			case "-test":
				runTests();
				break;
			default:
				File srcDir = new File(args[0]);
				File targetDir;
				if (args.length >1){
					targetDir = new File(args[1]);
				}
				else{
					targetDir = srcDir;
				}
				EmotionAnalyzer analyzer = new EmotionAnalyzer(Util.DEFAULTLEXICON);
				DocumentContainer[] containers = analyzer.analyze(srcDir, targetDir, Util.defaultSettings);
				printDataTemplate();
				for (DocumentContainer container: containers){
					container.printData();
				}
				break;
			}
		}
		else printHelp();
	}
	
	
	private static void runTests(){
		JUnitCore junit = new JUnitCore();
		Result result = junit.run(Tests.class);
		System.err.println("Ran " + result.getRunCount() + " tests in "+ result.getRunTime() +"ms.");
		if (result.wasSuccessful()) System.out.println("All tests were successfull!");
		else {
			System.err.println(result.getFailureCount() + "Failures:");
			for (Failure fail: result.getFailures()){
				System.err.println("Failure in: "+ fail.getTestHeader());
				System.err.println(fail.getMessage());
				System.err.println(fail.getTrace());
				System.err.println();
			}
		}
		
	}
	
	
	private static void printDataTemplate() {
		System.out.println("File Name"
				+ "\tValence"
				+ "\tArousal"
				+ "\tDominance"
				+ "\tStdDev Valenence"
				+ "\tStdDev Arousal"
				+ "\tStdDev Dominance"
				+ "\tTokens"
				+ "\tAlphabetic Token"
				+ "\tNon-Stopword Tokens"
				+ "\tRecognized Tokens"
				+ "\tNumberCount"); 
		
	}

	private static void printHelp(){
		System.out.println("\nUsage:\tIndicate a source folder (first argument, all txt-files will be "
				+ "analyzed) and a target folder (second argument, auxilary files and additional output "
				+ "will be saved there). The main output of this tool will be printed in standard output."
				+ " For further Information, please consult the README-file."
				+ "\n\n Options:"
				+ "\n\n\t-help\t\tPrint this message."
				+ "\n\t-test\t\tCheck functionality of this tool.\n"
				);
	}
}
