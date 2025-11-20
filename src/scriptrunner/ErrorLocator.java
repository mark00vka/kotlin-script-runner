package scriptrunner;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ErrorLocator {
    private EditorPane editor;
    private JTextPane output;
    private Pattern errorPattern;


    public ErrorLocator(EditorPane editor, JTextPane output) {
        this.editor = editor;
        this.output = output;
        //kts:2:4: error: expecting a condition in parentheses '(...)'
        this.errorPattern = Pattern.compile("kts:(\\d+):(\\d+):\\s+(error|warning):.+");
        
        setupOutputPane();
    }
    
    private void setupOutputPane() {
        output.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int position = output.viewToModel(e.getPoint());
                    handleOutputClick(position);
                }
            }
        });
        
        output.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        installOutputStyle();
    }

    private void handleOutputClick(int position) {
        try {
            StyledDocument doc = output.getStyledDocument();
            Element element = doc.getCharacterElement(position);
            AttributeSet attributes = element.getAttributes();

            Integer lineNumber = (Integer) attributes.getAttribute("errorLine");
            Integer columnNumber = (Integer) attributes.getAttribute("errorColumn");

            if (lineNumber != null && columnNumber != null) {
                editor.navigateToErrorLocation(lineNumber, columnNumber);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void installOutputStyle() {
        StyledDocument doc = output.getStyledDocument();

        Style defaultStyle = doc.addStyle("default", null);
        StyleConstants.setForeground(defaultStyle, Color.BLACK);
        StyleConstants.setFontFamily(defaultStyle, Font.MONOSPACED);
        StyleConstants.setFontSize(defaultStyle, 12);
        
        Style errorLocationStyle = doc.addStyle("error", null);
        StyleConstants.setForeground(errorLocationStyle, Color.BLUE);
        StyleConstants.setUnderline(errorLocationStyle, true);
        StyleConstants.setFontFamily(errorLocationStyle, Font.MONOSPACED);
        StyleConstants.setFontSize(errorLocationStyle, 12);
    }
    
    public void appendOutput(String text) {
        SwingUtilities.invokeLater(() -> {
            try {
                StyledDocument doc = output.getStyledDocument();
                
                processText(text, doc);
                
                output.setCaretPosition(doc.getLength());
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        });
    }
    
    private void processText(String text, StyledDocument doc) throws BadLocationException {
        String[] lines = text.split("\n", -1);
        
        for (String line : lines) {
            Matcher matcher = errorPattern.matcher(line);
            
            if (matcher.find()) {
                appendClickableErrorLine(line, matcher, doc);
            } else {
                appendRegularLine(line, doc);
            }

            doc.insertString(doc.getLength(), "\n", doc.getStyle("default"));
        }
    }

    private void appendRegularLine(String line, StyledDocument doc) throws BadLocationException {
        doc.insertString(doc.getLength(), line, doc.getStyle("default"));
    }

    private void appendClickableErrorLine(String line, Matcher matcher, StyledDocument doc) throws BadLocationException {
        int lineNumber = Integer.parseInt(matcher.group(1));
        int columnNumber = Integer.parseInt(matcher.group(2));

        int start = doc.getLength();

        doc.insertString(doc.getLength(), line, doc.getStyle("error"));

        SimpleAttributeSet attributes = new SimpleAttributeSet();
        attributes.addAttribute("errorLine", lineNumber);
        attributes.addAttribute("errorColumn", columnNumber);
        doc.setCharacterAttributes(start, line.length(), attributes, false);

        output.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
