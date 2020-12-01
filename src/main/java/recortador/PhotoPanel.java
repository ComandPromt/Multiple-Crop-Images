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
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

public class PhotoPanel extends JPanel implements MouseMotionListener, MouseListener {

	private static final long serialVersionUID = 1L;

	transient Image photo;

	transient BufferedImage bufferedImage;

	static LinkedList<String> listaImagenes = new LinkedList<>();

	int count = 0;

	private Color color1 = new Color(255, 255, 255);

	private Color color2 = new Color(0, 0, 0);

	transient BufferedImage tmpRecorte;

	private float clipX = 0;
	private float clipY = 0;
	private float clipWidth;
	private float clipHeight;
	public static int paso = 1;

	public static boolean penultimaFoto = false;
	private int x1 = 0;
	private int y1 = 0;
	private int dx1x2 = 0;
	private int dy1y2 = 0;
	private int x2 = 0;
	private int y2 = 0;
	public static int entrada;
	boolean band = true;

	private JPopupMenu popupMenu = new JPopupMenu();

	public PhotoPanel() {

		super();

		PhotoPanel.this.setSize(new Dimension(0, 0));

		PhotoPanel.this.setPreferredSize(new Dimension(0, 0));

		PhotoPanel.this.addMouseMotionListener(PhotoPanel.this);

		PhotoPanel.this.addMouseListener(PhotoPanel.this);

		JMenuItem menuItem = new JMenuItem("Recortar");

		menuItem.addActionListener((ActionEvent e) -> {

			PhotoFrame.recortar();

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

	public void guardar() {

		try {

			saveImage();

		}

		catch (RasterFormatException e1) {

			Metodos.mensaje("La zona seleccionada es más grande que una o más imágenes", 1);

			Metodos.mensaje("Se han recortado las fotos anteriores a la foto que se muestra", 3);

		}

		catch (Exception e1) {

			File directorio = new File(PhotoFrame.carpetaRecortes);

			directorio.mkdir();

		}

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

	private void saveImage() throws Exception {

		try {

			entrada = paso;

			listaImagenes.clear();

			listaImagenes = Metodos.directorio(PhotoFrame.directorio + Main.getSeparador(), ".", true, false);

			listaImagenes.sort(String::compareToIgnoreCase);

			if (!listaImagenes.isEmpty()) {

				int vueltas = 1;

				count = 1;

				int numeroImagen = Metodos.listarFicherosPorCarpeta(new File(PhotoFrame.carpetaRecortes));

				if (numeroImagen > 0) {

					count += numeroImagen;
				}

				if (PhotoFrame.rdbtnmntmNewRadioItem_2.isSelected()) {

					vueltas = listaImagenes.size();
				}

				else {

					String imagenSelecionada;

					try {
						imagenSelecionada = saberImagenSeleccionada();
					}

					catch (NullPointerException e) {
						imagenSelecionada = listaImagenes.get(0);
					}

					listaImagenes.set(0, imagenSelecionada);

				}

				String extension;

				String numero = "";

				int y = Metodos.directorio(PhotoFrame.carpetaRecortes + Main.getSeparador(), ".", true, false).size()
						+ 1;

				int inicio = 0;

				inicio = paso;

				vueltas = paso;

				++vueltas;

				if (PhotoFrame.rdbtnmntmNewRadioItem_2.isSelected()) {

					vueltas = listaImagenes.size();

				}

				for (int x = inicio; x < vueltas; x++) {

					extension = listaImagenes.get(x).substring(listaImagenes.get(x).length() - 3,
							listaImagenes.get(x).length());

					if (extension.equals("peg")) {
						extension = "jpg";
					}

					photo = ImageIO.read(new File(PhotoFrame.directorio + Main.getSeparador() + listaImagenes.get(x)));

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

					ImageIO.write(tmpRecorte, extension, new File(
							PhotoFrame.carpetaRecortes + Main.getSeparador() + "Image_" + numero + "." + extension));
				}

				if (PhotoFrame.rdbtnmntmNormal.isSelected()) {

					int ultimaFoto = listaImagenes.size();

					--ultimaFoto;

					if (paso == entrada && ++entrada == ultimaFoto) {
						--paso;
						penultimaFoto = true;
					} else {
						penultimaFoto = false;
					}

					++paso;

					if (paso == listaImagenes.size()) {
						--paso;
					}

					if (penultimaFoto && PhotoFrame.recortar) {

						paso = listaImagenes.size() - 3;
					}

					if (PhotoFrame.reemplazar.isSelected() && (paso < entrada)) {

						paso = --entrada;

					}

					if (PhotoFrame.recortar) {

						--paso;

						if (!PhotoFrame.reemplazar.isSelected()) {

							++paso;

						}
					}

					PhotoFrame.verFoto(paso);
				}

				Metodos.renombrarArchivos(PhotoFrame.carpetaRecortes + Main.getSeparador(), ".", true);

				--y;

				PhotoFrame.photoPanel.setBackground(Color.WHITE);

				PhotoFrame.photoPanel.setForeground(Color.WHITE);

				PhotoFrame.getjPanel1().add(PhotoFrame.photoPanel);

				if (PhotoFrame.rdbtnmntmNewRadioItem_2.isSelected() && y > 0) {

					paso = 0;

					int numerOpcion = 0;

					listaImagenes.clear();

					listaImagenes = Metodos.directorio(PhotoFrame.carpetaRecortes + Main.getSeparador(), ".", true,
							true);

					String[] options = { "<html><h2>[1] Subir al CMS</h2></html>",
							"<html><h2>[2] Mover a la carpeta imagenes</h2></html>",
							"<html><h2>[3] Hacer GIF</h2></html>",
							"<html><h2>[4] Abrir imagenes para recortar</h2></html>",
							"<html><h2>[5] Abrir carpeta de salida</h2></html>",
							"<html><h2>[6] Borrar imagenes para recortar</h2></html>" };

					ImageIcon icon = new ImageIcon(Main.class.getResource("/imagenes/utilities.png"));

					String opcion = (String) JOptionPane.showInputDialog(null, "", "Seleccione una opcion",
							JOptionPane.QUESTION_MESSAGE, icon, options, options[0]);

					opcion = opcion.replace("<html><h2>[", "");

					numerOpcion = Integer.parseInt(opcion.substring(0, 1));

					switch (numerOpcion) {

					case 1:
						Metodos.abrirCarpeta(PhotoFrame.directorio);
						break;

					case 2:
						Metodos.abrirCarpeta(PhotoFrame.carpetaRecortes);
						break;

					case 3:
						Metodos.eliminarArchivos(PhotoFrame.directorio + Main.getSeparador());
						break;

					}

				}

			}
		}

		catch (Exception e) {

		}

	}

	static String saberImagenSeleccionada() {

		String imagenSelecionada;

		try {

			imagenSelecionada = PhotoFrame.fileChooser.getSelectedFile().toString();

			imagenSelecionada = imagenSelecionada.substring(imagenSelecionada.lastIndexOf(Main.getSeparador()) + 1,
					imagenSelecionada.length());
		}

		catch (NullPointerException e) {

			try {

				imagenSelecionada = PhotoFrame.listaImagenes.get(paso);
			}

			catch (Exception e1) {

				try {

					imagenSelecionada = PhotoFrame.listaImagenes.get(0);
				}

				catch (Exception e2) {

					imagenSelecionada = "";
				}

			}

		}

		return imagenSelecionada;
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

	}

	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mouseMoved(MouseEvent arg0) {

	}

}