package JvMaBoSS;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import javafx.scene.control.TextField;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class InputFilesMBSS {
	public final String bndName;
	public final String cfgName;
	public final HashSet<String> nodeSet = new HashSet<String>();
	public final HashMap<String, String> extVarSet = new HashMap<String, String>(); // the value of an external variable
																					// can be an expression.
	public HashMap<String, String> extVarModSet = new HashMap<String, String>(); // extVarModSet is used at MaBoSS run
																					// (see Exec.java)
	public String cfgModSuffix = new String(""); // suffix to add to cfg file when ApplyExtVarMod() is run
	public HashSet<String> mutNodeSet = new HashSet<String>(); // set of node that are mutable (by changing external
																// variables in .cfg
	private static Pattern nodeBndPattern;
	private static Pattern extVarCfgPattern;
	private static Pattern rate_upPattern;
	private static Pattern rate_downPattern;
	private static double max_rate = 1E+308;

	static {
		nodeBndPattern = Pattern.compile("[^/]*(?i)node(?i)[ \\t]+([a-aA-Z_][a-aA-Z0-9_]*).*");
		extVarCfgPattern = Pattern.compile("[^/]*(\\$[a-zA-Z_0-9]+)[ \\t]*=(.*);");
		rate_upPattern = Pattern.compile("[^/]*rate_up[ \\t]*=(.*)");
		rate_downPattern = Pattern.compile("[^/]*rate_down[ \\t]*=(.*)");
	}

	public InputFilesMBSS(String BndInput, String CfgInput) {
		File bndFile = new File(BndInput);
		bndName = bndFile.getAbsolutePath();
		if (!bndFile.exists())
			System.out.println(bndName + " do not exist");
		else if (bndFile.isDirectory())
			System.out.println(bndName + " is a directory");
		else
			this.ExtractNode();
		File cfgFile = new File(CfgInput);
		cfgName = cfgFile.getAbsolutePath();
		if (!cfgFile.exists())
			System.out.println(cfgName + " do not exist");
		else if (cfgFile.isDirectory())
			System.out.println(cfgName + " is a directory");
		else
			this.ExtractExtVar();
	}

	// private, because .bnd is not modified; public method is MutantModel below
	private InputFilesMBSS(String BndInput, String CfgInput, HashSet<String> InMutNodeSet) {
		this(BndInput, CfgInput);
		mutNodeSet = new HashSet<String>(InMutNodeSet);
	}

	public void printFiles() {
		System.out.println("Bnd File: " + bndName);
		System.out.println("Cfg File: " + cfgName);
	}

	protected void ExtractNode() { // used in constructor
		try {
			File bndFile = new File(bndName);
			BufferedReader bufferBndFile = new BufferedReader(new FileReader(bndFile));
			String fileLine;
			while ((fileLine = bufferBndFile.readLine()) != null) {
				Matcher nodeMatcher = nodeBndPattern.matcher(fileLine);
				if (nodeMatcher.matches())
					if (nodeMatcher.groupCount() > 0)
						nodeSet.add(nodeMatcher.group(1));
			}
			bufferBndFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void ExtractExtVar() { // used in constructor
		try {
			File cfgFile = new File(cfgName);
			BufferedReader bufferCfgFile = new BufferedReader(new FileReader(cfgFile));
			String fileLine;
			while ((fileLine = bufferCfgFile.readLine()) != null) {
				Matcher extVarMatcher = extVarCfgPattern.matcher(fileLine);
				if (extVarMatcher.matches())
					if (extVarMatcher.groupCount() > 0)
						extVarSet.put(extVarMatcher.group(1), extVarMatcher.group(2));
			}
			bufferCfgFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Create new Input files, given a set of mutable nodes.
	public InputFilesMBSS MutantModel(HashSet<String> InMutNodeSet) {
		String bndMutFileName = this.bndName.replace(".bnd", "_mut.bnd");
		String cfgMutFileName = this.cfgName.replace(".cfg", "_mut.cfg");
		HashSet<String> CatchedMutNodeSet = new HashSet<String>();
		double max_mut_rate = max_rate / InMutNodeSet.size();
		try { // write .bnd file with mutation variables
			File bndMutFile = new File(bndMutFileName);
			BufferedWriter bufferBndMutFile = new BufferedWriter(new FileWriter(bndMutFile));
			File bndFile = new File(this.bndName);
			BufferedReader bufferBndFile = new BufferedReader(new FileReader(bndFile));
			String fileLine;
			while ((fileLine = bufferBndFile.readLine()) != null) {
				bufferBndMutFile.write(fileLine + "\n");
				Matcher nodeMatcher = nodeBndPattern.matcher(fileLine);
				if (nodeMatcher.matches())
					if (nodeMatcher.groupCount() > 0)
						if (InMutNodeSet.contains(nodeMatcher.group(1))) {
							CatchedMutNodeSet.add(nodeMatcher.group(1));
							String LineRateUp = new String();
							String LineRateDown = new String();
							fileLine = bufferBndFile.readLine();
							while (!fileLine.contains("}")) {
								Matcher rate_upMatcher = rate_upPattern.matcher(fileLine);
								if (rate_upMatcher.matches()) {
									LineRateUp = fileLine;
									while (!LineRateUp.contains(";"))
										LineRateUp = LineRateUp.concat(bufferBndFile.readLine());
								} else {
									Matcher rate_downMatcher = rate_downPattern.matcher(fileLine);
									if (rate_downMatcher.matches()) {
										LineRateDown = fileLine;
										while (!LineRateDown.contains(";"))
											LineRateDown = LineRateDown.concat(bufferBndFile.readLine());
									} else
										bufferBndMutFile.write(fileLine + "\n");
								}
								fileLine = bufferBndFile.readLine();
							}
							if (LineRateUp.isEmpty())
								bufferBndMutFile.write("rate_up = $Low_" + nodeMatcher.group(1) + " ? 0.0 : ( $High_"
										+ nodeMatcher.group(1) + " ? @max_rate : ( @logic ? 1.0 : 0.0 ));\n");
							else
								bufferBndMutFile.write(LineRateUp
										.replace("=",
												"= $Low_" + nodeMatcher.group(1) + " ? 0.0 : ( $High_"
														+ nodeMatcher.group(1) + " ? @max_rate : ( ")
										.replace(";", "));\n"));
							if (LineRateDown.isEmpty())
								bufferBndMutFile
										.write("rate_down = $Low_" + nodeMatcher.group(1) + " ? @max_rate : ( $High_"
												+ nodeMatcher.group(1) + " ? 0.0 : ( @logic ? 1.0 : 0.0 ));\n");
							else
								bufferBndMutFile
										.write(LineRateDown
												.replace("=",
														"= $Low_" + nodeMatcher.group(1) + " ? @max_rate : ( $High_"
																+ nodeMatcher.group(1) + " ? 0.0 : ( ")
												.replace(";", "));\n"));
							bufferBndMutFile.write("max_rate = " + Double.toString(max_mut_rate) + ";");
							bufferBndMutFile.newLine();
							bufferBndMutFile.write(fileLine + "\n");
						}
			}
			bufferBndFile.close();
			bufferBndMutFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try { // write .cfg file
			File cfgMutFile = new File(cfgMutFileName);
			BufferedWriter bufferCfgMutFile = new BufferedWriter(new FileWriter(cfgMutFile));
			File cfgFile = new File(this.cfgName);
			BufferedReader bufferCfgFile = new BufferedReader(new FileReader(cfgFile));
			String fileLine;
			while ((fileLine = bufferCfgFile.readLine()) != null) {
				bufferCfgMutFile.write(fileLine);
				bufferCfgMutFile.newLine();
			}
			for (String MutNode : InMutNodeSet) {
				bufferCfgMutFile.write("$High_" + MutNode + " = 0;\n");
				bufferCfgMutFile.write("$Low_" + MutNode + " = 0;\n");
			}
			bufferCfgMutFile.close();
			bufferCfgFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		InputFilesMBSS MutInputFiles = new InputFilesMBSS(bndMutFileName, cfgMutFileName, CatchedMutNodeSet);
		return (MutInputFiles);
	}

	public void AddModExtVar(String extVar, String Value) {
		if (extVarSet.get(extVar) != null)
			extVarModSet.put(extVar, Value);
	}

	// javaFx table for modifying external variables, use internal class
	// TableEditModVar
	public Stage TableEditModVarInput(SimpleStringProperty modVarProperty) {
		TableEditModVar tableEditModVar = new TableEditModVar(modVarProperty);
		tableEditModVar.Title = new String("Bnd file: " + this.bndName + "\nCfg file: " + this.cfgName);
		Stage myStage = tableEditModVar.StageTable();
		return (myStage);
	}

	public Stage StageInputMutInput(SimpleObjectProperty<InputFilesMBSS> MutInput) {
		Stage MutStage = new Stage();
		VBox vboxMut = new VBox();
		Label nodeListLabel = new Label("");
		CheckBox[] cbMutNodes = new CheckBox[nodeSet.size()];
		Integer i = 0;
		HashSet<String> NodeSetSelected = new HashSet<String>();

		for (String Node : nodeSet) {
			cbMutNodes[i] = new CheckBox(Node);
			cbMutNodes[i].setAllowIndeterminate(false);
			cbMutNodes[i].setSelected(false);
			cbMutNodes[i].selectedProperty().addListener(new ChangeListener<Boolean>() {
				public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) {
					if (new_val && !old_val)
						NodeSetSelected.add(Node);
					else if (!new_val && old_val)
						NodeSetSelected.remove(Node);
				}
			});
			vboxMut.getChildren().add(cbMutNodes[i]);
		}
		Button mutButton = new Button("Create mutation files");
		Button closeButton = new Button("Create new MaBoSS interface");
		mutButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				String nodeListString = new String("");
				if (NodeSetSelected.isEmpty()) {
					MutInput.set(null);
					closeButton.setDisable(true);
				}
				else {
					MutInput.set(InputFilesMBSS.this.MutantModel(NodeSetSelected));
					nodeListString = "Nodes: ";
					for (String Node : NodeSetSelected)
						nodeListString = nodeListString + " " + Node;
					nodeListString = nodeListString + "\n mut files constructed";
					closeButton.setDisable(false);
				}
				nodeListLabel.setText(nodeListString);
			}
		});
		closeButton.setDisable(true);
		closeButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				MutStage.close();
			}
		});
		
		vboxMut.getChildren().add(mutButton);
		vboxMut.getChildren().add(nodeListLabel);
		vboxMut.getChildren().add(closeButton);
		ScrollPane sp = new ScrollPane(vboxMut);
		sp.setVvalue(max_rate);
		Scene MutScene = new Scene(sp, 400, 400);
		MutStage.setScene(MutScene);
		return (MutStage);
	}

	// create new Input files that have the new values of external variables, given
	// in extVarModSet
	public InputFilesMBSS ApplyExtVarMod() {
		String bndMutFileName = this.bndName;
		String cfgVarModFileName = new String(this.cfgName.replace(".cfg", "_" + cfgModSuffix + ".cfg"));
		try { // write .bnd file with mutation variables
			File cfgVarModFile = new File(cfgVarModFileName);
			BufferedWriter bufferCfgVarModFile = new BufferedWriter(new FileWriter(cfgVarModFile));
			File cfgFile = new File(this.cfgName);
			BufferedReader bufferCfgFile = new BufferedReader(new FileReader(cfgFile));
			String fileLine;
			while ((fileLine = bufferCfgFile.readLine()) != null) {
				Matcher ExtVarMatcher = extVarCfgPattern.matcher(fileLine);
				if (ExtVarMatcher.matches())
					if (extVarModSet.get(ExtVarMatcher.group(1)) != null)
						bufferCfgVarModFile.write(ExtVarMatcher.group(1) + " = "
								+ extVarModSet.get(ExtVarMatcher.group(1)) + "; // changed for " + cfgModSuffix + "\n");
					else
						bufferCfgVarModFile.write(fileLine + "\n");
				else
					bufferCfgVarModFile.write(fileLine + "\n");
			}
			bufferCfgVarModFile.close();
			bufferCfgFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		InputFilesMBSS ModVarInputFiles = new InputFilesMBSS(bndMutFileName, cfgVarModFileName);
		return (ModVarInputFiles);
	}

	// class for javaFx table that modifies the external variable values
	private class TableEditModVar {
		private TreeMap<String,String> orderedExtVarSet;
		private TableView<ExtVarModVal> tableViewExtVar = new TableView<ExtVarModVal>();
		private ObservableList<ExtVarModVal> observableExtVar = FXCollections.observableArrayList();
		public String Title;
		private Button applyButton = new Button("Apply Edit Values");
		private Button undoButton = new Button("Undo Edit Values");
		private Button closeButton = new Button("Close");
		private Label ModName = new Label("Change Name: <empty>");
		private TextField ModNameField = new TextField("");

		public TableEditModVar(SimpleStringProperty modVarNameProperty) { // modVarNameProperty for passing it to a
																			// javaFx stage
			if (InputFilesMBSS.this.cfgModSuffix.length() > 0)
				ModName.setText("Change Name: " + InputFilesMBSS.this.cfgModSuffix);
			orderedExtVarSet = new TreeMap<String,String>(InputFilesMBSS.this.extVarSet); 
			for (HashMap.Entry<String, String> ExtVarVal : orderedExtVarSet.entrySet())
				observableExtVar.add(new ExtVarModVal(ExtVarVal.getKey(), ExtVarVal.getValue(),
						InputFilesMBSS.this.extVarModSet.get(ExtVarVal.getKey())));
			tableViewExtVar.setEditable(true);
			TableColumn<ExtVarModVal, String> extVarNameCol = new TableColumn<ExtVarModVal, String>("Ext. Var.");
			extVarNameCol.setMinWidth(200);
			extVarNameCol.setCellValueFactory(new PropertyValueFactory<ExtVarModVal, String>("name"));
			TableColumn<ExtVarModVal, String> extVarValCol = new TableColumn<ExtVarModVal, String>("Value");
			extVarValCol.setMinWidth(200);
			extVarValCol.setCellValueFactory(new PropertyValueFactory<ExtVarModVal, String>("val"));
			TableColumn<ExtVarModVal, String> extVarModValCol = new TableColumn<ExtVarModVal, String>("New Value");
			extVarModValCol.setMinWidth(200);
			extVarModValCol.setCellValueFactory(new PropertyValueFactory<ExtVarModVal, String>("modVal"));
			TableColumn<ExtVarModVal, String> extVarModValColField = new TableColumn<ExtVarModVal, String>(
					"Edit Value");
			extVarModValColField.setMinWidth(200);
			extVarModValColField.setCellValueFactory(new PropertyValueFactory<ExtVarModVal, String>("modVal"));
			extVarModValColField.setCellFactory(TextFieldTableCell.forTableColumn());
			extVarModValColField.setOnEditCommit(new EventHandler<CellEditEvent<ExtVarModVal, String>>() {
				@Override
				public void handle(CellEditEvent<ExtVarModVal, String> t) {
					if (t.getNewValue().length() > 0)
						((ExtVarModVal) t.getTableView().getItems().get(t.getTablePosition().getRow()))
								.setModVal(t.getNewValue());
					else
						((ExtVarModVal) t.getTableView().getItems().get(t.getTablePosition().getRow())).setModVal(null);
				}
			});
			tableViewExtVar.setItems(observableExtVar);
			tableViewExtVar.getColumns().addAll(extVarNameCol, extVarValCol, extVarModValCol, extVarModValColField);
			applyButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					for (ExtVarModVal extVar : observableExtVar) {
						if (extVar.getModVal() != null) {
							InputFilesMBSS.this.extVarModSet.put(extVar.getName(), extVar.getModVal());
						} else if (InputFilesMBSS.this.extVarModSet.containsKey(extVar.getName()))
							InputFilesMBSS.this.extVarModSet.remove(extVar.getName());
					}
					orderedExtVarSet = new TreeMap<String,String>(InputFilesMBSS.this.extVarSet);
					observableExtVar.clear();
					for (HashMap.Entry<String, String> ExtVarVal : orderedExtVarSet.entrySet())
						observableExtVar.add(new ExtVarModVal(ExtVarVal.getKey(), ExtVarVal.getValue(),
								InputFilesMBSS.this.extVarModSet.get(ExtVarVal.getKey())));
				}
			});
			undoButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					observableExtVar.clear();
					for (HashMap.Entry<String, String> ExtVarVal : orderedExtVarSet.entrySet()) {
						observableExtVar.add(new ExtVarModVal(ExtVarVal.getKey(), ExtVarVal.getValue(),
								InputFilesMBSS.this.extVarModSet.get(ExtVarVal.getKey())));
					}
				}
			});
			closeButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					((Stage) applyButton.getScene().getWindow()).close();
				}
			});

			ModNameField.setOnKeyPressed(new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent keyEvent) {
					if (keyEvent.getCode() == KeyCode.ENTER) {
						cfgModSuffix = ModNameField.getText();
						modVarNameProperty.set(cfgModSuffix);
						if (ModNameField.getText().length() > 0)
							ModName.setText("Change Name: " + ModNameField.getText());
						else
							ModName.setText("Change Name: <empty>");
					}
				}
			});
		}

		public Stage StageTable() {
			Stage stageTable = new Stage();
			final Label label = new Label(this.Title);
			final VBox vbox = new VBox();
			final HBox hboxButton = new HBox();
			final HBox hboxName = new HBox();
			hboxName.setSpacing(10);
			hboxButton.setSpacing(10);
			hboxButton.getChildren().addAll(applyButton, undoButton, closeButton);
			hboxName.getChildren().addAll(ModName, ModNameField);
			vbox.setSpacing(30);
			vbox.setPadding(new Insets(10, 10, 10, 10));
			vbox.getChildren().addAll(label, tableViewExtVar, hboxName, hboxButton);
			Scene scene = new Scene(vbox, 600, 800);
			stageTable.setScene(scene);
			return (stageTable);
		}

		public class ExtVarModVal {
			private SimpleStringProperty name;
			private SimpleStringProperty val;
			private SimpleStringProperty modVal;

			private ExtVarModVal(String iname, String ival, String imodVal) {
				this.name = new SimpleStringProperty(iname);
				this.val = new SimpleStringProperty(ival);
				this.modVal = new SimpleStringProperty(imodVal);
			}

			public String getName() {
				return name.get();
			}

			public void setName(String fName) {
				name.set(fName);
			}

			public String getVal() {
				return val.get();
			}

			public void setVal(String fName) {
				val.set(fName);
			}

			public String getModVal() {
				return modVal.get();
			}

			public void setModVal(String fName) {
				modVal.set(fName);
			}
		}

	}

}
