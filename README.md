# TLIB I18N

[![Maven Central Version](https://img.shields.io/maven-central/v/one.tranic/t-i18n)](https://central.sonatype.com/artifact/one.tranic/t-i18n)
[![javadoc](https://javadoc.io/badge2/one.tranic/t-i18n/javadoc.svg)](https://javadoc.io/doc/one.tranic/t-i18n)

Quick use, consistent behavior i18n wrapper.

## Feature
- Lightweight implementation, easy to use
- Support `json`, `yml/yaml`
- Supports multiple output styles, such as `Standard String`, `Kyori Component` and `BungeeCord BaseComponent`

## Installation
### Maven
```xml
<dependency>
    <groupId>one.tranic</groupId>
    <artifactId>t-i18n</artifactId>
    <version>[VERSION]</version>
</dependency>
```

### Gradle (Groovy)
```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation 'one.tranic:t-i18n:[VERSION]'
}
```

### Gradle (Kotlin DSL)
```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("one.tranic:t-i18n:[VERSION]")
}
```

### Additional Installation
You need to manually install the dependencies of TI18N according to your requirements.

#### Use JSON as input
`compileOnly("com.google.code.gson:gson:2.13.0")`

#### Use YAML as input
`compileOnly("org.yaml:snakeyaml:2.4")`

or

`compileOnly("com.github.Carleslc.Simple-YAML:Simple-Yaml:1.8.4")`

If you use Spigot or its forks, you can choose not to install any 
adapter dependencies and choose `BukkitYAML` when selecting the 
adapter.

#### Use Kyori as Output
If you use Paper or its forks, these dependencies are not required.

`compileOnly("net.kyori:adventure-api:4.20.0")`

`compileOnly("net.kyori:adventure-text-minimessage:4.20.0")`

#### Use BaseComponent as Output
It is only applicable when using BungeeCord.

## License

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this soft except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
