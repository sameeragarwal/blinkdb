from random import gauss
import scipy.stats as stat
from numpy import *
import pylab
import commands
import scipy.cluster.vq
import random
from math import sqrt
from math import floor

def mean(X):
    return sum(X)/ float(len(X))
 
def percentile_99(X):
  return stat.scoreatpercentile(X,99)

def percentile_95(X):
  return stat.scoreatpercentile(X,95)

def median(X):
  return stat.scoreatpercentile(X,50)

def non_zero_avg(X):
  count = 0
  nzsum = 0
  for i in X:
    if i != 0:
      count += 1
      nzsum += i
  return float(nzsum)/count

def top_k_avg(X):
  # K=100
  Y = sorted(X)
  size = len(Y)
  top_Y = []
  for i in range(1,100):
    top_Y.append(Y[size-i])
  return mean(top_Y)

def even_number_avg(X):
  count = 0
  esum = 0
  for i in X:
    if i%2 == 0:
      count += 1
      esum += i
  return float(esum)/count

def average_of_kmeans(X):
  #print array(X)
  return average(scipy.cluster.vq.kmeans(array(X), 5)[0])

def non_zero_min(X):
  Y = []
  for i in X:
    if i != 0:
      Y.append(i)
  return min(Y)

def non_zero_mode(X):
  Y = []
  for i in X:
    if i != 0:
      Y.append(i)
  return float(stat.mode(Y)[0])

# Closed Form Error Functions

def cf_mean(X):
  return float(std(X))/sqrt(len(X))

def bootstrap(sample, samplesize = None, nsamples = 300, statfunc = mean):
    """
    Arguments:
       sample - input sample of values
       nsamples - number of samples to generate
       samplesize - sample size of each generated sample
       statfunc- statistical function to apply to each generated sample.
 
    Performs resampling from sample with replacement, gathers
    statistic in a list computed by statfunc on the each generated sample.
    """
    if samplesize is None:                                                                   
        samplesize=len(sample)
    #print "input sample = ",  sample

    n = len(sample)
    X = []
    for i in range(nsamples):
        #print "i = ",  i, 
        resample = [sample[j] for j in stat.randint.rvs(0, n-1, size=samplesize)] 
        x = statfunc(resample)
        X.append(x)
    return X

def stratified_bootstrap(sample_bins, samplesize = None, nsamples = 300, statfunc = mean):
    """
    Arguments:
       sample - input sample of values
       nsamples - number of samples to generate
       samplesize - sample size of each generated sample
       statfunc- statistical function to apply to each generated sample.
 
    Performs resampling from sample with replacement, gathers
    statistic in a list computed by statfunc on the each generated sample.
    """

    #FIXME(sameer): Doesn't work with varyable sub-sample sizes; irrelevant for now
    #if samplesize is None:
    #    samplesize=len(sample)
    #print "input sample = ",  sample

    #n = len(sample)
    X = []

    count = []
    for i in sample_bins:
      count.append(len(i))
    print "Stratified Sample Size", count

    for i in range(nsamples):
      total_resample = []
      for bins in sample_bins:
        n = len(bins)
        #print n
        #print bins
        samplesize = len(bins)
        resample = []
        if samplesize != 1:
          resample = [bins[j] for j in stat.randint.rvs(0, n-1, size=samplesize)]
        else:
          resample = bins
        total_resample.extend(resample)
      #print "Stratified Sample Size", len(total_resample)
      x = statfunc(total_resample)
      X.append(x)
    return X

def aggregate_data(data_bins):
  data = []
  #for k,v in data_bins.iteritems():
  #  data.extend(v)
  for k in data_bins:
    data.append(k)
  return data

def bootstrap_wrapper(data, data_bins, func, fname, cf=None):
  f = open(fname+'relative_error.txt', 'w')
  f2 = open(fname+'varying_k.txt', 'w')

  true_answer = func(data)
  print "True Answer", true_answer

  #Estimate Ground Truth
  for size in xrange(1000, 11000, 1000):
    print "Sample Size", size
    s_ratio = size/float(len(data))
    print "Sampling Ratio: ", s_ratio
    
    assert s_ratio <= 1.0
    #continue
    actual_sample_size = 0
    gt_sample_bs = []
    gt_sample = []
    f2.write(str(2*size/1000) + '\t')

    #Bootstrap Samples
    for i, bins in data_bins.iteritems():
      gt_sample = []
      # Number of samples for this bin
      size_samples = int(math.floor(len(bins)*s_ratio))
      if size_samples == 0:
        size_samples = 1 #Include at least one sample
      actual_sample_size += size_samples
      #print "Bin", i, ":", num_samples, "elements need to be sampled from", len(bins)
      #start = j*size_samples #range from (start to start + size_samples)
      #stop = start + size_samples
      start = 0
      stop = start + size_samples
      if start > len(bins):
        break
      elif start < len(bins) and stop > len(bins):
        stop = len(bins)
      for ib in range(start, stop):
        gt_sample.append(bins[ib])
      gt_sample_bs.append(gt_sample)

    #Bootstrap Sample
    ans = stratified_bootstrap(gt_sample_bs, statfunc=func)

    bootstrap_answer = func(aggregate_data(gt_sample))
    sd = std(ans)
    print fname + " BootStrap", bootstrap_answer, sd
    f.write(str(2*size/1000) + '\t' + str(float(sd)/bootstrap_answer))

    gt_sample = []
    gt = []
    j = 0

    for i in range(0,len(data)):
      gt_sample.append(data[i])
      if i%size == 0 and i > 0 and j < 2000:
        j += 1
        #if j == 1:
        #  #Calculate bootstrap on this sample
        #  ans = bootstrap(gt_sample, statfunc=func)
        #  bootstrap_answer = func(gt_sample)
        #  sd = std(ans)
        #  print fname + " BootStrap", bootstrap_answer, sd
        #  f.write(str(2*size/1000) + '\t' + str(float(sd)/bootstrap_answer))
        gt.append(func(gt_sample))
        if j%50 == 0 and j <= 600:
          #Variance of sampling truth wrt k
          ans = average(gt)
          sd = std(gt)
          print "Variation of Bootstrap", j, ans, sd
          f2.write(str(float(sd)/ans) + '\t')
          if j == 300:
            f.write('\t' + str(float(sd)/ans))
        gt_sample = []
    sd = std(gt)
    print "Ground Truth", true_answer, sd
    f.write('\t' + str(float(sd)/true_answer)+'\n')
    f2.write('\n')

def main():
  # Read data from file and store it in 'data'
  #data = [1,2,3,4,5,6,7,8,9,10]
  data = []
  
  f = open("/mnt/ramfs/sessiontimems.txt")
  #f = open("session_time.txt")
  for line in f:
    data.append(float(line))
  
  """
  # Gaussian
  for i in xrange(0,5000):
    data.append(gauss(5, 1))

  #data = [1,1,1,2,2,2,5,5]
  f = open('GaussianDataTest.txt', 'w')
  for i in data:
    f.write(str(i)+'\n')
  f.close()
  """

  histogram = {}
  for d in data:
    if d not in histogram:
      histogram[d] = 0
    histogram[d] += 1

  keys = sorted(histogram.keys())
  bins = 100
  count = len(keys)
  keys_per_bin = (count/bins) + 1
  #print keys_per_bin

  data_bins = {}
  #Divide into bins
  for i in range(0,len(keys)):
    bin_no = int(i/keys_per_bin)
    if bin_no not in data_bins:
      data_bins[bin_no] = []
    for j in range(0,histogram[keys[i]]):
      data_bins[bin_no].append(keys[i])

  #for k,v in data_bins.iteritems():
  #  print k, len(v)

  #return

  temp = {}
  for k,v in data_bins.iteritems():
    val = v
    random.shuffle(val)
    temp[k] = val
  data_bins = temp

  #for k,v in data_bins.iteritems():
  #  print k,v

  #f = open("/mnt/ramfs/sessiontimems.txt")
  #f = open("session_time.txt")
  #for line in f:
  #  data.append(float(line))

  #random.shuffle(data)

  fn = {}

  fn[mean] = "Mean"
  fn[std] = "Standard Deviation"
  fn[var] = "Variance"
  fn[percentile_99] = "99th Percentile"
  fn[percentile_95] = "95th Percentile"
  fn[non_zero_avg] = "Non Zero Average"
  ##fn[top_k_avg] = "Top K Average"
  ##fn[even_number_avg] = "Average of Even Numbers"
  fn[median] = "Median"
  ##fn[average_of_kmeans] = "Average of K-Means"
  fn[max] = "Max"
  ##fn[non_zero_min] = "Non-Zero Min"
  fn[min] = "Min"
  ##fn[non_zero_mode] = "Non-Zero Mode"

  #cfn = {}
  #cfn[mean] = cf_mean
  #cfn[count] = cf_count
  #cfn[percentile_99] = cf_p99
  #cfn[percentile_95] = cf_p95

  for k,v in fn.iteritems():
    cf = None
    #if k in cfn:
    #  cf = cfn[k]
    bootstrap_wrapper(data, data_bins, k, v, cf)

if __name__ == "__main__":
  main()
