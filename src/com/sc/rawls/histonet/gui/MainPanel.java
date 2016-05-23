package com.sc.rawls.histonet.gui;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MainPanel extends JPanel {
	
	private int[] scale = {500, 500};
	
	private JLabel imgLabel;
	private BufferedImage jitImg;
	
	
	public MainPanel(BufferedImage img)
	{
		this.setLayout(null);
		
		jitImg = img;
		
		imgLabel = new JLabel(getScaledImage(img, scale));
		
		
		imgLabel.setBounds(25, 25, scale[0], scale[1]);
		
		this.add(imgLabel);
		
		this.setPreferredSize(new Dimension(550, 550));
		
	}
	
	private ImageIcon getScaledImage(BufferedImage img, int[] scale)
	{
		BufferedImage newImg = new BufferedImage(scale[0], scale[1], img.getType());
		
		Graphics2D g = newImg.createGraphics();
		
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		
		g.drawImage(img, 0, 0, scale[0], scale[1], null);
		
		g.dispose();
		
		return new ImageIcon(newImg);
	}
	
	public void setImage(BufferedImage img)
	{
		jitImg = img;
		
		imgLabel.setIcon(getScaledImage(jitImg, scale));
		
		this.revalidate();
		this.repaint();
	}

}
