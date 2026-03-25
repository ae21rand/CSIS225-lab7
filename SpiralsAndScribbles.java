
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * This program draws spirals and scribbles on the screen.
 * The user can choose to draw spirals or scribbles, and can also choose
 * different color modes.
 * The user can clear the drawings if the mouse exits the drawing area,
 * depending on the checkbox selection.
 * 
 * @author Prof. White
 * @version Spring 2026
 */
public class SpiralsAndScribbles extends MouseAdapter implements Runnable, ActionListener {

    /** A list of all the shapes to be drawn. */
    private ArrayList<ShapesOfScenes> items = new ArrayList<>();

    /** The current color used for drawing. */
    private Color currentColor = Color.BLACK;

    /** The point where the mouse was last pressed. */
    private Point pressPoint;

    /** The current ShapesOfScenes object being drawn. */
    private ShapesOfScenes current;

    /** The JPanel on which the shapes are drawn. */
    private JPanel panel;

    /** The JComboBox for selecting what to draw: spirals or scribbles. */
    private JComboBox drawWhat;

    /** The JCheckBox for clearing the GUI window. */
    private JCheckBox clearOnExit;

    /** The JComboBox for selecting the color. */
    private JComboBox colorMode;

    protected static Random rand = new Random();

    /**
     * This method returns a new Color object with randomly selected RGB
     * values in the range [0, 255] using the class variable.
     * 
     * @return a new Color object with randomly selected RGB values in the range [0,
     *         255]
     */
    public static Color randomColor() {
        return new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
    }

    /**
     * This method recolors all objects in the items list.
     */
    protected void recolorAll() {
        for (ShapesOfScenes obj : items) {
            for (ColorLineSegment line : obj.lines) {
                line.c = randomColor();
            }
        }
    }

    public void randomColor(boolean isCrazy) {
        Color c;
        if (isCrazy) {
            for (ShapesOfScenes obj : items) {
                for (ColorLineSegment line : obj.lines) {
                    line.c = randomColor();
                }
            }
        } else {
            Color newColor = randomColor();

            for (ShapesOfScenes obj : items) {
                for (ColorLineSegment line : obj.lines) {
                    line.c = newColor;
                }
            }
            panel.repaint();
        }
    }

    /**
     * This method is called by the paintComponent method of
     * the anonymous extension of JPanel, to keep that method
     * from getting too long.
     */
    protected void redraw(Graphics g) {
        for (ShapesOfScenes l : items) {
            l.paint(g);
        }
    }

    /**
     * The run method to set up the graphical user interface.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void run() {

        // Set up the GUI "look and feel" which should match
        // the OS on which we are running.
        JFrame.setDefaultLookAndFeelDecorated(true);

        // Create a JFrame in which we will build our very
        // tiny GUI, and give the window a name.
        JFrame frame = new JFrame("SpiralsAndScribbles");
        frame.setPreferredSize(new Dimension(800, 800));

        // Tell the JFrame that when someone closes the
        // window, the application should terminate.
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel outerPanel = new JPanel(new BorderLayout());
        frame.add(outerPanel);

        panel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                redraw(g);
            }
        };

        outerPanel.add(panel, BorderLayout.CENTER);
        panel.addMouseListener(this);
        panel.addMouseMotionListener(this);

        JPanel controlPanel = new JPanel();
        drawWhat = new JComboBox();
        drawWhat.addItem("Spirals");
        drawWhat.addItem("Scribbles");
        drawWhat.setSelectedItem("Scribbles");
        controlPanel.add(drawWhat);

        clearOnExit = new JCheckBox();
        clearOnExit.setSelected(false);
        controlPanel.add(clearOnExit);

        String[] colorOptions = { "Black", "Colorful", "More Colorful", "Crazy Colorful" };
        colorMode = new JComboBox<>(colorOptions);
        colorMode.setSelectedIndex(0);
        controlPanel.add(colorMode);

        outerPanel.add(controlPanel, BorderLayout.SOUTH);

        // Display the window we've created.
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * This method is called when the mouse is pressed. It initializes a new
     * ShapesOfScenes object and sets the current color based on the selected
     * color mode.
     * 
     * @param e the MouseEvent containing information about the mouse press
     */
    @Override
    public void mousePressed(MouseEvent e) {

        pressPoint = e.getPoint();
        current = new ShapesOfScenes();
        items.add(current);
    }

    /**
     * This method is called when the mouse is moved.
     * It repaints the panel to reflect any changes in the drawing.
     * 
     * @param e the MouseEvent containing information about the mouse movement
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        panel.repaint();
    }

    /**
     * This method is called when the mouse is dragged. It creates a new
     * ColorLineSegment
     * from the press point to the current mouse position, adds it to the current
     * ShapesOfScenes, and repaints the panel.
     * 
     * @param e the MouseEvent containing information about the mouse drag
     */
    @Override
    public void mouseDragged(MouseEvent e) {

        if (pressPoint == null) {
            return;
        }
        Point p[] = new Point[2];
        p[0] = pressPoint;
        p[1] = e.getPoint();
        current.addSegment(new ColorLineSegment(currentColor, p));
        if (drawWhat.getSelectedItem().equals("Scribbles")) {
            pressPoint = e.getPoint();
        }
        panel.repaint();
    }

    /**
     * This method is called when the mouse exits the drawing area.
     * If the "Clear on exit" checkbox is selected, it clears all the drawings.
     */
    @Override
    public void mouseExited(MouseEvent e) {
        if (clearOnExit.isSelected()) {
            items.clear();
            panel.repaint();
        }
    }

    /**
     * This method is called when an action event occurs, such as a button press or
     * menu selection.
     * In this program, we don't have any buttons, so this method is not used.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // We don't have any buttons, so this method is not used.
    }

    /**
     * The main method is responsible for creating a thread
     * that will construct and show the graphical user interface.
     */
    public static void main(String args[]) {
        javax.swing.SwingUtilities.invokeLater(new SpiralsAndScribbles());
    }
}

/**
 * A ColorLineSegment is a line segment with a color.
 * The line segment is defined by two points, and the color is used when drawing
 * the line.
 */
class ColorLineSegment {

    /**
     * The color of the line segment.
     */
    protected Color c;

    /**
     * The two endpoints of the line segment.
     */
    protected Point[] p;

    /**
     * Constructs a ColorLineSegment with the specified color and endpoints.
     * 
     * @param c the color of the line segment
     * @param p an array of two points representing the endpoints of the line
     *          segment
     */
    public ColorLineSegment(Color c, Point[] p) {

        this.c = c;
        this.p = p;
    }

    /**
     * Paints the line segment on the given Graphics context.
     * 
     * @param g the Graphics context to draw on
     */
    public void paint(Graphics g) {
        g.setColor(c);
        g.drawLine(p[0].x, p[0].y, p[1].x, p[1].y);
    }
}

/**
 * The ShapesOfScenes class represents a collection of ColorLineSegments.
 */
class ShapesOfScenes {

    /**
     * A list of all the line segments in the scene.
     */
    protected ArrayList<ColorLineSegment> lines = new ArrayList<>();

    /**
     * Adds a ColorLineSegment to the collection of line segments.
     * 
     * @param s the ColorLineSegment to add
     */
    public void addSegment(ColorLineSegment s) {

        lines.add(s);
    }

    /**
     * Paints all the line segments in the collection on the given Graphics context.
     * 
     * @param g the Graphics context to draw on
     */
    public void paint(Graphics g) {

        for (ColorLineSegment l : lines) {
            l.paint(g);
        }
    }

    /**
     * Determines if the given point is near any of the line segments in the
     * collection.
     * 
     * @param p the point to check
     * @return true if the point is near any line segment, false otherwise
     */
    public boolean nearTo(Point p) {
        if (lines.size() == 0) {
            return false;
        }
        return p.distance(lines.get(0).p[0]) < 25;
    }
}