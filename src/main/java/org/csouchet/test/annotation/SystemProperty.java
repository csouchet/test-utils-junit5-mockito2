package org.csouchet.test.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.csouchet.test.extension.SystemPropertyExtension;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * To add a system property for a specific test
 *
 * @author SOUCHET Céline
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ExtendWith(SystemPropertyExtension.class)
public @interface SystemProperty {

	/**
	 * @return The name of the property
	 */
	String name();

	/**
	 * @return The value of the property
	 */
	String value();
}
