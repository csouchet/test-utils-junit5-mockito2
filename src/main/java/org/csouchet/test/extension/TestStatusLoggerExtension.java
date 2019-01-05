package org.csouchet.test.extension;

import java.util.Optional;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Log the name of the test at its beginning and at its end with its status
 *
 * @author SOUCHET Céline
 */
public class TestStatusLoggerExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

	@Override
	public void beforeTestExecution(final ExtensionContext context) throws Exception {
		LoggerFactory.getLogger(context.getTestClass()
				.get()
				.getName())
				.warn("Starting test: " + context.getTestMethod()
						.get()
						.getName());
	}

	@Override
	public void afterTestExecution(final ExtensionContext context) throws Exception {
		final Optional<Throwable> executionException = context.getExecutionException();
		final Logger logger = LoggerFactory.getLogger(context.getTestClass()
				.get()
				.getName());
		if (executionException.isPresent()) {
			logger.error("Failed test: " + context.getTestMethod()
					.get()
					.getName(), executionException.get());
		} else {
			logger.warn("Succeeded test: " + context.getTestMethod()
					.get()
					.getName());
		}
		logger.warn("-----------------------------------------------------------------------------------------------");
	}

}
