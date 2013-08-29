import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

public class Peer extends Thread {

	int id, energy, velocity, angle, networkNumber;
	double x, y;
	String type;
	Message msg[] = new Message[500];
	int mctr = 0;
	long trigger;
	// int qryreplied = -1;
	Integer[] replied = new Integer[500];
	int repliedctr;
	Integer[] nearby = new Integer[GlobalParams.numOfPeers];
	int nearbyctr;
	double vx, vy;
	int sentmsg = 0;
	HashMap<Integer, Integer> repsent = new HashMap<Integer, Integer>();

	HashMap<Integer, String> path = new HashMap<Integer, String>();

	Peer(int i) {
		this.id = i + 1;

		if (this.id <= .30 * GlobalParams.numOfPeers)
			this.type = "BROKER";
		else
			this.type = "RANKER";

		this.energy = GlobalParams.energy;
		this.x = (int) (Math.random() * (1310 - 0 + 1) ) + 0;
		this.y = (int) (Math.random() * (650 - 0 + 1) ) + 0;
		// this.velocity = (int) (Math.random() * 10);
		this.velocity = 7;
		this.angle = (int) (Math.random() * 1000);
		if (this.angle > 360) {
			this.angle %= 360;
		}
		for (int j = 0; j < 500; j++) {
			replied[j] = 0;
		}

		vx = (this.velocity * Math.cos(this.angle)) * 0.001;
		vy = (this.velocity * Math.sin(this.angle)) * 0.001;
	}

	public void getInfo() {
		System.out.println("Peer-" + this.id + "\tLocation-(" + this.x + ","
				+ this.y + ")" + "\tAngle-" + this.angle + "\tVelocity-"
				+ this.velocity + "\tEnergy-" + this.energy + "\tType-"
				+ this.type);

	}

	public void run() {
		pos_timer();
		trigger_timer();
		checkqry_timer();
	}

	void checkqry_timer() {
		int delay = 0; // delay for 0 sec.
		int period = 1; // repeat every 1 millisec.
		Timer timer = new Timer();

		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				checkQuery();
			}
		}, delay, period);
	}

	void checkQuery() {
		for (int i = 0; i < mctr; i++) {

			if (msg[i].mtype == "REQUEST" && type == "BROKER"	&& replied[msg[i].mid - 1] == 0) {
				// System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< >>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
				trigger = System.currentTimeMillis() + Network.adder;
				replied[msg[i].mid - 1] = 1;
				Network.adder += 50;

			}

			if (msg[i].mtype == "QUERY" && type == "BROKER"
					&& msg[i].mreciever == id && replied[msg[i].mid - 1] == 777) {
				path.put(Simulator.q.no, msg[i].path);
				replied[msg[i].mid - 1] = 999;
				// System.out.println(msg[i].mtype+" "+type+" "+msg[i].mreciever+" "+replied[msg[i].mid-1]);
				trigger = System.currentTimeMillis() + Network.adder;
				Network.adder += 50;
				System.out
						.println("\nQuery forwarded to all the Peers .......");
			}

			if ((msg[i].mtype == "QUERY_FORWARD" || msg[i].mtype == "QUERY_FORWARD_MORE")
					&& type == "RANKER" && replied[msg[i].mid - 1] == 0) {
				path.put(Simulator.q.no, msg[i].path);
				replied[msg[i].mid - 1] = 25;
				trigger = System.currentTimeMillis() + Network.adder;
				Network.adder += 50;
			//	System.out.println("\nQuery replied by ranker " + id);
			}

			if (msg[i].mtype == "QUERY_REPLY" && type == "RANKER"
					&& msg[i].mreciever == id && replied[msg[i].mid - 1] == 100
					&& repsent.get(msg[i].msender) == null) {
				repsent.put(msg[i].msender, 7777);
				replied[msg[i].mid - 1] = 200;
				Simulator.net.sendThisBack(id, msg[i].path);
				// trigger=System.currentTimeMillis()+Network.adder;
				// Network.adder+=50;
			}
		}
	}

	void trigger_timer() {
		int delay = 0; // delay for 0 sec.
		int period = 1; // repeat every millisec.
		Timer timer = new Timer();

		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				checkTrigger();
			}
		}, delay, period);
	}

	void checkTrigger() {
		if (System.currentTimeMillis() == this.trigger) {
			//System.out.println("TRIGGER SET ..." + this.id);
			Simulator.callNW(this.id);
		}
	}

	void pos_timer() {
		int delay = 0; // delay for 0 sec.
		int period = 1; // repeat every sec.
		Timer timer = new Timer();

		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				changepos();
			}
		}, delay, period);
	}

	void changepos() {

		this.x += vx;
		this.y += vy;

		if (this.x <= 0) {
			x = 0;
			vx = -vx;
		}
		if (this.x >= 1310) {
			x = 1310;
			vx = -vx;
		}
		if (this.y <= 0) {
			y = 0;
			vy = -vy;
		}
		if (this.y >= 650) {
			y = 650;
			vy = -vy;
		}
	}

	public void sendtoall(Network net, Integer[] nearby, String type,
			int sender, int reciever, String content, String path) {

		Message mymsg = new Message();
		mymsg.mid = Simulator.q.no;
		mymsg.msender = sender;
		mymsg.mreciever = reciever;
		mymsg.mtype = type;
		mymsg.mcontent = content;
		mymsg.path = path;

		for (int i = 0; i < GlobalParams.numOfPeers; i++) {
			if (nearby[i] > 0) {
				net.mp[i].msg[net.mp[i].mctr] = mymsg;
				net.mp[i].mctr++;
				net.mp[i].energy -= GlobalParams.energyrecieve;
				net.mp[this.id - 1].energy -= GlobalParams.energysend;
			}
		}
	}

	public double nearbypeers(Peer p) {
		if (this.id == p.id) {
			return -1;
		}

		double d = Math.sqrt(Math.pow((this.x - p.x), 2)
				+ Math.pow((this.y - p.y), 2));
		if (d < GlobalParams.radius) {
			return d;
		}

		return 0;
	}

	public int choosebroker() {
		int e, selbroker = 0;
		double d, least = 999999999, val;
		for (int i = 0; i < mctr; i++) {
			if (msg[i].mtype == "REQUEST_REPLY") {
				d = nearbypeers(Simulator.net.mp[msg[i].msender - 1]);
			/*	StringTokenizer st = new StringTokenizer(msg[i].mcontent);
				st.nextToken();
				st.nextToken();
				e = Integer.parseInt(st.nextToken());*/
				val = d;
				if (val < least) {
					least = val;
					selbroker = msg[i].msender;
				}
			}
		}
		return selbroker;
		
	}

	public void sendResult() {

		Simulator.net.mp[Simulator.q.broker - 1].nearbyctr = 0;
		for (int i = 0; i < GlobalParams.numOfPeers; i++) {
			Simulator.net.mp[Simulator.q.broker - 1].nearby[Simulator.net.mp[Simulator.q.broker - 1].nearbyctr++] = (int) Simulator.net.mp[Simulator.q.broker - 1]
					.nearbypeers(Simulator.net.mp[i]);
		}

		for (int i = 0; i < mctr; i++) {
			if (msg[i].mid == Simulator.q.no && msg[i].mtype == "QUERY_REPLY") {
				sendtoall(Simulator.net,
						Simulator.net.mp[Simulator.q.broker - 1].nearby,
						"FINAL_RESULT", Simulator.q.broker, Simulator.q.QI,
						"HELLO " + id, msg[i].path);
			}
		}
	}

	public void draw(Graphics g) {
		if (this.type == "BROKER") {
			g.setColor(Color.RED);
			g.fillOval((int) x, (int) y, 20, 20);
			g.setColor(Color.WHITE);
			g.drawString(this.id + " ", (int) x, (int) y);

			if (this.id == Simulator.q.broker) {
				g.setColor(Color.MAGENTA);
				g.fillOval((int) x, (int) y, 20, 20);
				g.setColor(Color.WHITE);
				g.drawString(this.id + " ", (int) x, (int) y);
			}
		} else {
			if (this.id == Simulator.q.QI) {
				g.setColor(Color.GREEN);
				g.fillOval((int) x, (int) y, 20, 20);
				g.setColor(Color.WHITE);
				g.drawString(this.id + " ", (int) x, (int) y);
			} else {
				g.setColor(Color.BLUE);
				g.fillOval((int) x, (int) y, 20, 20);
				g.setColor(Color.WHITE);
				g.drawString(this.id + " ", (int) x, (int) y);
			}

		}

	}
}
