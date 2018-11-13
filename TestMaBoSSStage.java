import JvMaBoSS.*;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.ScrollPane;

public class TestMaBoSSStage extends Application {
	static String Version = new String("0.8");
	@Override
	public void start(Stage primaryStage) throws Exception {
		MaBoSSGrid grid = new MaBoSSGrid();
		// grid.gridLinesVisibleProperty().set(true);;
		Scene scene = new Scene(new ScrollPane(grid), 1200, 750);
		primaryStage.setTitle("MaBoSS Graphical Interface");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		if (args.length != 0)
			if (args[0].equals("-version"))
				System.out.println("Version: "+Version);
			else
				Application.launch(args);
		else
			Application.launch(args);
	}
}
