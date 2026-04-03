[English](./README.en.md)

<div align="center">

# MultiLoginC

_✨ 正版与多种外置登录共存 ✨_\
_社区维护版 (Community Edition)_

[!\[GitHub license\](https://img.shields.io/github/license/bytfr/MultiLoginC?style=flat-square null)](https://github.com/bytfr/MultiLoginC/blob/master/LICENSE)

</div>

## 概述

MultiLoginC 是基于 MultiLogin 项目的社区维护版本。它是一款主要为 Minecraft 代理端设计的插件，旨在实现对正版与多种外置登录共存的支持，用于连接两个或多个外置验证服务器下的玩家，使他们能够在同一个服务器上一起游戏。

> 由于原 MultiLogin 项目已停止维护，社区成员接手维护此项目，旨在继续为大家提供支持和更新。

## 特性

- 支持多达 128 个不同来源的 Yggdrasil 同时共存
- 鉴权代理、重试机制
- 游戏内档案管理系统
- 异步/同步皮肤修复机制
- 支持接管 Floodgate

## 安装

最低需要 `Java 21`， 不需要安装 `authlib-injector` ，没有任何前置插件，也不需要添加和更改 `JVM` 参数

~~把大象装进冰箱需要几步？~~

1. [下载](https://github.com/bytfr/MultiLoginC/releases/latest) 插件
2. 丢进 plugins
3. 启动服务器

## 配置

详见 [Wiki](https://github.com/bytfr/MultiLoginC/wiki)

## 构建

1. 克隆这个项目
2. 执行 `./gradlew shadowJar` / `gradlew shadowJar`
3. 在 `*/build/libs` 下寻找你需要的

或者你也可以

1. [Fork](https://github.com/bytfr/MultiLoginC/fork) 此项目
2. 开启 Actions
3. 随便提交一个文件

## BUG 汇报

[new issue](https://github.com/bytfr/MultiLoginC/issues/new) 点击此处，提交你的问题

## 贡献者

<a href="https://github.com/bytfr/MultiLoginC/graphs/contributors">
  < img src="https://contrib.rocks/image?repo=bytfr/MultiLoginC"  alt="作者头像"/>
</a>

[我也想为贡献者之一？](https://github.com/bytfr/MultiLogin/pulls)
