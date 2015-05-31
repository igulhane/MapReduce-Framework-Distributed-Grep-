/**
 * FileInfo : Contains the File Object
 * 
 */
import java.io.Serializable;
import java.util.LinkedHashMap;

public class FileInfo implements Serializable {
	String name;
	int chunkNo;
	String regex;
	LinkedHashMap<String, String> result;
	public FileInfo(String name, int c, String exp) {
		this.name = name;
		chunkNo = c;
		regex = exp;
		result = new LinkedHashMap<String,String>();
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getChunkNo() {
		return chunkNo;
	}
	public void setChunkNo(int chunkNo) {
		this.chunkNo = chunkNo;
	}
	public String getRegex() {
		return regex;
	}
	public void setRegex(String regex) {
		this.regex = regex;
	}
	public LinkedHashMap<String, String> getResult() {
		return result;
	}
	public void setResult(LinkedHashMap<String, String> result) {
		this.result = result;
	}
	
}
