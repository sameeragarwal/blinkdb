import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class CalculateSamples {

	public static void main(String[] args) 
	{
		try
		{
			new File("samples").mkdir();
			String file = args[0]; //filename
			int N_init = Integer.parseInt(args[1]); //64000
			int num_samples = Integer.parseInt(args[2]); //25
			int max_levels = Integer.parseInt(args[3]); //6

			ReservoirSampling rs = new ReservoirSampling();
			HashMap<Integer, HashMap<Integer, List <String>>> sample = rs.bulkReservoirSampling(file, N_init, num_samples, max_levels);
			for (Integer key : sample.keySet())
			{
				for (Integer _key : sample.get(key).keySet())
				{
					new File("samples/"+key).mkdir();
					List <String> reservoir = sample.get(key).get(_key);
					Iterator<String> iterator = reservoir.iterator();
					Writer output = new BufferedWriter(new FileWriter("samples/"+key+"/"+_key+".txt"));
					while (iterator.hasNext())
						output.write(iterator.next()+"\n");
					output.close();
				}
			}
		}
		catch (Exception e)
		{
			System.out.println(e.toString());
		}
	}
}