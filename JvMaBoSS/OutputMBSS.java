package JvMaBoSS;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OutputMBSS {
	public final String Name;
	public final String runName;
	public final String fpName;
	public final String probTrajName;
	public final String statDistName;

	public OutputMBSS(String Prefix) {
		Name = new String(Prefix);
		File TempFile = new File(Prefix + "_run.txt");
		runName = TempFile.getAbsolutePath();
		TempFile = new File(Prefix + "_fp.csv");
		fpName = TempFile.getAbsolutePath();
		TempFile = new File(Prefix + "_probtraj.csv");
		probTrajName = TempFile.getAbsolutePath();
		TempFile = new File(Prefix + "_statdist.txt");
		statDistName = TempFile.getAbsolutePath();
	}

	protected OutputMBSS(OutputMBSS Oput) {
		this.Name = Oput.Name;
		this.runName = Oput.runName;
		this.fpName = Oput.fpName;
		this.probTrajName = Oput.probTrajName;
		this.statDistName = Oput.statDistName;
	}

	public void printFiles() {
		System.out.println("Run file: " + runName);
		System.out.println("fp file: " + fpName);
		System.out.println("probtraj file: " + probTrajName);
		System.out.println("statdist file: " + statDistName);
	}

	public PieProbDist PieLastProbTrj(Double ProbThresh) {
		String LastfileLine = new String();
		try {
			File probTrajFile = new File(probTrajName);
			BufferedReader bufferTrajProbFile = new BufferedReader(new FileReader(probTrajFile));
			String fileLine = new String();
			while ((fileLine = bufferTrajProbFile.readLine()) != null)
				LastfileLine = fileLine;
			bufferTrajProbFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		PieProbDist pPDist = new PieProbDist(LastfileLine, ProbThresh);
		pPDist.setTitle("Final State Probability Distribution");
		return (pPDist);
	}

	public PieProbDist PieFixPoint(Double ProbThresh, Double CycleThresh) {
		Pattern FixPointNumberMatcher = Pattern.compile("Fixed Points [(](.*)[)]");
		ArrayList<Double> FixPointProb = new ArrayList<Double>();
		ArrayList<String> FixPointStates = new ArrayList<String>();
		try {
			File fixPointFile = new File(fpName);
			BufferedReader bufferFixPointFile = new BufferedReader(new FileReader(fixPointFile));
			String fileLine = new String(bufferFixPointFile.readLine());
			Matcher MatcherFixPoints = FixPointNumberMatcher.matcher(fileLine);
			Integer FixPointNumber = 0;
			if (MatcherFixPoints.matches())
				FixPointNumber = Integer.parseInt(MatcherFixPoints.group(1));
			else
				System.out.println("Did not find fix point number");
			if (FixPointNumber > 0) {
				fileLine = bufferFixPointFile.readLine();
				for (int i = 0; i < FixPointNumber; i++) {
					fileLine = bufferFixPointFile.readLine();
					String[] fileLineSplitted = fileLine.split("\t");
					FixPointProb.add(i, Double.parseDouble(fileLineSplitted[1]));
					FixPointStates.add(i, fileLineSplitted[2]);
				}
			}
			bufferFixPointFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		PieProbDist pieFixPoint = new PieProbDist(FixPointProb, ProbThresh, CycleThresh);
		pieFixPoint.OrderedNames.addAll(FixPointStates);
		if (FixPointStates.size() < pieFixPoint.getData().size())
			pieFixPoint.OrderedNames.add("Cycle(s)");
		pieFixPoint.setTitle("Stationary States Distribution");
		return (pieFixPoint);
	}
}
