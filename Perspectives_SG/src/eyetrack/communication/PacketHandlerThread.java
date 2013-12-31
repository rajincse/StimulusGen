package eyetrack.communication;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PacketHandlerThread extends Thread {
	private ConcurrentLinkedQueue<NetworkPacket> packetQueue;
	private PacketHandler packetHandler;
	public PacketHandlerThread(
			ConcurrentLinkedQueue<NetworkPacket> packetQueue
			,PacketHandler packetHandler)
	{
		this.packetQueue = packetQueue;
		this.packetHandler = packetHandler;
	}
	@Override
	public void run() {
		while(true)
		{
			if(!this.packetQueue.isEmpty())
			{
				try {
					
					NetworkPacket networkPacket = this.packetQueue.poll();
					String packet =  networkPacket.getPacket();
					if(this.packetHandler != null)
					{
						packet = this.packetHandler.handlePacket(packet);
					}
					
					OutputStream out = networkPacket.getOutputStream();
					PrintStream os = new PrintStream(out);
					os.print(packet);
					os.flush();
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				Thread.sleep(CommunicationModule.THREAD_SLEEP_TIME);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}