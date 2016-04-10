package model;

import javafx.beans.property.SimpleStringProperty;
import org.codehaus.jackson.annotate.JsonIgnore;

public class Vocabel
{
    private final SimpleStringProperty germanWord;
    private final SimpleStringProperty foreignWord;
    @JsonIgnore
    private SimpleStringProperty anwserStateText;
    @JsonIgnore
    private boolean vocabelCorrect;
    
    public Vocabel(){
        germanWord = new SimpleStringProperty();
        foreignWord = new SimpleStringProperty();
        anwserStateText = new SimpleStringProperty();
    }
    
     public Vocabel(String germanWord, String foreignWord){
        this.germanWord = new SimpleStringProperty(germanWord);
        this.foreignWord = new SimpleStringProperty(foreignWord);
        this.anwserStateText = new SimpleStringProperty();
    }
    
    public Vocabel(String germanWord, String foreignWord, String answerStateText){
        this(germanWord, foreignWord);
        this.anwserStateText = new SimpleStringProperty(answerStateText);
    }
    
    public String getGermanWord() {
        return this.germanWord.get();
    }

    public void setGermanWord(final String germanWord) {
        this.germanWord.set(germanWord);
    }  

    public String getForeignWord() {
        return this.foreignWord.get();
    }

    public void setForeighWord(String englishWord) {
        this.foreignWord.set(englishWord);
    }

    public boolean isVocabelCorrect() {
        return this.vocabelCorrect;
    }

    public void setVocabelCorrect(boolean vocabelCorrect) {
        this.vocabelCorrect = vocabelCorrect;
    }
    public String getAnwserStateText() {
        return anwserStateText.get();
    }
}