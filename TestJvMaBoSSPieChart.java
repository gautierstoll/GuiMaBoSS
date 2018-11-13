import JvMaBoSS.*;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.print.PrinterJob;
import javafx.scene.control.Button;

public class TestJvMaBoSSPieChart extends Application {
	private VBox vboxPie;
	private PieProbDist MyPieProbDist;

	@Override
	public void start(Stage PieStage) throws Exception {
		PieStage.setTitle("Pie Chart of probability distribution");
		OutputMBSS MyOutput = new OutputMBSS("testout");
		MyPieProbDist = MyOutput.PieLastProbTrj(0.01);
		//MyPieProbDist.setPrefSize(1000, 1000);
		
		vboxPie = new VBox();
		Button PrintButton = new Button();
		PrintButton.setText("Print");
		//vboxPie.getChildren().addAll(MyPieProbDist);
		vboxPie.getChildren().addAll(MyPieProbDist, PrintButton);
		Scene scenePie = new Scene(vboxPie,1000,1000);
		
		PrintButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				PrinterJob printJob = PrinterJob.createPrinterJob();
				if (printJob != null && printJob.showPrintDialog(MyPieProbDist.getScene().getWindow())) {
					System.out.println("Color:" + printJob.getPrinter().getPrinterAttributes().getDefaultPrintColor());
					OutputMBSS MyOutput = new OutputMBSS("testout");
					MyPieProbDist = MyOutput.PieLastProbTrj(0.01);
					MyPieProbDist.getStylesheets().add("pie.css");
					boolean printed = printJob.printPage(MyPieProbDist);
					if (printed)
						printJob.endJob();
					else
						System.out.println("print failed");
				}
			}
		});

		PieStage.setScene(scenePie);
		PieStage.setHeight(500);
		PieStage.setWidth(500);
		PieStage.show();	

	}

	public static void main(String[] args) {
		Application.launch(args);
	}

	private void OpenPieChart() {
		Stage PieStage = new Stage();
		PieStage.setTitle("Pie Chart of probability distribution");
		OutputMBSS MyOutput = new OutputMBSS("testout");
		MyPieProbDist = MyOutput.PieLastProbTrj(0.01);
		vboxPie = new VBox();
		Button PrintButton = new Button();
		PrintButton.setText("Print");
		vboxPie.getChildren().addAll(MyPieProbDist, PrintButton);
		Scene scenePie = new Scene(vboxPie, 400, 200);
		scenePie.getStylesheets().add("pie.css");
		PrintButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				PrinterJob printJob = PrinterJob.createPrinterJob();
				if (printJob != null && printJob.showPrintDialog(MyPieProbDist.getScene().getWindow())) {
					System.out.println("Color:" + printJob.getPrinter().getPrinterAttributes().getDefaultPrintColor());
					OutputMBSS MyOutput = new OutputMBSS("testout");
					MyPieProbDist = MyOutput.PieLastProbTrj(0.01);
					MyPieProbDist.getStylesheets().add("pie.css");
					boolean printed = printJob.printPage(MyPieProbDist);
					if (printed)
						printJob.endJob();
					else
						System.out.println("print failed");
				}
			}
		});

		PieStage.setScene(scenePie);
		PieStage.setHeight(600);
		PieStage.setWidth(600);
		PieStage.show();
	}
}