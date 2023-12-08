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
package me.lucko.helper.plugin

import me.lucko.helper.terminable.TerminableConsumer
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.ServicePriority

interface KHelperPlugin : Plugin, TerminableConsumer {
    /**
     * Gets a service provided by the ServiceManager
     *
     * @param service the service class
     * @param T the class type
     * @return the service
     */
    fun <T> getService(service: Class<T>): T

    /**
     * Provides a service to the ServiceManager, bound to this plugin
     *
     * @param clazz the service class
     * @param instance the instance
     * @param priority the priority to register the service at
     * @param T the service class type
     * @return the instance
     */
    fun <T : Any> provideService(clazz: Class<T>, instance: T, priority: ServicePriority): T

    /**
     * Provides a service to the ServiceManager, bound to this plugin at
     * [ServicePriority.Normal].
     *
     * @param clazz the service class
     * @param instance the instance
     * @param T the service class type
     * @return the instance
     */
    fun <T : Any> provideService(clazz: Class<T>, instance: T): T

    /**
     * Gets if a given plugin is enabled.
     *
     * @param name the name of the plugin
     * @return if the plugin is enabled
     */
    fun isPluginPresent(name: String): Boolean

    /**
     * Gets a plugin instance for the given plugin name
     *
     * @param name the name of the plugin
     * @param pluginClass the main plugin class
     * @param T the main class type
     * @return the plugin
     */
    fun <T> getPlugin(name: String, pluginClass: Class<T>): T?

    /**
     * Gets the plugin's class loader
     *
     * @return the class loader
     */
    fun getClazzLoader(): ClassLoader
}
