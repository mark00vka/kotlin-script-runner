package scriptrunner;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class OutputPane extends JPanel {
    private JTextPane outputArea;
    private ErrorLocator errorLocator;

    public OutputPane(EditorPane editor) {
        initComponents();
        errorLocator = new ErrorLocator(editor, outputArea);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(BorderFactory.createTitledBorder("Output"));
        outputArea = new JTextPane();
        outputArea.setEditable(false);
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        outputPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        add(outputPanel,  BorderLayout.CENTER);
    }

    public void appendText(String text) {
        SwingUtilities.invokeLater(() -> {
            try {
                if (errorLocator != null) {
                    errorLocator.appendOutput(text);
                } else {
                    Document document = outputArea.getDocument();
                    Style style = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
                    document.insertString(document.getLength(), text, style);
                    outputArea.setCaretPosition(document.getLength());
                }
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        });
    }

    public void clear() {
        outputArea.setText("");
    }
}
