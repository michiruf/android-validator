package de.michiruf.android.validator;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

// TODO order attribute for rules!!!
// TODO addValidationPackage
// TODO addRule
// TODO test async validation
public class Validator {

    public static final String VALIDATOR_PACKAGE = Validator.class.getPackage().getName();
    public static final String VALIDATION_PACKAGE_ANNOTATIONS = VALIDATOR_PACKAGE + ".annotations";
    public static final String VALIDATION_PACKAGE_RULES = VALIDATOR_PACKAGE + ".rules";

    public static final String TAG = "Validator";

    private Object container;
    private ValidationListener listener;
    private AsyncTask<Void, Void, Void> asyncValidationTask;

    public Validator(Object subject) {
        this.container = subject;
    }

    /**
     * Returns the callback registered for this Validator.
     *
     * @return The callback, or null if one is not registered
     */
    public ValidationListener getValidationListener() {
        return this.listener;
    }

    /**
     * Register a callback to be invoked when {@code validate()} is called.
     *
     * @param listener The callback that will run
     */
    public void setValidationListener(ValidationListener listener) {
        this.listener = listener;
    }

    /**
     * Validates the given object and invokes the validation listener callback.
     *
     * @throws IllegalStateException If a {@link ValidationListener} is not registered.
     */
    public void validate() {
        if (this.getValidationListener() == null) {
            throw new IllegalStateException("Set a " + ValidationListener.class.getSimpleName() + " before attempting to validate.");
        }

        this._validate();
    }

    /**
     * Asynchronously validates all the {@link Rule}s against their {@link View}s. Subsequent calls to this method will cancel any pending asynchronous
     * validations and start a new one.
     *
     * @throws IllegalStateException If a {@link ValidationListener} is not registered.
     */
    public void validateAsync() {
        if (this.getValidationListener() == null) {
            throw new IllegalStateException("Set a " + ValidationListener.class.getSimpleName() + " before attempting to validate.");
        }

        if (this.asyncValidationTask != null) {
            this.asyncValidationTask.cancel(true);
            this.asyncValidationTask = null;
        }

        this.asyncValidationTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                Validator.this._validate();
                return null;
            }
        };

        this.asyncValidationTask.execute((Void[]) null);
    }

    // TODO: this method should return the failed combinations to get highlighted when all validation is done!
    protected void _validate() {
        Map<View, Rule> failedViewRuleCombinations = new HashMap<>();

        for (FieldRulesAssociation association : FieldRulesAssociationsBuilder.get(this.container)) {
            for (Rule rule : association.rules) {
                View view = association.getViewForObject(this.container);
                if (!rule.isValid(view)) {
                    failedViewRuleCombinations.put(view, rule);
                }
            }
        }

        if (failedViewRuleCombinations.size() == 0) {
            this.getValidationListener().onValidationSucceeded();
        } else {
            for (Entry<View, Rule> entry : failedViewRuleCombinations.entrySet()) {
                this.getValidationListener().onValidationFailed(entry.getKey(), entry.getValue());
            }
        }
    }

    protected static class FieldRulesAssociation {

        private Field field;
        private List<Rule> rules;

        private FieldRulesAssociation() {
            this.rules = new ArrayList<>();
        }

        protected View getViewForObject(Object object) {
            try {
                this.field.setAccessible(true);
                Object fieldObject = this.field.get(object);

                if (fieldObject instanceof View) {
                    return (View) this.field.get(object);
                }
            } catch (IllegalAccessException | IllegalArgumentException e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }

            return null;
        }

    }

    protected static class FieldRulesAssociationsBuilder {

        private static Map<String, List<FieldRulesAssociation>> cachedFieldRuleAssociations;

        static {
            cachedFieldRuleAssociations = new HashMap<>();
        }

        private static boolean isCached(Object object) {
            return cachedFieldRuleAssociations.containsKey(object.getClass().getName());
        }

        public static List<FieldRulesAssociation> get(Object object) {
            if (!isCached(object)) {
                List<FieldRulesAssociation> fieldRuleAssociations = build(object);
                cachedFieldRuleAssociations.put(object.getClass().getName(), fieldRuleAssociations);
            }

            return cachedFieldRuleAssociations.get(object.getClass().getName());
        }

        private static List<FieldRulesAssociation> build(Object object) {
            Log.i(TAG, "STARTED BUILDING FIELD CACHE");
            List<FieldRulesAssociation> fieldRulesAssociations = new ArrayList<>();

            for (Field field : object.getClass().getDeclaredFields()) {
                try {
                    FieldRulesAssociation association = null;

                    for (Annotation annotation : field.getAnnotations()) {
                        if (AnnotationRuleFactory.isValidationAnnotation(annotation)) {
                            if (association == null) {
                                association = new FieldRulesAssociation();
                                association.field = field;
                            }

                            Rule rule = AnnotationRuleFactory.getRule(annotation);
                            association.rules.add(rule);
                        }
                    }

                    if (association != null) {
                        fieldRulesAssociations.add(association);
                    }
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                }
            }

            return fieldRulesAssociations;
        }

    }

    protected static class AnnotationRuleFactory {

        public static boolean isValidationAnnotation(Annotation annotation) {
            return annotation.annotationType().getName().startsWith(VALIDATION_PACKAGE_ANNOTATIONS);
        }

        public static Rule getRule(Annotation annotation) {
            try {
                Object ruleObject = Class.forName(VALIDATION_PACKAGE_RULES + "." + annotation.annotationType().getSimpleName()).newInstance();
                if (ruleObject instanceof Rule) {
                    Rule rule = (Rule) ruleObject;
                    rule.setAnnotation(annotation);
                    return rule;
                } else {
                    Log.e(TAG, "Your rule object must be a subclass of Rule");
                }
            } catch (ClassNotFoundException e) {
                Log.e(TAG, "You must declare a rule for your annotation " + annotation.annotationType().getName());
                Log.e(TAG, Log.getStackTraceString(e));
            } catch (InstantiationException | IllegalAccessException e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }

            return null;
        }

    }

    /**
     * Interface definition for a callback to be invoked when {@code validate()} is called.
     */
    public interface ValidationListener {

        /**
         * Called when all the {@link Rule}s added to this Validator are valid.
         */
        void onValidationSucceeded();

        /**
         * Called if any of the {@link Rule}s fail.
         *
         * @param failedView The {@link View} that did not pass validation.
         * @param failedRule The failed {@link Rule} associated with the {@link View}.
         */
        void onValidationFailed(View failedView, Rule failedRule);
    }
}
