package org.adempiere.webui.util;

/**
 * HTML Output Encoding Utility
 */
public class HTMLEncoder {
    
    public static String encode(String input) {
        if (input == null) return null;
        
        return input
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#x27;")
            .replace("/", "&#x2F;");
    }
    
    public static String encodeForAttribute(String input) {
        if (input == null) return null;
        
        return input
            .replace("&", "&amp;")
            .replace("\"", "&quot;")
            .replace("'", "&#x27;")
            .replace("<", "&lt;")
            .replace(">", "&gt;");
    }
}