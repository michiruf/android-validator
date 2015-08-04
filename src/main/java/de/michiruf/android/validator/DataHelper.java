package de.michiruf.android.validator;

import android.view.View;
import android.widget.TextView;

public class DataHelper {

    public static String getString(View view) {
        if (TextView.class.isAssignableFrom(view.getClass())) {
            return ((TextView) view).getText().toString();
        }

        return null;
    }

    public static int getInteger(View view) throws EmptyException {
        if (TextView.class.isAssignableFrom(view.getClass())) {
            try {
                return Integer.parseInt(((TextView) view).getText().toString());
            } catch (NumberFormatException e) {
                throw new EmptyException();
            }
        }

        return 0;
    }

    public static long getLong(View view) throws EmptyException {
        if (TextView.class.isAssignableFrom(view.getClass())) {
            try {
                return Long.parseLong(((TextView) view).getText().toString());
            } catch (NumberFormatException e) {
                throw new EmptyException();
            }
        }

        return 0;
    }

    public static float getFloat(View view) throws EmptyException {
        if (TextView.class.isAssignableFrom(view.getClass())) {
            try {
                return Float.parseFloat(((TextView) view).getText().toString());
            } catch (NumberFormatException e) {
                throw new EmptyException();
            }
        }

        return 0;
    }

    public static double getDouble(View view) throws EmptyException {
        if (TextView.class.isAssignableFrom(view.getClass())) {
            try {
                return Double.parseDouble(((TextView) view).getText().toString());
            } catch (NumberFormatException e) {
                throw new EmptyException();
            }
        }

        return 0;
    }

    public static class EmptyException extends Exception {
        private static final long serialVersionUID = 7169061564382203478L;
    }

}
