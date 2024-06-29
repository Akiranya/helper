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

package me.lucko.helper.nbt;

import me.lucko.shadow.bukkit.BukkitShadowFactory;

/**
 * Utilities for working with NBT shadows.
 */
public final class ShadowTags {

    private static TagParser parser = null;

    private static TagParser parser() {
        // harmless race
        if (parser == null) {
            // must use BukkitShadowFactory for the Bukkit-specialized target resolvers
            return parser = BukkitShadowFactory.global().staticShadow(TagParser.class);
        }
        return parser;
    }

    public static Tag shadow(Object tagObject) {
        // first, shadow as a NBTBase
        Tag shadow = BukkitShadowFactory.global().shadow(Tag.class, tagObject);

        // extract the tag's type
        ShadowTagType type = shadow.getType();
        Class<? extends Tag> realClass = type.shadowClass();

        // return a shadow instance for the actual type
        return BukkitShadowFactory.global().shadow(realClass, tagObject);
    }

    public static CompoundTag parse(String s) {
        return parser().parseTag(s);
    }

    private ShadowTags() {}

}
