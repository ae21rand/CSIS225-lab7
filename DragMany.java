
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * A program to demonstrate a dragging operation on any one of many objects.
 * Could be circles and squares, filled or outlined, and come in various sizes,
 * and colors.
 *
 * @author Prof. White, modified by Lexi Randt
 * @version Spring 2026
 */
public class DragMany extends MouseAdapter implements Runnable {

    /**
     * minimum size of a shape
     */
    public static final int MIN_SIZE = 25;

    /**
     * maximum size of a shape
     */
    public static final int MAX_SIZE = 100;

    /**
     * size of the panel
     */
    public static final int PANEL_SIZE = 600;

    /**
     * list of shapes
     */
    private List<DraggableShape> shapes;

    /**
     * Instead of a boolean to remember if we are dragging, we have a variable
     * that says where the mouse last was so we can move the shape relative to
     * that position. This will be null if the mouse is dragging, but was not
     * pressed on a shape.
     */
    private Point lastMouse;

    /**
     * the shape being dragged
     */
    private DraggableShape dragging;

    /**
     * number of shapes to create, passed on the command line
     */
    private int count;

    /**
     * the panel
     */
    private JPanel panel;

    /**
     * This constructor initializes the count instance variable to the number of
     * shapes that was passed on the command line.
     *
     * @param count number of shapes to create
     */
    public DragMany(int count) {
        this.count = count;
    }

    /**
     * The run method to set up the graphical user interface.
     */
    @Override
    public void run() {

        // Set up the GUI "look and feel" which should match
        // the OS on which we are running.
        JFrame.setDefaultLookAndFeelDecorated(true);

        // Create a JFrame in which we will build our GUI,
        // and give the window a name.
        JFrame frame = new JFrame("DragMany");
        frame.setPreferredSize(new Dimension(PANEL_SIZE, PANEL_SIZE));

        // Tell the JFrame that, when someone closes the
        // window, the application should terminate.
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // JPanel with a paintComponent method.
        panel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {

                // First, call the paintComponent method we are
                // overriding in JPanel.
                super.paintComponent(g);

                // For each shape we have, paint it on the Graphics object.
                for (DraggableShape s : shapes) {
                    s.paint(g);
                }

            }
        };

        frame.add(panel);
        panel.addMouseListener(this);
        panel.addMouseMotionListener(this);

        // Construct our list of shapes and add the required
        // number of shapes to it.
        shapes = new ArrayList<DraggableShape>(count);
        Random rand = new Random();
        for (int i = 0; i < count; i++) {
            // int shape = rand.nextInt(2); // 0 for circle, 1 for square

            // boolean isFilled = rand.nextBoolean(); // true for filled, false for outline

            int size = MIN_SIZE + rand.nextInt(MAX_SIZE - MIN_SIZE); // size of the shape

            Point point = new Point(rand.nextInt(PANEL_SIZE - size),
                    rand.nextInt(PANEL_SIZE - size)); // upper left corner of the shape

            Color color = new Color(rand.nextInt(255),
                    rand.nextInt(255),
                    rand.nextInt(255)); // RGB color for the shape

            // shapes.add(new DraggableShape(shape, isFilled, size, point, color));
            shapes.add(new CircleOpen(size, point, color));

            Point point2 = new Point(rand.nextInt(PANEL_SIZE - size),
                    rand.nextInt(PANEL_SIZE - size));
            shapes.add(new SqaureFilled(size, point2, color));
        }

        // display the window we've created
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Handle mouse pressed events. If the mouse was pressed on a shape, we set
     * up for dragging that shape.
     *
     * @param e the mouse event
     */
    @Override
    public void mousePressed(MouseEvent e) {

        // Make sure we don't start dragging if we didn't press on a shape.
        lastMouse = null;

        // Get the point where the mouse was pressed.
        Point p = e.getPoint();

        // If we pressed within a shape in our list, set up for dragging.
        // Note: the loop counts backwards so we first encounter the object drawn on
        // top (in the foreground) in the case of any overlapping shapes.
        int i = shapes.size() - 1;
        while (i >= 0 && !shapes.get(i).contains(p)) {
            i--;
        }
        if (i >= 0) {
            dragging = shapes.get(i);
            lastMouse = p;

            // Move the shape to the end of the list so this one's
            // drawn on top (in the foreground) of the other shapes.
            // This is a simple way to handle overlapping shapes.
            shapes.remove(i);
            shapes.add(dragging);
        }
    }

    /**
     * Handle mouse released events. If we were dragging a shape, we update its
     * position by the amount the mouse has moved since the last press or drag
     * event and repaint the panel.
     * 
     * @param e the mouse event
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if (lastMouse != null) {
            int dx = e.getPoint().x - lastMouse.x;
            int dy = e.getPoint().y - lastMouse.y;
            dragging.translate(dx, dy);
            panel.repaint();
        }
    }

    /**
     * Handle mouse dragged events. If we are dragging a shape, we update its
     * position by the amount the mouse has moved since the last press or drag
     * event and repaint the panel.
     * 
     * @param e the mouse event
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        if (lastMouse != null) {
            int dx = e.getPoint().x - lastMouse.x;
            int dy = e.getPoint().y - lastMouse.y;
            dragging.translate(dx, dy);
            lastMouse = e.getPoint();
            panel.repaint();
        }
    }

    /**
     * Main method to launch our application.
     *
     * @param args[0] the number of shapes to draw
     */
    public static void main(String args[]) {
        if (args.length != 1) {
            System.err.println("Usage: java DragMany count");
            System.exit(1);
        }

        int count = 0;
        try {
            count = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.err.println("Could not parse " + args[0] + " as integer.");
            System.exit(1);
        }

        // With no errors or exceptions, we can now launch the GUI
        // in the event dispatch thread by creating a new DragMany object
        // and passing it to the invokeLater method.
        javax.swing.SwingUtilities.invokeLater(new DragMany(count));
    }
}

/**
 * A package private class that encapsulates information about a shape on the
 * screen.
 */
abstract class DraggableShape {

    // Some named constants to define shapes and make our code more readable.
    // public static final int CIRCLE = 0;
    // public static final int SQUARE = 1;

    // Attributes of a shape. (originally private before abstract class)
    // protected int shape;
    // private boolean isFilled;
    protected int size;
    protected Point upperLeft;
    protected Color color;

    /**
     * Constructor to create a DraggableShape object with the given attributes.
     * 
     * @param size      the size of the shape
     * @param upperLeft the upper left corner of the shape
     * @param color     the color of the shape
     */
    // @param isFilled whether the shape is filled or outlined (true for filled,
    // false for outlined)
    // @param shape the shape type (CIRCLE or SQUARE)
    public DraggableShape(int size, Point upperLeft, Color color) {

        // NOTE: we do not check the validity of the parameters here,
        // but we could if we wanted to be more robust.
        // this.shape = shape;
        // this.isFilled = isFilled;
        this.size = size;
        this.color = color;

        // Since Point is mutable, we want to avoid side-effects that
        // could result from changes to the Point object. So, we
        // make a new Point object that is private. This way, we know
        // it will not be modified outside of our control.
        this.upperLeft = new Point(upperLeft);
    }

    /**
     * Paint this object onto the given Graphics area.
     *
     * @param g the Graphics object where the shape should be drawn
     */
    public abstract void paint(Graphics g);
    // OLD METHOD:
    // public void paint(Graphics g) {
    // g.setColor(color);
    // if (shape == CIRCLE) {
    // if (isFilled) {
    // g.fillOval(upperLeft.x, upperLeft.y, size, size);
    // } else {
    // g.drawOval(upperLeft.x, upperLeft.y, size, size);
    // }
    // } else {
    // if (isFilled) {
    // g.fillRect(upperLeft.x, upperLeft.y, size, size);
    // } else {
    // g.drawRect(upperLeft.x, upperLeft.y, size, size);
    // }
    // }
    // }

    /**
     * Move this shape by the given amounts in x and y, relative to its current
     * position.
     *
     * @param dx amount to translate in x
     * @param dy amount to translate in y
     */
    public void translate(int dx, int dy) {

        upperLeft.translate(dx, dy);
    }

    /**
     * Determine if the given point is within this shape.
     *
     * @param p Point to check
     */
    public abstract boolean contains(Point p);
    // public boolean contains(Point p) {
    // if (shape == CIRCLE) {
    // Point circleCenter = new Point(upperLeft.x + size / 2, upperLeft.y + size /
    // 2);
    // return circleCenter.distance(p) <= size / 2;
    // } else {
    // return p.x >= upperLeft.x && p.x <= upperLeft.x + size
    // && p.y >= upperLeft.y && p.y <= upperLeft.y + size;
    // }
    // }
}

class CircleOpen extends DraggableShape {

    public CircleOpen(int size, Point upperLeft, Color color) {
        super(size, upperLeft, color);
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(color);
        g.drawOval(upperLeft.x, upperLeft.y, size, size);
    }

    @Override
    public boolean contains(Point p) {
        Point circleCenter = new Point(upperLeft.x + size / 2, upperLeft.y + size / 2);
        return circleCenter.distance(p) <= size / 2;
    }

}

class CircleFilled extends DraggableShape {

    public CircleFilled(int size, Point upperLeft, Color color) {
        super(size, upperLeft, color);
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(color);
        g.fillOval(upperLeft.x, upperLeft.y, size, size);
    }

    @Override
    public boolean contains(Point p) {
        Point circleCenter = new Point(upperLeft.x + size / 2, upperLeft.y + size / 2);
        return circleCenter.distance(p) <= size / 2;
    }

}

class SqaureOpen extends DraggableShape {

    public SqaureOpen(int size, Point upperLeft, Color color) {
        super(size, upperLeft, color);
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(color);
        g.drawRect(upperLeft.x, upperLeft.y, size, size);
    }

    @Override
    public boolean contains(Point p) {
        return p.x >= upperLeft.x && p.x <= upperLeft.x + size 
            && p.y >= upperLeft.y && p.y <= upperLeft.y + size;
    }
}

class SqaureFilled extends DraggableShape {

    public SqaureFilled(int size, Point upperLeft, Color color) {
        super(size, upperLeft, color);
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(color);
        g.fillRect(upperLeft.x, upperLeft.y, size, size);
    }

    @Override
    public boolean contains(Point p) {
        return p.x >= upperLeft.x && p.x <= upperLeft.x + size 
            && p.y >= upperLeft.y && p.y <= upperLeft.y + size;
    }
}
