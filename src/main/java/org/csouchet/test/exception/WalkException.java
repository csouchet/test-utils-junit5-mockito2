/**
 *
 */
package org.csouchet.test.exception;

/**
 * Throws when an error occurs while walking folder
 *
 * @author SOUCHET Céline
 *
 */
public class WalkException extends Exception {

	private static final long serialVersionUID = 2453524504869173391L;

	/**
	 * @param message
	 *            The message
	 * @param cause
	 *            The root exception
	 */
	public WalkException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 *            The message
	 */
	public WalkException(final String message) {
		super(message);
	}

	/**
	 * @param cause
	 *            The root exception
	 */
	public WalkException(final Throwable cause) {
		super(cause);
	}

}
