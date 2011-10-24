datfiles <- list.files(pattern='stat_kmean.*\\.dat')
for (i in seq(along=datfiles)) {
    filename <- datfiles[i]
    fileparts <- unlist(strsplit(filename, '_'))
    print(filename)
    stat <- read.csv(file=filename, sep=',', header=T)
    graphname <- paste(paste('stat_kmean', fileparts[3], sep='_'), 'png', sep='.')
    print(graphname)
    png(graphname)
    plot(stat$Samples, stat$Error, ,type='l', xlab='Samples', ylab='Error')
    dev.off()
}
