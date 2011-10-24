package org.apache.hadoop.hive.ql.costmodel;

public class Bounds {

	public double errorBound;
	public double timeBound;
	
	public Bounds() {
		this.errorBound = -1.0;
		this.timeBound = -1.0;
	}
	
	public boolean isInitialized() {
		if (this.errorBound > -1.0 || this.timeBound > -1.0) {
			return true;
		}
		return false;
	}
}