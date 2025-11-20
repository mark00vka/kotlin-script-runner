package scriptrunner;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Highlighter {
    private static final Set<String> KEYWORDS = new HashSet<String>(Arrays.asList(
            "fun", "val", "var","if", "else", "for",
            "while", "do", "when", "class", "object",
            "interface", "package", "import", "return",
            "true", "false", "null", "this", "super", "in", "is", "as"));

    private static final Color KEYWORD_COLOR = new Color(0x901080);
    private static final Color DEFAULT_COLOR = new Color(0x111111);

    private StyleContext styleContext;
    private Style keywordStyle;
    private Style defaultStyle;

    public Highlighter() {
        initialiazeStyles();
    }

    private void initialiazeStyles() {
        styleContext = StyleContext.getDefaultStyleContext();

        defaultStyle = styleContext.addStyle("default", null);
        StyleConstants.setForeground(defaultStyle, DEFAULT_COLOR);
        StyleConstants.setFontFamily(defaultStyle, Font.MONOSPACED);
        StyleConstants.setFontSize(defaultStyle, 14);

        keywordStyle = styleContext.addStyle("keyword", null);
        StyleConstants.setForeground(keywordStyle, KEYWORD_COLOR);
        StyleConstants.setFontFamily(keywordStyle, Font.MONOSPACED);
        StyleConstants.setFontSize(keywordStyle, 14);
    }

    public void applyHighlighting(JTextPane textPane) {
        String text = textPane.getText();

        if (text.isEmpty()) {
            return;
        }

        StyledDocument document = textPane.getStyledDocument();

        try {
            highlightText(document, text);
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }

    private void highlightText(StyledDocument doc, String text) throws BadLocationException {
        doc.setCharacterAttributes(0, doc.getLength(), defaultStyle, true);

        String keywordPattern = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
        Pattern pattern = Pattern.compile(keywordPattern);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            doc.setCharacterAttributes(start, end - start, keywordStyle, false);
        }
    }

    public void install(JTextPane textPane) {
        textPane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));

        textPane.getDocument().addDocumentListener(new DocumentListener() {
            private boolean isHighlighting = false;

            @Override
            public void insertUpdate(DocumentEvent e) {
                if (!isHighlighting) delayHighlight(textPane);
            }

            public void removeUpdate(DocumentEvent e) {
                if (!isHighlighting) delayHighlight(textPane);
            }

            public void changedUpdate(DocumentEvent e) {
                if (!isHighlighting) applyHighlighting(textPane);
            }

            private void delayHighlight(JTextPane textPane) {
                SwingUtilities.invokeLater(() -> {
                    isHighlighting = true;
                    try {
                        applyHighlighting(textPane);
                    } finally {
                        isHighlighting = false;
                    }
                });
            }
        });

        applyHighlighting(textPane);
    }
}
