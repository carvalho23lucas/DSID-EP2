import java.awt.EventQueue;

import javax.swing.JFrame;import javax.swing.event.ListDataListener;
import javax.swing.plaf.LabelUI;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;
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
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		

		JButton btnMostrar = new JButton("Mostrar");
		btnMostrar.setBounds(12, 210, 117, 25);
		frame.getContentPane().add(btnMostrar);
		
		JComboBox cmbVariavel = new JComboBox(Variavel.values());
		cmbVariavel.setBounds(12, 46, 117, 24);
		frame.getContentPane().add(cmbVariavel);
		
		JComboBox<Integer> cmbAnoIni = new JComboBox<Integer>();
		cmbAnoIni.setBounds(12, 101, 58, 24);
		populateComboBoxAnos(cmbAnoIni);
		frame.getContentPane().add(cmbAnoIni);
		
		JComboBox<Integer> cmbAnoFinal = new JComboBox<Integer>();
		cmbAnoFinal.setBounds(71, 101, 58, 24);
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
		cmbAgrupamento.setBounds(12, 164, 117, 24);
		frame.getContentPane().add(cmbAgrupamento);
		
		JPanel pnlGrafico = new JPanel();
		pnlGrafico.setBackground(Color.GRAY);
		pnlGrafico.setBounds(147, 46, 276, 189);
		frame.getContentPane().add(pnlGrafico);
		
		JLabel lblLblvariaveis = new JLabel("");
		pnlGrafico.add(lblLblvariaveis);
		
		btnMostrar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String sPeriodo = MessageFormat.format("{0}-{1}", cmbAnoIni.getSelectedItem().toString(), cmbAnoFinal.getSelectedItem().toString());
				Variavel var = (Variavel) cmbVariavel.getSelectedItem();
				Agrupamento agrupamento = (Agrupamento) cmbAgrupamento.getSelectedItem();
				lblLblvariaveis.setText(MessageFormat.format("{0} {1} {2}",
						var.toString(), sPeriodo, agrupamento.toString()));
			}
		});
	}
	void populateComboBoxAnos(JComboBox<Integer> jcbbAno) {
		for(int i=1900;i<2018;i++) {
			jcbbAno.addItem(i);
		}
	}
	public enum Variavel{
		Temperatura,Umidade,Visibilidade
	}
	
	public enum Agrupamento{
		Ano,Mês,Semana,Dia
	}
}
