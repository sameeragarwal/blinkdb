# Note you need gnuplot 4.4 for the pdfcairo terminal.

set terminal pdfcairo font "Gill Sans,6" linewidth 4 rounded

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

set log x 2
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
set style line 8 lt rgb "#F25900" lw 1 pt 9

#set output "error.pdf"
#set xlabel "Sample Size (rows X 1000)"
#set ylabel "Statistical Answer \n(with error bars)"
set output "RCFile-BlinkDB-Over-Shark-NoPrefetch.pdf"
set xlabel "Sample Size (MB)"
set ylabel "Response Time (seconds)"
set title "BlinkDB Over Shark\n(No Prefetching)"

#set y2label "Effective Sampling Ratio"

set key top left

#set xrange [0:1]
#set yrange [0:0.08]

plot "rcfile512-shark-noprefetch.txt" using 1:2 with lines title "Sequence Files" ls 1, \
     "rcfile512-shark-noprefetch.txt" using 1:3 with lines title "RCFile (Uncompressed)" ls 2, \
     "rcfile512-shark-noprefetch.txt" using 1:4 with lines title "RCFile (Default Compression)" ls 3, \
     "rcfile512-shark-noprefetch.txt" using 1:5 with lines title "Column Store (Default Compression)" ls 4, \
     "rcfile512-shark-noprefetch.txt" using 1:2 notitle ls 5, \
     "rcfile512-shark-noprefetch.txt" using 1:3 notitle ls 6, \
     "rcfile512-shark-noprefetch.txt" using 1:4 notitle ls 7, \
     "rcfile512-shark-noprefetch.txt" using 1:5 notitle ls 8

#plot "Maxrelative_error.txt" using 1:2 with lines title "Bootstrap" ls 1, \
#     "Maxrelative_error.txt" using 1:2 notitle ls 5, \
#     "Maxrelative_error.txt" using 1:3 with lines title "Sampling Truth (K=300)" ls 2, \
#     "Maxrelative_error.txt" using 1:3 notitle ls 6, \
#     "Maxrelative_error.txt" using 1:4 with lines title "Ground Truth" ls 3, \
#     "Maxrelative_error.txt" using 1:4 notitle ls 7

#     "e1.txt" using 1:5 axis x1y2 notitle lt 3;
#   index 0 title "Example line" w lp ls 1, \
#"" index 1 title "Another example" w lp ls 2
