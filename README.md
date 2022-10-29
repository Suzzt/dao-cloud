# 项目介绍
    一个SpringBoot构建基于netty开发轻量级的微服务框架.麻雀虽小,五脏俱全

# 项目结构
    dao-cloud-rpc=rpc调用
    dao-cloud-config=注册+配置-中心
    dao-cloud-web=交互辅助页面
    dao-cloud-gateway=网关
    dao-cloud-boot=rpc-Spring-boot-start

#协议约定

    默认采用以下约定的协议

        魔数(3b)
        序列化方式(1b)
        版本(1b)
        数据包长度(4b)
        数据包内容

    序列化支持:jdk、json、protobuf(推荐)
    
    
    
