package org.stonlexx.servercontrol.api.chat;

import lombok.Getter;
import org.fusesource.jansi.Ansi;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Simplistic enumeration of all supported color values for chat.
 */
public enum ChatColor {

    /**
     * Represents black.
     */
    BLACK('0', "black", Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLACK).boldOff()),

    /**
     * Represents dark blue.
     */
    DARK_BLUE('1', "dark_blue", Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLUE).boldOff()),

    /**
     * Represents dark green.
     */
    DARK_GREEN('2', "dark_green", Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.GREEN).boldOff()),

    /**
     * Represents dark blue (aqua).
     */
    DARK_AQUA('3', "dark_aqua", Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.CYAN).boldOff()),

    /**
     * Represents dark red.
     */
    DARK_RED('4', "dark_red", Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.RED).boldOff()),

    /**
     * Represents dark purple.
     */
    DARK_PURPLE('5', "dark_purple", Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.MAGENTA)),

    /**
     * Represents gold.
     */
    GOLD('6', "gold", Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.YELLOW).boldOff()),

    /**
     * Represents gray.
     */
    GRAY('7', "gray", Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.WHITE).boldOff()),

    /**
     * Represents dark gray.
     */
    DARK_GRAY('8', "dark_gray", Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLACK).bold()),

    /**
     * Represents blue.
     */
    BLUE('9', "blue", Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLUE).bold()),

    /**
     * Represents green.
     */
    GREEN('a', "green", Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.GREEN).bold()),

    /**
     * Represents aqua.
     */
    AQUA('b', "aqua", Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.CYAN).bold()),

    /**
     * Represents red.
     */
    RED('c', "red", Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.RED).bold()),

    /**
     * Represents light purple.
     */
    LIGHT_PURPLE('d', "light_purple", Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.MAGENTA).bold()),

    /**
     * Represents yellow.
     */
    YELLOW('e', "yellow", Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.YELLOW).bold()),

    /**
     * Represents white.
     */
    WHITE('f', "white", Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.WHITE).bold()),

    /**
     * Represents magical characters that change around randomly.
     */
    MAGIC('k', "obfuscated", Ansi.ansi().a(Ansi.Attribute.BLINK_SLOW)),

    /**
     * Makes the text bold.
     */
    BOLD('l', "bold", Ansi.ansi().a(Ansi.Attribute.UNDERLINE_DOUBLE)),

    /**
     * Makes a line appear through the text.
     */
    STRIKETHROUGH('m', "strikethrough", Ansi.ansi().a(Ansi.Attribute.STRIKETHROUGH_ON)),

    /**
     * Makes the text appear underlined.
     */
    UNDERLINE('n', "underline", Ansi.ansi().a(Ansi.Attribute.UNDERLINE)),

    /**
     * Makes the text italic.
     */
    ITALIC('o', "italic",  Ansi.ansi().a(Ansi.Attribute.ITALIC)),

    /**
     * Resets all previous chat colors or formats.
     */
    RESET('r', "reset", Ansi.ansi().a(Ansi.Attribute.RESET));

    /**
     * The special character which prefixes all chat colour codes. Use this if
     * you need to dynamically convert colour codes from your custom format.
     */
    public static final char COLOR_CHAR = '\u00A7';
    public static final String ALL_CODES = "0123456789AaBbCcDdEeFfKkLlMmNnOoRr";

    /**
     * Pattern to remove all colour codes.
     */
    public static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + COLOR_CHAR + "[0-9A-FK-OR]");

    /**
     * Colour instances keyed by their active character.
     */
    private static final Map<Character, ChatColor> BY_CHAR = new HashMap<>();

    /**
     * The code appended to {@link #COLOR_CHAR} to make usable colour.
     */
    @Getter
    private final char code;

    @Getter
    private final String name;

    @Getter
    private final Ansi ansi;


    private final String toString;

    static {
        for (ChatColor colour : values()) {
            BY_CHAR.put(colour.code, colour);
        }
    }

    ChatColor(char code, String name, Ansi ansi) {
        this.code = code;
        this.name = name;
        this.ansi = ansi;

        this.toString = new String(new char[]{ COLOR_CHAR, code });
    }

    @Override
    public String toString() {
        return toString;
    }

    /**
     * Strips the given message of all color codes
     *
     * @param input String to strip of color
     * @return A copy of the input string, without any coloring
     */
    public static String stripColor(final String input) {
        if (input == null) {
            return null;
        }

        return STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
    }

    public static String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
        if (textToTranslate == null) {
            return null;
        }

        char[] b = textToTranslate.toCharArray();

        for ( int i = 0; i < b.length - 1; i++ ) {
            if (!(b[i] == altColorChar && ALL_CODES.indexOf(b[i + 1]) > -1)) {
                continue;
            }

            b[i] = ChatColor.COLOR_CHAR;
            b[i + 1] = Character.toLowerCase(b[i + 1]);
        }

        return new String( b );
    }

    /**
     * Get the colour represented by the specified code.
     *
     * @param code the code to search for
     * @return the mapped colour, or null if non exists
     */
    public static ChatColor getByChar(char code) {
        return BY_CHAR.get(code);
    }
}
