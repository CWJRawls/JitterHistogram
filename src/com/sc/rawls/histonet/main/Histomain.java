package com.sc.rawls.histonet.main;

import java.io.IOException;

import com.sc.rawls.histonet.data.PacketQueue;
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
	
	public static void main(String[] args)
	{
		parallel_mode = THREAD_SINGLE;
		max_threads = 1;
		reply_mode = REPLY_NET_AND_SAVE;
		try {
			 jr = new JitterRecv(9955, packQ);
			
			Thread t = new Thread(jr);
			t.start();
			
			t.join();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
