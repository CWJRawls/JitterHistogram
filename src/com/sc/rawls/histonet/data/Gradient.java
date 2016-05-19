package com.sc.rawls.histonet.data;

public class Gradient {
	
	public static final int GRAD_FEATURE_NONE = 0;
	public static final int GRAD_FEATURE_BLACK = 1;
	public static final int GRAD_FEATURE_WHITE = 2;
	
	private int[] base;
	private int[] gradient;
	private int gradient_feature;
	private Histogram hist;
	private boolean init_check = false;
	
	/* CONSTRUCTOR
	 * Takes the integer array from the sort
	 * an integer describing any features that need to be added to the gradient
	 * A histogram object to reference for bin information. (Should be the same object that provided the array)
	 */
	public Gradient(int[] b, int feat, Histogram h)
	{
		//set the histogram
		hist = h;
		
		//set the feature value
		gradient_feature = feat;
		
		//initialize the base array
		base = new int[b.length];
		
		for(int i = 0; i < b.length; i++)
		{
			base[i] = b[i];
		}
		
		//filter out any extra values in the base array and reset it to the correct size
		removeExtraIndeces();
	}
	
	/*COMPUTE GRADIENT Function
	 * takes no arguments and returns an integer when done.
	 * Will use the gradient feature to determine how the gradient between adjacent indeces is calculated.
	 * If the computation is successful, the gradient array will be filled at the function's conclusion.
	 */
	public int computeGradient()
	{
		//check to make sure there is data to compute a gradient on
		if(base.length == 0)
			return 0;
		
		System.out.println("Computing Gradient");
		//get options chosen for the gradient
		int steps = (gradient_feature >> 8) & 0xFF;
		int feat = gradient_feature  & 0xFF;
		
		//init the array for the final gradient
		gradient = new int[((base.length - 1) * steps)];
		
		if(steps > 0) //Why go through running this if we have no steps?
		{
			//if our gradient is drawing a line through the RGB cube
			if(feat == GRAD_FEATURE_NONE)
			{
				System.out.println("Computing for no features gradient");
				for(int i = 0; i < base.length - 1; i++) //we will not calculate anything past the last index
				{
					int[] temp = new int[steps];
					//temp[0] = base[i];
					//temp[temp.length - 1] = base[i + 1];
					
					float r_shift = ((float)((base[i + 1] & 0xFF) - (base[i] & 0xFF))) / ((float)steps);
					float g_shift = ((float)(((base[i + 1] >> 8) & 0xFF) - ((base[i] >> 8) & 0xFF))) / ((float)steps);
					float b_shift = ((float)(((base[i + 1] >> 16) & 0xFF) - ((base[i] >> 16) & 0xFF))) / ((float)steps);
					
					int r = base[i] & 0xFF;
					int g = (base[i] >> 8) & 0xFF;
					int b = (base[i] >> 16) & 0xFF;
					
					
					for(int j = 0; j < temp.length; j++)
					{
						int t_r, t_g, t_b;
						
						t_r = (int)(r + (j * r_shift));
						t_g = (int)(g + (j * g_shift));
						t_b = (int)(b + (j * b_shift));
						
						temp[j] = t_r + (t_g << 8) + (t_b << 16);
					}
					
					for(int j = 0; j < temp.length; j++)
					{
						gradient[((i * steps) + j)] = temp[j];
						int index = ((i * steps) + j);
						System.out.println("Setting index: " + index);
					}
				}
			}
			else if(feat == GRAD_FEATURE_BLACK)
			{
				for(int i = 0; i < base.length - 1; i++)
				{
					int[] temp = new int[steps + 2];
					temp[0] = base[i];
					temp[temp.length - 1] = base[i + 1];
					
					float r_shift_a = (base[i] & 0xFF) / (steps / 2);
					float g_shift_a = ((base[i] >> 8) & 0xFF) / (steps / 2);
					float b_shift_a = ((base[i] >> 16) & 0xFF) / (steps / 2);
					
					float r_shift_b = (base[i + 1] & 0xFF) / (steps / 2);
					float g_shift_b = ((base[i + 1] >> 8) & 0xFF) / (steps / 2);
					float b_shift_b = ((base[i + 1] >> 16) & 0xFF) / (steps / 2);
					
					int r = base[i] & 0xFF;
					int g = (base[i] >> 8) & 0xFF;
					int b = (base[i] >> 16) & 0xFF;
					
					int step_check = steps % 2;
					int median = steps / 2;
					
					if(step_check != 0)
					{
						median--;
					}
					
					for(int j = 1; j < median; j++)
					{
						int t_r = (int)(r - (j * r_shift_a));
						int t_g = (int)(g - (j * g_shift_a));
						int t_b = (int)(b - (j * b_shift_a));
						
						temp[j] = t_r + (t_g << 8) + (t_b << 16);
					}
					
					r = 0;
					b = 0;
					g = 0;
					
					for(int j = median; j < steps; j++)
					{
						int t_r = (int)(r + ((j - (median - 1)) * r_shift_b));
						int t_g = (int)(g + ((j - (median - 1)) * g_shift_b));
						int t_b = (int)(b + ((j - (median - 1)) * b_shift_b));
						
						temp[j] = t_r + (t_g << 8) + (t_b << 16);
					}
					
					for(int j = 0; j < temp.length; j++)
					{
						gradient[((i * steps) + i + j)] = temp[j];
					}
				}
			}
			else if(feat == GRAD_FEATURE_WHITE)
			{
				for(int i = 0; i < base.length - 1; i++)
				{
					int[] temp = new int[steps + 2];
					temp[0] = base[i];
					temp[temp.length - 1] = base[i + 1];
					
					float r_shift_a = (255 - (base[i] & 0xFF)) / (steps / 2);
					float g_shift_a = (255 - ((base[i] >> 8) & 0xFF)) / (steps / 2);
					float b_shift_a = (255 - ((base[i] >> 16) & 0xFF)) / (steps / 2);
					
					float r_shift_b = (255 - (base[i + 1] & 0xFF)) / (steps / 2);
					float g_shift_b = (255 - ((base[i + 1] >> 8) & 0xFF)) / (steps / 2);
					float b_shift_b = (255 - ((base[i + 1] >> 16) & 0xFF)) / (steps / 2);
					
					int r = base[i] & 0xFF;
					int g = (base[i] >> 8) & 0xFF;
					int b = (base[i] >> 16) & 0xFF;
					
					int step_check = steps % 2;
					int median = steps / 2;
					
					if(step_check != 0)
					{
						median--;
					}
					
					for(int j = 1; j < median; j++)
					{
						int t_r = (int)(r + (j * r_shift_a));
						int t_g = (int)(g + (j * g_shift_a));
						int t_b = (int)(b + (j * b_shift_a));
						
						temp[j] = t_r + (t_g << 8) + (t_b << 16);
					}
					
					r = 255;
					b = 255;
					g = 255;
					
					for(int j = median; j < steps; j++)
					{
						int t_r = (int)(r - ((j - (median - 1)) * r_shift_b));
						int t_g = (int)(g - ((j - (median - 1)) * g_shift_b));
						int t_b = (int)(b - ((j - (median - 1)) * b_shift_b));
						
						temp[j] = t_r + (t_g << 8) + (t_b << 16);
					}
					
					for(int j = 0; j < temp.length; j++)
					{
						gradient[((i * steps) + i + j)] = temp[j];
					}
				}
			}
			else
			{
				for(int i = 0; i < gradient.length; i++)
				{
					gradient[i] = base[i];
				}
			}
		}
		else //just copy data to the output array
		{
			for(int i = 0; i < gradient.length; i++)
			{
				gradient[i] = base[i];
			}
		}
		
		init_check = true;
		
		return 1;
	}
	
	/*REMOVE EXTRA INDECES Function
	 * takes no arguments and is a void return when done.
	 * Starts at the end of the base array and works towards base[0] to find all values that do not have a bin > 0
	 * When the loop is complete, and if there were any values of bin == 0, then the base array is reinitialized and refilled with only valid values.
	 */
	private void removeExtraIndeces()
	{
		int end = base.length - 1;
		
		//start at the end of the matrix and find all open black pixels
		for(int i = base.length - 1; hist.getBinAt(i) == 0 && i >= 0; i--)
		{
			end = i;
		}
		
		//only reinitialize matrices in the case that empty pixels need to be removed
		if(end != base.length - 1)
		{
			//create a temporary array to hold values
			int[] temp = new int[end + 1];
			
			for(int i = 0; i < temp.length; i++)
			{
				temp[i] = base[i];
			}
			
			//reallocate base to the new number of indexes and refill the array
			base = new int[temp.length];
			
			for(int i = 0; i < base.length; i++)
			{
				base[i] = temp[i];
			}
		}
		
		System.out.println("Base Size: " + base.length);
	}
	
	/* CAN GET DATA Function
	 * takes no arguments and returns a boolean value
	 * returns the value of init_check flag
	 * Should be called before asking for gradient data
	 */
	public boolean canGetData()
	{
		return init_check;
	}
	
	/* GET GRADIENT Function
	 * Takes no arguments and returns a 1-dimensional integer array
	 * should call check function before asking for data to ensure Gradient array is not null
	 */
	public int[] getGradient()
	{
		return gradient;
	}

}
