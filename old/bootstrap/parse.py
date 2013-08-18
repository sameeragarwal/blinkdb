f = open("table-200mb.txt")
row = {}
i = 1
for line in f:
  print line.split('\t')[91]
  """
  for col in line.split('\t'):
    col = col.strip()
    if col != "":
      #print col
      row[i] = col
      i += 1
  print row[92]
  #break
  """
