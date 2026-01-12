package org.bem.iot.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.bem.iot.util.WebClientUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RabbitMQ API接口
 * @author JiangShiYi
 */
@Service
public class RabbitMQInfoService {
    
    @Value("${spring.rabbitmq.management.host:localhost}")
    private String host;
    
    @Value("${spring.rabbitmq.management.port:15672}")
    private String port;
    
    @Value("${spring.rabbitmq.management.username:guest}")
    private String username;
    
    @Value("${spring.rabbitmq.management.password:guest}")
    private String password;
    
    private String baseUrl;
    private String basicAuthHeader;
    
    @PostConstruct
    private void init() {
        this.baseUrl = "http://" + host + ":" + port + "/api";
        String auth = username + ":" + password;
        this.basicAuthHeader = "Basic " + Base64.getEncoder().encodeToString(auth.getBytes());
    }
    
    // 构建带认证头的请求头
    private Map<String, String> getAuthHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", basicAuthHeader);
        headers.put("Content-Type", "application/json");
        return headers;
    }
    
    // ================================================
    // 系统概述相关API
    // ================================================
    
    /**
     * 获取系统概述信息
     * GET /api/overview
     */
    public JSONObject getOverview() {
        String url = baseUrl + "/overview";
        return WebClientUtil.get(url, getAuthHeaders(), new HashMap<>(), JSONObject.class);
    }
    
    /**
     * 获取集群名称
     * GET /api/cluster-name
     */
    public JSONObject getClusterName() {
        String url = baseUrl + "/cluster-name";
        return WebClientUtil.get(url, getAuthHeaders(), new HashMap<>(), JSONObject.class);
    }
    
    /**
     * 更新集群名称
     * PUT /api/cluster-name
     */
    public void updateClusterName(String name) {
        String url = baseUrl + "/cluster-name";
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", name);
        WebClientUtil.putJson(url, getAuthHeaders(), payload, JSONObject.class);
    }
    
    // ================================================
    // 节点管理相关API
    // ================================================
    
    /**
     * 获取所有节点信息
     * GET /api/nodes
     */
    public JSONArray getNodes() {
        String url = baseUrl + "/nodes";
        return WebClientUtil.get(url, getAuthHeaders(), new HashMap<>(), JSONArray.class);
    }
    
    /**
     * 获取指定节点信息
     * GET /api/nodes/{name}
     */
    public JSONObject getNode(String nodeName) {
        String encodedNodeName = encodeVhost(nodeName);
        String url = baseUrl + "/nodes/" + encodedNodeName;
        return WebClientUtil.get(url, getAuthHeaders(), new HashMap<>(), JSONObject.class);
    }
    
    /**
     * 获取指定节点内存信息
     * GET /api/nodes/{name}/memory
     */
    public JSONObject getNodeMemory(String nodeName) {
        String encodedNodeName = encodeVhost(nodeName);
        String url = baseUrl + "/nodes/" + encodedNodeName + "/memory";
        return WebClientUtil.get(url, getAuthHeaders(), new HashMap<>(), JSONObject.class);
    }
    
    // ================================================
    // 虚拟主机管理相关API
    // ================================================
    
    /**
     * 获取所有虚拟主机
     * GET /api/vhosts
     */
    public JSONArray getVhosts() {
        String url = baseUrl + "/vhosts";
        return WebClientUtil.get(url, getAuthHeaders(), new HashMap<>(), JSONArray.class);
    }
    
    /**
     * 获取指定虚拟主机
     * GET /api/vhosts/{vhost}
     */
    public JSONObject getVhost(String vhost) {
        String encodedVhost = encodeVhost(vhost);
        String url = baseUrl + "/vhosts/" + encodedVhost;
        return WebClientUtil.get(url, getAuthHeaders(), new HashMap<>(), JSONObject.class);
    }
    
    /**
     * 创建虚拟主机
     * PUT /api/vhosts/{vhost}
     */
    public void createVhost(String vhost) {
        String encodedVhost = encodeVhost(vhost);
        String url = baseUrl + "/vhosts/" + encodedVhost;
        WebClientUtil.putJson(url, getAuthHeaders(), new HashMap<>(), JSONObject.class);
    }
    
    /**
     * 删除虚拟主机
     * DELETE /api/vhosts/{vhost}
     */
    public void deleteVhost(String vhost) {
        String encodedVhost = encodeVhost(vhost);
        String url = baseUrl + "/vhosts/" + encodedVhost;
        WebClientUtil.delete(url, getAuthHeaders(), new HashMap<>(), JSONObject.class);
    }
    
    // ================================================
    // 用户管理相关API
    // ================================================
    
    /**
     * 获取所有用户
     * GET /api/users
     */
    public JSONArray getUsers() {
        String url = baseUrl + "/users";
        return WebClientUtil.get(url, getAuthHeaders(), new HashMap<>(), JSONArray.class);
    }
    
    /**
     * 获取指定用户
     * GET /api/users/{name}
     */
    public JSONObject getUser(String username) {
        String url = baseUrl + "/users/" + username;
        return WebClientUtil.get(url, getAuthHeaders(), new HashMap<>(), JSONObject.class);
    }
    
    /**
     * 创建用户
     * PUT /api/users/{name}
     */
    public void createUser(String username, String password, List<String> tags) {
        String url = baseUrl + "/users/" + username;
        Map<String, Object> payload = new HashMap<>();
        payload.put("password", password);
        payload.put("tags", String.join(",", tags));
        WebClientUtil.putJson(url, getAuthHeaders(), payload, JSONObject.class);
    }
    
    /**
     * 删除用户
     * DELETE /api/users/{name}
     */
    public void deleteUser(String username) {
        String url = baseUrl + "/users/" + username;
        WebClientUtil.delete(url, getAuthHeaders(), new HashMap<>(), JSONObject.class);
    }
    
    // ================================================
    // 权限管理相关API
    // ================================================
    
    /**
     * 获取虚拟主机中指定用户的权限
     * GET /api/permissions/{vhost}/{user}
     */
    public JSONObject getPermissions(String vhost, String username) {
        String encodedVhost = encodeVhost(vhost);
        String url = baseUrl + "/permissions/" + encodedVhost + "/" + username;
        return WebClientUtil.get(url, getAuthHeaders(), new HashMap<>(), JSONObject.class);
    }
    
    /**
     * 设置虚拟主机中指定用户的权限
     * PUT /api/permissions/{vhost}/{user}
     */
    public void setPermissions(String vhost, String username, String configure, String write, String read) {
        String encodedVhost = encodeVhost(vhost);
        String url = baseUrl + "/permissions/" + encodedVhost + "/" + username;
        Map<String, Object> payload = new HashMap<>();
        payload.put("configure", configure);
        payload.put("write", write);
        payload.put("read", read);
        WebClientUtil.putJson(url, getAuthHeaders(), payload, JSONObject.class);
    }
    
    /**
     * 删除虚拟主机中指定用户的权限
     * DELETE /api/permissions/{vhost}/{user}
     */
    public void deletePermissions(String vhost, String username) {
        String encodedVhost = encodeVhost(vhost);
        String url = baseUrl + "/permissions/" + encodedVhost + "/" + username;
        WebClientUtil.delete(url, getAuthHeaders(), new HashMap<>(), JSONObject.class);
    }
    
    // ================================================
    // 交换机管理相关API
    // ================================================
    
    /**
     * 获取所有交换机
     * GET /api/exchanges
     */
    public JSONArray getExchanges() {
        String url = baseUrl + "/exchanges";
        return WebClientUtil.get(url, getAuthHeaders(), new HashMap<>(), JSONArray.class);
    }
    
    /**
     * 获取指定虚拟主机的所有交换机
     * GET /api/exchanges/{vhost}
     */
    public JSONArray getExchangesByVhost(String vhost) {
        String encodedVhost = encodeVhost(vhost);
        String url = baseUrl + "/exchanges/" + encodedVhost;
        return WebClientUtil.get(url, getAuthHeaders(), new HashMap<>(), JSONArray.class);
    }
    
    /**
     * 获取指定交换机
     * GET /api/exchanges/{vhost}/{name}
     */
    public JSONObject getExchange(String vhost, String name) {
        String encodedVhost = encodeVhost(vhost);
        String url = baseUrl + "/exchanges/" + encodedVhost + "/" + name;
        return WebClientUtil.get(url, getAuthHeaders(), new HashMap<>(), JSONObject.class);
    }
    
    /**
     * 创建交换机
     * PUT /api/exchanges/{vhost}/{name}
     */
    public void createExchange(String vhost, String name, String type, boolean durable, boolean autoDelete, boolean internal) {
        String encodedVhost = encodeVhost(vhost);
        String url = baseUrl + "/exchanges/" + encodedVhost + "/" + name;
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", type);
        payload.put("durable", durable);
        payload.put("auto_delete", autoDelete);
        payload.put("internal", internal);
        payload.put("arguments", new HashMap<>());
        WebClientUtil.putJson(url, getAuthHeaders(), payload, JSONObject.class);
    }
    
    /**
     * 删除交换机
     * DELETE /api/exchanges/{vhost}/{name}
     */
    public void deleteExchange(String vhost, String name) {
        String encodedVhost = encodeVhost(vhost);
        String url = baseUrl + "/exchanges/" + encodedVhost + "/" + name;
        WebClientUtil.delete(url, getAuthHeaders(), new HashMap<>(), JSONObject.class);
    }
    
    // ================================================
    // 队列管理相关API
    // ================================================
    
    /**
     * 获取所有队列
     * GET /api/queues
     */
    public JSONArray getQueues() {
        String url = baseUrl + "/queues";
        return WebClientUtil.get(url, getAuthHeaders(), new HashMap<>(), JSONArray.class);
    }
    
    /**
     * 获取指定虚拟主机的所有队列
     * GET /api/queues/{vhost}
     */
    public JSONArray getQueuesByVhost(String vhost) {
        String encodedVhost = encodeVhost(vhost);
        String url = baseUrl + "/queues/" + encodedVhost;
        return WebClientUtil.get(url, getAuthHeaders(), new HashMap<>(), JSONArray.class);
    }
    
    /**
     * 获取指定队列
     * GET /api/queues/{vhost}/{name}
     */
    public JSONObject getQueue(String vhost, String name) {
        String encodedVhost = encodeVhost(vhost);
        String url = baseUrl + "/queues/" + encodedVhost + "/" + name;
        return WebClientUtil.get(url, getAuthHeaders(), new HashMap<>(), JSONObject.class);
    }
    
    /**
     * 创建队列
     * PUT /api/queues/{vhost}/{name}
     */
    public void createQueue(String vhost, String name, boolean durable, boolean autoDelete, boolean exclusive) {
        String encodedVhost = encodeVhost(vhost);
        String url = baseUrl + "/queues/" + encodedVhost + "/" + name;
        Map<String, Object> payload = new HashMap<>();
        payload.put("durable", durable);
        payload.put("auto_delete", autoDelete);
        payload.put("exclusive", exclusive);
        payload.put("arguments", new HashMap<>());
        WebClientUtil.putJson(url, getAuthHeaders(), payload, JSONObject.class);
    }
    
    /**
     * 删除队列
     * DELETE /api/queues/{vhost}/{name}
     */
    public void deleteQueue(String vhost, String name) {
        String encodedVhost = encodeVhost(vhost);
        String url = baseUrl + "/queues/" + encodedVhost + "/" + name;
        WebClientUtil.delete(url, getAuthHeaders(), new HashMap<>(), JSONObject.class);
    }
    
    /**
     * 清空队列消息
     * DELETE /api/queues/{vhost}/{name}/contents
     */
    public void purgeQueue(String vhost, String name) {
        String encodedVhost = encodeVhost(vhost);
        String url = baseUrl + "/queues/" + encodedVhost + "/" + name + "/contents";
        WebClientUtil.delete(url, getAuthHeaders(), new HashMap<>(), JSONObject.class);
    }
    
    // ================================================
    // 绑定管理相关API
    // ================================================
    
    /**
     * 获取队列的绑定关系
     * GET /api/queues/{vhost}/{name}/bindings
     */
    public JSONObject getQueueBindings(String vhost, String name) {
        String encodedVhost = encodeVhost(vhost);
        String url = baseUrl + "/queues/" + encodedVhost + "/" + name + "/bindings";
        return WebClientUtil.get(url, getAuthHeaders(), new HashMap<>(), JSONObject.class);
    }
    
    /**
     * 创建绑定关系
     * POST /api/bindings/{vhost}/e/{source}/q/{destination}
     */
    public void bindQueue(String vhost, String exchange, String queue, String routingKey) {
        String encodedVhost = encodeVhost(vhost);
        String url = baseUrl + "/bindings/" + encodedVhost + "/e/" + exchange + "/q/" + queue;
        Map<String, Object> payload = new HashMap<>();
        payload.put("routing_key", routingKey);
        payload.put("arguments", new HashMap<>());
        WebClientUtil.postJson(url, getAuthHeaders(), payload, JSONObject.class);
    }
    
    /**
     * 删除绑定关系
     * DELETE /api/bindings/{vhost}/e/{source}/q/{destination}/{props}
     */
    public void unbindQueue(String vhost, String exchange, String queue, String routingKey) {
        String encodedVhost = encodeVhost(vhost);
        // props是基于绑定属性生成的，这里简化处理
        String props = encodeRoutingKey(routingKey);
        String url = baseUrl + "/bindings/" + encodedVhost + "/e/" + exchange + "/q/" + queue + "/" + props;
        WebClientUtil.delete(url, getAuthHeaders(), new HashMap<>(), JSONObject.class);
    }
    
    // ================================================
    // 消息发布与消费相关API
    // ================================================
    
    /**
     * 发布消息到交换机
     * POST /api/exchanges/{vhost}/{name}/publish
     */
    public JSONObject publishMessage(String vhost, String exchange, String routingKey, String payload, String payloadEncoding) {
        String encodedVhost = encodeVhost(vhost);
        String url = baseUrl + "/exchanges/" + encodedVhost + "/" + exchange + "/publish";
        Map<String, Object> message = new HashMap<>();
        Map<String, Object> props = new HashMap<>();
        message.put("properties", props);
        message.put("routing_key", routingKey);
        message.put("payload", payload);
        message.put("payload_encoding", payloadEncoding != null ? payloadEncoding : "string");
        return WebClientUtil.postJson(url, getAuthHeaders(), message, JSONObject.class);
    }
    
    /**
     * 从队列获取消息（拉取模式）
     * POST /api/queues/{vhost}/{name}/get
     */
    public List<JSONObject> getMessages(String vhost, String queue, int count, boolean requeue) {
        String encodedVhost = encodeVhost(vhost);
        String url = baseUrl + "/queues/" + encodedVhost + "/" + queue + "/get";
        Map<String, Object> request = new HashMap<>();
        request.put("count", count);
        request.put("requeue", requeue);
        request.put("encoding", "auto");
        request.put("truncate", 50000);
        return WebClientUtil.postJson(url, getAuthHeaders(), request, List.class);
    }
    
    // ================================================
    // 定义导出/导入相关API
    // ================================================
    
    /**
     * 导出集群定义
     * GET /api/definitions
     */
    public JSONObject getDefinitions() {
        String url = baseUrl + "/definitions";
        return WebClientUtil.get(url, getAuthHeaders(), new HashMap<>(), JSONObject.class);
    }
    
    /**
     * 导出虚拟主机定义
     * GET /api/definitions/{vhost}
     */
    public JSONObject getDefinitionsByVhost(String vhost) {
        String encodedVhost = encodeVhost(vhost);
        String url = baseUrl + "/definitions/" + encodedVhost;
        return WebClientUtil.get(url, getAuthHeaders(), new HashMap<>(), JSONObject.class);
    }
    
    /**
     * 上传集群定义
     * POST /api/definitions
     */
    public void uploadDefinitions(JSONObject definitions) {
        String url = baseUrl + "/definitions";
        WebClientUtil.postJson(url, getAuthHeaders(), definitions, JSONObject.class);
    }
    
    // ================================================
    // 队列操作统计相关API
    // ================================================
    
    /**
     * 获取队列操作累计统计信息
     * 包括：Declared（声明）、Created（创建）、Deleted（删除）的总计值
     * GET /api/overview
     */
    public JSONObject getQueueOperationsStats() {
        JSONObject overview = getOverview();
        if (overview != null) {
            JSONObject messageStats = overview.getJSONObject("message_stats");
            Integer queueCount = overview.getInteger("queue_count");
            
            JSONObject queueOpsStats = new JSONObject();
            
            // 尝试从message_stats获取真正的累计统计信息
            if (messageStats != null) {
                // RabbitMQ的message_stats中可能包含队列操作的累计统计
                // 不同版本的RabbitMQ可能使用不同的字段名
                Integer declaredQueueTotal = null;
                Integer createdQueueTotal = null;
                Integer deletedQueueTotal = null;
                
                // 检查各种可能的字段名
                if (messageStats.containsKey("queue_declared")) {
                    declaredQueueTotal = messageStats.getInteger("queue_declared");
                } else if (messageStats.containsKey("queue_declared_details")) {
                    JSONObject details = messageStats.getJSONObject("queue_declared_details");
                    if (details != null) {
                        declaredQueueTotal = details.getInteger("rate");
                    }
                }
                
                if (messageStats.containsKey("queue_created")) {
                    createdQueueTotal = messageStats.getInteger("queue_created");
                }
                
                if (messageStats.containsKey("queue_deleted")) {
                    deletedQueueTotal = messageStats.getInteger("queue_deleted");
                }
                
                // 也检查以_total结尾的字段名
                if (declaredQueueTotal == null && messageStats.containsKey("queue_declared_total")) {
                    declaredQueueTotal = messageStats.getInteger("queue_declared_total");
                }
                if (createdQueueTotal == null && messageStats.containsKey("queue_created_total")) {
                    createdQueueTotal = messageStats.getInteger("queue_created_total");
                }
                if (deletedQueueTotal == null && messageStats.containsKey("queue_deleted_total")) {
                    deletedQueueTotal = messageStats.getInteger("queue_deleted_total");
                }
                
                // 如果存在这些字段，则使用它们作为累计统计值
                if (declaredQueueTotal != null) {
                    queueOpsStats.put("declared", declaredQueueTotal);
                } else {
                    // 如果没有具体的声明队列总数，使用队列总数作为近似值
                    queueOpsStats.put("declared", queueCount != null ? queueCount : 0);
                }
                
                if (createdQueueTotal != null) {
                    queueOpsStats.put("created", createdQueueTotal);
                } else {
                    queueOpsStats.put("created", queueCount != null ? queueCount : 0);
                }
                
                if (deletedQueueTotal != null) {
                    queueOpsStats.put("deleted", deletedQueueTotal);
                } else {
                    queueOpsStats.put("deleted", 0); // 删除的队列数量如果没有明确提供则设为0
                }
            } else {
                // 如果message_stats不可用，使用队列总数作为近似值
                queueOpsStats.put("declared", queueCount != null ? queueCount : 0);
                queueOpsStats.put("created", queueCount != null ? queueCount : 0);
                queueOpsStats.put("deleted", 0);
            }
            
            return queueOpsStats;
        }
        
        // 如果无法获取统计信息，返回默认值
        JSONObject fallbackStats = new JSONObject();
        fallbackStats.put("declared", 0);
        fallbackStats.put("created", 0);
        fallbackStats.put("deleted", 0);
        
        return fallbackStats;
    }
    
    /**
     * 获取所有队列的详细统计信息以计算累计值
     * GET /api/queues
     */
    public JSONObject getQueueCumulativeStats() {
        JSONArray allQueues = getQueues();
        JSONObject cumulativeStats = new JSONObject();
        
        if (allQueues != null) {
            int totalDeclared = allQueues.size(); // 当前存在的队列数，可以视为已声明队列的净数量
            int totalCreated = 0;
            int totalDeleted = 0;
            
            // 遍历所有队列，汇总统计信息
            for (int i = 0; i < allQueues.size(); i++) {
                JSONObject queue = allQueues.getJSONObject(i);
                
                // 获取每个队列的特定统计信息
                JSONObject messageStats = queue.getJSONObject("message_stats");
                if (messageStats != null) {
                    // 这里我们尝试获取每个队列的详细统计信息
                    Integer published = messageStats.getInteger("publish");
                    Integer delivered = messageStats.getInteger("deliver_get");
                }
            }
            
            // 由于无法直接从队列列表获取创建和删除的累计数量，
            // 我们只能返回当前存在的队列数量作为已声明的累计值
            cumulativeStats.put("declared", totalDeclared);
            cumulativeStats.put("created", totalDeclared); // 假设创建的队列数量至少等于当前存在的数量
            cumulativeStats.put("deleted", 0); // 删除的数量需要从其他地方获取
            
            return cumulativeStats;
        }
        
        // 如果无法获取队列列表，返回默认值
        cumulativeStats.put("declared", 0);
        cumulativeStats.put("created", 0);
        cumulativeStats.put("deleted", 0);
        
        return cumulativeStats;
    }
    

    
    // ================================================
    // 辅助方法
    // ================================================
    
    /**
     * URL编码虚拟主机名称（将"/"编码为"%2F"）
     */
    private String encodeVhost(String vhost) {
        if (vhost.equals("/")) {
            return "%2F";
        }
        return vhost.replace("/", "%2F");
    }
    
    /**
     * 编码路由键
     */
    private String encodeRoutingKey(String routingKey) {
        // 这是一个简化的实现，实际的props编码可能更复杂
        return routingKey.replace("%", "%25").replace("+", "%2B").replace(" ", "%20");
    }
    
    // ================================================
    // 客户端连接统计相关API
    // ================================================
    
    /**
     * 获取当前活跃连接数（客户端连接统计）
     * GET /api/connections
     */
    public int getActiveConnectionsCount() {
        JSONArray connections = getConnections();
        if (connections != null) {
            return connections.size();
        }
        return 0;
    }
    
    /**
     * 获取所有连接信息
     * GET /api/connections
     */
    public JSONArray getConnections() {
        String url = baseUrl + "/connections";
        return WebClientUtil.get(url, getAuthHeaders(), new HashMap<>(), JSONArray.class);
    }
    
    /**
     * 获取当前消费者数（可用来间接统计在线客户端）
     * GET /api/consumers
     */
    public int getActiveConsumersCount() {
        JSONArray consumers = getConsumers();
        if (consumers != null) {
            return consumers.size();
        }
        return 0;
    }
    
    /**
     * 获取所有消费者信息
     * GET /api/consumers
     */
    public JSONArray getConsumers() {
        String url = baseUrl + "/consumers";
        return WebClientUtil.get(url, getAuthHeaders(), new HashMap<>(), JSONArray.class);
    }
    
    /**
     * 获取客户端连接统计信息
     * 包括：当前活跃连接数、当前消费者数
     */
    public JSONObject getClientConnectionStats() {
        JSONObject stats = new JSONObject();
        
        // 获取活跃连接数
        int activeConnections = getActiveConnectionsCount();
        
        // 获取活跃消费者数
        int activeConsumers = getActiveConsumersCount();
        
        stats.put("active_connections", activeConnections);
        stats.put("active_consumers", activeConsumers);
        
        return stats;
    }
}