package xyz.xenondevs.stringremapper.task

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

abstract class RemapExtension {

    abstract val spigotVersion: Property<String>
    
    abstract val inputClasses: ListProperty<String>
    
    abstract val classes: ListProperty<String>
    
    abstract val copyClasses: Property<Boolean>

    abstract val remapGoal: Property<String>
    
}