package recortador;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@SuppressWarnings("all")
public class Config extends javax.swing.JFrame implements ActionListener, ChangeListener {

	private javax.swing.JLabel jLabel1;
	static javax.swing.JTextField jTextField1;
	private JLabel label;
	private JTextField txtHttplocalhost;

	@SuppressWarnings("all")
	public void buscarArchivoConf() throws IOException {
		File af = new File("Config/Config.txt");

		if (af.exists()) {
			String[] lectura;
			try {
				lectura = Metodos.leerFicheroArray("Config/Config.txt", 2);

				if (lectura[0] == null) {
					lectura[0] = "1";
				}

				if (lectura[1] == null) {
					lectura[1] = "http://localhost/Periquito";
				}

				jTextField1.setText(lectura[0]);
				txtHttplocalhost.setText(lectura[1]);

			} catch (ArrayIndexOutOfBoundsException e) {

			}
			guardarDatos(false);
		}
	}

	public void guardarDatos(Boolean mensaje) {
		try {
			String texto = jTextField1.getText().trim();
			String servidor = txtHttplocalhost.getText().trim();

			if (texto.isEmpty() || texto == null) {
				texto = "C:\\Users\\" + System.getProperty("user.name") + "\\Desktop";
			}

			if (servidor.isEmpty() || servidor == null) {
				servidor = "http://localhost/Periquito";
			}

			FileWriter flS = new FileWriter("Config/Config.txt");
			BufferedWriter fS = new BufferedWriter(flS);

			fS.write(texto);
			fS.newLine();
			fS.write(servidor);
			fS.close();
			flS.close();
			dispose();
			Main.setLectura(Metodos.leerFicheroArray("Config/Config.txt", 2));
			if (mensaje) {
				Metodos.mensaje("Archivo guardado con exito!", 2);
			}

		} catch (IOException e) {

			if (mensaje) {
				Metodos.mensaje("Error al crear el fichero de configuracion", 1);
			}

		}
	}

	public Config() throws IOException {
		setIconImage(Toolkit.getDefaultToolkit().getImage(Config.class.getResource("/imagenes/config.png")));
		setTitle("Periquito v3 Config ");
		setType(Type.UTILITY);
		initComponents();
		this.setVisible(true);
	}

	public void initComponents() throws IOException {

		jTextField1 = new JTextField();
		jTextField1.setHorizontalAlignment(SwingConstants.CENTER);
		jTextField1.setToolTipText("");

		txtHttplocalhost = new JTextField();
		txtHttplocalhost.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					guardarDatos(true);
				}
			}
		});
		txtHttplocalhost.setText("http://localhost/Periquito");
		txtHttplocalhost.setToolTipText("");
		txtHttplocalhost.setHorizontalAlignment(SwingConstants.CENTER);
		txtHttplocalhost.setFont(new Font("Tahoma", Font.PLAIN, 20));

		jLabel1 = new javax.swing.JLabel();
		jLabel1.setText("Utils");
		jLabel1.setIcon(new ImageIcon(Config.class.getResource("/imagenes/folder.png")));
		jLabel1.setHorizontalAlignment(SwingConstants.CENTER);

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
		setResizable(false);

		jTextField1.setFont(new Font("Tahoma", Font.PLAIN, 20));

		jLabel1.setFont(new Font("Tahoma", Font.BOLD, 20));
		buscarArchivoConf();

		JButton btnNewButton = new JButton("");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {

				File[] files = Metodos.seleccionar(1, "Elija la carpeta para hacer el backup de la bd",
						"Elija una carpeta para hacer el backup de la bd");

				try {
					jTextField1.setText(files[0].getCanonicalPath());
				} catch (Exception e1) {

				}

			}
		});
		btnNewButton.setIcon(new ImageIcon(Config.class.getResource("/imagenes/abrir.png")));

		label = new JLabel();
		label.setIcon(new ImageIcon(Config.class.getResource("/imagenes/remote.png")));
		label.setText("Web");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setFont(new Font("Tahoma", Font.BOLD, 20));

		JButton btnNewButton_1 = new JButton("");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnNewButton_1.setIcon(new ImageIcon(Config.class.getResource("/imagenes/save.png")));
		btnNewButton_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				guardarDatos(true);
			}
		});

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING).addGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addGroup(layout.createParallelGroup(Alignment.LEADING, false)
						.addComponent(label, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(jLabel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				.addGap(26)
				.addGroup(layout.createParallelGroup(Alignment.TRAILING, false).addComponent(txtHttplocalhost)
						.addComponent(jTextField1, GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE))
				.addGroup(layout.createParallelGroup(Alignment.LEADING)
						.addGroup(layout.createSequentialGroup().addGap(51)
								.addComponent(btnNewButton_1, 0, 0, Short.MAX_VALUE).addContainerGap())
						.addGroup(
								Alignment.TRAILING, layout.createSequentialGroup()
										.addPreferredGap(ComponentPlacement.RELATED).addComponent(btnNewButton,
												GroupLayout.PREFERRED_SIZE, 48, GroupLayout.PREFERRED_SIZE)
										.addGap(31)))));
		layout.setVerticalGroup(layout.createParallelGroup(Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(Alignment.LEADING)
						.addGroup(layout.createSequentialGroup().addGap(24)
								.addGroup(layout.createParallelGroup(Alignment.BASELINE)
										.addComponent(jLabel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)
										.addComponent(jTextField1, GroupLayout.PREFERRED_SIZE, 38,
												GroupLayout.PREFERRED_SIZE))
								.addGap(18))
						.addGroup(
								Alignment.TRAILING,
								layout.createSequentialGroup().addContainerGap()
										.addComponent(btnNewButton, GroupLayout.PREFERRED_SIZE, 47,
												GroupLayout.PREFERRED_SIZE)
										.addGap(27)))
						.addGroup(layout
								.createParallelGroup(Alignment.LEADING)
								.addGroup(layout.createSequentialGroup().addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(label, GroupLayout.PREFERRED_SIZE, 64,
												GroupLayout.PREFERRED_SIZE))
								.addGroup(layout.createSequentialGroup().addGap(16)
										.addGroup(layout.createParallelGroup(Alignment.LEADING)
												.addComponent(btnNewButton_1).addComponent(txtHttplocalhost,
														GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
														GroupLayout.PREFERRED_SIZE))))
						.addContainerGap(23, Short.MAX_VALUE)));
		getContentPane().setLayout(layout);
		setSize(new Dimension(532, 231));
		setLocationRelativeTo(null);
	}

	public void actionPerformed(ActionEvent arg0) {
	}

	public void stateChanged(ChangeEvent e) {
	}
}