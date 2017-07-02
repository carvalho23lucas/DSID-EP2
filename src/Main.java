import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;
import java.util.Calendar;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Main {

  public static class LineMapper extends Mapper<LongWritable, Text, IntWritable, Text> {
    private Configuration conf;
    private int posIni, posFim, countIni, countFim, agrupamento, funcao;

    private IntWritable group = new IntWritable();
    private Text val = new Text();
    private int dataPosIni = 14, dataPosFim = 22;

    @Override
    public void setup(Context context) throws IOException, InterruptedException {
      conf = context.getConfiguration();
      posIni = conf.getInt("pos.ini", 0);
      posFim = conf.getInt("pos.fim", 0);
      countIni = conf.getInt("count.ini", 0);
      countFim = conf.getInt("count.fim", 0);
      agrupamento = conf.getInt("agrupamento", 1);
      funcao = conf.getInt("funcao", 1);
    }

    @Override
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
      // skip header
      if (key.get() == 0 && value.toString().contains("STN---"))
        return;

      String line = value.toString();
      int c = countFim == 0 ? 1 : Integer.parseInt(line.substring(countIni, countFim).trim());
      // verifica se o valor é 9999.99 ou se a quantidade de medições é 0
      if (!Pattern.matches("9*\\.?9*", line.substring(posIni, posFim)) && c != 0) {
        double v = Double.parseDouble(line.substring(posIni, posFim).trim());

        DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();
        try { cal.setTime(formatter.parse(line.substring(dataPosIni, dataPosFim))); } catch (ParseException e) { }
        
        switch (agrupamento) {
        case 1:
          group.set(cal.get(Calendar.DAY_OF_WEEK));
          break;
        case 2:
          group.set(cal.get(Calendar.MONTH));
          break;
        default:
          group.set(cal.get(Calendar.YEAR));
          break;
        }

        switch (funcao) {
        case 1:
          val.set(v + " " + c);
          break;
        case 2:
          // desv padr
          break;
        default:
          val.set(v + " " + c); // TODO: implementar min quadrados aqui
          break;
        }

        context.write(group, val);
      }
    }
  }

  public static class FinalReducer extends Reducer<IntWritable, Text, IntWritable, Text> {
    private Configuration conf;
    private int funcao;

    private IntWritable group = new IntWritable();
    private Text val = new Text();

    @Override
    public void setup(Context context) throws IOException, InterruptedException {
      conf = context.getConfiguration();
      funcao = conf.getInt("funcao", 1);
    }

    @Override
    public void reduce(IntWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
      int c = 0, v = 0;
      for (Text value : values) {
        String tuple = value.toString();
        int count = Integer.parseInt(tuple.split(" ")[1]);
        c += count;
        v += Double.parseDouble(tuple.split(" ")[0]) * count;
      }
      group.set(key.get());
      val.set((v / c) + " " + c);
      context.write(group, val);
    }
  }

  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "EP2");
    job.setJarByClass(Main.class);
    job.setMapperClass(LineMapper.class);
    job.setCombinerClass(FinalReducer.class);
    job.setReducerClass(FinalReducer.class);
    job.setOutputKeyClass(IntWritable.class);
    job.setOutputValueClass(Text.class);

    int posIni = 0, posFim = 0, countIni = 0, countFim = 0;
    int periodoIni = Integer.parseInt(args[1].split("-")[0]);
    int periodoFim = Integer.parseInt(args[1].split("-")[1]);
    int agrupamento = Integer.parseInt(args[2]); // 1 = dia da semana, 2 = mes, 3 = ano
    int funcao = Integer.parseInt(args[3]); // 1 = media, 2 = desvio padrão, 3 = minimos quadrados 

    switch (args[0]) {
    case "TEMP":
      posIni = 24;
      posFim = 30;
      countIni = 31;
      countFim = 33;
      break;
    case "DEWP":
      posIni = 35;
      posFim = 41;
      countIni = 42;
      countFim = 44;
      break;
    case "SLP":
      posIni = 46;
      posFim = 52;
      countIni = 53;
      countFim = 55;
      break;
    case "STP":
      posIni = 57;
      posFim = 63;
      countIni = 64;
      countFim = 66;
      break;
    case "VISIB":
      posIni = 68;
      posFim = 73;
      countIni = 74;
      countFim = 76;
      break;
    case "WDSP":
      posIni = 78;
      posFim = 83;
      countIni = 84;
      countFim = 86;
      break;
    }

    job.getConfiguration().setInt("pos.ini", posIni);
    job.getConfiguration().setInt("pos.fim", posFim);
    job.getConfiguration().setInt("count.ini", countIni);
    job.getConfiguration().setInt("count.fim", countFim);
    job.getConfiguration().setInt("agrupamento", agrupamento);
    job.getConfiguration().setInt("funcao", funcao);

    for (int i = periodoIni; i <= periodoFim; i++) {
      FileInputFormat.addInputPath(job, new Path("input/" + i));
    }
    FileOutputFormat.setOutputPath(job, new Path("output"));

    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}