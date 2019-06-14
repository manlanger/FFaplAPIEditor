package sunset.gui.api.spec;

import java.io.Serializable;
import java.util.List;

public class TypeList implements Serializable {

	private static final long serialVersionUID = 7776359732048641268L;

    protected List<Type> type;

    public List<Type> getType() {
        return this.type;
    }
    
    public void setType(List<Type> value) {
    	type = value;
    }
}
