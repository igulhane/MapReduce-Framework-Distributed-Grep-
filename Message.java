/**
 * Message : Messages that are used for communication. 
 */
import java.io.Serializable;

public class Message implements Serializable {
	public int command;
	public Object data;

	public Message(int command, Object data) {
		super();
		this.data = data;
		this.command = command;
	}

	public int getCommand() {
		return command;
	}

	public void setCommand(int command) {
		this.command = command;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
}
