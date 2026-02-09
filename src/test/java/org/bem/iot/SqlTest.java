package org.bem.iot;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.bem.iot.mapper.postgresql.ProductMapper;
import org.bem.iot.mapper.postgresql.ProductModelMapper;
import org.bem.iot.mapper.tdengine.IotStableMapper;
import org.bem.iot.mapper.tdengine.StatisticsOptionMessageMapper;
import org.bem.iot.model.iotbase.Stable;
import org.bem.iot.model.monitor.StatisticsOptionMessage;
import org.bem.iot.model.product.Product;
import org.bem.iot.model.product.ProductModel;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisServerCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

@SpringBootTest
public class SqlTest {
    @Resource
    RedisTemplate<String, Object> redisTemplate;

    @Resource
    StatisticsOptionMessageMapper statisticsOptionMessageMapper;

    @Test
    public void createDeviceModelTables() {
        RedisConnection connection = Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection();
        Properties info = connection.serverCommands().info("stats");
        System.out.println("------------------------------------------");
        if (info != null) {
            System.out.println(info.getProperty("keyspace_hits"));
            System.out.println(info.getProperty("keyspace_misses"));
        }
        System.out.println("------------------------------------------");
    }

    @Test
    public void queryTest1() {
        RedisConnection connection = Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection();
        Properties info = connection.serverCommands().info("stats");
        System.out.println("------------------------------------------");
        if (info != null) {
            System.out.println(info.getProperty("keyspace_hits"));
            System.out.println(info.getProperty("keyspace_misses"));
        }
        System.out.println("------------------------------------------");
    }

    @Test
    public void queryTest2() {
        QueryWrapper<StatisticsOptionMessage> example = new QueryWrapper<>();
        example.select("CSUM(received_number) AS receiveds",
                "CSUM(send_number) AS sends",
                "CSUM(event_number) AS events",
                "CSUM(alarm_number) AS alarms");
        // 查询输出为map
        Map<String, Object> map = statisticsOptionMessageMapper.selectMaps(example).get(0);
        System.out.println("------------------------------------------");
        System.out.println(JSONObject.toJSONString(map));
        System.out.println("------------------------------------------");
    }

// server 信息
//    "redis_version": "5.0.14.1",  //Redis 服务器版本
//    "redis_git_sha1": "ec77f72d",  //Git SHA1
//    "redis_git_dirty": "0",  //Git dirty flag
//    "os": "Windows  ",  //Redis 服务器的宿主操作系统
//    "arch_bits": "64",  //架构（32 或 64 位）
//    "multiplexing_api": "WinSock_IOCP",  //Redis 所使用的事件处理机制
//    "process_id": "19344",  // 服务器进程的 PID
//    "redis_build_id": "5627b8177c9289c",  // Redis 构建ID
//    "run_id": "7e3b8ec1b8c9981d309912a500bc4e7ad987822a",  // Redis 服务器的随机标识符（用于 Sentinel 和集群）
//    "tcp_port": "6379",  // TCP/IP 监听端口
//    "uptime_in_seconds": "786375",  // 自 Redis 服务器启动以来，经过的秒数
//    "uptime_in_days": "9",  // 自 Redis 服务器启动以来，经过的天数
//    "lru_clock": "6150867",  // 以分钟为单位进行自增的时钟，用于 LRU 管理
//    "executable": "D:Program FilesRedis\"d:program files\redis\redis-server.exe\"", // 运行 Redis 的可执行文件
//    "config_file": "D:Program FilesRedis\redis.windows-service.conf",  // 置文件路径
//            "atomicvar_api": "pthread-mutex",  // 指示原子变量API的实现方式
//            "expired_stale_perc": "0.00",  // 预估过期键（expired keys）占数据库总键数的比例‌，帮助Redis 决定过期键删除策略
//            "configured_hz": "10",  // 定期任务（如关闭超时客户端连接、主动清除过期 Key 等）的心跳基准执行频率
//            "redis_mode": "standalone",  // Redis 当前以运行模式（standalone:单机模式）

//  clients 客户端信息
//        "connected_clients": "1",  // 已连接客户端的数量（不包括通过从属服务器连接的客户端）
//        "blocked_clients": "0",  // 正在等待阻塞命令（BLPOP、BRPOP、BRPOPLPUSH）的客户端的数量
//            "client_recent_max_output_buffer": "0",  // 客户端最近的最大输出缓冲区
//            "client_recent_max_input_buffer": "2",  // 客户端最近的最大输入缓冲区

// memory 内存信息
//            "used_memory": "1003424",  // 由 Redis 分配器分配的内存总量，以字节（byte）为单位
//            "used_memory_human": "979.91K",  // 以人类可读的格式返回 Redis 分配的内存总量
//            "used_memory_rss": "1002176",  // 从操作系统的角度，返回 Redis 已分配的内存总量（俗称常驻集大小）。这个值和top 、 ps 等命令的输出一致。
//            "used_memory_rss_human": "978.69K", // 以人类可读的格式返回 Redis 已分配的内存总量（俗称常驻集大小）
//            "used_memory_peak": "1004160", // Redis 的内存消耗峰值（以字节为单位）
//            "used_memory_peak_human": "980.62K",  // 以人类可读的格式返回 Redis 的内存消耗峰值
//            "used_memory_peak_perc": "99.93%",  // 内存消耗百分比(used_memory/ used_memory_peak) *100%
//            "used_memory_overhead": "900982",  // Redis为了维护数据集的内部机制所需的内存开销，包括所有客户端输出缓冲区、查询缓冲区、AOF重写缓冲区和主从复制的backlog。
//            "used_memory_startup": "661448",  // Redis服务器启动时消耗的内存
//            "used_memory_dataset": "102442",  // 用户可用内存 used_memory — used_memory_overhead
//            "used_memory_dataset_perc": "29.96%",  // 用户可用内存百分比 100%*(used_memory_dataset/(used_memory—used_memory_startup))
//            "total_system_memory": "0",  // 整个系统内存
//            "total_system_memory_human": "0B",  // 以人类可读的格式，显示整个系统内存
//            "used_memory_lua": "37888",  // Lua 引擎所使用的内存大小（以字节为单位）
//            "used_memory_lua_human": "37.00K",  // 以人类可读的格式，显示Lua脚本存储占用的内存
//            "maxmemory": "0",  // Redis实例的最大内存配置
//            "maxmemory_human": "0B",  // 以人类可读的格式，显示Redis实例的最大内存配置
//            "maxmemory_policy": "noeviction",  // 当达到maxmemory时的淘汰策略
//            "mem_fragmentation_ratio": "1.00",  // used_memory_rss 和 used_memory 之间的比率
//            "mem_allocator": "jemalloc-5.2.1-redis",  // 在编译时指定的， Redis 所使用的内存分配器。可以是 libc 、 jemalloc 或者 tcmalloc 。
//            "active_defrag_running": "0",  // 表示没有活动的defrag任务正在运行，1表示有活动的defrag任务正在运行（defrag:表示内存碎片整理）
//            "lazyfree_pending_objects": "0",  // 0表示不存在延迟释放（也有资料翻译未惰性删除）的挂起对象
//            "used_memory_scripts": "0",  // 脚本所使用的内存大小（以B为单位）
//            "used_memory_scripts_human": "0B",  // 以人类可读的格式，显示脚本存储占用的内存
//            "active_defrag_hits": "0",  // 主动碎片整理进程每分钟成功重新分配内存值的次数
//            "active_defrag_misses": "0",  // 在主动碎片整理过程中，‌未能成功处理内存碎片的次数统计
//            "active_defrag_key_hits": "0",  // 在主动碎片整理操作中，成功处理并重新分配内存的键（key）的数量
//            "active_defrag_key_misses": "0",  // 在主动碎片整理操作中，键（key）操作时发生未命中的次数统计
//            "allocator_allocated": "1640904",  // Redis 分配器分配的内存，包含内部碎片，通常与 used_memory 一致
//            "allocator_rss_ratio": "1.01",  // 内存分配器（如 jemalloc）的常驻内存（RSS）与活跃内存（active memory）的比例
//            "allocator_active": "381681664",  // 内存分配器（如 jemalloc）当前实际占用的内存字节数
//            "allocator_resident": "385875968",  //  Redis 进程实际占用的物理内存
//            "allocator_rss_bytes": "4194304",  // Redis 进程的常驻内存（Resident Set Size，RSS）与内存分配器活跃内存之间的差值
//            "rss_overhead_ratio": "0.00",  //  Redis 进程常驻内存（used_memory_rss）与分配器常驻内存（allocator_active）之间的比率‌，计算公式为 rss_overhead_ratio = used_memory_rss / allocator_active
//            "rss_overhead_bytes": "-384873792",  // 进程常驻内存（used_memory_rss）与内存分配器常驻内存（allocator_resident）之间的差值


// persistence : RDB 和 AOF 的相关信息
//            "loading": "0",  // 服务器是否正在载入持久化文件
//            "rdb_changes_since_last_save": "0",  // 离最近一次成功生成rdb文件，写入命令的个数，即有多少个写入命令没有持久化
//            "rdb_bgsave_in_progress": "0",  // 服务器是否正在创建rdb文件
//            "rdb_last_save_time": "1767755615",  // 离最近一次成功创建rdb文件的时间戳。当前时间戳 - rdb_last_save_time=多少秒未成功生成rdb文件
//            "rdb_last_bgsave_status": "ok",  // 最近一次rdb持久化是否成功
//            "rdb_last_bgsave_time_sec": "0",  // 最近一次成功生成rdb文件耗时秒数
//            "rdb_current_bgsave_time_sec": "-1",  // 如果服务器正在创建rdb文件，那么这个域记录的就是当前的创建操作已经耗费的秒数
//            "rdb_last_cow_size": "0",  // RDB过程中父进程与子进程相比执行了多少修改(包括读缓冲区，写缓冲区，数据修改等)。
//            "aof_enabled": "0",  // 是否开启了aof
//            "aof_rewrite_in_progress": "0",  // 标识aof的rewrite操作是否在进行中
//            "aof_rewrite_scheduled": "0",  // rewrite任务计划，当客户端发送bgrewriteaof指令，如果当前rewrite子进程正在执行，那么将客户端请求的bgrewriteaof变为计划任务，待aof子进程结束后执行rewrite
//            "aof_last_rewrite_time_sec": "-1",  // 最近一次aof rewrite耗费的时长
//            "aof_current_rewrite_time_sec": "-1",  // 如果rewrite操作正在进行，则记录所使用的时间，单位秒
//            "aof_last_bgrewrite_status": "ok",  // 上次bgrewriteaof操作的状态
//            "aof_last_write_status": "ok",  // 上次aof写入状态
//            "aof_last_cow_size": "0",  // 最近一次 aof 重写（BGREWRITEAOF）操作时，操作系统为子进程分配的写时复制（Copy-on-Write, COW）内存大小‌，单位为字节
//            "mem_not_counted_for_evict": "0",  // 不计入密钥驱逐的使用内存。这基本上是瞬态副本和AOF缓冲区。
//            "allocator_frag_bytes": "380040760",  // 内存分配器当前活跃内存与总分配内存之间的差值，直接反映了由于内存分配和释放导致的外部碎片量
//            "allocator_frag_ratio": "232.60",  // 内存分配器内部因内存分配和释放操作产生的外部碎片情况，比值越接近 1.0 表示内存利用率越高，碎片浪费越少；比值显著高于 1.0 则表明存在较多内存碎片

// stats : 一般统计信息
//            "total_connections_received": "30",  // 新创建连接个数,如果新创建连接过多，过度地创建和销毁连接对性能有影响，说明短连接严重或连接池使用有问题，需调研代码的连接设置
//            "total_commands_processed": "8226",  // redis处理的命令数
//            "instantaneous_ops_per_sec": "0",  // redis当前的qps，redis内部较实时的每秒执行的命令数
//            "total_net_input_bytes": "469408",  // redis网络入口流量字节数
//            "total_net_output_bytes": "51014",  // redis网络出口流量字节数
//            "instantaneous_input_kbps": "0.04",  // redis网络入口kps
//            "instantaneous_output_kbps": "0.00",  // redis网络出口kps
//            "rejected_connections": "0",  // 拒绝的连接个数，redis连接个数达到maxclients限制，拒绝新连接的个数
//            "sync_full": "0",  // 主从完全同步成功次数
//            "sync_partial_ok": "0",  // 主从部分同步成功次数
//            "sync_partial_err": "0",  // 主从部分同步失败次数
//            "expired_keys": "36",  // 运行以来过期的key的数量
//            "evicted_keys": "0",  // 运行以来剔除(超过了maxmemory后)的key的数量
//            "keyspace_hits": "1355",  // 命中次数
//            "keyspace_misses": "6590",  // 没命中次数
//            "pubsub_channels": "0",  // 当前使用中的频道数量
//            "pubsub_patterns": "0",  // 当前使用的模式的数量
//            "latest_fork_usec": "12929",  // 最近一次fork操作阻塞redis进程的耗时数，单位微秒
//            "migrate_cached_sockets": "0"  // 是否已经缓存了到该地址的连接
//            "hz": "10",  // Redis执行定期任务的频率，即每秒执行的命令数
//            "number_of_cached_scripts": "0",  // 缓存脚本数量
//            "mem_aof_buffer": "0",  //  Redis 实例当前正在使用的AOF缓冲区所占用的内存字节数
//            "mem_clients_normal": "49950",  // 普通客户端连接（包括主客户端、发布/订阅客户端等）使用的内存量，单位为字节
//            "expired_time_cap_reached_count": "0",  // 记录因过期键删除操作受到频率限制而达到上限的次数
//            "mem_fragmentation_bytes": "0",  // 内存碎片大小的指标，单位为字节
//            "mem_clients_slaves": "0",  // 所有‌slave 客户端‌（包括从节点和发布/订阅客户端）的内存开销总和，单位为字节
//            "mem_replication_backlog": "0",  // 复制积压缓冲区‌ 当前占用的内存大小，单位为字节

// replication : 主/从复制信息
//            "role": "master",  // 实例的角色，是master or slave
//            "connected_slaves": "0",  // 连接的slave实例个数
//            "master_replid": "9bc5ed0cf93a49ac94417dcc9483f73750c7dba2",  // 主实例启动随机字符串
//            "master_replid2": "0000000000000000000000000000000000000000",  // 主实例启动随机字符串2
//            "master_repl_offset": "0",  // 主从同步偏移量,此值如果和上面的offset相同说明主从一致没延迟，与master_replid可被用来标识主实例复制流中的位置。
//            "second_repl_offset": "-1",  // 主从同步偏移量2,此值如果和上面的offset相同说明主从一致没延迟
//            "repl_backlog_active": "0",  // 复制积压缓冲区是否开启
//            "repl_backlog_size": "1048576",  // 复制积压缓冲大小
//            "repl_backlog_first_byte_offset": "0",  // 复制缓冲区里偏移量的大小
//            "repl_backlog_histlen": "0",  // 此值等于 master_repl_offset - repl_backlog_first_byte_offset,该值不会超过repl_backlog_size的大小
//            "slave_expires_tracked_keys": "0",  // Redis Slave（从节点）‌ 当前正在跟踪的、设置了过期时间（expire）的键的数量

// cpu : CPU 计算量统计信息
//            "used_cpu_sys": "5.953125",  // 将所有redis主进程在核心态所占用的CPU时求和累计起来
//            "used_cpu_user": "35.421875",  // 将所有redis主进程在用户态所占用的CPU时求和累计起来
//            "used_cpu_sys_children": "0.000000",  // 将后台进程在核心态所占用的CPU时求和累计起来
//            "used_cpu_user_children": "0.000000",  // 将后台进程在用户态所占用的CPU时求和累计起来

// commandstats : Redis 命令统计信息

// cluster : Redis 集群信息
//            "cluster_enabled": "0",  // 实例是否启用集群模式

// keyspace : 数据库相关的统计信息
//            "db1": "keys=3,expires=2,avg_ttl=3401050",  // db1的key的数量,以及带有生存期的key的数,平均存活时间
//            "db10": "keys=3913,expires=0,avg_ttl=0",  // db10的key的数量,以及带有生存期的key的数,平均存活时间

}