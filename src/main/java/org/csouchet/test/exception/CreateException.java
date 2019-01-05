/**
 *
 */
package org.csouchet.test.exception;

/**
 * Throws when an error occurs while creating something
 *
 * @author SOUCHET Céline
 *
 */
public class CreateException extends Exception {

	private static final long serialVersionUID = 2453524504869173391L;

	/**
	 * @param message
	 *            The message
	 * @param cause
	 *            The root exception
	 */
	public CreateException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 *            The message
	 */
	public CreateException(final String message) {
		super(message);
	}

	/**
	 * @param cause
	 *            The root exception
	 */
	public CreateException(final Throwable cause) {
		super(cause);
	}

}
