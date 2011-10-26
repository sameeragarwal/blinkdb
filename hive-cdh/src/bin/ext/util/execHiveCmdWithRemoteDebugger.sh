# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

#-------------------------------------------------------------------------------
# Modified by Anand Iyer (api@cs.berkeley.edu)
#-------------------------------------------------------------------------------

execHiveCmdWithRemoteDebugger () {
  CLASS=$1;
  shift;

  # cli specific code
  if [ ! -f ${HIVE_LIB}/hive-cli-*.jar ]; then
    echo "Missing Hive CLI Jar"
    exit 3;
  fi

  if $cygwin; then
    HIVE_LIB=`cygpath -w "$HIVE_LIB"`
  fi

  #-----------------------------------------------------------------------------
  # Adding necessary classpath stuff, since without this, java fails.	
  #-----------------------------------------------------------------------------
  # Hive classpath
  #-----------------------------------------------------------------------------
  HIVE_CLASSPATH=$HIVE_HOME/conf 
  for i in `ls $HIVE_LIB/*.jar` 
  do
	HIVE_CLASSPATH=${HIVE_CLASSPATH}:${i} 
  done

  #-----------------------------------------------------------------------------
  # Hadoop classpath
  #-----------------------------------------------------------------------------
  export HADOOP_LIB=$HADOOP_HOME/bin/../lib
  HADOOP_CLASSPATH=$HADOOP_HOME/bin/../conf:$HADOOP_HOME/bin/.. 
  for i in `ls $HADOOP_HOME/*.jar` 
  do 
	HADOOP_CLASSPATH=${HADOOP_CLASSPATH}:${i} 
  done 
  for i in `ls $HADOOP_LIB/*.jar` 
  do 
	HADOOP_CLASSPATH=${HADOOP_CLASSPATH}:${i} 
  done 
  for i in `ls $HADOOP_HOME/share/hadoop/lib/*.jar` 
  do
	HADOOP_CLASSPATH=${HADOOP_CLASSPATH}:${i} 
  done

  export CLASSPATH=$HADOOP_CLASSPATH:$HIVE_CLASSPATH:$CLASSPATH
  
  export DEBUG_INFO="-Xmx1000m -Djava.compiler=NONE -Xdebug -Xrunjdwp:transport=dt_socket,address=8001,server=y,suspend=n"

  # hadoop 20 or newer - skip the aux_jars option. picked up from hiveconf
  echo "----------Launching hive using the following command----------"
  echo exec $JAVA_HOME/bin/java $DEBUG_INFO org.apache.hadoop.util.RunJar ${HIVE_LIB}/hive-cli-*.jar $CLASS $HIVE_OPTS "$@"
  echo "--------------------------------------------------------------"
  echo " If this fails, make sure you have the correct JAVA_HOME,
  echo " HADOOP_HOME and HIVE_HOME variables.
  echo "--------------------------------------------------------------"
  #exec $HADOOP ${HIVE_LIB}/hive-cli-*.jar $CLASS $HIVE_OPTS "$@"
  exec $JAVA_HOME/bin/java $DEBUG_INFO org.apache.hadoop.util.RunJar ${HIVE_LIB}/hive-cli-*.jar $CLASS $HIVE_OPTS "$@"
}
