package editor.handler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JOptionPane;

public class TextFileHandler {

	private static TextFileHandler instance = new TextFileHandler();
	
	private TextFileHandler() {
	}
	
	public static TextFileHandler getInstance() {
		return instance;
	}
	
	/*
	 * Reads the specified file and returns it's content within a String
	 * @param file the file to be readed
	 * @return the content of the text file if read operation succeeds, null otherwise
	 */
	public String readApiTextFromFile(File file) {
		String apiText = null;

		try {
			FileReader fileReader = new FileReader(file);
			char[] buffer = new char[(int) file.length()];
			
			fileReader.read(buffer);
			fileReader.close();
			
			apiText = String.valueOf(buffer);
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "File not found!", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Cannot read file!", JOptionPane.ERROR_MESSAGE);
		}
		
		return apiText;		
	}
	
	/*
	 * Saves the specified API text representation into the specified file
	 * @param apiText the API text which should be saved
	 * @file the file into which the API text should be saved
	 * @return true if save succeeded, false otherwise
	 */
	public boolean saveApiTextToFile(String apiText, File file) {
    	try {
			FileWriter fileWriter = new FileWriter(file);
			fileWriter.write(apiText);
			fileWriter.flush();
			fileWriter.close();
			return true;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Cannot save file!", JOptionPane.ERROR_MESSAGE);
		}
    	
    	return false;
    }
}

	