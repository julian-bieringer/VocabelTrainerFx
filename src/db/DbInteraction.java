/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import model.Lesson;

/**
 *
 * @author Jbiering
 */
public interface DbInteraction {
    public boolean writeLesson(Lesson lesson);
    public Lesson readLesson(Lesson lesson);
    public long getVocabelCount();
    public long getLessonCount();
}
