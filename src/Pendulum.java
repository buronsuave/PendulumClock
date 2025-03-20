import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

public class Pendulum extends JComponent {
    private final int pivotX, pivotY, pivotRadius;
    private final int scapeLength, scapeShortLength;
    private final int rodLength, rodStroke, bulbRadius;
    private double angle;

    public Pendulum(int pX, int pY, int pRadius,
                    int rLength, int rStroke,
                    int sLength, int sShortLength,
                    int bRadius, double A) {
        this.pivotX = pX;
        this.pivotY = pY;
        this.pivotRadius = pRadius;
        this.scapeLength = sLength;
        this.scapeShortLength = sShortLength;
        this.rodLength = rLength;
        this.rodStroke = rStroke;
        this.bulbRadius = bRadius;
        this.angle = A;
    }

    public void setAngle(double angle) {
        this.angle = angle;
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

        // Store original transform
        AffineTransform oldTransform = g2d.getTransform();

        // Translate to pivot point and rotate around it
        g2d.translate(pivotX, pivotY);
        g2d.rotate(angle);

        // Draw rod
        g2d.setColor(Color.decode("#6d3c11"));
        g2d.fillRect(-rodStroke / 2, 0, rodStroke, rodLength);

        // Draw scape arms
        g2d.rotate(Math.PI / 4);
        g2d.setColor(Color.decode("#b88b5c"));
        g2d.fillRect(-rodStroke / 2, 0, rodStroke, scapeLength);
        g2d.rotate(- Math.PI / 2);
        g2d.fillRect(-rodStroke / 2, 0, rodStroke, scapeLength);
        g2d.rotate(Math.PI / 4);

        // Draw scape 1st short arm
        g2d.setColor(Color.decode("#b88b5c"));
        /*g2d.translate(-(scapeLength - (double) rodStroke /2) * Math.sqrt(2)/2,
                (scapeLength - (double) rodStroke /2) * Math.sqrt(2)/2);
        g2d.rotate(- Math.PI / 4);
        g2d.fillRect(-rodStroke / 2, 0, rodStroke, scapeShortLength);
        g2d.rotate(Math.PI / 4);
        g2d.translate((scapeLength - (double) rodStroke /2) * Math.sqrt(2)/2,
                -(scapeLength - (double) rodStroke /2) * Math.sqrt(2)/2);*/

        // Draw scape 2nd short arm
        g2d.translate((scapeLength - (double) rodStroke /2) * Math.sqrt(2)/2,
                (scapeLength - (double) rodStroke /2) * Math.sqrt(2)/2);
        g2d.rotate(Math.PI / 4);
        g2d.fillRect(-rodStroke / 2, 0, rodStroke, scapeShortLength);
        g2d.rotate(-Math.PI / 4);
        g2d.translate(-(scapeLength - (double) rodStroke /2) * Math.sqrt(2)/2,
                -(scapeLength - (double) rodStroke /2) * Math.sqrt(2)/2);

        // Draw pivot
        g2d.setColor(Color.decode("#8e582c"));
        g2d.fillOval(-pivotRadius, -pivotRadius, pivotRadius * 2, pivotRadius * 2);

        // Draw bulb
        g2d.setColor(Color.decode("#a06c3f"));
        g2d.fillOval(-bulbRadius, rodLength - bulbRadius, bulbRadius * 2, bulbRadius * 2);

        // Restore original transform
        g2d.setTransform(oldTransform);
    }

    // For test alone
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Pendulum");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 400);

            Pendulum pendulum = new Pendulum(
                    200, 100, 20,
                    200, 10,
                    80, 20,
                    30, Math.PI / 12);
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(pendulum, BorderLayout.CENTER);

            frame.add(panel);
            frame.setVisible(true);
        });
    }
}