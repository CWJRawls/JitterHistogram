package com.sc.rawls.histonet.gui;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class MainFrame extends JFrame{

	private MainPanel mp;
	
	public MainFrame(BufferedImage img)
	{
		mp = new MainPanel(img);
		
		this.add(mp);
		this.setPreferredSize(new Dimension(550,550));
		this.setTitle("HistoNet");
		this.setResizable(false);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.pack();
		this.setVisible(true);
	}
	
	public void setImage(BufferedImage img)
	{
		mp.setImage(img);
	}
	
	
}
