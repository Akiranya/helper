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

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import me.lucko.helper.Schedulers
import me.lucko.helper.Services
import me.lucko.helper.config.ConfigFactory
import me.lucko.helper.internal.LoaderUtils
import me.lucko.helper.scheduler.HelperExecutors
import me.lucko.helper.terminable.composite.CompositeTerminable
import me.lucko.helper.terminable.module.TerminableModule
import me.lucko.helper.utils.CommandMapUtil
import me.lucko.helper.utils.JarExtractor
import ninja.leaping.configurate.ConfigurationNode
import org.bukkit.command.CommandExecutor
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.event.Listener
import org.bukkit.plugin.ServicePriority
import java.io.File
import java.util.Objects.requireNonNull
import java.util.concurrent.TimeUnit

/**
 * An "extended" JavaPlugin class.
 */
abstract class KExtendedJavaPlugin : SuspendingJavaPlugin(), KHelperPlugin {
    // the backing terminable registry
    private lateinit var terminableRegistry: CompositeTerminable

    // are we the plugin that's providing helper?
    private var isLoaderPlugin = false

    // Used by subclasses to perform logic for plugin load/enable/disable.
    protected open suspend fun load() {}
    protected open suspend fun enable() {}
    protected open suspend fun disable() {}

    override suspend fun onLoadAsync() {
        // LoaderUtils.getPlugin() has the side effect of caching the loader ref
        // do that nice and early. also store whether 'this' plugin is the loader.
        val loaderPlugin = LoaderUtils.getPlugin()
        isLoaderPlugin = this === loaderPlugin
        terminableRegistry = CompositeTerminable.create()

        // call subclass
        load()
    }

    override suspend fun onEnableAsync() {
        // schedule cleanup of the registry
        Schedulers.builder()
            .async()
            .after(10, TimeUnit.SECONDS)
            .every(30, TimeUnit.SECONDS)
            .run { terminableRegistry.cleanup() }
            .bindWith(terminableRegistry)

        // call subclass
        enable()
    }

    override suspend fun onDisableAsync() {
        // call subclass
        disable()

        // terminate the registry
        terminableRegistry.closeAndReportException()
        if (isLoaderPlugin) {
            // shutdown the scheduler
            HelperExecutors.shutdown()
        }
    }

    override fun <T : AutoCloseable> bind(terminable: T): T {
        return terminableRegistry.bind(terminable)
    }

    override fun <T : TerminableModule> bindModule(module: T): T {
        return terminableRegistry.bindModule(module)
    }

    override fun <T : Listener> registerListener(listener: T): T {
        requireNonNull(listener, "listener")
        server.pluginManager.registerEvents(listener, this)
        return listener
    }

    override fun <T : Listener> registerTerminableListener(listener: T): TerminableListener<T> {
        requireNonNull(listener, "listener")
        server.pluginManager.registerEvents(listener, this)
        return TerminableListener(listener)
    }

    override fun <T : Listener> registerSuspendListener(listener: T): T {
        requireNonNull(listener, "listener")
        server.pluginManager.registerSuspendingEvents(listener, this)
        return listener
    }

    override fun <T : Listener> registerTerminableSuspendListener(listener: T): TerminableListener<T> {
        requireNonNull(listener, "listener")
        server.pluginManager.registerSuspendingEvents(listener, this)
        return TerminableListener(listener)
    }

    override fun <T : CommandExecutor> registerCommand(
        command: T,
        permission: String?,
        permissionMessage: String?,
        description: String?,
        vararg aliases: String,
    ): T {
        return CommandMapUtil.registerCommand<T>(
            this@KExtendedJavaPlugin,
            command,
            permission,
            permissionMessage,
            description,
            *aliases
        )
    }

    override fun <T : Any> getService(service: Class<T>): T {
        return Services.load(service)
    }

    override fun <T : Any> provideService(
        clazz: Class<T>,
        instance: T,
        priority: ServicePriority,
    ): T {
        return Services.provide(clazz, instance, this, priority)
    }


    override fun <T : Any> provideService(clazz: Class<T>, instance: T): T {
        return provideService(clazz, instance, ServicePriority.Normal)
    }

    override fun isPluginPresent(name: String): Boolean {
        return server.pluginManager.getPlugin(name) != null
    }

    override fun <T> getPlugin(name: String, pluginClass: Class<T>): T? {
        @Suppress("UNCHECKED_CAST")
        return server.pluginManager.getPlugin(name) as T?
    }

    private fun getRelativeFile(name: String): File {
        dataFolder.mkdirs()
        return File(dataFolder, name)
    }

    override fun getBundledFile(name: String): File {
        requireNonNull(name, "name")
        val file = getRelativeFile(name)
        if (!file.exists()) {
            saveResource(name, false)
        }
        return file
    }

    override fun loadConfig(file: String): YamlConfiguration {
        requireNonNull(file, "file")
        return YamlConfiguration.loadConfiguration(getBundledFile(file))
    }

    @Deprecated("Subject to change when upgrading to configurate 4.0")
    override fun loadConfigNode(file: String): ConfigurationNode {
        requireNonNull(file, "file")
        return ConfigFactory.yaml().load(getBundledFile(file))
    }

    @Deprecated("Subject to change when upgrading to configurate 4.0")
    override fun <T : Any> setupConfig(file: String, configObject: T): T {
        requireNonNull(file, "file")
        requireNonNull(configObject, "configObject")
        val f = getRelativeFile(file)
        ConfigFactory.yaml().load(f, configObject)
        return configObject
    }

    override fun saveResource(name: String) {
        if (!getRelativeFile(name).exists()) {
            saveResource(name, false)
        }
    }

    override fun saveResourceRecursively(name: String) {
        saveResourceRecursively(name, false)
    }

    override fun saveResourceRecursively(name: String, overwrite: Boolean) {
        val targetDirectory = getRelativeFile(name)
        if (overwrite || !targetDirectory.exists()) {
            JarExtractor.extractJar(clazzLoader, name, targetDirectory)
        }
    }

    override val clazzLoader: ClassLoader
        get() = super.getClassLoader()
}
