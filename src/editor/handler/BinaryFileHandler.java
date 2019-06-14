package editor.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JOptionPane;

import sunset.gui.api.spec.IApiObject;

public class BinaryFileHandler {

	private static BinaryFileHandler instance = new BinaryFileHandler();
	
	private BinaryFileHandler() {
	}
	
	public static BinaryFileHandler getInstance() {
		return instance;
	}
	
	/*
	 * Deserializes a given binary file into it's API object representation
	 * @param file the binary file which should be deserialized
	 * @return the IAPIObject resulting from deserialization
	 */
	public IApiObject deserializeBinaryFileToApiObject(File file) {
		IApiObject apiObject = null;
		
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
			apiObject = (IApiObject) objectInputStream.readObject();
			objectInputStream.close();
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "File not found!", JOptionPane.ERROR_MESSAGE);
		} catch (ClassNotFoundException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Class not found!", JOptionPane.ERROR_MESSAGE);			
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Cannot read file!", JOptionPane.ERROR_MESSAGE);
		}
		
		return apiObject;
	}
	
	/*
	 * Serializes the specified API object into the specified file
	 * @param apiObject the API object which should be serialized
	 * @file the file into which the apiObject should be serialized
	 * @return true if serialization succeeded, false otherwise
	 */
	public boolean serializeApiObjectToBinaryFile(IApiObject apiObject, File file) {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(apiObject);
			objectOutputStream.flush();
			objectOutputStream.close();
			fileOutputStream.flush();
			fileOutputStream.close();
			return true;
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "File not found!", JOptionPane.ERROR_MESSAGE);
		}  catch (IOException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Cannot write file!", JOptionPane.ERROR_MESSAGE);
		}
		
		return false;
	}
}

	