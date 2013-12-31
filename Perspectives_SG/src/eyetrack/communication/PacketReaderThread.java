package eyetrack.communication;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PacketReaderThread extends Thread{
	private ServerSocket packetListener;
	private ConcurrentLinkedQueue<NetworkPacket> packetQueue;
	private PacketHandler packetHandler;
	public PacketReaderThread(ServerSocket packetListener,ConcurrentLinkedQueue<NetworkPacket> packetQueue, PacketHandler packetHandler )
	{
		this.packetListener = packetListener;
		this.packetQueue = packetQueue;
		this.packetHandler = packetHandler;
	}
	
	@Override
	public void run() {
		while(true)
		{
			try {
				Socket sender = this.packetListener.accept();
				
				InputStream in = sender.getInputStream();
				DataInputStream is = new DataInputStream(in);
				int length = is.available();
				byte[] buffer = new byte[length];
				is.readFully(buffer);
				
				String packet = new String(buffer);
				this.packetHandler.handlePacket(packet);
//				NetworkPacket networkMessage = new NetworkPacket(sender, packet);
//				this.packetQueue.add(networkMessage);
//				

//					Thread.sleep(CommunicationModule.THREAD_SLEEP_TIME);

			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
