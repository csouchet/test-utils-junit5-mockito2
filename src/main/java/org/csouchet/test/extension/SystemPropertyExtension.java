package org.csouchet.test.extension;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.csouchet.test.annotation.SystemProperty;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import lombok.Cleanup;

/**
 * The system property extension sets system properties before test execution
 * and unsets them after test execution.
 * <ul>
 * <li>If a new system property was added then it is removed after test
 * execution completes
 * <li>If an existing system property was overwritten then its original value is
 * reinstated after test execution completes
 * </ul>
 * <p>
 * System properties can be injected into your test as parameter injection into
 * a {@code @Test} method. For example:
 *
 * <pre>
 *
 * &#064;Test
 * &#064;SystemProperty(name = "property name", value = "property value")
 * public void testUsingSystemProperty() {
 * 	// ...
 * }
 * </pre>
 *
 * @author SOUCHET Céline
 */
public class SystemPropertyExtension implements AfterEachCallback, BeforeEachCallback, BeforeAllCallback, AfterAllCallback {

	private final Set<String> propertyNames = new HashSet<>();

	private final Map<String, String> oldValueOfProperties = new HashMap<>();

	/**
	 * If the current test class has a system property annotation(s) then
	 * retains a copy of pre-set values for reinstatement after test execution.
	 *
	 * @param extensionContext
	 *            The <em>context</em> in which the current test or container is
	 *            being executed
	 */
	@Override
	public void beforeAll(final ExtensionContext extensionContext) {
		setProperty(extensionContext);
	}

	/**
	 * Unset any system properties which were set in
	 * {@link #beforeAll(ExtensionContext)} and reinstate original value, if
	 * applicable.
	 *
	 * @param extensionContext
	 *            The <em>context</em> in which the current test or container is
	 *            being executed
	 */
	@Override
	public void afterAll(final ExtensionContext extensionContext) {
		restore();
	}

	/**
	 * If the current test method has a system property annotation(s) then
	 * retains a copy of pre-set values for reinstatement after test execution.
	 *
	 * @param extensionContext
	 *            The <em>context</em> in which the current test or container is
	 *            being executed
	 */
	@Override
	public void beforeEach(final ExtensionContext extensionContext) {
		setProperty(extensionContext);
	}

	/**
	 * Unset any system properties which were set in
	 * {@link #beforeEach(ExtensionContext)} and reinstate original value, if
	 * applicable.
	 *
	 * @param extensionContext
	 *            The <em>context</em> in which the current test or container is
	 *            being executed
	 */
	@Override
	public void afterEach(final ExtensionContext extensionContext) {
		restore();
	}

	private void setProperty(final ExtensionContext extensionContext) {
		final SystemProperty systemProperty = extensionContext.getTestMethod()
				.get()
				.getAnnotation(SystemProperty.class);

		final String propertyName = systemProperty.name();
		propertyNames.add(propertyName);

		if (System.getProperty(propertyName) != null) {
			oldValueOfProperties.put(propertyName, System.getProperty(propertyName));
		}

		System.setProperty(propertyName, systemProperty.value());
	}

	/**
	 * Reverse the system property 'sets' performed on behalf of this restore
	 * context.
	 * <p>
	 * For each entry in {@link #propertyNames}, if
	 * {@link #oldValueOfProperties} contains an entry then reset the system
	 * property with the value from {@link #oldValueOfProperties} otherwise just
	 * remove the system property for that property name.
	 */
	private void restore() {
		@Cleanup
		final Stream<String> stream = propertyNames.stream();
		stream.forEach(propertyName -> {
			if (oldValueOfProperties.containsKey(propertyName)) {
				// reinstate the original value
				System.setProperty(propertyName, oldValueOfProperties.get(propertyName));
			} else {
				// remove the (previously unset) property
				System.clearProperty(propertyName);
			}
		});
	}
}
