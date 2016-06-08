/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package runnables;

import db.DbHelper;
import file.ReadFile;
import model.Lesson;

/**
 *
 * @author Jbiering
 */
public class UnitSynchronizerRunnable implements Runnable{

    @Override
    public void run() {
        DbHelper dbHelper = new DbHelper();
        Lesson[] lessons = ReadFile.readAllJsonFiles();
        for (Lesson lesson : lessons) {
            if (dbHelper.lessonExists(lesson) == false) {
                dbHelper.writeLesson(lesson);
            }
        }
    }
}
