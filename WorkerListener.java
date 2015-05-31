/**
 * WorkerListener : Multithreaded server that accept the Master request for GREP. 
 */
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class WorkerListener extends Thread {
	ServerSocket ss;
	Worker worker;
	
	WorkerListener(Worker worker) {
		this.worker = worker;
		try {
			ss = new ServerSocket(Constants.WORKER_LISTEN_PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Run : Checks if the request is received for GREP. If yes then create a new thread for performing the GREP. 
	 */
	public void run() {
		while (true) {
			try {
				Socket socket = ss.accept();
				ObjectInputStream ois = new ObjectInputStream(
						socket.getInputStream());
				Message message = (Message) ois.readObject();
				switch (message.command) {
				case Constants.MASTER_SEND_FILE:new WorkerFileReceiver(socket, (FileInfo) message.data).start();
					break;
				}
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}
