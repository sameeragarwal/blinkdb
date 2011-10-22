#!/bin/env python
from subprocess import *
from sys import argv
def CreateTableAndInsert(tables, filenames):
    ret = subprocess.call(["$HIVE_HOME/bin/hive"], stdin=PIPE, stderr=PIPE, stdout=PIPE)
    for i in xrange(0, len(tables)):
        ret.stdin.write(
            str.format("CREATE TABLE {0} ROW FORMAT DELIMITED FIELDS TERMINATED BY ',' ROW TERMINATED BY '\\n';\n", tables[i]))
        ret.stdin.write(
            str.format("LOAD DATA LOCAL INPATH {0} INTO TABLE {1};\n", filenames[i], tables[i]))
    (out, err) = ret.communicate(input="quit;\n")
    print "=== OUT ==="
    print out
    print "=== ERR ==="
    print err

if __name__ == "__main__":
    CreateTableAndInsert([argv[1]], [argv[2]])
