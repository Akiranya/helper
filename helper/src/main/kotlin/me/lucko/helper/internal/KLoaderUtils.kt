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
package me.lucko.helper.internal

import me.lucko.helper.Helper
import me.lucko.helper.plugin.KHelperPlugin
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin

/**
 * Provides the instance which loaded the helper classes into the server
 */
object KLoaderUtils {
    private var plugin: KHelperPlugin? = null

    @get:Synchronized
    var mainThread: Thread? = null
        get() {
            if (field == null) {
                if (Bukkit.getServer().isPrimaryThread) {
                    field = Thread.currentThread()
                }
            }
            return field
        }
        private set

    @Synchronized
    fun getPlugin(): KHelperPlugin {
        if (plugin != null) {
            return plugin!!
        }

        val pl: JavaPlugin = JavaPlugin.getProvidingPlugin(LoaderUtils::class.java)
        if (pl is KHelperPlugin) {
            plugin = pl
            var pkg: String = LoaderUtils::class.java.getPackage().name
            pkg = pkg.substring(0, pkg.length - ".internal".length)
            Bukkit.getLogger().info("[helper] helper (" + pkg + ") bound to plugin " + plugin!!.name + " - " + plugin!!.javaClass.getName())
            setup()
            return plugin!!
        }
        throw IllegalStateException("helper providing plugin does not implement HelperPlugin: " + pl.javaClass.getName())
    }

    /**
     * To be used for testing only
     */
    @Synchronized
    fun forceSetPlugin(plugin: KHelperPlugin) {
        this.plugin = plugin
    }

    val helperImplementationPlugins: Set<Plugin>
        get() = sequenceOf(getPlugin())
            .plus(Helper.plugins().plugins.asSequence()
                .filter { pl: Plugin -> pl.javaClass.isAnnotationPresent(KHelperImplementationPlugin::class.java) })
            .toSet()

    val helperPlugins: Set<KHelperPlugin>
        get() {
            return sequenceOf(getPlugin())
                .plus(Helper.plugins().plugins.asSequence()
                    .filterIsInstance<KHelperPlugin>())
                .toSet()
        }


    // performs an intial setup for global handlers
    private fun setup() {

        // cache main thread in this class
        mainThread
    }
}
