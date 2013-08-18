import os
import glob
import commands

path = ''

def plot(fname, title, gtype="rel"):
  #Make GNUPLOT graphs

  fn = open(fname)
  if gtype == "rel":
    f = open('sample-influence.plt')
    fw = open('influence_temp.plt', 'w')

    c = f.read()
    c = c.replace("_gnuplot_output_", "influence" + title.replace(" ","-") + ".pdf")
    c = c.replace("_gnuplot_title_", title)
    c = c.replace("_gnuplot_file_", fname)
    fw.write(c)
    fw.close()
    commands.getoutput("gnuplot influence_temp.plt")
 
for infile in glob.glob( os.path.join(path, '*') ):
  if "SampleInfluence" in infile:
    print "current file is: " + infile
    title =  infile.split('Influence-')[1]
    plot(infile, title)
