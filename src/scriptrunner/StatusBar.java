package scriptrunner;

import javax.swing.*;
import java.awt.*;

public class StatusBar extends JPanel {
    private JLabel statusLabel;
    private JLabel exitCodeLabel;
    private JPanel statusIndicator;

    public StatusBar() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLoweredSoftBevelBorder());

        statusIndicator = new JPanel();
        statusIndicator.setPreferredSize(new Dimension(20, 20));
        statusIndicator.setBackground(Color.GRAY);
        statusIndicator.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

        statusLabel = new JLabel("Ready");
        exitCodeLabel = new JLabel("");

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.add(statusIndicator);
        leftPanel.add(statusLabel);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.add(exitCodeLabel);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
    }

    public void setRunning() {
        SwingUtilities.invokeLater(() -> {
            statusIndicator.setBackground(Color.GREEN);
            statusLabel.setText("Running...");
            exitCodeLabel.setText("");
        });
    }

    public void setSuccess(long execTime) {
        SwingUtilities.invokeLater(() -> {
            statusIndicator.setBackground(Color.BLUE);
            statusLabel.setText("Success (" + execTime + " ms)");
            exitCodeLabel.setText("Exit code: 0");
            exitCodeLabel.setForeground(Color.GRAY);
        });
    }

    public void setError(int exitCode) {
        SwingUtilities.invokeLater(() -> {
            statusIndicator.setBackground(Color.RED);
            statusLabel.setText("Execution failed");
            exitCodeLabel.setText("Exit code: " + exitCode);
            exitCodeLabel.setForeground(Color.RED);
        });
    }

    public void setReady() {
        SwingUtilities.invokeLater(() -> {
            statusIndicator.setBackground(Color.GRAY);
            statusLabel.setText("Ready");
            exitCodeLabel.setText("");
        });
    }

    public void setStopped() {
        SwingUtilities.invokeLater(() -> {
            statusIndicator.setBackground(Color.DARK_GRAY);
            statusLabel.setText("Stopped");
            exitCodeLabel.setText("Exit code: -1");
            exitCodeLabel.setForeground(Color.ORANGE);
        });
    }
}
