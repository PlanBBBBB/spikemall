# 秒杀商城（spikemall）

## 项目描述

本项目名为下单秒杀项目，主要实现了用户进行下单秒杀的服务，用户对商品进行下单，会有一系列的关于订单，库存，账户等的操作，用户进行支付也会有对应订单，库存，账户等的操作，其次作为一个完善的商城，该项目同样也包含了用户的注册，登录，登出，以及对商品进行查看等的功能。

![image-20230527103920081](./assets/image-20230527103920081.png)

## 相关技术栈

该项目主要使用了`springboot`，`mybatis-plus`，`springcloud`，`nacos`，`feign`，`rocketmq`，`redis`，`gateway`，`springsecurity`，`Sentinel`，`Seata`等技术。

## 项目启动

1. 启动nacos

```bash
startup.cmd -m standalone
```

随后浏览器访问[http://localhost:8848/nacos](http://localhost:8848/nacos ) 即可

2. 启动redis

先启动redis-server.exe，再启动redis-cli.exe

3. 启动rocketmq

先启动mqnamesrv.cmd，再启动mqbroker.cmd

4. 启动sentinel

~~~bash
java -Dserver.port=8090 -jar sentinel-dashboard-1.8.1.jar
~~~

随后浏览器访问[http://localhost:8090](http://localhost:8090 ) 即可

5. 启动seata

~~~bash
seata-server.bat
~~~

6. 启动所有服务

## 库表设计

### spikemall_users（用户数据库）

#### users（用户表）

| 名称     | 注释   |
| -------- | ------ |
| id       | 主键   |
| name     | 昵称   |
| phone    | 手机号 |
| avatar   | 头像   |
| password | 密码   |
| money    | 余额   |
| power    | 权限   |

---

#### oauth_client_details（spring security用户客户端表）

| 名称                    |
| ----------------------- |
| client_id               |
| resource_ids            |
| client_secret           |
| scope                   |
| authorized_grant_types  |
| web_server_redirect_uri |
| authorities             |
| access_token_validity   |
| refresh_token_validity  |
| additional_information  |
| autoapprove             |

该`oauth_client_details`表主要用于spring security整合oauth2.0时，作为认证授权服务器时的保存在数据库的客户端使用。

### spikemall_goods（商品数据库）

#### goods（商品表）

| 名称        | 类型     |
| ----------- | -------- |
| id          | 主键     |
| name        | 商品名   |
| price       | 商品价格 |
| image       | 商品图片 |
| description | 商品描述 |

### spike mall_orders（订单数据库）

#### orders（订单表）

| 名称       | 注释                         |
| ---------- | ---------------------------- |
| id         | 主键                         |
| user_id    | 下单用户id                   |
| good_id    | 商品id                       |
| status     | 订单状态（0未支付，1已支付） |
| order_time | 下单时间                     |
| check_time | 结账时间                     |
| amount     | 实收金额                     |

### spikemall_repertory（库存数据库）

#### repertory（库存表）

| 名称       | 注释     |
| ---------- | -------- |
| goods_id   | 商品id   |
| stock      | 库存     |
| begin_time | 开始时间 |
| end_time   | 结束时间 |

## 账户服务

### 注册功能

+ 使用场景

本项目使用了springsecurity整合的oauth2.0，故所有调用的资源都需要在请求头中携带jwt令牌，登录功能实现的是获取令牌，而注册功能是本系统唯一一个不需要携带令牌就能访问的资源。

### springsecurity登录功能⭐⭐⭐

#### 功能概述

本项目使用的是授权码模式，故是将账户服务作为一个认证授权的服务器，而其他的服务都是资源服务器，用户需要在认证授权服务器中获取到jwt令牌，并在访问其他服务时，在请求头携带jwt令牌才能访问其他服务的资源。

#### 获取授权码步骤

1. 在浏览器输入[http://localhost:8085](http://localhost:8085)会自动跳转到[http://localhost:8085/login](http://localhost:8085/login)，在表单中填写手机号和密码进行登录。


进入如下界面代表登录成功

![image-20230527101318752](./assets/image-20230527101318752.png)

2. 在浏览器中输入该网址[http://localhost:8085/oauth/authorize?client_id=client&response_type=code&redirect_uri=http://www.baidu.com](http://localhost:8085/oauth/authorize?client_id=client&response_type=code&redirect_uri=http://www.baidu.com)，跳转到百度首页之后，在上面url处找到授权码

![image-20230527101354324](./assets/image-20230527101354324.png)

点击Authorize进行授权

![image-20230527101451035](./assets/image-20230527101451035.png)

得到授权码

3. 将得到的授权码代入到以下url地址中，此处用postman进行操作：[http://client:secret@localhost:8085/oauth/token?grant_type=authorization_code&code=prgYij&redirect_uri=http://www.baidu.com](http://client:secret@localhost:8085/oauth/token?grant_type=authorization_code&code=prgYij&redirect_uri=http://www.baidu.com)

![image-20230527101535264](./assets/image-20230527101535264.png)

携带授权码，得到`access_token`，该`access_token`在前面拼接上`Bearer `即为jwt令牌。

#### 代码实现

因为本身使用的就是springsecurity整合好的安全框架，故只是对认证授权服务器和资源服务器做了一系列的配置而已。

### 获取用户余额功能

在支付功能进行中，会通过远程调用该方法，判断用户的余额是否充足，以进行后续的支付功能。

### 扣减用户余额功能


用户在进行支付功能时，满足一切支付条件后会对用户余额进行扣减。

## 商品服务

### 通过商品id查询价格功能



在创建订单时，需要知道商品的价格，故这是一个远程调用的方法。

### 查看商品列表功能

用户可以查看商品，属于完善系统的一个功能。


## 下单服务

### 下单功能


用户查看商品，选择好商品之后即可下单，下单功能会局限于商品是否在秒杀时间段内以及库存是否充足等条件，此处利用分布式锁来实现一人一单。


## 订单服务

### 创建订单功能



在用户进行下单操作时进行远程调用的方法。


### 查看该用户是否购买过该商品功能（一人一单）

用户在进行下单时，查看该用户是否已经购买过该商品，若已经购买过该商品，则不允许用户重复购买。



### 查看当前用户的所有订单功能

用户可查看自己的所有订单，算是完善系统的一个功能。

## 支付服务

### 支付功能


用户在下单功能成功之后，再进行支付功能，在支付过程种会出现未在规定时间段内支付以及余额不足无法支付的情况，该情况下会对库存进行回滚，同时对创建好的订单进行删除，若满足支付条件，将对订单进行修改，同时对用户余额进行扣减。



## 库存服务

### 扣减库存功能


用户在进行下单的时候，就直接对库存进行扣减。



### 回滚库存功能

用户在进行支付的时候，若未在商品的秒杀时间段内，或自身余额不足的情况下会进行库存的回滚。



## 网关服务

由于项目是微服务项目，故使用gateway网关进行端口等统一配置处理很有必要，本项目除用户登录进行获取授权码及获取jwt令牌的所有请求资源的端口号均为10010。

