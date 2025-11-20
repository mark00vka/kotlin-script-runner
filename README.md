# Kotlin Script Runner

A GUI tool for writing and executing Kotlin scripts with live output and syntax highlighting.

## Features

- **Side-by-side editor and output panes**
- **Live script execution** with real-time output
- **Syntax highlighting** for Kotlin keywords
- **Execution status indicator** (running/success/failure)
- **Stop execution** for long-running scripts
- **Error display:** error code in the top right corner

## Prerequisites

- Java 11 or higher
- Kotlin compiler (`kotlinc`) available in PATH
- Gradle 7.0 or higher

## Building and Running

### Option 1: Open the project in IntelliJ IDEA and build it

### Option 2: Manual Compilation

```bash
# Clone or extract the project
cd kotlin-script-runner

# Create output directory
mkdir bin

# Compile all Java files
javac -d bin src/scriptrunner/*.java

# Run the application
java -cp bin scriptrunner.Main

```
