package JvMaBoSS;

import java.util.*;

public class BoolState extends HashSet<String> {// can contain <nil>
	private static final long serialVersionUID = 3675050183556644068L;
	public HashSet<String> NodeSet; // careful, should not contain <nil>

	public BoolState() {
		NodeSet = new HashSet<String>();
	}

	public void UpdateNodeSet() {
		if (!this.contains("<nil>"))
			for (String Node : this)
				if (!NodeSet.contains(Node))
					NodeSet.add(Node);
	}
}
