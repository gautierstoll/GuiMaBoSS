package JvMaBoSS;

public class OutputTablesMBSS extends OutputMBSS {
	public final String nodeProbTrajTableName;
	public final String probTrajTableName;
	public final NodeTrajMaBoSS NodeTrajTable;
	public final TrajMaBoSS TrajTable;

	public OutputTablesMBSS(OutputMBSS Oput) {
		super(Oput);
		nodeProbTrajTableName = super.probTrajName.replace(".csv", "_nodetable.csv");
		probTrajTableName = super.probTrajName.replace(".csv", "_table.csv");
		NodeTrajTable = new NodeTrajMaBoSS(this.probTrajName);
		TrajTable = new TrajMaBoSS(this.probTrajName);
	}

	public void WriteNodeProbTrajTableFile() {
		NodeTrajTable.WriteProbNodeTrajTableFile(nodeProbTrajTableName);
	}

	public void WriteProbTrajTableFile() {
		TrajTable.WriteProbTrajTableFile(probTrajTableName);
	}
}
