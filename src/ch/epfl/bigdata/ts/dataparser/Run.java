package ch.epfl.bigdata.ts.dataparser;

import java.io.IOException;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;


public class Run {

    public enum Counter {
        ORDER_ID
    }

    public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, LongWritable, Text> {
        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();
        private LongWritable orderID = new LongWritable();//maybe we will need fileID as well

        public void map(LongWritable key, Text value, OutputCollector<LongWritable, Text> output, Reporter reporter) throws IOException {
            List<String> lines = new ArrayList<String>(Arrays.asList(value.toString().split("\n")));
            for (String line : lines) {
                List<String> cols = new ArrayList<String>(Arrays.asList(line.split(",")));
                orderID.set(Integer.parseInt(cols.get(2)));
                char delim = ',';
                StringBuilder builder = new StringBuilder();
                builder.append(cols.get(0));
                builder.append(delim);
//                builder.append(cols.get(2));
//                builder.append(delim);
                builder.append(cols.get(3));
                builder.append(delim);
                builder.append(cols.get(4));
                builder.append(delim);
                builder.append(cols.get(5));
                word.set(builder.toString());
                output.collect(orderID, word);
            }
        }
    }

    public static class Reduce extends MapReduceBase implements Reducer<LongWritable, Text, NullWritable, Text> {
        private IntWritable userID = new IntWritable();
        private IntWritable userTriangulations = new IntWritable();
        private NullWritable nothing = NullWritable.get();

        public void reduce(LongWritable key, Iterator<Text> values, OutputCollector<NullWritable, Text> output, Reporter reporter) throws IOException {


            int sum = 0;
            java.util.Map<Integer, List<String>> friendsMap = new HashMap<Integer, List<String>>();
            List<Order> sortedOrders = new ArrayList<Order>();

            while (values.hasNext()) {
                String value = values.next().toString();
                List<String> cols = new ArrayList<String>(Arrays.asList(value.split(",")));
                Order o = new Order(key.get(), Long.parseLong(cols.get(0)), cols.get(1).charAt(0), Long.parseLong(cols.get(2)), Long.parseLong(cols.get(3)));
                sortedOrders.add(o);
            }

            Collections.sort(sortedOrders);

            long shares = 0;
            long price = 0;
            long timestamp = 0;

            List<Order> tickOrders = new ArrayList<Order>();

            for (Order o : sortedOrders) {
                if (o.getType() == Order.TYPE_BUY || o.getType() == Order.TYPE_SELL) {
                    shares = o.getNumberShares();
                    price = o.getPrice();
                } else if (o.getType() == Order.TYPE_PART_EXECUTE) {
                    shares -= o.getNumberShares();
                    reporter.getCounter(Counter.ORDER_ID).increment(1);
                    long tickID = reporter.getCounter(Counter.ORDER_ID).getValue();//getTickID
                    tickOrders.add(new Order(tickID, o.getTimestamp(), 'T', o.getNumberShares(), price));
                } else if (o.getType() == Order.TYPE_PART_CANCEL) {
                    shares -= o.getNumberShares();
                } else if (o.getType() == Order.TYPE_FULL_EXECUTE) {
                    reporter.getCounter(Counter.ORDER_ID).increment(1);
                    long tickID = reporter.getCounter(Counter.ORDER_ID).getValue();//getTickID
                    tickOrders.add(new Order(tickID, o.getTimestamp(), 'T', shares, price));
                } else if (o.getType() == Order.TYPE_NON_ORDER_EXECUTE) {
                    reporter.getCounter(Counter.ORDER_ID).increment(1);
                    long tickID = reporter.getCounter(Counter.ORDER_ID).getValue();//getTickID
                    tickOrders.add(new Order(tickID, o.getTimestamp(), 'T', o.getNumberShares(), o.getPrice()));
                } else if (o.getType() == Order.TYPE_FULL_DELETE) {
                    //this should be the last order in the list
                    break;
                }
            }

            for (Order o : tickOrders) {
                output.collect(nothing, new Text(o.toString()));
            }
        }
    }

    public static void main(String[] args) throws Exception {

        int map1Tasks = 4;
        int reduce1Tasks = 80;

        int reduce2Tasks = 1;

        String intermediateDir = "/team03/tmp/" + Run.class.getSimpleName() + "-tmp";

        Path interMedPath = new Path(intermediateDir);

        JobConf conf = new JobConf(Run.class);
        conf.setJobName("ATS - Job1");


        conf.setMapOutputKeyClass(LongWritable.class);
        conf.setMapOutputValueClass(Text.class);

        conf.setOutputKeyClass(NullWritable.class);
        conf.setOutputValueClass(Text.class);

        conf.setMapperClass(Map.class);
        conf.setReducerClass(Reduce.class);

        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);


        FileInputFormat.setInputPaths(conf, new Path(args[0]));
        FileOutputFormat.setOutputPath(conf, interMedPath);

        JobConf conf2 = new JobConf(Run2.class);
        conf2.setJobName("ATS - Job2");


        conf2.setMapOutputKeyClass(IntWritable.class);
        conf2.setMapOutputValueClass(Text.class);

        conf2.setOutputKeyClass(Text.class);
        conf2.setOutputValueClass(NullWritable.class);

        conf2.setMapperClass(Run2.Map2.class);
        conf2.setReducerClass(Run2.Reduce2.class);

        conf2.setInputFormat(TextInputFormat.class);
        conf2.setOutputFormat(TextOutputFormat.class);


        FileInputFormat.setInputPaths(conf2, interMedPath);
        FileOutputFormat.setOutputPath(conf2, new Path(args[1]));

        JobClient jc = new JobClient(conf);
        JobClient jc2 = new JobClient(conf2);

        conf.setNumMapTasks(jc.getClusterStatus().getMapTasks());
        conf.setNumReduceTasks(jc.getClusterStatus().getMaxReduceTasks());

        conf2.setNumMapTasks(jc2.getClusterStatus().getMaxMapTasks());
        conf2.setNumReduceTasks(1);

        JobClient.runJob(conf).waitForCompletion();
        JobClient.runJob(conf2);
    }

}
