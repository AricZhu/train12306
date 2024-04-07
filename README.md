# 背景
Java 高并发项目

# 定时调度任务

## Spring 自带的定时任务
- 在 Spring 中使用自带的调度功能比较简单，如下示例，开启一个 5s 的定时任务
```java
package com.train.batch.job;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class ScheduleDemo {

    @Scheduled(cron = "0/5 * * * * ?")
    private void test() {
        System.out.println("dida");
    }
}
```

- 一般这种定时任务只适用于单体应用，不适合集群。因为上述的 job 一般都不能重复跑，因此当上述代码部署到集群中后，每个应用都会跑这个 job，就会导致业务上的问题
- 虽然问题可以通过增加分布式锁来解决，但还是有其他问题，比如无法实时的更改定时任务的状态和策略

所以在实际企业应用中，一般使用第三方定时任务调度框架，比如下面的 quartz

## quartz
- 使用 quartz，需要实现 Job 接口
- 在配置中声明任务、触发器
- 详细代码可查看：QuartzDemo.java 和 QuartzConfig.java
- 
## 定时任务的并发执行问题
在定时任务中有一个很关键的问题就是任务的并发执行，假设上一个任务运行很耗时，超过了定时间隔，那么下一个任务不等上一个任务结束就立刻开启，两个人物并发运行会造成很多意想不到的问题。

上述的并发执行问题在 quartz 中可以通过添加 @DisallowConcurrentExecution 注解来解决。


# 日期序列化和反序列化问题
- 在前端传过来的时间往往是非标准的日期格式，后端在解析日期的时候会直接报错，如下是前端发送给后端的日期字符串：
```javascript
createTime: "2024-04-06 09:04:27"
```
- 要解决上述日期解析问题需要添加如下的标注来告诉后端如何解析当前的日期字符串。**注意**：发送给前端的日期序列化同样可以用下面的标注将 Date 对象序列化为日期字符串
```java
public class PassengerSaveReq {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
```

# Long 精度丢失问题
- 由于 Java 中的 Long 类型的最大值要比 JS 中的 Long 最大值大很多，因此后端如果直接返回 Long 类型到前端，会有精度丢失的问题
- 解决办法是在返回的对象中对应字段上添加 Json 序列化，如下：
```java
public class PassengerQueryResp {
    
  @JsonSerialize(using= ToStringSerializer.class)
  private Long id;
  
  // ...
}
```

# 线程本地变量
- 可以将登陆信息存放到线程本地变量中(ThreadLocal)，方便后续直接使用,详细代码可参考 `LoginMemberContext`
- 通过拦截器来实现统一的处理
  - 拦截器定义：`MemberInterceptor`
  - 拦截器注册：`SpringMvcConfig`

# 短信验证码登陆
- 使用图形验证码来防止脚本模拟发短信进行攻击

# 雪花算法
- 使用系统 ms 来作为 ID 值的方式不适合高并发场景
- 自增 ID 不适合分布式数据库，在分表分库场景下有问题。只适合小项目
- uuid 方式也不行，生成的值是乱序的，会影响索引，从而导致性能损耗
- 基本原理：64 位
    - 最高位 1bit，不用
    - 41 bit 时间戳：保证了生成的 id 是增加的
    - 10 bit 工作机器 id
    - 12 bit 序列号：同一台机器一个 ms 内最多可以生成 4096 个不同 id，当同一台机器 1ms 内的并发数超过 4096 的话，那么会等待下一个 ms 来生成剩下的 id
- 需要的数据中心 id 和机器 id 的获取方式：可以在机器启动的时候，去 redis 中获取，或者从数据库中根据已经设置的机器 ip 和 id 的对应关系来获取
- 时钟回拨问题：当把机器时钟回拨后，可能会生成重复的 id，解决方案就是回拨时钟后，不让当前这台机器生成 id，当时间超过了回拨前的时间后，才继续生成 id

# 持续秒杀的高并发方案
## 前端
- 静态资源上CDN
- 页面静态化
- 秒杀读秒 + Loading
- 验证码削峰
## 后端
- 微服务
- 负载均衡
- 限流降级
- 令牌桶
- 缓存
- 异步处理
## 数据库
- 分库
- 读写分离
- 分表：横向、纵向
- 反范式设计：减少表关联次数、空间换时间
- 分布式数据库
## 其他
- 分时段秒杀
- 特有的业务逻辑：候补、排队

# Java9-17 新特性
## Java 9
### 模块化
- 更精确的控制要导出的包中的某些类，而不需要使用 private
- 使用 module-info.java 文件，通过 exports 来声明要导出的类、通过 requires 来声明要引入的类
## Java 10
### var 局部变量推导
- 需要保证当前变量可以推导出准确类型
- 必须初始化
- 只能用于局部变量
## Java 11

### 单文件程序
- 可以直接使用如下命令运行单个文件: ```java Demo.java```，而不再需要先编译，这个特性一般只是为了学习，实际生产中用不到

### shebang
- 通过 `#!` 来指定 java 版本，类似于 linux 中的 `#!bin/bash`

## Java 14

### 文本块
- 可以简单实现多行字符串，如下,通过 """ 来实现多行文本字符串

```java
String json = """
	{
		name: "test"
	}
""";
```
### instanceof 增强
- 在使用 instanceof 判断时，可以直接接赋值语句，如下：
```java
public class Demo {
    public static void main(String[] args) {
        Object a = "Hello world!";
        if (a instanceof String b) {
            System.out.println(b); // Hello world!
        }
    }
}
```

### 空指针提示
- 能够定位到具体为空的变量


## Java 16

### record 类
- 类似于 final 的特性，一旦初始化后，后面就不能再修改


## Java 17

### sealed 类
- 显式表明类继承

### switch 增强
- 可以和 instanceof 合并，简化判断和赋值的步骤，如下：
```java
public class Demo {
    public static void main(String[] args) {
        Object a = "Hello world!";

        switch (a) {
            case String b -> System.out.println(b);
            case Integer b -> System.out.println(b);
            case Long b -> System.out.println(b);
            default -> System.out.println("default");
        }
    }
}
```
# Spring Boot 3 新特性
## AOT
- Ahead of time: 预编译
- 启动运行速度更快：编译速度从原先的几秒降低到后面的几百毫秒，并且运行时没有 jvm，直接跟操作系统底层打交道，速度更快
- 打包体积更小
- 云原生

缺点：
- 编译后的程序不支持跨平台
- 不支持动态功能，比如 AOP

## JIT
- Just in time: 实时编译
- 没有 AOT 的缺点，但在高并发场景下会有一些问题，如下：
1. 热点业务在重启后一开始会业务超时，几分钟后会恢复正常
原因：启动后大量请求进来，导致多个类同时触发 JIT 超时，导致机器 CPU 升高，性能下降

底层原因：JVM 混合执行模式下，初期以解释方式执行，执行效率慢；当执行次数/频率达到指定阈值后，促发 JIT 编译，JIT 编译后，以机器指令码方式执行，执行效率提高。因此上述的问题本质是 JVM 的混合执行模式的机制

解决方案：
1. 先让程序进行预热，自己进行 JIT 编译
2. 做流量控制，后逐步放开流量
