[中文](./README.md)

<div align="center">

# MultiLoginC

_✨ Coexisting Minecraft Authentication and Multiple External Authentications ✨_
_Community Edition_

[![GitHub license](https://img.shields.io/github/license/bytfr/MultiLoginC?style=flat-square)](https://github.com/bytfr/MultiLoginC/blob/master/LICENSE)

</div>

## Summary

MultiLoginC is a community-maintained version based on the original MultiLogin project. It is a plugin designed primarily for Minecraft proxy, aimed at supporting the coexistence of Minecraft authentication and multiple external authentication servers. It is used to connect players under two or more external authentication servers, allowing them to play together on the same server.

> Since the original MultiLogin project has been discontinued, community members have taken over maintenance of this project to continue providing support and updates.

## Features

- Supports up to 128 Yggdrasils from different sources coexisting simultaneously
- Authentication proxy and retry mechanism
- In-game profile management system
- Asynchronous/synchronous skin repair mechanism
- Support takeover of Floodgate

## Installation

The minimum requirement is `Java 21`, no need to install `authlib-injector`, no pre plugins required, and no need to add or change `JVM` parameters

~~How many steps does it take to put an elephant in a refrigerator?~~

1. [Download](https://github.com/bytfr/MultiLoginC/releases/latest) plugin
2. throw into plugins
3. launch the server

## Configuration

See details in [Wiki](https://github.com/bytfr/MultiLoginC/wiki)

## Build

1. Clone this project
2. Execute `./gradlew shadowJar` / `gradlew shadowJar`
3. Find what you need under `*/build/libs`

Or you can also

1. [Fork](https://github.com/bytfr/MultiLoginC/fork) this project
2. Enable Actions
3. Commit any file

## Bug Report

[new issue](https://github.com/bytfr/MultiLoginC/issues/new) Click here to submit your issue

## Contributors

<a href="https://github.com/bytfr/MultiLoginC/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=bytfr/MultiLoginC"  alt="Contributor's avatar"/>
</a>

[Want to be a contributor?](https://github.com/bytfr/MultiLoginC/pulls)
