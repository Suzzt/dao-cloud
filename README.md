# 项目介绍
通过SpringBoot构建基于netty开发轻量级的微服务框架.麻雀虽小,五脏俱全;
本项目追求轻量、易接入、自定义协议、高可用、高性能、高扩展、易上手等特性;
致力于简化应用程序之间的RPC调用，并为应用程序提供方便、无代码入侵、稳定和高效的点对点远程微服务调用解决方案。对于开发人员来说,dao-cloud的提供了丰富的模型抽象和可扩展接口,为求一站式解决微服务带来的系统架构复杂度,包括路由、负载平衡、性能监控、微服务治理等;

# 系统架构
![img.png](img.png)

# 项目结构
dao-cloud-core = 核心
dao-cloud-config = 注册+配置-中心
dao-cloud-web = 交互辅助web页面(后续会把web页面都集成这里暴露出来)
dao-cloud-gateway = 网关
dao-cloud-spring-boot-starter = rpc的依赖的jar
dao-cloud-monitor = 监控性能

# dao-协议
魔数(3-byte)
版本(1-byte)
消息类型(1-byte)
序列化方式(1-byte)  支持:jdk、json、protobuf(推荐-todo)
数据包长度(4-byte)
数据包内容(~)

todo 自定义协议,这是该项目的看点

# 快速开始
暂时无需任何配置(追求轻)
不管是provider还是consumer都是直接引入的方式(注意:目前只支持Spring Boot自动注入的模式)

    <dependency>
        <groupId>org.junmo</groupId>
        <artifactId>dao-cloud-spring-boot-starter</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
    
查看config注册情况(在启动dao-cloud-config后)
todo 可视化页面(由dao-cloud-web提供出来)。目前可以通过
http://127.0.0.1:5555/dao-cloud-config/get/proxy-server ====查看所有服务的注册
http://127.0.0.1:5555/dao-cloud-config/get/server-nodes?proxy=demo =====查看某个proxy的所有服务节点

    项目中有一个示例
    0.把公共的接口请放在api-common中,就是你要暴露出去的函数方法
    1.先启动dao-cloud-config
    2.然后就是provider与consumer,项目中提供了工程(dao-cloud-example)来示例使用
    先启动provider,再启动consumer(其实启反也可以)
    3.验证! http://127.0.0.1:19998/dao-cloud-example-consumer/demo


**本项目是由作者利用平时自由时间创建或迭代,所有的微服务异常情况没办法测试全,未在真实项目上实践过!请酌情考虑使用,出事故本人拒不负责(^_^)!有问题请提issues;**
    
        



   
    
    
    
