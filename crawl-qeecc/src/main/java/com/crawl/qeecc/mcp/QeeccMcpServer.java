package com.crawl.qeecc.mcp;

import com.crawl.qeecc.tool.SongSearchFunction;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * MCP Server - 模型上下文协议服务端
 * 将歌曲查询能力通过标准化协议暴露给外部 AI 客户端（如 Claude Desktop、Cursor 等）
 * 传输层: SSE (Server-Sent Events)
 */
@RestController
@RequestMapping("/mcp")
public class QeeccMcpServer {

    private final SongSearchFunction songSearchFunction;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public QeeccMcpServer(SongSearchFunction songSearchFunction) {
        this.songSearchFunction = songSearchFunction;
    }

    /**
     * SSE 连接端点
     * MCP 客户端通过此端点建立事件流连接接收服务端消息
     */
    @GetMapping("/sse")
    public SseEmitter sse() {
        SseEmitter emitter = new SseEmitter(0L);
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(e -> emitters.remove(emitter));

        // 发送 endpoint 信息，告知客户端消息发送地址
        try {
            emitter.send(SseEmitter.event()
                    .name("endpoint")
                    .data("/mcp/message"));
        } catch (IOException e) {
            emitters.remove(emitter);
        }
        return emitter;
    }

    /**
     * JSON-RPC 消息处理端点
     * MCP 客户端通过 POST 发送请求，服务端通过 SSE 返回响应
     */
    @PostMapping("/message")
    public void message(@RequestBody Map<String, Object> request) {
        String method = (String) request.get("method");
        Object idObj = request.get("id");
        Integer id = idObj instanceof Number ? ((Number) idObj).intValue() : null;

        System.out.println("[MCP] 收到请求: method=" + method + ", id=" + id);

        try {
            switch (method) {
                case "initialize" -> sendResponse(id, Map.of(
                        "protocolVersion", "2024-11-05",
                        "capabilities", Map.of("tools", Map.of()),
                        "serverInfo", Map.of("name", "qeecc-mcp", "version", "1.0.0")
                ));
                case "notifications/initialized" -> {
                    // 无需响应
                }
                case "tools/list" -> sendResponse(id, Map.of("tools", List.of(Map.of(
                        "name", "searchSong",
                        "description", "在 qeecc.com 网站搜索指定歌曲是否存在",
                        "inputSchema", Map.of(
                                "type", "object",
                                "properties", Map.of("songName", Map.of(
                                        "type", "string",
                                        "description", "要搜索的歌曲名称"
                                )),
                                "required", List.of("songName")
                        )
                ))));
                case "tools/call" -> handleToolCall(request, id);
                default -> sendError(id, -32601, "Method not found: " + method);
            }
        } catch (Exception e) {
            System.out.println("[MCP] 处理失败: " + e.getMessage());
            sendError(id, -32603, "Internal error: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void handleToolCall(Map<String, Object> request, Integer id) {
        Map<String, Object> params = (Map<String, Object>) request.get("params");
        String toolName = (String) params.get("name");
        Map<String, Object> arguments = (Map<String, Object>) params.get("arguments");

        System.out.println("[MCP] 工具调用: " + toolName + ", args=" + arguments);

        if ("searchSong".equals(toolName)) {
            String songName = (String) arguments.get("songName");
            SongSearchFunction.Response response = songSearchFunction.apply(
                    new SongSearchFunction.Request(songName));
            sendResponse(id, Map.of("content", List.of(Map.of(
                    "type", "text",
                    "text", response.result()
            ))));
        } else {
            sendError(id, -32602, "Tool not found: " + toolName);
        }
    }

    private void sendResponse(Integer id, Object result) {
        Map<String, Object> response = Map.of("jsonrpc", "2.0", "id", id, "result", result);
        broadcast(response);
    }

    private void sendError(Integer id, int code, String message) {
        Map<String, Object> response = Map.of("jsonrpc", "2.0", "id", id,
                "error", Map.of("code", code, "message", message));
        broadcast(response);
    }

    private void broadcast(Map<String, Object> response) {
        String json;
        try {
            json = objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            return;
        }
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().data(json));
            } catch (IOException e) {
                emitters.remove(emitter);
            }
        }
    }
}
