import java.awt.Color;
import java.awt.Graphics;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Network extends Thread {

	public static int adder = 100;
	Peer mp[] = new Peer[GlobalParams.numOfPeers];
	JFrame f;
	JPanel content;

	public Network(GlobalParams gp) {
		for (int i = 0; i < GlobalParams.numOfPeers; i++) {
			mp[i] = new Peer(i);
		}
		begin();

		f = new JFrame();
		content = new JPanel();
		f.setContentPane(content);
		f.setTitle("SIMULATION");
		int frameWidth = 1360;
		int frameHeight = 720;
		f.setSize(frameWidth, frameHeight);
		f.setVisible(true);
	}

	public void run() {
		paint_timer();
	}

	void paint_timer() {
		int delay = 0; // delay for 0 sec.
		int period = 150; // repeat every 20 millisec.
		Timer timer = new Timer();

		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				Graphics g = content.getGraphics();
				paint(g);
			}
		}, delay, period);
	}

	void paint(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, content.getWidth(), content.getHeight());

		g.setColor(Color.orange);
		g.drawRect(0, 0, 1310, 650);
		g.setColor(Color.RED);
		g.fillOval(10, 20, 20, 20);
		g.setColor(Color.WHITE);
		g.drawString("SUPER PEER", 50, 30);
		g.setColor(Color.BLUE);
		g.fillOval(10, 50, 20, 20);
		g.setColor(Color.WHITE);
		g.drawString("DATA PROVIDER", 50, 60);
		g.setColor(Color.GREEN);
		g.fillOval(10, 80, 20, 20);
		g.setColor(Color.WHITE);
		g.drawString("QUERY ISSUER", 50, 90);
		g.setColor(Color.MAGENTA);
		g.fillOval(10, 110, 20, 20);
		g.setColor(Color.WHITE);
		g.drawString("SELECTED SUPER PEER", 50, 120);
		g.setColor(Color.CYAN);
		g.drawLine(10, 150, 30, 150);
		g.setColor(Color.WHITE);
		g.drawString("DIST < 500", 50, 150);

		for (int i = 0; i < GlobalParams.numOfPeers; i++) {
			for (int j = 0; j < GlobalParams.numOfPeers; j++) {
				double d = Math.sqrt(Math.pow(((int) mp[i].x - (int) mp[j].x),
						2) + Math.pow(((int) mp[i].y - (int) mp[j].y), 2));
				if (d < GlobalParams.radius) {
					g.setColor(Color.CYAN);
					g.drawLine((int) mp[i].x, (int) mp[i].y, (int) mp[j].x,
							(int) mp[j].y);
				}
			}
		}
		
		for (int i = 0; i < GlobalParams.numOfPeers; i++) {
			mp[i].draw(g);
		}


	}

	void begin() {
		for (int i = 0; i < GlobalParams.numOfPeers; i++) {
			mp[i].start();
		}
	}

	public void send(int id) {
		mp[id - 1].nearbyctr = 0;
		for (int i = 0; i < GlobalParams.numOfPeers; i++) {
			mp[id - 1].nearby[mp[id - 1].nearbyctr++] = (int) mp[id - 1]
					.nearbypeers(mp[i]);
		}
		// System.out.println(mp[id-1].replied[Simulator.q.no-1]);
		if (mp[id - 1].type == "BROKER"
				&& mp[id - 1].replied[Simulator.q.no - 1] == 999) {
			mp[id - 1].sendtoall(this, mp[id - 1].nearby, "QUERY_FORWARD", id,
					-1, "HELLO " + id, mp[id - 1].path.get(Simulator.q.no)
							+ " " + mp[id - 1].id);
			mp[id - 1].replied[Simulator.q.no - 1] = 222;
			Simulator.q.participants[Simulator.q.p++] = id;
			// System.out.println("CASE 1 >>>>>>>>>>>>>>>......................");
		}

		if (mp[id - 1].type == "BROKER"
				&& mp[id - 1].replied[Simulator.q.no - 1] == 1) {
			mp[id - 1]
					.sendtoall(this, mp[id - 1].nearby, "REQUEST_REPLY", id,
							Simulator.q.QI, "HELLO " + id + " "
									+ mp[id - 1].energy, "");
			mp[id - 1].replied[Simulator.q.no - 1] = 777;
			Simulator.q.participants[Simulator.q.p++] = id;
			// System.out.println(mp[id-1].replied[Simulator.q.no-1]);
			// System.out.println("CASE 2 >>>>>>>>>>>>>>>");
		}

		if (mp[id - 1].type == "RANKER"
				&& mp[id - 1].replied[Simulator.q.no - 1] == 25
				&& Simulator.q.QI != mp[id - 1].id) {
			if (random()) {
				// RELAY >>>>>>>>>>>>

				if (mp[id - 1].type == "RANKER"
						&& mp[id - 1].replied[Simulator.q.no - 1] == 25) {

					//System.out.println("**************I M A RELAY PEER "
						//	+ mp[id - 1].id);
					mp[id - 1].sendtoall(this, mp[id - 1].nearby,
							"QUERY_FORWARD_MORE", id, -1, "HELLO " + id,
							mp[id - 1].path.get(Simulator.q.no) + " "
									+ mp[id - 1].id);
					mp[id - 1].replied[Simulator.q.no - 1] = 100;
					Simulator.q.participants[Simulator.q.p++] = id;
				}

			} else {
				// REPLY >>>>>>>>>>>>

				if (mp[id - 1].type == "RANKER"
						&& mp[id - 1].replied[Simulator.q.no - 1] == 25) {

					String s = mp[id - 1].path.get(Simulator.q.no);
					StringTokenizer st = new StringTokenizer(s, " ");
					int n = 0;
					int ct = st.countTokens();
					for (int i = 0; i < ct; i++) {
						n = Integer.parseInt(st.nextToken());
					}
					//System.out.println(n + " =======================");

					mp[id - 1].sendtoall(this, mp[id - 1].nearby,
							"QUERY_REPLY", id, n, "HELLO " + id,
							mp[id - 1].path.get(Simulator.q.no) + " "
									+ mp[id - 1].id);
					mp[id - 1].replied[Simulator.q.no - 1] = 50;
					Simulator.q.participants[Simulator.q.p++] = id;
				}
			}
		}
	}

	boolean random() {
		int t = (int) (Math.random() * 10);
		int r = t % 2;
		if (r == 0)
			return false;
		else
			return true;
	}

	public void sendThisBack(int id, String path) {

		String s = mp[id - 1].path.get(Simulator.q.no);
		StringTokenizer st = new StringTokenizer(s, " ");
		int n = 0;
		int ct = st.countTokens();
		for (int i = 0; i < ct; i++) {
			n = Integer.parseInt(st.nextToken());
		}
		//System.out.println(n + " =======================");
		mp[id - 1].sendtoall(this, mp[id - 1].nearby, "QUERY_REPLY", id, n,
				"HELLO " + id, path);
		mp[id - 1].replied[Simulator.q.no - 1] = 100;
		Simulator.q.participants[Simulator.q.p++] = id;

	}

	public void display_result(long difference) {

		int sentsum = 0, recsum = 0;

		System.out.println("\t\t\tRESULT SUMMARY");
		System.out
				.println("````````````````````````````````````````````````````````````````````````````````````````````````````````````````````");

		System.out.println("\n---> TOTAL EXECUTION TIME <---\n");
		System.out.println(difference + " milliseconds\n");

		System.out.println("\n---> TOTAL MESSAGES <---\n");
		System.out.println("Peer \t Recieved Messages \t Sent Messages\n");
		for (int i = 0; i < GlobalParams.numOfPeers; i++) {
			for (int j = 0; j < mp[i].mctr; j++) {
				mp[mp[i].msg[j].msender - 1].sentmsg++;
				sentsum += 1;
			}
		}
		for (int i = 0; i < GlobalParams.numOfPeers; i++) {
			System.out.println("Peer " + (i + 1) + "\t\t" + mp[i].mctr
					+ "\t\t\t" + mp[i].sentmsg);
		}
		for (int i = 0; i < GlobalParams.numOfPeers; i++) {
			recsum += mp[i].mctr;
		}
		System.out.println("\nTOTAL \t\t " + sentsum + "\t\t\t" + recsum);

		System.out.println("\n\n---> TOTAL PARTICIPATING NODES <---\n");
		int participated = 0;
		for (int i = 0; i < GlobalParams.numOfPeers; i++) {
			if (mp[i].sentmsg > 0)
				participated++;
		}
		System.out.println(participated + " nodes");

		System.out.println("\n\n---> HOP COUNT <---\n");
		int i = Simulator.q.QI - 1;
		int total = 0;
		for (int j = 0; j < mp[i].mctr; j++) {
			if (mp[i].msg[j].mtype == "FINAL_RESULT") {
				String s = mp[i].msg[j].path;
				StringTokenizer st = new StringTokenizer(s, " ");
				int hop = st.countTokens() - 1;
				if (hop > total)
					total = hop;
			}
		}
		System.out.println(total);

	}

}
