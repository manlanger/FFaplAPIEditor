package editor.mapper;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import sunset.gui.api.spec.ApiSpecification;
import sunset.gui.api.spec.SampleCode;

public class FileMapper {

	private static FileMapper instance = new FileMapper();
	private Map<String, Class<?>> fileToClassMapping = new HashMap<String, Class<?>>();
	private final String FFAPLAPISPEC = "FFaplAPISpec";
	private final String FFAPLSAMPLE = "FFaplSampleCode";
	public static final String BIN_EXT = "bin";
	public static final String JSON_EXT = "json";
	public static final String YAML_EXT = "yaml";

	private FileMapper() {
		fileToClassMapping.put(FFAPLAPISPEC + "." + BIN_EXT, ApiSpecification.class);
		fileToClassMapping.put(FFAPLSAMPLE + "." + BIN_EXT, SampleCode.class);
		fileToClassMapping.put(FFAPLAPISPEC + "." + JSON_EXT, ApiSpecification.class);
		fileToClassMapping.put(FFAPLSAMPLE + "." + JSON_EXT, SampleCode.class);
		fileToClassMapping.put(FFAPLAPISPEC + "." + YAML_EXT, ApiSpecification.class);
		fileToClassMapping.put(FFAPLSAMPLE + "." + YAML_EXT, SampleCode.class);
	}
	
	public static FileMapper getInstance() {
		return instance;
	}
	
	/*
	 * Checks if the specified file is supported by the application
	 * @param file the file to be checked
	 * @return true if file is supported, false otherwise
	 */
	public boolean isFileSupported(File file) {
		return fileToClassMapping.containsKey(file.getName());
	}
	
	/*
	 * Checks if the specified file is a binary file
	 * @param file the file to be checked
	 * @return true if file is a binary file, false otherwise
	 */
	public boolean isBinaryFile(File file) {
		return file.getName().endsWith(BIN_EXT);
	}
	
	/*
	 * Returns the class of the specified file
	 * @param file the file of which the class is needed
	 * @return the class of the specified file, null if file is not supported
	 */
	public Class<?> getClass(File file) {
		if (isFileSupported(file))
			return fileToClassMapping.get(file.getName());
		
		return null;
	}
	
	/*
	 * Returns a String representation of the supported files
	 * @return a String representation of the supported files
	 */
	public String getSupportedFiles() {
		String supportedFiles = "";
		
		for (String fileName : fileToClassMapping.keySet())
			supportedFiles += fileName + "\n";
		
		return supportedFiles;
	}
	
	/*
	 * Returns the type of the specified file
	 * @return the type of the specified file
	 */
	public String getType(File file) {
		String fileName = file.getName();
		
		if (fileName.endsWith(JSON_EXT))
			return JSON_EXT;
		if (fileName.endsWith(YAML_EXT))
			return YAML_EXT;
		
		return null;
	}
}
