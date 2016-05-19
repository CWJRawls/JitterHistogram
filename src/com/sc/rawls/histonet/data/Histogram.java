package com.sc.rawls.histonet.data;

public class Histogram {

	private int[][][] bins = new int[256][256][256];
	private int[][][] colors = new int[256][256][256];
	private int[] sig_bins; //avoid allocating memory if we don't need it
	private int[] sig_colors;
	
	private int sortFlag = 0;
	
	//used to avoid functions that are expecting a certain array size
	private int colorResFlag;
	
	public static final int COLOR_RES_256 = 0;
	public static final int COLOR_RES_64 = 1;
	public static final int COLOR_RES_32 = 2;
	public static final int COLOR_RES_16 = 3;
	
	
	public Histogram(int crf)
	{
		//set the resolution flag
		colorResFlag = crf;
		
		//initialize the matrices
		if(crf == COLOR_RES_256)
			initMatrices(256, 1);
		else if(crf == COLOR_RES_64)
			initMatrices(64, 0);
		else if(crf == COLOR_RES_32)
			initMatrices(32, 0);
		else if(crf == COLOR_RES_16)
			initMatrices(16, 0);
		else
			initMatrices(256, 1); //default, though there should never be any sorting done if crf does not match a defined flag
			
		
		System.out.println("Finished Constructor");
	}
	
	/*------------------------------*/
	/*      UTILITY FUNCTIONS       */
	/*------------------------------*/
	
	//only take one int since all matrices should be cubic
	private void reInitMatrices(int dim)
	{
		colors = new int[dim][dim][dim];
		bins = new int[dim][dim][dim];
		
		for(int i = 0; i < dim; i++)
		{
			for(int j = 0; j < dim; j++)
			{
				for(int k = 0; k < dim; k++)
				{
					colors[i][j][k] = 0;
					bins[i][j][k] = 0;
				}
			}
		}
	}
	
	//placed here since it is not expecting an array of a predetermined size
	private void sortSigArrays()
	{
		for(int i = 1; i < sig_bins.length - 1; i++)
		{
			int temp = sig_bins[i];
			int temp_c = sig_colors[i];
			
			for(int j = i - 1; j >= 0 && temp > sig_bins[j]; j--)
			{
				sig_bins[j + 1] = sig_bins[j];
				sig_colors[j + 1] = sig_colors[j];
				sig_bins[j] = temp;
				sig_colors[j] = temp_c;
			}
		}
	}
	
	//utility function for initializing arrays for holding the most signficant values so far
	private void initSigArrays(int dim)
	{
		sig_bins = new int[dim];
		sig_colors = new int[dim];
		
		for(int i = 0; i < dim; i++)
		{
			sig_bins[i] = 0;
			sig_colors[i] = 0;
		}
	}
	
	//utility function to initialize main arrays to correct size for color resolution
	private void initMatrices(int dim, int color_init)
	{
		bins = new int[dim][dim][dim];
		colors = new int[dim][dim][dim];
		
		for(int i = 0; i < dim; i++)
		{
			for(int j = 0; j < dim; j++)
			{
				for(int k = 0; k < dim; k++)
				{
					bins[i][j][k] = 0;
					if(color_init == 1)
						colors[i][j][k] = (i) + (j << 8) + (k << 16);
					else
						colors[i][j][k] = 0;
				}
			}
		}
	}
	
	//utility function to compare whether a color is outside a threshold
	private boolean isDifferent(int control, int source, int delta)
	{
		int ar = 0xFF & control;
		int ag = 0xFF & (control >> 8);
		int ab = 0xFF & (control >> 16);
		
		int br = 0xFF & source;
		int bg = 0xFF & (source >> 8);
		int bb = 0xFF & (source >> 16);
		
		int dr = Math.abs(ar - br);
		int dg = Math.abs(ag - bg);
		int db = Math.abs(ab - bb);
		
		if(dr < delta && dg < delta && db < delta)
			return false;
		else
			return true;
	}
	
	
	/* Functions Below this are grouped by the number of values per color channel. */
	
	/*-------------------------------*/
	/* STANDARD 256 COLOR RESOLUTION */
	/*-------------------------------*/
	public int addToBin(int data)
	{
		//Avoid misplaced calls
		if(colorResFlag != 0)
			return 0;
		
		int r_val = data & 0xFF;
		int g_val = (data >> 8) & 0xFF;
		int b_val = (data >> 16) & 0xFF;
		
		bins[r_val][g_val][b_val]++;
		
		return 1;
	}
	
	public int blueMergeSortBins()
	{
		if(colorResFlag != 0)
			return 0;
		
		sortFlag = 1;
		//first sort all blue rows in z dim
		for(int i = 0; i < 65536; i++)
		{
			int x = i % 256;
			int y = i / 256;
			
			for(int z = 1; z < 256; z++)
			{
				int temp = bins[x][y][z];
				int temp_c = colors[x][y][z];
				
				for(int j = z - 1; j >= 0 && temp > bins[x][y][j]; j--)
				{
					bins[x][y][j + 1] = bins[x][y][j];
					colors[x][y][j + 1] = colors[x][y][j];
					
					bins[x][y][j] = temp;
					colors[x][y][j] = temp_c;
				}
			}
		}
		
		//sort remaining dimensions
		for(int i = 1; i < 65536; i++)
		{
			int temp = bins[i % 256][i / 256][0];
			int temp_c = colors[i % 256][i / 256][0];
			for(int j = i - 1; j >= 0 && temp > bins[i % 256][i / 256][0]; j--)
			{
				bins[(j + 1) % 256][(j + 1) / 256][0] = bins[j % 256][j / 256][0];
				colors[(j + 1) % 256][(j + 1) / 256][0] = colors[j % 256][j / 256][0];
				
				bins[j % 256][j / 256][0] = temp;
				colors[j % 256][j / 256][0] = temp_c;
			}
		}
		
		return 1;
	}
	
	public int greenMergeSortBins()
	{
		
		if(colorResFlag != 0)
			return 0;
		
		sortFlag = 2;
		
		//First sort the green dimension
		for(int i = 0; i < 65536; i++)
		{
			for(int j = 1; j < 256; j++)
			{
				int temp = bins[i % 256][j][i / 256];
				int temp_c = colors[i % 256][j - 1][i / 256]; 
				
				for(int z = j - 1; z >= 0 && temp > bins[i % 256][z][i / 256]; z--)
				{
					bins[i % 256][z + 1][i / 256] = bins[i % 256][z][i / 256];
					colors[i % 256][z + 1][i / 256] = colors[i % 256][z][i / 256];
					
					bins[i % 256][z][i / 256] = temp;
					colors[i % 256][z][i / 256] = temp_c;
				}
			}
		}
		
		//Then sort the rest of the matrix
		
		for(int i = 1; i < 65536; i++)
		{
			int temp = bins[i % 256][0][i / 256];
			int temp_c = colors[i % 256][0][i / 256];
			
			for(int j = i - 1; j >= 0 && temp > bins[j % 256][0][j / 256]; j--)
			{
				bins[(j + 1) % 256][0][(j + 1) / 256] = bins[j % 256][0][j / 256];
				colors[(j + 1) % 256][0][(j + 1) / 256] = colors[j % 256][0][j / 256];
				
				bins[j % 256][0][j / 256] = temp;
				colors[j % 256][0][j / 256] = temp_c;
				
			}
		}
		
		return 1;
	}
	
	public int redMergeSortBins()
	{
		if(colorResFlag != 0)
			return 0;
		
		sortFlag = 2;
		
		//First sort the red dimension
		System.out.println("Merging Red Values");
		for(int i = 0; i < 65536; i++)
		{
			int a = i % 256;
			int b = i / 256;
			System.out.println("at: " + "green " + a + "| blue " + b);
			
			for(int j = 1; j < 256; j++)
			{
				int temp = bins[j - 1][i % 256][i / 256];
				int temp_c = colors[j - 1][i % 256][i / 256]; 
				
				for(int z = j - 1; z >= 0 && temp > bins[z][i % 256][i / 256]; z--)
				{
					bins[z + 1][i % 256][i / 256] = bins[z][i % 256][i / 256];
					colors[z + 1][i % 256][i / 256] = colors[z][i % 256][i / 256];
					
					bins[z][i % 256][i / 256] = temp;
					colors[z][i % 256][i / 256] = temp_c;
				}
			}
		}
		
		//Then sort the rest of the matrix
		
		for(int i = 1; i < 65536; i++)
		{
			int temp = bins[0][i % 256][i / 256];
			int temp_c = colors[0][i % 256][i / 256];
			
			int a = i % 256;
			int b = i / 256;
			System.out.println("Sorting for: 0|" + a + "|" + b + " " + temp + "|" + temp_c);
			
			for(int j = i - 1; j >= 0 && temp > bins[0][j % 256][j / 256]; j--)
			{
				bins[0][(j + 1) % 256][(j + 1) / 256] = bins[0][j % 256][j / 256];
				colors[0][(j + 1) % 256][(j + 1) / 256] = colors[0][j % 256][j / 256];
				
				bins[0][j % 256][j / 256] = temp;
				colors[0][j % 256][j / 256] = temp_c;
				
			}
		}
		
		return 1;
	}
	
	/* Disabled for how slow it is
	public int accurateSortBins()
	{
		if(colorResFlag != 0)
			return 0;
		
		sortFlag = 4;
		
		for(int j = 0; j < 256; j++) //z loop
		{
			for(int i = 0; i < 65536; i++) //xy loop
			{
				int temp = bins[i % 256][i / 256][j];
				int temp_c = colors[i % 256][i / 256][j];
				int a = i % 256;
				int b = i / 256;
				int k = i - 1;
				int l = j;
				
				if(k < 0)
				{
					k = 65535;
					l -= 1;
				}
				
				System.out.print("Sorting for: " + a + "|" + b + "|" + j + " k=" + k + " L=" + l + " bin_count=" + temp);
				
				while(l >= 0 && temp > bins[k % 256][k / 256][l] && temp > 100)
				{
					//swap values
					if(k == 65535 && l < 255)
					{
						bins[0][0][l + 1] = bins[k % 256][k / 256][l];
						colors[0][0][l + 1] = colors[k % 256][k / 256][l];
						
						bins[k % 256][k / 256][l] = temp;
						colors[k % 256][k / 256][l] = temp_c;
					}
					else
					{
						bins[(k + 1) % 256][(k + 1) / 256][l] = bins[k % 256][k / 256][l];
						colors[(k + 1) % 256][(k + 1) / 256][l] = colors[k % 256][k / 256][l];
						
						bins[k % 256][k / 256][l] = temp;
						colors[k % 256][k / 256][l] = temp_c;
					}
					//decrement and prep for next iteration
					k--;
					if(k < 0)
					{
						l--;
						k = 65535;
					}
				}
				System.out.println(" K=" + k + " L=" + l );
				
			}
		}
		
		return 1;
	}
	*/
	
	public int quickAccurateSort()
	{
		if(colorResFlag != 0)
			return 0;
		
		sortFlag = 5;
		sig_bins = new int[512];
		sig_colors = new int[512];
		for(int i = 0; i < 64; i++)
		{
			sig_bins[i] = 0;
			sig_colors[i] = 0;
		}
		
		int head = 0; //variable for us to find where we are for copying data
		
		for(int z = 0; z < 256; z++)
		{
			for(int y = 0; y < 256; y++)
			{
				for(int x = 0; x < 256; x++)
				{
					if(bins[x][y][z] > 10)
					{
						boolean unique = true;
						for(int i = 0; i < sig_bins.length && sig_bins[i] != 0 && unique; i++)
						{
							unique = isDifferent(sig_colors[i], colors[x][y][z], 32);
						}
						//if we have not yet filled the array of significant values
						if(head < sig_bins.length - 1 && unique)
						{
							sig_bins[head] = bins[x][y][z];
							sig_colors[head] = colors[x][y][z];
							
							head++;
								
							//if that value filled the array, then sort them
							if(head == sig_bins.length - 1)
							{
								sortSigArrays();
							}
						}
						//if the array is full, but this value is more significant
						else if(head == sig_bins.length - 1 && bins[x][y][z] > sig_bins[head] && unique)
						{
							sig_bins[head] = bins[x][y][z];
							sig_colors[head] = colors[x][y][z];
								
							//sort to make sure the the color of lowest occurence is always at the end.
							sortSigArrays();
						}
					}
				}
			}
		}
		
		//on the off chance there were not enough values that fit the range, then sort before exiting the function
		if(head < 63)
		{
			sortSigArrays();
		}
		
		return 1;
	}
	
	
	/*----------------------*/
	/* 64 COLOR RESOLUTION  */
	/*----------------------*/
	
	public int addToBin64(int color)
	{
		//early return with a zero to signify improper function call
		if(colorResFlag != COLOR_RES_64)
			return 0;
		
		
		int r = 0xFF & (color);
		int g = 0xFF & (color >> 8);
		int b = 0xFF & (color >> 16);
		int offset = (r % 4) + ((g % 4) * 4) + ((b % 4) * 16);
		
		//System.out.println("Offset = " + offset);
		
		r /= 4;
		g /= 4;
		b /= 4;
		
		bins[r][g][b]++;
		colors[r][g][b] += offset;
		
		return 1;
		
	}
	
	//Named and used specifically for when dealing with 64 colors in r/g/b
	public int calcAverageBinColor64()
	{
		if(colorResFlag != COLOR_RES_64)
			return 0;
		
		System.out.println("");
		System.out.print("Averaging Bins ");
		//using a single value to traverse matrix with single loop
		for(int x = 0; x < 64; x++)
		{
			for(int y = 0; y < 64; y++)
			{
				for(int z = 0; z < 64; z++)
				{	
					if(bins[x][y][z] != 0)
					{
						int c = colors[x][y][z] / bins[x][y][z]; //get the average offset
						
						System.out.println("c = " + c);
						
						//find the top left color for this group of bins.
						int base_color = ((x * 4)) + ((y * 4) << 8) + ((z * 4) << 16);
						
						//get the offsets for each color
						int b = c / 16; //find the plane of the 3d offset matrix
						int g = (c - b * 16) / 4; //find the row within the offset plane
						int r = (c - b * 16) % 4; //find the column within the offset plane

						
						System.out.print("Offsets " + c + " " + r + "|" + g +"|" + b);						
						
						//add average offsets into the base
						base_color += r + (g << 8) + (b << 16);
						
						System.out.println(" Final Color: " + base_color);
						
						//set it back into the array;
						colors[x][y][z] = base_color;
					}
				}
			}
		}
		
		return 1;
	}
	
	public int fullSortColors64()
	{
		//color resolution check
		if(colorResFlag != COLOR_RES_64)
			return 0;
		
		sortFlag = 6;
		System.out.print("Sorting Data");
		for(int z = 0; z < 64; z++)
		{
			for(int i = 0; i < 4096; i++)
			{
				/*
				//printing methods
				if(i % 10 == 0)
					System.out.print("#");
				if(i % 1000 == 0)
					System.out.println("");
					*/
				
				int temp = bins[i % 64][i / 64][z];
				int temp_c = colors[i % 64][i / 64][z];
				int k = i - 1;
				int l = z;
				
				if(k < 0)
				{
					k = 4095;
					l--;
				}
				 //it is given in this sorting system that not all data is significant, and we only care about data over a certain threshold
				while(l > -1 && bins[k % 64][k / 64][l] < temp && temp > 100)
				{
					if(k == 4095)
					{
						bins[0][0][l + 1] = bins[63][63][l];
						colors[0][0][l + 1] = colors[63][63][l];
						
						bins[63][63][l] = temp;
						colors[63][63][l] = temp_c;
					}
					else
					{
						bins[(k + 1) % 64][(k + 1) / 64][l] = bins[k % 64][k / 64][l];
						colors[(k + 1) % 64][(k + 1) / 64][l] = colors[k % 64][k / 64][l];
						
						bins[k % 64][k / 64][l] = temp;
						colors[k % 64][k / 64][l] = temp_c;
					}
					
					k--;
					
					if(k < 0)
					{
						k = 4095;
						l--;
					}
					
				}
			}
		}
		
		return 1;
	}
	
	//sorts though all the bins and returns a sorted list of the top 256 values (over the specified threshold)
	public int bin256Sort64()
	{
		//color resolution check
		if(colorResFlag != COLOR_RES_64)
			return 0;
		
		System.out.print("Sorting Data");
		//set the sorting flag to avoid trying to return the incorrect values
		sortFlag = 7;
		int head = 0;
		initSigArrays(256);
		
		for(int x = 0; x < 64; x++)
		{
			for(int y = 0; y < 64; y++)
			{
				for(int z = 0; z < 64; z++)
				{
					/*
					//printing methods
					if(x % 4 == 0)
						System.out.print("#");
					if(y / 63 == 1)
						System.out.println(""); */
					
					if(bins[x][y][z] > 10)
					{
						boolean unique = true;
						for(int i = 0; i < sig_bins.length && sig_bins[i] != 0 && unique; i++)
						{
							unique = isDifferent(sig_colors[i], colors[x][y][z], 32);
						}
						//if we have not yet filled the array of significant values
						if(head < sig_bins.length - 1 && unique)
						{
							sig_bins[head] = bins[x][y][z];
							sig_colors[head] = colors[x][y][z];
							
							head++;
								
							//if that value filled the array, then sort them
							if(head == sig_bins.length - 1)
							{
								sortSigArrays();
							}
						}
						//if the array is full, but this value is more significant
						else if(head == sig_bins.length - 1 && bins[x][y][z] > sig_bins[head] && unique)
						{
							sig_bins[head] = bins[x][y][z];
							sig_colors[head] = colors[x][y][z];
								
							//sort to make sure the the color of lowest occurence is always at the end.
							sortSigArrays();
						}
					}
				}
			}
		}
		
		//if for seom reason we did not fill the entire array, then sort before returning
		if(head < sig_bins.length - 1)
		{
			sortSigArrays();
		}
		
		return 1;
	}
	
	
	/*--------------------------------*/
	/*      32 COLOR RESOLUTION       */
	/*--------------------------------*/
	
	public int addToBin32(int data)
	{
		if(colorResFlag != COLOR_RES_32)
			return 0;
		
		
		sortFlag = 8;
		/* Offset values run 0 - 512 for each bin */
		int r = 0xFF & data;
		int g = (data >> 8) & 0xFF;
		int b = (data >> 16) & 0xFF;
		
		int offset = (r % 8) + ((g % 8) * 8) + ((b % 8) * 64);
		
		r /= 8;
		g /= 8;
		b /= 8;
		
		bins[r][g][b]++;
		colors[r][g][b] += offset;
		
		return 1;
	}
	
	public int calcAverageBinColor32()
	{
		//color resolution check
		if(colorResFlag != COLOR_RES_32)
			return 0;
		
		for(int x = 0; x < 32; x++)
		{
			for(int y = 0; y < 32; y++)
			{
				for(int z = 0; z < 32; z++)
				{
					if(bins[x][y][z] != 0)
					{
						int base_color = (x * 8) + ((y * 8) << 8) + ((z * 8) << 16);
						
						int c = colors[x][y][z] / bins[x][y][z];
						
						int b = c / 64;
						int g = (c - (b * 64)) / 8;
						int r = (c - (b * 64)) % 8;
						
						base_color += r + (g << 8) + (b << 16);
						
						colors[x][y][z] = base_color;
					}
				}
			}
		}
		
		return 1;
	}
	
	public int fullSortColors32()
	{
		if(colorResFlag != COLOR_RES_32)
			return 0;
		
		sortFlag = 8;
		
		for(int z = 0; z < 32; z++)
		{
			for(int i = 0; i < 1024; i++)
			{
				int k = i - 1;
				int l = z;
				
				int temp = bins[i % 32][i / 32][l];
				int temp_c = colors[i % 32][i / 32][l];
				
				if(k < 0)
				{
					k = 1023;
					l--;
				}
				
				while(l > -1 && bins[k % 32][k / 32][l] < temp && temp > 100)
				{
					if(k == 1023)
					{
						bins[0][0][l + 1] = bins[31][31][l];
						colors[0][0][l + 1] = colors[31][31][l];
						
						bins[31][31][l] = temp;
						colors[31][31][l] = temp_c;
					}
					else
					{
						bins[(k + 1) % 32][(k + 1) / 32][l] = bins[k % 32][k / 32][l];
						colors[(k + 1) % 32][(k + 1) / 32][l] = colors[k % 32][k / 32][l];
						
						bins[k % 32][k / 32][l] = temp;
						colors[k % 32][k / 32][l] = temp_c;
					}
					
					k--;
					
					if(k < 0)
					{
						k = 1023;
						l--;
					}
					
				}
			}
		}
		
		return 1;
	}
	
	//sorts though all the bins and returns a sorted list of the top 256 values (over the specified threshold)
	public int bin256Sort32()
	{
		//color resolution check
		if(colorResFlag != COLOR_RES_32)
			return 0;
			
		System.out.print("Sorting Data");
		//set the sorting flag to avoid trying to return the incorrect values
		sortFlag = 9;
		int head = 0;
		initSigArrays(256);
		
		for(int x = 0; x < 32; x++)
		{
			for(int y = 0; y < 32; y++)
			{
				for(int z = 0; z < 32; z++)
				{
					/*
					//printing methods
					if(x % 4 == 0)
						System.out.print("#");
					if(y / 63 == 1)
						System.out.println(""); */
						
					if(bins[x][y][z] > 10)
					{
						boolean unique = true;
						for(int i = 0; i < sig_bins.length && sig_bins[i] != 0 && unique; i++)
						{
							unique = isDifferent(sig_colors[i], colors[x][y][z], 32);
						}
						//if we have not yet filled the array of significant values
						if(head < sig_bins.length - 1 && unique)
						{
							sig_bins[head] = bins[x][y][z];
							sig_colors[head] = colors[x][y][z];
							
							head++;
								
							//if that value filled the array, then sort them
							if(head == sig_bins.length - 1)
							{
								sortSigArrays();
							}
						}
						//if the array is full, but this value is more significant
						else if(head == sig_bins.length - 1 && bins[x][y][z] > sig_bins[head] && unique)
						{
							sig_bins[head] = bins[x][y][z];
							sig_colors[head] = colors[x][y][z];
								
							//sort to make sure the the color of lowest occurence is always at the end.
							sortSigArrays();
						}
					}
				}
			}
		}
			
		//if for seom reason we did not fill the entire array, then sort before returning
		if(head < sig_bins.length - 1)
		{
			sortSigArrays();
		}
			
		return 1;
	}
	
	/*--------------------------------*/
	/*     16 COLOR RESOLUTION        */
	/*--------------------------------*/
	public int addToBin16(int data)
	{
		if(colorResFlag != COLOR_RES_16)
			return 0;
		
		
		sortFlag = 8;
		/* Offset values run 0 - 512 for each bin */
		int r = 0xFF & data;
		int g = (data >> 8) & 0xFF;
		int b = (data >> 16) & 0xFF;
		
		int offset = (r % 16) + ((g % 16) * 16) + ((b % 8) * 256);
		
		r /= 16;
		g /= 16;
		b /= 16;
		
		bins[r][g][b]++;
		colors[r][g][b] += offset;
		
		return 1;
	}
	
	public int calcAverageBinColor16()
	{
		//color resolution check
		if(colorResFlag != COLOR_RES_16)
			return 0;
		
		for(int x = 0; x < 16; x++)
		{
			for(int y = 0; y < 16; y++)
			{
				for(int z = 0; z < 16; z++)
				{
					if(bins[x][y][z] != 0)
					{
						int base_color = (x * 16) + ((y * 16) << 8) + ((z * 16) << 16);
						
						int c = colors[x][y][z] / bins[x][y][z];
						
						int b = c / 256;
						int g = (c - (b * 256)) / 16;
						int r = (c - (b * 256)) % 16;
						
						base_color += r + (g << 8) + (b << 16);
						
						colors[x][y][z] = base_color;
					}
				}
			}
		}
		
		return 1;
	}
	
	public int fullSortColors16()
	{
		if(colorResFlag != COLOR_RES_16)
			return 0;
		
		sortFlag = 10;
		
		for(int z = 0; z < 16; z++)
		{
			for(int i = 0; i < 256; i++)
			{
				int k = i - 1;
				int l = z;
				
				int temp = bins[i % 16][i / 16][l];
				int temp_c = colors[i % 16][i / 16][l];
				
				if(k < 0)
				{
					k = 255;
					l--;
				}
				
				while(l > -1 && bins[k % 16][k / 16][l] < temp && temp > 100)
				{
					if(k == 255)
					{
						bins[0][0][l + 1] = bins[15][15][l];
						colors[0][0][l + 1] = colors[15][15][l];
						
						bins[15][15][l] = temp;
						colors[15][15][l] = temp_c;
					}
					else
					{
						bins[(k + 1) % 16][(k + 1) / 16][l] = bins[k % 16][k / 16][l];
						colors[(k + 1) % 16][(k + 1) / 16][l] = colors[k % 16][k / 16][l];
						
						bins[k % 16][k / 16][l] = temp;
						colors[k % 16][k / 16][l] = temp_c;
					}
					
					k--;
					
					if(k < 0)
					{
						k = 255;
						l--;
					}
					
				}
			}
		}
		
		return 1;
	}
	
	//sorts though all the bins and returns a sorted list of the top 256 values (over the specified threshold)
	public int bin256Sort16()
	{
		//color resolution check
		if(colorResFlag != COLOR_RES_16)
			return 0;
			
		System.out.print("Sorting Data");
		//set the sorting flag to avoid trying to return the incorrect values
		sortFlag = 11;
		int head = 0;
		initSigArrays(256);
		
		for(int x = 0; x < 16; x++)
		{
			for(int y = 0; y < 16; y++)
			{
				for(int z = 0; z < 16; z++)
				{
					/*
					//printing methods
					if(x % 4 == 0)
						System.out.print("#");
					if(y / 63 == 1)
						System.out.println(""); */
						
					if(bins[x][y][z] > 100)
					{
						//if we have not yet filled the array of significant values
						if(head < sig_bins.length - 1)
						{
							sig_bins[head] = bins[x][y][z];
							sig_colors[head] = colors[x][y][z];
							
							head++;
								
							//if that value filled the array, then sort them
							if(head == sig_bins.length - 1)
							{
								sortSigArrays();
							}
						}
						//if the array is full, but this value is more significant
						else if(head == sig_bins.length - 1 && bins[x][y][z] > sig_bins[head])
						{
							sig_bins[head] = bins[x][y][z];
							sig_colors[head] = colors[x][y][z];
								
							//sort to make sure the the color of lowest occurence is always at the end.
							sortSigArrays();
						}
					}
				}
			}
		}
			
		//if for seom reason we did not fill the entire array, then sort before returning
		if(head < sig_bins.length - 1)
		{
			sortSigArrays();
		}
			
		return 1;
	}
		
	/*--------------------------------*/
	/*      DATA OUTPUT FUNCTIONS     */
	/*--------------------------------*/
	
	//Programmers should be wary of 0 returns from sort functions and avoid calling this function in that case
	public int[][] getSortedArray()
	{
		//need to change array grab based on sort method
		
		int[][] output = new int[256][256];
		if(sortFlag == 1)
		{
			for(int i = 0; i < 65536; i++)
			{
				output[i % 256][i / 256] = colors[i % 256][i / 256][0];
			}
		}
		else if(sortFlag == 2)
		{
			for(int i = 0; i < 65336; i++)
			{
				output[i % 256][i / 256] = colors[i % 256][0][i / 256];				
			}
		}
		else if(sortFlag == 3)
		{
			for(int i = 0; i < 65336; i++)
			{
				output[i % 256][i / 256] = colors[0][i % 256][i / 256];				
			}			
		}
		else if(sortFlag == 4)
		{
			for(int i = 0; i < 65536; i++)
			{
				output[i % 256][i / 256] = colors[i % 256][i / 256][0];
			}
		}
		else if(sortFlag == 5)
		{
			output = new int[sig_bins.length][1];
			for(int i = 0; i < sig_bins.length; i++)
			{
				output[i][0] = sig_colors[i];
			}
		}
		else if(sortFlag == 6)
		{
			//only return the 64 top colors
			output = new int[64][1];
			for(int i = 0; i < 64; i++)
			{
				output[i][0] = colors[i][0][0];
			}
		}
		else if(sortFlag == 7)
		{
			//return the top 256 colors
			output = new int[256][1];
			for(int i = 0; i < 256; i++)
			{
				output[i][0] = sig_colors[i];
			}
		}
		else if(sortFlag == 8)
		{
			output = new int[64][1];
			for(int i = 0; i < 64; i++)
			{
				output[i][0] = colors[i % 32][i / 32][0];
			}
		}
		else if(sortFlag == 9)
		{
			output = new int[256][1];
			for(int i = 0; i < 256; i++)
			{
				output[i][0] = sig_colors[i];
			}
		}
		else if(sortFlag == 10)
		{
			output = new int[64][1];
			for(int i = 0; i < 64; i++)
			{
				output[i][0] = colors[i % 16][i / 16][0];
			}
		}
		else if(sortFlag == 11)
		{
			output = new int[256][1];
			for(int i = 0; i < 256; i++)
			{
				output[i][0] = sig_colors[i];
			}
		}
		else
		{
			for(int i = 0; i < 65536; i++)
			{
				output[i % 256][i / 256] = colors[i % 256][i / 256][0];
			}			
		}
		
		return output;
	}
	
	public int getBinAt(int x)
	{
		
		if(x >= sig_bins.length || x < 0)
			return -1;
		else
			return sig_bins[x];
	}
}
