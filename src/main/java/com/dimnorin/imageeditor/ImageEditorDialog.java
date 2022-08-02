package com.dimnorin.imageeditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.RenderedImage;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.info.file.InfoFile;

import button.SimpleButton;
import checkbox.JCheckBoxCustom;
import combo_suggestion.ComboBoxSuggestion;
import componente.PopupAlerts;
import drag_and_drop.UtilDragAndDrop;
import net.miginfocom.swing.MigLayout;
import simple.chooser.DemoJavaFxStage;
import slider_material.JsliderCustom;
import spinner.Spinner;
import util.Metodos;

@SuppressWarnings("serial")

public class ImageEditorDialog extends JFrame {

	List<String> lista = new LinkedList<String>();

	List<File> comprobacion = new LinkedList<File>();

	LinkedList<String> comprobacionCarpeta = new LinkedList<String>();

	boolean carpeta;

	public static final int BRIGHTNESS_MULT = 1;

	DemoJavaFxStage test;

	int indice = -1;

	public static final int CONTRAST_MULT = 10;

	public static final int HUE_MULT = 360;

	public static final int SATURATION_MULT = 100;

	static String f;

	float contraste;

	int brillo;

	float matiz;

	float saturacion;

	private Image image;

	private ImageAdjuster imageAdjuster;

	private ImagePanel imgPanel;

	int angulo;

	boolean rotar;

	boolean cortar;

	int contar;

	JsliderCustom slHue;

	JsliderCustom slBrightness;

	JsliderCustom slContrast;

	JsliderCustom slSaturation;

	JCheckBoxCustom multiple;

	JLabel contador;

	Spinner textField;

	JCheckBoxCustom folder;

	InfoFile prueba;

	private void rotar(boolean b) {

		if (!lista.isEmpty()) {

			rotar = true;

			if (b) {

				if (angulo >= 360) {

					angulo = 90;

				}

				else {

					angulo += 90;

				}

			}

			else {

				if (angulo <= 0 || angulo >= 360) {

					angulo = 270;

				}

				else {

					angulo -= 90;
				}

			}

			if (angulo == 0 || angulo == 360) {

				rotar = false;

			}

			rotarImagen();

			ponerImagen(lista.get(indice));

		}

	}

	private void rotarImagen() {

		try {

			imgPanel.setImg(Metodos.rotacionImagen(ImageIO.read(new File(lista.get(indice))), angulo));

		}

		catch (Exception e) {

		}

	}

	private void guardarImagen(boolean multiple) {

		try {

			if (!lista.isEmpty()) {

				if (multiple) {

					for (int i = 0; i < lista.size(); i++) {

						ponerImagen(lista.get(i));

						if (cortar) {

							imgPanel.pintar();

						}

						ImageIO.write((RenderedImage) imgPanel.getImg(), "png",
								new File("./images/"
										+ lista.get(i).substring(lista.get(i).lastIndexOf(Metodos.saberSeparador()),
												lista.get(i).length())
										+ "_output_image_" + i + ".png"));

					}

					ponerImagen(lista.get(indice));

				}

				else {

					ImageIO.write((RenderedImage) imgPanel.getImg(), "png",
							new File("./images/" + lista.get(indice).substring(
									lista.get(indice).lastIndexOf(Metodos.saberSeparador()), lista.get(indice).length())
									+ "_output_image_.png"));

				}

				PopupAlerts.mensaje("Images converted", 2, 28);

			}

		}

		catch (Exception e) {

			e.printStackTrace();

		}

	}

	private void adelante() {

		if (!lista.isEmpty()) {

			indice++;

			if (indice >= lista.size()) {

				indice = lista.size();

				indice--;

			}

			ponerImagen(lista.get(indice));

		}

	}

	private void atras() {

		if (!lista.isEmpty()) {

			indice--;

			if (indice < 0) {

				indice = 0;

			}

			ponerImagen(lista.get(indice));

		}

	}

	private void ponerImagen(String file) {

		contar = indice;

		contar++;

		contador.setText(contar + " / " + lista.size());

		f = new File(file).getAbsolutePath();

		image = new ImageIcon(f).getImage();

		int hue = slHue.getValue();

		int con = slContrast.getValue();

		int sar = slSaturation.getValue();

		int br = slBrightness.getValue();

		imgPanel.setImg(image);

		if (rotar) {

			try {

				imgPanel.setImg(Metodos.rotacionImagen(ImageIO.read(new File(file)), angulo));

			}

			catch (Exception e) {

			}

		}

		if (cortar) {

			imgPanel.pintar();

			imageAdjuster.doCrop();

		}

		slHue.setValue(0);

		slContrast.setValue(0);

		slSaturation.setValue(0);

		slBrightness.setValue(0);

		slHue.setValue(hue);

		slContrast.setValue(con);

		slSaturation.setValue(sar);

		slBrightness.setValue(br);

	}

	private void resetear() {

		rotar = false;

		cortar = false;

		angulo = 90;

		rotar(false);

		slBrightness.setValue(BRIGHTNESS_MULT * ImageAdjuster.DEF_OFFSET);

		slContrast.setValue(CONTRAST_MULT * ImageAdjuster.DEF_SCALE);

		slHue.setValue(HUE_MULT * ImageAdjuster.DEF_HUE);

		slSaturation.setValue(SATURATION_MULT * ImageAdjuster.DEF_SATURATION);

		imageAdjuster.resetImage();

		if (!lista.isEmpty()) {

			ponerImagen(lista.get(indice));

		}

	}

	private void calcularIndice() {

		try {

			indice = textField.getValor();

			indice--;

		}

		catch (Exception e) {

		}

	}

	private void ponerImagenSpinner() {

		calcularIndice();

		if (!lista.isEmpty() && indice < lista.size()) {

			ponerImagen(lista.get(indice));

		}

	}

	public ImageEditorDialog() {

		try {

			Metodos.crearCarpeta(Metodos.rutaActual() + "images");

		}

		catch (Exception e) {

		}

		setIconImage(Toolkit.getDefaultToolkit().getImage(ImageEditorDialog.class.getResource("/images/crop.png")));

		cortar = false;

		rotar = false;

		JCheckBoxCustom folder = new JCheckBoxCustom("Multiple", 2);

		contador = new JLabel("");

		multiple = new JCheckBoxCustom("Multiple", SwingConstants.LEFT);

		multiple.setBackground(new Color(0, 0, 255));

		multiple.setHorizontalAlignment(SwingConstants.CENTER);

		lista = new LinkedList<String>();

		indice = -2;

		setResizable(false);

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		setTitle("Edit Photo");

		setSize(new Dimension(880, 600));

		getContentPane().setLayout(new BorderLayout(0, 0));

		imgPanel = new ImagePanel();

		imgPanel.addMouseWheelListener(new MouseWheelListener() {

			public void mouseWheelMoved(MouseWheelEvent e) {

				if (e.getWheelRotation() < 0) {

					adelante();

				}

				else {

					atras();

				}

			}

		});

		imgPanel.setForeground(Color.WHITE);

		imgPanel.setBackground(Color.WHITE);

		this.imageAdjuster = new ImageAdjuster(imgPanel);

		getContentPane().add(imgPanel, BorderLayout.CENTER);

		JPanel propsPanel = new JPanel();

		getContentPane().add(propsPanel, BorderLayout.NORTH);

		propsPanel.setLayout(new BoxLayout(propsPanel, BoxLayout.X_AXIS));

		JPanel adjPanel = new JPanel();

		adjPanel.setBackground(Color.WHITE);

		adjPanel.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		adjPanel.setLayout(new MigLayout("", "[][][][][][grow][][grow]", "[grow][][][][][][]"));

		propsPanel.add(adjPanel);

		JLabel lblBrightness = new JLabel("Brightness");

		lblBrightness.setIcon(new ImageIcon(ImageEditorDialog.class.getResource("/images/brillo.png")));

		lblBrightness.setFont(new Font("Tahoma", Font.PLAIN, 16));

		adjPanel.add(lblBrightness, "cell 0 0");

		slBrightness = new JsliderCustom();

		slBrightness.setMinimum(0);

		slBrightness.setMaximum(255);

		slBrightness.setValue(BRIGHTNESS_MULT * ImageAdjuster.DEF_OFFSET);

		slBrightness.setMinorTickSpacing(5);

		slBrightness.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {

				brillo = ((JSlider) e.getSource()).getValue() / BRIGHTNESS_MULT;

				imageAdjuster.adjustBrightness(brillo);

			}

		});

		adjPanel.add(slBrightness, "cell 1 0");

		JLabel lblHue = new JLabel("Hue");

		lblHue.setIcon(new ImageIcon(ImageEditorDialog.class.getResource("/images/hue.png")));

		lblHue.setHorizontalAlignment(SwingConstants.CENTER);

		lblHue.setFont(new Font("Tahoma", Font.PLAIN, 16));

		adjPanel.add(lblHue, "cell 2 0");

		slHue = new JsliderCustom();

		slHue.setMinimum(0);

		slHue.setMaximum(360);

		slHue.setValue(HUE_MULT * ImageAdjuster.DEF_HUE);

		slHue.setMinorTickSpacing(10);

		slHue.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {

				matiz = (float) ((JSlider) e.getSource()).getValue() / HUE_MULT;

				imageAdjuster.adjustHue(matiz);

			}

		});

		adjPanel.add(slHue, "cell 4 0");

		SimpleButton btnNewButton = new SimpleButton("Reset");

		btnNewButton.setColorOver(Color.WHITE);

		btnNewButton.setColorClick(Color.LIGHT_GRAY);

		btnNewButton.setColor(new Color(255, 255, 255));

		btnNewButton.setBorderColor(Color.WHITE);

		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 16));

		btnNewButton.setIcon(new ImageIcon(ImageEditorDialog.class.getResource("/images/actualizar.png")));

		final JLabel lblContrast = new JLabel("Contrast");

		contador.setHorizontalAlignment(SwingConstants.CENTER);

		contador.setFont(new Font("Tahoma", Font.PLAIN, 16));

		adjPanel.add(contador, "cell 6 0");

		SimpleButton lblNewLabel = new SimpleButton("Info");

		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));

		lblNewLabel.setColorOver(Color.WHITE);

		lblNewLabel.setColorClick(Color.LIGHT_GRAY);

		lblNewLabel.setBorderColor(Color.WHITE);

		lblNewLabel.setIcon(new ImageIcon(ImageEditorDialog.class.getResource("/images/info.png")));

		lblNewLabel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				if (!lista.isEmpty()) {

					if (!(prueba instanceof InfoFile)) {

						prueba = new InfoFile(lista);

					}

					prueba.verImagen(indice);

					prueba.setVisible(true);

				}

			}

		});

		SimpleButton btnNewButton_7 = new SimpleButton("New button");

		btnNewButton_7.setIcon(new ImageIcon(ImageEditorDialog.class.getResource("/images/rotate_left.png")));

		btnNewButton_7.setText("Rotate Left");

		btnNewButton_7.setBackground(Color.WHITE);

		btnNewButton_7.setColorOver(Color.WHITE);

		btnNewButton_7.setColorClick(Color.LIGHT_GRAY);

		btnNewButton_7.setBorderColor(Color.WHITE);

		btnNewButton_7.setFont(new Font("Tahoma", Font.PLAIN, 16));

		btnNewButton_7.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				try {

					rotar(false);

				}

				catch (Exception e1) {

				}

			}

		});

		JSeparator separator_7 = new JSeparator();

		separator_7.setForeground(Color.WHITE);

		adjPanel.add(separator_7, "cell 2 1");

		JSeparator separator_8 = new JSeparator();

		separator_8.setForeground(Color.WHITE);

		adjPanel.add(separator_8, "cell 4 2");

		adjPanel.add(btnNewButton_7, "cell 2 3");

		SimpleButton btnNewButton_8 = new SimpleButton("New button");

		btnNewButton_8.setIcon(new ImageIcon(ImageEditorDialog.class.getResource("/images/rotate_right.png")));

		btnNewButton_8.setText("Rotate Right");

		btnNewButton_8.setColorOver(Color.WHITE);

		btnNewButton_8.setColorClick(Color.LIGHT_GRAY);

		btnNewButton_8.setBorderColor(Color.WHITE);

		btnNewButton_8.setFont(new Font("Tahoma", Font.PLAIN, 16));

		btnNewButton_8.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				try {

					rotar(true);

				}

				catch (Exception e1) {

				}

			}

		});

		JSeparator separator_10 = new JSeparator();

		separator_10.setForeground(Color.WHITE);

		adjPanel.add(separator_10, "cell 3 3");

		adjPanel.add(btnNewButton_8, "cell 4 3");

		adjPanel.add(lblNewLabel, "cell 6 3");

		SimpleButton btnNewButton_10 = new SimpleButton("Reset Position");

		btnNewButton_10.setIcon(new ImageIcon(ImageEditorDialog.class.getResource("/images/objetive.png")));

		btnNewButton_10.setSelectedIcon(null);

		btnNewButton_10.setColorOver(Color.WHITE);

		btnNewButton_10.setColorClick(Color.LIGHT_GRAY);

		btnNewButton_10.setBorderColor(Color.WHITE);

		btnNewButton_10.setFont(new Font("Tahoma", Font.PLAIN, 16));

		btnNewButton_10.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				angulo = 90;

				rotar(false);

			}

		});

		JSeparator separator_9 = new JSeparator();

		separator_9.setForeground(Color.WHITE);

		adjPanel.add(separator_9, "cell 4 4");

		adjPanel.add(btnNewButton_10, "cell 4 5");

		adjPanel.add(btnNewButton, "cell 6 5");

		lblContrast.setIcon(new ImageIcon(ImageEditorDialog.class.getResource("/images/contrast.png")));

		lblContrast.setFont(new Font("Tahoma", Font.PLAIN, 16));

		adjPanel.add(lblContrast, "cell 0 6");

		slContrast = new JsliderCustom();

		slContrast.setMinimum(0);

		slContrast.setMaximum(20);

		slContrast.setValue(CONTRAST_MULT * ImageAdjuster.DEF_SCALE);

		slContrast.setMinorTickSpacing(1);

		slContrast.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {

				contraste = (float) ((JSlider) e.getSource()).getValue() / CONTRAST_MULT;

				imageAdjuster.adjustContrast(contraste);

			}

		});

		adjPanel.add(slContrast, "cell 1 6");

		JLabel lblSaturation = new JLabel("Saturation");

		lblSaturation.setIcon(new ImageIcon(ImageEditorDialog.class.getResource("/images/saturacion.png")));

		lblSaturation.setHorizontalAlignment(SwingConstants.CENTER);

		lblSaturation.setFont(new Font("Tahoma", Font.PLAIN, 16));

		adjPanel.add(lblSaturation, "cell 2 6");

		slSaturation = new JsliderCustom();

		slSaturation.setMaximum(200);

		slBrightness.setMinimum(0);

		slBrightness.setMaximum(400);

		slBrightness.setValue(SATURATION_MULT * ImageAdjuster.DEF_SATURATION);

		slSaturation.setMinorTickSpacing(10);

		slSaturation.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {

				saturacion = (float) ((JSlider) e.getSource()).getValue() / SATURATION_MULT;

				imageAdjuster.adjustSaturation(saturacion);

			}

		});

		adjPanel.add(slSaturation, "cell 4 6");

		SimpleButton btnNewButton_9 = new SimpleButton("Clear");

		btnNewButton_9.setFont(new Font("Tahoma", Font.PLAIN, 16));

		btnNewButton_9.setBorderColor(Color.WHITE);

		btnNewButton_9.setColorClick(Color.LIGHT_GRAY);

		btnNewButton_9.setColorOver(Color.WHITE);

		btnNewButton_9.setIcon(new ImageIcon(ImageEditorDialog.class.getResource("/images/clean.png")));

		btnNewButton_9.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				indice = -1;

				lista.clear();

				contador.setText("");

				cortar = false;

				rotar = false;

				angulo = 0;

				imageAdjuster.resetImage();

				imgPanel.setImg(null);

			}

		});

		adjPanel.add(btnNewButton_9, "cell 6 6");

		btnNewButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				resetear();

			}

		});

		JPanel cropPanel = new JPanel();

		cropPanel.setBackground(Color.WHITE);

		cropPanel.setLayout(new MigLayout("", "[][]", "[][][][][][][]"));

		cropPanel.setAlignmentY(Component.TOP_ALIGNMENT);

		cropPanel.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		propsPanel.add(cropPanel);

		SimpleButton btnCrop_1 = new SimpleButton("Crop");

		btnCrop_1.setBorderColor(Color.WHITE);

		btnCrop_1.setIcon(new ImageIcon(ImageEditorDialog.class.getResource("/images/cut.png")));

		btnCrop_1.setColorClick(Color.LIGHT_GRAY);

		btnCrop_1.setColorOver(Color.WHITE);

		btnCrop_1.setFont(new Font("Tahoma", Font.PLAIN, 16));

		btnCrop_1.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				cortar = true;

				imageAdjuster.doCrop();

			}

		});

		cropPanel.add(btnCrop_1, "cell 0 0");

		SimpleButton btnNewButton_1 = new SimpleButton("<<");

		btnNewButton_1.setColorOver(Color.WHITE);

		btnNewButton_1.setColorClick(Color.LIGHT_GRAY);

		btnNewButton_1.setBorderColor(Color.WHITE);

		btnNewButton_1.setFont(new Font("Tahoma", Font.PLAIN, 16));

		btnNewButton_1.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				atras();

			}

		});

		JSeparator separator_6 = new JSeparator();

		separator_6.setForeground(Color.WHITE);

		cropPanel.add(separator_6, "cell 0 1");

		cropPanel.add(btnNewButton_1, "flowx,cell 0 2");

		JSeparator separator_3 = new JSeparator();

		separator_3.setBackground(Color.WHITE);

		separator_3.setForeground(Color.WHITE);

		cropPanel.add(separator_3, "cell 0 3");

		JSeparator separator_2 = new JSeparator();

		separator_2.setBackground(Color.WHITE);

		separator_2.setForeground(Color.WHITE);

		cropPanel.add(separator_2, "cell 0 5");

		multiple.setFont(new Font("Tahoma", Font.PLAIN, 16));

		cropPanel.add(multiple, "cell 0 6");

		SimpleButton btnNewButton_4 = new SimpleButton("|>");

		btnNewButton_4.setColorOver(Color.WHITE);

		btnNewButton_4.setColorClick(Color.LIGHT_GRAY);

		btnNewButton_4.setColor(Color.WHITE);

		btnNewButton_4.setBackground(Color.WHITE);

		btnNewButton_4.setBorderColor(Color.WHITE);

		btnNewButton_4.setFont(new Font("Tahoma", Font.PLAIN, 16));

		btnNewButton_4.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				if (!lista.isEmpty()) {

					indice = lista.size();

					indice--;

					ponerImagen(lista.get(indice));

				}

			}

		});

		SimpleButton btnNewButton_5 = new SimpleButton("<|");

		btnNewButton_5.setColorOver(Color.WHITE);

		btnNewButton_5.setColorClick(Color.LIGHT_GRAY);

		btnNewButton_5.setBorderColor(Color.WHITE);

		btnNewButton_5.setFont(new Font("Tahoma", Font.PLAIN, 16));

		btnNewButton_5.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				if (!lista.isEmpty()) {

					indice = 0;

					ponerImagen(lista.get(indice));

				}

			}

		});

		cropPanel.add(btnNewButton_5, "flowx,cell 0 4");

		JSeparator separator_5 = new JSeparator();

		separator_5.setForeground(Color.WHITE);

		cropPanel.add(separator_5, "cell 0 4");

		cropPanel.add(btnNewButton_4, "cell 0 4");

		SimpleButton btnNewButton_2 = new SimpleButton(">>");

		btnNewButton_2.setColorOver(Color.WHITE);

		btnNewButton_2.setColorClick(Color.LIGHT_GRAY);

		btnNewButton_2.setColor(Color.WHITE);

		btnNewButton_2.setBorderColor(Color.WHITE);

		btnNewButton_2.setFont(new Font("Tahoma", Font.PLAIN, 16));

		btnNewButton_2.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				adelante();

			}

		});

		JSeparator separator_4 = new JSeparator();

		separator_4.setForeground(Color.WHITE);

		cropPanel.add(separator_4, "cell 0 2");

		cropPanel.add(btnNewButton_2, "cell 0 2");

		textField = new Spinner();

		textField.getEditor().setFont(new Font("Tahoma", Font.PLAIN, 11));

		textField.setLabelText("Go");

		textField.setMaxValor(1);

		textField.setMinValor(1);

		textField.getEditor().addMouseListener(new MouseAdapter() {

			@Override

			public void mousePressed(MouseEvent e) {

				ponerImagenSpinner();

			}

		});

		textField.getEditor().addKeyListener(new KeyAdapter() {

			@Override

			public void keyPressed(KeyEvent e) {

				ponerImagenSpinner();

			}

			@Override

			public void keyReleased(KeyEvent e) {

				try {

					textField.ponerFiltro();

				}

				catch (Exception e1) {

				}

			}

		});

		cropPanel.add(textField, "cell 0 4");

		JPanel filtersPanel = new JPanel();

		filtersPanel.setBackground(Color.WHITE);

		filtersPanel.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		propsPanel.add(filtersPanel);

		ComboBoxSuggestion<Object> cmbFilters = new ComboBoxSuggestion<Object>();

		cmbFilters.setFont(new Font("Tahoma", Font.PLAIN, 16));

		cmbFilters.addItem("Sharpen");

		cmbFilters.addItem("Clear Red Eye");

		filtersPanel.setLayout(new MigLayout("", "[][]", "[][][][][][][][][]"));

		filtersPanel.add(cmbFilters, "cell 0 0 2 1,growx,aligny center");

		SimpleButton btnApply = new SimpleButton("Apply");

		btnApply.setBorderColor(Color.WHITE);

		btnApply.setColorClick(Color.LIGHT_GRAY);

		btnApply.setColorOver(Color.WHITE);

		btnApply.setIcon(new ImageIcon(ImageEditorDialog.class.getResource("/images/check.png")));

		btnApply.setFont(new Font("Tahoma", Font.PLAIN, 16));

		btnApply.addActionListener(new ActionListener() {

			@Override

			public void actionPerformed(ActionEvent e) {

				switch ((String) cmbFilters.getSelectedItem()) {

				case "Sharpen":

					imageAdjuster.addSharpen();

					break;

				case "Clear Red Eye":

					imageAdjuster.addRemoveRedEye();

					break;

				default:

					break;

				}

			}

		});

		SimpleButton btnUndo = new SimpleButton("Undo");

		btnUndo.setColorClick(Color.LIGHT_GRAY);

		btnUndo.setBorderColor(Color.WHITE);

		btnUndo.setColorOver(Color.WHITE);

		btnUndo.setIcon(new ImageIcon(ImageEditorDialog.class.getResource("/images/undo.png")));

		btnUndo.setFont(new Font("Tahoma", Font.PLAIN, 16));

		btnUndo.addActionListener(new ActionListener() {

			@Override

			public void actionPerformed(ActionEvent e) {

				imageAdjuster.undoLastFilter();

			}

		});

		JSeparator separator_1 = new JSeparator();

		separator_1.setBackground(Color.WHITE);

		separator_1.setForeground(Color.WHITE);

		filtersPanel.add(separator_1, "cell 1 1");

		JSeparator separator = new JSeparator();

		separator.setForeground(Color.WHITE);

		separator.setBackground(Color.WHITE);

		filtersPanel.add(separator, "flowy,cell 0 2");

		filtersPanel.add(btnUndo, "cell 0 2");

		filtersPanel.add(btnApply, "cell 1 2,alignx left,aligny top");

		SimpleButton btnNewButton_3 = new SimpleButton("Open");

		btnNewButton_3.setColorOver(Color.WHITE);

		btnNewButton_3.setColorClick(Color.LIGHT_GRAY);

		btnNewButton_3.setColor(new Color(255, 255, 255));

		btnNewButton_3.setBorderColor(Color.WHITE);

		btnNewButton_3.setFont(new Font("Tahoma", Font.PLAIN, 16));

		btnNewButton_3.setIcon(new ImageIcon(ImageEditorDialog.class.getResource("/images/abrir.png")));

		btnNewButton_3.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				try {

					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

					if (!(test instanceof DemoJavaFxStage)) {

						test = new DemoJavaFxStage();

					}

					if (folder.isSelected()) {

						carpeta = true;

					}

					else {

						carpeta = false;

					}

					comprobacion = test.showOpenFileDialog(carpeta, "images");

					if (!comprobacion.isEmpty()) {

						if (carpeta) {

							for (int i = 0; i < comprobacion.size(); i++) {

								comprobacionCarpeta = Metodos.directorio(
										comprobacion.get(i).getAbsolutePath().toString() + Metodos.saberSeparador(),
										"allimages", false);

								for (int x = 0; x < comprobacionCarpeta.size(); x++) {

									lista.add(comprobacionCarpeta.get(x));

								}

							}

						}

						else {

							for (int i = 0; i < comprobacion.size(); i++) {

								lista.add(comprobacion.get(i).getAbsolutePath().toString());

							}

						}

						indice = 0;

						ponerImagen(lista.get(0));

						textField.setMaxValor(lista.size());

					}

				}

				catch (Exception e1) {

				}

			}

		});

		filtersPanel.add(btnNewButton_3, "cell 0 4");

		SimpleButton btnNewButton_6 = new SimpleButton("Save");

		btnNewButton_6.setIcon(new ImageIcon(ImageEditorDialog.class.getResource("/images/save.png")));

		btnNewButton_6.setFont(new Font("Tahoma", Font.PLAIN, 16));

		btnNewButton_6.setColorOver(Color.WHITE);

		btnNewButton_6.setColorClick(Color.LIGHT_GRAY);

		btnNewButton_6.setBorderColor(Color.WHITE);

		btnNewButton_6.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				try {

					guardarImagen(multiple.isSelected());

				}

				catch (Exception e1) {

				}

			}

		});

		filtersPanel.add(btnNewButton_6, "cell 1 4");

		folder.setText("Select folder");

		folder.setHorizontalAlignment(SwingConstants.CENTER);

		folder.setFont(new Font("Tahoma", Font.PLAIN, 16));

		folder.setBackground(Color.BLUE);

		filtersPanel.add(folder, "cell 0 7");

		resetear();

		setLocationRelativeTo(null);

		try {

			new UtilDragAndDrop(imgPanel, null, true, new UtilDragAndDrop.Listener() {

				public void filesDropped(java.io.File[] archivos) {

					for (File f : archivos) {

						lista.add(f.getAbsolutePath().toString());

					}

					if (indice < 0) {

						indice = 0;

						ponerImagen(lista.get(0));

					}

					textField.setMaxValor(lista.size());

					textField.setValor(
							Integer.parseInt(contador.getText().substring(0, contador.getText().indexOf("/")).trim()

							));

					contador.setText(
							contador.getText().substring(0, contador.getText().indexOf("/") + 2) + lista.size());

				}

			});

		}

		catch (Exception e1) {

			e1.printStackTrace();

		}

	}

	public static void main(String[] args) {

		new ImageEditorDialog().setVisible(true);

	}

}
