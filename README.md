# TLIB I18N

[![Maven Central Version](https://img.shields.io/maven-central/v/one.tranic/t-i18n)](https://central.sonatype.com/artifact/one.tranic/t-i18n)
[![javadoc](https://javadoc.io/badge2/one.tranic/t-i18n/javadoc.svg)](https://javadoc.io/doc/one.tranic/t-i18n)

Quick use, consistent behavior i18n wrapper.

## Feature
- Lightweight implementation, easy to use
- Support `json`, `yml/yaml`, `properties`, `xml`
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

If the target environment contains gson, no installation is necessary.

#### Use YAML as input
`compileOnly("org.yaml:snakeyaml:2.4")`

If the target environment contains snakeyaml, no installation is necessary.

#### Use Properties as input
No additional dependencies are required as Properties support is built into the JDK.

#### Use XML as input
No additional dependencies are required as XML support is built into the JDK.

#### Use Kyori as Output
If you use Paper or its forks, these dependencies are not required.

`compileOnly("net.kyori:adventure-api:4.20.0")`

`compileOnly("net.kyori:adventure-text-minimessage:4.20.0")`

#### Use BaseComponent as Output
It is only applicable when using BungeeCord.

#### Use Minecraft Component as Output
This is not possible for now, you should use String output and wrap it manually, 
I will provide a separate wrapper to solve this problem later.

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
