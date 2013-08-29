import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

public class Simulator {
	static Network net;
	static Query q;

	public static void main(String[] args) throws IOException,
			InterruptedException {

		long lStartTime = new Date().getTime(); // start time

		File file = new File("src\\sim.csv");
		BufferedReader bufRdrF = new BufferedReader(new FileReader(file));
		GlobalParams gp = new GlobalParams(bufRdrF);

		net = new Network(gp);
		net.start();

		q = new Query();
		q.run(net);

		long lEndTime = new Date().getTime();
		long difference = lEndTime - lStartTime; // check different

		net.display_result(difference);

		// System.exit(0);
	}

	public static void callNW(int id) {
		net.send(id);
	}

}
