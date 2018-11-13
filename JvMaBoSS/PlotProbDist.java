package JvMaBoSS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.text.Font;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.transform.*;
import javafx.print.*;

public class PlotProbDist extends LineChart<Number, Number> {
	public PlotProbDist(ArrayList<Double> XAxisArray, HashMap<String, ArrayList<Double>> probTraj, Double ProbThresh) {
		super(new NumberAxis(), new NumberAxis());
		this.getXAxis().setTickLabelRotation(90);
		this.getXAxis().setLabel("Time");
		this.getXAxis().tickLabelFontProperty().set(Font.font("Tahoma")); // necessary for printing
		this.getYAxis().setLabel("Probability");
		this.getYAxis().tickLabelFontProperty().set(Font.font("Tahoma")); // necessary for printing
		this.setStyle("-fx-font-family: Tahoma;");
		this.setCreateSymbols(false);
		TreeMap<String, ArrayList<Double>> probTrajOrderName = new TreeMap<String, ArrayList<Double>>();
		for (HashMap.Entry<String, ArrayList<Double>> NodeProbArray : probTraj.entrySet()) {
			boolean BelowThresh = true;
			for (int Index = 0; Index < NodeProbArray.getValue().size(); Index++)
				if (NodeProbArray.getValue().get(Index) > ProbThresh)
					BelowThresh = false;
			if (!BelowThresh) {
				probTrajOrderName.put(NodeProbArray.getKey(), NodeProbArray.getValue());
			}
		}
		for (Map.Entry<String, ArrayList<Double>> NodeProbArray : probTrajOrderName.entrySet()) {
			XYChart.Series<Number, Number> seriesState = new XYChart.Series<Number, Number>();
			for (int Index = 0; Index < XAxisArray.size(); Index++)
				seriesState.setName(NodeProbArray.getKey());
			for (int Index = 0; Index < XAxisArray.size(); Index++)
				seriesState.getData().add(
						new XYChart.Data<Number, Number>(XAxisArray.get(Index), NodeProbArray.getValue().get(Index)));
			this.getData().add(seriesState);
			this.setPrefSize(1000, 1000);
		}
	}

	public Stage StagePlotProbDist() {
		Stage stagePlotProbDist = new Stage();
		VBox vboxProbTraj = new VBox();
		Button PrintButton = new Button();
		PrintButton.setText("Print");
		vboxProbTraj.getChildren().addAll(this, PrintButton);
		Scene sceneProbTraj = new Scene(vboxProbTraj, 400, 500);
		PrintButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				PrinterJob printJob = PrinterJob.createPrinterJob();
				if (printJob != null && printJob.showPrintDialog(PlotProbDist.this.getScene().getWindow())) {
					Printer printer = printJob.getPrinter();
					PageLayout pageLayout = printer.createPageLayout(Paper.A4, PageOrientation.PORTRAIT,
							Printer.MarginType.HARDWARE_MINIMUM);
					double scaleX = .8 * pageLayout.getPrintableWidth()
							/ PlotProbDist.this.getBoundsInParent().getWidth();
					double scaleY = .8 * pageLayout.getPrintableHeight()
							/ PlotProbDist.this.getBoundsInParent().getHeight();
					Scale scale = new Scale((scaleX < scaleY ? scaleX : scaleY), (scaleX < scaleY ? scaleX : scaleY));
					PlotProbDist.this.getTransforms().add(scale);
					boolean printed = printJob.printPage(PlotProbDist.this);
					if (printed)
						printJob.endJob();
					else
						System.out.println("print failed");
					PlotProbDist.this.getTransforms().remove(scale);
				}
			}
		});
		stagePlotProbDist.setScene(sceneProbTraj);
		return (stagePlotProbDist);
	}
}
