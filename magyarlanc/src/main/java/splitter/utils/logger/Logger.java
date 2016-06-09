package splitter.utils.logger;

/**
 * Interface for a logger.
 * <p>
 * <p>
 * A logger is used to write messages to a log file.
 * Each message has an associated level of severity.
 * In increasing order these are:
 * </p>
 * <p>
 * <ol>
 * <li>debug</li>
 * <li>information</li>
 * <li>warning</li>
 * <li>error</li>
 * <li>fatal</li>
 * </p>
 * <p>
 */

public interface Logger {
  /**
   * Log a message at the Debug level.
   *
   * @param str Log message.
   */

  public void logDebug(String str);

  /**
   * Log a message at the Info level.
   *
   * @param str Log message.
   */

  public void logInfo(String str);

  /**
   * Log a message at the Warn level.
   *
   * @param str Log message.
   */

  public void logWarning(String str);

  /**
   * Log a message at the Error level.
   *
   * @param str Log message.
   */

  public void logError(String str);

  /**
   * Logs a error message with a stack trace.
   *
   * @param str Log message.
   * @param t   Throwable.
   */

  public void logError(String str, Throwable t);

  /**
   * Log a message at the Fatal level.
   *
   * @param str Log message.
   */

  public void logFatal(String str);

  /**
   * Logs a fatal message with a stack trace.
   *
   * @param str Log message.
   * @param t   Throwable.
   */

  public void logFatal(String str, Throwable t);

  /**
   * Log a message at a specified level.
   *
   * @param level Message level.
   * @param str   Log message.
   */

  public void log(int level, String str);

  /**
   * Log a message with a stack trace.
   *
   * @param level Message level.
   * @param str   Log message.
   * @param t     Throwable.
   */

  public void log(int level, String str, Throwable t);

  /**
   * Returns true if debugging messages are enabled.
   *
   * @return True if debugging messages are enabled.
   */

  public boolean isDebuggingEnabled();

  /**
   * Returns true if logger is enabled.
   *
   * @return True if logger is enabled.
   */

  public boolean isLoggerEnabled();

  /**
   * Terminate.logger.
   */

  public void terminate();
}