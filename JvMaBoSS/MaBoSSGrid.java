package JvMaBoSS;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;

import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.geometry.Insets;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.stage.WindowEvent;

public class MaBoSSGrid extends GridPane {
	BndFx myBndFx = new BndFx();
	CfgFx myCfgFx = new CfgFx();
	ModVarFx myModVarFx = new ModVarFx();
	MutFx myMutFx = new MutFx();
	ExecFx myExecFx = new ExecFx();
	ProbTrajFx myProbTrajFx = new ProbTrajFx();
	NodeTrajFx myNodeTrajFx = new NodeTrajFx();
	PieProbDistFx myPieProbDistFx = new PieProbDistFx();
	PieFixPointFx myPieFixPointFx = new PieFixPointFx();
	InputFilesMBSS MBSSInput;
	OutputMBSS MBSSOutput;
	Image MaBoSSReady;
	Image MaBoSSHappy;
	Image MaBoSSFailed;
	ImageView ViaraLogo = new ImageView();

	public MaBoSSGrid(InputFilesMBSS MInput) {
		this();
		this.MBSSInput = MInput;
		this.myBndFx.BndLabel.setText(new File(MBSSInput.bndName).getAbsolutePath());
		this.myBndFx.BndFileName = new File(MBSSInput.bndName).getAbsolutePath();

		this.myCfgFx.CfgLabel.setText(new File(MBSSInput.cfgName).getAbsolutePath());
		this.myCfgFx.CfgFileName = new File(MBSSInput.cfgName).getAbsolutePath();
		this.myModVarFx.buttonModVar.setDisable(false);
		this.myMutFx.buttonMut.setDisable(false);
	}

	public MaBoSSGrid() {
		try {
			MaBoSSReady = new Image(getClass().getResource("/images/viara.jpg").toURI().toString());
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			MaBoSSHappy = new Image(getClass().getResource("/images/IMGP2358.jpg").toURI().toString());
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			MaBoSSFailed = new Image(getClass().getResource("/images/P4110054.JPG").toURI().toString());
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		this.setAlignment(Pos.CENTER);
		this.setHgap(15);
		this.setVgap(15);
		this.setPadding(new Insets(25, 25, 25, 25));

		this.add(new Label("Boolean Network Descriptor (.bnd) File: "), 0, 0);
		this.add(myBndFx.BndField, 1, 0, 2, 1);
		this.add(myBndFx.BndLabel, 0, 1, 5, 1);
		// myBndFx.BndLabel.setStyle("-fx-text-fill: red ;"
		// +"-fx-background-color: white ;"); // Coloring grid?
		this.add(myBndFx.ChooseBnd, 3, 0);

		this.add(new Label("Configuration (.cfg) File: "), 0, 4);
		this.add(myCfgFx.CfgField, 1, 4, 2, 1);
		this.add(myCfgFx.CfgLabel, 0, 5, 5, 1);
		this.add(myCfgFx.ChooseCfg, 3, 4);

		this.add(myMutFx.buttonMut, 4, 0);

		this.add(myModVarFx.buttonModVar, 4, 4);
		this.add(myModVarFx.labelModVar, 3, 5, 3, 1);
		
		this.add(ViaraLogo, 4, 13, 3, 3);

		this.add(new Label("Executable Name: "), 0, 7);
		this.add(myExecFx.ExecField, 1, 7, 2, 1);
		this.add(myExecFx.ExecLabel, 0, 8, 5, 1);
		this.add(myExecFx.ChooseExec, 3, 7);
		this.add(new Label("Output Prefix: "), 0, 11);
		this.add(myExecFx.OutField, 1, 11);
		this.add(myExecFx.OutLabel, 0, 12, 4, 1);
		this.add(myExecFx.ExecResult, 2, 15, 4, 1);
		this.add(myExecFx.runButton, 0, 15, 2, 1);

		this.add(myPieProbDistFx.PieButton, 0, 16, 2, 1);
		this.add(myPieProbDistFx.PieThreshText, 2, 16);
		this.add(myPieProbDistFx.PieThreshField, 3, 16);

		this.add(myNodeTrajFx.NodeTrajButton, 0, 17, 2, 1);
		this.add(myNodeTrajFx.NodeTrajThreshText, 2, 17);
		this.add(myNodeTrajFx.NodeTrajThreshField, 3, 17);
		this.add(myNodeTrajFx.NodeTrajFileButton, 4, 17, 3, 1);
		this.add(myNodeTrajFx.NodeTrajFileDone, 7, 17);

		this.add(myProbTrajFx.TrajButton, 0, 18, 2, 1);
		this.add(myProbTrajFx.TrajThreshText, 2, 18);
		this.add(myProbTrajFx.TrajThreshField, 3, 18);
		this.add(myProbTrajFx.TrajFileButton, 4, 18, 3, 1);
		this.add(myProbTrajFx.TrajFileDone, 7, 18);

		this.add(myPieFixPointFx.FixPointButton, 0, 19, 2, 1);
		this.add(myPieFixPointFx.FpThreshText, 2, 19);
		this.add(myPieFixPointFx.FpThreshField, 3, 19);
		this.add(myPieFixPointFx.CycleThreshText, 2, 20);
		this.add(myPieFixPointFx.CycleThreshField, 3, 20);
	}

	private class BndFx {
		String BndFileName = new String();
		Label BndLabel = new Label("?");;
		TextField BndField = new TextField();
		Button ChooseBnd = new Button("Choose");
		FileChooser BndFileChooser = new FileChooser();

		private BndFx() {
			BndField.setOnKeyPressed(new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent keyEvent) {
					if (keyEvent.getCode() == KeyCode.ENTER) {
						BndFileName = BndField.getText();
						if (MaBoSSGrid.this.myCfgFx.CfgFileName.length() > 0) {
							MaBoSSGrid.this.MBSSInput = new InputFilesMBSS(BndFileName,
									MaBoSSGrid.this.myCfgFx.CfgFileName);
							MaBoSSGrid.this.myModVarFx.buttonModVar.setDisable(false);
							MaBoSSGrid.this.myModVarFx.modVarLabelProperty.set("");
							MaBoSSGrid.this.myMutFx.buttonMut.setDisable(false);
							if (new File(MaBoSSGrid.this.MBSSInput.bndName).exists()) {
								BndLabel.setText(new File(MaBoSSGrid.this.MBSSInput.bndName).getAbsolutePath());
								if ((MaBoSSGrid.this.myExecFx.MaBoSSExecName.length() > 0)
										&& (MaBoSSGrid.this.myExecFx.MBSSOutPrefix.length() > 0)) {
									MaBoSSGrid.this.myExecFx.runButton.setDisable(false);
									ViaraLogo.setImage(MaBoSSReady);
									ViaraLogo.setPreserveRatio(true);
									ViaraLogo.setFitHeight(100);

								}
							} else {
								BndLabel.setText("??-");
								BndFileName = "";
								MaBoSSGrid.this.myExecFx.runButton.setDisable(true);
								ViaraLogo.setImage(null);
							}
							BndLabel.setWrapText(true);
						} else {
							if (new File(BndFileName).exists())
								BndLabel.setText(new File(BndFileName).getAbsolutePath());
							else {
								BndLabel.setText("??");
								BndFileName = "";
							}
							BndLabel.setWrapText(true);
						}
					}
				}
			});
			ChooseBnd.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(final ActionEvent e) {
					Stage FileStage = new Stage();
					File file = BndFileChooser.showOpenDialog(FileStage);
					if (file != null) {
						BndField.setText(file.getName());
						BndFileName = file.getAbsolutePath();
						BndLabel.setText(BndFileName);
						BndLabel.setWrapText(true);
						if (MaBoSSGrid.this.myCfgFx.CfgFileName.length() > 0) {
							MaBoSSGrid.this.MBSSInput = new InputFilesMBSS(BndFileName,
									MaBoSSGrid.this.myCfgFx.CfgFileName);

							MaBoSSGrid.this.myModVarFx.buttonModVar.setDisable(false);
							MaBoSSGrid.this.myModVarFx.modVarLabelProperty.set("");
							MaBoSSGrid.this.myMutFx.buttonMut.setDisable(false);
							if ((MaBoSSGrid.this.myExecFx.MaBoSSExecName.length() > 0)
									&& (MaBoSSGrid.this.myExecFx.MBSSOutPrefix.length() > 0)) {
								MaBoSSGrid.this.myExecFx.runButton.setDisable(false);
								ViaraLogo.setImage(MaBoSSReady);
								ViaraLogo.setPreserveRatio(true);
								ViaraLogo.setFitHeight(100);
							}
						}
					}
				}
			});
		}
	}

	private class CfgFx {
		String CfgFileName = new String();
		Label CfgLabel;
		TextField CfgField = new TextField();
		Button ChooseCfg = new Button("Choose");
		FileChooser CfgFileChooser = new FileChooser();

		private CfgFx() {
			CfgLabel = new Label("?");
			CfgField.setOnKeyPressed(new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent keyEvent) {
					if (keyEvent.getCode() == KeyCode.ENTER) {
						CfgFileName = CfgField.getText();
						if (MaBoSSGrid.this.myBndFx.BndFileName.length() > 0) {
							MBSSInput = new InputFilesMBSS(MaBoSSGrid.this.myBndFx.BndFileName, CfgFileName);
							MaBoSSGrid.this.myModVarFx.buttonModVar.setDisable(false);
							MaBoSSGrid.this.myModVarFx.modVarLabelProperty.set("");
							MaBoSSGrid.this.myMutFx.buttonMut.setDisable(false);
							if (new File(MaBoSSGrid.this.MBSSInput.cfgName).exists()) {
								CfgLabel.setText(new File(MaBoSSGrid.this.MBSSInput.cfgName).getAbsolutePath());
								if ((MaBoSSGrid.this.myExecFx.MaBoSSExecName.length() > 0)
										&& (MaBoSSGrid.this.myExecFx.MBSSOutPrefix.length() > 0)) {
									MaBoSSGrid.this.myExecFx.runButton.setDisable(false);
									ViaraLogo.setImage(MaBoSSReady);
									ViaraLogo.setPreserveRatio(true);
									ViaraLogo.setFitHeight(100);
								}
							} else {
								CfgLabel.setText("??");
								CfgFileName = "";
								MaBoSSGrid.this.myExecFx.runButton.setDisable(true);
								ViaraLogo.setImage(null);
							}
							CfgLabel.setWrapText(true);
						} else {
							if (new File(CfgFileName).exists())
								CfgLabel.setText(new File(CfgFileName).getAbsolutePath());
							else {
								CfgLabel.setText("??");
								CfgFileName = "";
							}
							CfgLabel.setWrapText(true);
						}
					}
				}
			});
			ChooseCfg.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(final ActionEvent e) {
					Stage FileStage = new Stage();
					File file = CfgFileChooser.showOpenDialog(FileStage);
					if (file != null) {
						CfgField.setText(file.getName());
						CfgFileName = file.getAbsolutePath();
						CfgLabel.setText(CfgFileName);
						CfgLabel.setWrapText(true);
						if (MaBoSSGrid.this.myBndFx.BndFileName.length() > 0) {
							MaBoSSGrid.this.MBSSInput = new InputFilesMBSS(MaBoSSGrid.this.myBndFx.BndFileName,
									CfgFileName);

							MaBoSSGrid.this.myModVarFx.buttonModVar.setDisable(false);
							MaBoSSGrid.this.myModVarFx.modVarLabelProperty.set("");
							MaBoSSGrid.this.myMutFx.buttonMut.setDisable(false);
							if ((MaBoSSGrid.this.myExecFx.MaBoSSExecName.length() > 0)
									&& (MaBoSSGrid.this.myExecFx.MBSSOutPrefix.length() > 0)) {
								MaBoSSGrid.this.myExecFx.runButton.setDisable(false);
								ViaraLogo.setImage(MaBoSSReady);
								ViaraLogo.setPreserveRatio(true);
								ViaraLogo.setFitHeight(100);
							}
						}
					}
				}
			});
		}
	}

	private class ExecFx {
		String MaBoSSExecName = new String();
		Exec MBSSExec;
		String MBSSOutPrefix = new String();
		Label ExecLabel;
		Label OutLabel;
		Label ExecResult;
		TextField ExecField = new TextField();
		TextField OutField = new TextField();
		FileChooser ExecFileChooser = new FileChooser();
		Button ChooseExec = new Button("Choose");
		Button runButton;
		Image MaBoSSLogoImage;
		ImageView MaBoSSLogo;
		

		private ExecFx() {
			ExecLabel = new Label("?");
			ExecField.setOnKeyPressed(new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent keyEvent) {
					if (keyEvent.getCode() == KeyCode.ENTER) {
						MaBoSSExecName = ExecField.getText();
						if (new File(MaBoSSExecName).exists())
							MaBoSSExecName = (new File(MaBoSSExecName).getAbsolutePath());
						try {
							Runtime commandPrompt = Runtime.getRuntime();
							Process process = commandPrompt.exec(MaBoSSExecName + " -h ");
							process.waitFor();
							ExecLabel.setText(MaBoSSExecName);
							ExecLabel.setWrapText(true);
							if ((MaBoSSGrid.this.myBndFx.BndFileName.length() > 0)
									&& (MaBoSSGrid.this.myCfgFx.CfgFileName.length() > 0)
									&& (MaBoSSExecName.length() > 0) && (MBSSOutPrefix.length() > 0)) {
								runButton.setDisable(false);
								ViaraLogo.setImage(MaBoSSReady);
								ViaraLogo.setPreserveRatio(true);
								ViaraLogo.setFitHeight(100);
							}

						} catch (Exception ExExec) {
							ExExec.printStackTrace();
							ExecLabel.setText(MaBoSSExecName + " is not a MaBoSS executable");
							runButton.setDisable(true);
							ViaraLogo.setImage(null);
						}
					}
				}
			});
			ChooseExec.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(final ActionEvent e) {
					Stage FileStage = new Stage();
					File file = ExecFileChooser.showOpenDialog(FileStage);
					if (file != null) {
						ExecField.setText(file.getName());
						MaBoSSExecName = file.getAbsolutePath();
						try {
							Runtime commandPrompt = Runtime.getRuntime();
							Process process = commandPrompt.exec(MaBoSSExecName + " -h ");
							process.waitFor();
							ExecLabel.setText(MaBoSSExecName);
							ExecLabel.setWrapText(true);
							if ((MaBoSSGrid.this.myBndFx.BndFileName.length() > 0)
									&& (MaBoSSGrid.this.myCfgFx.CfgFileName.length() > 0)
									&& (MBSSOutPrefix.length() > 0)) {
								runButton.setDisable(false);
								ViaraLogo.setImage(MaBoSSReady);
								ViaraLogo.setPreserveRatio(true);
								ViaraLogo.setFitHeight(100);
							}
						} catch (Exception ExChoosExec) {
							ExChoosExec.printStackTrace();
							ExecLabel.setText(MaBoSSExecName + " is not a MaBoSS executable");
							runButton.setDisable(true);
							ViaraLogo.setImage(null);
						}
					}
				}
			});
			OutLabel = new Label("?");
			OutField.setOnKeyPressed(new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent keyEvent) {
					if (keyEvent.getCode() == KeyCode.ENTER) {
						MBSSOutPrefix = OutField.getText();
						OutLabel.setText(MBSSOutPrefix);
						OutLabel.setWrapText(true);
						if ((MaBoSSGrid.this.myBndFx.BndFileName.length() > 0)
								&& (MaBoSSGrid.this.myCfgFx.CfgFileName.length() > 0) && (MaBoSSExecName.length() > 0)
								&& (MBSSOutPrefix.length() > 0)) {
							runButton.setDisable(false);
							ViaraLogo.setImage(MaBoSSReady);
							ViaraLogo.setPreserveRatio(true);
							ViaraLogo.setFitHeight(100);
						} else {
							runButton.setDisable(true);
							ViaraLogo.setImage(null);
							;
						}
					}
				}
			});
			ExecResult = new Label("");
			try {
				MaBoSSLogoImage = new Image(getClass().getResource("/images/maboss_logo.jpg").toURI().toString());
				} catch (URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			MaBoSSLogo = new ImageView(MaBoSSLogoImage);
			MaBoSSLogo.setPreserveRatio(true);
			MaBoSSLogo.setFitHeight(70);
			
			runButton = new Button("Launch",MaBoSSLogo);
			runButton.setStyle(" -fx-background-color: linear-gradient(#ff5400, #be1d00);"
							+ "    -fx-background-radius: 15;" + "    -fx-background-insets: 0;" + "    -fx-text-fill: white;"
							+ "    -fx-font-size: 18px;");
			
			runButton.setAlignment(Pos.CENTER);
			runButton.setDisable(true);
			runButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					ExecResult.setText("Running...");
					runButton.setDisable(true);
					final Service<Void> calculateService = new Service<Void>() {
						@Override
						protected Task<Void> createTask() {
							return new Task<Void>() {
								@Override
								protected Void call() throws Exception {
									if (MaBoSSGrid.this.MBSSInput.cfgModSuffix.length() > 0)
										MBSSExec = new Exec(MaBoSSGrid.this.MBSSInput, MaBoSSExecName, MBSSOutPrefix,
												true);
									else
										MBSSExec = new Exec(MaBoSSGrid.this.MBSSInput, MaBoSSExecName, MBSSOutPrefix,
												false);

									MaBoSSGrid.this.MBSSOutput = MBSSExec.output;
									return null;
								}
							};
						}
					};
					calculateService.stateProperty().addListener(new ChangeListener<Worker.State>() {
						@Override
						public void changed(ObservableValue<? extends Worker.State> observableValue,
								Worker.State oldValue, Worker.State newValue) {
							switch (newValue) {
							case FAILED:
							case CANCELLED:
							case SUCCEEDED:
								if (MBSSOutput == null) {
									ExecResult.setText("Failed!");
									ViaraLogo.setImage(MaBoSSFailed);
									ViaraLogo.setPreserveRatio(true);
									ViaraLogo.setFitHeight(100);
									MaBoSSGrid.this.myPieProbDistFx.PieButton.setText("Pie of Final States");
									MaBoSSGrid.this.myPieProbDistFx.PieButton.setDisable(true);
									MaBoSSGrid.this.myNodeTrajFx.NodeTrajButton.setText("Trajectory of Node Prob.");
									MaBoSSGrid.this.myNodeTrajFx.NodeTrajButton.setDisable(true);
									MaBoSSGrid.this.myNodeTrajFx.NodeTrajFileButton.setDisable(true);
									MaBoSSGrid.this.myNodeTrajFx.NodeTrajFileButton.setText("Trajectory File");
									MaBoSSGrid.this.myNodeTrajFx.NodeTrajFileDone.setText("");
									MaBoSSGrid.this.myProbTrajFx.TrajButton.setText("Trajectory of States Prob.");
									MaBoSSGrid.this.myProbTrajFx.TrajButton.setDisable(true);
									MaBoSSGrid.this.myProbTrajFx.TrajFileButton.setText("Trajectory File");
									MaBoSSGrid.this.myProbTrajFx.TrajFileButton.setDisable(true);
									MaBoSSGrid.this.myProbTrajFx.TrajFileDone.setText("");
									MaBoSSGrid.this.myPieFixPointFx.FixPointButton.setText("Pie of Stationary States");
									MaBoSSGrid.this.myPieFixPointFx.FixPointButton.setDisable(true);
									runButton.setDisable(true);
								} else {
									ExecResult.setText(MBSSExec.outPrefix + " has succeeded!");
									ViaraLogo.setImage(MaBoSSHappy);
									ViaraLogo.setPreserveRatio(true);
									ViaraLogo.setFitHeight(100);
									MaBoSSGrid.this.myPieProbDistFx.PieButton
											.setText("Pie of Final States for " + MBSSExec.outPrefix);
									MaBoSSGrid.this.myPieProbDistFx.PieButton.setDisable(false);
									MaBoSSGrid.this.myNodeTrajFx.NodeTrajButton
											.setText("Trajectory of Node Prob. for " + MBSSExec.outPrefix);
									MaBoSSGrid.this.myNodeTrajFx.NodeTrajButton.setDisable(false);
									MaBoSSGrid.this.myNodeTrajFx.NodeTrajFileButton.setDisable(false);
									MaBoSSGrid.this.myNodeTrajFx.NodeTrajFileButton
											.setText("Trajectory File for " + MBSSExec.outPrefix);
									MaBoSSGrid.this.myNodeTrajFx.NodeTrajFileDone.setText("");
									MaBoSSGrid.this.myProbTrajFx.TrajButton
											.setText("Trajectory of States Prob. for " + MBSSExec.outPrefix);
									MaBoSSGrid.this.myProbTrajFx.TrajButton.setDisable(false);
									MaBoSSGrid.this.myProbTrajFx.TrajFileButton
											.setText("Trajectory File for " + MBSSExec.outPrefix);
									MaBoSSGrid.this.myProbTrajFx.TrajFileButton.setDisable(false);
									MaBoSSGrid.this.myProbTrajFx.TrajFileDone.setText("");
									MaBoSSGrid.this.myPieFixPointFx.FixPointButton
											.setText("Pie of Stationary States for " + MBSSExec.outPrefix);
									MaBoSSGrid.this.myPieFixPointFx.FixPointButton.setDisable(false);
									runButton.setDisable(false);
								}
							}
						}
					});
					calculateService.start();
				}
			});
		}
	}

	private class PieProbDistFx {
		Double PieThresh = new Double(0.01);
		Label PieThreshText = new Label("Probability Threshold: 0.01");
		TextField PieThreshField = new TextField("0.01");
		Button PieButton = new Button();

		private PieProbDistFx() {
			PieButton.setText("Pie of Final States");
			PieButton.setAlignment(Pos.CENTER);
			PieButton.setDisable(true);
			PieButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					PieProbDist PieFigure = MaBoSSGrid.this.MBSSOutput.PieLastProbTrj(PieThresh);
					Stage StagePieProbDist = PieFigure.StagePie();
					StagePieProbDist.setTitle("Final State Probability Distribution of " + MBSSOutput.Name);
					StagePieProbDist.show();
				}
			});
			PieThreshField.setOnKeyPressed(new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent keyEvent) {
					if (keyEvent.getCode() == KeyCode.ENTER)
						try {
							Double Thresh;
							Thresh = Double.parseDouble(PieThreshField.getText());
							if (Thresh < 1) {
								PieThresh = Thresh;
								PieThreshText.setText("Probability Threshold: " + PieThresh);
							}
						} catch (NumberFormatException e) {
							e.printStackTrace(); // prints error
						}
				}
			});
		}
	}

	private class NodeTrajFx {
		Double NodeTrajThresh = new Double(0.01);
		Label NodeTrajThreshText = new Label("Probability Threshold: 0.01");
		TextField NodeTrajThreshField = new TextField("0.01");
		Button NodeTrajButton = new Button();
		Button NodeTrajFileButton = new Button();
		Label NodeTrajFileDone = new Label("");

		private NodeTrajFx() {
			NodeTrajButton.setText("Trajectory of Node Prob.");
			NodeTrajButton.setAlignment(Pos.CENTER);
			NodeTrajButton.setDisable(true);
			NodeTrajButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					OutputTablesMBSS myMBSSOutputTables = new OutputTablesMBSS(MaBoSSGrid.this.MBSSOutput);
					Stage StageNodeTrajProbDist = myMBSSOutputTables.NodeTrajTable.NodeTrajPlotProbDist(NodeTrajThresh)
							.StagePlotProbDist();
					StageNodeTrajProbDist.setTitle("Node Probability Trajectory of " + MBSSOutput.Name);
					StageNodeTrajProbDist.show();
				}
			});
			NodeTrajThreshField.setOnKeyPressed(new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent keyEvent) {
					if (keyEvent.getCode() == KeyCode.ENTER)
						try {
							Double Thresh;
							Thresh = Double.parseDouble(NodeTrajThreshField.getText());
							if (Thresh < 1) {
								NodeTrajThresh = Thresh;
								NodeTrajThreshText.setText("Probability Threshold: " + NodeTrajThresh);
							}
						} catch (NumberFormatException e) {
							e.printStackTrace(); // prints error
						}
				}
			});
			// file of node probability table
			NodeTrajFileButton.setText("Trajectory File");
			NodeTrajFileButton.setAlignment(Pos.CENTER);
			NodeTrajFileButton.setDisable(true);
			NodeTrajFileButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					OutputTablesMBSS MBSSOutputTables = new OutputTablesMBSS(MaBoSSGrid.this.MBSSOutput);
					MBSSOutputTables.WriteNodeProbTrajTableFile();
					NodeTrajFileDone.setText("Done");
				}
			});
		}
	}

	private class ProbTrajFx {
		Double TrajThresh = new Double(0.01);
		Label TrajThreshText = new Label("Probability Threshold: 0.01");
		TextField TrajThreshField = new TextField("0.01");
		Button TrajButton = new Button();
		Button TrajFileButton = new Button();
		Label TrajFileDone = new Label("");

		private ProbTrajFx() {
			TrajButton.setText("Trajectory of States Prob.");
			TrajButton.setAlignment(Pos.CENTER);
			TrajButton.setDisable(true);
			TrajButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					OutputTablesMBSS myMBSSOutputTables = new OutputTablesMBSS(MaBoSSGrid.this.MBSSOutput);
					Stage StageTrajProbDist = myMBSSOutputTables.TrajTable.TrajPlotProbDist(TrajThresh)
							.StagePlotProbDist();
					StageTrajProbDist.setTitle("Probability Distribution Trajectory of " + MBSSOutput.Name);
					StageTrajProbDist.show();
				}
			});
			TrajThreshField.setOnKeyPressed(new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent keyEvent) {
					if (keyEvent.getCode() == KeyCode.ENTER)
						try {
							Double Thresh;
							Thresh = Double.parseDouble(TrajThreshField.getText());
							if (Thresh < 1) {
								TrajThresh = Thresh;
								TrajThreshText.setText("Probability Threshold: " + TrajThresh);
							}
						} catch (NumberFormatException e) {
							e.printStackTrace(); // prints error
						}
				}
			});
			// file of probability distribution table
			TrajFileButton.setText("Trajectory File");
			TrajFileButton.setAlignment(Pos.CENTER);
			TrajFileButton.setDisable(true);
			TrajFileButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					OutputTablesMBSS MBSSOutputTables = new OutputTablesMBSS(MaBoSSGrid.this.MBSSOutput);
					MBSSOutputTables.WriteProbTrajTableFile();
					TrajFileDone.setText("Done");
				}
			});
		}
	}

	private class PieFixPointFx {

		Double FpThresh = new Double(0.0);
		Double CycleThresh = new Double(0.1);
		Label FpThreshText = new Label("Fixed Point Probability Threshold: 0.0");
		Label CycleThreshText = new Label("Cycle Probability Threshold: 0.01");
		TextField FpThreshField = new TextField("0.0");
		TextField CycleThreshField = new TextField("0.01");
		Button FixPointButton = new Button();

		private PieFixPointFx() {
			FixPointButton.setText("Pie of Stationary States");
			FixPointButton.setAlignment(Pos.CENTER);
			FixPointButton.setDisable(true);
			FixPointButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					PieProbDist PieFigure = MaBoSSGrid.this.MBSSOutput.PieFixPoint(FpThresh, CycleThresh);
					// PieProbDist PieFigure = MaBoSSGrid.this.MBSSOutput.PieLastProbTrj(PieThresh);
					Stage StagePieFixPoint = PieFigure.StageFixPointPie();
					// StageFixPoint fixPointStage = new StageFixPoint(FpThresh, CycleThresh);
					StagePieFixPoint.setTitle("Stationary State Probability Distribution of " + MBSSOutput.Name);
					StagePieFixPoint.show();
				}
			});
			FpThreshField.setOnKeyPressed(new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent keyEvent) {
					if (keyEvent.getCode() == KeyCode.ENTER)
						try {
							Double Thresh;
							Thresh = Double.parseDouble(FpThreshField.getText());
							if (Thresh < 1) {
								FpThresh = Thresh;
								FpThreshText.setText("Fixed Points Probability Threshold: " + FpThresh);
							}
						} catch (NumberFormatException e) {
							e.printStackTrace(); // prints error
						}
				}
			});
			CycleThreshField.setOnKeyPressed(new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent keyEvent) {
					if (keyEvent.getCode() == KeyCode.ENTER)
						try {
							Double Thresh;
							Thresh = Double.parseDouble(CycleThreshField.getText());
							if (Thresh < 1) {
								CycleThresh = Thresh;
								CycleThreshText.setText("Cycle Probability Threshold: " + CycleThresh);
							}
						} catch (NumberFormatException e) {
							e.printStackTrace(); // prints error
						}
				}
			});
		}
	}

	private class ModVarFx {
		Button buttonModVar = new Button("Change External Variables");
		SimpleStringProperty modVarLabelProperty = new SimpleStringProperty("");
		Label labelModVar = new Label("No change");

		private ModVarFx() {
			modVarLabelProperty.addListener(new ChangeListener<String>() {
				@Override
				public void changed(ObservableValue<? extends String> ov, String oldVal, String newVal) {
					if (!MaBoSSGrid.this.myExecFx.runButton.isDisable()) {
						ViaraLogo.setImage(MaBoSSReady);
						
					}
					if (newVal.length() > 0) {
						labelModVar.setText("Variable change in cfg file: "
								+ MBSSInput.cfgName.substring(MBSSInput.cfgName.lastIndexOf("/") + 1).replace(".cfg",
										"_" + newVal + ".cfg"));
						labelModVar.setWrapText(true);
					} else
						labelModVar.setText("No change");

				}
			});
			buttonModVar.setDisable(true);
			buttonModVar.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent actionEvent) {
					Stage myStageTable = MBSSInput.TableEditModVarInput(modVarLabelProperty);
					myStageTable.setTitle("External Variables in " + MBSSInput.cfgName);
					myStageTable.setWidth(850);
					myStageTable.setHeight(650);
					myStageTable.show();
				}
			});
		}

	}

	private class MutFx {
		Button buttonMut = new Button("Build mutations");

		private MutFx() {
			buttonMut.setDisable(true);
			buttonMut.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent actionEvent) {
					SimpleObjectProperty<InputFilesMBSS> MBSSInputMut = new SimpleObjectProperty<InputFilesMBSS>();
					Stage myStageMutInput = MBSSInput.StageInputMutInput(MBSSInputMut);
					myStageMutInput.setOnHiding(new EventHandler<WindowEvent>() {
						public void handle(WindowEvent we) {
							if (MBSSInputMut.get() != null) {
								Stage mutStage = new Stage();
								MaBoSSGrid mutGrid = new MaBoSSGrid(MBSSInputMut.get());
								Scene scene = new Scene(new ScrollPane(mutGrid), 1200, 750);
								mutStage.setTitle("MaBoSS Graphical Interface, mutations generated");
								mutStage.setScene(scene);
								mutStage.setX(MaBoSSGrid.this.getScene().getWindow().getX() + 50);
								mutStage.setY(MaBoSSGrid.this.getScene().getWindow().getY() + 50);
								mutStage.show();
							}
						}
					});
					myStageMutInput.setTitle("Select mutations");
					myStageMutInput.show();
				}
			});
		}
	}
}
