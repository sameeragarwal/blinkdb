import math
import commands

for i in range(10,-1, -1):
  size = int(math.pow(2,i))
  prev_size = int(math.pow(2,i+1))
  #assuming 2KB row sizes
  rows = int(size*1024/2)
  #print rows
  if i == 10:
    h_query = "\'CREATE TABLE anon_sdm2_ss_sample_" + \
              str(size) + \
              "_mb stored as sequencefile as select * from anon_sdm2_ss limit " + \
              str(rows) + \
              "\'"
  else:
    h_query = "\'CREATE TABLE anon_sdm2_ss_sample_" + \
              str(size) + \
              "_mb stored as sequencefile as select * from anon_sdm2_ss_sample_" + \
              str(prev_size) + \
              "_mb limit " + \
              str(rows) + \
              "\'"
  #print h_query
  execute = "$HIVE_HOME/bin/hive -e " + h_query
  print execute
  print commands.getoutput(execute)
 

