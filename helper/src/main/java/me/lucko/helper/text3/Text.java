/*
 * This file is part of helper, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package me.lucko.helper.text3;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utilities for working with {@link Component}s and formatted text strings.
 */
public final class Text {

    public static final char SECTION_CHAR = '\u00A7'; // §
    public static final char AMPERSAND_CHAR = '&';
    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + SECTION_CHAR + "[0-9A-FK-ORX]");

    public static @NotNull String compressConsecutiveSpaces(@NotNull String str) {
        return str.trim().replaceAll("\\s+", " ");
    }

    public static @NotNull String removeSpaces(@NotNull String str) {
        return str.trim().replaceAll("\\s+", "");
    }

    public static @NotNull String joinNewline(String @NotNull ... strings) {
        return joinNewline(Arrays.stream(strings));
    }

    public static @NotNull String joinNewline(@NotNull Stream<@NotNull String> strings) {
        return strings.collect(Collectors.joining("\n"));
    }

    public static @NotNull TextComponent fromLegacy(@NotNull String input, char character) {
        return LegacyComponentSerializer.legacy(character).deserialize(input);
    }

    public static @NotNull TextComponent fromLegacyAmpersand(@NotNull String input) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(input);
    }

    public static @NotNull TextComponent fromLegacySection(@NotNull String input) {
        return LegacyComponentSerializer.legacySection().deserialize(input);
    }

    public static @NotNull String toLegacy(@NotNull Component component, char character) {
        return LegacyComponentSerializer.legacy(character).serialize(component);
    }

    public static @NotNull String toLegacyAmpersand(@NotNull Component component) {
        return LegacyComponentSerializer.legacyAmpersand().serialize(component);
    }

    public static @NotNull String toLegacySection(@NotNull Component component) {
        return LegacyComponentSerializer.legacySection().serialize(component);
    }

    /**
     * Translates ampersand ({@code &}) color codes into section ({@code §}) color codes.
     * <p>
     * The translation supports three different RGB formats: 1) Legacy Mojang color and formatting codes (such as §a or
     * §l), 2) Adventure-specific RGB format (such as §#a25981) and  3) BungeeCord RGB color code format (such as
     * §x§a§2§5§9§8§1).
     *
     * @param s a legacy text where its color codes are in <b>ampersand</b> {@code &} format
     * @return a legacy text where its color codes are in <b>section</b> {@code §} format
     */
    @Contract(value = "null -> null")
    public static String colorize(String s) {
        if (s == null) {
            return null;
        }

        return LegacyComponentSerializer.legacySection().serialize(LegacyComponentSerializer.legacy(AMPERSAND_CHAR).deserialize(s));
    }

    /**
     * Translates section ({@code §}) color codes into ampersand ({@code &}) color codes.
     * <p>
     * The translation supports three different RGB formats: 1) Legacy Mojang color and formatting codes (such as §a or
     * §l), 2) Adventure-specific RGB format (such as §#a25981) and  3) BungeeCord RGB color code format (such as
     * §x§a§2§5§9§8§1).
     *
     * @param s a legacy text where its color codes are in <b>section</b> {@code &} format
     * @return a legacy text where its color codes are in <b>ampersand</b> {@code §} format
     */
    @Contract(value = "null -> null")
    public static String decolorize(String s) {
        if (s == null) {
            return null;
        }

        return LegacyComponentSerializer.legacyAmpersand().serialize(LegacyComponentSerializer.legacy(SECTION_CHAR).deserialize(s));
    }

    /**
     * Converts given legacy text into plain text.
     *
     * @param legacy a text containing legacy color codes
     * @return a plain text
     */
    @Contract(value = "null -> null")
    public static String stripColor(String legacy) {
        if (legacy == null) {
            return null;
        }

        return STRIP_COLOR_PATTERN.matcher(legacy).replaceAll("");
    }

    @Contract(value = "_,_,null -> null")
    public static String translateAlternateColorCodes(char from, char to, String textToTranslate) {
        if (textToTranslate == null) {
            return null;
        }

        char[] b = textToTranslate.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == from && "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx".indexOf(b[i + 1]) > -1) {
                b[i] = to;
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }
        return new String(b);
    }

    private Text() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

}
