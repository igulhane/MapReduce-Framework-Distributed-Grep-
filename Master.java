/**
 * Master : Assign the job to workers and display the GREP result. 
 */

import java.io.*;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Master {
	Object object = new Object();
	Object object2= new Object();
	String regex;
	ArrayList<String> fileList;
	static int resultsReceived=0;
	public ArrayList<String> workerSet;
	LinkedHashMap<Integer, String> workerList;
	LinkedHashMap<String,LinkedHashMap<String, String>> result;
	ServerSocket masterServer;
	ArrayList<FileInfo> rescheduleObjects;
	public Master() {
		rescheduleObjects= new ArrayList<FileInfo>();
		workerSet = new ArrayList<String>();
		fileList = new ArrayList<String>();
		workerList = new LinkedHashMap<Integer, String>();
		result = new LinkedHashMap<String, LinkedHashMap<String,String>>();
	}
	

	/**
	 * Initiates the grep opertaion 
	 */
	public void initiateGrep(String exp,String fileName) {
		regex=exp;
		String s[] = fileName.split(" ");
		for (String string : s) {
			fileList.add(string);
		}
		resultsReceived=0;
		result = new LinkedHashMap<String, LinkedHashMap<String,String>>();
		sendFileChunks();
	}


	/**
	 * Breaks the file into chunks and assigns job to workers. 
	 */
	public void sendFileChunks() {
		long current= System.currentTimeMillis();
		ExecutorService executor = Executors.newFixedThreadPool(30);	
		ArrayList<Integer> list = new ArrayList<Integer>();
		ArrayList<String> listIP = new ArrayList<String>();
		for (Integer string : workerList.keySet()) {
			listIP.add(workerList.get(string));
		}
		list.addAll(workerList.keySet());
		int k = 0;
		int count=0;
		//Assigning work to the Workers
		for (String string2 : fileList) {
			File file = new File(string2);
			long l = file.length();
			int rem = ((l % Constants.CHUNK_SIZE) > 0) ? 1 : 0;
			int length = (int) ((l / Constants.CHUNK_SIZE) + rem);
			count+=length;
			for (int i = 0; i < length; i++) {
				k = k % workerList.size();
				String mw = workerList.get(list.get(k));
				try {
					FileInfo filedata = new FileInfo(string2, i, regex);
					Message masterMessage = new Message(Constants.MASTER_SEND_FILE,filedata);
					MasterWorkerConnection mWorkerConnection = new MasterWorkerConnection(mw, this, list,filedata,listIP);
					mWorkerConnection.start();
					result.put(string2+i, new LinkedHashMap<String, String>());
					ObjectOutputStream oos;
					oos = new ObjectOutputStream(mWorkerConnection.out);
					oos.writeObject(masterMessage);
					oos.flush();
					Runnable worker = new MasterFileSend(string2, i, mWorkerConnection, this);
					executor.execute(worker);
					k++;
				} catch (IOException e) {
					workerList.remove(list.get(k));
					list.remove(k);
					//k--;
					i--;
				}
				
			}
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
		}
		
		while(Master.resultsReceived<count) {
			try {
				Thread.currentThread().sleep(10);						
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		long p=System.currentTimeMillis()-current;
		System.out.println("Grep Result");
		//Displaying the results
		for (Entry<String, LinkedHashMap<String,String>> i: result.entrySet()) {
			LinkedHashMap<String, String> temp=i.getValue();
			for (Entry<String, String> integer : temp.entrySet()) {
				System.out.println(integer.getValue().split("chunk")[0]+" :  "+ integer.getKey());
			}
		}
		System.out.println("Time taken :"+p);
		fileList = new ArrayList<String>();
	}

	
	public static void main(String args[]) {
		Master master = new Master();
		MasterListener listener = new MasterListener(master);
		listener.start();
		Scanner scanner = new Scanner(System.in);
		while(master.workerList.size()<3){
			try {
				Thread.currentThread().sleep(10);
			} catch (InterruptedException e) {
			}
		}
		while(true){
			System.out.println("Enter the Regex");
			String regex = scanner.nextLine();
			System.out.println("Enter the list of files");
			String fileName = scanner.nextLine();
			master.initiateGrep(regex,fileName);
			System.out.println("Do you want to continue?[y/n]");
			regex= scanner.nextLine();
			if(regex.equalsIgnoreCase("n")){
				System.exit(0);
			}
		}
	}
	

	/**
	 * Checks the presence of worker in List 
	 */
	public void addWorker(int string, String workerConnection) {
		if (!workerList.containsKey(string)) {
			workerList.put(string, workerConnection);
		}
	}
	

	/**
	 * Performance the merging operation for the results. 
	 */
	public void addResult(LinkedHashMap<String, String> lines, String string) {
		result.get(string).putAll(lines);
		synchronized (object) {
			Master.resultsReceived= Master.resultsReceived+1;
		}
	}
}
