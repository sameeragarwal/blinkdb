from random import gauss
import scipy.stats as stat
from numpy import *
import pylab
import commands
import scipy.cluster.vq
import random
from math import sqrt
from math import floor

def main():

  # Read data from file and store it in 'data'
  #data = [1,2,3,4,5,6,7,8,9,10]
  data = []
 
  f = open("/mnt/ramfs/sessiontimems.txt")
  for line in f:
    data.append(float(line))
  
  
  # Uncomment for Gaussian Vs. Conviva
  # Gaussian
  #for i in xrange(0,30000000):
  #  data.append(gauss(5, 1))

  #data = [1,1,1,2,2,2,5,5]
  #f = open('GaussianDataTest.txt', 'w')
  #for i in data:
  #  f.write(str(i)+'\n')
  #f.close()
  
  print "Data Loaded/Generated"

  random.shuffle(data)

  print "Data Shuffled"

  histogram = {}
  for d in data:
    if d not in histogram:
      histogram[d] = 0
    histogram[d] += 1

  keys = sorted(histogram.keys())
  bins = 100
  count = len(keys)
  keys_per_bin = (count/bins) + 1

  data_bins = {}
  #Divide into bins
  for i in range(0,len(keys)):
    bin_no = int(i/keys_per_bin)
    if bin_no not in data_bins:
      data_bins[bin_no] = []
    for j in range(0,histogram[keys[i]]):
      data_bins[bin_no].append(keys[i])

  print "Data Binned"

  temp = {}
  for k,v in data_bins.iteritems():
    val = v
    random.shuffle(val)
    temp[k] = val
  data_bins = temp

  print "Data Bin Shuffled"

  percentile = {}
  sort_data = sorted(data)
  l = float(len(data))
  print "Data Sorted"
  for k in range(1,100):
    print "Percentile", k
    percentile[k] = sort_data[int(l*float(k))/100]

  #Sampling
  uniform = {}
  stratified = {}
  for size in range(1000, 33000, 2000):
    print size
    X = []
    Y = []
    s_ratio = size/float(len(data))
    uniform[size] = {}
    stratified[size] = {}

    # Create Uniform Sample
    for j in range(0,size):
      X.append(data[j])

    #Creating Stratified Sample
    for i, bins in data_bins.iteritems():
      size_samples = int(math.floor(len(bins)*s_ratio))
      if size_samples == 0:
        size_samples = 1
      start = 0
      stop = start + size_samples
      if start > len(bins):
        break
      elif start < len(bins) and stop > len(bins):
        stop = len(bins)
      for ib in range(start,stop):
        Y.append(bins[ib])

    for k in range(1,100):
      val = stat.scoreatpercentile(X,k)
      val_s = stat.scoreatpercentile(Y,k)
      #print size, k, val, val_s
      uniform[size][k] = val
      stratified[size][k] = val_s

    size_mb = 2*float(size)/1000
    f1 = open("qq-uniform-"+str(size_mb)+".txt", "w")
    f2 = open("qq-stratified-"+str(size_mb)+".txt", "w")
    for k, v in uniform[size].iteritems():
      f1.write(str(percentile[k]) + '\t' + str(v) + '\n')
      f2.write(str(percentile[k]) + '\t' + str(stratified[size][k]) + '\n')
    f1.close()
    f2.close()

  #f = open("qq-data.txt", "w")
  #for k,v in percentile.iteritems():
  #  f.write(str(k) + '\t' + str(v) + '\n')
  #f.close()

  #print "Min", min(data)
  #print "Max", max(data)

if __name__ == "__main__":
  main()
