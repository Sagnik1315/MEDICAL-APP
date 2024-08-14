import com.github.javaparser.printer.lexicalpreservation.Added
import org.gradle.kotlin.dsl.provider.inClassPathMode

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.google.gms.google.services) apply false
}
