import { 
  ref, 
  push, 
  onValue, 
  off, 
  query, 
  orderByChild,
  equalTo,
  update,
  set,
  serverTimestamp 
} from 'firebase/database';
import { database } from '../configs/firebase';

export class ChatService {
  constructor() {
    this.listeners = new Map();
  }

  // Tạo chat room ID giữa 2 user
  createChatRoomId(userId1, userId2) {
    // Sắp xếp để đảm bảo chat room ID luôn giống nhau
    const sortedIds = [userId1, userId2].sort();
    return `${sortedIds[0]}_${sortedIds[1]}`;
  }

                                // Gửi tin nhắn
               async sendMessage(senderId, receiverId, message) {
                 const chatRoomId = this.createChatRoomId(senderId, receiverId);
                 
                 // Tạo chat room trước
                 await this.createOrUpdateChatRoom(senderId, receiverId, message);
                 
                 const chatRef = ref(database, `chats/${chatRoomId}`);
                 
                 const newMessage = {
                   senderId,
                   receiverId,
                   message,
                   timestamp: serverTimestamp(),
                   read: false,
                   messageId: Date.now().toString() // Unique ID
                 };
                 
                 try {
                   const messageRef = push(chatRef, newMessage);
                   return { success: true, messageId: messageRef.key };
                 } catch (error) {
                   console.error('Error sending message:', error);
                   throw error;
                 }
               }

  // Lắng nghe tin nhắn real-time
  listenToConversation(userId1, userId2, callback) {
    const chatRoomId = this.createChatRoomId(userId1, userId2);
    const chatRef = ref(database, `chats/${chatRoomId}`);

    const listener = onValue(chatRef, (snapshot) => {
      const messages = [];
      if (snapshot.exists()) {
        snapshot.forEach((childSnapshot) => {
          const message = childSnapshot.val();
          const messageId = childSnapshot.key;
          messages.push({
            id: messageId,
            ...message
          });
        });
        
        // Sắp xếp theo timestamp
        messages.sort((a, b) => {
          if (a.timestamp && b.timestamp) {
            return a.timestamp - b.timestamp;
          }
          return 0;
        });
      }
      
      callback(messages);
    });

    // Lưu listener để cleanup
    const key = `${userId1}-${userId2}`;
    this.listeners.set(key, { ref: chatRef, listener });
    
    return listener;
  }

  // Dừng lắng nghe
  stopListening(userId1, userId2) {
    const key = `${userId1}-${userId2}`;
    const listenerInfo = this.listeners.get(key);
    if (listenerInfo) {
      off(listenerInfo.ref, listenerInfo.listener);
      this.listeners.delete(key);
    }
  }

  // Đánh dấu tin nhắn đã đọc
  async markAsRead(chatRoomId, messageId) {
    try {
      const messageRef = ref(database, `chats/${chatRoomId}/${messageId}`);
      await update(messageRef, { read: true });
    } catch (error) {
      console.error('Error marking message as read:', error);
    }
  }

  // Lấy danh sách chat rooms của user
  listenToUserChats(userId, callback) {
    const userChatsRef = ref(database, 'userChats');
    const userChatsQuery = query(userChatsRef, orderByChild('userId'), equalTo(userId));

    const listener = onValue(userChatsQuery, (snapshot) => {
      const chatRooms = [];
      if (snapshot.exists()) {
        snapshot.forEach((childSnapshot) => {
          chatRooms.push(childSnapshot.val());
        });
      }
      callback(chatRooms);
    });

    return listener;
  }

  // Tạo hoặc cập nhật chat room
  async createOrUpdateChatRoom(userId1, userId2, lastMessage = null) {
    const chatRoomId = this.createChatRoomId(userId1, userId2);
    
    const chatRoomRef = ref(database, `chatRooms/${chatRoomId}`);
    
    const chatRoomData = {
      chatRoomId,
      users: {
        [userId1]: true,
        [userId2]: true
      },
      lastMessage: lastMessage || null,
      lastMessageTime: lastMessage ? serverTimestamp() : null,
      createdAt: serverTimestamp(),
      updatedAt: serverTimestamp()
    };

    try {
      await set(chatRoomRef, chatRoomData);
      return chatRoomId;
    } catch (error) {
      console.error('Error creating chat room:', error);
      throw error;
    }
  }

  // Cleanup tất cả listeners
  cleanup() {
    this.listeners.forEach((listenerInfo) => {
      off(listenerInfo.ref, listenerInfo.listener);
    });
    this.listeners.clear();
  }
}

export default ChatService;
