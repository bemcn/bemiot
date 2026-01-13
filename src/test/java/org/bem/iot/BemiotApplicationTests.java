package org.bem.iot;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.bem.iot.service.RabbitMQInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

@SpringBootTest
public class BemiotApplicationTests {

    @Autowired
    private RabbitMQInfoService rabbitMQInfoService;

    @Test
    public void contextLoads() {
        try {
            System.out.println("开始测试获取RabbitMQ客户端连接统计信息...");
            
            // 获取客户端连接统计信息
            JSONObject clientStats = rabbitMQInfoService.getClientConnectionStats();
            
            if (clientStats != null) {
                System.out.println("=== RabbitMQ 客户端连接统计信息 ===");
                System.out.println("活跃连接数: " + clientStats.get("active_connections"));
                System.out.println("活跃消费者数: " + clientStats.get("active_consumers"));
            } else {
                System.out.println("未能获取客户端连接统计信息");
            }
            
            // 同时获取详细的连接信息
            JSONArray connections = rabbitMQInfoService.getConnections();
            if (connections != null) {
                System.out.println("\n详细连接信息:");
                for (int i = 0; i < connections.size(); i++) {
                    JSONObject connection = connections.getJSONObject(i);
                    System.out.println("连接 " + (i+1) + ": " + connection.getString("name") + 
                                      " | 客户端地址: " + connection.getString("peer_host") + 
                                      " | 端口: " + connection.getInteger("peer_port"));
                }
            }
            
        } catch (Exception e) {
            System.err.println("测试获取RabbitMQ客户端连接统计信息时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void lastTest() {
        try {
            System.out.println("开始测试获取RabbitMQ队列操作统计信息...");
            
            // 获取队列操作统计信息
            JSONObject queueOpsStats = rabbitMQInfoService.getQueueOperationsStats();
            
            if (queueOpsStats != null) {
                System.out.println("=== RabbitMQ 队列操作统计信息 ===");
                System.out.println("Declared (声明): " + queueOpsStats.get("declared"));
                System.out.println("Created (创建): " + queueOpsStats.get("created"));
                System.out.println("Deleted (删除): " + queueOpsStats.get("deleted"));
            } else {
                System.out.println("未能获取队列操作统计信息");
            }
            
            // 同时也测试新的累积统计方法
            System.out.println("\n开始测试获取RabbitMQ队列累计统计信息...");
            JSONObject cumulativeStats = rabbitMQInfoService.getQueueCumulativeStats();
            
            if (cumulativeStats != null) {
                System.out.println("=== RabbitMQ 队列累计统计信息 ===");
                System.out.println("Declared (声明): " + cumulativeStats.get("declared"));
                System.out.println("Created (创建): " + cumulativeStats.get("created"));
                System.out.println("Deleted (删除): " + cumulativeStats.get("deleted"));
            } else {
                System.out.println("未能获取队列累计统计信息");
            }
            
        } catch (Exception e) {
            System.err.println("测试获取RabbitMQ队列操作统计信息时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试获取RabbitMQ概览信息
     */
    @Test
    public void testGetOverview() {
        try {
            System.out.println("开始测试获取RabbitMQ概览信息...");
            JSONObject overview = rabbitMQInfoService.getOverview();
            
            if (overview != null) {
                System.out.println("RabbitMQ概览信息获取成功:");
                System.out.println("RabbitMQ版本: " + overview.get("rabbitmq_version"));
                System.out.println("集群名称: " + overview.get("cluster_name"));
                System.out.println("消息统计信息: " + overview.get("message_stats"));
                System.out.println("队列统计信息: " + overview.get("queue_totals"));
                
                // 打印完整的概览信息
                System.out.println("完整概览JSON: " + overview.toJSONString());
            } else {
                System.out.println("获取概览信息失败，返回结果为null");
            }
        } catch (Exception e) {
            System.err.println("测试获取RabbitMQ概览信息时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试获取集群节点信息
     */
    @Test
    public void testGetNodes() {
        try {
            System.out.println("开始测试获取RabbitMQ节点信息...");
            JSONArray nodes = rabbitMQInfoService.getNodes();
            
            if (nodes != null) {
                System.out.println("RabbitMQ节点信息获取成功:");
                System.out.println("节点数量: " + nodes.size());
                System.out.println("节点列表: " + nodes.toJSONString());
            } else {
                System.out.println("获取节点信息失败，返回结果为null");
            }
        } catch (Exception e) {
            System.err.println("测试获取RabbitMQ节点信息时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试获取虚拟主机信息
     */
    @Test
    public void testGetVhosts() {
        try {
            System.out.println("开始测试获取RabbitMQ虚拟主机信息...");
            JSONArray vhosts = rabbitMQInfoService.getVhosts();
            
            if (vhosts != null) {
                System.out.println("RabbitMQ虚拟主机信息获取成功:");
                System.out.println("虚拟主机列表: " + vhosts.toJSONString());
            } else {
                System.out.println("获取虚拟主机信息失败，返回结果为null");
            }
        } catch (Exception e) {
            System.err.println("测试获取RabbitMQ虚拟主机信息时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试获取集群名称
     */
    @Test
    public void testGetClusterName() {
        try {
            System.out.println("开始测试获取RabbitMQ集群名称...");
            JSONObject clusterName = rabbitMQInfoService.getClusterName();
            
            if (clusterName != null) {
                System.out.println("RabbitMQ集群名称获取成功:");
                System.out.println("集群名称: " + clusterName.get("name"));
                System.out.println("完整集群名称JSON: " + clusterName.toJSONString());
            } else {
                System.out.println("获取集群名称失败，返回结果为null");
            }
        } catch (Exception e) {
            System.err.println("测试获取RabbitMQ集群名称时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试获取指定节点信息
     */
    @Test
    public void testGetNode() {
        try {
            System.out.println("开始测试获取RabbitMQ指定节点信息...");
            JSONArray nodes = rabbitMQInfoService.getNodes();
            
            if (nodes != null && !nodes.isEmpty()) {
                String nodeName = nodes.getJSONObject(0).getString("name");
                System.out.println("获取节点信息，节点名称: " + nodeName);
                
                JSONObject node = rabbitMQInfoService.getNode(nodeName);
                
                if (node != null) {
                    System.out.println("节点信息获取成功:");
                    System.out.println("节点名称: " + node.getString("name"));
                    System.out.println("节点状态: " + node.getBoolean("running"));
                    System.out.println("完整节点信息JSON: " + node.toJSONString());
                } else {
                    System.out.println("获取节点信息失败，返回结果为null");
                }
            } else {
                System.out.println("没有找到任何节点信息，无法测试getNode方法");
            }
        } catch (Exception e) {
            System.err.println("测试获取RabbitMQ指定节点信息时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试获取指定节点内存信息
     */
    @Test
    public void testGetNodeMemory() {
        try {
            System.out.println("开始测试获取RabbitMQ指定节点内存信息...");
            JSONArray nodes = rabbitMQInfoService.getNodes();
            
            if (nodes != null && !nodes.isEmpty()) {
                String nodeName = nodes.getJSONObject(0).getString("name");
                System.out.println("获取节点内存信息，节点名称: " + nodeName);
                
                JSONObject nodeMemory = rabbitMQInfoService.getNodeMemory(nodeName);
                
                if (nodeMemory != null) {
                    System.out.println("节点内存信息获取成功:");
                    System.out.println("内存使用情况: " + nodeMemory.get("memory"));
                    System.out.println("完整节点内存信息JSON: " + nodeMemory.toJSONString());
                } else {
                    System.out.println("获取节点内存信息失败，返回结果为null");
                }
            } else {
                System.out.println("没有找到任何节点信息，无法测试getNodeMemory方法");
            }
        } catch (Exception e) {
            System.err.println("测试获取RabbitMQ指定节点内存信息时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试获取指定虚拟主机
     */
    @Test
    public void testGetVhost() {
        try {
            System.out.println("开始测试获取RabbitMQ指定虚拟主机...");
            JSONArray vhosts = rabbitMQInfoService.getVhosts();
            
            if (vhosts != null && !vhosts.isEmpty()) {
                String vhostName = vhosts.getJSONObject(0).getString("name");
                System.out.println("获取虚拟主机信息，虚拟主机名称: " + vhostName);
                
                JSONObject vhost = rabbitMQInfoService.getVhost(vhostName);
                
                if (vhost != null) {
                    System.out.println("虚拟主机信息获取成功:");
                    System.out.println("虚拟主机名称: " + vhost.getString("name"));
                    System.out.println("虚拟主机状态: " + vhost.getBoolean("tracing"));
                    System.out.println("完整虚拟主机信息JSON: " + vhost.toJSONString());
                } else {
                    System.out.println("获取虚拟主机信息失败，返回结果为null");
                }
            } else {
                System.out.println("没有找到任何虚拟主机信息，无法测试getVhost方法");
            }
        } catch (Exception e) {
            System.err.println("测试获取RabbitMQ指定虚拟主机时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试创建和删除虚拟主机
     */
    @Test
    public void testCreateAndDeleteVhost() {
        try {
            System.out.println("开始测试创建和删除RabbitMQ虚拟主机...");
            String testVhost = "test_vhost_" + System.currentTimeMillis();
            
            System.out.println("创建虚拟主机: " + testVhost);
            rabbitMQInfoService.createVhost(testVhost);
            System.out.println("虚拟主机创建成功: " + testVhost);
            
            // 验证虚拟主机是否创建成功
            JSONArray vhosts = rabbitMQInfoService.getVhosts();
            boolean created = false;
            if (vhosts != null) {
                for (int i = 0; i < vhosts.size(); i++) {
                    if (testVhost.equals(vhosts.getJSONObject(i).getString("name"))) {
                        created = true;
                        break;
                    }
                }
            }
            
            if (created) {
                System.out.println("验证虚拟主机创建成功，现在删除虚拟主机: " + testVhost);
                rabbitMQInfoService.deleteVhost(testVhost);
                System.out.println("虚拟主机删除成功: " + testVhost);
                
                // 验证虚拟主机是否被删除
                JSONArray vhostsAfter = rabbitMQInfoService.getVhosts();
                boolean deleted = true;
                if (vhostsAfter != null) {
                    for (int i = 0; i < vhostsAfter.size(); i++) {
                        if (testVhost.equals(vhostsAfter.getJSONObject(i).getString("name"))) {
                            deleted = false;
                            break;
                        }
                    }
                }
                
                if (deleted) {
                    System.out.println("虚拟主机删除验证成功");
                } else {
                    System.out.println("虚拟主机删除验证失败");
                }
            } else {
                System.out.println("虚拟主机创建验证失败");
            }
        } catch (Exception e) {
            System.err.println("测试创建和删除RabbitMQ虚拟主机时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试获取所有用户
     */
    @Test
    public void testGetUsers() {
        try {
            System.out.println("开始测试获取RabbitMQ所有用户...");
            JSONArray users = rabbitMQInfoService.getUsers();
            
            if (users != null) {
                System.out.println("RabbitMQ用户信息获取成功:");
                System.out.println("用户数量: " + users.size());
                System.out.println("用户列表: " + users.toJSONString());
            } else {
                System.out.println("获取用户信息失败，返回结果为null");
            }
        } catch (Exception e) {
            System.err.println("测试获取RabbitMQ所有用户时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试获取指定用户
     */
    @Test
    public void testGetUser() {
        try {
            System.out.println("开始测试获取RabbitMQ指定用户...");
            JSONArray users = rabbitMQInfoService.getUsers();
            
            if (users != null && !users.isEmpty()) {
                String username = users.getJSONObject(0).getString("name");
                System.out.println("获取用户信息，用户名: " + username);
                
                JSONObject user = rabbitMQInfoService.getUser(username);
                
                if (user != null) {
                    System.out.println("用户信息获取成功:");
                    System.out.println("用户名: " + user.getString("name"));
                    System.out.println("用户标签: " + user.getString("tags"));
                    System.out.println("完整用户信息JSON: " + user.toJSONString());
                } else {
                    System.out.println("获取用户信息失败，返回结果为null");
                }
            } else {
                System.out.println("没有找到任何用户信息，无法测试getUser方法");
            }
        } catch (Exception e) {
            System.err.println("测试获取RabbitMQ指定用户时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试获取虚拟主机权限
     */
    @Test
    public void testGetPermissions() {
        try {
            System.out.println("开始测试获取RabbitMQ虚拟主机权限...");
            JSONArray vhosts = rabbitMQInfoService.getVhosts();
            JSONArray users = rabbitMQInfoService.getUsers();
            
            if (vhosts != null && !vhosts.isEmpty() && users != null && !users.isEmpty()) {
                String vhostName = vhosts.getJSONObject(0).getString("name");
                String username = users.getJSONObject(0).getString("name");
                
                System.out.println("获取权限信息，虚拟主机: " + vhostName + ", 用户: " + username);
                
                JSONObject permissions = rabbitMQInfoService.getPermissions(vhostName, username);
                
                if (permissions != null) {
                    System.out.println("权限信息获取成功:");
                    System.out.println("权限详情: " + permissions.toJSONString());
                } else {
                    System.out.println("获取权限信息失败，返回结果为null");
                }
            } else {
                System.out.println("没有找到虚拟主机或用户信息，无法测试getPermissions方法");
            }
        } catch (Exception e) {
            System.err.println("测试获取RabbitMQ虚拟主机权限时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试获取所有交换机
     */
    @Test
    public void testGetExchanges() {
        try {
            System.out.println("开始测试获取RabbitMQ所有交换机...");
            JSONArray exchanges = rabbitMQInfoService.getExchanges();
            
            if (exchanges != null) {
                System.out.println("RabbitMQ交换机信息获取成功:");
                System.out.println("交换机数量: " + exchanges.size());
                System.out.println("交换机列表: " + exchanges.toJSONString());
            } else {
                System.out.println("获取交换机信息失败，返回结果为null");
            }
        } catch (Exception e) {
            System.err.println("测试获取RabbitMQ所有交换机时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试获取指定虚拟主机的所有交换机
     */
    @Test
    public void testGetExchangesByVhost() {
        try {
            System.out.println("开始测试获取RabbitMQ指定虚拟主机的所有交换机...");
            JSONArray vhosts = rabbitMQInfoService.getVhosts();
            
            if (vhosts != null && !vhosts.isEmpty()) {
                String vhostName = vhosts.getJSONObject(0).getString("name");
                System.out.println("获取虚拟主机 " + vhostName + " 的交换机列表");
                
                JSONArray exchanges = rabbitMQInfoService.getExchangesByVhost(vhostName);
                
                if (exchanges != null) {
                    System.out.println("交换机信息获取成功:");
                    System.out.println("交换机数量: " + exchanges.size());
                    System.out.println("交换机列表: " + exchanges.toJSONString());
                } else {
                    System.out.println("获取交换机信息失败，返回结果为null");
                }
            } else {
                System.out.println("没有找到虚拟主机信息，无法测试getExchangesByVhost方法");
            }
        } catch (Exception e) {
            System.err.println("测试获取RabbitMQ指定虚拟主机的所有交换机时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试获取指定交换机
     */
    @Test
    public void testGetExchange() {
        try {
            System.out.println("开始测试获取RabbitMQ指定交换机...");
            JSONArray vhosts = rabbitMQInfoService.getVhosts();
            
            if (vhosts != null && !vhosts.isEmpty()) {
                String vhostName = vhosts.getJSONObject(0).getString("name");
                JSONArray exchanges = rabbitMQInfoService.getExchangesByVhost(vhostName);
                
                if (exchanges != null && !exchanges.isEmpty()) {
                    String exchangeName = exchanges.getJSONObject(0).getString("name");
                    System.out.println("获取交换机信息，虚拟主机: " + vhostName + ", 交换机名称: " + exchangeName);
                    
                    JSONObject exchange = rabbitMQInfoService.getExchange(vhostName, exchangeName);
                    
                    if (exchange != null) {
                        System.out.println("交换机信息获取成功:");
                        System.out.println("交换机名称: " + exchange.getString("name"));
                        System.out.println("交换机类型: " + exchange.getString("type"));
                        System.out.println("完整交换机信息JSON: " + exchange.toJSONString());
                    } else {
                        System.out.println("获取交换机信息失败，返回结果为null");
                    }
                } else {
                    System.out.println("没有找到交换机信息，无法测试getExchange方法");
                }
            } else {
                System.out.println("没有找到虚拟主机信息，无法测试getExchange方法");
            }
        } catch (Exception e) {
            System.err.println("测试获取RabbitMQ指定交换机时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试创建和删除交换机
     */
    @Test
    public void testCreateAndDeleteExchange() {
        try {
            System.out.println("开始测试创建和删除RabbitMQ交换机...");
            String testVhost = "/";
            String testExchange = "test_exchange_" + System.currentTimeMillis();
            
            System.out.println("创建交换机: " + testExchange + " 在虚拟主机: " + testVhost);
            rabbitMQInfoService.createExchange(testVhost, testExchange, "direct", true, false, false);
            System.out.println("交换机创建成功: " + testExchange);
            
            // 验证交换机是否创建成功
            JSONObject exchange = rabbitMQInfoService.getExchange(testVhost, testExchange);
            if (exchange != null) {
                System.out.println("验证交换机创建成功，现在删除交换机: " + testExchange);
                rabbitMQInfoService.deleteExchange(testVhost, testExchange);
                System.out.println("交换机删除成功: " + testExchange);
                
                // 验证交换机是否被删除
                JSONObject exchangeAfter = rabbitMQInfoService.getExchange(testVhost, testExchange);
                if (exchangeAfter == null || exchangeAfter.isEmpty()) {
                    System.out.println("交换机删除验证成功");
                } else {
                    System.out.println("交换机删除验证失败");
                }
            } else {
                System.out.println("交换机创建验证失败");
            }
        } catch (Exception e) {
            System.err.println("测试创建和删除RabbitMQ交换机时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试获取所有队列
     */
    @Test
    public void testGetQueues() {
        try {
            System.out.println("开始测试获取RabbitMQ所有队列...");
            JSONArray queues = rabbitMQInfoService.getQueues();
            
            if (queues != null) {
                System.out.println("RabbitMQ队列信息获取成功:");
                System.out.println("队列数量: " + queues.size());
                System.out.println("队列列表: " + queues.toJSONString());
            } else {
                System.out.println("获取队列信息失败，返回结果为null");
            }
        } catch (Exception e) {
            System.err.println("测试获取RabbitMQ所有队列时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试获取指定虚拟主机的所有队列
     */
    @Test
    public void testGetQueuesByVhost() {
        try {
            System.out.println("开始测试获取RabbitMQ指定虚拟主机的所有队列...");
            JSONArray vhosts = rabbitMQInfoService.getVhosts();
            
            if (vhosts != null && !vhosts.isEmpty()) {
                String vhostName = vhosts.getJSONObject(0).getString("name");
                System.out.println("获取虚拟主机 " + vhostName + " 的队列列表");
                
                JSONArray queues = rabbitMQInfoService.getQueuesByVhost(vhostName);
                
                if (queues != null) {
                    System.out.println("队列信息获取成功:");
                    System.out.println("队列数量: " + queues.size());
                    System.out.println("队列列表: " + queues.toJSONString());
                } else {
                    System.out.println("获取队列信息失败，返回结果为null");
                }
            } else {
                System.out.println("没有找到虚拟主机信息，无法测试getQueuesByVhost方法");
            }
        } catch (Exception e) {
            System.err.println("测试获取RabbitMQ指定虚拟主机的所有队列时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试获取指定队列
     */
    @Test
    public void testGetQueue() {
        try {
            System.out.println("开始测试获取RabbitMQ指定队列...");
            JSONArray vhosts = rabbitMQInfoService.getVhosts();
            
            if (vhosts != null && !vhosts.isEmpty()) {
                String vhostName = vhosts.getJSONObject(0).getString("name");
                JSONArray queues = rabbitMQInfoService.getQueuesByVhost(vhostName);
                
                if (queues != null && !queues.isEmpty()) {
                    String queueName = queues.getJSONObject(0).getString("name");
                    System.out.println("获取队列信息，虚拟主机: " + vhostName + ", 队列名称: " + queueName);
                    
                    JSONObject queue = rabbitMQInfoService.getQueue(vhostName, queueName);
                    
                    if (queue != null) {
                        System.out.println("队列信息获取成功:");
                        System.out.println("队列名称: " + queue.getString("name"));
                        System.out.println("队列状态: " + queue.getInteger("messages"));
                        System.out.println("完整队列信息JSON: " + queue.toJSONString());
                    } else {
                        System.out.println("获取队列信息失败，返回结果为null");
                    }
                } else {
                    System.out.println("没有找到队列信息，无法测试getQueue方法");
                }
            } else {
                System.out.println("没有找到虚拟主机信息，无法测试getQueue方法");
            }
        } catch (Exception e) {
            System.err.println("测试获取RabbitMQ指定队列时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试创建和删除队列
     */
    @Test
    public void testCreateAndDeleteQueue() {
        try {
            System.out.println("开始测试创建和删除RabbitMQ队列...");
            String testVhost = "/";
            String testQueue = "test_queue_" + System.currentTimeMillis();
            
            System.out.println("创建队列: " + testQueue + " 在虚拟主机: " + testVhost);
            rabbitMQInfoService.createQueue(testVhost, testQueue, true, false, false);
            System.out.println("队列创建成功: " + testQueue);
            
            // 验证队列是否创建成功
            JSONObject queue = rabbitMQInfoService.getQueue(testVhost, testQueue);
            if (queue != null) {
                System.out.println("验证队列创建成功，现在删除队列: " + testQueue);
                rabbitMQInfoService.deleteQueue(testVhost, testQueue);
                System.out.println("队列删除成功: " + testQueue);
                
                // 验证队列是否被删除
                JSONObject queueAfter = rabbitMQInfoService.getQueue(testVhost, testQueue);
                if (queueAfter == null || queueAfter.isEmpty()) {
                    System.out.println("队列删除验证成功");
                } else {
                    System.out.println("队列删除验证失败");
                }
            } else {
                System.out.println("队列创建验证失败");
            }
        } catch (Exception e) {
            System.err.println("测试创建和删除RabbitMQ队列时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试清空队列消息
     */
    @Test
    public void testPurgeQueue() {
        try {
            System.out.println("开始测试清空RabbitMQ队列消息...");
            String testVhost = "/";
            String testQueue = "test_queue_purge_" + System.currentTimeMillis();
            
            // 首先创建一个队列用于测试
            System.out.println("创建队列: " + testQueue + " 在虚拟主机: " + testVhost);
            rabbitMQInfoService.createQueue(testVhost, testQueue, true, false, false);
            System.out.println("队列创建成功: " + testQueue);
            
            // 清空队列消息
            System.out.println("清空队列消息: " + testQueue);
            rabbitMQInfoService.purgeQueue(testVhost, testQueue);
            System.out.println("队列消息清空完成: " + testQueue);
            
            // 最后删除队列
            rabbitMQInfoService.deleteQueue(testVhost, testQueue);
            System.out.println("测试队列已删除: " + testQueue);
        } catch (Exception e) {
            System.err.println("测试清空RabbitMQ队列消息时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试获取队列的绑定关系
     */
    @Test
    public void testGetQueueBindings() {
        try {
            System.out.println("开始测试获取RabbitMQ队列的绑定关系...");
            JSONArray vhosts = rabbitMQInfoService.getVhosts();
            
            if (vhosts != null && !vhosts.isEmpty()) {
                String vhostName = vhosts.getJSONObject(0).getString("name");
                JSONArray queues = rabbitMQInfoService.getQueuesByVhost(vhostName);
                
                if (queues != null && !queues.isEmpty()) {
                    String queueName = queues.getJSONObject(0).getString("name");
                    System.out.println("获取队列绑定关系，虚拟主机: " + vhostName + ", 队列名称: " + queueName);
                    
                    JSONObject bindings = rabbitMQInfoService.getQueueBindings(vhostName, queueName);
                    
                    if (bindings != null) {
                        System.out.println("队列绑定关系获取成功:");
                        System.out.println("绑定关系详情: " + bindings.toJSONString());
                    } else {
                        System.out.println("获取队列绑定关系失败，返回结果为null");
                    }
                } else {
                    System.out.println("没有找到队列信息，无法测试getQueueBindings方法");
                }
            } else {
                System.out.println("没有找到虚拟主机信息，无法测试getQueueBindings方法");
            }
        } catch (Exception e) {
            System.err.println("测试获取RabbitMQ队列的绑定关系时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试发布消息到交换机
     */
    @Test
    public void testPublishMessage() {
        try {
            System.out.println("开始测试发布消息到RabbitMQ交换机...");
            String testVhost = "/";
            String testExchange = "amq.topic"; // 使用默认存在的交换机
            
            System.out.println("向交换机发布消息，虚拟主机: " + testVhost + ", 交换机: " + testExchange);
            JSONObject result = rabbitMQInfoService.publishMessage(
                testVhost, 
                testExchange, 
                "test.routing.key", 
                "Hello, RabbitMQ!", 
                "string"
            );
            
            if (result != null) {
                System.out.println("消息发布成功:");
                System.out.println("发布结果: " + result.toJSONString());
            } else {
                System.out.println("消息发布失败，返回结果为null");
            }
        } catch (Exception e) {
            System.err.println("测试发布消息到RabbitMQ交换机时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试从队列获取消息
     */
    @Test
    public void testGetMessages() {
        try {
            System.out.println("开始测试从RabbitMQ队列获取消息...");
            JSONArray vhosts = rabbitMQInfoService.getVhosts();
            
            if (vhosts != null && !vhosts.isEmpty()) {
                String vhostName = vhosts.getJSONObject(0).getString("name");
                JSONArray queues = rabbitMQInfoService.getQueuesByVhost(vhostName);
                
                if (queues != null && !queues.isEmpty()) {
                    String queueName = queues.getJSONObject(0).getString("name");
                    System.out.println("从队列获取消息，虚拟主机: " + vhostName + ", 队列名称: " + queueName);
                    
                    List<JSONObject> messages = rabbitMQInfoService.getMessages(vhostName, queueName, 1, true);
                    
                    if (messages != null) {
                        System.out.println("消息获取成功:");
                        System.out.println("获取到的消息数量: " + messages.size());
                        if (!messages.isEmpty()) {
                            System.out.println("第一条消息: " + messages.get(0).toJSONString());
                        }
                    } else {
                        System.out.println("获取消息失败，返回结果为null");
                    }
                } else {
                    System.out.println("没有找到队列信息，无法测试getMessages方法");
                }
            } else {
                System.out.println("没有找到虚拟主机信息，无法测试getMessages方法");
            }
        } catch (Exception e) {
            System.err.println("测试从RabbitMQ队列获取消息时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试获取RabbitMQ队列操作统计信息
     */
    @Test
    public void testGetQueueOperationsStats() {
        try {
            System.out.println("开始测试获取RabbitMQ队列操作统计信息...");
            
            // 获取队列操作统计信息
            JSONObject queueOpsStats = rabbitMQInfoService.getQueueOperationsStats();
            
            if (queueOpsStats != null) {
                System.out.println("=== RabbitMQ 队列操作统计信息 ===");
                System.out.println("Declared (声明): " + queueOpsStats.get("declared"));
                System.out.println("Created (创建): " + queueOpsStats.get("created"));
                System.out.println("Deleted (删除): " + queueOpsStats.get("deleted"));
            } else {
                System.out.println("未能获取队列操作统计信息");
            }
            
        } catch (Exception e) {
            System.err.println("测试获取RabbitMQ队列操作统计信息时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 测试获取集群定义
     */
    @Test
    public void testGetDefinitions() {
        try {
            System.out.println("开始测试获取RabbitMQ集群定义...");
            JSONObject definitions = rabbitMQInfoService.getDefinitions();
            
            if (definitions != null) {
                System.out.println("集群定义获取成功:");
                System.out.println("定义详情: " + definitions.toJSONString());
            } else {
                System.out.println("获取集群定义失败，返回结果为null");
            }
        } catch (Exception e) {
            System.err.println("测试获取RabbitMQ集群定义时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试获取虚拟主机定义
     */
    @Test
    public void testGetDefinitionsByVhost() {
        try {
            System.out.println("开始测试获取RabbitMQ虚拟主机定义...");
            JSONArray vhosts = rabbitMQInfoService.getVhosts();
            
            if (vhosts != null && !vhosts.isEmpty()) {
                String vhostName = vhosts.getJSONObject(0).getString("name");
                System.out.println("获取虚拟主机定义，虚拟主机: " + vhostName);
                
                JSONObject definitions = rabbitMQInfoService.getDefinitionsByVhost(vhostName);
                
                if (definitions != null) {
                    System.out.println("虚拟主机定义获取成功:");
                    System.out.println("定义详情: " + definitions.toJSONString());
                } else {
                    System.out.println("获取虚拟主机定义失败，返回结果为null");
                }
            } else {
                System.out.println("没有找到虚拟主机信息，无法测试getDefinitionsByVhost方法");
            }
        } catch (Exception e) {
            System.err.println("测试获取RabbitMQ虚拟主机定义时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
}




