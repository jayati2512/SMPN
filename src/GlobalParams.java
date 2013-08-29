import java.io.BufferedReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class GlobalParams {

	static int simNo;
	static int numOfPeers;
	static int radius;
	static int energy;
	static int energysend;
	static int energyrecieve;

	public GlobalParams(BufferedReader bufRdrF) throws IOException {

		String line = null;

		while ((line = bufRdrF.readLine()) != null) {

			StringTokenizer st = new StringTokenizer(line, ",");

			while (st.hasMoreTokens()) {

				GlobalParams.simNo = Integer.parseInt(st.nextToken());
				GlobalParams.numOfPeers = Integer.parseInt(st.nextToken());
				GlobalParams.radius = Integer.parseInt(st.nextToken());
				GlobalParams.energy = Integer.parseInt(st.nextToken());
				GlobalParams.energysend = Integer.parseInt(st.nextToken());
				GlobalParams.energyrecieve = Integer.parseInt(st.nextToken());

				System.out.println("Simno-" + GlobalParams.simNo + " Numpeer-"
						+ GlobalParams.numOfPeers + " Radius-"
						+ GlobalParams.radius + " Energy-"
						+ GlobalParams.energy + " Energy to send-"
						+ GlobalParams.energysend + " Energy to revieve-"
						+ GlobalParams.energyrecieve + "\n");
			}
		}
	}

}
