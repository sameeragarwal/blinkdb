import os
import glob
import commands

path = ''

def plot(fname, title, gtype):
  #Make GNUPLOT graphs

  fn = open(fname)
  f = open('qq.plt')
  fw = open('qq-temp.plt', 'w')
  c = f.read()
  c = c.replace("_gnuplot_output_", gtype + "-" + title + ".pdf")
  c = c.replace("_gnuplot_title_", gtype + " (" + title + ")")
  c = c.replace("_gnuplot_file_", fname)
  fw.write(c)
  fw.close()
  commands.getoutput("gnuplot qq-temp.plt")
   
for infile in glob.glob( os.path.join(path, '*.txt') ):
  print "current file is: " + infile
  if "uniform" in infile:
    title =  infile.split('uniform-')[1].split('.0.txt')[0] + 'MB'
    plot(infile, title, "Uniform-Sample")
    #print title
    
  if "stratified" in infile:
    title =  infile.split('stratified-')[1].split('.0.txt')[0] + 'MB'
    plot(infile, title, "Stratified-Sample")
    #print title


