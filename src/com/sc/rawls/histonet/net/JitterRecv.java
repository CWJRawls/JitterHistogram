package com.sc.rawls.histonet.net;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class JitterRecv implements Runnable{

	public static final String JMATRIX = "JMTX"; //packet identifier for receiving a matrix
	public static final String JLATENCY = "JMLP"; //packet identifier for latency data
	public static final String JMESS = "JMMP"; //packet identifier for messages
	
	public static final int BMASK0 = 0x000000FF;
	public static final int BMASK1 = 0x0000FF00;
	public static final int BMASK2 = 0x00FF0000;
	public static final int BMASK3 = 0xFF000000;
	
	//flag to leave threaded loop in socket
	public boolean exit = false;
	
	//socket for listening to things sent by jitter.
	private ServerSocket jit_sock;
	private Socket connect_sock;
	private JitMatrixPacket jmp;
	
	public JitterRecv(int port, JitMatrixPacket jmp) throws IOException
	{
		jit_sock = new ServerSocket(port);
		connect_sock = jit_sock.accept();
		this.jmp = jmp;
	}
	
	@Override
	public void run() {
		
		try {
		DataInputStream datain = new DataInputStream(connect_sock.getInputStream());
		while(!exit)
		{
				int test = datain.available();
				System.out.println(test);
				System.out.println("At the top of the loop");
				byte[] b = new byte[4];
				
				//datain.skipBytes(4);
				
				datain.read(b);
				
				//for(int i = 0; i < 4; i++)
					//System.out.print(Integer.toHexString(b[i]) + " ");

				String id = new String(b, "UTF-8");
				
				System.out.println(id);
				
				System.out.println("Reading More Data");
				datain.read(b);
				
				int size = 0;
				for(int i = 0; i < 4; i++)
				{
					System.out.print(Integer.toHexString(b[i]) + " ");
					size += (b[i] << (i * 8));
				}
				
				System.out.println(" = " + size);
				//String size = new String(b, "UTF-8");
				//
				//System.out.println(size);
				
				System.out.println("At the bottom of the loop");
				
				if(id.equals(JMATRIX) && size == 288)
				{
					//datain.skipBytes(24);
					System.out.println("Incoming Matrix!");
					int avail = datain.available();
					System.out.println(avail);
					
					byte[] mat_data = new byte[288];
					datain.readFully(mat_data);
					
					jmp.setPacketData(mat_data);
					
					System.out.println(jmp.hasData());
					
					for(int i = 0; i < 288; i++)
					{
					
						System.out.print(String.format("%2x", mat_data[i]) + " ");
						
						if(i % 4 == 3)
							System.out.println();
					}
					
					System.out.println();
					
					int dimcount = ((mat_data[16] & 0xFF) << 24) +  ((mat_data[17] & 0xFF) << 16) + ((mat_data[18] & 0xFF) << 8) + ((mat_data[19] & 0xFF));
					
					System.out.println("DimCount = " + dimcount);
					
					int mat_size = ((mat_data[276] & 0xFF) << 24) +  ((mat_data[277] & 0xFF) << 16) + ((mat_data[278] & 0xFF) << 8) + ((mat_data[279] & 0xFF)); 
					System.out.println(String.format(Integer.toHexString(mat_data[276])) + " " + String.format(Integer.toHexString(mat_data[277])) + " " + String.format(Integer.toHexString(mat_data[278])) + " " + String.format(Integer.toHexString(mat_data[279])));
					
					
					System.out.println("Matrix Byte Size = " + mat_size);
					
					mat_data = new byte[mat_size];
					
					datain.readFully(mat_data);
					
					jmp.setMatrixData(mat_data);
					System.out.println(jmp.hasMatrixData());
					System.out.println(jmp.getSizeInBytes());
					System.out.println("PlaneCount = " + jmp.getPlaneCount());
					
				}
				else if(id.equals(JMESS))
				{
					datain.readFully(new byte[size]);
				}
				
			}
		}catch (IOException e) {
				e.printStackTrace();
			}



		}
	
	public void exit(boolean b)
	{
		exit = b;
	}

}
