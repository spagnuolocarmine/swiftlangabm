#!/bin/bash

set -eu

# Args
param_line=$1   # The upf param line    
#echo "$param_line"

instanceDir=$2  # The instance directory
tproot=$3
#echo $instanceDir

modelDir=$tproot"/complete_model/"

cd $instanceDir
ln -s $modelDir"data" data

cPath=$modelDir"lib/*"

#echo $cPath
pxml=$modelDir"scenario.rs/batch_params.xml"

#echo $pxml
scenario=$modelDir"scenario.rs"

#echo $scenario

#/Library/Java/JavaVirtualMachines/jdk1.7.0_51.jdk/Contents/Home/bin/

if [[ ${JAVA:-0} == 0 ]] 
then
  JAVA=java 
fi
$JAVA -Xmx1536m -XX:-UseLargePages -jar netlogo-1.0-wrapper.jar

-cp "$cPath" repast.simphony.batch.InstanceRunner \
        -pxml "$pxml" \
        -scenario "$scenario" \
        -id 1 "$param_line"
-m resources/models/JZombiesLogo.nlogo 
-outfile test.out 
-runid 1 
-s 10 
-trial 1 
-i num-people,800,num-infected,10,f_humans,2.3,f_zombies,1.5 
-o end_humans,end_infected