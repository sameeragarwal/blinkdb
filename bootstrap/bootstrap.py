from random import gauss
import scipy.stats as stat
from numpy import *
import pylab
import commands

def mean(X):
    return sum(X)/ float(len(X))
 
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

def main():
  # Read data from file and store it in 'data'
  #data = [1,2,3,4,5,6,7,8,9,10]
  data = []

  # Gaussian
  #for i in xrange(0,100000):
  #  data.append(gauss(5, 1))

  f = open("session_time.txt")
  for line in f:
    data.append(float(line))

  true_answer = mean(data)
  print "True Answer", true_answer

  error = []
  x = []
  f_error = open('error.txt', 'w')
  f_bars = open('error_bars.txt', 'w')

  for size in xrange(1000, 11000, 1000):
    ans = bootstrap(data, samplesize=size)
    avg = average(ans)
    sd = std(ans)
    print avg, sd
    error.append(sd)
    x.append(size/1000)
    f_error.write(str(2*size/1000)+'\t'+str((100*1.96*sd/avg))+'\n')
    f_bars.write(str(2*size/1000)+'\t'+str(avg)+'\t'+str(avg-1.96*sd)+'\t'+str(avg+1.96*sd)+'\n')
  
  f_error.close()
  f_bars.close()

  #Make GNUPLOT graphs

  gf_error = open("error.template.plt")
  gf_error_w = open("error.plt", "w")

  c = gf_error.read()
  c = c.replace("_gnuplot_output_", "error.pdf")
  c = c.replace("_gnuplot_xlabel_", "Sample Size (MB)")
  c = c.replace("_gnuplot_ylabel_", "% Error")
  c = c.replace("_gnuplot_title_", "Mean")
  gf_error_w.write(c)
  gf_error_w.close()

  commands.getoutput("gnuplot error.plt")

  gf_bars = open("error_bars.template.plt")
  gf_bars_w = open("error_bars.plt", "w")

  c = gf_bars.read()
  c = c.replace("_gnuplot_output_", "error_bars.pdf")
  c = c.replace("_gnuplot_xlabel_", "Sample Size (MB)")
  c = c.replace("_gnuplot_ylabel_", "Statistical Answer \\n(with error bars)")
  c = c.replace("_gnuplot_true_answer_", str(true_answer))
  c = c.replace("_gnuplot_title_", "Mean")

  gf_bars_w.write(c)
  gf_bars_w.close()
 
  commands.getoutput("gnuplot error_bars.plt")
  
if __name__ == "__main__":
  main()
