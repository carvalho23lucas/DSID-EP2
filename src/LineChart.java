import java.awt.BasicStroke;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

//import Main.PosAndCount;

public class LineChart extends ApplicationFrame {
	public Map<Integer, Double> meanList = new HashMap<Integer, Double>();
	public LineChart(int funcao, int agrupamento, double a, double b, double x0, double y0, double x1, double y1) {
	      super("DSID-EP2");
	      String titulo_x = "";
	      String titulo_y = "";
	      
	      switch (funcao) {
	      case 1:
	    	titulo_y = "Média";
	        break;
	      case 2:
	        titulo_y = "Desvio Padrão";
	        break;
	      }
	      
	      switch (agrupamento) {
	        case 1:
	          titulo_x = "Dias da Semana";
	          break;
	        case 2:
	          titulo_x = "Meses";
	          break;
	        default:
	          titulo_y = "Anos";
	          break;
	       }
	      
	      JFreeChart xylineChart = ChartFactory.createXYLineChart(
	         "" ,
	         titulo_x ,	// X
	         titulo_y ,		// Y
	         createDataset(a, b, x0, y0, x1, y1) ,
	         PlotOrientation.VERTICAL ,
	         true , true , false);
	         
	      ChartPanel chartPanel = new ChartPanel( xylineChart );
	      chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );
	      final XYPlot plot = xylineChart.getXYPlot( );
	      
	      XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer( );
	      renderer.setSeriesPaint( 0 , Color.RED );
	      renderer.setSeriesPaint( 1 , Color.GREEN );
//	      renderer.setSeriesPaint( 2 , Color.YELLOW );
	      renderer.setSeriesStroke( 0 , new BasicStroke( 4.0f ) );
	      renderer.setSeriesStroke( 1 , new BasicStroke( 3.0f ) );
//	      renderer.setSeriesStroke( 2 , new BasicStroke( 2.0f ) );
	      plot.setRenderer( renderer ); 
	      setContentPane( chartPanel ); 
	   }
	   
		private void parseMeanFile(String fileName) {
			BufferedReader fis;
	      try {
	        fis = new BufferedReader(new FileReader(fileName));
	        String line = null;
	        while ((line = fis.readLine()) != null) {
	          meanList.put(Integer.parseInt(line.split("\t")[0]), Double.parseDouble(line.split("\t")[1]));
	        }
	        
	        System.out.println("MeanList Size = " + meanList.size());
	      } catch (IOException ioe) {
	    	  ioe.printStackTrace();
	      }
	    }
	
	   private XYDataset createDataset(double a, double b, double x0, double y0, double x1, double y1) {
		   String _path = "/usr/local/hadoop/output/part-r-00000";
		   parseMeanFile(_path);
		   
	      final XYSeries data = new XYSeries("Dados Estatísticos/Unidade de tempo");
	      
	      
	      Iterator it = meanList.entrySet().iterator();
	      
	      while(it.hasNext()) {
	    	  Map.Entry<Integer, Double> pair = (Map.Entry<Integer, Double>)it.next();
	    	  data.add( (int)pair.getKey(), (double)pair.getValue());  
	      }
	             
	      
//	      final XYSeries MMQ = new XYSeries( "Método dos Mínimos Quadrados" );          
//	      MMQ.add( x0, y0 );          
//	      MMQ.add( x1 , y1 );          
//	      MMQ.add( 100 , 1011);          
	             
	      
	      final XYSeriesCollection dataset = new XYSeriesCollection( );          
	      dataset.addSeries( data );          
//	      dataset.addSeries( MMQ );          

	      return dataset;
	   }

	   public static void main( String[ ] args ) {
		   LineChart chart = new LineChart(1,
	         1, 2.0, 2.0, 1, 1000, 7, 1001);
	      chart.pack();          
	      RefineryUtilities.centerFrameOnScreen( chart );          
	      chart.setVisible( true ); 
	   }

}