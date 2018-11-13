import JvMaBoSS.*;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;

public class TestJvMaBoSSPlotProbTraj extends Application {
	PlotProbDist MyPlotProbTraj;
	VBox vboxProbTraj;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Time dependant probablities of states");
		OutputMBSS MyOutput = new OutputMBSS("TestFate");
		OutputTablesMBSS MyOutputTables = new OutputTablesMBSS(MyOutput);
		MyPlotProbTraj = MyOutputTables.TrajTable.TrajPlotProbDist(0.01);
		vboxProbTraj = new VBox();
		Button PrintButton = new Button();
		PrintButton.setText("Print");
		vboxProbTraj.getChildren().addAll(MyPlotProbTraj,PrintButton);
		Scene sceneProbTraj = new Scene(vboxProbTraj, 400, 500);
		//sceneProbTraj.getStylesheets().add("line.css");
		PrintButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				PrinterJob printJob = PrinterJob.createPrinterJob();
				if (printJob !=null && printJob.showPrintDialog(MyPlotProbTraj.getScene().getWindow())) {
					System.out.println("Color:"+printJob.getPrinter().getPrinterAttributes().getDefaultPrintColor());
				boolean printed = printJob.printPage(MyPlotProbTraj);
				if (printed) printJob.endJob();
				else System.out.println("print failed");
				}
			}
		});
		primaryStage.setScene(sceneProbTraj);
		primaryStage.show();
	}
	public static void main(String[] args) {
		Application.launch(args);
	}
}