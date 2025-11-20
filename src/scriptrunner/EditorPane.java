package scriptrunner;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.io.Console;

public class EditorPane extends JPanel {
    private JTextPane editor;
    private JScrollPane scrollPanel;
    private Highlighter highlighter;

    public EditorPane() {
        initComponent();
    }

    private void initComponent() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Editor"));

        editor = new JTextPane();
        scrollPanel = new JScrollPane(editor);
        scrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        scrollPanel.setAutoscrolls(true);
        add(scrollPanel, BorderLayout.CENTER);

        highlighter = new Highlighter();
        highlighter.install(editor);

        editor.setText(getDefaultScript());
    }

    public void navigateToErrorLocation(int lineNumber, int columnNumber) {
        StyledDocument doc = editor.getStyledDocument();
        Element root = doc.getDefaultRootElement();

        if (lineNumber > 0 && lineNumber <= root.getElementCount()) {
            Element line = root.getElement(lineNumber-1);
            int start = line.getStartOffset();

            int position = start + Math.max(0, columnNumber - 1);

            editor.setCaretPosition(position);
            editor.requestFocusInWindow();
        }
    }

    public String getScriptContent() {
        return editor.getText();
    }

    private String getDefaultScript() {
        return "println(\"Hello from Kotlin!\")\n";
    }
}
