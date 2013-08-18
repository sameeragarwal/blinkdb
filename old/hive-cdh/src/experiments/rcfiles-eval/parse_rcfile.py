f = open("rcfile.txt")
for line in f:
  if "Time taken" in line:
    t = line.split("Time taken:")[1].split("seconds")[0]
    t = t.strip()
