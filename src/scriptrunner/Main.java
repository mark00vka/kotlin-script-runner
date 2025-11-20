package scriptrunner;

import javax.swing.*;
import java.awt.Window;

class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            ScriptRunnerUI ui = new ScriptRunnerUI();
            ui.setVisible(true);
        });
    }
}