package sunset.gui.api.spec;

public interface IApiObject {
	public boolean isApiConsistent();
	public String getInconsistencies();
	public boolean isValid();
}
