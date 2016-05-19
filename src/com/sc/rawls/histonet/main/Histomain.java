package com.sc.rawls.histonet.main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import com.sc.rawls.histonet.net.JitMatrixPacket;
import com.sc.rawls.histonet.net.JitterRecv;

public class Histomain implements KeyListener{

	public static JitterRecv jr;
	private static JitMatrixPacket jmp;
	
	public static void main(String[] args)
	{
		try {
			 jr = new JitterRecv(9955, jmp);
			
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

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		System.out.println("Key Release!");
		
		if(e.getKeyCode() == KeyEvent.VK_E)
			jr.exit(true);
			
		
	}
}
