package sunset.gui.api.spec;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ApiSpecification implements Serializable, IApiObject {

	private static final long serialVersionUID = 5207366277587602916L;
	
	protected TypeList typeList;
    protected ProcedureList procedureList;
    protected FunctionList functionList;

    public TypeList getTypeList() {
        return typeList;
    }

    public void setTypeList(TypeList value) {
        this.typeList = value;
    }

    public ProcedureList getProcedureList() {
        return procedureList;
    }

    public void setProcedureList(ProcedureList value) {
        this.procedureList = value;
    }

    public FunctionList getFunctionList() {
        return functionList;
    }

    public void setFunctionList(FunctionList value) {
        this.functionList = value;
    }

	@Override
	public boolean isApiConsistent() {
		String result = getInconsistencies();
		
		return result==null?true:false;
	}

	/*
	 * Checks this object for inconsistencies
	 * @return a String representation of this object's inconsistencies, null otherwise
	 */
	@Override
	public String getInconsistencies() {
		String result = "";
		List<String> typeListString = new ArrayList<String>();
    	List<String> missingTypes = new ArrayList<String>();
    	List<String> regexConflicts = new ArrayList<String>();
    	
    	for (sunset.gui.api.spec.Type type : typeList.getType()) {
    		String alphaType = getAlphabeticRepresentation(type.getName());
    		
    		typeListString.add(alphaType);	
    	}
    	
    	for (Function function : functionList.getFunction()) {
    		for (Parameter parameter : function.getParameterList().getParameter()) {
    			for (String type : parameter.getType()) {
    				checkMissingType(type, typeListString, missingTypes);
    			}
    		}
    		
    		if (function.returnType.contains("|")) {
	    		String[] returnTypes = function.returnType.split("\\|");
	    		
	    		for (String type : returnTypes) {
	    			checkMissingType(type.trim(), typeListString, missingTypes);
	    		}
    		} else {
    			checkMissingType(function.returnType, typeListString, missingTypes);
    		}
    		
    		if (function.getName().compareTo(function.getRegex()) != 0)
    			regexConflicts.add("Function \"" + function.getName() + "\" has regex \"" + function.getRegex() + "\"");
    	}
    	
    	for (Procedure procedure : procedureList.getProcedure()) {
    		for (Parameter parameter : procedure.getParameterList().getParameter()) {
    			for (String type : parameter.getType()) {
    				checkMissingType(type, typeListString, missingTypes);
    			}
    		}
    	}
    	
    	if (missingTypes.size() == 0 && regexConflicts.size() == 0)	// no conflict found
    		return null;
    	
    	if (missingTypes.size() > 0) {
	    	result += "Missing types:\n\n";
	    	
	    	for (String missingType : missingTypes) {
	    		result += "\t" + missingType + "\n";
	    	}
    	}
    	
    	if (regexConflicts.size() > 0) {
    		result += "\nRegex conflicts:\n\n";
    		
    		for (String regex : regexConflicts) {
    			result += "\t" + regex + "\n";
    		}
    	}
    	
    	return result;
	}
	
	/*
	 * Checks if the specified type is contained in the specified list
	 * @param type the type to be checked
	 * @param typeListString a String list of all supported types
	 * @param missingTypes a String list which should be filled with missing types if any
	 */
	private void checkMissingType(String type, List<String> typeListString, List<String> missingTypes) {
		String alphaType = getAlphabeticRepresentation(type);
		
		if (!typeListString.contains(alphaType)) {
			if (!missingTypes.contains(alphaType)) {
				missingTypes.add(alphaType);
			}
		}
	}
	
	/*
	 * Returns the alphabetic representation of the specified type String
	 * @param type the type of which the alphabetic representation is needed
	 * @return all alphabetic letters until the first non alphabetic letter
	 */
	private String getAlphabeticRepresentation(String type) {
		if (type.matches("[A-Za-z]+[^A-Za-z]+.*")) {	// if type starts with alphabetic char but contains any non alphabetic char
			String[] split = type.split("[^A-Za-z]");
			
			return split[0];
		}
		
		return type;
	}

	/*
	 * Checks if this object is valid
	 * @return true if object is valid (i.e. it's members and it's members members are not null, false otherwise
	 */
	@Override
	public boolean isValid() {
		if (typeList != null && functionList != null && procedureList != null) {
			if (typeList.getType() != null && functionList.getFunction() != null && procedureList.getProcedure() != null) {
				return true;
			}
		}
		
		return false;
	}
}
