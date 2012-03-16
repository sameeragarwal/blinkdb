f = open("session_time.txt")
hist = {}
for line in f:
  line = line.strip()
  if line != "":
    num = float(line)
    if num not in hist:
      hist[num] = 0
    hist[num] += 1
keys = sorted(hist.keys())

f = open("session_time_histogram_top_50.txt", "w")
count = 0
total = len(keys)
for k in keys:
  count += 1
  if total-count <= 100:
    f.write(str(100*float(count)/len(keys)) + "\t" + str(hist[k])+"\n")
f.close()


