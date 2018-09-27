package jemas;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import emotionAnalyzer.EmotionAnalyzer;
import emotionAnalyzer.EmotionVector;
import emotionAnalyzer.MemoryContainer;
import emotionAnalyzer.Util;

@RestController
public class EmotionController {

    EmotionAnalyzer analyzer = null; 

    public EmotionController(){
        try {
            analyzer = new EmotionAnalyzer(Util.defaultSettings, Util.DEFAULTLEXICON);
        } catch (Exception e) {
            //TODO: handle exception
        }        
    }

    @RequestMapping(value="/emotion", method=RequestMethod.POST,consumes = "text/plain")
    public Emotion[] emotion(HttpEntity<String> httpEntity) throws Exception{

        List<Emotion> emotions =new ArrayList<Emotion>();

        String input = httpEntity.getBody();
        
        InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));

        MemoryContainer[] containers = analyzer.analyze(stream);
        for (MemoryContainer container: containers){
            EmotionVector sdVector= container.getStandardDeviationVector();
            EmotionVector emotionVector= container.getDocumentEmotionVector();
            emotions.add(new Emotion(emotionVector.getDominance(),	emotionVector.getValence(),	emotionVector.getArousal(),	sdVector.getDominance(),	sdVector.getValence(),	sdVector.getArousal(),	container.getTokenCount(),	container.getAlphabeticTokenCount(),	container.getNonStopwordTokenCount(),	container.getRecognizedTokenCount()	,container.getNumberCount())) ;      
        }
        return  emotions.toArray(new Emotion[0]);
    }
}
