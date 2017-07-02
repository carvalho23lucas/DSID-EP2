import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Main {

  public static class TokenizerMapper
      extends Mapper<Object, Text, Text, IntWritable> {

    @Override
    public void setup(Context context)
        throws IOException, InterruptedException {
      // tudo o que for feito aqui será feito apenas uma vez
    }

    @Override
    public void map(Object key, Text value, Context context)
        throws IOException, InterruptedException {
      // tudo o que for feito aqui será feito para cada arquivo
    }
  }

  public static class IntSumReducer
      extends Reducer<Text, IntWritable, Text, IntWritable> {

    public void reduce(Text key, Iterable<IntWritable> values, Context context)
        throws IOException, InterruptedException {
      // tudo o que for feito aqui será feito após a execução dos mappers
    }
  }

  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "EP2");
    job.setJarByClass(Main.class);
    job.setMapperClass(TokenizerMapper.class);
    job.setCombinerClass(IntSumReducer.class);
    job.setReducerClass(IntSumReducer.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(IntWritable.class);

    // int tipoInfo = Integer.parseInt(args[0]);
    // setar configurações de tipo informacao
    int periodoIni = Integer.parseInt(args[1]);
    int periodoFim = Integer.parseInt(args[2]);
    int agrupamento = Integer.parseInt(args[3]);

    job.getConfiguration().setInt("agrupamento", agrupamento);

    for (int i = periodoIni; i <= periodoFim; i++) {
      FileInputFormat.addInputPath(job, new Path("input/" + i));
    }
    FileOutputFormat.setOutputPath(job, new Path("output"));

    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}