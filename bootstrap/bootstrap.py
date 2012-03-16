from random import gauss
import scipy.stats as stat
from numpy import *
import pylab

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
  for i in xrange(0,100000):
    data.append(gauss(5, 1))
  
  error = []
  x = []
  f_error = open('error.txt', 'w')
  f_bars = open('error_bars', 'w')

  for size in xrange(1000, 10000, 1000):
    ans = bootstrap(data, samplesize=size)
    avg = average(ans)
    sd = std(ans)
    print avg, sd
    error.append(sd)
    x.append(size)
    f_error.write(str(size)+'\t'+str(sd)+'\n')
    f_bars.write(str(size)+'\t'+str(avg)+'\t'+str(avg-1.96*sd)+'\t'+str(avg+1.96*sd)+'\n')
  
  f_error.close()
  f_bars.close()


  """
  pylab.plot(x, error, 'bx')
  pylab.xlabel('Sample Size (tuples)')
  pylab.ylabel('Standard Deviation')
  pylab.title('Bootstrap on Mean')
  pylab.grid(True)

  pylab.savefig('mean.png')
  pylab.show()
  """

if __name__ == "__main__":
    main()
