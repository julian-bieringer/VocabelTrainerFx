package model;

import java.util.List;

public class Lesson
{
    private List<Vocabel> vocabels;
    private String lessonName;
    private String language;

    public List<Vocabel> getVocabels() {
        return this.vocabels;
    }


    public void setVocabels(final List<Vocabel> vocabels) {
        this.vocabels = vocabels;
    }

    

    public String getLessonName() {
        return this.lessonName;
    }  

    public void setLessonName(final String lessonName) {
        this.lessonName = lessonName;
    }

    public String getLanguage() {
            return language;
    }



    public void setLanguage(String language) {
            this.language = language;
    }

    @Override
    public String toString() {
        return this.lessonName;
    }
}