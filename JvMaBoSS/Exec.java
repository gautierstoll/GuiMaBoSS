package JvMaBoSS;

import java.io.File;
import java.io.*;
import java.io.IOException;

public class Exec {
	public InputFilesMBSS inputFiles;
	public String execName;
	public String outPrefix;
	public OutputMBSS output = null;

	public Exec(InputFilesMBSS IF, String ExecN, String OPrefix, Boolean ApplyModVar) {
		if (!(ApplyModVar))
			this.inputFiles = IF;
		else {
			inputFiles = IF.ApplyExtVarMod(); // Construct new .cfg file, applying extVarModSet to it.
		}
		File ExecFile = new File(ExecN);
		if (ExecFile.exists())
			execName = ExecFile.getAbsolutePath();
		else
			execName = ExecN;
		outPrefix = OPrefix;
		Runtime commandPrompt = Runtime.getRuntime();
		System.out.println("Launch MaBoSS:");
		System.out.println(execName + " -c " + inputFiles.cfgName + " -o " + outPrefix + " " + inputFiles.bndName);
		try {
			Process process = commandPrompt
					.exec(execName + " -c " + inputFiles.cfgName + " -o " + outPrefix + " " + inputFiles.bndName);
			process.waitFor();
			BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			String ErrLine = new String();
			ErrLine = stdError.readLine();
			if (ErrLine == null)
				output = new OutputMBSS(outPrefix);
			else {
				Boolean OnlyWarning = true;
				while (ErrLine != null) {
					System.out.println(ErrLine);
					if (!ErrLine.contains("Warning:"))
						OnlyWarning = false;
					ErrLine = stdError.readLine();
				}
				if (OnlyWarning)
					output = new OutputMBSS(outPrefix);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void printNames() {
		inputFiles.printFiles();
		System.out.println("Executable: " + execName);
	}
}
