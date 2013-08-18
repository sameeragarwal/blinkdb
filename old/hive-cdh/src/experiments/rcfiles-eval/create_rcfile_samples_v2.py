import math
import commands

for i in range(0,11):
  size = int(math.pow(2,i))
  h_query = "\'CREATE TABLE new_anon_sdm2_ss_rc_sample_" + \
             str(size) + \
            "_mb stored as sequencefile as select * from anon_sdm2_ss_rc_sample_" + \
            str(size) + \
              "_mb" + \
              "\'"
  execute = "$HIVE_HOME/bin/hive -e " + h_query
  print execute
  print commands.getoutput(execute)
 

