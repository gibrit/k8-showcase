/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.saltuk.core;


import io.vertx.core.Vertx;
import io.vertx.core.VertxException;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 *
 * @author  Saltık Buğra Avcı ben@saltuk.org
 */
public class StringUtils {

    public final static Locale TURKISH_LOCALE = Locale.forLanguageTag("tr-TR");
    ;
    private final static Logger log = LoggerFactory.getLogger(StringUtils.class);
    private final static Pattern passwordValidater = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$");
    
    private final static String HASH_TAG = "_PASSWORD_123_LOGTAIL_";


    public static String wordsUpper(String name) {
        final String[] words = name.split(" ");
        StringBuilder output = new StringBuilder();
        for (String word : words) {
            output.append(firstUpper(word, true)).append(" ");
        }
        return output.toString().trim();

    }

    public static String base64BasicHeader(String consumerId, String consumerKey) {
        return StringUtils.append("Basic ", base64Encode(StringUtils.append(consumerId, ":", consumerKey)));
    }

    public static Buffer base64DecodeURL(String data) {
        return Buffer.buffer(Base64.getUrlDecoder().decode(data));
    }

    public static String base64EncodeURL(String data) {
        return Base64.getUrlEncoder().encodeToString(data.getBytes());
    }

    public static Buffer base64Decode(String data) {
        return Buffer.buffer(Base64.getDecoder().decode(data));
    }

    public static String base64Encode(String data) {
        return Base64.getEncoder().encodeToString(data.getBytes());
    }

    /**
     * Split String with delimeter
     *
     * @param text
     * @param delimeter
     * @return
     */
    public static List<String> splitString(String text, String delimeter) {
        List<String> result = new ArrayList<String>();
        int pos = 0, end;
        while ((end = text.indexOf(delimeter, pos)) >= 0) {
            result.add(text.substring(pos, end));
            pos = end + 1;
        }
        result.add(text.substring(pos));
        return result;
    }

    public static String slug(String value) {
        return Normalizer.normalize(value.toLowerCase().replace(".", "").trim().replace(",", " ").trim().replaceAll("\\s{2,}", " "), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").replaceAll("\\p{M}", "").replace(" ", "-").replaceAll("-+", "-").toLowerCase();
    }

    public static boolean isNumeric(String value) {
        try {
            NumberFormat.getInstance().parse(value);
            return true;
        } catch (ParseException ex) {
            return false;
        }
    }

    public static Number toNumber(String value) {
        try {
            return NumberFormat.getInstance().parse(value);
        } catch (ParseException ex) {
            return null;
        }
    }

    public static Number toNumber(String field, JsonObject value) {
        final Object fieldVal = value.getValue(field, null);
        if (fieldVal != null && fieldVal instanceof Number) {
            return (Number) fieldVal;
        }
        return null;
    }

    public static Number toNumber(Object value) {
        try {
            return NumberFormat.getInstance().parse(value.toString());
        } catch (ParseException ex) {
            return null;
        }
    }

    public static Double toDouble(String value) {
        try {
            return Double.valueOf(value);
        } catch (Exception ex) {
            return null;
        }
    }

    public static Float toFloat(String value) {
        try {
            return Float.valueOf(value);
        } catch (Exception ex) {
            return null;
        }
    }

    public static Integer toInteger(String value) {
        try {
            return Integer.valueOf(value);
        } catch (Exception ex) {
            return null;
        }
    }

    public static Long toLong(String value) {
        try {
            return Long.valueOf(value);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Converts Character Array to Byte Array
     *
     * @param val
     * @return
     */
    public static byte[] byteArrayFromCharacterArray(Character[] val) {
        byte[] chars = new byte[val.length];
        int i = 0;
        while (i < val.length) {
            chars[i] = (byte) val[i].charValue();
            i++;
        }
        return chars;
    }

    /**
     * Contains String
     *
     * @param texttoAppend
     * @param delimeter
     * @param keys
     * @return
     */
    public static String stringBuild(String texttoAppend, String delimeter, Object... keys) {
        StringBuilder sb = new StringBuilder();
        if (texttoAppend != null) {
            sb.append(texttoAppend);
            sb.append(delimeter);
        }
        for (Object str : keys) {
            sb.append(str);
            sb.append(delimeter);
        }
        return sb.toString();
    }

    public static String stringBuild(String delimeter, Object... keys) {
        return stringBuild(null, delimeter, keys);
    }

    public static String getSubString(String value, String starterPhase, String endPhase) {
        value = value.trim();
        int start = value.indexOf(starterPhase);
        int end = value.indexOf(endPhase);

        if (((end - start) == 0) || (end - start) == 1 || value.length() == 2) {
            return "";
        } else {
            return value.substring(value.indexOf(starterPhase), value.indexOf(endPhase));
        }
    }

    public static boolean isStartsAndEndsWith(String value, String startPhase, String endPhase) {
        value = value.trim();
        return (value.startsWith(startPhase) && value.endsWith(endPhase));
    }

    public static String append(Object... appends) {
        StringBuilder bf = new StringBuilder();
        for (Object val : appends) {
            if (val != null) {
                bf.append(val);
            }
        }
        return bf.toString();
    }

    public static StringBuilder newLine(final StringBuilder output, int tabCount) {
        output.append("\n");
        int i = 0;
        while (tabCount > i) {
            output.append("\t");
            i++;
        }
        return output;
    }

    public static String newLine(int tabCount) {
        final StringBuilder output = new StringBuilder();
        output.append("\n");
        int i = 0;
        while (tabCount > i) {
            output.append("\t");
            i++;
        }
        return output.toString();
    }

    public static StringBuilder newLine(final StringBuilder output) {
        return newLine(output, 0);
    }

    public static String newLine() {
        return newLine(0);
    }

    public static boolean isEmpty(String data) {
        return (data == null) || (data.trim().equals(""));
    }


    public static boolean isValidPassword(String password) {
        return password != null ? passwordValidater.matcher(password).matches() : false;
    }

    public static String parseAsJavaName(String name) {
        final String[] split = name.replace(" ", "-").replaceAll("[^a-zA-Z\\-]+", "").split("-");
        StringBuilder output = new StringBuilder();
        int i = 0;
        while (i < split.length) {
            String part = split[i];
            if (i > 0) {
                part = firstUpper(part);
            }
            output.append(part);
            i++;
        }
        return output.toString();
    }

    public static String parseAsJavaEnumName(String name) {
        return parseAsJavaName(name).replaceAll("([A-Z])", "_$1").toUpperCase(Locale.ENGLISH);
    }

    /**
     * { Adds Start to start And End Value to end of the Given String
     *
     * @param value
     * @param start
     * @param end
     * @return
     */
    public static String appendStartEnd(String value, String start, String end) {
        return append(start, value, end);
    }

    protected static int countText(int current, String text, String searchText) {
        final int index = text.indexOf(searchText);
        if ((index == -1)) {
            return current;
        } else {
            current++;
            return countText(current, text.substring(index + 1), searchText);
        }

    }

    public static int count(String value, String searchVal) {
        return countText(0, value, searchVal);
    }

    public static String asReduxMethod(String methodName) {
        String[] parts = methodName.toLowerCase().split("\\.");
        final StringBuilder builder = new StringBuilder();
        boolean isFirst = true;
        for (final String part : parts) {
            if (!isFirst) {
                builder.append("_");
            }
            final String val = isFirst ? firstLower(part) : firstUpper(part, true);
            builder.append(val);
            isFirst = false;
        }
        return builder.toString();
    }

    public static String changeCharacter(String value, int pos, String replaceValue) {
        StringBuilder bf = new StringBuilder(value);
        char[] charList = replaceValue.toCharArray();
        for (char val : charList) {
            bf.setCharAt(pos, val);
            pos++;
        }
        return bf.toString();
    }

    /**
     * return the Short name of Given Class
     *
     * @param clazz
     * @return
     */
    public static String getShortClassName(Class clazz) {
        return getShortClassName(clazz.getName());
    }

    /**
     * Gets Short name of Given className
     *
     * @param className
     * @return
     */
    public static String getShortClassName(String className) {
        return className.substring(className.lastIndexOf(".") + 1);
    }

    /**
     * Returns entity Class Name
     *
     * @param className
     * @return
     */
    public static String getEntityName(String className) {
        return firstLower(getShortClassName(className));
    }

    /**
     * Returns Entity Class Name
     *
     * @param clazz
     * @return
     */
    public static String getEntityName(Class clazz) {
        return getEntityName(clazz.getName());
    }

    /**
     * Returns the String first Lower
     *
     * @param value
     * @return
     */
    public static String firstLower(String value) {
        if (isEmpty(value)) {
            return value;
        }
        return (value.length() > 1) ? append(value.substring(0, 1).toLowerCase(), value.substring(1)) : value.toLowerCase();
    }

    /**
     * Returns the String first Upper others Lower
     *
     * @param value
     * @return
     */
    public static String firstUpper(String value) {
        return firstUpper(value, false);
    }

    /**
     * Returns The String Upper
     *
     * @param value
     * @param allLowerAfterFirst
     * @return
     */
    public static String firstUpper(String value, boolean allLowerAfterFirst) {
        if (isEmpty(value)) {
            return value;
        }
        return value.length() > 1 ? allLowerAfterFirst ? append(value.substring(0, 1).toUpperCase(), value.substring(1).toLowerCase()) : append(value.substring(0, 1).toUpperCase(), value.substring(1)) : value.toUpperCase();
    }

    public static String firstUpper(String value, Locale locale) {
        return firstUpper(value, locale, false);
    }

    public static String firstUpper(String value, Locale locale, boolean allLowerAfterFirst) {
        if (isEmpty(value)) {
            return value;
        }
        return value.length() > 1 ? allLowerAfterFirst ? append(value.substring(0, 1).toUpperCase(locale), value.substring(1).toLowerCase(locale)) : append(value.substring(0, 1).toUpperCase(locale), value.substring(1)) : value.toUpperCase(locale);
    }

    public static String firstUpperWords(String value) {
        return firstUpperWords(value, false);
    }

    public static String firstUpperWords(String value, boolean allLowerAfterFirst) {
        final String[] items = value.split(" ");
        final StringBuilder output = new StringBuilder();
        for (String val : items) {
            output.append(firstUpper(val, allLowerAfterFirst)).append(" ");
        }
        return output.toString().trim();
    }

    public static String firstUpperWords(String value, Locale locale) {
        return firstUpperWords(value, locale, false);
    }

    public static String firstUpperWords(String value, Locale locale, boolean allLowerAfterFirst) {
        final String[] items = value.split(" ");
        final StringBuilder output = new StringBuilder();
        for (String val : items) {
            output.append(firstUpper(val, locale, allLowerAfterFirst)).append(" ");
        }
        return output.toString().trim();
    }

    /**
     * *
     * Joins Array Object With given Join String
     *
     * @param joiner
     * @param segments
     * @return
     */
    public static String join(final String joiner, final LinkedList segments) {
        return join(joiner, segments.toArray());
    }

    /**
     * *
     * Joins Array Object With given Join String
     *
     * @param joiner
     * @param segments
     * @return
     */
    public static String join(final String joiner, final Set segments) {
        return join(joiner, segments.toArray());
    }

    /**
     * Joins Array Object With given Join String
     *
     * @param joiner
     * @param segments
     * @return
     */
    public static String join(final String joiner, final List segments) {
        return join(joiner, segments.toArray());
    }

    /**
     * Joins Array Object With given Join String
     *
     * @param joiner
     * @param segments
     * @return
     */
    public static String join(final String joiner, final Object[] segments) {
        final StringBuilder strB = new StringBuilder();
        boolean isFirst = true;
        for (final Object obj : segments) {
            if (!isFirst) {
                strB.append(joiner);
            }
            isFirst = false;
            strB.append(obj);
        }
        return strB.toString();
    }

    /**
     * Joins Array Object With given Join String
     *
     * @param joiner
     * @param segments
     * @return
     */
    public static String joinLower(final String joiner, final Object[] segments) {
        final StringBuilder strB = new StringBuilder();
        boolean isFirst = true;
        for (final Object obj : segments) {
            if (!isFirst) {
                strB.append(joiner);
            }
            isFirst = false;
            strB.append(obj.toString().toLowerCase());
        }
        return strB.toString();
    }

    

    public static String checkPath(String path) {
        return checkPath(path, false);
    }

    public static String checkPath(String path, boolean urldecode) {
        if (path == null) {
            return "/";
        }

        if (path.charAt(0) != '/') {
            path = "/" + path;
        }

        try {
            StringBuilder result = new StringBuilder(path.length());

            for (int i = 0; i < path.length(); i++) {
                char c = path.charAt(i);

                // we explicitly ignore the + sign as it should not be translated to
                // space within a path as per RFC3986 we only consider percent encoded values
                if (c == '/') {
                    if (i == 0 || result.charAt(result.length() - 1) != '/') {
                        result.append(c);
                    }
                } else if (urldecode && c == '%') {
                    i = processEscapeSequence(path, result, i);
                } else if (c == '.') {
                    if (i == 0 || result.charAt(result.length() - 1) != '.') {
                        result.append(c);
                    } else {
                        result.deleteCharAt(result.length() - 1);
                    }
                } else {
                    result.append(c);
                }
            }

            return result.toString();

        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Processes a escape sequence in path
     *
     * @param path The original path
     * @param result The result of unescaping the escape sequence (and removing
     * dangerous constructs)
     * @param i The index of path where the escape sequence begins
     * @return The index of path where the escape sequence ends
     * @throws UnsupportedEncodingException If the escape sequence does not
     * represent a valid UTF-8 string
     */
    private static int processEscapeSequence(String path, StringBuilder result, int i) throws UnsupportedEncodingException {
        Buffer buf = Buffer.buffer(2);
        do {
            if (i >= path.length() - 2) {
                throw new IllegalArgumentException("Invalid position for escape character: " + i);
            }
            int unescaped = Integer.parseInt(path.substring(i + 1, i + 3), 16);
            if (unescaped < 0) {
                throw new IllegalArgumentException("Invalid escape sequence: " + path.substring(i, i + 3));
            }
            buf.appendByte((byte) unescaped);
            i += 3;
        } while (i < path.length() && path.charAt(i) == '%');

        String escapedSeq = new String(buf.getBytes(), "UTF-8");

        for (int j = 0; j < escapedSeq.length(); j++) {
            char c = escapedSeq.charAt(j);
            if (c == '/') {
                if (j == 0 || result.charAt(result.length() - 1) != '/') {
                    result.append(c);
                }
            } else if (c == '.') {
                if (j == 0 || result.charAt(result.length() - 1) != '.') {
                    result.append(c);
                } else {
                    result.deleteCharAt(result.length() - 1);
                }
            } else {
                result.append(c);
            }
        }
        return i - 1;
    }

    /**
     * Replaces characters that may be confused by a HTML parser with their
     * equivalent character entity references.
     *
     * Any data that will appear as text on a web page should be be escaped.
     * This is especially important for data that comes from untrusted sources
     * such as Internet users. A common mistake in CGI programming is to ask a
     * user for data and then put that data on a web page. For example:
     * <pre>
     * Server: What is your name?
     * User: &lt;b&gt;Joe&lt;b&gt;
     * Server: Hello <b>Joe</b>, Welcome</pre> If the name is put on the page
     * without checking that it doesn't contain HTML code or without sanitizing
     * that HTML code, the user could reformat the page, insert scripts, and
     * control the the content on your web server.
     *
     * This method will replace HTML characters such as &gt; with their HTML
     * entity reference (&amp;gt;) so that the html parser will be sure to
     * interpret them as plain text rather than HTML or script.
     *
     * This method should be used for both data to be displayed in text in the
     * html document, and data put in form elements. For example:<br>
     * <code>&lt;html&gt;&lt;body&gt;<i>This in not a &amp;lt;tag&amp;gt; in
     * HTML</i>&lt;/body&gt;&lt;/html&gt;</code><br>
     * and<br>
     * <code>&lt;form&gt;&lt;input type="hidden" name="date" value="<i>This data
     * could be &amp;quot;malicious&amp;quot;</i>"&gt;&lt;/form&gt;</code><br>
     * In the second example, the form data would be properly be resubmitted to
     * your cgi script in the URLEncoded format:<br>
     * <code><i>This data could be %22malicious%22</i></code>
     *
     * @param s String to be escaped
     * @return escaped String
     * @throws NullPointerException if s is null.
     *
     */
    public static String cleanHtml(String htmlValue) {
        int length = htmlValue.length();
        int newLength = length;
        boolean someCharacterEscaped = false;
        // first check for characters that might
        // be dangerous and calculate a length
        // of the string that has escapes.
        for (int i = 0; i < length; i++) {
            char c = htmlValue.charAt(i);
            int cint = 0xffff & c;
            if (cint < 32) {
                switch (c) {
                    case '\r':
                    case '\n':
                    case '\t':
                    case '\f': {
                    }
                    break;
                    default: {
                        newLength -= 1;
                        someCharacterEscaped = true;
                    }
                }
            } else {
                switch (c) {
                    case '\"': {
                        newLength += 5;
                        someCharacterEscaped = true;
                    }
                    break;
                    case '&':
                    case '\'': {
                        newLength += 4;
                        someCharacterEscaped = true;
                    }
                    break;
                    case '<':
                    case '>': {
                        newLength += 3;
                        someCharacterEscaped = true;
                    }
                    break;
                }
            }
        }
        if (!someCharacterEscaped) {
            // nothing to escape in the string
            return htmlValue;
        }
        StringBuilder sb = new StringBuilder(newLength);
        for (int i = 0; i < length; i++) {
            char c = htmlValue.charAt(i);
            int cint = 0xffff & c;
            if (cint < 32) {
                switch (c) {
                    case '\r':
                    case '\n':
                    case '\t':
                    case '\f': {
                        sb.append(c);
                    }
                    break;
                    default: {
                        // Remove this character
                    }
                }
            } else {
                switch (c) {
                    case '\"': {
                        sb.append("&quot;");
                    }
                    break;
                    case '\'': {
                        sb.append("&#39;");
                    }
                    break;
                    case '&': {
                        sb.append("&amp;");
                    }
                    break;
                    case '<': {
                        sb.append("&lt;");
                    }
                    break;
                    case '>': {
                        sb.append("&gt;");
                    }
                    break;
                    default: {
                        sb.append(c);
                    }
                }
            }
        }
        return sb.toString();
    }

    public static ClassLoader getClassLoader() {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return classLoader == null ? StringUtils.class.getClassLoader() : classLoader;
    }

    public static Buffer readResourceToBuffer(String resource) throws Exception {
        ClassLoader cl = getClassLoader();
        try {
            Buffer buffer = Buffer.buffer();
            try (InputStream in = cl.getResourceAsStream(resource)) {
                if (in == null) {
                    return null;
                }
                int read;
                byte[] data = new byte[4096];
                while ((read = in.read(data, 0, data.length)) != -1) {
                    if (read == data.length) {
                        buffer.appendBytes(data);
                    } else {
                        byte[] slice = new byte[read];
                        System.arraycopy(data, 0, slice, 0, slice.length);
                        buffer.appendBytes(slice);
                    }
                }
            }
            return buffer;
        } catch (IOException ioe) {
            throw new Exception(ioe);
        }
    }

    /*
     Reads from file or classpath
     */
    public static String readFileToString(Vertx vertx, String resource) {
        try {
            Buffer buff = vertx.fileSystem().readFileBlocking(resource);
            return buff.toString();
        } catch (Exception e) {
            throw new VertxException(e);
        }
    }

    public static String readResourceToString(String resource) throws Exception {
        Buffer buff = readResourceToBuffer(resource);
        return buff == null ? null : buff.toString();
    }

    public static List<String> getSortedAcceptableMimeTypes(String acceptHeader) {
        // accept anything when accept is not present
        if (acceptHeader == null) {
            return Collections.emptyList();
        }

        // parse
        String[] items = acceptHeader.split(" *, *");
        // sort on quality
        Arrays.sort(items, ACCEPT_X_COMPARATOR);

        List<String> list = new ArrayList<>(items.length);

        for (String item : items) {
            // find any ; e.g.: "application/json;q=0.8"
            int space = item.indexOf(';');

            if (space != -1) {
                list.add(item.substring(0, space));
            } else {
                list.add(item);
            }
        }

        return list;
    }

    private static final Comparator<String> ACCEPT_X_COMPARATOR = new Comparator<String>() {
        float getQuality(String s) {
            if (s == null) {
                return 0;
            }

            String[] params = s.split(" *; *");
            for (int i = 1; i < params.length; i++) {
                String[] q = params[1].split(" *= *");
                if ("q".equals(q[0])) {
                    return Float.parseFloat(q[1]);
                }
            }
            return 1;
        }

        @Override
        public int compare(String o1, String o2) {
            float f1 = getQuality(o1);
            float f2 = getQuality(o2);
            if (f1 < f2) {
                return 1;
            }
            if (f1 > f2) {
                return -1;
            }
            return 0;
        }
    };

    public static String stringValue(Object obj) {
        return obj != null ? String.valueOf(obj) : null;
    }

    public static Set<String> parseParameters(String parameterHolder) {
        Matcher matcher = Pattern.compile("\\{:([A-Za-z][A-Za-z0-9_]*)\\}").matcher(parameterHolder);
        final Set<String> params = new HashSet();
        while (matcher.find()) {
            final String paramName = matcher.group();
            final String parameter = paramName.substring(2, paramName.length() - 1);
            params.add(parameter);
        }
        return params;
    }

    public static String pathParameterString(String parametedString, JsonObject values) {
        String parsedString = parametedString;
        final Matcher matcher = Pattern.compile("\\{:([A-Za-z][A-Za-z0-9_]*)\\}").matcher(parametedString);

        while (matcher.find()) {
            final String paramName = matcher.group();
            final String parameter = paramName.substring(2, paramName.length() - 1);
            final String value = String.valueOf(values.getValue(parameter, parameter));
            parsedString = parsedString.replaceAll(StringUtils.append("\\{:", parameter, "\\}"), value.replace("-", "_").replace(" ", "-"));
        }
        return parsedString;
    }

    public static String parseParametedString(String parametedString, JsonObject values) {
        String parsedString = parametedString;
        final Matcher matcher = Pattern.compile("\\{:([A-Za-z][A-Za-z0-9_]*)\\}").matcher(parametedString);

        while (matcher.find()) {
            final String paramName = matcher.group();
            final String parameter = paramName.substring(2, paramName.length() - 1);
            final String value = String.valueOf(values.getValue(parameter, parameter));
            parsedString = parsedString.replaceAll(StringUtils.append("\\{:", parameter, "\\}"), value);
        }
        return parsedString;
    }

    public static String parseParametedReact(String parametedString, JsonObject values) {
        String parsedString = parametedString;
        final Matcher matcher = Pattern.compile("\\{:([A-Za-z][A-Za-z0-9_]*)\\}").matcher(parametedString);

        while (matcher.find()) {
            final String paramName = matcher.group();
            final String parameter = paramName.substring(2, paramName.length() - 1);
            final String value = StringUtils.append("${", String.valueOf(values.getValue(parameter, parameter)), "}");
            parsedString = parsedString.replaceAll(StringUtils.append("\\{:", parameter, "\\}"), value);
        }
        return append("{`", parsedString, "`}");
    }

    public static <T> Set<T> commons(T[] base, T[] search) {
        Set<T> baseSet = new HashSet<>(Arrays.asList(base));
        Set<T> searchSet = new HashSet<>(Arrays.asList(search));
        baseSet.retainAll(searchSet);

        return baseSet;
    }

    public static <T> Set<T> commons(Set<T> base, Set<T> search) {
        if (search != null) {
            base.removeAll(search);
        }
        return base;
    }

    public static void appendStartEnd(String name, String _, String fromTable, String _0, String toTable) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
