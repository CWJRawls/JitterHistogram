package com.sc.rawls.histonet.net;

import java.nio.ByteBuffer;

public class JitMatrixPacket {
	
	public static final int TYPE_CHAR = 0;
	public static final int TYPE_LONG = 1;
	public static final int TYPE_FLOAT32 = 2;
	public static final int TYPE_FLOAT64 = 3;
	
	public static final int PCKT_LENGTH = 288;
	
	private byte[] matrix; //byte representation of the matrix
	private int id; //type of packet
	private int size; //size of the packet
	private int planecount;
	private int type;
	private int dimcount;
	private int[] dim;
	private int[] dimstride;
	private int datasize;
	private double time;
	private boolean hasData = false;
	private boolean hasMatrixData = false;
	
	public JitMatrixPacket(){};
	
	public JitMatrixPacket(byte[] packet)
	{
		//find the size of the packet
		size = (packet[4] << 24) + (packet[5] << 16) + (packet[6] << 8) + (packet[7]);
		
		System.out.println("Current Size = " + size);
		
		dim = new int[32];
		dimstride = new int[32];
		if(size == PCKT_LENGTH)
		{
			for(int i = 0; i < packet.length; i+= 4)
			{
				System.out.println("i = " + i);
				if((i <= 16 || i >= 276) && i < 280)
				{
					int tInt = ((packet[i] & 0xFF) << 24) + ((packet[i + 1] & 0xFF) << 16) + ((packet[i + 2] & 0xFF) << 8) + (packet[i + 3] & 0xFF);
					
					System.out.println("i = " + i + "  tInt = " + tInt);
					
					switch(i)
					{
					case 0:
						id = tInt;
						break;
					case 8:
						planecount = tInt;
						break;
					case 12:
						type = tInt;
						break;
					case 16:
						dimcount = tInt;
						break;
					case 276:
						datasize = tInt;
						break;
					}
				}
				
				if(i > 16 && i < 148)
				{
					for(int j = i; j < 148; j += 4)
					{
						dim[(j - 20) / 4] = ((packet[j] & 0xFF) << 24) + ((packet[j + 1] & 0xFF) << 16) + ((packet[j + 2] & 0xFF) << 8) + (packet[j + 3] & 0xFF); 
					}
					
					i = 148;
				}
				
				if(i == 148)
				{
					for(int j = i; j < 276; j += 4)
					{
						dimstride[(j - 148) / 4] = ((packet[j] & 0xFF) << 24) + ((packet[j + 1] & 0xFF) << 16) + ((packet[j + 2] & 0xFF) << 8) + (packet[j + 3] & 0xFF); 
					}
					
					i = 272;
				}
				/*
				if(i == 276)
				{
					datasize = ((packet[276] & 0xFF) << 24) +  ((packet[277] & 0xFF) << 16) + ((packet[278] & 0xFF) << 8) + ((packet[279] & 0xFF)); 
				}
				*/
				
				if(i == 280)
				{
					byte[] timeB = {packet[280], packet[281], packet[282], packet[283], packet[284], packet[285], packet[286], packet[287]};
					time = ByteBuffer.wrap(timeB).getDouble();
				}
			}
			
			hasData = true;
		}
	}
	
	
	//method to check if there is packet data available
	public boolean hasData()
	{
		return hasData;
	}
	
	//method to check if there is data available for use
	public boolean hasMatrixData()
	{
		return hasMatrixData;
	}
	
	//method to get the raw byte array of the matrix
	public byte[] getByteArray()
	{
		return matrix;
	}
	
	//method to return the array type
	public int getType()
	{
		return type;
	}
	
	//method to convert the matrix data to a singular 4-byte int array
	public int[] convertToIntArr()
	{
		return new int[10];
	}
	
	//method to set the byte contents of the matrix
	public void setMatrixData(byte[] data)
	{
		matrix = data.clone();
		
		System.out.println("Matrix Length = " + matrix.length);
		System.out.println("Datasize = " + datasize);
		if(matrix.length == datasize)
		{
			hasMatrixData = true;
		}
	}
	
	public void setPacketData(byte[] packet)
	{
		//find the size of the packet
				size = (packet[4] << 24) + (packet[5] << 16) + (packet[6] << 8) + (packet[7]);
				
				System.out.println("Current Size = " + size);
				
				dim = new int[32];
				dimstride = new int[32];
				if(size == PCKT_LENGTH)
				{
					for(int i = 0; i < packet.length; i+= 4)
					{
						System.out.println("i = " + i);
						if((i <= 16 || i >= 276) && i < 280)
						{
							int tInt = ((packet[i] & 0xFF) << 24) + ((packet[i + 1] & 0xFF) << 16) + ((packet[i + 2] & 0xFF) << 8) + (packet[i + 3] & 0xFF);
							
							System.out.println("i = " + i + "  tInt = " + tInt);
							
							switch(i)
							{
							case 0:
								id = tInt;
								break;
							case 8:
								planecount = tInt;
								break;
							case 12:
								type = tInt;
								break;
							case 16:
								dimcount = tInt;
								break;
							case 276:
								datasize = tInt;
								break;
							}
						}
						
						if(i > 16 && i < 148)
						{
							for(int j = i; j < 148; j += 4)
							{
								dim[(j - 20) / 4] = ((packet[j] & 0xFF) << 24) + ((packet[j + 1] & 0xFF) << 16) + ((packet[j + 2] & 0xFF) << 8) + (packet[j + 3] & 0xFF); 
							}
							
							i = 148;
						}
						
						if(i == 148)
						{
							for(int j = i; j < 276; j += 4)
							{
								dimstride[(j - 148) / 4] = ((packet[j] & 0xFF) << 24) + ((packet[j + 1] & 0xFF) << 16) + ((packet[j + 2] & 0xFF) << 8) + (packet[j + 3] & 0xFF); 
							}
							
							i = 272;
						}
						/*
						if(i == 276)
						{
							datasize = ((packet[276] & 0xFF) << 24) +  ((packet[277] & 0xFF) << 16) + ((packet[278] & 0xFF) << 8) + ((packet[279] & 0xFF)); 
						}
						*/
						
						if(i == 280)
						{
							byte[] timeB = {packet[280], packet[281], packet[282], packet[283], packet[284], packet[285], packet[286], packet[287]};
							time = ByteBuffer.wrap(timeB).getDouble();
						}
					}
					
					hasData = true;
					
					if(matrix != null && datasize == matrix.length)
					{
						hasMatrixData = true;
					}
				}
				
	}
	
	public int getPlaneCount()
	{
		return planecount;
	}
	
	public int[] getIntData()
	{
		int[] data = new int[dim[0] * dim[1]];
		
		int planestride = dimstride[0] + dimstride[1];
		
		if(hasData)
		{
			if(type == TYPE_CHAR)
			{
				for(int i = 0; i < data.length; i++)
				{
					data[i] = 0;
					
					for(int j = 0; j < planecount; j++)
					{
						data[i] += (matrix[j * planestride] << (j * 8));
					}
				}
			}
			else if(type == TYPE_FLOAT32)
			{
				for(int i = 0; i < data.length; i++)
				{
					data[i] = 0;
					
					for(int j = 0; j < planecount; j++)
					{
						byte[] bytes = {matrix[((i * 4) + (j * planestride))], matrix[((i * 4) + (j * planestride) + 1)], matrix[((i * 4) + (j * planestride) + 2)], matrix[((i * 4) + (j * planestride) + 3)]};
						float f = DataConversion.byteToFloat(bytes);
						int bits = (int)(f * 255);
						if(bits > 255)
							bits = 255;
						
						data[i] += bits << (j * 8);
					}
				}
			}
		}
		return data;
	}
	
	public byte[] getMatrixData()
	{
		if(hasMatrixData)
		{
			return matrix;
		}
		else
		{
			return new byte[10];
		}
	}
	
	public int getSizeInBytes()
	{
		if(matrix == null)
		{
			return 0;
		}
		else
		{
			return matrix.length;
		}
	}
	
	public int getDimcount()
	{
		return dimcount;
	}
	
	public double getTime()
	{
		return time;
	}
}
