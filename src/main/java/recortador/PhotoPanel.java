package recortador;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

@SuppressWarnings("serial")
public class PhotoPanel extends JPanel implements MouseMotionListener, MouseListener {

	transient Image photo;
	transient BufferedImage bufferedImage;

	int count = 0;

	private Color color1 = new Color(255, 255, 255);
	private Color color2 = new Color(0, 0, 0);
	int salto = 0;
	transient BufferedImage tmpRecorte;

	private float clipX = 0;
	private float clipY = 0;
	private float clipWidth;
	private float clipHeight;

	private int x1 = 0;
	private int y1 = 0;
	private int dx1x2 = 0;
	private int dy1y2 = 0;
	private int x2 = 0;
	private int y2 = 0;

	boolean band = true;

	private JPopupMenu popupMenu = new JPopupMenu();

	public PhotoPanel() {

		super();

		setSize(new Dimension(0, 0));

		setPreferredSize(new Dimension(0, 0));

		addMouseMotionListener(PhotoPanel.this);

		addMouseListener(PhotoPanel.this);

		JMenuItem menuItem = new JMenuItem("Save image");

		menuItem.addActionListener((ActionEvent e) -> {

			try {

				saveImage();
			}

			catch (RasterFormatException e1) {

				Metodos.mensaje("La zona seleccionada es más grande que una o más imágenes", 1);

				Metodos.mensaje("Se han recortado las fotos anteriores a la foto que se muestra", 3);

			}

			catch (Exception e1) {

				File directorio = new File(Main.getDirectorioActual() + "Config" + Main.getSeparador()
						+ "imagenes_para_recortar" + Main.getSeparador() + "recortes");

				directorio.mkdir();

			}

		});

		popupMenu.add(menuItem);

		Timer timer = new Timer();
		TimerTask timerColor = new TimerTask() {
			@Override
			public void run() {
				color1 = (band) ? new Color(255, 255, 255) : new Color(0, 0, 0);
				color2 = (band) ? new Color(0, 0, 0) : new Color(255, 255, 255);
				band = !band;
				repaint();
			}
		};
		timer.schedule(timerColor, 1000, 350);
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(new Color(255, 255, 255));
		g2.fill(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));

		if (photo != null) {

			bufferedImage = new BufferedImage(getWidth(), getHeight(), java.awt.image.BufferedImage.TYPE_INT_RGB);
			Graphics2D g2D = bufferedImage.createGraphics();
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			g2D.drawImage(photo, 0, 0, photo.getWidth(this), photo.getHeight(this), this);

			g2D.setStroke(new BasicStroke(1f));
			g2D.setColor(color1);
			drawRect(g2D);

			float[] dash1 = { 5.0f };
			g2D.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 5.0f, dash1, 0.0f));
			g2D.setColor(color2);
			drawRect(g2D);

			g2.drawImage(bufferedImage, 0, 0, this);
		}
	}

	private void drawRect(Graphics2D g2D) {
		g2D.drawLine(x1, y1, x1 + dx1x2, y1);
		g2D.drawLine(x1, y1, x1, y1 + dy1y2);
		g2D.drawLine(x1 + dx1x2, y1, x1 + dx1x2, y1 + dy1y2);
		g2D.drawLine(x1, y1 + dy1y2, x1 + dx1x2, y1 + dy1y2);
	}

	private void showPopup(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3 && e.isPopupTrigger()) {

			popupMenu.show(e.getComponent(), e.getX(), e.getY());

		}
	}

	public void setPhoto(Image photo) {
		if (photo != null) {
			this.photo = photo;
			this.setSize(new Dimension(photo.getWidth(null), photo.getHeight(null)));
			this.setPreferredSize(new Dimension(photo.getWidth(null), photo.getHeight(null)));
			// reinicia valores
			x1 = 0;
			y1 = 0;
			x2 = 0;
			y2 = 0;
			dx1x2 = 0;
			dy1y2 = 0;
			clipX = 0;
			clipY = 0;
			clipWidth = 0;
			clipHeight = 0;
			count = 0;
			repaint();
		}
	}

	@SuppressWarnings("all")

	private void saveImage() throws Exception {
		++salto;
		System.out.println("salto: " + salto);
		if (salto == 2) {
			System.out.println("parate");
		}
		boolean terminado = false;

		String directorioActual = Main.getDirectorioActual();

		LinkedList<String> listaImagenes = new LinkedList<>();

		listaImagenes = Metodos.directorio(
				directorioActual + "Config" + Main.getSeparador() + "imagenes_para_recortar" + Main.getSeparador(), ".",
				1);

		listaImagenes.sort(String::compareToIgnoreCase);

		System.out.println("entro a recortar");

		int vueltas = 1;

		listaImagenes = Metodos.directorio(
				directorioActual + "Config" + Main.getSeparador() + "imagenes_para_recortar" + Main.getSeparador(), ".",
				1);

		count = 1;

		int numeroImagen = Metodos.listarFicherosPorCarpeta(new File(directorioActual + "Config" + Main.getSeparador()
				+ "imagenes_para_recortar" + Main.getSeparador() + "recortes"));

		if (numeroImagen > 0) {

			count += numeroImagen;
		}

		if (PhotoFrame.rdbtnmntmNewRadioItem_2.isSelected()) {

			vueltas = listaImagenes.size();
		}

		else {

			String imagenSelecionada = PhotoFrame.fileChooser.getSelectedFile().toString();

			imagenSelecionada = imagenSelecionada.substring(imagenSelecionada.lastIndexOf(Main.getSeparador()) + 1,
					imagenSelecionada.length());

			listaImagenes.set(0, imagenSelecionada);
		}

		String extension;

		String numero = "";

		int y = Metodos.directorio(directorioActual + "Config" + Main.getSeparador() + "imagenes_para_recortar"
				+ Main.getSeparador() + "recortes" + Main.getSeparador(), ".", 1).size() + 1;

		for (int x = 0; x < vueltas; x++) {

			extension = listaImagenes.get(x).substring(listaImagenes.get(x).length() - 3,
					listaImagenes.get(x).length());

			if (extension.equals("peg")) {
				extension = "jpg";
			}

			photo = ImageIO.read(new File(directorioActual + "Config" + Main.getSeparador() + "imagenes_para_recortar"
					+ Main.getSeparador() + listaImagenes.get(x)));

			tmpRecorte = ((BufferedImage) photo).getSubimage((int) clipX, (int) clipY, (int) clipWidth,
					(int) clipHeight);

			if (y < 10) {
				numero = "000" + y;
			}

			else {

				if (y >= 10 && y < 100) {
					numero = "00" + y;
				} else {

					if (y >= 100 && y < 1000) {
						numero = "0" + y;
					} else {
						numero = "" + y;
					}
				}
			}

			y++;

			ImageIO.write(tmpRecorte, extension,
					new File(directorioActual + "Config" + Main.getSeparador() + "imagenes_para_recortar"
							+ Main.getSeparador() + "recortes" + Main.getSeparador() + "Image_" + numero + "."
							+ extension));
		}

		if (PhotoFrame.rdbtnmntmNewRadioItem_1.isSelected()) {

			if (JOptionPane.showConfirmDialog(null, "¿Quieres recortar otra imagen?", "Recorte normal",
					JOptionPane.YES_NO_OPTION) == 0) {
				photo = null;
				PhotoFrame.jMenuItem1ActionPerformed();
			}

			else {

				terminado = true;
				PhotoFrame.photoPanel.photo = null;
			}
		}

		else {
			terminado = true;
			PhotoFrame.photoPanel.photo = null;
		}

		int n = 2;

		if (terminado) {

			int respuesta = JOptionPane.showConfirmDialog(null, "¿Quieres borrar las imágenes para recortar?",
					"Borrar archivos", JOptionPane.YES_NO_OPTION);

			if (respuesta == 0) {

				Metodos.eliminarArchivos(directorioActual + "Config" + Main.getSeparador() + "imagenes_para_recortar"
						+ Main.getSeparador());
			}

			--y;

			if ((PhotoFrame.rdbtnmntmNewRadioItem_2.isSelected() || PhotoFrame.rdbtnmntmNewRadioItem_1.isSelected())
					&& y > 1 && y <= 170) {

				n = JOptionPane.showConfirmDialog(null, "¿Quieres crear un gif con las imágenes recortadas?",
						"Crear GIF", JOptionPane.YES_NO_OPTION);

				if (n == 0) {

					listaImagenes = Metodos.directorio(directorioActual + "Config" + Main.getSeparador()
							+ "imagenes_para_recortar" + Main.getSeparador() + "recortes" + Main.getSeparador(), ".",
							1);

					for (int x = 0; x < listaImagenes.size(); x++) {

						Files.move(

								FileSystems.getDefault()
										.getPath(directorioActual + "Config" + Main.getSeparador()
												+ "imagenes_para_recortar" + Main.getSeparador() + "recortes"
												+ Main.getSeparador() + listaImagenes.get(x)),

								FileSystems.getDefault()
										.getPath(Main.getLectura()[0] + Main.getSeparador() + "Hacer_gif"
												+ Main.getSeparador() + "img" + Main.getSeparador()
												+ listaImagenes.get(x)),

								StandardCopyOption.REPLACE_EXISTING);
					}

					try {

						int recuento = Metodos
								.listarFicherosPorCarpeta(new File(Main.getLectura()[0] + "/Hacer_gif/img"), ".");

						if (recuento <= 170) {

							if (Main.getOs().equals("Linux")) {

								Metodos.cambiarPermisos();

							}

							Main.hacerGIF();

						}

						else {
							Main.mensaje170();
						}

					}

					catch (Exception e1) {

						try {

							new Config().setVisible(true);
						}

						catch (IOException e2) {
							//
						}

					}

				}

				else {

					Object seleccion = JOptionPane.showInputDialog(null,
							"Seleccione opcion" + "[1] Mover para subir al CMS" + "[2] Mover para hacer GIF"
									+ "[3] Comprobar SHA" + "",
							"Selector de opciones", JOptionPane.QUESTION_MESSAGE, null, // null para icono defecto
							new Object[] { "", "opcion 2", "opcion 3" }, "opcion 1");

					System.out.println("El usuario ha elegido " + seleccion);
				}

				if (n > 0) {

					Metodos.abrirCarpeta(directorioActual + "Config" + Main.getSeparador() + "imagenes_para_recortar"
							+ Main.getSeparador() + "recortes");
				}

			}

			else {

				PhotoFrame.photoPanel.setBackground(Color.WHITE);
				PhotoFrame.photoPanel.setForeground(Color.WHITE);
				PhotoFrame.getjPanel1().add(PhotoFrame.photoPanel);
			}
		}

	}

	@Override

	public void mouseDragged(MouseEvent e) {
		x2 = (int) e.getPoint().getX();
		y2 = (int) e.getPoint().getY();

		if (x2 < 0)
			x2 = 0;
		if (y2 < 0)
			y2 = 0;

		dx1x2 = x2 - x1;
		dy1y2 = y2 - y1;

		if (x1 + dx1x2 > getWidth())
			dx1x2 = getWidth() - x1 - 1;
		if (y1 + dy1y2 > getHeight())
			dy1y2 = getHeight() - y1 - 1;

		repaint();
	}

	public void mouseMoved(MouseEvent e) {
//
	}

	public void mouseClicked(MouseEvent e) {
		showPopup(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			x1 = (int) e.getPoint().getX();
			y1 = (int) e.getPoint().getY();
		}
		showPopup(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {

		clipX = x1;
		clipY = y1;
		clipWidth = Math.abs(dx1x2);
		clipHeight = Math.abs(dy1y2);

		if (dx1x2 < 0 && dy1y2 < 0) {
			clipX = clipX - Math.abs(dx1x2);
			clipY = clipY - Math.abs(dy1y2);
		} else if (dy1y2 < 0) {
			clipY = clipY - Math.abs(dy1y2);
		} else if (dx1x2 < 0) {
			clipX = clipX - Math.abs(dx1x2);
		}
		showPopup(e);
	}

	public void mouseEntered(MouseEvent e) {
//
	}

	public void mouseExited(MouseEvent e) {
//
	}

}