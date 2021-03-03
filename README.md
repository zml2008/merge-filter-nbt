# merge-filter-nbt

A simple harness for exposing `adventure-nbt`'s SNBT support for use as a filter in Araxis Merge.

I build this as a graal native image so it can be run with less JVM startup overhead, but it could just as easily be run as a normal Java application.


# Building

Fairly straightforward gradle build, `./gradlew build` to compile.

The buildscript is configured to look in the `jvm.graal` Gradle property for the GrallVM environment to use the `native-image` from, falling back to the `java.home` system property. Keep in mind that in order to build a native image on Windows, the gradle command has to be run from within the "x64 Native Tools Command Prompt for VS 2019".

# License

`merge-filter-nbt` is released under the terms of the GNU General Public License version 3 or later