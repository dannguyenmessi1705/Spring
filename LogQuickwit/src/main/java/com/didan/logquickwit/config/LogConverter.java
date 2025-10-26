package com.didan.logquickwit.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Plugin(name = "LogConverter", category = "Converter")
@ConverterKeys({"emsg"})
@Slf4j
public class LogConverter extends LogEventPatternConverter {

    public static final String REPLACEMENT = "********";
    public static final String PASSWORD_REGEX = "(\"password\"\\s*:\\s*\")[^\"]+(\"?)";

    protected LogConverter(String name, String style) {
        super(name, style);
    }

    public static LogConverter newInstance() {
        return new LogConverter("log-quickwit", Thread.currentThread().getName());
    }

    @Override
    public void format(LogEvent event, StringBuilder toAppendTo) {
        String message = event.getMessage().getFormattedMessage();
        String maskedMessage;
        try {
            maskedMessage = mask(message);
        } catch (Exception ex) {
            log.error("Failed while masking [{}]", ex.getMessage());
            maskedMessage = message;
        }
        toAppendTo.append(maskedMessage);
    }

    private String mask(String message) {
        message = replaceGroup(PASSWORD_REGEX, message, 1, REPLACEMENT);
        return message;
    }

    public static String replaceGroup(String regex, String source, int groupToReplace, String replacement) {
        return replaceGroup(regex, source, groupToReplace, 1, replacement);
    }

    public static String replaceGroup(String regex, String source, int groupToReplace, int groupOccurrence, String replacement) {
        Matcher matcher = Pattern.compile(regex).matcher(source);
        for (int i = 0; i < groupOccurrence; i++) {
            if (!matcher.find()) {
                return source; // pattern not met, may also throw an exception here
            }
        }
        return new StringBuffer(source).replace(matcher.start(groupToReplace), matcher.end(groupToReplace), replacement).toString();
    }
}
