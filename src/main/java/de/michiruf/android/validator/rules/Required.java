package de.michiruf.android.validator.rules;

import android.view.View;

import java.lang.annotation.Annotation;

import de.michiruf.android.validator.DataHelper;
import de.michiruf.android.validator.Rule;

public class Required extends Rule {

    private de.michiruf.android.validator.annotations.Required annotation;

    @Override
    public void setAnnotation(Annotation annotation) {
        this.annotation = (de.michiruf.android.validator.annotations.Required) annotation;
    }

    @Override
    public boolean isValid(View view) {
        String data = DataHelper.getString(view);

        if (this.annotation.trim()) {
            data = data.trim();
        }

        return data.length() != 0;
    }

}
