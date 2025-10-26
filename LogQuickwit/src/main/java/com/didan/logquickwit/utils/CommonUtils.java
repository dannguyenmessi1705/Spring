package com.didan.logquickwit.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class CommonUtils {

    public static final Gson GSON = (new GsonBuilder()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    public static String createNewUUID() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase(); // Tạo một UUID mới và loại bỏ dấu "-"
    }
}
