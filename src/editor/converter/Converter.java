package editor.converter;

import com.google.gson.*;

import editor.mapper.FileMapper;

import org.yaml.snakeyaml.*;
import org.yaml.snakeyaml.error.YAMLException;
import javax.swing.JOptionPane;

import sunset.gui.api.spec.IApiObject;

public class Converter {

	private static Converter instance = new Converter();
	
	private Converter() {
	}
	
	public static Converter getInstance() {
		return instance;
	}
	
	/*
	 * Converts a text into a specific object type
	 * @param text the text which needs to be converted into an object
	 * @param type the type of the text
	 * @param clazz the clazz of the object 
	 * @return an IApiObject instance of type clazz
	 */
	public IApiObject textToObject(String text, String type, Class<?> clazz) {
		IApiObject apiObject = null;
		
		switch (type) {
		case FileMapper.JSON_EXT:
			try {
				GsonBuilder gsonBuilder = new GsonBuilder();
			    Gson gson = gsonBuilder.create();
			    apiObject = (IApiObject) gson.fromJson(text, clazz);
			} catch (JsonSyntaxException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "Invalid JSON Format!", JOptionPane.ERROR_MESSAGE);
			}
			
			break;
			
		case FileMapper.YAML_EXT:
			try {
				Yaml yaml = new Yaml();
				apiObject = (IApiObject) yaml.loadAs(text, clazz);
			} catch (YAMLException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "Invalid YAML Format!", JOptionPane.ERROR_MESSAGE);
			}
			
			break;
		}
		
		if (apiObject != null) {
			if (apiObject.isValid()) {
				return apiObject;
			}
			else {
				JOptionPane.showMessageDialog(null, "Invalid API Object. Please check content of text file.", "Error when parsing Object!", JOptionPane.ERROR_MESSAGE);
			}
		}
		
		return null;
	}
	
	/*
	 * Converts an object into it's string representation of type
	 * @param apiObject the API object which should be converted
	 * @param type the type of the string representation
	 * @return String representation of API object
	 */
	public String objectToText(IApiObject apiObject, String type) {
		String text = "";
		
		switch (type) {
		case FileMapper.JSON_EXT:
			GsonBuilder gsonBuilder = new GsonBuilder(); 
			gsonBuilder.setPrettyPrinting();
		    Gson gson = gsonBuilder.create();
		    text = gson.toJson(apiObject);
			break;
			
		case FileMapper.YAML_EXT:
			Yaml yaml = new Yaml();
			text = yaml.dump(apiObject);
			break;
		}
		
	    return text;
	}
}

	