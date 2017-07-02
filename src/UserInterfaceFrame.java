import java.awt.EventQueue;

import javax.swing.JFrame;import javax.swing.event.ListDataListener;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;

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
		btnMostrar.setBounds(103, 210, 117, 25);
		frame.getContentPane().add(btnMostrar);
		
		JComboBox cmbVariavel = new JComboBox(Variavel.values());
		cmbVariavel.setBounds(103, 46, 117, 24);
		frame.getContentPane().add(cmbVariavel);
		
		JComboBox<Integer> cmbAnoIni = new JComboBox<Integer>();
		cmbAnoIni.setBounds(103, 101, 58, 24);
		populateComboBoxAnos(cmbAnoIni);
		frame.getContentPane().add(cmbAnoIni);
		
		JComboBox<Integer> cmbAnoFinal = new JComboBox<Integer>();
		cmbAnoFinal.setBounds(162, 101, 58, 24);
		populateComboBoxAnos(cmbAnoFinal);
		frame.getContentPane().add(cmbAnoFinal);
		
		JLabel lblVarivel = new JLabel("Variável");
		lblVarivel.setBounds(103, 30, 70, 15);
		frame.getContentPane().add(lblVarivel);
		
		JLabel lblPerodo = new JLabel("Período");
		lblPerodo.setBounds(103, 82, 70, 15);
		frame.getContentPane().add(lblPerodo);
		
		JLabel lblAgruparPor = new JLabel("Agrupar por:");
		lblAgruparPor.setBounds(103, 137, 117, 15);
		frame.getContentPane().add(lblAgruparPor);
		
		JComboBox cmbAgrupamento = new JComboBox(Agrupamento.values());
		cmbAgrupamento.setBounds(103, 164, 117, 24);
		frame.getContentPane().add(cmbAgrupamento);
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
