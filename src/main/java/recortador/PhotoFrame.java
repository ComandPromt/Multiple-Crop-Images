package recortador;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

@SuppressWarnings("serial")

public class PhotoFrame extends javax.swing.JFrame {

	static JRadioButtonMenuItem rdbtnmntmNewRadioItem_2 = new JRadioButtonMenuItem("Multiple");

	static PhotoPanel photoPanel = new PhotoPanel();

	javax.swing.JMenu jMenu1;

	javax.swing.JMenuBar jMenuBar1;

	javax.swing.JMenuItem jMenuItem1;

	static javax.swing.JPanel jPanel1;

	private static boolean siguiente = false;

	public static String directorio = Main.getDirectorioActual() + "Config" + Main.getSeparador()
			+ "imagenes_para_recortar";

	static String carpetaRecortes = directorio + Main.getSeparador() + "recortes";

	String carpetaImagenesRotadas = carpetaRecortes + Main.getSeparador() + "Image_rotate";

	static JCheckBoxMenuItem reemplazar = new JCheckBoxMenuItem("Reemplazar");

	private static boolean rotar = false;

	public static LinkedList<String> listaImagenes = new LinkedList<>();

	javax.swing.JScrollPane jScrollPane1;

	static boolean recortar = false;

	static JFileChooser fileChooser = new JFileChooser();

	static int angulo = 90;

	transient static BufferedImage img;

	transient static BufferedImage dst;

	public static JRadioButtonMenuItem rdbtnmntmNormal = new JRadioButtonMenuItem("Normal");

	static JTextField recorrido = new JTextField();

	static int posicionImagen;

	private static JTextField ir;

	private static LinkedList<String> comprobacion = new LinkedList<String>();

	public JTextField getRecorrido() {
		return recorrido;
	}

	public static javax.swing.JPanel getjPanel1() {
		return jPanel1;
	}

	static void guardarImagenRotada() throws IOException {
		int numeroImagenes = Metodos
				.listarFicherosPorCarpeta(new File(carpetaRecortes + Main.getSeparador() + "Image_rotate"), ".");

		String imagen = imagenActual();

		dst = rotacionImagen(img, angulo);

		photoPanel.setPhoto(dst);

		saveJPGImage(dst, ++numeroImagenes, Metodos.extraerExtension(imagen));
	}

	private static void normalizar(boolean atras) throws IOException {

		if (PhotoPanel.paso < 0) {

			inicializar();

		}

		else {

			siguiente = true;

			PhotoPanel.paso -= listaImagenes.size() - comprobacion.size();

			if (atras) {

				--PhotoPanel.paso;
			}

			actualizar();

			if (PhotoPanel.paso < 0) {

				verFoto(0);
			}

			else {
				verFoto(PhotoPanel.paso);
			}

		}

	}

	public static void siguienteImagen() {

		try {

			comprobarImagenes(directorio + Main.getSeparador(), false);

			if (recortar) {

				PhotoPanel.paso = posicionImagen;

				recortar = false;

			}

			if (PhotoPanel.paso < listaImagenes.size()) {
				++PhotoPanel.paso;
			}

			if (PhotoPanel.paso == listaImagenes.size() - 1) {

				PhotoPanel.paso = listaImagenes.size() - 1;

				if (PhotoPanel.paso < 0) {

					Metodos.mensaje("La carpeta se ha quedado vacía", 3);

					recorrido.setText("");

				}

				else {

					photoPanel.setPhoto(ImageIO
							.read(new File(directorio + Main.getSeparador() + listaImagenes.get(PhotoPanel.paso))));

					recorrido.setText(listaImagenes.size() + " / " + listaImagenes.size());

				}

			}

			else {

				if (PhotoPanel.paso >= listaImagenes.size()) {

					PhotoPanel.paso = listaImagenes.size() - 1;

				}

				photoPanel.setPhoto(
						ImageIO.read(new File(directorio + Main.getSeparador() + listaImagenes.get(PhotoPanel.paso))));

				++PhotoPanel.paso;

				recorrido.setText(PhotoPanel.paso + " / " + listaImagenes.size());

				--PhotoPanel.paso;

			}

		}

		catch (IOException e) {

			inicializar();
		}
	}

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

	private static void eliminarImagenSeleccionada(boolean eliminar) {

		try {

			if (!eliminar) {

				if (!PhotoPanel.penultimaFoto && PhotoPanel.paso > 0 && PhotoPanel.paso < listaImagenes.size() - 1) {

					--PhotoPanel.paso;
				}

				else {

					if (PhotoPanel.paso < 0) {

						PhotoPanel.paso = 0;

					}

				}

				posicionImagen = PhotoPanel.paso;

				verFoto(PhotoPanel.paso);

				++PhotoPanel.paso;

			}

			int resp = JOptionPane.showConfirmDialog(null, "¿Esta seguro de borrar la imagen actual?");

			if (resp == 0) {

				if (!eliminar) {

					--PhotoPanel.paso;

					verFoto(PhotoPanel.paso);
				}

				Metodos.eliminarFichero(directorio + Main.getSeparador() + PhotoPanel.saberImagenSeleccionada());

				actualizar();

				verFoto(PhotoPanel.paso);

				++PhotoPanel.paso;

				if (PhotoPanel.paso == 0 && listaImagenes.size() == 0) {

					PhotoPanel.paso = 0;

					recorrido.setText("");

				}

				else {

					recorrido.setText(PhotoPanel.paso + " / " + (listaImagenes.size()));

				}

				--PhotoPanel.paso;

			}

		}

		catch (Exception e) {

			try {
				verFoto(0);
			}

			catch (Exception e1) {
				ir.setText("");
			}

		}

	}

	public static void recortar() {

		try {

			recortar = true;

			if (PhotoPanel.paso >= listaImagenes.size()) {
				PhotoPanel.paso = listaImagenes.size();
			}

			if (rotar) {

				guardarImagenRotada();

				rotar = false;

			}

			else {

				photoPanel.guardar();

				if (PhotoFrame.rdbtnmntmNormal.isSelected()) {

					PhotoPanel.paso = PhotoPanel.entrada;

					if (PhotoPanel.paso > PhotoPanel.entrada) {
						--PhotoPanel.paso;
					}

					if (PhotoPanel.entrada < PhotoPanel.paso) {
						PhotoPanel.paso = PhotoPanel.entrada;
					}

					if (PhotoPanel.entrada == listaImagenes.size()) {
						PhotoPanel.paso = listaImagenes.size() - 1;
					}

					if (PhotoPanel.entrada == listaImagenes.size() - 1 || PhotoPanel.paso == listaImagenes.size()) {

						PhotoPanel.paso = listaImagenes.size() - 1;

						verFoto(PhotoPanel.paso);
					}
				}
			}

			if (reemplazar.isSelected()) {

				eliminarImagenSeleccionada(false);

			}

			if (!comprobarNumeroImagenes()) {

				normalizar(false);

			}

		}

		catch (Exception e1) {

		}

	}

	static void verFoto(int posicion) throws IOException {

		if (posicion == listaImagenes.size()) {
			posicion = listaImagenes.size() - 1;
		}

		if (posicion < 0) {
			posicion = 0;
		}

		PhotoPanel.paso = posicion;

		if (listaImagenes.size() > 0) {

			photoPanel.setPhoto(ImageIO.read(new File(directorio + Main.getSeparador() + listaImagenes.get(posicion))));

			recorrido.setText(++posicion + " / " + listaImagenes.size());
		}

		posicionImagen = PhotoPanel.paso;

		--PhotoFrame.posicionImagen;

	}

	static void actualizar() throws IOException {

		try {

			rotar = false;

			listaImagenes.clear();

			Metodos.borrarArchivosDuplicados(directorio + Main.getSeparador());

			Metodos.borrarArchivosDuplicados(carpetaRecortes + Main.getSeparador());

			listaImagenes = Metodos.directorio(directorio + Main.getSeparador(), ".", true, false);

			listaImagenes.sort(String::compareToIgnoreCase);

		} catch (Exception e) {
			//
		}
	}

	private static BufferedImage loadJPGImage(String directorio) throws IOException {

		BufferedImage imagen = ImageIO.read(new File(directorio));

		BufferedImage source = new BufferedImage(imagen.getWidth(), imagen.getHeight(), BufferedImage.TYPE_INT_RGB);

		source.getGraphics().drawImage(imagen, 0, 0, null);

		return source;
	}

	private static void saveJPGImage(BufferedImage im, int num, String extension) throws IOException {
		ImageIO.write(im, "JPG", new File(carpetaRecortes + Main.getSeparador() + "Image_rotate" + Main.getSeparador()
				+ "Image_rotate_" + num + "." + extension));
	}

	private void rabioBoxPorDefecto() {

		if (rdbtnmntmNormal.isSelected() && !rdbtnmntmNewRadioItem_2.isSelected()) {
			rdbtnmntmNormal.setSelected(true);
		}

	}

	private void irAPosicionImagen() {

		try {

			comprobarImagenes(directorio + Main.getSeparador(), false);

			int destino = Integer.parseInt(Metodos.eliminarEspacios(ir.getText()));

			if (destino > 0 && destino <= listaImagenes.size()) {

				try {
					verFoto(--destino);
				}

				catch (IOException e) {
					inicializar();
				}

			}

			else {
				ir.setText("");
			}

		}

		catch (Exception e) {
			ir.setText("");
		}
	}

	public static boolean comprobarNumeroImagenes() {

		comprobacion.clear();

		comprobacion = Metodos.directorio(directorio + Main.getSeparador(), ".", true, false);

		boolean resultado;

		if (comprobacion.size() != listaImagenes.size()) {
			resultado = false;
		}

		else {
			resultado = true;
		}

		return resultado;

	}

	public static void comprobarImagenes(String directorio, boolean atras) throws IOException {

		if (!comprobarNumeroImagenes()) {

			normalizar(atras);

		} else {
			siguiente = false;
		}

	}

	public PhotoFrame() {

		getContentPane().setBackground(Color.DARK_GRAY);

		setBackground(Color.LIGHT_GRAY);

		photoPanel.setBackground(Color.WHITE);

		photoPanel.setForeground(Color.WHITE);

		setIconImage(Toolkit.getDefaultToolkit().getImage(PhotoFrame.class.getResource("/imagenes/crop.png")));

		initComponents();

		inicializar();

		setMinimumSize(new Dimension(900, 300));

		PhotoFrame.this.setTitle("Periquito - Crop");

		PhotoFrame.this.setLocationRelativeTo(null);

		jPanel1.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

		jPanel1.add(photoPanel);

		JPanel panel = new JPanel();

		jScrollPane1.setColumnHeaderView(panel);

		JButton btnNewButton_2 = new JButton("<|");

		btnNewButton_2.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

				try {

					comprobarImagenes(directorio + Main.getSeparador(), false);

					PhotoPanel.paso = 0;

					verFoto(PhotoPanel.paso);

					posicionImagen = PhotoPanel.paso;

				}

				catch (IOException e) {
					inicializar();
				}

			}

		});
		btnNewButton_2.setFont(new Font("Dialog", Font.BOLD, 16));
		panel.add(btnNewButton_2);

		JButton btnNewButton = new JButton("");
		btnNewButton.setFont(new Font("Dialog", Font.BOLD, 14));

		btnNewButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

				try {

					int posicionPaso = PhotoPanel.paso;

					PhotoPanel.entrada = listaImagenes.size();

					PhotoPanel.paso = posicionImagen;

					--PhotoPanel.paso;

					if (posicionImagen < 0) {
						PhotoPanel.paso = 0;
					}

					if (PhotoPanel.paso == PhotoPanel.entrada) {
						--PhotoPanel.paso;
					}

					else {
						PhotoPanel.paso = posicionImagen;
					}

					if (PhotoPanel.paso >= PhotoPanel.listaImagenes.size()) {

						if (PhotoPanel.listaImagenes.size() > 1) {

							PhotoPanel.paso = PhotoPanel.listaImagenes.size() - 1;
						}

					}

					recortar = false;

					if (PhotoPanel.paso < 0) {

						PhotoPanel.paso = 0;

						verFoto(0);

					}

					else {

						PhotoPanel.paso = posicionPaso;

						if (posicionPaso == PhotoPanel.listaImagenes.size()) {

							PhotoPanel.paso = PhotoPanel.listaImagenes.size() - 1;

						}

						comprobarImagenes(directorio + Main.getSeparador(), true);

						if (!siguiente) {

							--PhotoPanel.paso;

						}

					}

					PhotoPanel.paso = posicionImagen;

					verFoto(PhotoPanel.paso);

					++PhotoPanel.paso;

					recorrido.setText(PhotoPanel.paso + " / " + listaImagenes.size());

					--PhotoPanel.paso;

				} catch (IOException e) {
					inicializar();
				}

			}

		});

		btnNewButton.setIcon(new ImageIcon(PhotoFrame.class.getResource("/imagenes/back.png")));

		panel.add(btnNewButton);

		recorrido.setEditable(false);

		recorrido.setHorizontalAlignment(SwingConstants.CENTER);

		recorrido.setFont(new Font("Dialog", Font.PLAIN, 18));

		panel.add(recorrido);

		recorrido.setColumns(10);

		JButton btnNewButton_1 = new JButton("");
		btnNewButton_1.setFont(new Font("Dialog", Font.BOLD, 14));

		btnNewButton_1.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

				siguienteImagen();

			}

		});

		btnNewButton_1.setIcon(new ImageIcon(PhotoFrame.class.getResource("/imagenes/next.png")));

		panel.add(btnNewButton_1);

		JButton btnNewButton_3 = new JButton(">|");
		btnNewButton_3.setFont(new Font("Dialog", Font.BOLD, 16));

		btnNewButton_3.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

				try {

					comprobarImagenes(directorio + Main.getSeparador(), false);

					PhotoPanel.paso = listaImagenes.size() - 1;

					posicionImagen = PhotoPanel.paso;

					verFoto(PhotoPanel.paso);

				} catch (IOException e) {
					inicializar();
				}

			}
		});

		panel.add(btnNewButton_3);

		ir = new JTextField();

		ir.addKeyListener(new KeyAdapter() {

			@Override

			public void keyPressed(KeyEvent e) {

				if (e.getKeyCode() == KeyEvent.VK_ENTER) {

					irAPosicionImagen();

				}

			}

		});

		ir.setFont(new Font("Dialog", Font.PLAIN, 19));
		ir.setHorizontalAlignment(SwingConstants.CENTER);

		panel.add(ir);

		ir.setColumns(4);

		JButton btnNewButton_6 = new JButton("IR");
		btnNewButton_6.setFont(new Font("Dialog", Font.BOLD, 16));

		btnNewButton_6.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

				irAPosicionImagen();

			}

		});

		panel.add(btnNewButton_6);
	}

	static void inicializar() {

		try {

			actualizar();

			verFoto(0);

		} catch (IOException e1) {
			//
		}
	}

	private static String imagenActual() throws IOException {

		String imagenActual = "";

		if (PhotoPanel.paso < 0) {

			imagenActual = fileChooser.getSelectedFile().toString();

		}

		else {
			imagenActual = directorio + Main.getSeparador() + listaImagenes.get(PhotoPanel.paso);

		}

		img = loadJPGImage(imagenActual);

		return imagenActual;

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
		jMenu1.setFont(new Font("Dialog", Font.PLAIN, 24));
		jMenuItem1 = new javax.swing.JMenuItem();
		jMenuItem1.setFont(new Font("Dialog", Font.PLAIN, 18));
		jMenuItem1.setIcon(new ImageIcon(PhotoFrame.class.getResource("/imagenes/abrir.png")));

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new java.awt.CardLayout());

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
		jPanel1Layout.setHorizontalGroup(
				jPanel1Layout.createParallelGroup(Alignment.LEADING).addGap(0, 801, Short.MAX_VALUE));
		jPanel1Layout.setVerticalGroup(
				jPanel1Layout.createParallelGroup(Alignment.TRAILING).addGap(0, 159, Short.MAX_VALUE));
		jPanel1.setLayout(jPanel1Layout);

		jScrollPane1.setViewportView(jPanel1);

		getContentPane().add(jScrollPane1, "card2");

		jMenu1.setText("File  ");

		jMenuItem1.setText("Open image...");

		jMenuItem1.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(java.awt.event.ActionEvent evt) {

				try {

					Metodos.borrarArchivosDuplicados(directorio + Main.getSeparador());

					Metodos.borrarArchivosDuplicados(carpetaRecortes + Main.getSeparador());

					PhotoPanel.paso = 0;

					listaImagenes.clear();

					listaImagenes = Metodos.directorio(directorio + Main.getSeparador(), ".", true, false);

					listaImagenes.sort(String::compareToIgnoreCase);

					ImageIcon icono = new ImageIcon(jMenuItem1ActionPerformed());

					int ancho = icono.getIconWidth();

					int alto = icono.getIconHeight();

					setSize(new Dimension(ancho, alto));

					if (redimensionarJFrame(ancho, alto)) {
						setExtendedState(JFrame.MAXIMIZED_BOTH);
					}

				}

				catch (Exception e) {

					Metodos.mensaje("Error", 1);
				}

			}

		});

		jMenu1.add(jMenuItem1);

		jMenuBar1.add(jMenu1);

		setJMenuBar(jMenuBar1);

		JMenu mnNewMenu = new JMenu("Modo");
		mnNewMenu.setForeground(Color.BLACK);
		mnNewMenu.setFont(new Font("Dialog", Font.PLAIN, 24));
		mnNewMenu.setIcon(new ImageIcon(PhotoFrame.class.getResource("/imagenes/config.png")));
		jMenuBar1.add(mnNewMenu);

		rdbtnmntmNewRadioItem_2.addMouseListener(new MouseAdapter() {

			@Override

			public void mousePressed(MouseEvent e) {

				if (rdbtnmntmNewRadioItem_2.isSelected()) {
					rdbtnmntmNewRadioItem_2.setSelected(false);
				}

				else {

					if (rdbtnmntmNormal.isSelected()) {
						rdbtnmntmNormal.setSelected(false);
					}

				}

			}

			@Override

			public void mouseReleased(MouseEvent e) {
				rabioBoxPorDefecto();
			}

		});

		rdbtnmntmNewRadioItem_2.setIcon(new ImageIcon(PhotoFrame.class.getResource("/imagenes/multiple.png")));

		rdbtnmntmNewRadioItem_2.setFont(new Font("Dialog", Font.PLAIN, 20));

		mnNewMenu.add(rdbtnmntmNewRadioItem_2);

		JSeparator separator_1 = new JSeparator();

		mnNewMenu.add(separator_1);
		rdbtnmntmNormal.setSelected(true);

		rdbtnmntmNormal.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {

				if (rdbtnmntmNormal.isSelected()) {
					rdbtnmntmNormal.setSelected(false);
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

		rdbtnmntmNormal.setIcon(new ImageIcon(PhotoFrame.class.getResource("/imagenes/simple.png")));

		rdbtnmntmNormal.setFont(new Font("Dialog", Font.PLAIN, 20));

		mnNewMenu.add(rdbtnmntmNormal);

		JLabel lblNewLabel2;

		JLabel lblNewLabel6;

		JMenu mnAbrir = new JMenu("Abrir");

		mnAbrir.setIcon(new ImageIcon(PhotoFrame.class.getResource("/imagenes/folder.png")));

		mnAbrir.setFont(new Font("Dialog", Font.PLAIN, 24));

		mnAbrir.setForeground(Color.BLACK);

		jMenuBar1.add(mnAbrir);

		JMenuItem mntmNewMenuItem = new JMenuItem("Recortes");

		mntmNewMenuItem.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {

				try {

					Metodos.abrirCarpeta(directorio);
				}

				catch (IOException e1) {
					//
				}

			}

		});

		mntmNewMenuItem.setIcon(new ImageIcon(PhotoFrame.class.getResource("/imagenes/crop.png")));
		mntmNewMenuItem.setFont(new Font("Dialog", Font.PLAIN, 20));
		mntmNewMenuItem.setForeground(Color.BLACK);
		mnAbrir.add(mntmNewMenuItem);

		JSeparator separator_3 = new JSeparator();
		mnAbrir.add(separator_3);

		JMenuItem mntmNewMenuItem_1 = new JMenuItem("Imagenes rotadas");

		mntmNewMenuItem_1.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {

				try {
					Metodos.abrirCarpeta(carpetaImagenesRotadas);
				}

				catch (IOException e1) {
					//
				}

			}

		});

		mntmNewMenuItem_1.setIcon(new ImageIcon(PhotoFrame.class.getResource("/imagenes/actualizar.png")));
		mntmNewMenuItem_1.setFont(new Font("Dialog", Font.PLAIN, 20));
		mntmNewMenuItem_1.setForeground(Color.BLACK);
		mnAbrir.add(mntmNewMenuItem_1);

		JMenu mnNewMenu_1 = new JMenu("Rotar");
		mnNewMenu_1.setFont(new Font("Dialog", Font.PLAIN, 24));
		mnNewMenu_1.setIcon(new ImageIcon(PhotoFrame.class.getResource("/imagenes/rotate_180.png")));
		jMenuBar1.add(mnNewMenu_1);
		lblNewLabel6 = new JLabel("Left");
		lblNewLabel6.setFont(new Font("Dialog", Font.PLAIN, 20));
		mnNewMenu_1.add(lblNewLabel6);
		lblNewLabel6.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {

				try {

					if (!comprobarNumeroImagenes()) {

						normalizar(false);

					}

					rotar = true;

					if (angulo <= 0 || angulo >= 360) {
						angulo = 270;
					}

					else {
						angulo -= 90;
					}

					imagenActual();

					dst = rotacionImagen(img, angulo);

					photoPanel.setPhoto(dst);

				} catch (Exception e1) {

				}

			}

		});

		lblNewLabel6.setIcon(new ImageIcon(PhotoFrame.class.getResource("/imagenes/rotate_90.png")));

		JSeparator separator = new JSeparator();

		mnNewMenu_1.add(separator);

		lblNewLabel2 = new JLabel("Right");
		lblNewLabel2.setIcon(new ImageIcon(PhotoFrame.class.getResource("/imagenes/rotate_180.png")));

		lblNewLabel2.setFont(new Font("Dialog", Font.PLAIN, 20));
		mnNewMenu_1.add(lblNewLabel2);
		lblNewLabel2.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {

				try {

					rotar = true;

					if (angulo >= 360) {

						angulo = 90;
					}

					else {
						angulo += 90;
					}

					imagenActual();

					dst = rotacionImagen(img, angulo);

					photoPanel.setPhoto(dst);

				} catch (Exception e1) {

				}
			}
		});

		JLabel lblNewLabel2_1 = new JLabel("    ");

		lblNewLabel2_1.setBackground(Color.LIGHT_GRAY);

		lblNewLabel2_1.addMouseListener(new MouseAdapter() {

			@Override

			public void mousePressed(MouseEvent e) {

				recortar();

			}

		});

		lblNewLabel2_1.setIcon(new ImageIcon(PhotoFrame.class.getResource("/imagenes/cut.png")));

		jMenuBar1.add(lblNewLabel2_1);

		JMenu mnNewMenu_2 = new JMenu("Acciones");

		mnNewMenu_2.setFont(new Font("Dialog", Font.PLAIN, 24));

		mnNewMenu_2.setIcon(new ImageIcon(PhotoFrame.class.getResource("/imagenes/utilities.png")));

		jMenuBar1.add(mnNewMenu_2);

		JMenuItem mntmNewMenuItem_2 = new JMenuItem("Actualizar");

		mntmNewMenuItem_2.addMouseListener(new MouseAdapter() {

			@Override

			public void mousePressed(MouseEvent e) {

				try {

					actualizar();

					if (!listaImagenes.isEmpty()) {

						int resp = JOptionPane.showConfirmDialog(null, "¿Quieres ver la primera imagen?");

						if (resp == 0) {

							verFoto(0);

						}

					}

					else {
						Metodos.mensaje("No hay imagenes en la carpeta para recorar", 2);

						Metodos.abrirCarpeta(directorio);

					}

				}

				catch (IOException e1) {
					//
				}
			}
		});
		mntmNewMenuItem_2.setFont(new Font("Dialog", Font.PLAIN, 20));
		mntmNewMenuItem_2.setIcon(new ImageIcon(PhotoFrame.class.getResource("/imagenes/actualizar.png")));
		mnNewMenu_2.add(mntmNewMenuItem_2);

		JSeparator separator_4 = new JSeparator();
		mnNewMenu_2.add(separator_4);

		JMenuItem mntmNewMenuItem_3 = new JMenuItem("Eliminar Imagen");
		mnNewMenu_2.add(mntmNewMenuItem_3);
		mntmNewMenuItem_3.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {

				eliminarImagenSeleccionada(true);

			}

		});
		mntmNewMenuItem_3.setIcon(new ImageIcon(PhotoFrame.class.getResource("/imagenes/delete.png")));
		mntmNewMenuItem_3.setHorizontalAlignment(SwingConstants.CENTER);
		mntmNewMenuItem_3.setFont(new Font("Dialog", Font.PLAIN, 20));

		JSeparator separator_2 = new JSeparator();
		mnNewMenu_2.add(separator_2);
		reemplazar.setIcon(new ImageIcon(PhotoFrame.class.getResource("/imagenes/utilities.png")));
		reemplazar.setHorizontalAlignment(SwingConstants.CENTER);
		reemplazar.setFont(new Font("Dialog", Font.PLAIN, 20));

		mnNewMenu_2.add(reemplazar);

		JSeparator separator_7 = new JSeparator();
		mnNewMenu_2.add(separator_7);

		JMenu mnNewMenu_3 = new JMenu("Vaciar");

		mnNewMenu_3.setFont(new Font("Dialog", Font.PLAIN, 20));

		mnNewMenu_3.setIcon(new ImageIcon(PhotoFrame.class.getResource("/imagenes/clean.png")));

		mnNewMenu_2.add(mnNewMenu_3);

		JSeparator separator_5 = new JSeparator();

		mnNewMenu_3.add(separator_5);

		JMenuItem mntmNewMenuItem_4 = new JMenuItem("Imagenes a recortar");

		mntmNewMenuItem_4.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {

				int resp = JOptionPane.showConfirmDialog(null, "Desea vaciar la carpeta de las imágenes a recortar?");

				if (resp == 0) {

					Metodos.eliminarArchivos(directorio + Main.getSeparador());

				}

			}

		});

		mntmNewMenuItem_4.setFont(new Font("Dialog", Font.PLAIN, 20));
		mntmNewMenuItem_4.setIcon(new ImageIcon(PhotoFrame.class.getResource("/imagenes/clean.png")));
		mnNewMenu_3.add(mntmNewMenuItem_4);

		JSeparator separator_6 = new JSeparator();
		mnNewMenu_3.add(separator_6);

		JMenuItem mntmNewMenuItem_5 = new JMenuItem("Recortes");

		mntmNewMenuItem_5.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {

				int resp = JOptionPane.showConfirmDialog(null, "Desea vaciar la carpeta de las imágenes recortadas?");

				if (resp == 0) {

					Metodos.eliminarArchivos(carpetaRecortes + Main.getSeparador());
				}

			}

		});

		mntmNewMenuItem_5.setFont(new Font("Dialog", Font.PLAIN, 20));
		mntmNewMenuItem_5.setIcon(new ImageIcon(PhotoFrame.class.getResource("/imagenes/clean.png")));
		mnNewMenu_3.add(mntmNewMenuItem_5);

		JSeparator separator_8 = new JSeparator();
		mnNewMenu_3.add(separator_8);

		JMenuItem mntmNewMenuItem_6 = new JMenuItem("Rotadas");
		mntmNewMenuItem_6.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				System.out.println(carpetaImagenesRotadas);
				int resp = JOptionPane.showConfirmDialog(null, "Desea vaciar la carpeta de las imágenes rotadas?");

				if (resp == 0) {
					try {
						Metodos.eliminarArchivos(carpetaImagenesRotadas + Main.getSeparador());
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}

			}
		});
		mntmNewMenuItem_6.setFont(new Font("Dialog", Font.PLAIN, 20));
		mntmNewMenuItem_6.setIcon(new ImageIcon(PhotoFrame.class.getResource("/imagenes/clean.png")));
		mnNewMenu_3.add(mntmNewMenuItem_6);

	}

	String jMenuItem1ActionPerformed() throws IOException {

		Metodos.convertir(directorio + Main.getSeparador());

		String imagen = "";

		fileChooser.setFileFilter(new FileNameExtensionFilter("Archivo de Imagen", "jpg", "png"));

		fileChooser.setCurrentDirectory(new File(directorio));

		int result = fileChooser.showOpenDialog(null);

		if (result == JFileChooser.APPROVE_OPTION) {

			try {

				imagen = fileChooser.getSelectedFile().toString();

				photoPanel.setPhoto(ImageIO.read(fileChooser.getSelectedFile()));

				int posicionImagen = listaImagenes.indexOf(fileChooser.getSelectedFile().getName());

				if (posicionImagen != -1) {
					recorrido.setText(++posicionImagen + " / " + listaImagenes.size());
				}

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
