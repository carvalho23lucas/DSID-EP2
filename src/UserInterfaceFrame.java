import java.awt.EventQueue;
import java.util.List;
import java.awt.Toolkit;

import javax.swing.JFrame;import javax.swing.event.ListDataListener;
import javax.swing.plaf.LabelUI;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class UserInterfaceFrame {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UserInterfaceFrame window = new UserInterfaceFrame();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public UserInterfaceFrame() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		//frame.setBounds(100, 100, 450, 300);
	    int frameWidth = 600;
	    int frameHeight = 350;
	    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setBounds((int) screenSize.getWidth() - frameWidth, 0, frameWidth, frameHeight);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JComboBox cmbVariavel = new JComboBox(Variavel.values());
		cmbVariavel.setBounds(12, 46, 167, 24);
		frame.getContentPane().add(cmbVariavel);
		
		JComboBox<Integer> cmbAnoIni = new JComboBox<Integer>();
		cmbAnoIni.setBounds(12, 101, 70, 24);
		populateComboBoxAnos(cmbAnoIni);
		frame.getContentPane().add(cmbAnoIni);
		
		JComboBox<Integer> cmbAnoFinal = new JComboBox<Integer>();
		cmbAnoFinal.setBounds(109, 101, 70, 24);
		populateComboBoxAnos(cmbAnoFinal);
		frame.getContentPane().add(cmbAnoFinal);
		
		JLabel lblVarivel = new JLabel("Variável");
		lblVarivel.setLabelFor(cmbVariavel);
		lblVarivel.setBounds(12, 30, 70, 15);
		frame.getContentPane().add(lblVarivel);
		
		JLabel lblPerodo = new JLabel("Período");
		lblPerodo.setBounds(12, 82, 70, 15);
		frame.getContentPane().add(lblPerodo);
		
		JLabel lblAgruparPor = new JLabel("Agrupar por:");
		lblAgruparPor.setBounds(12, 137, 117, 15);
		frame.getContentPane().add(lblAgruparPor);
		
		JComboBox cmbAgrupamento = new JComboBox(Agrupamento.values());
		lblAgruparPor.setLabelFor(cmbAgrupamento);
		cmbAgrupamento.setBounds(12, 164, 167, 24);
		frame.getContentPane().add(cmbAgrupamento);
		
		JPanel pnlGrafico = new JPanel();
		pnlGrafico.setBackground(Color.GRAY);
		pnlGrafico.setBounds(191, 46, 397, 252);
		frame.getContentPane().add(pnlGrafico);
		
		JLabel lblVariaveis = new JLabel("");
		pnlGrafico.add(lblVariaveis);
		
		JLabel lblFuncao = new JLabel("Função");
		lblFuncao.setBounds(12, 200, 70, 15);
		frame.getContentPane().add(lblFuncao);	
		
		JComboBox cmbFuncao = new JComboBox(Funcao.values());
		lblFuncao.setLabelFor(cmbFuncao);
		cmbFuncao.setBounds(12, 214, 167, 24);
		frame.getContentPane().add(cmbFuncao);
		
		JButton btnMostrar = new JButton("Mostrar");
		btnMostrar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String sPeriodo = MessageFormat.format("{0}-{1}", cmbAnoIni.getSelectedItem().toString(), cmbAnoFinal.getSelectedItem().toString());
				Variavel variavel = (Variavel) cmbVariavel.getSelectedItem();
				Funcao funcao = (Funcao) cmbFuncao.getSelectedItem();
				Agrupamento agrupamento = (Agrupamento) cmbAgrupamento.getSelectedItem();
				String sArgumentos = MessageFormat.format("{0} {1} {2} {3}",
						variavel.name(), sPeriodo, agrupamento.getCodigo(), funcao.name());
				lblVariaveis.setText(sArgumentos);
				
			    
				try {
					String comando1 = "/usr/local/hadoop/bin/hdfs dfs -rm -r /user/" + System.getenv("NOMEDEUSUARIO") + "/output/"; 
					String comando2 = "/usr/local/hadoop/bin/hadoop jar main.jar Main " + sArgumentos;
					
					runProcess(comandoToList(comando1));
					runProcess(comandoToList(comando2));
					JOptionPane.showMessageDialog(frame, "Programa Executado!");
				    
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(frame, "Erro ao executar programa. ");
				}
			}
		});
		btnMostrar.setBounds(12, 246, 167, 25);
		frame.getContentPane().add(btnMostrar);
		
		JLabel lblA = new JLabel("a");
		lblA.setBounds(88, 101, 24, 30);
		frame.getContentPane().add(lblA);
	}
	
	void populateComboBoxAnos(JComboBox<Integer> jcbbAno) {
		for(int i=1900;i<2018;i++) {
			jcbbAno.addItem(i);
		}
	}
	void runProcess(List<String> comandos) throws IOException,InterruptedException{
		ProcessBuilder processBuilder = new ProcessBuilder(comandos);
	    System.out.println(comandos);
	    final Process process = processBuilder.start();
	    process.waitFor();
	}
	List<String> comandoToList(String comando){
		List<String> comandos = new ArrayList<String>();
		for(String s : comando.split("\\s+")) {
			comandos.add(s);
		}
		return comandos;
	}
}
