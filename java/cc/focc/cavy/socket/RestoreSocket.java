package cc.focc.cavy.socket;

import cc.focc.cavy.core.RestoreJobHandle;
import cc.focc.cavy.model.dto.SocketDataDTO;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Controller
@ServerEndpoint("/socket/v1/restore/{msgId}")
public class RestoreSocket {

    private static final ConcurrentHashMap<String, Session> SESSIONS = new ConcurrentHashMap<>();


    @OnOpen
    public void onOpen(Session session, @PathParam(value = "msgId") String msgId) {
        try {
            log.info("【WebSocket消息】有新的连接:" + msgId);
            SESSIONS.put(msgId,session);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session) {
        try {
            SESSIONS.remove(session.getPathParameters().get("msgId"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnMessage
    public void onMessage(Session session,String message) {
        log.info("【WebSocket消息】收到客户端消息：" + message);
        SocketDataDTO bean = JSONUtil.toBean(message, SocketDataDTO.class);
        RestoreJobHandle restoreJobHandle = SpringUtil.getBean(RestoreJobHandle.class);
        restoreJobHandle.restore(bean);
    }


    public static void sendMessage(String messageId, String message) {
        Session session = SESSIONS.get(messageId);
        if (session != null && session.isOpen()) {
            try {
                synchronized (session) {
                    log.info("【WebSocket消息】单点消息：" + message);
                    session.getAsyncRemote().sendText(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
