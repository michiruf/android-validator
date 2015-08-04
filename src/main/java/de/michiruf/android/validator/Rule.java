package de.michiruf.android.validator;

import android.view.View;

import java.lang.annotation.Annotation;

public abstract class Rule {

    private String failureMessage;

    public String setFailureMessage() {
        return this.failureMessage;
    }

    public String getFailureMessage() {
        return this.failureMessage;
    }

    public abstract boolean isValid(View view);

    public abstract void setAnnotation(Annotation annotation);

}
