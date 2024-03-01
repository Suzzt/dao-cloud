<div style="margin-top: 50px; margin-bottom: -30px;">
    <p align="center" style="display: flex; justify-content: center; gap: 10px;">
        <img src="https://github.com/Suzzt/dao-cloud/assets/27397567/e1d2c6d9-d1f8-4bb3-917a-b0a4c9de3f3e" style="max-width: 100px; height: auto;">
    </p>
    <p align="center" style="display: flex; justify-content: center; gap: 10px;">
      <img src="https://img.shields.io/badge/dao_cloud-Microservices-33cc99" style="max-width: 100px; height: auto;">
      <img src="https://img.shields.io/badge/Source-github-d021d6?style=flat&logo=GitHub" style="max-width: 100px; height: auto;">
      <img src="https://img.shields.io/badge/JDK-1.8+-ffcc00" style="max-width: 100px; height: auto;">
      <img src="https://img.shields.io/badge/Apache_License-2.0-33ccff" style="max-width: 100px; height: auto;">
      <img src="https://img.shields.io/badge/maven-006633" style="max-width: 100px; height: auto;">
      <img src="https://img.shields.io/badge/build-passing-green" style="max-width: 100px; height: auto;">
    </p>
</div>

# 项目介绍
DaoCloud通过SpringBoot构建基于netty开发轻量级的微服务框架.麻雀虽小,五脏俱全(完全自研开发且完全开源,放心使用);
本项目追求轻量、易接入、自定义协议、高可用、高性能、高扩展、易上手等特性;
致力于简化应用程序之间的RPC调用，并为应用程序提供方便、无代码入侵、稳定和高效的点对点远程微服务调用解决方案。对于开发人员来说,dao-cloud的提供了丰富的模型抽象和可扩展接口,为求一站式解决微服务带来的系统架构复杂度,包括路由、负载平衡、故障转移、性能监控、微服务治理等;

# 系统架构
![dao-cloud(v2)](https://github.com/Suzzt/dao-cloud/assets/27397567/90102c50-e0a5-41b2-8188-f457bb47b755)

![组件架构](https://github.com/Suzzt/dao-cloud/assets/27397567/bb54e056-4ece-44a6-8393-3ed1f62d4f0a)

    整个系统不严格重度依赖任何中间价组件即可启动，只需要一个操作系统依赖！
    1.注册中心与配置中心都是center来提供能力，两者是一体化集成的
    2.这里服务与center、provider与comsumer都是长连接的提供高可用的方案，这里的心跳检测存活分为两种，一种是重心跳，一种轻量级单向交互的心跳(该单向并不是指只有一方发送心跳！)
    3.服务间设计由于有长连接的存在，因此center宕机对现有服务间的调用没有影响，但是对配置中心的回调有一定程度的通知失败
    4.组件服务都有高可用是采用多节点保证的，高性能是采用最轻化的协议、同通道复用保证的。能保证整个微服务始终在一定程度下提供能力，不保证一些场景下的一致性


# center集群架构
![dao-center-cluster](https://github.com/Suzzt/dao-cloud/assets/27397567/70c3c971-faac-4423-8488-4d61df91b4a3)

    首先center cluster一个同步节点复制、相互独立去中心化的集群方案！建议center cluster数不要超过100个节点
    1.center集群之间: center节点交互通过重心跳维持集群，加入或宕机在center中相互同步节点数据，节点失效时会将未收到心跳回应的对应节点设置为暂时失效节点，心跳逻辑维持，一直到有响应返回变正常节点
    2.center与服务连接: 服务节点端通过拉取存活集群节点来轮询注册一个节点，一旦重试失败，换下一个存活节点，继续一直向center发送重心跳(即一直注册)
    3.center与服务负载: center cluster节点加入或宕机时，center会自主发送协调server负载情况
    4.center与服务数据: 通过心跳channel发送数据(config、server-info)同步，这里采用异步发送，并要求响应返回结果，在失败下会一定重试，并做了幂等

# 网关设计架构
![网关设计](https://github.com/Suzzt/dao-cloud/assets/27397567/d4195cb7-7ffd-4a8f-b3bd-ac4e34acfe78)

# 项目结构
    dao-cloud-core = 核心
    dao-cloud-center = 注册+配置-中心
    dao-cloud-gateway = 网关
    dao-cloud-spring-boot-starter = rpc的依赖的jar
    dao-cloud-example = 使用示例

# dao-cloud提供了什么能力？

## 1. rpc服务调用
提供服务之间的RPC调用能力,在轻量dao协议(tcp)下,降低你每次服务间调用的代价
## 2. 服务注册管理
帮助rpc服务发现和服务运行状况检查,使服务注册自己与发现其他服务变得简单。还提供服务的实时健康检查，以防止向不健康的主机或服务实例发送请求
## 3. 动态配置管理
服务中的配置允许您在动态变更的方式下异步通知订阅服务。还支持动态变更配置后回调订阅服务中的监听事件
## 4. 服务监控与链路追踪(todo)
监控服务负载流量压力,追踪各服务间调用的完整链路,归置日志统一收集输出打印,让你知道每个节点性能消耗情况
## 5. 高性能高可用center集群(注册中心、配置中心)
为微服务提供了自身的高可用、高性能、合并愈合以及更简单的集群恢复能力
## 6. 统一网关(todo) doing
提供请求的鉴权、限流等能力,整合服务入口的统一

# dao-协议
    +---------+---------+--------------+----------------+----------------+----------------+
    |  magic  | version | message type | serialize type |   data length  |  data content  |
    | 3(byte) | 1(byte) |    1(byte)   |     1(byte)    |     4(byte)    |       ~~       |
    +---------+---------+--------------+----------------+----------------+----------------+
    version: 暂时没用到
    serialize type 支持: jdk(DTO请实现序列化接口)、json、hessian(推荐默认)

# 快速开始
可以先看这个部署方案: http://47.95.39.37:5555/dao-cloud/help
<img width="1809" alt="dao-cloud-web" src="https://github.com/Suzzt/dao-cloud/assets/27397567/a3db691a-4797-4ae2-94ee-694b1065e3d6">
无需任何配置(追求轻量).所有功能组件都是通过SpringBoot自动装配一键化启动(引入启动依赖jar包)

    <dependency>
        <groupId>org.dao</groupId>
        <artifactId>dao-cloud-spring-boot-starter</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>

rpc注解用法说明(其实用法与dubbo、spring-cloud、sofa这些差不多一致)
    
    每个provider一定要设置自己的proxy名字! 确定唯一接口: proxy+provider+version
    @DaoService = 用于服务注册    provider:暴露服务的provider名称, version:发布版本, serialize:序列化选择
    @DaoReference = 用于服务注入  provider:暴露服务的provider名称, version:发布版本, serialize:序列化选择, loadbanalce:负载路由选择, timeout:超时时间

注册｜配置中心(引入dao-cloud-center的pom依赖jar包)

    <dependency>
        <groupId>org.dao</groupId>
        <artifactId>dao-cloud-center</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
    通过注解EnableDaoCloudCenter标注在一个SpringBoot工程的启动类上,告诉这是dao-cloud的注册|配置中心(这里内嵌一个web页面)

    DaoConfig这个类提供了服务对配置信息的获取、订阅(详情看dao-cloud-example示例)
        DaoConfig.getConf  ==== 获取配置
        DaoConfig.subscribe  ==== 订阅配置,在监听到订阅的配置发生变化时,做某些事(回调)
        注意: 此外你在配置中心更改配置后,配置中心会自动刷新到服务上
        
服务注册管理
<img width="1808" alt="dao-cloud-registry" src="https://github.com/Suzzt/dao-cloud/assets/27397567/acf757f4-b60f-4f6a-9d2c-893999e2744c">

配置中心管理
<img width="1808" alt="dao-cloud-config" src="https://github.com/Suzzt/dao-cloud/assets/27397567/379fbb3b-5ade-45f9-ab1c-3415d6f0e39a">

项目(dao-cloud-example)中有一个示例

    0.把公共的接口请放在api-common中,就是你要暴露出去的函数方法
    1.先启动web工程(通过@EnableDaoCloudCenter注解搞定注册中心).你可以访问 http://127.0.0.1:5555/dao-cloud/index 来打开页面管控整个页面(用户名:admin,密码:123456)
    2.然后就是provider与consumer,项目中提供了工程(dao-cloud-example)来示例使用
    先启动provider,再启动consumer(其实启反也可以)
    3.验证! http://127.0.0.1:19998/dao-cloud-example-consumer/demo (rpc调用测试)
    还有其他test-demo也都放在工程dao-cloud-example中

<p style="display: flex; align-items: center; font-size: 16px; font-weight: bold;">
    本项目是由作者利用平时自由时间创建或迭代，感谢jetbrains
    <a href="https://www.jetbrains.com/?from=dao-cloud"><img src="https://resources.jetbrains.com/storage/products/company/brand/logos/jb_beam.png?_gl=1*1dm12e8*_ga*MTE5NTg5NzkyNC4xNTk1OTQyNTAy*_ga_9J976DJZ68*MTY5MzI0MTIwMi40My4wLjE2OTMyNDEyMDIuNjAuMC4w&_ga=2.181464359.1807781522.1693241203-1195897924.1595942502" style="height: 17px; margin-right: 5px;"></a>
    提供的license。所有的微服务异常情况没办法测试全，未在真实项目上实践过！请酌情考虑使用，出事故拒不负责(^_^)! 有问题请提issues;
</p>
    
    
