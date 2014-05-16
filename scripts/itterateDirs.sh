#!/bin/bash
count=0
for dir in 2014* 
do
count=$((count+1))
$echo $count
#insert input folder for data
input="/team03/data2/$dir"
#insert output folder for data
output="/team03/outputData2/$dir"
echo $dir
hadoop jar ../jars/algo-trading-strategies.jar ch.epfl.bigdata.ts.dataparser.ts.Run $input $output $count
done
