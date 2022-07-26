package xyz.xenondevs.stringremapper

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.plugins.JavaPlugin
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.register
import xyz.xenondevs.stringremapper.Mappings.ResolveGoal
import xyz.xenondevs.stringremapper.task.RemapExtension
import xyz.xenondevs.stringremapper.task.StringRemapTask
import java.io.File

/**
 * A simple 'hello world' plugin.
 */
class StringRemapperGradlePlugin : Plugin<Project> {
    
    override fun apply(project: Project) {
        project.pluginManager.apply(JavaPlugin::class.java)
        project.repositories.mavenLocal {
            metadataSources {
                mavenPom()
                artifact()
            }
        }
        val extension = project.extensions.create<RemapExtension>("remapStrings")
        val remapTask = project.tasks.register<StringRemapTask>("remapStrings") {
            dependsOn(project.tasks.getByName("classes"))
        }.apply {
            configure {
                val (mojangMappings, spigotMappings, memberMappings) = resolveMappings(project, extension.spigotVersion.get())
                this.mojangMappings.set(mojangMappings)
                this.spigotMappings.set(spigotMappings)
                this.spigotMemberMappings.set(memberMappings)
                if (extension.inputClasses.isPresent && extension.inputClasses.get().isNotEmpty()) {
                    this.inputClasses.set(extension.inputClasses.get().map(::File))
                } else {
                    println("No input classes specified, defaulting to \"classes/main/kotlin\" and \"classes/main/java\"")
                    this.inputClasses.set(listOf(
                        project.buildDir.resolve("classes/kotlin/main"),
                        project.buildDir.resolve("classes/java/main")
                    ))
                }
                this.remapGoal.set(ResolveGoal.valueOf(extension.remapGoal.get().toUpperCase()))
                this.classes.set(extension.classes)
                this.copyClasses.set(extension.copyClasses)
            }
        }
        
        project.tasks["assemble"].dependsOn(remapTask)
    }
    
    private fun resolveMappings(project: Project, version: String): Triple<File, File, File> {
        val mojangMappings = project.dependencies.create("org.spigotmc:minecraft-server:$version:maps-mojang@txt").getFile(project)
        val spigotMappings = project.dependencies.create("org.spigotmc:minecraft-server:$version:maps-spigot@csrg").getFile(project)
        val memberMappings = project.dependencies.create("org.spigotmc:minecraft-server:$version:maps-spigot-members@csrg").getFile(project)
        return Triple(mojangMappings, spigotMappings, memberMappings)
    }
    
    private fun Dependency.getFile(project: Project) =
        project.configurations.detachedConfiguration(this).singleFile
    
    
}
