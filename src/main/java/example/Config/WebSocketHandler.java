package example.Config;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketHandler extends TextWebSocketHandler {
    // 채팅에 참여한 유저의 Session ID
    private ConcurrentHashMap<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();
    
    // Session ID와 Username을 매핑
    private ConcurrentHashMap<String, String> userMap = new ConcurrentHashMap<>();

    /* 클라이언트가 소켓 연결 시 동작 */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);

        System.out.println(session.getId() + " 연결되었습니다.");
        sessionMap.put(session.getId(), session);

        JSONObject obj = new JSONObject();
        obj.put("type", "getId");
        obj.put("sessionId", session.getId());

        // 클라이언트에게 메시지 전달
        session.sendMessage(new TextMessage(obj.toJSONString()));
    }

    /* 클라이언트로부터 메시지 수신 시 동작 */
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String msg = message.getPayload().toString();
        System.out.println(msg);

        JSONObject obj = jsonParser(msg);
        
        // 로그인된 유저 (afterConnectionEstablished 메서드에서 session을 저장함)
        for (String key : sessionMap.keySet()) {
            WebSocketSession webSocketSession = sessionMap.get(key);
            userMap.putIfAbsent(webSocketSession.getId(), (String) obj.get("userName"));

            // 채팅에 참가한 모든 유저에게 메시지 전송
            webSocketSession.sendMessage(new TextMessage(obj.toJSONString()));
        }
    }

    /* 클라이언트가 소켓 연결 종료 시 동작 */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);

        System.out.println(session.getId() + " 연결이 종료되었습니다.");
        sessionMap.remove(session.getId());

        String userName = userMap.get(session.getId()); // 연결 종료된 유저
        for (String key : sessionMap.keySet()) {
            WebSocketSession webSocketSession = sessionMap.get(key);

            if (webSocketSession == session) {
                continue;
            } else {
                JSONObject obj = new JSONObject();
                obj.put("type", "close");
                obj.put("userName", userName);

                // 채팅에 참가한 모든 유저에게 메시지 전송
                webSocketSession.sendMessage(new TextMessage(obj.toJSONString()));
            }
        }
        userMap.remove(session.getId());
    }

    private JSONObject jsonParser(String jsonString) throws Exception {
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(jsonString);
    }
}
