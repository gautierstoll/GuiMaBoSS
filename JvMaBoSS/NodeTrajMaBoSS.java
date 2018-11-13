package JvMaBoSS;

import java.util.*;
import java.io.*;

public class NodeTrajMaBoSS {
	public ArrayList<Double> timeArray;
	public ArrayList<Double> hArray; // entropy
	public ArrayList<Double> thArray; // transition entropy
	public ArrayList<Double> errThArray; // transtion entropy error
	public ArrayList<ArrayList<Double>> hamDistArray; // hamming distance
	public HashMap<String, ArrayList<Double>> nodeProbTraj;
	public int Trajlength;
	public HashSet<String> nodeList; // node list identified by the whole possible states, cannot contains "<nil>"

	public NodeTrajMaBoSS(String probTrajFileName) // constructor from a MaBoSS probtraj file
	{
		Trajlength = 0;
		nodeList = new HashSet<String>();
		timeArray = new ArrayList<Double>();
		hArray = new ArrayList<Double>();
		thArray = new ArrayList<Double>();
		errThArray = new ArrayList<Double>();
		hamDistArray = new ArrayList<ArrayList<Double>>();
		nodeProbTraj = new HashMap<String, ArrayList<Double>>();
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
			for (ArrayList<Double> ProbList : nodeProbTraj.values())
				ProbList.add(0.0);
		for (int i = 0; i < Line.length; i = i + 3) {
			if (!Line[i].equals("<nil>")) {
				String[] NodeFromLine = Line[i].split(" -- ");
				for (int j = 0; j < NodeFromLine.length; j++) {
					ArrayList<Double> ProbList = nodeProbTraj.get(NodeFromLine[j]);
					if (ProbList == null) {
						if (Trajlength == 0) {
							ArrayList<Double> NewProbList = new ArrayList<Double>();
							NewProbList.add(Double.parseDouble(Line[i + 1]));
							nodeProbTraj.put(NodeFromLine[j], NewProbList);
						} else {
							ArrayList<Double> NewProbList = new ArrayList<Double>(Collections.nCopies(Trajlength, 0.0));
							NewProbList.add(Trajlength, Double.parseDouble(Line[i + 1]));
							nodeProbTraj.put(NodeFromLine[j], NewProbList);
						}
						nodeList.add(NodeFromLine[j]);
					} else {
						Double Prob = new Double(
								nodeProbTraj.get(NodeFromLine[j]).get(Trajlength) + Double.parseDouble(Line[i + 1]));
						nodeProbTraj.get(NodeFromLine[j]).set(Trajlength, Prob);
					}
				}
			}
		}
	}

	public void WriteProbNodeTrajTableFile(String nodeProbTrajTableName) // create the probtraj_nodetable file
	{
		try {
			File nodeProbTrajFile = new File(nodeProbTrajTableName);
			BufferedWriter bufferNodeTrajTableFile = new BufferedWriter(new FileWriter(nodeProbTrajFile));
			bufferNodeTrajTableFile.write("Time\tTH\tErrTH\tH");
			ArrayList<String> orderedNodeList = new ArrayList<String>(nodeList);
			for (int i = 0; i < orderedNodeList.size(); i++)
				bufferNodeTrajTableFile.write("\tProb[" + orderedNodeList.get(i) + "]");
			bufferNodeTrajTableFile.newLine();
			for (int Traj = 0; Traj < Trajlength; Traj++) {
				bufferNodeTrajTableFile.write(this.timeArray.get(Traj).toString() + "\t");
				bufferNodeTrajTableFile.write(thArray.get(Traj).toString() + "\t");
				bufferNodeTrajTableFile.write(errThArray.get(Traj).toString() + "\t");
				bufferNodeTrajTableFile.write(hArray.get(Traj).toString());
				for (int i = 0; i < orderedNodeList.size(); i++)
					bufferNodeTrajTableFile
							.write("\t" + this.nodeProbTraj.get(orderedNodeList.get(i)).get(Traj).toString());
				bufferNodeTrajTableFile.newLine();
			}
			bufferNodeTrajTableFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public PlotProbDist NodeTrajPlotProbDist(Double ProbThresh) {
		PlotProbDist PlProbDist = new PlotProbDist(timeArray, nodeProbTraj, ProbThresh);
		PlProbDist.setTitle("Time dependent node probabilities");
		return (PlProbDist);
	}
}
