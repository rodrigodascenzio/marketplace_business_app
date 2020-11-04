package com.nuppin.company.Util;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.text.NumberFormat;
import java.util.Locale;

public abstract class MaskEditUtil {

    public static final String FORMAT_CPF = "###.###.###-##";
    public static final String FORMAT_FONE = "(###)####-#####";
    public static final String FORMAT_CEP = "#####-###";
    public static final String FORMAT_DATE = "##/##/####";
    public static final String FORMAT_HOUR = "##:##";
    public static final String FORMAT_CNPJ = "##.###.###/####-##";

    /**
     * Método que deve ser chamado para realizar a formatação
     *
     * @param ediTxt
     * @param mask
     * @return
     */
    public static TextWatcher mask(final EditText ediTxt, final String mask) {
        return new TextWatcher() {
            boolean isUpdating;
            String old = "";

            @Override
            public void afterTextChanged(final Editable s) {
            }

            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
            }

            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                final String str = MaskEditUtil.unmask(s.toString());
                String mascara = "";
                if (isUpdating) {
                    old = str;
                    isUpdating = false;
                    return;
                }
                int i = 0;
                for (final char m : mask.toCharArray()) {
                    if (m != '#' && str.length() > old.length()) {
                        mascara += m;
                        continue;
                    }
                    try {
                        mascara += str.charAt(i);
                    } catch (final Exception e) {
                        FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ediTxt.getContext()));
                        FirebaseCrashlytics.getInstance().recordException(e);
                        break;
                    }
                    i++;
                }
                isUpdating = true;
                ediTxt.setText(mascara);
                ediTxt.setSelection(mascara.length());
            }
        };
    }

    private static String unmask(final String s) {
        return s.replaceAll("[.]", "").replaceAll("[-]", "").replaceAll("[/]", "").replaceAll("[(]", "").replaceAll("[ ]", "").replaceAll("[:]", "").replaceAll("[)]", "");
    }

    public static TextWatcher monetario(final EditText ediTxt) {
        return new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    ediTxt.removeTextChangedListener(this);
                    String cleanString = s.toString().replaceAll("[^0-9]", "");
                    double parsed;
                    if (!cleanString.equals("")) {
                        parsed = Double.parseDouble(cleanString);
                        String formatted = NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format((parsed / 100));
                        current = formatted.replaceAll("[^.,0-9]", "");
                    } else {
                        String formatted = NumberFormat.getCurrencyInstance().format((0));
                        current = formatted.replaceAll("[^.,0-9]", "");
                    }
                    ediTxt.setText(current);
                    ediTxt.setSelection(current.length());
                    ediTxt.addTextChangedListener(this);
                }
            }

        };
    }
}