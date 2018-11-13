package JvMaBoSS;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BoolStateDist extends HashMap<HashSet<String>, Double> { // careful, HashSet<String> could be "<nil>"
	private static final long serialVersionUID = 1L;
	public HashSet<String> NodeSet; // careful, should not contain <nil>, not final

	public BoolStateDist() {
		NodeSet = new HashSet<String>();
	}

	public BoolStateDist(String probTrajLine) // line from probtraj file, separated by \t
	{
		NodeSet = new HashSet<String>();
		String[] SplittedLine = probTrajLine.split("\t");
		Pattern letterPattern = Pattern.compile(".*[a-aA-Z_][a-aA-Z0-9_]*.*");
		int FirstStateIndex = -1;
		Matcher StateMatcher;
		do {
			FirstStateIndex++;
			StateMatcher = letterPattern.matcher(SplittedLine[FirstStateIndex]);
		} while ((!StateMatcher.matches()) && (!SplittedLine[FirstStateIndex].equals("<nil>")));
		for (int i = FirstStateIndex; i < SplittedLine.length; i = i + 3) {
			HashSet<String> State = new HashSet<String>(Arrays.asList(SplittedLine[i].split(" -- ")));
			this.put(State, Double.parseDouble(SplittedLine[i + 1]));
		}
	}

	public void put(HashSet<String> BState, double Prob) {
		if (super.containsKey(BState)) {
			double NewProb = Prob + super.get(BState);
			super.put(BState, NewProb);
			this.normalize();
		} else {
			super.put(BState, Prob);
			this.updateNodeSet(BState);
		}
	}

	private void normalize() {
		double Norm = 0;
		for (HashMap.Entry<HashSet<String>, Double> entry : this.entrySet())
			Norm += entry.getValue();
		for (HashMap.Entry<HashSet<String>, Double> entry : this.entrySet())
			this.put(entry.getKey(), entry.getValue() / Norm);
	}

	private void updateNodeSet(HashSet<String> NewBState) {
		for (String Node : NewBState)
			if (!this.NodeSet.contains(Node) && (!Node.equals("<nil>")))
				this.NodeSet.add(Node);
	}

	public String Tranform2InitCond() {
		String OutLine = "p[";
		ArrayList<String> NodeList = new ArrayList<String>(NodeSet);
		OutLine = OutLine + NodeList.get(0);
		for (int i = 1; i < NodeList.size(); i++) {
			OutLine = OutLine + (",");
			OutLine = OutLine + NodeList.get(i);
		}
		OutLine = OutLine + "].istate = ";
		for (HashMap.Entry<HashSet<String>, Double> BoolStatProb : this.entrySet()) {
			OutLine = OutLine + Double.toString(BoolStatProb.getValue());
			OutLine = OutLine + "[";
			Integer Test = new Integer(BoolStatProb.getKey().contains(NodeList.get(0)) ? 1 : 0);
			OutLine = OutLine + Test.toString();
			for (int i = 1; i < NodeList.size(); i++) {
				OutLine = OutLine + ",";
				Test = BoolStatProb.getKey().contains(NodeList.get(i)) ? 1 : 0;
				OutLine = OutLine + Test.toString();
			}
			OutLine = OutLine + "] , ";
		}
		OutLine = OutLine.substring(0, OutLine.length() - 3) + ";";
		return (OutLine);
	}
}
