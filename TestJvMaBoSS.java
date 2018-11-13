import JvMaBoSS.*;

public class TestJvMaBoSS {
	public static void main(String[] args) {
		InputFilesMBSS MyInputFiles = new InputFilesMBSS(args[0], args[1]);
		for (String Node : MyInputFiles.nodeSet)
			System.out.println("Node: " + Node);
		for (String ExtVar : MyInputFiles.extVarSet.keySet())
			System.out.println("ExtVar: " + ExtVar);
		
		MyInputFiles.AddModExtVar("$cracra", ".5");
		
		
		//HashSet<String> MutList = new HashSet<String>(Arrays.asList("p53", "Mdm2C", "blabla"));
		//InputFilesMBSS MutInputFiles = MyInputFiles.MutantModel(MutList);
		//for (String CatchedMutation : MutInputFiles.mutNodeSet)
			//System.out.println("Catched Mutation: " + CatchedMutation);
		//MutInputFiles.printFiles();
		//String Line = "0.2100	0.1000	0.0302	1.4177	0.5900	0.4100	<nil>	0.380000	0.048783	Mdm2N	0.050000	0.021904	p53_h	0.530000	0.050161	Mdm2N -- p53_h"
		//		+ "	0.040000	0.019695";
		//BoolStateDist MyBStateDist = new BoolStateDist(Line);
		//System.out.println("Initial Condition" + Line);
		//System.out.println("Initial Condition transformed:");
		//System.out.println(MyBStateDist.Tranform2InitCond());

		Exec MyExec = new Exec(MyInputFiles, args[2], args[3],false);

		OutputTablesMBSS MyOutputTables = new OutputTablesMBSS(MyExec.output);
		MyOutputTables.WriteProbTrajTableFile();
		//System.out.println(MyOutputTables.probTrajName);
		//System.out.println(MyOutputTables.probTrajTableName);
	}
}
