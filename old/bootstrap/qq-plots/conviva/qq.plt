# Note you need gnuplot 4.4 for the pdfcairo terminal.

set terminal pdfcairo font "Gill Sans,7" linewidth 4 rounded

# Line style for axes
set style line 80 lt rgb "#808080"

# Line style for grid
set style line 81 lt 0  # dashed
set style line 81 lt rgb "#808080"  # grey

set grid back linestyle 81
set border 3 back linestyle 80 # Remove border on top and right.  These
             # borders are useless and make it harder
             # to see plotted lines near the border.
    # Also, put it in grey; no need for so much emphasis on a border.

#set xtics 0.5

set xtics nomirror
set ytics nomirror
#set y2tics nomirror

set log x
set log y

#set mxtics 10    # Makes logscale look good.

# Line styles: try to pick pleasing colors, rather
# than strictly primary colors or hard-to-see colors
# like gnuplot's default yellow.  Make the lines thick
# so they're easy to see in small plots in papers.
set style line 1 lt rgb "#A00000" lw 2 pt 1
set style line 2 lt rgb "#00A000" lw 2 pt 6
set style line 3 lt rgb "#5060D0" lw 2 pt 2
set style line 4 lt rgb "#F25900" lw 2 pt 9
set style line 5 lt rgb "#A00000" lw 1 pt 1
set style line 6 lt rgb "#00A000" lw 1 pt 6
set style line 7 lt rgb "#5060D0" lw 1 pt 2

#set output "error.pdf"
#set xlabel "Sample Size (rows X 1000)"
#set ylabel "Statistical Answer \n(with error bars)"
set output "_gnuplot_output_"
set xlabel "Ground Truth Distribution"
set ylabel "Samples Distribution"
set title "_gnuplot_title_"

#set y2label "Effective Sampling Ratio"

set key top left

#set xrange [0:1]
#set yrange [0:0.08]

plot "_gnuplot_file_" using 1:2 notitle ls 5, \
     x with lines title "X=Y" ls 7
