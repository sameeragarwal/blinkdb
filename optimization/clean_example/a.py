f = open("s-storage.txt")

m = 0
for line in f:
  a = int(line.strip('\n'))
  if m < a:
    m = a
print m
  
