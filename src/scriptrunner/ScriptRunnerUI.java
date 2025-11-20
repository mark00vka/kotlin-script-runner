package scriptrunner;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ScriptRunnerUI extends JFrame {
    private EditorPane editorPane;
    private OutputPane outputPane;
    private ScriptRunner scriptRunner;
    private JButton runButton;
    private JButton stopButton;
    private StatusBar statusBar;

    private long startTime;

    public ScriptRunnerUI() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Kotlin Script Runner");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        editorPane = new EditorPane();
        outputPane = new OutputPane(editorPane);
        scriptRunner = new ScriptRunner();
        statusBar = new StatusBar();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(editorPane);
        splitPane.setRightComponent(outputPane);
        splitPane.setDividerSize(1);
        splitPane.setResizeWeight(0.5);

        add(splitPane, BorderLayout.CENTER);

        setSize(1200, 800);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                scriptRunner.shutdown();
            }
        });

        runButton = new JButton("Run");
        stopButton = new JButton("Stop");

        runButton.addActionListener(e -> runScript());
        stopButton.addActionListener(e -> stopScript());
        stopButton.setEnabled(false);

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        toolBar.add(runButton);
        toolBar.add(stopButton);
        toolBar.addSeparator();

        JButton clearButton = new JButton("Clear Output");
        clearButton.addActionListener(e -> outputPane.clear());
        toolBar.add(clearButton);

        toolBar.add(statusBar);
        add(toolBar, BorderLayout.NORTH);
    }

    private void runScript() {
        String script = editorPane.getScriptContent();
        if (script.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Script cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        outputPane.clear();

        scriptRunner.runScript(script, new ScriptRunner.ScriptExecutionListener() {
            @Override
            public void onScriptStarted() {
                runButton.setEnabled(false);
                stopButton.setEnabled(true);

                startTime = System.currentTimeMillis();
                statusBar.setRunning();
            }

            @Override
            public void onScriptCompleted(Integer exitCode) {
                SwingUtilities.invokeLater(() -> {
                    runButton.setEnabled(true);
                    stopButton.setEnabled(false);

                    if (exitCode == 0) {
                        statusBar.setSuccess(System.currentTimeMillis() - startTime);
                    } else {
                        statusBar.setError(exitCode);
                    }
                });
            }

            @Override
            public void onOutput(String output) {
                outputPane.appendText(output);
            }

            @Override
            public void onError(String error) {
                outputPane.appendText(error);
            }
        });
    }

    private void stopScript() {
        scriptRunner.stopScript();
        runButton.setEnabled(true);
        stopButton.setEnabled(false);
    }
}
