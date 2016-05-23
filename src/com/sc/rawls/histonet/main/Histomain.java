package com.sc.rawls.histonet.main;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.sc.rawls.histonet.data.PacketQueue;
import com.sc.rawls.histonet.gui.MainFrame;
import com.sc.rawls.histonet.net.JitterRecv;

public class Histomain {

	public static final int THREAD_SINGLE = 0;
	public static final int THREAD_MULTIPLE = 1;
	
	public static final int REPLY_NET_AND_SAVE = 0;
	public static final int REPLY_SAVE_ONLY = 1;
	public static final int REPLY_NET_ONLY = 2;
	
	public static int parallel_mode, max_threads;
	public static int reply_mode;
	public static JitterRecv jr;
	private static PacketQueue packQ;
	
	static MainFrame mf;
	static BufferedImage bImg;
	
	public static void main(String[] args) throws IOException
	{
		
		File f = new File("./images/HistoNetLogo.png");
		
		System.out.println(f.getCanonicalPath());
		
		BufferedImage bImg;
		try {
			bImg = ImageIO.read(f);
		} catch (IOException e1) {
			bImg = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
			System.out.println("Could Not Find Image");
		}
		
		
		mf = new MainFrame(bImg);
		packQ = new PacketQueue();
		
		parallel_mode = THREAD_SINGLE;
		max_threads = 1;
		reply_mode = REPLY_NET_AND_SAVE;
		try {
			 jr = new JitterRecv(9955, packQ);
			
			Thread t = new Thread(jr);
			t.start();
			
			//t.join();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}/* catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		while(!packQ.hasNext()){System.out.println("Delaying for a packet in main");
		}
		
		while(packQ.hasNext())
		{
			int[] imgData = packQ.getNext().convertToIntArr();
			
			for(int i = 0; i < imgData.length; i++)
			{
				imgData[i] = imgData[i] << 8;
				imgData[i] = imgData[i] >> 8;
			}
			

			BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
			WritableRaster wr = (WritableRaster)img.getRaster();
			wr.setPixels(0, 0, img.getWidth(), img.getHeight(), imgData);
			
			mf.setImage(img);
			
			while(!packQ.hasNext()){}
			
		}
		
		
	}


}
