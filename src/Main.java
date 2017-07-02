import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
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
      if (key.get() == 0 && value.toString().contains("STN---")) // skip header
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
        case 2:
          val.set(v + "\t" + c);
          break;
        default:
          val.set(v + "\t" + c); // TODO: implementar min quadrados aqui
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

    private BufferedReader fis;
    private Map<Integer, Double> meanList = new HashMap<Integer, Double>();

    @Override
    public void setup(Context context) throws IOException, InterruptedException {
      conf = context.getConfiguration();
      funcao = conf.getInt("funcao", 1);
      if (funcao == 2) {
        URI[] patternsURIs = Job.getInstance(conf).getCacheFiles();
        for (URI patternsURI : patternsURIs) {
          Path path = new Path(patternsURI.getPath());
          String fileName = path.getName().toString();
          parseMeanFile(fileName);
        }
      }
    }
    private void parseMeanFile(String fileName) {
      try {
        fis = new BufferedReader(new FileReader(fileName));
        String line = null;
        while ((line = fis.readLine()) != null) {
          meanList.put(Integer.parseInt(line.split("\t")[0]), Double.parseDouble(line.split("\t")[1]));
        }
      } catch (IOException ioe) { }
    }

    @Override
    public void reduce(IntWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
      int c = 0;
      double v = 0, result;

      switch (funcao) {
      case 1:
        for (Text value : values) {
          String tuple = value.toString();
          int count = Integer.parseInt(tuple.split("\t")[1]);
          c += count;
          v += Double.parseDouble(tuple.split("\t")[0]) * count;
        }
        result = v / c;
        break;
      case 2:
        for (Text value : values) {
          String tuple = value.toString();
          int count = Integer.parseInt(tuple.split("\t")[1]);
          c += count;
          double mean = meanList.get(key.get());
          v += Math.pow(Double.parseDouble(tuple.split("\t")[0]) - mean, 2);
        }
        result = Math.sqrt(v / (c - 1.0));
        break;
      default:
        result = 0; // TODO: implementar min quadrados aqui
        break;
      }

      group.set(key.get());
      val.set(result + "\t" + c);

      context.write(group, val);
    }
  }

  public static void main(String[] args) throws Exception {
    int periodoIni = Integer.parseInt(args[1].split("-")[0]);
    int periodoFim = Integer.parseInt(args[1].split("-")[1]);
    int agrupamento = Integer.parseInt(args[2]);
    int funcao = Integer.parseInt(args[3]);

    switch (funcao) {
    case 1:
      System.exit(calculaMedia(new PosAndCount(args[0]), periodoIni, periodoFim, agrupamento) ? 0 : 1);
      break;
    case 2:
      System.exit(calculaDesvioPadrao(new PosAndCount(args[0]), periodoIni, periodoFim, agrupamento) ? 0 : 1);
      break;
    case 3:
      System.exit(calculaMinimosQuadrados(new PosAndCount(args[0].split("-")[0]), new PosAndCount(args[0].split("-")[1]), periodoIni, periodoFim, agrupamento) ? 0 : 1);
      break;
    }
  }

  public static boolean calculaMedia(PosAndCount pac, int periodoIni, int periodoFim, int agrupamento) throws Exception {
    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "job");
    FileSystem hdfs = FileSystem.get(conf);

    if (hdfs.exists(new Path("output")))
      hdfs.delete(new Path("output"), true);

    job.setJarByClass(Main.class);
    job.setMapperClass(LineMapper.class);
    job.setCombinerClass(FinalReducer.class);
    job.setReducerClass(FinalReducer.class);
    job.setOutputKeyClass(IntWritable.class);
    job.setOutputValueClass(Text.class);
    
    job.getConfiguration().setInt("pos.ini", pac.posIni);
    job.getConfiguration().setInt("pos.fim", pac.posFim);
    job.getConfiguration().setInt("count.ini", pac.countIni);
    job.getConfiguration().setInt("count.fim", pac.countFim);
    job.getConfiguration().setInt("agrupamento", agrupamento);
    job.getConfiguration().setInt("funcao", 1);

    for (int i = periodoIni; i <= periodoFim; i++) {
      FileInputFormat.addInputPath(job, new Path("input/" + i));
    }
    FileOutputFormat.setOutputPath(job, new Path("output"));

    return job.waitForCompletion(true);
  }

  public static boolean calculaDesvioPadrao(PosAndCount pac, int periodoIni, int periodoFim, int agrupamento) throws Exception {
    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "job2");
    FileSystem hdfs = FileSystem.get(conf);

    calculaMedia(pac, periodoIni, periodoFim, agrupamento);
    
    hdfs.moveToLocalFile(new Path("output/part-r-00000"), new Path("/usr/local/hadoop/output"));
    hdfs.moveFromLocalFile(new Path("/usr/local/hadoop/output"), new Path("input/part-r-00000"));
    hdfs.delete(new Path("output"), true);

    job.getConfiguration().setInt("pos.ini", pac.posIni);
    job.getConfiguration().setInt("pos.fim", pac.posFim);
    job.getConfiguration().setInt("count.ini", pac.countIni);
    job.getConfiguration().setInt("count.fim", pac.countFim);
    job.getConfiguration().setInt("agrupamento", agrupamento);
    job.getConfiguration().setInt("funcao", 2);

    job.setJarByClass(Main.class);
    job.setMapperClass(LineMapper.class);
    job.setReducerClass(FinalReducer.class);
    job.setOutputKeyClass(IntWritable.class);
    job.setOutputValueClass(Text.class);
    job.addCacheFile(new Path("input/part-r-00000").toUri());

    for (int i = periodoIni; i <= periodoFim; i++) {
      FileInputFormat.addInputPath(job, new Path("input/" + i));
    }
    FileOutputFormat.setOutputPath(job, new Path("output"));

    return job.waitForCompletion(true);
  }

  public static boolean calculaMinimosQuadrados(PosAndCount pac1, PosAndCount pac2, int periodoIni, int periodoFim, int agrupamento) throws Exception {
    return false;
  }

  public static class PosAndCount {
    public int posIni = 0, posFim = 0, countIni = 0, countFim = 0;

    public void set(int posIni, int posFim, int countIni, int countFim) {
      this.posIni = posIni;
      this.posFim = posFim;
      this.countIni = countIni;
      this.countFim = countFim;
    }

    public void set(int posIni, int posFim) {
      this.posIni = posIni;
      this.posFim = posFim;
    }

    public PosAndCount(String arg) {

      switch (arg) {
      case "TEMP":
        set(24, 30, 31, 33);
        break;
      case "DEWP":
        set(35, 41, 42, 44);
        break;
      case "SLP":
        set(46, 52, 53, 55);
        break;
      case "STP":
        set(57, 63, 64, 66);
        break;
      case "VISIB":
        set(68, 73, 74, 76);
        break;
      case "WDSP":
        set(78, 83, 84, 86);
        break;
      case "MXSPD":
        set(88, 93);
        break;
      case "GUST":
        set(95, 100);
        break;
      case "MAX":
        set(102, 108);
        break;
      case "MIN":
        set(110, 116);
        break;
      case "PRCP":
        set(118, 123);
        break;
      case "SNDP":
        set(125, 130);
        break;
      }
    }
  }
}