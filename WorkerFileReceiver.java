/**
 * WorkerFileReceiver : Receives the data from Master and performs the GREP opertion. 
 */
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class WorkerFileReceiver extends Thread {
	Socket socket;
	FileInfo filedata;
	ObjectOutputStream oos;
	public WorkerFileReceiver(Socket s,FileInfo f) {
		socket = s;
		this.filedata=f;
		
	}
	

	/**
	 * Run : Receives the file from Master, performs GREP and sends the result back to the Master. 
	 */
	public void run() {
		byte[] chunkBytes = new byte[Constants.CHUNK_SIZE];
		int current=0;
		int bytesRead=0;
		try {
			oos= new ObjectOutputStream(socket.getOutputStream());
			BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
			do {
				bytesRead = bis.read(chunkBytes, current, chunkBytes.length-current);
				if(bytesRead>0) current += bytesRead;
			}while(bytesRead>-1&&current<Constants.CHUNK_SIZE);
			System.out.println("Received file :"+filedata.name+", chunk no "+ filedata.chunkNo+" From Master..!!!");
			filedata.result= new Job().map(filedata.regex, chunkBytes,filedata.name+"chunk"+filedata.chunkNo);
			Message masterMessage = new Message(Constants.receiveResult,filedata);
			oos.writeObject(masterMessage);
			oos.flush();
			System.out.println("Result sent for :"+filedata.name+", chunk no "+ filedata.chunkNo+" to Master..!!!");
		} catch (IOException e) {
		}
		
	}
}
