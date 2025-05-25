package com.kh.utils;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    public static String formatVietnameseDate(Date date) {
        // Sử dụng Locale.forLanguageTag thay cho new Locale("vi", "VN")
        Locale vietnameseLocale = Locale.forLanguageTag("vi-VN");
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE, 'ngày' dd 'tháng' MM 'năm' yyyy", vietnameseLocale);
        return capitalizeFirstLetter(formatter.format(date));
    }

    private static String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) return input;
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
