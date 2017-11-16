package ru.uu.voda;

import android.os.Build;
import android.text.Html;
import android.text.Spanned;

public class HtmlCompat {
    /** Класс для совместимости отображения Html в разных версиях Android*/
    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String source) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(source);
        }
    }
}
