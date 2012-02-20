import re

delimiter = "id is_deleted  creator_id  input_params  log execution_finished_at"
f = open("allLogs.txt")
#data = f.read()
count = 0
for line in f:
  queries = line.split("Query:")
  for i in range(1, len(queries)):
    fw = open('queries/q'+str(count)+'.txt', 'w')
    fw.write(queries[i])
    fw.close()
    count += 1
