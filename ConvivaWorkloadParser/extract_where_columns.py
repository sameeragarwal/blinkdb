import glob
import os
import sys

distribution = {}
unique_cols = []

column_index = {}
f = open('schema.hql')

count = 0

column_index['dt'] = count

schema = f.read()
schema = schema.split('CREATE TABLE anon_sdm2_ss (')[1]
schema = schema.strip().strip('\n').strip()
for item in schema.split(','):
  count += 1
  #print count, item.strip().split(' ')[0]
  column_index[item.strip().split(' ')[0].lower()] = count

path = "wheres-sample/"
for indir in os.listdir(path):
    if os.path.isdir(path+indir):
      all_where = []
      for infile in os.listdir(path+indir):
        if infile[0] != ".":
          f = open(path+indir+'/'+infile)
          s = f.read().lower()
          s = s.replace('true', '')
          # Hacks Begin
          s = s.replace('viewer', '')
          s = s.replace('data', '')
          # Hacks End
          s = s.split('where')[1].strip()
          for clause in s.split('and'):
              _clause = clause.strip().strip('\n')
              if _clause != "":
                #print _clause
                #Extract left hand side column
                if '=' in _clause:
                  _clause = _clause.split('=')[0]
                if '<' in _clause:
                  _clause = _clause.split('<')[0]
                if '>' in _clause:
                  _clause = _clause.split('>')[0]
                if '!' in _clause:
                  _clause = _clause.split('!')[0]
                if 'like' in _clause:
                  _clause = _clause.split('like')[0]
                if 'is' in _clause:
                  _clause = _clause.split('is')[0]
                _clause = _clause.strip()
                #if '(' in _clause:
                  #_clause = _clause.split('(')[1]
                _clause = _clause.lstrip('(').rstrip(')')
                #if '(' in _clause:
                #_clause = clause.lstrip('(')
                _clause = _clause.strip()
                #print _clause
                if 'userexitcauses' not in _clause:
                  all_where.append(_clause)
      fs = frozenset(all_where)
      if fs not in distribution:
        distribution[fs] = 0
      distribution[fs] += 1
      #print "------------"

all_cols = ""

f1 = open("where.txt", "w")
f2 = open("freq.txt", "w")

for k,v in distribution.iteritems():
  _k = ""
  for item in k:
    _item = item
    #More Hacks
    #if '(' in item:
    #  _item = item.lstrip('(')
    #Hacks End
    if _item in column_index:
			_item = column_index[_item]
    _k += str(_item) + ','
  # More Hacks
  if ('navratilova' in _k or 'kudryavtseva' in _k or 'monica' in _k or
     'dolgopolov' in _k):
    continue
  if ('objectid' in _k or 'errorcount' in _k or 'tagvalue' in _k or 'size' in _k
      or 'css' in _k):
    continue
  #Hacks End
  #print _k + '#' + str(v)
  if ("startednonjoinedflag" not in _k and 'timestampus' not in _k and
      'avgestbwkbps' not in _k): 
    f1.write(_k.rstrip(',') + '\n')
    f2.write(str(v)+'\n')

  all_cols += _k

f1.close()
f2.close()

print "\n-----------------------\n"

for item in all_cols.split(','):
  if item != "":
		unique_cols.append(item)

uniq = frozenset(unique_cols)

for item in uniq:
	if item not in column_index:
		print item
	else:	
		print str(column_index[item]) + ','


