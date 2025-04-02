<template>
    <div class="notifications-container">
        <div class="notifications-wrapper">
            <TransitionGroup name="slide-notification">
                <div v-for="(notification, index) in activeNotifications" 
                    :key="notification.id" 
                    class="mac-notification"
                    :style="{ top: `${getNotificationTop(index)}px` }">
                    <div class="notification-header">
                        <v-icon color="white" size="small">mdi-bell</v-icon>
                        <span class="ml-2">알림</span>
                        <v-btn
                            icon="mdi-close"
                            size="small"
                            variant="text"
                            color="white"
                            @click="removeNotification(notification.id)"
                            class="close-btn"
                        >
                        </v-btn>
                    </div>
                    <div class="notification-content">
                        <div class="notification-title"><h2>{{ notification.title }}</h2></div>
                        <div class="notification-description" @click="toggleDescription(notification)">
                            <span v-if="notification.isExpanded">{{ notification.description }}</span>
                            <span v-else>{{ truncateText(notification.description) }}</span>
                            <v-icon
                                v-if="notification.description.length > 20"
                                small
                                color="white"
                                class="expand-icon"
                            >
                                {{ notification.isExpanded ? 'mdi-chevron-up' : 'mdi-chevron-down' }}
                            </v-icon>
                        </div>
                        <div class="notification-time">
                            {{ getElapsedTime(notification.createdAt) }}
                        </div>
                    </div>
                </div>
            </TransitionGroup>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';
import BaseRepository from './repository/BaseRepository';
import axios from 'axios';

// Repository 인스턴스 생성
const repository = new BaseRepository(axios, 'reservations');

// State
const reservations = ref([]);
const currentDate = ref(null);
const activeNotifications = ref([]);
const notificationCounter = ref(0);
const userInfo = ref(null);
const reconnectAttempts = ref(0);
const maxReconnectAttempts = 10;
let updateTimeInterval = null;

// Methods
const generateUUID = () => {
    return 'm' + 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        var r = Math.random() * 16 | 0,
            v = c == 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
};

const truncateText = (text) => {
    if (!text) return '';
    return text.length > 20 ? text.substring(0, 20) + '...' : text;
};

const removeNotification = (id) => {
    activeNotifications.value = activeNotifications.value.filter(n => n.id !== id);
};

const addNotification = (reservation) => {
    if(!reservation.taskId) {
        reservation.taskId = generateUUID();
    }
    const existingNotification = activeNotifications.value.find(n => n.taskId === reservation.taskId);
    if (!existingNotification) {
        activeNotifications.value.push({
            id: notificationCounter.value++,
            taskId: reservation.taskId,
            title: reservation.title,
            description: reservation.displayDescription || reservation.description,
            createdAt: new Date(),
            isExpanded: false
        });
    }
};

const toggleDescription = (notification) => {
    if (notification.description.length > 20) {
        notification.isExpanded = !notification.isExpanded;
    }
};

const setupTimeEventSource = () => {
    const eventSource = new EventSource('/notifications/stream');
    
    eventSource.onopen = () => {
        console.log('Time SSE connection opened');
        reconnectAttempts.value = 0;
    };

    eventSource.onerror = (error) => {
        console.error('Time SSE error:', error);
        eventSource.close();
        
        if (reconnectAttempts.value < maxReconnectAttempts) {
            reconnectAttempts.value++;
            console.log(`Attempting to reconnect time stream (${reconnectAttempts.value}/${maxReconnectAttempts})`);
            setTimeout(() => {
                setupTimeEventSource();
            }, 5000 * reconnectAttempts.value);
        }
    };

    eventSource.addEventListener('time', handleTimeEvent);
};

const setupNotificationEventSource = () => {
    const notificationSource = new EventSource('/notifications/subscribe');
    
    notificationSource.onopen = () => {
        console.log('Notification SSE connection opened');
        reconnectAttempts.value = 0;
    };

    notificationSource.onerror = (error) => {
        console.error('Notification SSE error:', error);
        notificationSource.close();
        
        if (reconnectAttempts.value < maxReconnectAttempts) {
            reconnectAttempts.value++;
            console.log(`Attempting to reconnect notification stream (${reconnectAttempts.value}/${maxReconnectAttempts})`);
            setTimeout(() => {
                setupNotificationEventSource();
            }, 5000 * reconnectAttempts.value);
        }
    };

    notificationSource.addEventListener('notification', handleNotificationEvent);
};

const handleTimeEvent = (event) => {
    const currentTime = event.data;
    currentDate.value = currentTime.substring(0, 16);

    reservations.value.forEach(async (reservation) => {
        const targetUserIds = reservation.targetUserIds || [];
        const userMatches = targetUserIds.includes("target_all_users") || targetUserIds.includes(userInfo.value.userId);
        if(userMatches) {
            const currentDateTime = new Date(currentTime);
            const dueDateTime = new Date(reservation.dueDate);
            const tenMinutesBeforeDue = new Date(dueDateTime.getTime() - 10 * 60000);
            
            if (currentDateTime.getFullYear() === tenMinutesBeforeDue.getFullYear() &&
                currentDateTime.getMonth() === tenMinutesBeforeDue.getMonth() &&
                currentDateTime.getDate() === tenMinutesBeforeDue.getDate() &&
                currentDateTime.getHours() === tenMinutesBeforeDue.getHours() &&
                currentDateTime.getMinutes() === tenMinutesBeforeDue.getMinutes()) {
                
                addNotification({
                    ...reservation,
                    taskId: `${reservation.taskId}_10min`,
                    displayDescription: `[10분 후 시작] ${reservation.description}`
                });
            } else if (currentDateTime.getFullYear() === dueDateTime.getFullYear() &&
                currentDateTime.getMonth() === dueDateTime.getMonth() &&
                currentDateTime.getDate() === dueDateTime.getDate() &&
                currentDateTime.getHours() === dueDateTime.getHours() &&
                currentDateTime.getMinutes() === dueDateTime.getMinutes()) {
                
                addNotification({
                    ...reservation,
                    taskId: `${reservation.taskId}_start`,
                    displayDescription: `[일정 시작] ${reservation.description}`
                });
            } else if (dueDateTime < currentDateTime) {
                // 로컬 상태에서 예약이 존재하는지 먼저 확인
                const existingReservation = reservations.value.find(r => r.taskId === reservation.taskId);
                if (existingReservation) {
                    // 로컬 상태에서 먼저 제거
                    reservations.value = reservations.value.filter(r => r.taskId !== reservation.taskId);
                    // API 호출로 서버에서 삭제
                    await repository.delete(reservation);
                    
                    await axios.post('/notifications/broadcast', {
                        type: 'NOTIFICATION_DELETED',
                        taskId: reservation.taskId
                    });
                }
            }
        }
    });
};

const handleNotificationEvent = (event) => {
    const eventData = JSON.parse(event.data);
    
    if (eventData.type === 'NOTIFICATION_ADDED') {
        const existingNotification = reservations.value.find(n => n.taskId === eventData.notification.taskId);
        if (existingNotification) {
            existingNotification.dueDate = eventData.notification.dueDate;
        } else {
            reservations.value.push(eventData.notification);
        }
    } else if (eventData.type === 'NOTIFICATION_DELETED') {
        reservations.value = reservations.value.filter(
            noti => noti.taskId !== eventData.taskId
        );
    } else {
        const targetUserIds = eventData.targetUserIds || [];
        const userMatches = targetUserIds.includes("target_all_users") || targetUserIds.includes(userInfo.value.userId);
        if(userMatches) {
            if (eventData.title && eventData.description) {
                addNotification(eventData);
            }
        }
    }
};

const getElapsedTime = (createdAt) => {
    if (!createdAt) return '';
    
    const now = new Date();
    const created = new Date(createdAt);
    const diff = now - created;
    const minutes = Math.floor(diff / 60000);
    const hours = Math.floor(minutes / 60);

    if (hours > 0) {
        return `${hours}시간 전`;
    } else if (minutes > 0) {
        return `${minutes}분 전`;
    } else {
        return '지금';
    }
};

const startTimeUpdate = () => {
    updateTimeInterval = setInterval(() => {
        // 모든 활성 알림의 경과 시간을 강제로 업데이트
        activeNotifications.value = [...activeNotifications.value];
    }, 10000); // 10초마다 업데이트 (1분 미만의 변화를 감지하기 위함)
};

const getNotificationTop = (index) => {
    let top = 20;
    for (let i = 0; i < index; i++) {
        const prevNotification = activeNotifications.value[i];
        const baseHeight = 145;
        const expandedHeight = prevNotification.isExpanded ? 
            (prevNotification.description.length / 20) * 17 : 0;
        top += baseHeight + expandedHeight + 20;
    }
    return top;
};

// Lifecycle hooks
onMounted(async () => {
    userInfo.value = JSON.parse(localStorage.getItem('userInfo'));

    // 초기 알림 목록 로드 - BaseRepository 사용
    const temp = await repository.find(null);
    reservations.value = temp;

    // SSE 연결 설정
    setupTimeEventSource();
    setupNotificationEventSource();
    startTimeUpdate();
});

onBeforeUnmount(() => {
    if (window.timeEventSource) {
        window.timeEventSource.close();
    }
    if (window.notificationEventSource) {
        window.notificationEventSource.close();
    }
    if (updateTimeInterval) {
        clearInterval(updateTimeInterval);
    }
});
</script>

<style scoped>
*{
    font-family: "Noto Sans KR", sans-serif !important;
}

.notifications-container {
    position: fixed;
    top: 0;
    right: 0;
    z-index: 999999999;
    width: 340px;
    height: 100vh;
}

.notifications-wrapper {
    height: 100%;
    overflow-y: auto;
    position: relative;
}

.mac-notification {
    pointer-events: auto;
    position: absolute;
    right: 20px;
    width: 300px;
    background: rgba(50, 50, 50, 0.95);
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    overflow: hidden;
    z-index: 999999;
    transition: all 0.3s ease;
}

.notification-header {
    padding: 12px 15px;
    background: rgba(60, 60, 60, 0.95);
    color: white !important;
    font-weight: 500;
    display: flex;
    align-items: center;
}

.notification-header .v-icon {
    color: white !important;
}

.notification-content {
    padding: 15px;
    color: white;
    font-size: 14px;
    word-break: break-word;
}

.close-btn {
    margin-left: auto !important;
    margin-right: -15px !important;
    margin-top: -15px;
    color: white !important;
}

.close-btn :deep(.v-btn__content) {
    color: white !important;
}

.slide-notification-enter-active,
.slide-notification-leave-active {
    transition: all 0.3s ease;
}

.slide-notification-enter-from {
    transform: translateX(100%);
    opacity: 0;
}

.slide-notification-leave-to {
    transform: translateX(100%);
    opacity: 0;
}

.slide-notification-move {
    transition: transform 0.3s ease;
}

.notification-time {
    font-size: 12px;
    color: #999;
    margin-top: 8px;
    text-align: right;
}

.notification-description {
    color: white !important;
    cursor: pointer;
    position: relative;
    padding-right: 24px;
}

.expand-icon {
    position: absolute;
    right: 0;
    top: 50%;
    transform: translateY(-50%);
}

.notification-title h2 {
    color: white !important;
    font-size: 16px;
    margin: 0 0 8px 0;
}
</style>