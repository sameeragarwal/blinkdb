import math
import commands

f = open("create.hql","w")
f.write("SET hive.exec.compress.output=true;\n")
f.write("SET io.seqfile.compression.type=BLOCK;\n")
for i in range(10,-1, -1):
  size = int(math.pow(2,i))
  h_query = "CREATE TABLE new_anon_sdm2_ss_rc_compressed_sample_" + \
            str(size) + \
            "_mb stored as rcfile as select * from anon_sdm2_ss_sample_" + \
            str(size) + \
            "_mb" + \
            ";"
  execute = "$HIVE_HOME/bin/hive -e " + h_query
  print execute
  f.write(h_query+'\n')
  #print commands.getoutput(execute)
f.close()
commands.getoutput("$HIVE_HOME/bin/hive -f create.hql")
