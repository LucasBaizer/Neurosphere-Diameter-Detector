package org.jointheleague.ir;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_imgcodecs;

public class ImageComponent extends JComponent {
	private static final long serialVersionUID = -2546741406493029667L;

	public static final int DEFAULT_WIDTH = 550;
	public static final int DEFAULT_HEIGHT = 450;

	private Subject<ImageEvent> imageSelected = new Subject<ImageEvent>();
	private Subject<Double> scaleChanged = new Subject<Double>();
	private BufferedImage image;
	private BufferedImage renderImage;
	private double scale;
	private File file;
	private boolean input;
	private String backgroundText = null;
	private Dimension preferredSize;

	private MouseAdapter inputListener = new MouseAdapter() {
		public void mouseMoved(MouseEvent e) {
			setCursor(new Cursor(Cursor.HAND_CURSOR));
		};

		public void mouseExited(MouseEvent e) {
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		};

		@Override
		public void mouseReleased(MouseEvent e) {
			if (e.getButton() != MouseEvent.BUTTON1) {
				return;
			}

			selectFile();
		}
	};

	public ImageComponent(File imageFile) {
		this(imageFile, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

	public ImageComponent(File imageFile, int width, int height) {
		this.preferredSize = new Dimension(width, height);

		if (imageFile != null) {
			setImage(imageFile);
		}

		this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
	}

	public void selectFile() {
		if (!input) {
			return;
		}

		JFileChooser chooser = new JFileChooser(Cache.get("ImageDirectory"));

		chooser.setFileFilter(new FileFilter() {
			@Override
			public String getDescription() {
				return "Image Files (*.png, *.jpg, *.jpeg, *.gif, *.bmp)";
			}

			@Override
			public boolean accept(File f) {
				String p = f.getPath().toLowerCase();
				return f.isDirectory() || p.endsWith(".png") || p.endsWith(".jpg") || p.endsWith(".jpeg")
						|| p.endsWith(".bmp") || p.endsWith(".gif");
			}
		});

		if (chooser.showOpenDialog(Main.FRAME) == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			try {
				image = ImageIO.read(file);
			} catch (IOException ex) {
				Program.exit(ex);
				return;
			}
			if (image == null) {
				return;
			}

			resize(image);
			renderImage = ImageUtility.scale(image, getPreferredSize().width, getPreferredSize().height);

			ImageComponent.this.file = file;
			refresh();

			Cache.save("ImageDirectory", file.getParentFile().getAbsolutePath());

			imageSelected.markChanged();
			imageSelected.notifyObservers(new ImageEvent(file, image));
		}
	}

	public boolean isGreyscale() {
		if (image == null) {
			return false;
		}

		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				Color color = new Color(image.getRGB(x, y));
				if (!(color.getRed() == color.getGreen() && color.getRed() == color.getBlue())) {
					return false;
				}
			}
		}

		return true;
	}

	private void resize(BufferedImage image) {
		if (!input) {
			return;
		}

		double ratio = getPreferredSize().width / (double) image.getWidth();
		this.scale = ratio;

		double imageHeight = image.getHeight() * ratio;

		this.preferredSize = new Dimension(getPreferredSize().width, (int) imageHeight);

		scaleChanged.markChanged();
		scaleChanged.notifyObservers(this.scale);
	}

	@Override
	public Dimension getPreferredSize() {
		return preferredSize;
	}

	@Override
	public Dimension getSize() {
		return preferredSize;
	}

	@Override
	public Dimension getSize(Dimension rv) {
		rv.width = preferredSize.width;
		rv.height = preferredSize.height;
		return rv;
	}

	@Override
	public Dimension getMinimumSize() {
		return preferredSize;
	}

	@Override
	public Dimension getMaximumSize() {
		return preferredSize;
	}

	public void refresh() {
		revalidate();
		repaint();
	}

	public ImageComponent setImage(File imageFile) {
		this.file = imageFile;

		return setImage(opencv_imgcodecs.cvLoadImage(imageFile.getPath()));
	}

	public ImageComponent setImage(IplImage image) {
		if (image == null) {
			this.image = null;
			return this;
		}
		this.image = ImageUtility.toBufferedImage(image);
		resize(this.image);
		this.renderImage = ImageUtility.scale(this.image, getPreferredSize().width, getPreferredSize().height);
		return this;
	}

	public ImageComponent setInput(boolean input) {
		this.input = input;

		if (input) {
			this.backgroundText = "[drag and drop or click to choose an image]";
			this.addMouseListener(inputListener);
			this.addMouseMotionListener(inputListener);
			this.setDropTarget(new DropTarget(this, new DropTargetListener() {
				public void dropActionChanged(DropTargetDragEvent e) {
				}

				public void drop(DropTargetDropEvent e) {
					e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);

					try {
						@SuppressWarnings("unchecked")
						List<File> files = (List<File>) e.getTransferable()
								.getTransferData(DataFlavor.javaFileListFlavor);
						File file = files.get(0);

						ImageComponent.this.image = ImageIO.read(file);
						if (image == null) {
							return;
						}
						resize(image);
						refresh();
					} catch (Exception ex) {
						Program.exit(ex);
					}
				}

				public void dragOver(DropTargetDragEvent e) {
					if ((e.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) == DnDConstants.ACTION_COPY_OR_MOVE) {
						e.rejectDrag();
					}
				}

				public void dragExit(DropTargetEvent e) {
				}

				public void dragEnter(DropTargetDragEvent e) {
				}
			}));
		} else {
			this.backgroundText = null;
			this.removeMouseListener(inputListener);
			this.removeMouseMotionListener(inputListener);
			this.setDropTarget(null);
		}
		return this;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (image == null) {
			g.setColor(getBackground());
			g.fillRect(0, 0, getWidth(), getHeight());

			if (backgroundText != null) {
				g.setColor(Color.BLACK);
				g.setFont(new Font("Arial", Font.BOLD, getWidth() / 25));

				FontMetrics metrics = getFontMetrics(g.getFont());

				g.drawString(backgroundText, (getWidth() - metrics.stringWidth(backgroundText)) / 2,
						((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent());
			}
		} else {
			g.drawImage(renderImage, 0, 0, this);
		}
	}

	public Subject<ImageEvent> onImageSelected() {
		return imageSelected;
	}

	public Subject<Double> onScaleChanged() {
		return scaleChanged;
	}

	public BufferedImage getImage() {
		return image;
	}

	public BufferedImage getRenderImage() {
		return renderImage;
	}

	public File getSelectedFile() {
		return file;
	}

	public String getBackgroundText() {
		return backgroundText;
	}

	public void setBackgroundText(String backgroundText) {
		this.backgroundText = backgroundText;
	}

	public double getScale() {
		return scale;
	}

	public void setForcedSize(Dimension ps) {
		this.preferredSize = ps;
	}
}
