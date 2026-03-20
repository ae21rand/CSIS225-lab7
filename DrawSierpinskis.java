
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * A program to draw Sierpinski gaskets on the screen.
 * 
 * @author Prof. White
 * @version Spring 2026
 */
public class DrawSierpinskis extends MouseAdapter implements Runnable {

	/** List of Sierpinski objects currently on the screen. */
	private java.util.List<Sierpinski> list;

	/** Number of points selected so far. */
	private int soFar;

	/** The first two points in the drawing. */
	private Point point1, point2;

	/** The last mouse position for "rubber banding". */
	private Point lastMouse;

	/** true if only drawing triangles. */
	private boolean trianglesOnly;

	/** The JPanel on which we are drawing. */
	private JPanel panel;

	/**
	 * Constructor that includes option to draw only triangles.
	 * 
	 * @param trianglesOnly true if only drawing triangles, false if drawing
	 *                      Sierpinski gaskets.
	 */
	public DrawSierpinskis(boolean trianglesOnly) {

		this.trianglesOnly = trianglesOnly;
	}

	/**
	 * The run method to set up the graphical user interface.
	 */
	@Override
	public void run() {

		// set up the GUI "look and feel" which should match
		// the OS on which we are running
		JFrame.setDefaultLookAndFeelDecorated(true);

		// create a JFrame in which we will build our very
		// tiny GUI, and give the window a name
		JFrame frame = new JFrame("DrawSierpinskis");
		frame.setPreferredSize(new Dimension(800, 800));

		// tell the JFrame that when someone closes the
		// window, the application should terminate
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// JPanel with a paintComponent method
		panel = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {

				// first, we should call the paintComponent method we are
				// overriding in JPanel
				super.paintComponent(g);

				// rubber banding, if drawing a new gasket is in progress
				if (soFar >= 1) {
					// line from point1 to lastMouse
					g.drawLine(point1.x, point1.y,
							lastMouse.x, lastMouse.y);
					// we have 2, 2 more lines to show our triangle in
					// progress
					if (soFar == 2) {
						g.drawLine(point2.x, point2.y,
								lastMouse.x, lastMouse.y);
						g.drawLine(point1.x, point1.y,
								point2.x, point2.y);
					}
				}
				// redraw each Sierpinski
				for (Sierpinski s : list) {
					s.paint(g, trianglesOnly);
				}
			}
		};
		frame.add(panel);
		panel.addMouseListener(this);
		panel.addMouseMotionListener(this);

		// construct the list
		list = new ArrayList<Sierpinski>();

		// display the window we've created
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * Mouse press event handler creation of new Sierpinski
	 * 
	 * @param e mouse event info
	 */
	@Override
	public void mousePressed(MouseEvent e) {

		switch (soFar) {
			case 0:
				// just remember the start point for initial "line" rubber banding
				point1 = e.getPoint();
				soFar = 1;
				break;
			case 1:
				// remember the second point for "triangle" rubber banding
				point2 = e.getPoint();
				soFar = 2;
				break;
			case 2:
				// we have 3 points, make a Sierpinski object
				list.add(new Sierpinski(point1, point2, e.getPoint()));
				soFar = 0;
				panel.repaint();
				break;
		}
	}

	/**
	 * Mouse moved event handler for "rubber banding" on drawing
	 * 
	 * @param e mouse event info
	 */
	@Override
	public void mouseMoved(MouseEvent e) {

		// if we are in the rubber banding phase, update that
		if (soFar > 0) {
			lastMouse = e.getPoint();
			panel.repaint();
		}
	}

	public static void main(String args[]) {

		boolean trianglesOnly = false;
		if (args.length == 1 && args[0].equals("triangles")) {
			trianglesOnly = true;
		}
		javax.swing.SwingUtilities.invokeLater(new DrawSierpinskis(trianglesOnly));
	}
}

/**
 * The Sierpinski class is responsible for one Sierpinski object.
 */
class Sierpinski {

	private static final int MIN_SIZE = 10;

	/**
	 * The three corners of the outer triangle.
	 */
	private Point corners[];

	/**
	 * Construct a new Sierpinski gasket object.
	 * 
	 * @param corner1 first corner of the outer triangle
	 * @param corner2 second corner of the outer triangle
	 * @param corner3 third corner of the outer triangle
	 */
	public Sierpinski(Point corner1, Point corner2, Point corner3) {

		corners = new Point[3];
		corners[0] = corner1;
		corners[1] = corner2;
		corners[2] = corner3;
	}

	/**
	 * Recursive method to draw the gasket.
	 * 
	 * @param corner1 the first corner
	 * @param corner2 the second corner
	 * @param corner3 the third corner
	 * @param g       the Graphics object on which to draw
	 */
	protected static void drawSierpinski(Point corner1, Point corner2,
			Point corner3, Graphics g) {

		// Find side lengths:
		double sideA = corner1.distance(corner2);
		double sideB = corner2.distance(corner3);
		double sideC = corner3.distance(corner1);
		// Find shortest side:
		double shortest = Math.min(sideA, Math.min(sideB, sideC));

		if (shortest < MIN_SIZE) {
			// Make polygon object:
			Polygon triangle = new Polygon();
			// Add 3 corner points:
			triangle.addPoint(corner1.x, corner1.y);
			triangle.addPoint(corner2.x, corner2.y);
			triangle.addPoint(corner3.x, corner3.y);
			// Draw polygon:
			g.drawPolygon(triangle);
		} else {
			// Find 3 midpoints:
			Point midA = new Point((corner1.x + corner2.x) / 2, (corner1.y + corner2.y) / 2);
			Point midB = new Point((corner2.x + corner3.x) / 2, (corner2.y + corner3.y) / 2);
			Point midC = new Point((corner3.x + corner1.x) / 2, (corner3.y + corner1.y) / 2);
			// Recursively draw top/left/right smaller triangles:
			drawSierpinski(corner1, midA, midC, g);
			drawSierpinski(corner2, midA, midB, g);
			drawSierpinski(corner3, midB, midC, g);
		}
	}

	/**
	 * Draw the gasket on the given Graphics object.
	 * 
	 * @param g            the Graphics object on which the gasket should be drawn
	 * @param triangleOnly should we only draw the outer triangle?
	 */
	public void paint(Graphics g, boolean triangleOnly) {

		if (triangleOnly) {
			g.drawLine(corners[0].x, corners[0].y, corners[1].x, corners[1].y);
			g.drawLine(corners[1].x, corners[1].y, corners[2].x, corners[2].y);
			g.drawLine(corners[2].x, corners[2].y, corners[0].x, corners[0].y);
		} else {
			drawSierpinski(corners[0], corners[1], corners[2], g);
		}
	}

}
