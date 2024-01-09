@file:OptIn(ExperimentalTypeInference::class)

package me.lucko.helper.text3

import com.google.common.collect.ListMultimap
import com.google.common.collect.MultimapBuilder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.TranslatableComponent
import net.kyori.adventure.text.TranslationArgument
import kotlin.experimental.ExperimentalTypeInference

class I18nLore private constructor(
    private val format: List<String>,
) {
    private val sanitizers: HashMap<String, () -> Boolean> = HashMap()
    private val registry: HashMap<String, TranslatableComponent> = HashMap()
    private val replaced: ListMultimap<String, TranslatableComponent> = MultimapBuilder.ListMultimapBuilder.hashKeys().arrayListValues().build()

    companion object Factory {
        /**
         * 创建一个新的 [I18nLore] 实例。
         *
         * 给定的参数 [format] 中的每一个字符串必须是合法的 [TranslatableComponent.key]。下面是一个合法的例子：
         *
         * ```yaml
         * lore:
         *   - menu.enchantment.icon.description
         *   - general.empty
         *   - menu.enchantment.icon.rarity
         *   - menu.enchantment.icon.target
         *   - menu.enchantment.icon.level
         *   - general.empty
         *   - menu.enchantment.icon.conflict.title
         *   - menu.enchantment.icon.conflict.item
         *   - menu.enchantment.icon.charging.title
         *   - menu.enchantment.icon.charging.fuel_item
         *   - menu.enchantment.icon.charging.consume_amount
         *   - menu.enchantment.icon.charging.recharge_amount
         *   - menu.enchantment.icon.charging.max_amount
         *   - menu.enchantment.icon.obtaining.title
         *   - menu.enchantment.icon.obtaining.enchanting
         *   - menu.enchantment.icon.obtaining.villager
         *   - menu.enchantment.icon.obtaining.loot_generation
         *   - menu.enchantment.icon.obtaining.fishing
         *   - menu.enchantment.icon.obtaining.mob_spawning
         *   - general.empty
         *   - menu.enchantment.icon.preview
         * ```
         *
         * @param format 描述的格式
         * @return 一个新的 [I18nLore]
         */
        fun format(format: List<String>): I18nLore {
            return I18nLore(format)
        }
    }

    /**
     * 注册一个 [TranslatableComponent]，并将其 [TranslatableComponent.key] 设置为 [key]。
     *
     * @param key 将作为 [TranslatableComponent.key]
     */
    fun register(key: String) {
        registry[key] = Component.translatable(key)
    }

    /**
     * 跟 [register] 一样，只不过是一次性注册多个。
     */
    fun register(keys: Iterable<String>) {
        keys.forEach { key -> register(key) }
    }

    /**
     * 为指定的 [TranslatableComponent] 添加 [TranslationArgument]。
     *
     * @param key 翻译的键
     * @param args 翻译的变量
     * @throws IllegalArgumentException 如果 [key] 还未注册
     */
    @JvmName("argumentsListComponent")
    fun arguments(key: String, args: List<ComponentLike>) {
        val raw = requireNotNull(registry[key])
        val new = raw.arguments(args)
        replaced.put(key, new)
    }

    fun arguments(key: String, vararg args: ComponentLike) = arguments(key, args.toList())

    @JvmName("argumentsListComponentGetter")
    @OverloadResolutionByLambdaReturnType // WTF ???
    fun arguments(key: String, args: () -> List<ComponentLike>) = arguments(key, args())

    @JvmName("argumentsComponentGetter")
    @OverloadResolutionByLambdaReturnType
    fun arguments(key: String, args: () -> ComponentLike) = arguments(key, args())

    @JvmName("argumentsListString")
    fun arguments(key: String, args: List<String>) = arguments(key, args.map(String::mini))

    fun arguments(key: String, vararg args: String) = arguments(key, args.toList())

    @JvmName("argumentsListStringGetter")
    @OverloadResolutionByLambdaReturnType
    fun arguments(key: String, args: () -> List<String>) = arguments(key, args())

    @JvmName("argumentsStringGetter")
    @OverloadResolutionByLambdaReturnType
    fun arguments(key: String, args: () -> String) = arguments(key, args())

    /**
     * 为指定的 [TranslatableComponent] 添加 [TranslationArgument]。
     *
     * 该函数会逐个将 [items] 中的元素作为**首个** [TranslationArgument] 添加到 [key]
     * 所对应的 [TranslatableComponent] 上，最终形成等同于 [items] 长度的一个列表
     * ([List]<[TranslatableComponent]>)。
     *
     * 例如，如果 [items] 中有3个 [ComponentLike]，那么最终同一个 [key] 将对应3个
     * [TranslatableComponent]。这其中：
     * - 第一个 [TranslatableComponent] 的变量 `{0}` 为 [items] 中的第一个元素
     * - 第二个 [TranslatableComponent] 的变量 `{0}` 为 [items] 中的第二个元素
     * - 第三个 [TranslatableComponent] 的变量 `{0}` 为 [items] 中的第三个元素
     *
     * 最后，这3个 [TranslatableComponent] 都将在替换模板的时候被展开，也就是这3个
     * [TranslatableComponent] 将替换掉模板中 [key] 所对应的内容。
     *
     * @param key 翻译的键
     * @param items 翻译的参数
     * @throws IllegalArgumentException 如果 [key] 还未注册
     */
    @JvmName("argumentsManyListComponent")
    fun argumentsMany(key: String, items: List<ComponentLike>) {
        // Hints: items.size 就是最终要复制的行数
        val raw: TranslatableComponent = requireNotNull(registry[key]) // 要复制的 component
        val map: List<TranslatableComponent> = items.map { raw.arguments(it) } // 只替换第一个遇到的 argument
        replaced.putAll(key, map)
    }

    @JvmName("argumentsManyListString")
    fun argumentsMany(key: String, items: List<String>) = argumentsMany(key, items.map(String::mini))

    @JvmName("argumentsManyListComponentGetter")
    @OverloadResolutionByLambdaReturnType
    fun argumentsMany(key: String, items: () -> List<ComponentLike>) = argumentsMany(key, items())

    @JvmName("argumentsManyListString")
    @OverloadResolutionByLambdaReturnType
    fun argumentsMany(key: String, items: () -> List<String>) = argumentsMany(key, items())

    /**
     * 注册一个清理 [format] 的逻辑。
     *
     * 如果 [predicate] 返回 `true`，则会将 [format] 中包含 [pattern] 的字符串元素移除。
     *
     * @param pattern 要移除的字符串模式
     * @param predicate 执行该清理逻辑的前提条件
     */
    fun sanitize(pattern: String, predicate: () -> Boolean) {
        sanitizers[pattern] = predicate
    }

    fun build(): List<TranslatableComponent> {
        val format1 = format.toMutableList()

        // 清理 format，隐藏没必要呈现的内容
        for ((pattern, predicate) in sanitizers.entries) {
            if (predicate.invoke()) {
                format1.removeIf { it.contains(pattern) }
            }
        }

        // 根据清理后的 format，生成最终的 List<TranslatableComponent>
        val componentLore = format1.flatMap { replaced[it] }
        return componentLore
    }
}