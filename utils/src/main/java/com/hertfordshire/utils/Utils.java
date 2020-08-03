package com.hertfordshire.utils;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hertfordshire.utils.dto.CookieLangDto;
import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.Years;
import org.json.JSONArray;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;
import java.util.regex.Pattern;

import static org.springframework.beans.MethodInvocationException.ERROR_CODE;

public class Utils {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy");
    private static final SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd MMM yyyy hh:mmm:ss");
    private static final SimpleDateFormat yearDateFormat = new SimpleDateFormat("yyyy");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
    private static final Pattern GLOBAL_PHONE_NUMBER_PATTERN = Pattern.compile("[+]?[0-9.-]+");
    private static final Pattern GLOBAL_NUMBER_PATTERN = Pattern.compile("[0-9]+");
    private static final Pattern USER_NAME_PATTERN = Pattern.compile("[A-Za-z]?[0-9A-Za-z_.]+");
    private static final DecimalFormat CURRENCY_DECIMAL_FORMAT = new DecimalFormat("#,##0.00");
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,##0");
    // private static final com.google.i18n.phonenumbers.PhoneNumberUtil phoneNumberUtil = com.google.i18n.phonenumbers.PhoneNumberUtil.getInstance();
    private final static String NON_THIN = "[^iIl1\\.,']";
    private static final Gson GSON = new GsonBuilder().create();

    public static boolean isDateFormat(String dateString) {
        return dateString.matches("\\d{2}/\\d{2}/\\d{4}(\\s\\d{2}:\\d{2})?");
    }

    public static String formatDate(Date date, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        return formatter.format(date);
    }

    public static boolean isValidString(String string) {
        return string != null && !string.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        return isValidString(email) && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        return isValidString(phoneNumber) && GLOBAL_PHONE_NUMBER_PATTERN.matcher(phoneNumber).matches();
    }

    public static boolean isValidNumber(String string) {
        return isValidString(string) && GLOBAL_NUMBER_PATTERN.matcher(string).matches();
    }

    public static boolean isValidUserName(String userName) {
        return isValidString(userName) && USER_NAME_PATTERN.matcher(userName).matches();
    }

    public static <T> T fromGson(String jsonString, Class<T> tClass) {
        return GSON.fromJson(jsonString, tClass);
    }

//    public static String toInternationalPhoneNumber(String phoneNumber) {
//        try {
//            Phonenumber.PhoneNumber number = phoneNumberUtil.parse(phoneNumber, "NG");
//            return phoneNumberUtil.format(number, com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat.E164);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return phoneNumber;
//    }

    public static String capitalizeSentence(String sentence) {
        int pos = 0;
        boolean capitalize = true;
        StringBuilder sb = new StringBuilder(sentence.trim());

        while (pos < sb.length()) {
            if (sb.charAt(pos) == '.') {
                capitalize = true;
            } else if (capitalize && !Character.isWhitespace(sb.charAt(pos))) {
                sb.setCharAt(pos, Character.toUpperCase(sb.charAt(pos)));
                capitalize = false;
            }
            pos++;
        }
        return sb.toString();
    }

    private static int textWidth(String str) {
        return (int) (str.length() - str.replaceAll(NON_THIN, "").length() / 2);
    }

    public static String ellipsize(String text, int max) {

        if (textWidth(text) <= max) {
            return text;
        }
        int end = text.lastIndexOf(' ', max - 3);
        if (end == -1) {
            return text.substring(0, max - 3) + "...";
        }
        int newEnd = end;
        do {
            end = newEnd;
            newEnd = text.indexOf(' ', end + 1);
            if (newEnd == -1) {
                newEnd = text.length();
            }
        } while (textWidth(text.substring(0, newEnd) + "...") < max);
        return text.substring(0, end) + "...";
    }

    public static String generateRandomKey() {
        return RandomStringUtils.randomAlphanumeric(8);
    }

    public static String formatCurrency(long l) {
        return CURRENCY_DECIMAL_FORMAT.format(l);
    }

    public static String encodeToBase36(Long number) {
        return Long.toString(number, 36);
    }

    public static String getCurrentYear() {
        return yearDateFormat.format(new Date());
    }

    public static boolean isToday(Date timestamp) {
        return Days.daysBetween(new DateTime(timestamp), DateTime.now()).getDays() == 0;
    }

    public static boolean isSameMonth(Date timestamp) {
        return Months.monthsBetween(new DateTime(timestamp), DateTime.now()).getMonths() == 0;
    }

    public static boolean isSameYear(Date timestamp) {
        return Years.yearsBetween(new DateTime(timestamp), DateTime.now()).getYears() == 0;
    }

    public static Timestamp getMidTimestamp(Date startTime, Date endTime) {
        return new Timestamp(((endTime.getTime() - startTime.getTime()) / 2) + startTime.getTime());
    }

    public static String formatTimestamp(Timestamp timestamp) {
        String time = dateFormat2.format(timestamp);
        return time;
    }

    public String toJson(Object o) {
        return GSON.toJson(o);
    }

    static class AggregateIterator<E> implements Iterator<E> {

        LinkedList<Enumeration<E>> enums = new LinkedList<Enumeration<E>>();
        Enumeration<E> cur = null;
        E next = null;
        Set<E> loaded = new HashSet<E>();

        public AggregateIterator<E> addEnumeration(Enumeration<E> e) {
            if (e.hasMoreElements()) {
                if (cur == null) {
                    cur = e;
                    next = e.nextElement();
                    loaded.add(next);
                } else {
                    enums.add(e);
                }
            }
            return this;
        }

        public boolean hasNext() {
            return (next != null);
        }

        public E next() {
            if (next != null) {
                E prev = next;
                next = loadNext();
                return prev;
            } else {
                throw new NoSuchElementException();
            }
        }

        private Enumeration<E> determineCurrentEnumeration() {
            if (cur != null && !cur.hasMoreElements()) {
                if (enums.size() > 0) {
                    cur = enums.removeLast();
                } else {
                    cur = null;
                }
            }
            return cur;
        }

        private E loadNext() {
            if (determineCurrentEnumeration() != null) {
                E tmp = cur.nextElement();
                int loadedSize = loaded.size();
                while (loaded.contains(tmp)) {
                    tmp = loadNext();
                    if (tmp == null || loaded.size() > loadedSize) {
                        break;
                    }
                }
                if (tmp != null) {
                    loaded.add(tmp);
                }
                return tmp;
            }
            return null;

        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }


    public static Timestamp atEndOfDay(long timestamp) {
        Date date = new Date(timestamp);
        Date newDate = DateUtils.addMilliseconds(DateUtils.ceiling(date, Calendar.DATE), -1);
        return new Timestamp(newDate.getTime());
    }

    public static Timestamp atStartOfDay(long timestamp) {
        Date date = new Date(timestamp);
        Date newDate = DateUtils.truncate(date, Calendar.DATE);
        return new Timestamp(newDate.getTime());
    }

    private static LocalDateTime dateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    private static Date localDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date atStartOfDay(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
        return localDateTimeToDate(startOfDay);
    }

    public static Date atEndOfDay(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
        return localDateTimeToDate(endOfDay);
    }


    public static double round(double number, int decimalPlaces) {
        return BigDecimal.valueOf(number)
                .setScale(decimalPlaces, RoundingMode.HALF_UP)
                .doubleValue();
    }


    public static String roundOffTo7DecPlaces(double val)
    {
        return String.format("%.7f", val);
    }

    public static Timestamp dateToTimeStamp(Date date) {

        if (date != null) {  // simple null check
            return new Timestamp(date.getTime());
        }

        return null;
    }

    public static Date timeStampToDate(Timestamp timestamp) {

        if (timestamp != null) {
            return new Date(timestamp.getTime());
        }

        return null;
    }

    public static File multipartToFile(MultipartFile multipart) throws IllegalStateException, IOException
    {
        if(multipart != null) {
            File convFile = new File(multipart.getOriginalFilename());
            multipart.transferTo(convFile);
            return convFile;
        }else {
            return null;
        }
    }

    public File convert(MultipartFile multipart)
    {
        if(multipart != null) {
            File convFile = new File(multipart.getOriginalFilename());
            try {
                convFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(convFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                fos.write(multipart.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return convFile;
        }else {
            return null;
        }
    }


    public static boolean checkIfValidNumber(String value) {


        if (value.contains("+")) {
            String newValue = value.replace("+", "");
            try {
                double d = Double.parseDouble(newValue.trim());
                return true;
            } catch (NumberFormatException | NullPointerException nfe) {
                return false;
            }

        } else {

            try {
                double d = Double.parseDouble(value.trim());
                return true;
            } catch (NumberFormatException | NullPointerException nfe) {
                return false;
            }
        }
    }

    public static boolean isEmailValid(String email){
        return EmailValidator.getInstance(true).isValid(email);
    }

    public static Long nairaToKobo(String nairaInString) {

        String strippedResult = nairaInString.replace(",", "");
        Long result = Long.parseLong(strippedResult) * 100;
        return result;
    }

    public static String koboToNaira(Long koboInLong){
        Long result = koboInLong/100;
        return result.toString();
    }

    public static ZonedDateTime dateToZonedDateTime(Date calculateFromDate) {
        return ZonedDateTime.ofInstant(calculateFromDate.toInstant(), ZoneId.systemDefault());
    }


    public static Calendar convertZonedDateTimeToCalendar(ZonedDateTime entityAttribute) {
        if (entityAttribute == null) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(entityAttribute.toInstant().toEpochMilli());
        calendar.setTimeZone(TimeZone.getTimeZone(entityAttribute.getZone()));
        return calendar;
    }

    public static Date getFirstDayOfYear(int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.DAY_OF_YEAR, 1);
        return cal.getTime();
    }


    public static Date getLastDayOfYear(int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, 11); // 11 = december
        cal.set(Calendar.DAY_OF_MONTH, 31); // new years eve
        return cal.getTime();
    }

    public static int getMonthFromDate(Date date) {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate.getMonthValue();
    }


    public static String generatePassayPassword() {
        PasswordGenerator gen = new PasswordGenerator();
        CharacterData lowerCaseChars = EnglishCharacterData.LowerCase;
        CharacterRule lowerCaseRule = new CharacterRule(lowerCaseChars);
        lowerCaseRule.setNumberOfCharacters(2);

        CharacterData upperCaseChars = EnglishCharacterData.UpperCase;
        CharacterRule upperCaseRule = new CharacterRule(upperCaseChars);
        upperCaseRule.setNumberOfCharacters(2);

        CharacterData digitChars = EnglishCharacterData.Digit;
        CharacterRule digitRule = new CharacterRule(digitChars);
        digitRule.setNumberOfCharacters(2);

        CharacterData specialChars = new CharacterData() {
            public String getErrorCode() {
                return ERROR_CODE;
            }

            public String getCharacters() {
                return "!@#$%^&*()_+";
            }
        };
        CharacterRule splCharRule = new CharacterRule(specialChars);
        splCharRule.setNumberOfCharacters(2);

        return gen.generatePassword(10, splCharRule, lowerCaseRule,
                upperCaseRule, digitRule);
    }


    public static String formatToReadableCurrency(String number) {
        double amount = Double.parseDouble(number);
        DecimalFormat formatter = new DecimalFormat("#,###.00");
        return formatter.format(amount);
    }


    public static String[] getStringArray(JSONArray jsonArray) {
        String[] stringArray = null;
        if (jsonArray != null) {
            int length = jsonArray.length();
            stringArray = new String[length];
            for (int i = 0; i < length; i++) {
                stringArray[i] = jsonArray.optString(i);
            }
        }
        return stringArray;
    }

    private static String extractCookie(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) {
            return null;
        }
        String authToken = null;
        for (Cookie cookie: request.getCookies()) {
            if (cookie.getName().equals(cookieName)) {
                authToken = cookie.getValue();
                break;
            }
        }
        if (authToken != null) {
            return authToken;
        }else {
            return null;
        }
    }


    public static String fetchLanguageFromCookie(HttpServletRequest request) {

        String cookieLang = extractCookie(request, "lang");

        if(cookieLang != null) {
            String reqParam = null;
            try {
                reqParam = URLDecoder.decode(cookieLang, "UTF-8");
                Gson gson = new Gson();
                CookieLangDto cookieLangDto = gson.fromJson(reqParam, CookieLangDto.class);
                if (cookieLangDto != null) {
                    return cookieLangDto.getLang();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();

                return null;
            }
        }

        return null;
    }

    public static String fetchCookie(HttpServletRequest request, String name) {
        String cookieLang = extractCookie(request, name);
        if(cookieLang != null) {
            String reqParam = null;
            try {
                reqParam = URLDecoder.decode(cookieLang, "UTF-8");
                return reqParam;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public static Long getDifferenceBetweenTwoDates(Date startDate) {
        ZoneId defaultZoneId = ZoneId.systemDefault();
        //3. Instant + system default time zone + toLocalDateTime() = LocalDateTime
        LocalDateTime localDateTime =  startDate.toInstant().atZone(defaultZoneId).toLocalDateTime();

        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(localDateTime, now);
        return duration.toHours();
    }

    public static <T> List<T> getPage(List<T> sourceList, int page, int pageSize) {
        if(pageSize <= 0 || page <= 0) {
            throw new IllegalArgumentException("invalid page size: " + pageSize);
        }

        int fromIndex = (page - 1) * pageSize;
        if(sourceList == null || sourceList.size() < fromIndex){
            return Collections.emptyList();
        }
        // toIndex exclusive
        return sourceList.subList(fromIndex, Math.min(fromIndex + pageSize, sourceList.size()));
    }


    public static String addUnderScore(String value){
        return  value.replaceAll(" ", "_").toUpperCase();
    }


    public static String getAccessToken() throws IOException {

        String fileUrl = "service-account.json";
        GoogleCredential googleCredential = GoogleCredential
                .fromStream(new ClassPathResource(fileUrl).getInputStream())
                .createScoped(Collections.emptyList());
        googleCredential.refreshToken();
        return googleCredential.getAccessToken();
    }


    public static int calculatePageNumber(int pageNumber, int pageSize, int index) {
        if (pageNumber == 0) {
            return index + 1;
        } else {
            return index + pageSize + pageNumber;
        }
    }

    public boolean verify2Fa(String secret, String twoFactorCode) {

        try {
            TimeProvider timeProvider = new SystemTimeProvider();
            CodeGenerator codeGenerator = new DefaultCodeGenerator();
            CodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
            return verifier.isValidCode(secret, twoFactorCode);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
