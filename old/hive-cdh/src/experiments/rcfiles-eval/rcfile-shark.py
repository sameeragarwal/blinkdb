import math
import commands

f = open("rcfile-memory.txt", "w")
prev_size = 0
for i in range(10,-1, -1):
  size = int(math.pow(2,i))
  f.write(str(size))
  #assuming 2KB row sizes
  for table in ("anon_sdm2_ss", "anon_sdm2_ss_rc", "anon_sdm2_ss_rc_compressed"):
    q1 = "SELECT avg(sessiontimems) FROM new_" + table + "_sample_" + str(size) + "_mb;"
    print q1
    execute = "$SHARK_HOME/bin/shark -e \'" + q1 + "\'"
    out = commands.getoutput(execute)
    t = out.split("Time taken:")[1].split("seconds")[0].strip()
    f.write('\t'+str(t))
  f.write('\n')
