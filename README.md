# 项目介绍
    一个SpringBoot构建基于netty开发轻量级的微服务框架.麻雀虽小,五脏俱全

# 项目结构
    dao-cloud-core = 核心
    dao-cloud-config = 注册+配置-中心
    dao-cloud-web = 交互辅助web页面(后续会把web页面都集成这里暴露出来)
    dao-cloud-gateway = 网关
    dao-cloud-spring-boot-starter = rpc的依赖的jar
    dao-monitor = 监控性能

# 协议约定

    魔数(3-byte)
    版本(1-byte)
    消息类型(1-byte)
    序列化方式(1-byte)  支持:jdk、json、protobuf(推荐-todo)
    数据包长度(4-byte)
    数据包内容(~)

    todo 自定义协议,这是该项目的看点

# 快速开始
    暂时无需任何配置(追求轻)
    provider还是consumer直接引入(注意:目前只支持Spring Boot自动注入的模式)
    <dependency>
        <groupId>org.junmo</groupId>
        <artifactId>dao-cloud-spring-boot-starter</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>

    项目中有一个示例
    0.把公共的接口请放在api-common中
    1.先启动dao-cloud-config
    2.然后就是provider与consumer,项目中提供了工程(dao-cloud-example)来示例使用
        先启动provider,再启动consumer(其实启反也可以)
    3.验证! http://127.0.0.1:19998/dao-cloud-example-consumer/demo

    查看config注册情况(在启动dao-cloud-config后)
    todo 可视化页面(由dao-cloud-web提供出来)。目前可以通过
    http://127.0.0.1:5555/dao-cloud-config/get/proxy-server ====查看所有服务的注册
    http://127.0.0.1:5555/dao-cloud-config/get/server-nodes?proxy=demo =====查看某个proxy的所有服务节点

# 系统架构实现
    todo 
    
        



   
    
    
    
