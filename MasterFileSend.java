/**
 * MasterFileSend : Sends the assigned file to chunks to respective workers.
 *  
 */
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
public class MasterFileSend extends Thread {
	String filename;
	int chunkpart;
	MasterWorkerConnection mw;
	Master master;
	public MasterFileSend(String filename, int chunkpart,MasterWorkerConnection worker, Master master) {
		super();
		this.filename = filename;
		this.chunkpart = chunkpart;
		this.mw = worker;
		this.master = master;
	}
	public void run()
	{
		try {
			byte[] chunkBytes = new byte[Constants.CHUNK_SIZE];
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filename));
			BufferedOutputStream bos = new BufferedOutputStream(mw.worker.getOutputStream());
			long skipped = bis.skip(chunkpart * Constants.CHUNK_SIZE);
			bis.read(chunkBytes, 0, chunkBytes.length);
			bos.write(chunkBytes, 0, chunkBytes.length);
			bos.flush();
			bis.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}
}
