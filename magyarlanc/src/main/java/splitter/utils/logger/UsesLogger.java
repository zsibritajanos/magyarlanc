package splitter.utils.logger;

/**
 * Interface for using a logger.
 * <p>
 * <p>
 * A class which uses a logger can implement this interface to
 * standardize on the methods for getting and setting a logger.
 * </p>
 */

public interface UsesLogger {
  /**
   * Get the logger.
   *
   * @return The logger.
   */

  public Logger getLogger();

  /**
   * Set the logger.
   *
   * @param logger The logger.
   */

  public void setLogger(Logger logger);
}
