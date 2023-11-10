package example.Config;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketHandler extends TextWebSocketHandler {
    private static ConcurrentHashMap<String, WebSocketSession> sessonMap = new ConcurrentHashMap<>();

    /* 클라이언트가 소켓 연결 시 동작 */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);

        sessonMap.put(session.getId(), session);

        JSONObject obj = new JSONObject();
        obj.put("type", "getId");
        obj.put("sessionId", session.getId());

        // 클라이언트에게 메시지 전달
        session.sendMessage(new TextMessage(obj.toJSONString()));
    }

    /* 클라이언트로부터 메시지 수신 시 동작 */
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        super.handleMessage(session, message);

        // TODO: implement
    }

    /* 클라이언트가 소켓 연결 종료 시 동작 */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
    }
}
