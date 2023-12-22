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
import ninja.leaping.configurate.ConfigurationNode
import org.bukkit.command.CommandExecutor
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.ServicePriority
import java.io.File
import javax.annotation.Nonnull

interface KHelperPlugin : Plugin, TerminableConsumer {
    val clazzLoader: ClassLoader

    /**
     * Register a listener with the server.
     *
     *
     * [me.lucko.helper.Events] should be used instead of this method in most cases.
     *
     * @param listener the listener to register
     * @param <T> the listener class type
     * @return the listener
     */
    fun <T : Listener> registerListener(@Nonnull listener: T): T

    /**
     * Register a listener with the server.
     *
     *
     * The return value is a [TerminableListener] that wraps the listener in it.
     * The [TerminableListener] will automatically unregister the listener when closed.
     *
     * @param listener the listener to register
     * @param <T>      the listener class type
     * @return the listener
     */
    fun <T : Listener> registerTerminableListener(listener: T): TerminableListener<T>

    /**
     * Registers a CommandExecutor with the server
     *
     * @param command the command instance
     * @param aliases the command aliases
     * @param <T> the command executor class type
     * @return the command executor
     */
    fun <T : CommandExecutor> registerCommand(command: T, vararg aliases: String): T {
        return registerCommand<T>(command, null, null, null, *aliases)
    }

    /**
     * Registers a CommandExecutor with the server
     *
     * @param command the command instance
     * @param permission the command permission
     * @param permissionMessage the message sent when the sender doesn't the required permission
     * @param description the command description
     * @param aliases the command aliases
     * @param <T> the command executor class type
     * @return the command executor
     */
    fun <T : CommandExecutor> registerCommand(
        command: T, permission: String?,
        permissionMessage: String?,
        description: String?,
        vararg aliases: String
    ): T

    /**
     * Gets a service provided by the ServiceManager
     *
     * @param service the service class
     * @param T the class type
     * @return the service
     */
    fun <T : Any> getService(service: Class<T>): T

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
     * Gets a bundled file from the plugins resource folder.
     *
     *
     * If the file is not present, a version of it copied from the jar.
     *
     * @param name the name of the file
     * @return the file
     */
    fun getBundledFile(name: String): File

    /**
     * Loads a config file from a file name.
     *
     *
     * Behaves in the same was as [.getBundledFile] when the file is not present.
     *
     * @param file the name of the file
     * @return the config instance
     */
    fun loadConfig(file: String): YamlConfiguration

    /**
     * Loads a config file from a file name.
     *
     *
     * Behaves in the same was as [.getBundledFile] when the file is not present.
     *
     * @param file the name of the file
     * @return the config instance
     */
    @Deprecated("Subject to change when upgrading to configurate 4.0")
    fun loadConfigNode(file: String): ConfigurationNode

    /**
     * Populates a config object.
     *
     * @param file the name of the file
     * @param configObject the config object
     * @param <T> the config object type
    </T> */
    @Deprecated("Subject to change when upgrading to configurate 4.0")
    fun <T : Any> setupConfig(file: String, configObject: T): T

    /**
     * Saves the raw contents of any resource embedded with a plugin's .jar
     * file assuming it can be found using [.getResource].
     *
     *
     * The resource is saved into the plugin's data folder using the same
     * hierarchy as the .jar file (subdirectories are preserved).
     *
     *
     * This method will not overwrite existing files and
     * will silently fail if the files already exist.
     *
     * @param name the embedded resource path to look for within the
     * plugin's .jar file. (No preceding slash).
     * @throws IllegalArgumentException if the resource path is null, empty,
     * or points to a nonexistent resource.
     */
    fun saveResource(name: String)

    /**
     * Saves a bundled file from the plugins resource folder.
     *
     * @param name the name of the file
     */
    fun saveResourceRecursively(name: String)

    /**
     * Saves a bundled file from the plugins resource folder, optionally overwriting any existing file.
     *
     * @param name the name of the file
     */
    fun saveResourceRecursively(name: String, overwrite: Boolean)

    /**
     * Gets the plugin's class loader
     *
     * @return the class loader
     */
    fun getClassloader(): ClassLoader {
        return clazzLoader
    }
}
