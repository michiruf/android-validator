package de.michiruf.android.validator.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NumberRule {

    /**
     * @return the type for the number rule
     */
    public NumberType type();

    /**
     * Default is the maximum value of integer.
     *
     * @return maximum value for this field
     */
    public double max() default Double.MAX_VALUE;

    /**
     * Default in the minimum value of integer
     *
     * @return minimum value for the field
     */
    public double min() default Double.MIN_VALUE;

    /**
     * Public enumeration to define the type for this number rule.
     *
     * @author Michi
     */
    public enum NumberType {
        INTEGER, LONG, FLOAT, DOUBLE
    }

}
