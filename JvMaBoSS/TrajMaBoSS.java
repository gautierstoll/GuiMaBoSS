package JvMaBoSS;

import java.util.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.*;
import java.io.IOException;

public class TrajMaBoSS {
	public ArrayList<Double> timeArray;
	public ArrayList<Double> hArray; // entropy
	public ArrayList<Double> thArray; // transition entropy
	public ArrayList<Double> errThArray; // transtion entropy error
	public ArrayList<ArrayList<Double>> hamDistArray; // hamming distance
	public HashMap<HashSet<String>, ArrayList<Double>> probTraj; // can contains <nil>
	public int Trajlength;
	public HashSet<String> nodeList; // node list identified by the whole possible states, cannot contains "<nil>"

	public TrajMaBoSS(String probTrajFileName) // constructor from a MaBoSS probtraj file
	{
		Trajlength = 0;
		nodeList = new HashSet<String>();
		timeArray = new ArrayList<Double>();
		hArray = new ArrayList<Double>();
		thArray = new ArrayList<Double>();
		errThArray = new ArrayList<Double>();
		hamDistArray = new ArrayList<ArrayList<Double>>();
		probTraj = new HashMap<HashSet<String>, ArrayList<Double>>();

		try {
			File probTrajFile = new File(probTrajFileName);
			BufferedReader bufferTrajProbFile = new BufferedReader(new FileReader(probTrajFile));
			String fileLine = bufferTrajProbFile.readLine();
			String[] splittedHeader = fileLine.split("\t");
			for (String Column : splittedHeader)
				if (Column.contains("HD="))
					hamDistArray.add(hamDistArray.size(), new ArrayList<Double>());
			while ((fileLine = bufferTrajProbFile.readLine()) != null)
				this.addLine(fileLine);
			bufferTrajProbFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void addLine(String Line) // private method for iterative construction from a probtraj file line,
	// used in the constructor
	{
		String[] splittedLine = Line.split("\t");
		timeArray.add(timeArray.size(), (Double.parseDouble(splittedLine[0])));
		hArray.add(hArray.size(), (Double.parseDouble(splittedLine[3])));
		thArray.add(thArray.size(), (Double.parseDouble(splittedLine[1])));
		errThArray.add(errThArray.size(), (Double.parseDouble(splittedLine[2])));
		for (int i = 0; i < hamDistArray.size(); i++) {
			ArrayList<Double> indexhamDistArray = hamDistArray.get(i);
			indexhamDistArray.add(indexhamDistArray.size(), Double.parseDouble(splittedLine[4 + i]));
			hamDistArray.set(i, indexhamDistArray);
		}
		this.AddProbDist(Arrays.copyOfRange(splittedLine, 4 + hamDistArray.size(), splittedLine.length - 1));
		Trajlength++;
	}

	private void AddProbDist(String[] Line) // private method for adding a probability distribution to probTraj, used in
											// addLine,
	// from the sub-part of a probtraj file line
	{
		if (Trajlength != 0)
			for (ArrayList<Double> ProbList : probTraj.values())
				ProbList.add(0.0);
		for (int i = 0; i < Line.length; i = i + 3) {
			String[] StatesList = Line[i].split(" -- ");
			HashSet<String> State = new HashSet<String>();
			for (String Node : StatesList)
				State.add(Node);
			ArrayList<Double> ProbList = probTraj.get(State);
			if (Trajlength == 0) {
				ArrayList<Double> NewProbList = new ArrayList<Double>();
				NewProbList.add(Trajlength, Double.parseDouble(Line[i + 1]));
				probTraj.put(State, NewProbList);
				if (!State.contains("<nil>"))
					for (String Node : State)
						nodeList.add(Node);
			} else if (ProbList == null) {
				ArrayList<Double> NewProbList = new ArrayList<Double>(Collections.nCopies(Trajlength, 0.0));
				NewProbList.add(Trajlength, Double.parseDouble(Line[i + 1]));
				probTraj.put(State, NewProbList);
				if (!State.contains("<nil>"))
					for (String Node : State)
						this.nodeList.add(Node);
			} else
				probTraj.get(State).set(Trajlength, Double.parseDouble(Line[i + 1]));
		}
	}

	public void WriteProbTrajTableFile(String probTrajTableName) // create the probtraj_table file
	{
		try {
			File probTrajFile = new File(probTrajTableName);
			BufferedWriter bufferTrajTableFile = new BufferedWriter(new FileWriter(probTrajFile));
			bufferTrajTableFile.write("Time\tTH\tErrTH\tH");
			ArrayList<HashSet<String>> StateList = new ArrayList<HashSet<String>>();
			for (HashSet<String> MyState : this.probTraj.keySet())
				StateList.add(MyState);
			for (int i = 0; i < StateList.size(); i++) {
				bufferTrajTableFile.write("\tProb[");
				Boolean FirstOne = true;
				for (String Node : StateList.get(i)) {
					if (FirstOne) {
						bufferTrajTableFile.write(Node);
						FirstOne = false;
					} else
						bufferTrajTableFile.write("--" + Node);
				}
				bufferTrajTableFile.write("]");
			}
			bufferTrajTableFile.newLine();
			for (int Traj = 0; Traj < Trajlength; Traj++) {
				bufferTrajTableFile.write(this.timeArray.get(Traj).toString() + "\t");
				bufferTrajTableFile.write(thArray.get(Traj).toString() + "\t");
				bufferTrajTableFile.write(errThArray.get(Traj).toString() + "\t");
				bufferTrajTableFile.write(hArray.get(Traj).toString());
				for (int i = 0; i < StateList.size(); i++)
					bufferTrajTableFile.write("\t" + this.probTraj.get(StateList.get(i)).get(Traj).toString());
				bufferTrajTableFile.newLine();
			}
			bufferTrajTableFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public PlotProbDist TrajPlotProbDist(Double ProbThresh) {
		HashMap<String, ArrayList<Double>> probTrajStateName = new HashMap<String, ArrayList<Double>>();
		for (HashMap.Entry<HashSet<String>, ArrayList<Double>> StateProbArray : probTraj.entrySet()) {
			String StateName = new String();
			for (String Node : new TreeSet<String>(StateProbArray.getKey()))
				StateName = StateName + " " + Node;
			probTrajStateName.put(StateName, StateProbArray.getValue());
		}
		PlotProbDist PlProbDist = new PlotProbDist(timeArray, probTrajStateName, ProbThresh);
		PlProbDist.setTitle("Time dependent probabilities");
		return (PlProbDist);
	}

}