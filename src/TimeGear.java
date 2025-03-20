import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

public class TimeGear extends JComponent {
    private final int centerX, centerY;
    private final int outerTeeth, innerTeeth;
    private final int outerRadius, innerRadius;
    private final int arms, armsStroke;
    private final int internalGearRadius;
    private final Color primaryColor, secondaryColor;
    private final boolean arrow;

    private double angle;

    public TimeGear(int centerX, int centerY,
                     int outerTeeth, int innerTeeth,
                     int outerRadius, int innerRadius,
                     int arms, int armsStroke, int internalGearRadius,
                    Color primaryColor, Color secondaryColor, boolean arrow) {

        this.centerX = centerX;
        this.centerY = centerY;
        this.outerTeeth = outerTeeth;
        this.innerTeeth = innerTeeth;
        this.outerRadius = outerRadius;
        this.innerRadius = innerRadius;
        this.arms = arms;
        this.armsStroke = armsStroke;
        this.internalGearRadius = internalGearRadius;
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.arrow = arrow;

        this.angle = 0.0;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public int getOuterTeeth() {
        return outerTeeth;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(400, 400);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Store original transform and apply angle offset
        AffineTransform oldTransform = g2d.getTransform();
        g2d.translate(centerX, centerY);
        g2d.rotate(angle);

        g2d.rotate(Math.PI / arms);
        // Draw arms
        g2d.setColor(secondaryColor);
        for (int a = 0; a < arms; ++a) {
            g2d.fillRect(-armsStroke/2, 0, armsStroke, outerRadius);
            g2d.rotate(2*Math.PI / arms);
        }
        g2d.rotate(-Math.PI / arms);

        // Draw all outer teeth
        g2d.setColor(primaryColor);
        double outerStep = 2 * Math.PI / outerTeeth;

        for (int t = 0; t < outerTeeth; ++t) {
            int[] xPoints = {-4, 4, 4, -4}; // Teeth width
            int[] yPoints = {-outerRadius, -outerRadius, -outerRadius-8, -outerRadius-8}; // Teeth length and position
            int nPoints = 4;

            g2d.fillPolygon(xPoints, yPoints, nPoints);

            // Rotate for next iteration
            g2d.rotate(outerStep);
        }

        // Undo the transformations after drawing the teeth
        g2d.setTransform(oldTransform);

        // Draw the ring shape (gear with inner hole)
        Ellipse2D.Double outerCircle = new Ellipse2D.Double(centerX - outerRadius, centerY - outerRadius, 2 * outerRadius, 2 * outerRadius);
        Ellipse2D.Double innerCircle = new Ellipse2D.Double(centerX - innerRadius, centerY - innerRadius, 2 * innerRadius, 2 * innerRadius);
        Area gearArea = new Area(outerCircle);
        gearArea.subtract(new Area(innerCircle)); // Remove inner circle to create the gear shape
        g2d.fill(gearArea); // Fill the ring

        // Draw the internal gear
        // First draw its teeth
        g2d.translate(centerX, centerY);
        g2d.rotate(angle);
        g2d.setColor(primaryColor);

        double innerStep = 2 * Math.PI / innerTeeth;

        for (int t = 0; t < innerTeeth; ++t) {
            int[] xPoints = {-4, 4, 4, -4}; // Teeth width
            int[] yPoints = {-internalGearRadius, -internalGearRadius, -internalGearRadius-8, -internalGearRadius-8}; // Teeth length and position
            int nPoints = 4;

            g2d.fillPolygon(xPoints, yPoints, nPoints);

            // Rotate for next iteration
            g2d.rotate(innerStep);
        }

        // Undo the transformations after drawing the teeth
        g2d.setTransform(oldTransform);

        // Center for internal gear
        g2d.fillOval(centerX - internalGearRadius, centerY - internalGearRadius, 2*internalGearRadius, 2*internalGearRadius);

        if (arrow) {
            // Draw arrow
            g2d.translate(centerX, centerY);
            g2d.rotate(angle);
            g2d.setColor(Color.decode("#ea8760"));
            g2d.fillRect(-2,-outerRadius/2, 4, outerRadius/2);
            int[] xPoints = {-10, 10, 0}; // Teeth width
            int[] yPoints = {-outerRadius/2, -outerRadius/2, -outerRadius/2-15};
            int nPoints = 3;
            g2d.fillPolygon(xPoints, yPoints, nPoints);
        }

        // Undo the transformations after drawing the teeth
        g2d.setTransform(oldTransform);
    }

    // For testing alone
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("ScapeGear");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 400);

            TimeGear gear = new TimeGear(
                    200, 200, 30, 15,
                    150, 140, 6, 6, 50,
                    Color.decode("#8e582c"), Color.decode("#d0ab7a"), true);
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(gear, BorderLayout.CENTER);

            frame.add(panel);
            frame.setVisible(true);
        });
    }
}
