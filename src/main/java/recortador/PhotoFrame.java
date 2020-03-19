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

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.filechooser.FileNameExtensionFilter;

@SuppressWarnings("serial")
public class PhotoFrame extends javax.swing.JFrame {

	static JRadioButtonMenuItem rdbtnmntmNewRadioItem = new JRadioButtonMenuItem("Simple");

	static JRadioButtonMenuItem rdbtnmntmNewRadioItem_2 = new JRadioButtonMenuItem("Multiple");
	static PhotoPanel photoPanel = new PhotoPanel();
	javax.swing.JMenu jMenu1;
	javax.swing.JMenuBar jMenuBar1;
	javax.swing.JMenuItem jMenuItem1;
	static javax.swing.JPanel jPanel1;

	public static javax.swing.JPanel getjPanel1() {
		return jPanel1;
	}

	javax.swing.JScrollPane jScrollPane1;

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

	public boolean redimensionarJFrame(int ancho, int alto) {

		boolean resultado = false;

		Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();

		int width = pantalla.width;

		ancho += 20;

		alto += 113;

		if (width > ancho) {

			setSize(ancho, alto);
		}

		else {
			resultado = true;

		}

		setLocationRelativeTo(null);

		setVisible(true);

		return resultado;
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
		if (rdbtnmntmNewRadioItem_2.isSelected() && !rdbtnmntmNewRadioItem.isSelected()) {
			rdbtnmntmNewRadioItem_2.setSelected(true);
		}
	}

	public PhotoFrame() {
		setResizable(false);
		setType(Type.POPUP);
		photoPanel.setBackground(Color.WHITE);
		photoPanel.setForeground(Color.WHITE);
		setIconImage(Toolkit.getDefaultToolkit().getImage(PhotoFrame.class.getResource("/imagenes/crop.png")));
		initComponents();
		rdbtnmntmNewRadioItem_2.setSelected(true);

		setMinimumSize(new Dimension(656, 300));
		PhotoFrame.this.setTitle("Periquito - Crop");
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
		jMenu1.setForeground(Color.BLACK);
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

					Metodos.renombrarArchivos(Main.getDirectorioActual() + "Config" + Main.getSeparador()
							+ "imagenes_para_recortar" + Main.getSeparador() + "recortes" + Main.getSeparador(), ".",
							true);

					ImageIcon icono = new ImageIcon(jMenuItem1ActionPerformed());

					int ancho = icono.getIconWidth();

					int alto = icono.getIconHeight();

					setSize(new Dimension(ancho, alto));

					if (redimensionarJFrame(ancho, alto)) {
						setExtendedState(JFrame.MAXIMIZED_BOTH);
					}

				}

				catch (Exception e) {
					e.printStackTrace();
					Metodos.mensaje("Error", 1);
				}

			}

		});

		jMenu1.add(jMenuItem1);

		jMenuBar1.add(jMenu1);

		setJMenuBar(jMenuBar1);

		JMenu mnNewMenu = new JMenu("Modo");
		mnNewMenu.setForeground(Color.BLACK);
		mnNewMenu.setFont(new Font("Segoe UI", Font.BOLD, 24));
		mnNewMenu.setIcon(new ImageIcon(PhotoFrame.class.getResource("/imagenes/config.png")));
		jMenuBar1.add(mnNewMenu);

		rdbtnmntmNewRadioItem.setFont(new Font("Segoe UI", Font.BOLD, 20));
		rdbtnmntmNewRadioItem.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {

				if (rdbtnmntmNewRadioItem.isSelected()) {
					rdbtnmntmNewRadioItem.setSelected(false);
				}

				else {
					if (rdbtnmntmNewRadioItem_2.isSelected()) {
						rdbtnmntmNewRadioItem_2.setSelected(false);
					}
				}

			}

			@Override
			public void mouseReleased(MouseEvent e) {
				rabioBoxPorDefecto();
			}
		});
		rdbtnmntmNewRadioItem.setIcon(new ImageIcon(PhotoFrame.class.getResource("/imagenes/simple.png")));
		mnNewMenu.add(rdbtnmntmNewRadioItem);

		JSeparator separator = new JSeparator();
		mnNewMenu.add(separator);

		rdbtnmntmNewRadioItem_2.addMouseListener(new MouseAdapter() {

			@Override

			public void mousePressed(MouseEvent e) {

				if (rdbtnmntmNewRadioItem_2.isSelected()) {
					rdbtnmntmNewRadioItem_2.setSelected(false);
				}

				else {

					if (rdbtnmntmNewRadioItem.isSelected()) {
						rdbtnmntmNewRadioItem.setSelected(false);
					}

				}

			}

			@Override

			public void mouseReleased(MouseEvent e) {
				rabioBoxPorDefecto();
			}

		});

		rdbtnmntmNewRadioItem_2.setIcon(new ImageIcon(PhotoFrame.class.getResource("/imagenes/multiple.png")));
		rdbtnmntmNewRadioItem_2.setFont(new Font("Segoe UI", Font.BOLD, 20));
		mnNewMenu.add(rdbtnmntmNewRadioItem_2);

		JLabel lblNewLabel2;
		lblNewLabel2 = new JLabel("    ");
		lblNewLabel2.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {

				try {

					if (angulo >= 360) {

						angulo = 90;
					}

					else {
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

		JMenu mnAbrir = new JMenu("Abrir");
		mnAbrir.setIcon(new ImageIcon(PhotoFrame.class.getResource("/imagenes/folder.png")));
		mnAbrir.setFont(new Font("Segoe UI", Font.BOLD, 24));
		mnAbrir.setForeground(Color.BLACK);
		jMenuBar1.add(mnAbrir);

		JMenuItem mntmNewMenuItem = new JMenuItem("Recortes");
		mntmNewMenuItem.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				try {
					Metodos.abrirCarpeta(Main.getDirectorioActual() + "Config" + Main.getSeparador()
							+ "imagenes_para_recortar" + Main.getSeparador() + "recortes");
				} catch (IOException e1) {
					//
				}
			}
		});
		mntmNewMenuItem.setIcon(new ImageIcon(PhotoFrame.class.getResource("/imagenes/crop.png")));
		mntmNewMenuItem.setFont(new Font("Segoe UI", Font.BOLD, 20));
		mntmNewMenuItem.setForeground(Color.BLACK);
		mnAbrir.add(mntmNewMenuItem);

		JSeparator separator_3 = new JSeparator();
		mnAbrir.add(separator_3);

		JMenuItem mntmNewMenuItem_1 = new JMenuItem("Imagenes rotadas");
		mntmNewMenuItem_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				try {
					Metodos.abrirCarpeta(Main.getDirectorioActual() + "Config" + Main.getSeparador() + "Image_rotate");
				} catch (IOException e1) {
					//
				}
			}
		});
		mntmNewMenuItem_1.setIcon(new ImageIcon(PhotoFrame.class.getResource("/imagenes/actualizar.png")));
		mntmNewMenuItem_1.setFont(new Font("Segoe UI", Font.BOLD, 20));
		mntmNewMenuItem_1.setForeground(Color.BLACK);
		mnAbrir.add(mntmNewMenuItem_1);

		lblNewLabel6.setIcon(new ImageIcon(PhotoFrame.class.getResource("/imagenes/rotate_90.png")));
		jMenuBar1.add(lblNewLabel6);
		lblNewLabel2.setIcon(new ImageIcon(PhotoFrame.class.getResource("/imagenes/rotate_180.png")));
		jMenuBar1.add(lblNewLabel2);
		JLabel lblNewLabel3;
		lblNewLabel3 = new JLabel("  ");

		lblNewLabel3.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				try {
					int numeroImagenes = Metodos.listarFicherosPorCarpeta(new File("Config/Image_rotate"), ".");
					saveJPGImage(dst, ++numeroImagenes,
							Metodos.extraerExtension(fileChooser.getSelectedFile().toString()));
				} catch (Exception e1) {
					//
				}
			}
		});
		lblNewLabel3.setIcon(new ImageIcon(PhotoFrame.class.getResource("/imagenes/save.png")));
		jMenuBar1.add(lblNewLabel3);

	}

	private String jMenuItem1ActionPerformed() throws IOException {

		String imagen = "";

		File miDir = new File(".");

		fileChooser.setFileFilter(new FileNameExtensionFilter("Archivo de Imagen", "jpg", "png"));

		fileChooser.setCurrentDirectory(new File(miDir.getCanonicalPath() + Main.getSeparador() + "Config"
				+ Main.getSeparador() + "imagenes_para_recortar"));

		int result = fileChooser.showOpenDialog(null);

		if (result == JFileChooser.APPROVE_OPTION) {

			try {

				imagen = fileChooser.getSelectedFile().toString();

				photoPanel.setPhoto(ImageIO.read(fileChooser.getSelectedFile()));

			} catch (IOException ex) {
				//
			}

		}

		return imagen;
	}

	public static void main(String[] args) {

		try {

			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {

				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}

			}

		}

		catch (Exception ex) {
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