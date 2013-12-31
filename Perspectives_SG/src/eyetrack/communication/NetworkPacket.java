package eyetrack.communication;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class NetworkPacket {
	private Socket sender;
	private String packet;
	public NetworkPacket(Socket sender, String packet)
	{
		this.sender = sender;
		this.packet = packet;
	}
	public Socket getSender() {
		return sender;
	}
	public void setSender(Socket sender) {
		this.sender = sender;
	}
	public String getPacket() {
		return packet;
	}
	public void setPacket(String packet) {
		this.packet = packet;
	}
	public OutputStream getOutputStream() throws IOException {
		// TODO Auto-generated method stub
		return this.sender.getOutputStream();
	}
	
}
