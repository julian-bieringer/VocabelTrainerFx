/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import com.mysql.jdbc.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Lesson;
import model.Vocabel;

/**
 *
 * @author Jbiering
 */
public class DbHelper implements DbInteraction{
    
    private static final Logger logger = 
            Logger.getLogger(DbHelper.class.getSimpleName());
    private final static String DB_CONNECTION = 
            "YOUR DB CONNECTION";
    private final static String USERNAME = "YOUR USERNAME";
    private final static String PASSWORD = "YOUR PASSWORD";
    private final static String SELECT_COUNT_VOCABELS = 
            "SELECT COUNT(*) AS VOCABEL_COUNT FROM Vocabel";
    private final static String SELECT_UNIT_BY_NAME = 
            "SELECT lessonName FROM Unit WHERE lessonName = ?";
    private final static String INSERT_UNIT = 
            "INSERT INTO Unit(lessonName, language, additionalInfoId) VALUES"
            + "(?,?,?); ";
    private final static String INSERT_ADDITIONAL_INFO = 
            "INSERT INTO AdditionalInfo(uploadDate, uploader) VALUES (?,?)";
    private final static String INSERT_VOCABEL = 
            "INSERT INTO Vocabel(germanWord, foreignWord, unitId) VALUES"
            + "(?,?,?)";

    @Override
    public  boolean writeLesson(Lesson lesson) {
        logger.log(Level.INFO, "Connecting to database...");
        try (Connection connection = (Connection) DriverManager
                        .getConnection(DB_CONNECTION, USERNAME, PASSWORD)) {
            PreparedStatement additionalInfoInsertStatement = 
                    prepareAdditionInfoStatement(connection);
            if(additionalInfoInsertStatement != null){
                additionalInfoInsertStatement.executeUpdate();
                ResultSet keysAddInfo = additionalInfoInsertStatement
                        .getGeneratedKeys();
                keysAddInfo.next();
                int addInfoId = keysAddInfo.getInt(1);
                PreparedStatement insertUnitStatement = 
                        prepareUnitStatement(connection, lesson, addInfoId);
                if(insertUnitStatement != null){
                    insertUnitStatement.executeUpdate();
                    ResultSet keysUnitId = additionalInfoInsertStatement
                            .getGeneratedKeys();
                    keysUnitId.next();
                    int unitId = keysUnitId.getInt(1);
                    for(Vocabel curr : lesson.getVocabels()){
                        PreparedStatement insertVocabelStatement = 
                            prepareVocabelStatement(connection, curr, unitId);
                        if(insertVocabelStatement != null)
                            insertVocabelStatement.executeUpdate();
                    }
                }
            }
            return true;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
            logger.log(Level.SEVERE, "Connection couldn't be establised");
        }
        return false;
    }
    
    private PreparedStatement prepareAdditionInfoStatement
        (Connection connection){
        PreparedStatement statement = null;
        try { 
            statement =
                    connection.prepareStatement(INSERT_ADDITIONAL_INFO,
                            Statement.RETURN_GENERATED_KEYS);
            LocalDateTime date = LocalDateTime.now();
            DateTimeFormatter formatter = 
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            statement.setString(1, formatter.format(date));
            statement.setString(2, System.getProperty("user.name"));
        } catch (SQLException ex) {
            Logger.getLogger(DbHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return statement;
    }
    
    private PreparedStatement prepareUnitStatement(Connection connection,
            Lesson lesson, int addInfoId){
        PreparedStatement statement = null;
        try { 
            statement = connection.prepareStatement(INSERT_UNIT,
                            Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, lesson.getLessonName());
            statement.setString(2, lesson.getLanguage());
            statement.setInt(3, addInfoId);
        } catch (SQLException ex) {
            Logger.getLogger(DbHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return statement;
    }
    
    private PreparedStatement prepareVocabelStatement(Connection connection,
            Vocabel vocabel, int unitId){
        PreparedStatement statement = null;
        try { 
            statement = connection.prepareStatement(INSERT_VOCABEL,
                            Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, vocabel.getGermanWord());
            statement.setString(2, vocabel.getForeignWord());
            statement.setInt(3, unitId);
        } catch (SQLException ex) {
            Logger.getLogger(DbHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return statement;
    }

    @Override
    public long getVocabelCount() {
        long count = 0;
        try (Connection connection = (Connection) DriverManager
                        .getConnection(DB_CONNECTION, USERNAME, PASSWORD)) {
            PreparedStatement statement= 
                    connection.prepareStatement(SELECT_COUNT_VOCABELS);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                count = resultSet.getLong("VOCABEL_COUNT");
            }
            logger.log(Level.INFO, "Connection established, Count = {0}", String.valueOf(count));
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
            logger.log(Level.SEVERE, "Connection couldn't be establised");
        }
        return count;
    }
    
    public boolean lessonExists(Lesson lesson){
        try (Connection connection = (Connection) DriverManager
                        .getConnection(DB_CONNECTION, USERNAME, PASSWORD)) {
            PreparedStatement statement= 
                    connection.prepareStatement(SELECT_UNIT_BY_NAME);
            statement.setString(1, lesson.getLessonName());
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
            logger.log(Level.SEVERE, "Connection couldn't be establised");
        }
        return false;
    }

    @Override
    public long getLessonCount() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Lesson readLesson(Lesson lesson) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
