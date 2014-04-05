package ch.epfl.bigdata.ts.dataparser;

import java.io.IOException;
import java.util.*;


import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;

public class Run2 {

    public static class Map2 extends MapReduceBase implements Mapper<LongWritable, Text, IntWritable, Text> {
        private final static IntWritable one = new IntWritable(1);
        private IntWritable outSums = new IntWritable();

        public void map(LongWritable key, Text value, OutputCollector<IntWritable, Text> output, Reporter reporter) throws IOException {
            List<String> lines = new ArrayList<String>(Arrays.asList(value.toString().split("\n")));
            for (String line : lines) {
                output.collect(one, new Text(line));
            }
        }
    }


    public static class Reduce2 extends MapReduceBase implements Reducer<IntWritable, Text, Text, NullWritable> {
        private NullWritable nothing = NullWritable.get();

        public void reduce(IntWritable key, Iterator<Text> values, OutputCollector<Text, NullWritable> output, Reporter reporter) throws IOException {

            while (values.hasNext()) {
                output.collect(values.next(), nothing);
            }
        }
    }
}	
