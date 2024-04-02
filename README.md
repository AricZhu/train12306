# 背景
12306
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

# 数据库表设计

## 会员模块：

### 会员表
- 手机号

### 乘客表
- 会员ID
- 姓名
- 身份证
- 旅客类型

### 车票表
- 会员ID
- 乘客ID
- 乘客姓名
- 日期
- 车次信息
- 座位信息



## 业务模块：

### 车站表
- 站名
- 站名（拼音）

### 车次表
- 车次编号
- 车次类型
- 始发站
- 出发时间
- 终点站
- 到站时间

### 到站表
- 车次编号
- 站名
- 进站时间
- 出站时间
- 停站时长
- 里程

### 车厢表
- 车次编号
- 厢号
- 座位类型
- 座位数
- 排数
- 列数

### 座位表
- 车次编号
- 厢号
- 排号
- 列号

### 每日车次表
- 日期
- 基础车次信息

### 每日到站表
- 日期
- 基础到站信息

### 每日余票表
- 日期
- 车次编号
- 出发站
- 出发时间
- 到达站
- 到站时间
- 各种座位的余票信息





















