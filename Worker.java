/**
 * Worker : Worker Node implementation. 
 */
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;
public class Worker {

	int id;
	Socket connection;
	String masterIp;
	OutputStream os = null;
	InputStream is = null;

	public Worker(String ip) {
		this.masterIp = ip;
		try {
			connection = new Socket(masterIp, Constants.WORKER_LISTEN_PORT);
			os = connection.getOutputStream();
			is = connection.getInputStream();
			connection.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		System.out.println("Enter Master Ip");
		Worker worker = new Worker(new Scanner(System.in).next());
		WorkerListener listener = new WorkerListener(worker);
		listener.start();
	}

}
