package xyz.xenondevs.stringremapper.task

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import xyz.xenondevs.stringremapper.FileRemapper
import xyz.xenondevs.stringremapper.Mappings
import xyz.xenondevs.stringremapper.Mappings.ResolveGoal
import java.io.File

abstract class StringRemapTask : DefaultTask() {
    
    @get:Input
    abstract val inputClasses: ListProperty<File>
    
    @get:InputFile
    abstract val spigotMappings: RegularFileProperty
    
    @get:InputFile
    abstract val spigotMemberMappings: RegularFileProperty
    
    @get:InputFile
    abstract val mojangMappings: RegularFileProperty
    
    @get:Input
    abstract val remapGoal: Property<ResolveGoal>
    
    @get:Input
    abstract val classes: ListProperty<String>
    
    @get:Input
    @get:Optional
    abstract val copyClasses: Property<Boolean>
    
    @TaskAction
    fun remap() {
        println("Loading mappings...")
        loadMappings()
        println("Remapping...")
        performRemapping()
    }
    
    private fun loadMappings() {
        Mappings.loadSpigotMappings(spigotMappings.asFile.get().bufferedReader())
        Mappings.loadSpigotMappings(spigotMemberMappings.asFile.get().bufferedReader())
        Mappings.loadMojangMappings(mojangMappings.asFile.get().bufferedReader())
    }
    
    private fun performRemapping() {
        var whitelistedClasses = classes.orNull
        if (whitelistedClasses != null)
            whitelistedClasses = classes.get().map { it.replace('.', File.separatorChar) }
        // TODO copy classes
        inputClasses.get().filter { it.exists() && it.isDirectory }.forEach { dir ->
            dir.walkTopDown()
                .filter {
                    if (!it.isFile || it.extension != "class")
                        return@filter false
                    return@filter whitelistedClasses?.contains(it.toRelativeString(dir).substringBefore('.').substringBefore('$'))
                        ?: true
                }.forEach { file ->
                    println("Remapping Strings: ${file.path}")
                    FileRemapper(file).remap(remapGoal.get())
                }
        }
    }
    
}