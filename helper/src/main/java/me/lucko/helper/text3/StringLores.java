package me.lucko.helper.text3;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.function.UnaryOperator;

public class StringLores {
    @SafeVarargs
    @Contract(pure = true)
    public static @NotNull List<String> replace(@NotNull List<String> original, @NotNull UnaryOperator<String>... replacer) {
        return replace(false, original, replacer);
    }

    @SafeVarargs
    @Contract(pure = true)
    public static @NotNull List<String> replace(boolean unfoldNewline, @NotNull List<String> original, @NotNull UnaryOperator<String>... replacer) {
        List<String> newList = new ArrayList<>(original);
        for (UnaryOperator<String> re : replacer) newList.replaceAll(re);
        if (unfoldNewline) newList = unfoldByNewline(newList);
        return newList;
    }

    @SafeVarargs
    @Contract(pure = true)
    public static @NotNull String replace(@NotNull String text, @NotNull UnaryOperator<String>... replacer) {
        for (UnaryOperator<String> re : replacer) {
            text = re.apply(text); // Reassign
        }
        return text;
    }

    @Contract(pure = true, value = "_, null, _ -> null; _, !null, _ -> !null ")
    public static List<String> replacePlaceholderList(@NotNull String placeholder, @Nullable List<String> dst, @NotNull List<String> src) {
        return replacePlaceholderList(placeholder, dst, src, false);
    }

    @Contract(pure = true, value = "_, null, _, _ -> null; _, !null, _, _ -> !null ")
    public static List<String> replacePlaceholderList(@NotNull String placeholder, @Nullable List<String> dst, @NotNull List<String> src, boolean keep) {
        if (dst == null) return null;

        // Let's find which line (in the dst) has the placeholder
        int placeholderIdx = -1;
        String placeholderLine = null;
        for (int i = 0; i < dst.size(); i++) {
            placeholderLine = dst.get(i);
            if (placeholderLine.contains(placeholder)) {
                placeholderIdx = i;
                break;
            }
        }
        if (placeholderIdx == -1) return dst;

        // Let's make the list to be inserted into the dst
        if (keep) {
            src = new ArrayList<>(src);
            ListIterator<String> it = src.listIterator();
            while (it.hasNext()) {
                String line = it.next();
                String replaced = placeholderLine.replace(placeholder, line);
                it.set(replaced);
            }
        }

        // Insert the src into the dst
        List<String> result = new ArrayList<>(dst);
        result.remove(placeholderIdx); // Need to remove the raw placeholder from dst
        result.addAll(placeholderIdx, src);

        return result;
    }

    /**
     * Transforms any group of empty strings found in a row into just one empty string.
     *
     * @param stringList a list of strings which may contain empty lines
     * @return a modified copy of the list
     */
    @Contract(pure = true)
    public static @NotNull List<String> compressEmptyLines(@NotNull List<String> stringList) {
        List<String> stripped = new ArrayList<>();
        boolean prevEmpty = false; // Mark whether the previous line is empty
        for (String line : stringList) {
            if (line.isEmpty()) {
                if (!prevEmpty) {
                    prevEmpty = true;
                    stripped.add(line);
                }
            } else {
                prevEmpty = false;
                stripped.add(line);
            }
        }
        return stripped;
    }

    @Contract(pure = true)
    public static @NotNull List<String> unfoldByNewline(@NotNull List<String> lore) {
        List<String> unfolded = new ArrayList<>();
        for (String str : lore) {
            String[] arr = str.split("\n");
            if (arr.length > 1) {
                unfolded.addAll(Arrays.asList(arr));
            } else { // for better performance
                unfolded.add(str);
            }
        }
        return unfolded;
    }

    @Contract(pure = true)
    public static @NotNull List<String> unfoldByNewline(@NotNull String... lore) {
        return unfoldByNewline(Arrays.asList(lore));
    }
}
