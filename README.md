# TCP4J (Thrift Client Pool For Java)

![tcp4j logo image](logo.jpg)

TCP原意是传输控制协议（Transfer Control Protocol），在本项目中是作为 Thrift Client Pool For Java的首字母缩写。

> 另外，本项目的Logo的来源，来自巴西的一家军械公司--金牛座军事工业公司（Taurus International）生产的TCP迷你手枪系列中的一款 Taurus 738 TCP 380 Pistol。

> 如果大家对这个Logo有兴趣的话，可以自行Google一下（要翻墙哦），Baidu貌似找不到。

## TCP4J使用方法

### 默认ThriftClientImpl的使用方法

```java
//ThriftClient初始化
ThriftClient client = new ThriftClientImpl(() -> Arrays.asList(
    ThriftServerInfo.of("127.0.0.1",9001),
    ThriftServerInfo.of("127.0.0.1",9002)
));

//调用Thrift定义的Service的client方法
client.iface(ThriftService.Client.class).echo("Hello World.")
```