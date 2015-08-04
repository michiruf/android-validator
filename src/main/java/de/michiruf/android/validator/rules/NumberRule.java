package de.michiruf.android.validator.rules;

import android.view.View;

import java.lang.annotation.Annotation;

import de.michiruf.android.validator.DataHelper;
import de.michiruf.android.validator.Rule;

public class NumberRule extends Rule {

    private de.michiruf.android.validator.annotations.NumberRule annotation;

    @Override
    public void setAnnotation(Annotation annotation) {
        this.annotation = (de.michiruf.android.validator.annotations.NumberRule) annotation;
    }

    @Override
    public boolean isValid(View view) {
        try {
            switch (this.annotation.type()) {
                case INTEGER:
                    int intValue = DataHelper.getInteger(view);
                    return intValue >= this.annotation.min() && intValue <= this.annotation.max();
                case LONG:
                    long longValue = DataHelper.getLong(view);
                    return longValue >= this.annotation.min() && longValue <= this.annotation.max();
                case FLOAT:
                    float floatValue = DataHelper.getFloat(view);
                    return floatValue >= this.annotation.min() && floatValue <= this.annotation.max();
                case DOUBLE:
                    double doubleValue = DataHelper.getDouble(view);
                    return doubleValue >= this.annotation.min() && doubleValue <= this.annotation.max();
            }
        } catch (DataHelper.EmptyException e) {
        }

        return false;
    }

}
