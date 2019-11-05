package recortador;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.filechooser.FileNameExtensionFilter;

@SuppressWarnings("serial")
public class PhotoFrame extends javax.swing.JFrame {

	static PhotoPanel photoPanel = new PhotoPanel();
	javax.swing.JMenu jMenu1;
	javax.swing.JMenuBar jMenuBar1;
	javax.swing.JMenuItem jMenuItem1;
	private javax.swing.JPanel jPanel1;
	javax.swing.JScrollPane jScrollPane1;
	private JRadioButton rdbtnSingle;
	static JRadioButton rdbtnMultipleCrop;

	static JFileChooser fileChooser = new JFileChooser();

	int angulo = 90;
	transient BufferedImage img;
	transient BufferedImage dst;

	public static BufferedImage rotacionImagen(BufferedImage origen, double grados) {
		BufferedImage destinationImage;
		ImageTransform imTransform = new ImageTransform(origen.getHeight(), origen.getWidth());
		imTransform.rotate(grados);
		imTransform.findTranslation();
		AffineTransformOp ato = new AffineTransformOp(imTransform.getTransform(), AffineTransformOp.TYPE_BILINEAR);
		destinationImage = ato.createCompatibleDestImage(origen, origen.getColorModel());
		return ato.filter(origen, destinationImage);
	}

	private static BufferedImage loadJPGImage(String ruta) throws IOException {
		BufferedImage imagen = ImageIO.read(new File(ruta));

		BufferedImage source = new BufferedImage(imagen.getWidth(), imagen.getHeight(), BufferedImage.TYPE_INT_RGB);
		source.getGraphics().drawImage(imagen, 0, 0, null);
		return source;
	}

	private static void saveJPGImage(BufferedImage im, int num, String extension) throws IOException {
		ImageIO.write(im, "JPG", new File("Config/Image_rotate/Image_rotate_" + num + "." + extension));
	}

	private void rabioBoxPorDefecto() {
		if (!rdbtnMultipleCrop.isSelected() && !rdbtnSingle.isSelected()) {
			rdbtnMultipleCrop.setSelected(true);
		}
	}

	public PhotoFrame() {
		photoPanel.setBackground(Color.WHITE);
		photoPanel.setForeground(Color.WHITE);
		setIconImage(Toolkit.getDefaultToolkit().getImage(PhotoFrame.class.getResource("/imagenes/crop.png")));
		initComponents();

		this.setSize(new Dimension(656, 600));

		PhotoFrame.this.setTitle("Recortador de imágenes");
		PhotoFrame.this.setLocationRelativeTo(null);
		this.jPanel1.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		this.jPanel1.add(photoPanel);
	}

	@SuppressWarnings("all")
	private void initComponents() {

		jScrollPane1 = new javax.swing.JScrollPane();
		jPanel1 = new javax.swing.JPanel();
		jMenuBar1 = new javax.swing.JMenuBar();
		jMenuBar1.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		jMenu1 = new javax.swing.JMenu();
		jMenu1.setIcon(new ImageIcon(PhotoFrame.class.getResource("/imagenes/insert.png")));
		jMenu1.setFont(new Font("Segoe UI", Font.BOLD, 20));
		jMenuItem1 = new javax.swing.JMenuItem();
		jMenuItem1.setFont(new Font("Segoe UI", Font.BOLD, 16));
		jMenuItem1.setIcon(new ImageIcon(PhotoFrame.class.getResource("/imagenes/abrir.png")));

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new java.awt.CardLayout());

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 487, Short.MAX_VALUE));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 499, Short.MAX_VALUE));

		jScrollPane1.setViewportView(jPanel1);

		getContentPane().add(jScrollPane1, "card2");

		jMenu1.setText("File  ");

		jMenuItem1.setText("Open image...");
		jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {
					LinkedList<String> listaImagenes = new LinkedList<>();

					listaImagenes = Metodos.directorio(
							Main.getDirectorioActual() + "Config" + Main.getSeparador() + "imagenes_para_recortar",
							".");

					Metodos.renombrarArchivos(listaImagenes, Main.getDirectorioActual() + "Config" + Main.getSeparador()
							+ "imagenes_para_recortar" + Main.getSeparador());

					jMenuItem1ActionPerformed();

				} catch (IOException e) {
					Metodos.mensaje("Error", 1);
				}
			}
		});

		jMenu1.add(jMenuItem1);

		jMenuBar1.add(jMenu1);

		setJMenuBar(jMenuBar1);

		JMenu mnNewMenu = new JMenu("Modo");
		mnNewMenu.setFont(new Font("Segoe UI", Font.BOLD, 24));
		mnNewMenu.setIcon(new ImageIcon(PhotoFrame.class.getResource("/imagenes/config.png")));
		jMenuBar1.add(mnNewMenu);

		rdbtnSingle = new JRadioButton("Simple");
		mnNewMenu.add(rdbtnSingle);
		rdbtnSingle.setFont(new Font("Dialog", Font.BOLD, 20));

		JSeparator separator = new JSeparator();
		mnNewMenu.add(separator);

		rdbtnMultipleCrop = new JRadioButton("Multiple");
		mnNewMenu.add(rdbtnMultipleCrop);
		rdbtnMultipleCrop.setFont(new Font("Dialog", Font.BOLD, 20));

		rdbtnMultipleCrop.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (rdbtnSingle.isSelected()) {
					rdbtnSingle.setSelected(false);
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				rabioBoxPorDefecto();
			}
		});
		rdbtnMultipleCrop.setSelected(true);

		rdbtnSingle.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				if (rdbtnMultipleCrop.isSelected()) {
					rdbtnMultipleCrop.setSelected(false);
				}
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				rabioBoxPorDefecto();
			}

		});
		JLabel lblNewLabel2;
		lblNewLabel2 = new JLabel("    ");
		lblNewLabel2.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {

				try {

					if (angulo >= 360) {
						angulo = 90;
					} else {
						angulo += 90;
					}

					img = loadJPGImage(fileChooser.getSelectedFile().toString());

					dst = rotacionImagen(img, angulo);
					photoPanel.setPhoto(dst);

				} catch (Exception e1) {
					//
				}
			}
		});

		JLabel lblNewLabel6;
		lblNewLabel6 = new JLabel("  ");
		lblNewLabel6.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				try {
					if (angulo <= 0 || angulo >= 360) {
						angulo = 270;
					} else {
						angulo -= 90;
					}

					img = loadJPGImage(fileChooser.getSelectedFile().toString());

					dst = rotacionImagen(img, angulo);
					photoPanel.setPhoto(dst);

				} catch (Exception e1) {
					//
				}
			}
		});

		lblNewLabel6.setIcon(new ImageIcon(PhotoFrame.class.getResource("/imagenes/rotate_90.png")));
		jMenuBar1.add(lblNewLabel6);
		lblNewLabel2.setIcon(new ImageIcon(PhotoFrame.class.getResource("/imagenes/rotate_180.png")));
		jMenuBar1.add(lblNewLabel2);
		JLabel lblNewLabel3;
		JLabel lblNewLabel4;
		JLabel lblNewLabel5;
		lblNewLabel3 = new JLabel("  ");

		lblNewLabel3.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				try {
					int numeroImagenes = Metodos.listarFicherosPorCarpeta(
							new File(Main.getDirectorioActual() + "Config/Image_rotate"), ".");
					saveJPGImage(dst, ++numeroImagenes,
							Metodos.extraerExtension(fileChooser.getSelectedFile().toString()));
				} catch (Exception e1) {
					//
				}
			}
		});
		lblNewLabel4 = new JLabel("   ");
		lblNewLabel4.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				try {
					Metodos.abrirCarpeta(Main.getDirectorioActual() + "Config" + Main.getSeparador() + "Image_rotate");
				} catch (IOException e1) {
					//
				}
			}
		});
		lblNewLabel4.setIcon(new ImageIcon(PhotoFrame.class.getResource("/imagenes/folder.png")));
		jMenuBar1.add(lblNewLabel4);
		lblNewLabel5 = new JLabel("  ");
		lblNewLabel5.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				try {
					Metodos.abrirCarpeta(
							Main.getDirectorioActual() + "Config" + Main.getSeparador() + "imagenes_para_recortar");
				} catch (IOException e1) {
					//
				}
			}
		});
		lblNewLabel5.setIcon(new ImageIcon(PhotoFrame.class.getResource("/imagenes/crop.png")));
		jMenuBar1.add(lblNewLabel5);
		lblNewLabel3.setIcon(new ImageIcon(PhotoFrame.class.getResource("/imagenes/save.png")));
		jMenuBar1.add(lblNewLabel3);

	}

	private void jMenuItem1ActionPerformed() throws IOException {

		File miDir = new File(".");

		fileChooser.setFileFilter(new FileNameExtensionFilter("Archivo de Imagen", "jpg", "png"));

		fileChooser.setCurrentDirectory(new File(miDir.getCanonicalPath() + Main.getSeparador() + "Config"
				+ Main.getSeparador() + "imagenes_para_recortar"));

		int result = fileChooser.showOpenDialog(null);

		if (result == JFileChooser.APPROVE_OPTION) {

			try {

				photoPanel.setPhoto(ImageIO.read(fileChooser.getSelectedFile()));
			}

			catch (IOException ex) {
				//
			}
		}
	}

	public static void main(String[] args) {

		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception ex) {
			java.util.logging.Logger.getLogger(PhotoFrame.class.getName()).log(java.util.logging.Level.SEVERE, null,
					ex);
		}

		java.awt.EventQueue.invokeLater(new Runnable() {

			public void run() {
				new PhotoFrame().setVisible(true);
			}
		});
	}
}