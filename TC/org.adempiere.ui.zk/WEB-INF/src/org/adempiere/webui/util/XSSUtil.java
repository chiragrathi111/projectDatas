package org.adempiere.webui.util;

import java.util.regex.Pattern;

/**
 * XSS Protection Utility for Web UI
 * * NOTE: This is a Blacklist approach. For full security, use a Whitelist HTML Sanitizer.
 */
public class XSSUtil {
    private static final Pattern[] XSS_PATTERNS = {
        Pattern.compile("<[a-z]+[\\s>]", Pattern.CASE_INSENSITIVE),
        Pattern.compile("</[a-z]+>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
        Pattern.compile("on\\w+\\s*=", Pattern.CASE_INSENSITIVE),
        Pattern.compile("alert\\s*\\(", Pattern.CASE_INSENSITIVE),
        Pattern.compile("eval\\s*\\(", Pattern.CASE_INSENSITIVE)
    };
    
    public static boolean containsXSS(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        
        String normalized = input.replaceAll("[\\r\\n]+", " ")
                                 .replaceAll("\\s+", " ")
                                 .trim();
        
        for (Pattern pattern : XSS_PATTERNS) {
            if (pattern.matcher(normalized).find()) {
                return true;
            }
        }
        return false;
    }
    
    public static String getErrorMessage(String fieldName) {
        return "Security Error: HTML tags not allowed in field '" + fieldName + "'. Please remove HTML content.";
    }
}