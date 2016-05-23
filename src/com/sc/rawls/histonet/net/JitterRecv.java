package com.sc.rawls.histonet.net;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.sc.rawls.histonet.data.PacketQueue;

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
	private PacketQueue pq;
	private JitMatrixPacket jmp;
	
	public JitterRecv(int port, PacketQueue pq) throws IOException
	{
		jit_sock = new ServerSocket(port);
		connect_sock = jit_sock.accept();
		jmp = new JitMatrixPacket();
	}
	
	@Override
	public void run() {
		
		try {
			//create data reader for the input stream
			DataInputStream datain = new DataInputStream(connect_sock.getInputStream());
		//main loop on thread, controlled by boolean exit, which can be modified by calling exit(boolean)
		while(!exit)
		{
				//checking remaining readable bytes
				int test = datain.available();
				System.out.println(test);
				
				//allocate to get header
				byte[] b = new byte[4];
				
				//get the packet header
				datain.read(b);

				//convert the header id to a string
				String id = new String(b, "UTF-8");
				
				System.out.println(id);
				
				//get the size of the next packet
				datain.read(b);
				
				//shift the bytes into a 4-byte integer to hold the size (little endian)
				int size = 0;
				for(int i = 0; i < 4; i++)
				{
					System.out.print(Integer.toHexString(b[i]) + " ");
					size += (b[i] << (i * 8));
				}
				
				System.out.println(" = " + size);
				
				//if we are going to receive a packet for matrices
				if(id.equals(JMATRIX) && size == 288)
				{
					//create a new object to hold the packet data
					jmp = new JitMatrixPacket();
					
					//check how much data is available to be read
					int avail = datain.available();
					System.out.println(avail);
					
					//allocate a byte array to read in the matrix information packet
					byte[] mat_data = new byte[288];
					//read the matrix data packet to the array
					datain.readFully(mat_data);
					
					//send the data to the object to be parsed and stored (big endian)
					jmp.setPacketData(mat_data);
					
					//console check to make sure data was parsed correctly
					System.out.println(jmp.hasData());
					
					/*
					//Console Check of Matrix information (hex values)
					for(int i = 0; i < 288; i++)
					{
					
						System.out.print(String.format("%2x", mat_data[i]) + " ");
						
						if(i % 4 == 3)
							System.out.println();
					}
					
					*/
					
					System.out.println();
					
					//get the dimension count from the information (big endian)
					int dimcount = ((mat_data[16] & 0xFF) << 24) +  ((mat_data[17] & 0xFF) << 16) + ((mat_data[18] & 0xFF) << 8) + ((mat_data[19] & 0xFF));
					
					//console check on number of dimensions
					System.out.println("DimCount = " + dimcount);
					
					//get the byte size of the matrix (big endian)
					int mat_size = ((mat_data[276] & 0xFF) << 24) +  ((mat_data[277] & 0xFF) << 16) + ((mat_data[278] & 0xFF) << 8) + ((mat_data[279] & 0xFF)); 
					
					//console check on matrix byte size
					System.out.println(String.format(Integer.toHexString(mat_data[276])) + " " + String.format(Integer.toHexString(mat_data[277])) + " " + String.format(Integer.toHexString(mat_data[278])) + " " + String.format(Integer.toHexString(mat_data[279])));
					System.out.println("Matrix Byte Size = " + mat_size);
					
					//reinit the byte array to hold the matrix data
					mat_data = new byte[mat_size];
					
					//read the matrix data
					datain.readFully(mat_data);
					
					//set the matrix data in the storage object
					jmp.setMatrixData(mat_data);
					
					//Console Checks on whether matrix data is registered, the size, and planecount
					System.out.println(jmp.hasMatrixData());
					System.out.println(jmp.getSizeInBytes());
					System.out.println("PlaneCount = " + jmp.getPlaneCount());
					
					//add the matrix to the queue
					pq.add(jmp);
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
