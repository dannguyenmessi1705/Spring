package com.example.demo.utils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Calendar;
import java.util.regex.Pattern;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

@UtilityClass
@Slf4j
public class ObjectMapperUtils {
  public static final String EXCEPTION_WHEN_PARSING_JSON = "Exception when parsing [JSON=%s] to object [class=%s]";
  private static final ObjectMapper objectMapper;

  static {
    objectMapper = new ObjectMapper()
        .setAnnotationIntrospector(new JacksonAnnotationIntrospector()) // thiết lập introspector để sử dụng annotation của Jackson
        .registerModule(new JavaTimeModule()) // thiết lập module để sử dụng các định dạng thời gian của Java
        .setDateFormat(new StdDateFormat()) // thiết lập định dạng ngày tháng
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES) // không bắt lỗi khi có thuộc tính không xác định
        .setTimeZone(Calendar.getInstance().getTimeZone()) // thiết lập múi giờ
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // không viết ngày tháng dưới dạng timestamp
  }

  public static String toJson(Object o) { // chuyển object thành chuỗi JSON
    try {
      return objectMapper.writeValueAsString(o);
    } catch (Exception e) {
      log.error("Exception when parsing object to JSON", e);
      return null;
    }
  }

  public static String toJsonWithParamNotNullOrEmpty(Object o) { // chuyển object thành chuỗi JSON với điều kiện không null hoặc rỗng
    try {
      ObjectMapper objectMapper1 = new ObjectMapper();
      objectMapper1.setSerializationInclusion(Include.NON_NULL); // không chuyển thuộc tính null
      return objectMapper1.writeValueAsString(o);
    } catch (Exception e) {
      log.error("Exception when parsing object to JSON", e);
      return null;
    }
  }

  public static <T> T fromJson(String json, TypeReference<T> typeReference) { // chuyển chuỗi JSON thành object
    try {
      return objectMapper.readValue(json, typeReference);
    } catch (Exception e) {
      log.error(String.format(EXCEPTION_WHEN_PARSING_JSON, json, typeReference.getType()), e);
      return null;
    }
  }

  public static <T> T fromJson(String json, Class<T> clazz) { // chuyển chuỗi JSON thành class cụ thể
    try {
      return objectMapper.readValue(json, clazz);
    } catch (Exception e) {
      log.error(String.format(EXCEPTION_WHEN_PARSING_JSON, json, clazz.getSimpleName()), e);
      return null;
    }
  }

  public static <T> T fromJsonWithUnknownProperties(String json, TypeReference<T> typeReference) {
    try {
      ObjectMapper objectMapper1 = new ObjectMapper();
      objectMapper1.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // không bắt lỗi khi có thuộc tính không xác định
      return objectMapper1.readValue(json, typeReference);
    } catch (Exception e) {
      log.error(String.format(EXCEPTION_WHEN_PARSING_JSON, json, typeReference.getType()), e);
      return null;
    }
  }

  public static String convertToUnsigned(String input){
    if (ObjectUtils.isEmpty(input)) { // kiểm tra xem chuỗi đầu vào có rỗng không
      return input; // nếu rỗng thì trả về luôn
    }
    String regex = "\\p{InCombiningDiacriticalMarks}+"; // regex để loại bỏ dấu
    String temp = Normalizer.normalize(input, Form.NFD);
    return Pattern.compile(regex).matcher(temp).replaceAll("").replace("đ", "d");
  }

}
