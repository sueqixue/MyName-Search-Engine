
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.HashMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

public class invertedIndex {

    public static class invertedIndexMapper extends Mapper<Object, Text, Text, Text>{

        private Text word = new Text();
        private Text fileName = new Text();

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

            String fileId = ((FileSplit) context.getInputSplit()).getPath().getName();
            String data = value.toString();
            StringTokenizer itr = new StringTokenizer(data, " \t\n\r\f\",.:;?![]()'-"); 

            fileName.set(fileId);

            while (itr.hasMoreTokens()) {
                word.set(itr.nextToken());
                if(word.toString() != "" && !word.toString().isEmpty()){
                    context.write(word, fileName);
                }
            }

        }

    }

    public static class invertedIndexReducer extends Reducer<Text,Text,Text,Text> {

        private Text result = new Text();

        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {

            StringBuilder sb = new StringBuilder();
            HashMap<String,Integer> map = new HashMap<String,Integer>();

            for (Text val : values) {
                if (map.containsKey(val.toString())) {
                    map.put(val.toString(), map.get(val.toString()) + 1);
                } else {
                    map.put(val.toString(), 1);
                }
            }
      
            for(String fileId : map.keySet()){
                sb.append(fileId + ":" + map.get(fileId) + " ");
            }

            result.set(sb.toString());
            context.write(key, result);

        }

    }

public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "inverted_index");
        job.setJarByClass(invertedIndex.class);
        job.setMapperClass(invertedIndexMapper.class);
        job.setReducerClass(invertedIndexReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
    
}