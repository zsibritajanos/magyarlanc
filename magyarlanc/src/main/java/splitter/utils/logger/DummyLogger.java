package splitter.utils.logger;

/**
 * Dummy logger which generates no output.
 */

public class DummyLogger implements Logger {
  /**
   * True if logger enabled.
   */

  protected boolean loggerEnabled = false;

  /**
   * Create a dummy logger.
   */

  public DummyLogger() {
    loggerEnabled = true;
  }

  /**
   * Terminates the logger.
   */

  public void terminate() {
    loggerEnabled = false;
  }

  /**
   * Logs a message at the DEBUG level.
   *
   * @param str Log message.
   */

  public void logDebug(String str) {
  }

  /**
   * Logs a message at the INFO level.
   *
   * @param str Log message.
   */

  public void logInfo(String str) {
  }

  /**
   * Logs a message at the WARN level.
   *
   * @param str Log message.
   */

  public void logWarning(String str) {
  }

  /**
   * Logs a message at the ERROR level.
   *
   * @param str Log message.
   */

  public void logError(String str) {
  }

  /**
   * Logs a error message with a stack trace.
   *
   * @param str Log message.
   * @param t   Throwable.
   */

  public void logError(String str, Throwable t) {
  }

  /**
   * Logs a message at the FATAL level.
   *
   * @param str Log message.
   */

  public void logFatal(String str) {
  }

  /**
   * Logs a fatal message with a stack trace.
   *
   * @param str Log message.
   * @param t   Throwable.
   */

  public void logFatal(String str, Throwable t) {
  }

  /**
   * Logs a message.
   *
   * @param level Log message level.
   * @param str   Log message.
   */

  public void log(int level, String str) {
  }

  /**
   * Logs a message with a stack trace.
   *
   * @param level Log message level.
   * @param str   Log message.
   * @param t     Throwable.
   */

  public void log(int level, String str, Throwable t) {
  }

  /**
   * Returns true if debugging messages are enabled.
   *
   * @return True if debugging messages are enabled.
   */

  public boolean isDebuggingEnabled() {
    return false;
  }

  /**
   * Returns true if logger is enabled.
   *
   * @return True if logger is enabled.
   */

  public boolean isLoggerEnabled() {
    return loggerEnabled;
  }
}


