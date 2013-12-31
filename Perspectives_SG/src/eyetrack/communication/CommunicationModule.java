package eyetrack.communication;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CommunicationModule {
	public static final int THREAD_SLEEP_TIME = 10;
	public static final int PACKET_RETRY_INTERVAL = 1000;
	
	private int portNumber;
	private ServerSocket packetListener;
	private PacketReaderThread packetReaderThread;
	private PacketHandlerThread packetHandlerThread;
	private PacketHandler packetHandler;
	private ConcurrentLinkedQueue<NetworkPacket> packetQueue;
	
	public CommunicationModule( int portNumber,  PacketHandler packetHandler)
	{
		try {
		this.portNumber = portNumber;
		this.packetHandler = packetHandler;		
	
		this.packetListener = new ServerSocket(this.portNumber);		
		this.packetQueue = new ConcurrentLinkedQueue<NetworkPacket>();
		this.packetReaderThread = new PacketReaderThread(packetListener, packetQueue, this.packetHandler);
		this.packetHandlerThread = new PacketHandlerThread(packetQueue, this.packetHandler);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void startCommunication()
	{
		this.packetHandlerThread.start();
		this.packetReaderThread.start();		
	}
	public String sendPacket(String packet,String host, int portNumber)
	{
		String responsePacket = null;
		try {
			Socket socket = new Socket(host, portNumber);
			OutputStream out = socket.getOutputStream();
			
			PrintStream os = new PrintStream(out);
			os.println(packet);
			
			
			InputStream in = socket.getInputStream();
			DataInputStream is =new DataInputStream(in);
			int length = is.available();
			byte[] buffer = new byte[length];
			is.readFully(buffer);
			responsePacket  = new String(buffer);
			os.close();
			is.close();
			
		} catch (UnknownHostException e) {
			System.out.println("Retrying to Send message (Unknown Host)");
			try {
				Thread.sleep(CommunicationModule.PACKET_RETRY_INTERVAL);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			this.sendPacket(packet, host, portNumber);
			
		} catch (IOException e) {
			System.out.println("Retrying to Send message (IO Exception)");
			e.printStackTrace();
			try {
				Thread.sleep(CommunicationModule.PACKET_RETRY_INTERVAL);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			this.sendPacket(packet, host, portNumber);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		return responsePacket;
		
	}
	
	
	
	public int getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}

	
	
}
