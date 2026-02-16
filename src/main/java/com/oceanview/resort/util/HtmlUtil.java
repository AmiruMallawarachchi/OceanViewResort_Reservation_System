package com.oceanview.resort.util;

/**
 * HTML escaping for XSS prevention when rendering user-controlled data in JSPs.
 */
public final class HtmlUtil {

    private HtmlUtil() {
    }

    /**
     * Escapes HTML special characters so the string can be safely embedded in HTML text or attributes.
     * Prevents XSS when displaying user input (e.g. guest name, search query).
     *
     * @param value the string to escape; may be null
     * @return escaped string, or empty string if null
     */
    public static String escape(String value) {
        if (value == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(value.length() + 16);
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '&':
                    sb.append("&amp;");
                    break;
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '"':
                    sb.append("&quot;");
                    break;
                case '\'':
                    sb.append("&#39;");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }
}
