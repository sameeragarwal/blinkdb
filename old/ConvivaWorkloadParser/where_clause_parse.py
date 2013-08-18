import glob
import os
import sqlparse
import sys

where_clauses = []

def parse_where(subquery):
  res = sqlparse.parse(subquery)
  stmt = res[0]
  for i in stmt.tokens:
      if "where" in i.to_unicode().lower():
        where_clauses.append(i)

def expand_parenthesis():
  # returns 0 if no parens needs expanding; else 1
  ret = 0
  for i in where_clauses:
    _i = str(i).strip()
    if (_i[0] == '(') and (_i[-1] == ')'):
      ret = 1
      break
  if ret == 1:
    where_clauses.remove(i)
    parse_where(i.to_unicode().strip().lstrip('(').rstrip(')'))
  return ret
	
def main(argv=None):
  path = 'queries/'
  query_count = 100
  for infile2 in glob.glob(os.path.join(path, '*.txt')):
    infile = 'queries/q'+str(query_count)+".txt"
    if query_count == -1:
      break
    query_count -= 1
    try:
      count = 0
      f = open(infile)
      #f = open("convivaquery.txt")
      query = f.read().replace('\\n', '\n')
      #query = "select * from tab where a == 'A' AND b == 'B'  group by country"
      parse_where(query)
      # Updates where_clauses
      ret = 1
      while (ret == 1):
        ret = expand_parenthesis()
      #print where_clauses
      dir_path = 'wheres-sample/' + infile.split('/')[-1].split('.txt')[0]
      if not os.path.exists(dir_path):
        os.mkdir(dir_path)
      for i in where_clauses:
        fw = open('wheres-sample/' + infile.split('/')[-1].split('.txt')[0] +'/' + 'w' + str(count) +
            '.txt', 'w')
        fw.write(i.to_unicode())
        fw.close()
        #print i, "\n----------------\n"
    except:
      continue

if __name__ == "__main__":
    sys.exit(main())

