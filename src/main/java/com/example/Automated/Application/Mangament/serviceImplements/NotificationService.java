package com.example.Automated.Application.Mangament.serviceImplements;

import com.example.Automated.Application.Mangament.dto.request.NotificationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Gửi cho 1 người
    public void sendToUser(Long userId, NotificationDTO notification) {
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/notifications",
                notification
        );
    }


    public void sendToUsers(List<Long> userIds, NotificationDTO notification) {
        for (Long userId : userIds) {
            sendToUser(userId, notification);
        }
    }

    public void sendGlobal(NotificationDTO notification) {
        messagingTemplate.convertAndSend("/topic/notifications", notification);
    }
}
