package scriptrunner;

import javax.swing.*;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.IntToDoubleFunction;

public class ScriptRunner {
    private Process currentProcess;
    private ExecutorService executorService;
    private volatile boolean isRunning;

    public ScriptRunner() {
        this.executorService = Executors.newCachedThreadPool();
    }

    public void runScript(String script, ScriptExecutionListener listener) {
        stopScript();

        executorService.submit(() -> {
            isRunning = true;
            listener.onScriptStarted();

            File scriptFile = null;
            try {
                scriptFile = File.createTempFile("script", ".kts");
                scriptFile.deleteOnExit();
                try (FileWriter fw = new FileWriter(scriptFile)) {
                    fw.write(script);
                }

                ProcessBuilder runPb = new ProcessBuilder("kotlinc.bat", "-script", scriptFile.getAbsolutePath());
                runPb.redirectErrorStream(true);
                currentProcess = runPb.start();

                startOutputReader(currentProcess.getInputStream(), listener::onOutput);
                startOutputReader(currentProcess.getErrorStream(), listener::onError);

                int exitCode = currentProcess.waitFor();
                isRunning = false;

                listener.onScriptCompleted(exitCode);
            } catch (IOException ex) {
                SwingUtilities.invokeLater(() -> {
                    listener.onError("IO Error: " + ex.getMessage() + "\n");
                    listener.onScriptCompleted(-1);
                });
            } catch (InterruptedException ex) {
                SwingUtilities.invokeLater(() -> {
                    listener.onError("Interrupted Error: " + ex.getMessage() + "\n");
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    listener.onError("Error: " + e.getMessage() + "\n");
                    listener.onScriptCompleted(-1);
                });
            } finally {
                isRunning = false;
                if (scriptFile != null && scriptFile.exists()) {
                    scriptFile.delete();
                }
            }
        });
    }

    public void stopScript() {
        isRunning = false;
        if (currentProcess != null && currentProcess.isAlive()) {
            currentProcess.destroy();
            try {
                currentProcess.waitFor(2, TimeUnit.SECONDS);
                if (currentProcess.isAlive()) {
                    currentProcess.destroyForcibly();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void startOutputReader(InputStream inputStream, OutputListener output) {
        executorService.submit(() -> {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = br.readLine()) != null && isRunning ) {
                    String outputLine = line + "\n";
                    SwingUtilities.invokeLater(() -> output.onOutput(outputLine));
                }
            } catch (IOException ex) {
                if (isRunning) {
                    SwingUtilities.invokeLater(() -> output.onOutput("Error reading output: " + ex.getMessage() + "\n"));
                }
            }
        });
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void shutdown() {
        stopScript();
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public interface ScriptExecutionListener {
        void onScriptStarted();
        void onScriptCompleted(Integer exitCode);
        void onOutput(String output);
        void onError(String error);
    }

    public interface  OutputListener {
        void onOutput(String output);
    }
}
