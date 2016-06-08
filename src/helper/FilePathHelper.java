/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helper;

/**
 *
 * @author JBiering
 */
public class FilePathHelper {
    
    public static String getUserDirectory(){
        final String user = System.getProperty("user.name");
        String s = String.format("C:\\Users\\%s\\Documents\\VocabelTrainer", user);
        s = s.replace("\\", "/");
        return s;
    }
}
