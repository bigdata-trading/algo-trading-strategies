#!/bin/bash
#Move all the csv files in separate dirs based on their date
for file in *_MSFT.csv 
do
year=`echo $file | cut -c1-4`
month=`echo $file | cut -c5-6`
day=`echo $file | cut -c7-8`
timestamp="$year-$month-$day"
`mkdir $timestamp`
`mv $file $timestamp`
done
