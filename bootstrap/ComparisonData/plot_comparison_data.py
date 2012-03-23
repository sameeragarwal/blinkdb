import os
import glob
import commands

path = ''

def plot(fname, title, gtype):
  #Make GNUPLOT graphs

  fn = open(fname)
  if gtype == "rel":
    f = open('relative_error_template.plt')
    fw = open('relative_error.plt', 'w')

    c = f.read()
    c = c.replace("_gnuplot_output_", "relative_error_" + title + ".pdf")
    #c = c.replace("_gnuplot_xlabel_", "Sample Size (MB)")
    #c = c.replace("_gnuplot_ylabel_", "Relative Error")
    c = c.replace("_gnuplot_title_", title)
    c = c.replace("_gnuplot_file_", fname)
    fw.write(c)
    fw.close()
    commands.getoutput("gnuplot relative_error.plt")
  else:
    f = open('varying_k_template.plt')
    fw = open('varying_k_error.plt', 'w')

    c = f.read()
    c = c.replace("_gnuplot_output_", "varying_k_error_" + title + ".pdf")
    #c = c.replace("_gnuplot_xlabel_", "Sample Size (MB)")
    #c = c.replace("_gnuplot_ylabel_", "Relative Error")
    c = c.replace("_gnuplot_title_", title)
    c = c.replace("_gnuplot_file_", fname)
    fw.write(c)
    fw.close()
    commands.getoutput("gnuplot varying_k_error.plt")
 
for infile in glob.glob( os.path.join(path, '*.txt') ):
  print "current file is: " + infile
  if "relative_error" in infile:
    title =  infile.split('relative_error')[0]
    plot(infile, title, "rel")
    
  if "varying_k" in infile:
    title =  infile.split('varying_k')[0]
    plot(infile, title, "var")

