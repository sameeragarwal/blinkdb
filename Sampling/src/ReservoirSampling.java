
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class ReservoirSampling 
{
	public List <String> reservoirSampling (String file, int reservoirSize)
	{
		try
		{
			File f = new File(file);
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line;
			List <String> reservoirList= new ArrayList<String>(reservoirSize);
			int count=0;
			Random rnd = new Random();

			int randomNumber;
			while ((line = br.readLine()) != null)
			{
				count ++;
				if (count <= reservoirSize)
				{
					reservoirList.add(line);
				}
				else
				{
					randomNumber = (int)rnd.nextInt(count);
					if (randomNumber < reservoirSize)
					{
						reservoirList.set(randomNumber, line);
					}
				}
			}
			
			return reservoirList;
		}
		catch (Exception e)
		{
			System.out.println(e.toString());
		}
		
		return null;
		
	 }
	
	public HashMap<Integer, HashMap<Integer, List <String>>> bulkReservoirSampling (String file, int N_init, int num_samples, int max_levels )
	{
		try
		{
			File f = new File(file);
			BufferedReader br = new BufferedReader(new FileReader(f));
			HashMap<Integer, HashMap<Integer, List <String>>> samples = new HashMap<Integer, HashMap<Integer, List <String>>>();

			String line;
			
			int count=0;
			int sample_size = 0;
			Random rnd = new Random();
			int randomNumber;
			
			while ((line = br.readLine()) != null)
			{
				count ++;
				for (int i = 0; i < max_levels; i++)
				{
					sample_size = (int) (N_init * Math.pow(2, i));
					if (!samples.containsKey(sample_size))
						samples.put(sample_size, new HashMap<Integer, List <String>>());
					for (int j = 0; j < num_samples; j++)
					{
						//System.out.println(line + " " + sample_size + " " + j);
						if (!samples.get(sample_size).containsKey(j))
							samples.get(sample_size).put(j, new ArrayList<String>());
						List <String> reservoirList = samples.get(sample_size).get(j);
						if (reservoirList.size() < sample_size)
							reservoirList.add(line);
						else
						{
							randomNumber = (int)rnd.nextInt(count);
							if (randomNumber < sample_size)
							{
								samples.get(sample_size).get(j).set(randomNumber, line);
							}
						}
					}
				}				
			}
			
			return samples;
		}
		catch (Exception e)
		{
			System.out.println(e.toString());
		}
		
		return null;
		
	 }
}
