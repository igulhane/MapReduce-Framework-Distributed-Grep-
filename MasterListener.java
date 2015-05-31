/**
 * MasterListener : Gets the results from worker. 
 */

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
public class MasterListener extends Thread {
	ServerSocket masterServerSocket;
	Master master;
	static int workerId=0;
	
	MasterListener(Master master) {
		this.master = master;
		try {
			masterServerSocket = new ServerSocket(Constants.MASTER_LISTEN_PORT);
			System.out.println("Master Started on :"+ InetAddress.getLocalHost().getHostAddress().toString());
		} catch (IOException e) {
			System.out.print("Error starting master server");
		}
	}
	

	/**
	 * Run : Accepts request from worker to connect the network. 
	 */
	public void run() {
		while (true) {
			try {
				Socket workerSocket = masterServerSocket.accept();
				master.workerList.put(++workerId, workerSocket.getInetAddress().getHostAddress().toString());
			} catch (IOException e) {
			e.printStackTrace();
			}
		}
	}

}
