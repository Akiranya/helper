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

package me.lucko.helper.nbt2;

import me.lucko.helper.shadows.nbt2.CompoundShadowTag;
import me.lucko.helper.shadows.nbt2.ShadowTag;
import me.lucko.helper.shadows.nbt2.ShadowTagParser;
import me.lucko.shadow.bukkit.BukkitShadowFactory;

/**
 * Utilities for working with NBT shadows.
 */
public final class ShadowTags {

    private static ShadowTagParser parser = null;

    private static ShadowTagParser parser() {
        // harmless race
        if (parser == null) {
            // must use BukkitShadowFactory for the Bukkit-specialized target resolvers
            return parser = BukkitShadowFactory.global().staticShadow(ShadowTagParser.class);
        }
        return parser;
    }

    public static ShadowTag shadow(Object tagObject) {
        // first, shadow as a NBTBase
        ShadowTag shadow = BukkitShadowFactory.global().shadow(ShadowTag.class, tagObject);

        // extract the tag's type
        ShadowTagType type = shadow.getType();
        Class<? extends ShadowTag> realClass = type.shadowClass();

        // return a shadow instance for the actual type
        return BukkitShadowFactory.global().shadow(realClass, tagObject);
    }

    public static CompoundShadowTag parse(String s) {
        return parser().parseTag(s);
    }

    private ShadowTags() {}

}
