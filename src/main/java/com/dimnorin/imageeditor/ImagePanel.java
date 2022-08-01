package com.dimnorin.imageeditor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;

import com.dimnorin.imageeditor.filter.AbstractBufferedImageOp;

/**
 * 
 * Panel to draw image
 *
 */
public class ImagePanel extends JPanel {
	/**
	 * Working image sample
	 */
	private Image img;
	/**
	 * Bounds of original image
	 */
	private Dimension imgSize;
	/**
	 * Internal images to apply filters and adjustments
	 */
	private BufferedImage biSrc;
	private BufferedImage biDst;
	/**
	 * Rectangles used to draw selection
	 */
	Graphics2D g2;
	private Rectangle curRect = null;
	private Rectangle rectDraw = null;
	private Rectangle prevRectDrawn = new Rectangle();
	int equis;
	int ye;
	public int widthDibujo;

	public int heightDibujo;

	public int wRrectangle;

	public int hRrectangle;

	public ImagePanel() {
		try {
			addMouseListener(new MListener());

			addMouseMotionListener(new MListener());
		} catch (Exception e) {

		}
	}

	/**
	 * Set a new image to panel
	 * 
	 * @param image
	 */
	public void setImg(Image image) {
		try {
			Graphics2D big = null;
			if (image != null) {

				biSrc = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);

				big = biSrc.createGraphics();
				big.drawImage(image, 0, 0, null);

				biDst = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);

				img = biSrc;
				this.imgSize = new Dimension(img.getWidth(this), img.getHeight(this));

			}

			else {

				img = null;

				imgSize = null;

				biSrc = null;

				biDst = null;

				g2 = null;

				curRect = null;

				rectDraw = null;

				prevRectDrawn = new Rectangle();

			}

			this.repaint();
		}

		catch (Exception e) {

		}

	}

	@Override
	public void paintComponent(Graphics g) {

		super.paintComponent(g);

		try {

			Dimension boundary = getSize();

			Dimension scaled = getScaledDimension(imgSize, boundary);

			BufferedImage resizedImage = new BufferedImage(scaled.width, scaled.height, BufferedImage.TYPE_INT_RGB);

			g2 = resizedImage.createGraphics();

			g2.drawImage(img, 0, 0, scaled.width, scaled.height, this);

			g2.dispose();

			g.drawImage(resizedImage, (boundary.width - scaled.width) / 2, (boundary.height - scaled.height) / 2, this);

			if (curRect != null) {

				float thickness = 4;

				g2.setStroke(new BasicStroke(thickness));

				g.setColor(Color.RED);

				g.drawRect(rectDraw.x, rectDraw.y, rectDraw.width - 1, rectDraw.height - 1);

			}

		}

		catch (Exception e) {

		}

	}

	/**
	 * Apply filters to image
	 * 
	 * @param filters
	 */
	public void applyFilter(List<AbstractBufferedImageOp> filters) {
		try {
			BufferedImage bi = null;
			for (AbstractBufferedImageOp filter : filters) {
				bi = filter.filter(bi != null ? bi : biSrc, biDst);
				// bi = biDst;
			}
			if (bi != null)
				img = bi;
		} catch (Exception e) {
		}
	}

	public void doCrop() {

		try {

			if (curRect != null) {

				// Do calculation to convert coordinates from panel to original image

				Dimension boundary = getSize();

				Dimension scaled = getScaledDimension(imgSize, boundary);

				int dx = (boundary.width - scaled.width) / 2;

				int dy = (boundary.height - scaled.height) / 2;

				float k = (float) scaled.width / imgSize.width;

				int x = (int) ((rectDraw.x - dx) / k);

				int y = (int) ((rectDraw.y - dy) / k);

				int w = (int) ((rectDraw.width - 1) / k);

				int h = (int) ((rectDraw.height - 1) / k);

				BufferedImage bi = biSrc.getSubimage(x, y, w, h);

				setImg(bi);

				curRect = null;

			}

		}

		catch (Exception e) {

		}

	}

	/**
	 * Get current image
	 * 
	 * @return
	 */
	public Image getImg() {
		return img;
	}

	/**
	 * Scale image to fit panel bounds and calculate it dimensions
	 * 
	 * @param imageSize
	 * @param boundary
	 * @return
	 */
	private Dimension getScaledDimension(Dimension imageSize, Dimension boundary) {

		double widthRatio = boundary.getWidth() / imageSize.getWidth();

		double heightRatio = boundary.getHeight() / imageSize.getHeight();

		double ratio = Math.min(widthRatio, heightRatio);

		return new Dimension((int) (imageSize.width * ratio), (int) (imageSize.height * ratio));

	}

	/**
	 * Update selection rectangle
	 * 
	 * @param compW
	 * @param compH
	 */
	private void updateDrawableRect(int compW, int compH) {
		int x = curRect.x;
		int y = curRect.y;
		int w = curRect.width;
		int h = curRect.height;

		if (w < 0) {
			w = 0 - w;
			x = x - w + 1;

			if (x < 0) {
				w += x;
				x = 0;
			}
		}

		if (h < 0) {
			h = 0 - h;
			y = y - h + 1;
			if (y < 0) {
				h += y;
				y = 0;
			}
		}

		if ((x + w) > compW) {
			w = compW - x;
		}

		if ((y + h) > compH) {
			h = compH - y;
		}

		if (rectDraw != null) {
			prevRectDrawn.setBounds(rectDraw.x, rectDraw.y, rectDraw.width, rectDraw.height);
			rectDraw.setBounds(x, y, w, h);
		} else {
			rectDraw = new Rectangle(x, y, w, h);
		}
	}

	/**
	 * Listen to mouse events to draw selection rectangle
	 * 
	 */

	public void pintar() {

		curRect = new Rectangle(equis, ye, 0, 0);

		curRect.setSize(wRrectangle, hRrectangle);

		updateDrawableRect(widthDibujo, heightDibujo);

		Rectangle totalRepaint = rectDraw.union(prevRectDrawn);

		repaint(totalRepaint.x, totalRepaint.y, totalRepaint.width, totalRepaint.height);

	}

	private class MListener extends MouseInputAdapter {

		@Override

		public void mousePressed(MouseEvent event) {

			int x = event.getX();

			int y = event.getY();

			equis = x;

			ye = y;

			curRect = new Rectangle(x, y, 0, 0);

			updateDrawableRect(getWidth(), getHeight());

			repaint();

		}

		@Override
		public void mouseDragged(MouseEvent e) {

			updateSize(e);

		}

		@Override
		public void mouseReleased(MouseEvent e) {

			updateSize(e);

		}

		void updateSize(MouseEvent e) {

			int x = e.getX();

			int y = e.getY();

			widthDibujo = getWidth();

			heightDibujo = getHeight();

			wRrectangle = x - curRect.x;

			hRrectangle = y - curRect.y;

			curRect.setSize(x - curRect.x, y - curRect.y);

			updateDrawableRect(getWidth(), getHeight());

			Rectangle totalRepaint = rectDraw.union(prevRectDrawn);

			repaint(totalRepaint.x, totalRepaint.y, totalRepaint.width, totalRepaint.height);

		}

	}

}
