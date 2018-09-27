package jemas;

public class Emotion {
    private final double dominance;
    private final double valence;
    private final double arousal;
    private final double dominanceStdDev;
    private final double valenceStdDev;
    private final double arousalStdDev;
    private final int tokens;
    private final int alphabeticTokens;	
    private final int onStopwordTokens;	
    private final int recognizedTokens;
    private final int numberCount;

    public Emotion(double dominance, double valence, double arousal,double dominanceStdDev, double valenceStdDev, double arousalStdDev, int tokens,int alphabeticTokens, int onStopwordTokens, int recognizedTokens, int numberCount){
      this.dominance = dominance;
      this.valence = valence;
      this.arousal = arousal;
      this.dominanceStdDev= dominanceStdDev;
      this.valenceStdDev=valenceStdDev;
      this.arousalStdDev=arousalStdDev;
      this.tokens=tokens;
      this.alphabeticTokens=alphabeticTokens;
      this.onStopwordTokens=onStopwordTokens;
      this.recognizedTokens=recognizedTokens;
      this.numberCount=numberCount;
  }

  public double getDominance() {
      return dominance;
  }

  public double getValence() {
      return valence;
  }

  public double getArousal() {
    return arousal;
  }

  public double getDominanceStdDev() {
    return dominanceStdDev;
  }

  public double getValenceStdDev() {
    return valenceStdDev;
  }

  public double getArousalStdDev() {
    return arousalStdDev;
  }

  public int getTokens() {
    return tokens;
  }

  public int getAlphabeticTokens() {
    return alphabeticTokens;
  }

  public int getOnStopwordTokens() {
    return onStopwordTokens;
  }

  public int getRecognizedTokens() {
    return recognizedTokens;
  }

  public int getNumberCount() {
    return numberCount;
  }
}