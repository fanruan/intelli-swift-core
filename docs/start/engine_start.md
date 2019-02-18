# Swift 启动方式
Swift启动方式很简单，只需要运行主类`SwiftSpringBootApplication`即可。
***在运行主类前，需要修改一些配置文件，详见下文***

## swift.properties

| 属性 | 值 | 含义 |
| --- | --- | --- |
| swift.rpcMaxObjectSize  | 1000000000 | Rpc最大对象大小 |
| swift.rpc_server_address  | 127.0.0.1:7000 | Swift Rpc 地址及jdbc连接地址 |
| swift.is_cluster  | false | 是否以集群模式启动 |
| swift.clusterId  | 127.0.0.1:7000 | 集群ID（当swift.is_cluster为true时生效） |
| swift.master_address | 192.168.0.101:7000 | 主节点地址 |

> 对于单机模式，只需将**swift.is_cluster**设为*false*，并将**swift.rpc_server_address**设为当前运行机器的地址及端口即可


## hibernate配置（用于存储swift配置信息）
### 简要说明
* swift配置存储在一个指定的数据库中。
* 单机模式下数据库可以指定任意数据库，如HSQLDB，H2，MySql等。
* 集群模式需要将数据库指定为同一个可以公共访问的关系型数据库

### 修改方式
将 ***hibernate.cfg.xml*** 中相关属性修改为自己需要的值即可。
其中必须修改的属性如下：

| 属性 | 值 | 含义 |
| --- | --- | --- |
| driverClassName | com.mysql.jdbc.Driver | 连接的数据库驱动 |
| url | jdbc:mysql://localhost:3306/test_hibernate | Jdbc url  |
| username  | admin | 用户名 |
| password | Admin | 密码 |


## 集群模式
集群模式需要确保一下配置正确：
1. 每个节点的**hibernate.chf.xml**一致
2. 确保**swift.properties**中的**swift.is_cluster**为*true*
3. 确保**swift.properties**中的**swift.rpc_server_address**与**swift.clusterId**保持一致
4. 确保**swift.properties**中的**swift.master_address**填写**master**节点中的**swift.rpc_server_address**值

### 集群模式下的特殊配置**public.properties**
集群模式下需要对共享存储进行配置。

| 属性 | 值 | 含义 |
| --- | --- | --- |
| repo.type  | FTP | 共享存储类型 |
| repo.protocol  | FTP | 协议（FTP、SFTP） |
| repo.port  | 21 | 端口 |
| repo.charset  | UTF-8 | 字符集 |
| repo.host | 192.168.0.101 | IP |
| repo.user | admin | 用户名 |
| repo.pass | admin | 密码 |

