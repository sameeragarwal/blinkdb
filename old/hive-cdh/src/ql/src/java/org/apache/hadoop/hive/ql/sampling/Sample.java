/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hadoop.hive.ql.sampling;

import java.io.*;
import java.util.List;
import java.util.Iterator;

import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.conf.HiveConf;


public class Sample {

	//@sameerag: Adding the sampling function
	public boolean sample(File src,
			FileSystem dstFS, Path dst,
			boolean deleteSource, 
			Configuration conf,
			int samplingLevel) throws IOException {
		dst = checkDest(src.getName(), dstFS, dst, false);

		if (src.isDirectory()) {
			if (!dstFS.mkdirs(dst)) {
				return false;
			}
			File contents[] = src.listFiles();
			for (int i = 0; i < contents.length; i++) {
				sample(contents[i], dstFS, new Path(dst, contents[i].getName()),
						deleteSource, conf, samplingLevel);
			}
		} else if (src.isFile()) {
			InputStream in = null;
			OutputStream out =null;
			try {
				in = new FileInputStream(src);
				out = dstFS.create(dst);
				//IOUtils.copyBytes(in, out, conf);
				sampleLines(in, out, conf, samplingLevel);
			} catch (IOException e) {
				IOUtils.closeStream( out );
				IOUtils.closeStream( in );
				throw e;
			}
		} else {
			throw new IOException(src.toString() + 
					": No such file or directory");
		}
		if (deleteSource) {
			return FileUtil.fullyDelete(src);
		} else {
			return true;
		}
	}

	//@sameerag
	/** Copy FileSystem files to local files. */
	public boolean sample(FileSystem srcFS, Path src, 
			File dst, boolean deleteSource,
			Configuration conf,
			int samplingLevel) throws IOException {
		if (srcFS.getFileStatus(src).isDir()) {
			if (!dst.mkdirs()) {
				return false;
			}
			FileStatus contents[] = srcFS.listStatus(src);
			for (int i = 0; i < contents.length; i++) {
				sample(srcFS, contents[i].getPath(), 
						new File(dst, contents[i].getPath().getName()),
						deleteSource, conf, samplingLevel);
			}
		} else if (srcFS.isFile(src)) {
			InputStream in = srcFS.open(src);
			//IOUtils.copyBytes(in, new FileOutputStream(dst), conf);
			sampleLines(in, new FileOutputStream(dst), conf, samplingLevel);
		} else {
			throw new IOException(src.toString() + 
					": No such file or directory");
		}
		if (deleteSource) {
			return srcFS.delete(src, true);
		} else {
			return true;
		}
	}

	//@sameerag
	/** Copy files between FileSystems. */
	public boolean sample(FileSystem srcFS, Path src, 
			FileSystem dstFS, Path dst, 
			boolean deleteSource,
			Configuration conf,
			int samplingLevel) throws IOException {
		return sample(srcFS, src, dstFS, dst, deleteSource, true, conf, samplingLevel);
	}

	//@sameerag
	public boolean sample(FileSystem srcFS, Path[] srcs, 
			FileSystem dstFS, Path dst,
			boolean deleteSource, 
			boolean overwrite,
			Configuration conf,
			int samplingLevel) throws IOException {
		boolean gotException = false;
		boolean returnVal = true;
		StringBuffer exceptions = new StringBuffer();

		if (srcs.length == 1)
			return sample(srcFS, srcs[0], dstFS, dst, deleteSource, overwrite, conf, samplingLevel);

		// Check if dest is directory
		if (!dstFS.exists(dst)) {
			throw new IOException("`" + dst +"': specified destination directory " +
			"doest not exist");
		} else {
			FileStatus sdst = dstFS.getFileStatus(dst);
			if (!sdst.isDir()) 
				throw new IOException("copying multiple files, but last argument `" +
						dst + "' is not a directory");
		}

		for (Path src : srcs) {
			try {
				if (!sample(srcFS, src, dstFS, dst, deleteSource, overwrite, conf, samplingLevel))
					returnVal = false;
			} catch (IOException e) {
				gotException = true;
				exceptions.append(e.getMessage());
				exceptions.append("\n");
			}
		}
		if (gotException) {
			throw new IOException(exceptions.toString());
		}
		return returnVal;
	}

	//@sameerag
	/** Copy files between FileSystems. */
	public boolean sample(FileSystem srcFS, Path src, 
			FileSystem dstFS, Path dst, 
			boolean deleteSource,
			boolean overwrite,
			Configuration conf,
			int samplingLevel) throws IOException {
		
		long fileSize = -1;
		File f = new File(src.toUri());
		if (f != null)
			fileSize = f.length(); //in bytes

		dst = checkDest(src.getName(), dstFS, dst, overwrite);

		if (srcFS.getFileStatus(src).isDir()) {
			checkDependencies(srcFS, src, dstFS, dst);
			if (!dstFS.mkdirs(dst)) {
				return false;
			}
			FileStatus contents[] = srcFS.listStatus(src);
			for (int i = 0; i < contents.length; i++) {
				sample(srcFS, contents[i].getPath(), dstFS, 
						new Path(dst, contents[i].getPath().getName()),
						deleteSource, overwrite, conf, samplingLevel);
			}
		} else if (srcFS.isFile(src)) {
			InputStream in=null;
			OutputStream out = null;
			try {
				in = srcFS.open(src);
				out = dstFS.create(dst, overwrite);
				//IOUtils.copyBytes(in, out, conf, true);
				sampleLines(in, out, conf, fileSize, true, samplingLevel);
			} catch (IOException e) {
				IOUtils.closeStream(out);
				IOUtils.closeStream(in);
				throw e;
			}
		} else {
			throw new IOException(src.toString() + ": No such file or directory");
		}
		if (deleteSource) {
			return srcFS.delete(src, true);
		} else {
			return true;
		}
	}

	public void sampleLines(InputStream in, OutputStream out, Configuration conf, long fileSize, boolean close, int samplingLevel) 
	throws IOException {

		//Sample and copy data in a memory stream before copying the stream over to out stream.

		PrintStream ps = out instanceof PrintStream ? (PrintStream)out : null;
		PrintStream outps = new PrintStream(out);

		//Read from configuration file that corresponds to size/level
		int sampleSize = 0;
		if (samplingLevel == 1)
			sampleSize = HiveConf.getIntVar(conf, HiveConf.ConfVars.SAMPLE_SIZE_LEVEL_1);
		else if (samplingLevel == 2)
			sampleSize = HiveConf.getIntVar(conf, HiveConf.ConfVars.SAMPLE_SIZE_LEVEL_2);
		
		try {
			//TODO: Take value from conf and pass it to reservoir sample
			ReservoirSampling _rs = new ReservoirSampling();
			List<String> sample = _rs.reservoirSampling(in, fileSize, sampleSize*1024*1024);

			Iterator<String> iterator = sample.iterator();
			while (iterator.hasNext()) {
				String _line = iterator.next();
				outps.println(_line);
				if ((ps != null) && ps.checkError()) {
					throw new IOException("Unable to write to output stream.");
				}	
			}

		} finally {
			if(close) {
				outps.close();
				out.close();
				in.close();
			}
		}
	}
	
	public void sampleLines(InputStream in, OutputStream out, Configuration conf, long fileSize, int samplingLevel) 
	throws IOException {
		sampleLines(in, out, conf, fileSize, true, samplingLevel);
	}
	
	public void sampleLines(InputStream in, OutputStream out, Configuration conf, int samplingLevel) 
	throws IOException {
		sampleLines(in, out, conf, -1, true, samplingLevel);
	}

	//
	// If the destination is a subdirectory of the source, then
	// generate exception
	//
	private static void checkDependencies(FileSystem srcFS, 
			Path src, 
			FileSystem dstFS, 
			Path dst)
	throws IOException {
		if (srcFS == dstFS) {
			String srcq = src.makeQualified(srcFS).toString() + Path.SEPARATOR;
			String dstq = dst.makeQualified(dstFS).toString() + Path.SEPARATOR;
			if (dstq.startsWith(srcq)) {
				if (srcq.length() == dstq.length()) {
					throw new IOException("Cannot copy " + src + " to itself.");
				} else {
					throw new IOException("Cannot copy " + src + " to its subdirectory " +
							dst);
				}
			}
		}
	}

	private static Path checkDest(String srcName, FileSystem dstFS, Path dst,
			boolean overwrite) throws IOException {
		if (dstFS.exists(dst)) {
			FileStatus sdst = dstFS.getFileStatus(dst);
			if (sdst.isDir()) {
				if (null == srcName) {
					throw new IOException("Target " + dst + " is a directory");
				}
				return checkDest(null, dstFS, new Path(dst, srcName), overwrite);
			} else if (!overwrite) {
				throw new IOException("Target " + dst + " already exists");
			}
		}
		return dst;
	}

}