package com.sc.rawls.histonet.io;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageWriter {

	public static void writeImage(BufferedImage img, String path)
	{
		String end = path.substring(path.length() - 4);
		
		switch(end)
		{
		case ".jpg":
			path = path.substring(0, path.length() - 4) + ".png";
			break;
		case ".gif":
			path = path.substring(0, path.length() - 4) + ".png";
			break;
		case ".png": //empty case since this is what we want to save as
			break;
		default :
			path += ".png";
			break;
		}
		
		File outfile = new File(path);
		try {
			ImageIO.write(img, "png", outfile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
