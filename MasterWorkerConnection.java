/**
 * MasterWorkerConnection : Class create connection between the master and worker node. 
 */
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class MasterWorkerConnection extends Thread {
	Master master;
	String workerip;
	int workerId;
	public InputStream in;
	public OutputStream out;
	Socket worker;
	FileInfo fileInfo;
	ArrayList<Integer> list;
	ArrayList<String> listIP;
	public MasterWorkerConnection(String ip, Master master, ArrayList<Integer> list, FileInfo filedata, ArrayList<String> listIP)
			throws UnknownHostException, IOException {
		this.fileInfo=filedata;
		this.workerip = ip;
		this.master = master;
		worker = new Socket(workerip, Constants.WORKER_LISTEN_PORT);
		this.in = worker.getInputStream();
		this.out = worker.getOutputStream();
		this.list=list;
		this.listIP=listIP;
	}

	/**
	 * run : Receives the GREP result from Worker. 
	 */
	public void run() {
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(in);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
				Message messageReceived = (Message) ois.readObject();
				switch (messageReceived.command) {
				case Constants.receiveResult:
					FileInfo fileInfo = (FileInfo) messageReceived.data;
					master.addResult(fileInfo.result,fileInfo.name+fileInfo.chunkNo);
					worker.close();
			}

		} catch (Exception e) {
				fileResend(fileInfo);
		} 
	}
	
	/**
	 * fileResend : This method is called in case of communication failure. 
	 */

	public void fileResend(FileInfo fileInfo){
		try {
			System.out.println("Resending File :" + fileInfo.name +" Chunk No : "+fileInfo.chunkNo);
			ArrayList<String> temp =listIP;
			for (int i = 0; i <temp.size(); i++) {
				if(temp.get(i).equalsIgnoreCase(workerip)){
					listIP.remove(i);
				}
			}
			MasterWorkerConnection mWorkerConnection = new MasterWorkerConnection(listIP.get(0),master,list,fileInfo,listIP);
			Message masterMessage = new Message(Constants.MASTER_SEND_FILE,fileInfo);
			mWorkerConnection.start();
			ObjectOutputStream oos;
			oos = new ObjectOutputStream(mWorkerConnection.out);
			oos.writeObject(masterMessage);
			oos.flush();
			new MasterFileSend(fileInfo.name, fileInfo.chunkNo, mWorkerConnection, master).start();
		} catch (IOException e) {
		}
	}


}
