#!/usr/bin/python                                 
import os,thread

machinesFile = "/root/ephemeral-hdfs/conf/slaves"
machs = open(machinesFile).readlines()
machs = map(lambda s: s.strip(),machs)
machCount = len(machs)
machID = 0
cmd = "sync; echo 3 > /proc/sys/vm/drop_caches"
done = {}
def dropCachesThread( mach, myID, *args ):
    print "SSH'ing to machine %i" % (myID)
    os.system("ssh %s '%s'" % (mach, cmd))
    done[mach] = "done"

for mach in ( machs ):
    os.system('sleep 2')
    thread.start_new_thread(dropCachesThread, (mach, machID))
    machID = machID + 1
while (len(done.keys()) < machCount):
    os.system('sleep 60')
    print "Done with %i threads" % (len(done.keys()))
