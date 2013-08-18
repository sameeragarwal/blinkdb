import sys
import re
import csv

def loadColumnNames(mapFilename):
    num2name = {}
    name2num = {}
    f = open(mapFilename,'r')
    lineNo = 0;
    for line in f.xreadlines():
	lineNo = lineNo + 1
	colName = line.strip()
	if not colName or colName.find(' ')!=-1:
	    raise Exception('empty or invalid line in ' +mapFilename+' file, line number '+str(lineNo))
	num2name[lineNo] = colName
	name2num[colName] = lineNo
    print 'A total of '+str(lineNo)+' column names were loaded.'
    f.close()
    return num2name, name2num

def showHelp():
    print 'Usage: format_convert columnNameFile [2int|2str] inputFile outputFile'

def convert_to_int(name2num, inputFile, outputFile):
    inf = open(inputFile,'r')
    outf = open(outputFile, 'w')
    lineNo = 0;
    for line in inf.xreadlines():
	lineNo = lineNo + 1
	line = line.strip()
	colNames = line.split()
	colNums = []
	for colName in colNames:
	    if not colName or name2num[colName]==None:
		raise Exception('empty or invalid colName in ' +inputFile+' file, line number'+str(lineNo))
	    colNums.append(name2num[colName])
	print >>outf, ' '.join(map(str, colNums))
    inf.close()
    outf.close()
    print 'A total of '+str(lineNo)+' lines were converted.'
    

def convert_to_str(num2name,inputFile, outputFile):
    inf = open(inputFile,'r')
    outf = open(outputFile, 'w')
    lineNo = 0;
    for line in inf.xreadlines():
	lineNo = lineNo + 1
	line = line.strip()
	colNums = map(int, line.split())
	colNames = []
	for colNum in colNums:
	    if num2name[colNum]==None:
		raise Exception('empty or invalid colNum in ' +inputFile+' file, line number'+str(lineNo))
	    colNames.append(num2name[colNum])
	print >>outf, ' '.join(colNames)
    inf.close()
    outf.close()
    print 'A total of '+str(lineNo)+' lines were converted.'
        


if __name__ == "__main__":
    argv = sys.argv
    if len(argv) != 5:
	showHelp()
	sys.exit()
    mapFilename = argv[1]
    inputFile = argv[3]
    outputFile = argv[4]
    (num2name, name2num) = loadColumnNames(mapFilename)
    
    if argv[2]=='2int':
	convert_to_int(name2num, inputFile, outputFile)
    elif argv[2] =='2str':
	convert_to_str(num2name,inputFile, outputFile)
    else:
	showHelp()
	raise Exception('Bad input parameters:' + argv[2])
    
    sys.exit()