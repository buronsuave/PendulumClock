import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.io.File;
import javax.sound.sampled.AudioSystem;

public class PendulumClock extends JFrame {
    private final Pendulum pendulum;
    private final ScapeGear scapeGear;
    private final TimeGear secondsGear;
    private final TimeGear aux1Gear;
    private final TimeGear minutesGear;
    private final TimeGear aux2Gear;
    private final TimeGear hoursGear;

    private final long startTime;

    private final double initialSeconds;
    private final double initialMinutes;
    private final double initialHours;

    private boolean soundPlaying;

    @Override
    public void paintComponents(Graphics g) {
        super.paintComponents(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Get the current time
        LocalTime now = LocalTime.now();
        String timeString = String.format("%02d:%02d:%02d", now.getHour(), now.getMinute(), now.getSecond());

        // Set font and colors
        Font font = new Font("Arial", Font.BOLD, 40);
        g2d.setFont(font);

        int x = getWidth() / 2 - g2d.getFontMetrics().stringWidth(timeString) / 2;
        int y = 120; // Margin from the top

        g2d.setColor(Color.decode("#003b6d"));
        g2d.drawString(timeString.substring(0, 2), x, y);

        g2d.setColor(Color.decode("#b74a23"));
        g2d.drawString(timeString.substring(3, 5), x + 60, y);

        g2d.setColor(Color.decode("#879281"));
        g2d.drawString(timeString.substring(6, 8), x + 120, y);
    }

    private void playTickSound() {
        new Thread(() -> {
            try {
                File soundFile = new File("/home/buronsuave/IdeaProjects/PendulumClock/src/tick.wav");
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
                AudioFormat format = audioInputStream.getFormat();

                DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
                if (!AudioSystem.isLineSupported(info)) {
                    System.out.println("Audio format not supported, try converting the file.");
                    return;
                }

                SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
                line.open(format);
                line.start();

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = audioInputStream.read(buffer)) != -1) {
                    line.write(buffer, 0, bytesRead);
                }

                line.drain();
                line.close();
                audioInputStream.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }).start();
    }

    public PendulumClock() {
        setTitle("Pendulum Clock");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 1000);

        // Get the current system time
        LocalTime now = LocalTime.now();
        initialSeconds = now.getSecond();
        initialMinutes = now.getMinute();
        initialHours = now.getHour() % 12; // Convert to 12-hour format

        // Create the pendulum
        pendulum = new Pendulum(
                250, 120, 20,
                350, 10,
                78, 15,
                30, 0
        );
        pendulum.setBounds(0, 0, 1000, 800);

        // Create the scape gear
        scapeGear = new ScapeGear(
                250, 340, 30, 15,
                150, 140, 6, 20, 50);
        scapeGear.setBounds(0, 0, 1000, 1000);

        // Create the seconds gear
        secondsGear = new TimeGear(407, 345, 30,
                10, 100, 90, 3, 20, 25,
                Color.decode("#879281"), Color.decode("#bab78c"), true);
        secondsGear.setBounds(0, 0, 1000, 1000);

        // Create 1st aux gear
        aux1Gear = new TimeGear(407, 610, 100,
                10, 230, 220, 8, 20, 25,
                Color.decode("#767b8d"), Color.decode("#a7adb2"), false);
        aux1Gear.setBounds(0, 0, 1000, 1000);

        // Create minutes gear
        minutesGear = new TimeGear(590, 610, 60,
                10, 150, 140, 4, 20, 25,
                Color.decode("#b74a23"), Color.decode("#d9633b"), true);
        minutesGear.setBounds(0, 0, 1000, 1000);

        // Create 2nd aux gear
        aux2Gear = new TimeGear(770, 610, 60,
                15, 150, 140, 6, 20, 50,
                Color.decode("#777777"), Color.decode("#999999"), false);
        aux2Gear.setBounds(0, 0, 1000, 1000);

        // Create the hours gear
        hoursGear = new TimeGear(760, 452, 30,
                10, 100, 90, 5, 20, 25,
                Color.decode("#003b6d"), Color.decode("#6699cc"), true);
        hoursGear.setBounds(0, 0, 1000, 1000);

        add(hoursGear);
        add(aux2Gear);
        add(minutesGear);
        add(aux1Gear);
        add(secondsGear);
        add(scapeGear);
        add(pendulum);

        soundPlaying = false;

        // Start timer
        startTime = System.nanoTime();
        Timer timer = new Timer(20, e -> updateComponents());
        timer.start();

        setVisible(true);
    }

    private void updateComponents() {
        double elapsedTime = (System.nanoTime() - startTime) / 1_000_000_000.0; // Convert to seconds
        elapsedTime += initialSeconds + 60*initialMinutes + 3600*initialHours;
        // Maximum swing (30 degrees)
        double maxAngle = -Math.PI / 12;
        // 1-second full oscillation
        double angularFrequency = 2 * Math.PI;
        double newPendulumAngle = maxAngle * Math.sin(angularFrequency * elapsedTime);
        pendulum.setAngle(newPendulumAngle);

        // Update ScapeGear rotation (rotate 1 tooth per second)
        double pseudoElapsedTime;
        if (elapsedTime - (int) elapsedTime >= 0.5) {
            if (!soundPlaying) {
                playTickSound();
                soundPlaying = true;
            }
            pseudoElapsedTime = (int) elapsedTime + 1;
        } else {
            if (soundPlaying) {
                soundPlaying = false;
            }
            pseudoElapsedTime = ((int) elapsedTime) + ((elapsedTime - (int) elapsedTime) * 2);
        }

        double newScapeGearAngle = -2 * Math.PI * pseudoElapsedTime / scapeGear.getOuterTeeth();
        scapeGear.setAngle(newScapeGearAngle);

        double newSecondsGearAngle = -(1.0 / 2.0) * newScapeGearAngle;
        secondsGear.setAngle(newSecondsGearAngle);

        double newAux1GearAngle = -(1.0 / 10.0) * newSecondsGearAngle;
        aux1Gear.setAngle(newAux1GearAngle + Math.PI / aux1Gear.getOuterTeeth());

        double newMinutesGearAngle = -(1.0 / 6) * newAux1GearAngle;
        minutesGear.setAngle(newMinutesGearAngle);

        double newAux2GearAngle = -(1.0 / 6) * newMinutesGearAngle;
        aux2Gear.setAngle(newAux2GearAngle);

        double newHoursGearAngle = -(1.0 / 2) * newAux2GearAngle;
        hoursGear.setAngle(newHoursGearAngle);

        // Repaint everything at once
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PendulumClock::new);
    }
}