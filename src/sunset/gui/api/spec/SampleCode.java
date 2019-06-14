package sunset.gui.api.spec;

import java.io.Serializable;

public class SampleCode implements Serializable, IApiObject {

	private static final long serialVersionUID = -7469586167408269122L;

	protected ProcedureList procedureList;
    protected FunctionList functionList;

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
		return true;
	}

	@Override
	public String getInconsistencies() {
		return null;
	}

	@Override
	public boolean isValid() {
		return true;
	}
}
