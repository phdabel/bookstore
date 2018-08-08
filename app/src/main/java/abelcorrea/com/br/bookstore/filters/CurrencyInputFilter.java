package abelcorrea.com.br.bookstore.filters;

import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CurrencyInputFilter implements InputFilter {

    private final Pattern currency;

    private final static String LOG_TAG = CurrencyInputFilter.class.getSimpleName();

    public CurrencyInputFilter(){
        currency = Pattern.compile("[0-9]{0," + (5-1) + "}+((\\.[0-9]{0," + (2-1) + "})?)||(\\.)?");
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        Matcher matcher = currency.matcher(dest);
        if(!matcher.matches())
            return "";
        return null;
    }

}
