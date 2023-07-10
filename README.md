# 项目介绍
DaoCloud通过SpringBoot构建基于netty开发轻量级的微服务框架.麻雀虽小,五脏俱全;
本项目追求轻量、易接入、自定义协议、高可用、高性能、高扩展、易上手等特性;
致力于简化应用程序之间的RPC调用，并为应用程序提供方便、无代码入侵、稳定和高效的点对点远程微服务调用解决方案。对于开发人员来说,dao-cloud的提供了丰富的模型抽象和可扩展接口,为求一站式解决微服务带来的系统架构复杂度,包括路由、负载平衡、故障转移、性能监控、微服务治理等;

# 系统架构
![dao-cloud(v2)](https://github.com/Suzzt/dao-cloud/assets/27397567/90102c50-e0a5-41b2-8188-f457bb47b755)

![组件架构](https://github.com/Suzzt/dao-cloud/assets/27397567/c98d9657-bfa0-4dd3-b0ac-0bff133ab0f5)

    整个系统不严格重度依赖任何中间价组件即可启动，只需要一个操作系统依赖！
    1.注册中心与配置中心都是center来提供能力，两者是一体化集成的
    2.这里服务与center、provider与comsumer都是长连接的提供高可用的方案，这里的心跳检测存活分为两种，一种是重心跳，一种轻量级单向交互的心跳(该单向并不是指只有一方发送心跳！)
    3.服务间设计由于有长连接的存在，因此center宕机对现有服务间的调用没有影响，但是对配置中心的回调有一定程度的通知失败
    4.组件服务都有高可用是采用多节点保证的，高性能是采用最轻化的协议、同通道复用保证的。能保证整个微服务始终在一定程度下提供能力，不保证一些场景下的一致性


# center集群架构
![dao-center-cluster](https://github.com/Suzzt/dao-cloud/assets/27397567/70c3c971-faac-4423-8488-4d61df91b4a3)

    首先center cluster一个同步节点复制、相互独立去中心化的集群方案！建议center cluster数不要超过100个节点
    1.center集群之间: center节点交互通过重心跳维持集群，加入或宕机在center中相互同步节点数据
    2.center与服务连接: 服务节点端通过拉取存活集群节点来轮询注册一个节点，一旦重试失败，换下一个存活节点，继续一直向center发送重心跳(即一直注册)
    3.center与服务负载: center cluster节点加入或宕机时，center会自主发送协调server负载情况
    4.center与服务数据: 通过心跳channel发送数据(config、server-info)同步，这里采用的单向发送，即发送端只管把数据发送出去，不关心服务端真的接收保存成功

# 项目结构
    dao-cloud-core = 核心
    dao-cloud-center = 注册+配置-中心
    dao-cloud-gateway = 网关
    dao-cloud-spring-boot-starter = rpc的依赖的jar
    dao-cloud-monitor = 监控性能
    dao-cloud-example = 使用示例

# dao-cloud提供了什么功能？
    rpc服务调用
        提供服务之间的RPC调用能力,在轻量dao协议(tcp)下,降低你每次服务间调用的代价
    服务注册管理
        帮助rpc服务发现和服务运行状况检查,使服务注册自己与发现其他服务变得简单。还提供服务的实时健康检查，以防止向不健康的主机或服务实例发送请求
    动态配置管理
        服务中的配置允许您在动态变更的方式下异步通知订阅服务。还支持动态变更配置后回调订阅服务中的监听事件
    服务监控与链路追踪
        监控服务负载流量压力,追踪各服务间调用的完整链路,归置日志统一收集输出打印,让你知道每个节点性能消耗情况
    高性能高可用集群
        center提供了高可用、高性能、更简单化启动集群能力

# dao-协议
    +---------+---------+--------------+----------------+----------------+----------------+
    |  magic  | version | message type | serialize type |   data length  |  data content  |
    | 3(byte) | 1(byte) |    1(byte)   |     1(byte)    |     4(byte)    |       ~~       |
    +---------+---------+--------------+----------------+----------------+----------------+
    version: 暂时没用到
    serialize type 支持: jdk(DTO请实现序列化接口)、json、hessian(推荐默认)

# 快速开始
无需任何配置(追求轻量).所有功能组件都是通过SpringBoot自动装配一键化启动(引入启动依赖jar包)

    <dependency>
        <groupId>org.junmo</groupId>
        <artifactId>dao-cloud-spring-boot-starter</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>

rpc注解用法说明(其实用法与dubbo、spring-cloud、sofa这些差不多一致)
    
    每个provider一定要设置自己的proxy名字! 确定唯一接口: proxy+provider+version
    @DaoService = 用于服务注册    provider:暴露服务的provider名称, version:发布版本, serialize:序列化选择
    @DaoReference = 用于服务注入  provider:暴露服务的provider名称, version:发布版本, serialize:序列化选择, loadbanalce:负载路由选择, timeout:超时时间

注册｜配置中心(引入dao-cloud-center的pom依赖jar包)

    <dependency>
        <groupId>org.junmo</groupId>
        <artifactId>dao-cloud-center</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
    通过注解EnableDaoCloudCenter标注在一个SpringBoot工程的启动类上,告诉这是dao-cloud的注册|配置中心(todo 页面)
    
    查看服务注册情况
        http://127.0.0.1:5555/dao-cloud/register/server ==== 查看所有服务信息
        http://127.0.0.1:5555/dao-cloud/register/proxy?proxy=demo&version=0 ==== 查看服务列表详情
    
    配置中心管理
        http://127.0.0.1:5555/dao-cloud/config/save ==== 更新配置信息
        http://127.0.0.1:5555/dao-cloud/config/query ==== 获取配置信息
    DaoConfig这个类提供了服务对配置信息的获取、订阅(详情看dao-cloud-example示例)
        DaoConfig.getConf  ==== 获取配置
        DaoConfig.subscribe  ==== 订阅配置,在监听到订阅的配置发生变化时,做某些事(回调)
        注意: 此外你在配置中心更改配置后,配置中心会自动刷新到服务上

项目(dao-cloud-example)中有一个示例

    0.把公共的接口请放在api-common中,就是你要暴露出去的函数方法
    1.先启动web工程(通过@EnableDaoCloudCenter注解搞定注册中心)
    2.然后就是provider与consumer,项目中提供了工程(dao-cloud-example)来示例使用
    先启动provider,再启动consumer(其实启反也可以)
    3.验证! http://127.0.0.1:19998/dao-cloud-example-consumer/demo (rpc调用测试)
    还有其他test-demo也放在该工程中

**本项目是由作者利用平时自由时间创建或迭代,所有的微服务异常情况没办法测试全,未在真实项目上实践过! 请酌情考虑使用,出事故拒不负责(^_^)! 有问题请提issues;**
    
        



   
    
    
    
