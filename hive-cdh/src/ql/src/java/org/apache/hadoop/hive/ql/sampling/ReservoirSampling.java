/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hadoop.hive.ql.sampling;

import org.apache.hadoop.hive.ql.estimation.QuickSilverStream;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class ReservoirSampling {

	public List <String> reservoirSampling (InputStream in, long fileSize, int sampleSize)
	{
		
		try
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line;
			
			// Estimated rows in the Input Stream
			//TODO: @sameerag make it a config parameter (readlimit: size and rows)
			int sampleStreamCount = 5;
			List <String> sampleStream= new ArrayList<String>(sampleStreamCount);
			//Never read more than 1MB of data.
			br.mark(1024*1024);
			while ((sampleStreamCount != 0) && (line = br.readLine()) != null)
			{
				sampleStream.add(line);
				sampleStreamCount--; 
			}
			
			QuickSilverStream _qs = new QuickSilverStream();
			int estimatedRows = _qs.estimateRows(sampleStream, fileSize);
			System.out.println("Estimated Rows in Table: "+ estimatedRows);

			br.reset();
			
			int reservoirSizeInRows = (int) (((float)sampleSize)/fileSize)*estimatedRows;
			if (reservoirSizeInRows > estimatedRows)
				reservoirSizeInRows = estimatedRows;
			
			List <String> reservoirList= new ArrayList<String>(reservoirSizeInRows);
			int count=0;
			Random rnd = new Random();

			int randomNumber;
			while ((line = br.readLine()) != null)
			{
				count ++;
				if (count <= reservoirSizeInRows)
				{
					reservoirList.add(line);
				}
				else
				{
					randomNumber = (int)rnd.nextInt(count);
					if (randomNumber < reservoirSizeInRows)
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

}