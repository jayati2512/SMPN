public class Query extends Thread {
	int QI, broker, no, p = 0;
	Integer participants[] = new Integer[500];

	public Query() {
		++no;
	}

	public void run(Network net) throws InterruptedException {
		for (int i = 0; i < GlobalParams.numOfPeers; i++) {
			net.mp[i].getInfo();
		}
		System.out.println("\n");

		QI = (int) (GlobalParams.numOfPeers * 0.3 + 1 + (int) (Math.random() * ((GlobalParams.numOfPeers - (GlobalParams.numOfPeers * 0.3 + 1)) + 1)));
		System.out.println("QUERY ISSUER = " + QI);
		System.out.println("\n");

		net.mp[QI - 1].nearbyctr = 0;
		for (int i = 0; i < GlobalParams.numOfPeers; i++) {
			net.mp[QI - 1].nearby[net.mp[QI - 1].nearbyctr++] = (int) net.mp[QI - 1]
					.nearbypeers(net.mp[i]);
		}

		System.out.println("Nearby nodes of QI");
		for (int i = 0; i < GlobalParams.numOfPeers; i++) {
			if (net.mp[QI - 1].nearby[i] > 0) {
				System.out.print((i + 1) + " ");
			}
		}
		System.out.println("\n");
		net.mp[QI - 1].sendtoall(net, net.mp[QI - 1].nearby, "REQUEST",
				net.mp[QI - 1].id, -1, "HELLO " + net.mp[QI - 1].id, "");

		Thread.sleep(7000);
		for (int i = 0; i < GlobalParams.numOfPeers; i++) {
			// if(net.mp[QI-1].nearby[i]>0)
			// {
			System.out.println("Peer " + (i + 1));
			for (int j = 0; j < net.mp[i].mctr; j++) {
				System.out.print("ID " + net.mp[i].msg[j].mid + "\t");
				System.out.print("SENDER " + net.mp[i].msg[j].msender + "\t");
				System.out.print("RECIEVER " + net.mp[i].msg[j].mreciever
						+ "\t");
				System.out.print("TYPE " + net.mp[i].msg[j].mtype + "\t\t");
				System.out.print("CONTENT " + net.mp[i].msg[j].mcontent + "\n");
			}
			System.out.println("\n");
			// }
		}
		broker = net.mp[QI - 1].choosebroker();
		Network.adder = 100;
		if (broker != 0) {
			System.out.println("Selected Super Peer = " + broker);

			String path;
			path = Integer.toString(net.mp[QI - 1].id);
			net.mp[QI - 1].sendtoall(net, net.mp[QI - 1].nearby, "QUERY",
					net.mp[QI - 1].id, broker, "HELLO " + net.mp[QI - 1].id,
					path);

			Thread.sleep(15000);
			net.mp[broker - 1].sendResult();

			Thread.sleep(2000);

			for (int i = 0; i < GlobalParams.numOfPeers; i++) {
				// if(net.mp[QI-1].nearby[i]>0)
				// {
				System.out.println("Peer " + (i + 1));
				for (int j = 0; j < net.mp[i].mctr; j++) {
					System.out.print("ID " + net.mp[i].msg[j].mid + "\t");
					System.out.print("SENDER " + net.mp[i].msg[j].msender
							+ "\t");
					System.out.print("RECIEVER " + net.mp[i].msg[j].mreciever
							+ "\t");
					System.out.print("TYPE " + net.mp[i].msg[j].mtype + "\t\t");
					System.out.print("CONTENT " + net.mp[i].msg[j].mcontent
							+ "\t");
					System.out.print("PATH " + net.mp[i].msg[j].path + "\n");
				}
				System.out.println("\n");
				// }
			}

			System.out.println("\n");
			for (int i = 0; i < GlobalParams.numOfPeers; i++) {
				net.mp[i].getInfo();
			}
			System.out.println("\n");

		} else {
			System.out.println("No Super Peers nearby !!!");

		}

	}
}
