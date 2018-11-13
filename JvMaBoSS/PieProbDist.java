package JvMaBoSS;

import javafx.scene.chart.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.transform.*;
import javafx.print.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PieProbDist extends PieChart {
	public ArrayList<String> OrderedNames = new ArrayList<String>();

	public PieProbDist(TreeMap<String, Double> Map4Pie, Double ProbThresh) {
		PieProbDistInnerConstructor(Map4Pie, ProbThresh);
	}

	public PieProbDist(ArrayList<Double> Array4Pie, Double ProbThresh, Double ThreshCycle) { // Add "cycle" if sum of
																								// probabilities <
		// (1-ThreshCycle)
		super();
		this.setStyle("-fx-font-family: Tahoma; -fx-opacity:.99;"); // necessary for printing
		Double Norm = 1 - ThreshCycle;
		for (Integer Index = 1; Index <= Array4Pie.size(); Index++) { // Index start to 1
			if (Array4Pie.get(Index - 1) > ProbThresh) {
				PieChart.Data StateProbData = new PieChart.Data(Index.toString(), Array4Pie.get(Index - 1));
				this.getData().add(StateProbData); // can think of adding if (Array4Pie.get(Index> > ThreshCycle)) ?
				Norm = Norm - Array4Pie.get(Index - 1);
			}
		}
		if (Norm >= 0) {
			PieChart.Data StateProbData = new PieChart.Data("Cycle(s)", Norm);
			this.getData().add(StateProbData);
		}
		this.setPrefSize(1000, 1000);
	}

	public PieProbDist(String probTrajLine, Double ProbThresh) {
		PieProbDistInnerConstructor(this.Map4Pie(probTrajLine), ProbThresh);
	}

	private void PieProbDistInnerConstructor(TreeMap<String, Double> Map4Pie, Double ProbThresh) {
		if (Map4Pie != null) {
			this.setStyle("-fx-font-family: Tahoma; -fx-opacity:.99;");
			for (Map.Entry<String, Double> Slice : Map4Pie.entrySet()) {
				if (Slice.getValue() > ProbThresh) {
					PieChart.Data StateProbData = new PieChart.Data(Slice.getKey(), Slice.getValue());
					OrderedNames.add(Slice.getKey());
					this.getData().add(StateProbData);
				}
			}
			this.setPrefSize(1000, 1000);
		}
	}

	private TreeMap<String, Double> Map4Pie(String probTrajLine) {
		TreeMap<String, Double> treeMap = new TreeMap<String, Double>();
		String[] SplittedLine = probTrajLine.split("\t");
		Pattern letterPattern = Pattern.compile(".*[a-aA-Z_][a-aA-Z0-9_]*.*");
		int FirstStateIndex = -1;
		Matcher StateMatcher;
		do {
			FirstStateIndex++;
			StateMatcher = letterPattern.matcher(SplittedLine[FirstStateIndex]);
		} while ((!StateMatcher.matches()) && (!SplittedLine[FirstStateIndex].equals("<nil>")));
		String[] ProbTrajSplittedLine = Arrays.copyOfRange(SplittedLine, FirstStateIndex, SplittedLine.length);
		for (int Index = 0; Index < ProbTrajSplittedLine.length; Index = Index + 3)
			treeMap.put(ProbTrajSplittedLine[Index], Double.parseDouble(ProbTrajSplittedLine[Index + 1]));
		return (treeMap);
	}

	public Stage StagePie() {
		Stage stagePie = new Stage();
		Button PrintButton = new Button();
		PrintButton.setText("Print");
		VBox vboxPie = new VBox();
		vboxPie.getChildren().addAll(this, PrintButton);
		Scene scenePie = new Scene(vboxPie, 400, 200);
		PrintButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				PrinterJob printJob = PrinterJob.createPrinterJob();
				if (printJob != null && printJob.showPrintDialog(PieProbDist.this.getScene().getWindow())) {
					Printer printer = printJob.getPrinter();
					PageLayout pageLayout = printer.createPageLayout(Paper.A4, PageOrientation.PORTRAIT,
							Printer.MarginType.HARDWARE_MINIMUM);
					double scaleX = .8 * pageLayout.getPrintableWidth()
							/ PieProbDist.this.getBoundsInParent().getWidth();
					double scaleY = .8 * pageLayout.getPrintableHeight()
							/ PieProbDist.this.getBoundsInParent().getHeight();
					Scale scale = new Scale((scaleX < scaleY ? scaleX : scaleY), (scaleX < scaleY ? scaleX : scaleY));
					PieProbDist.this.getTransforms().add(scale);
					boolean printed = printJob.printPage(pageLayout, PieProbDist.this);
					if (printed)
						printJob.endJob();
					else
						System.out.println("print failed");
					PieProbDist.this.getTransforms().remove(scale);
				}
			}
		});
		stagePie.setScene(scenePie);
		stagePie.setHeight(600);
		stagePie.setWidth(600);
		return (stagePie);
	}

	public Stage StageFixPointPie() {
		Stage stageFixPointPie = new Stage();
		Button PrintButton = new Button();
		String FixPointList = new String("Fixed Point(s) List:\n");
		for (Integer Index = 1; Index <= this.OrderedNames.size(); Index++)
			if (!this.OrderedNames.get(Index - 1).contains("Cycle(s"))
				FixPointList = FixPointList + Index.toString() + " " + this.OrderedNames.get(Index - 1) + "\n";
		PrintButton.setText("Print");
		Text FixPointListBox = new Text();
		FixPointListBox.setText(FixPointList);
		VBox vboxPie = new VBox();
		vboxPie.getChildren().addAll(this, PrintButton, FixPointListBox);
		Scene scenePie = new Scene(vboxPie, 400, 200);
		PrintButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				PrinterJob printJob = PrinterJob.createPrinterJob();
				if (printJob != null && printJob.showPrintDialog(PieProbDist.this.getScene().getWindow())) {
					Printer printer = printJob.getPrinter();
					PageLayout pageLayout = printer.createPageLayout(Paper.A4, PageOrientation.PORTRAIT,
							Printer.MarginType.HARDWARE_MINIMUM);
					double scaleX = .8 * pageLayout.getPrintableWidth()
							/ PieProbDist.this.getBoundsInParent().getWidth();
					double scaleY = .8 * pageLayout.getPrintableHeight()
							/ PieProbDist.this.getBoundsInParent().getHeight();
					Scale scale = new Scale((scaleX < scaleY ? scaleX : scaleY), (scaleX < scaleY ? scaleX : scaleY));
					PieProbDist.this.getTransforms().add(scale);
					boolean printed = printJob.printPage(pageLayout, PieProbDist.this);
					if (printed)
						printJob.endJob();
					else
						System.out.println("print failed");
					PieProbDist.this.getTransforms().remove(scale);
				}
			}
		});
		stageFixPointPie.setScene(scenePie);
		stageFixPointPie.setHeight(600);
		stageFixPointPie.setWidth(600);
		return (stageFixPointPie);
	}

}
