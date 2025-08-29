import React, { useState, useEffect, useRef, useContext } from 'react';
import ChatService from '../services/chatService';
import './Chat.css';
import { MyUserContext } from '../configs/Contexts';

const Chat = ({ isOpen, onClose, receiverId, receiverName }) => {
    const [messages, setMessages] = useState([]);
    const [newMessage, setNewMessage] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const messagesEndRef = useRef(null);
    const [currentUserId, setCurrentUserId] = useState(null);
    const chatService = useRef(new ChatService());
    const userContext = useContext(MyUserContext);

    useEffect(() => {
        if (isOpen && receiverId) {
            const foundUser = userContext;
            if (foundUser) {
                const userId = foundUser.id || foundUser.user_id;
                if (userId) {
                    setCurrentUserId(userId);
                    chatService.current.listenToConversation(
                        userId,
                        receiverId,
                        (payload) => {
                            if (Array.isArray(payload)) {
                                setMessages(payload);
                                setError('');
                            } else if (payload && payload.message) {
                                setMessages([]);
                                setError(payload.message);
                            } else {
                                setMessages([]);
                                setError('Không thể tải tin nhắn.');
                            }
                        }
                    );
                } else {
                    setError('Không thể lấy ID người dùng. Vui lòng đăng nhập lại.');
                }
            } else {
                setError('Vui lòng đăng nhập để sử dụng chat.');
            }
        }

        
        return () => {
            if (currentUserId && receiverId) {
                chatService.current.stopListening(currentUserId, receiverId);
            }
        };
    }, [isOpen, receiverId, currentUserId, userContext]);

    useEffect(() => {
        scrollToBottom();
    }, [messages]);

    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
    };

   

        const sendMessage = async (e) => {
        e.preventDefault();
        if (!newMessage.trim() || !receiverId) return;

        try {
            setLoading(true);
            
            
            await chatService.current.sendMessage(
                currentUserId, 
                receiverId, 
                newMessage.trim()
            );
            
            setNewMessage('');
            setError('');
            
           
        } catch (error) {
            setError('Lỗi khi gửi tin nhắn: ' + error.message);
        } finally {
            setLoading(false);
        }
    };

    const formatTime = (timestamp) => {
        if (!timestamp) return '';
        const date = new Date(timestamp);
        return date.toLocaleTimeString('vi-VN', { 
            hour: '2-digit', 
            minute: '2-digit' 
        });
    };

    const formatDate = (timestamp) => {
        if (!timestamp) return '';
        const date = new Date(timestamp);
        const today = new Date();
        const yesterday = new Date(today);
        yesterday.setDate(yesterday.getDate() - 1);

        if (date.toDateString() === today.toDateString()) {
            return 'Hôm nay';
        } else if (date.toDateString() === yesterday.toDateString()) {
            return 'Hôm qua';
        } else {
            return date.toLocaleDateString('vi-VN');
        }
    };

    if (!isOpen) return null;

    return (
        <div className="chat-overlay" onClick={onClose}>
            <div className="chat-container" onClick={(e) => e.stopPropagation()}>
                <div className="chat-header">
                    <div className="chat-user-info">
                        <div className="user-avatar">
                            <i className="fas fa-user"></i>
                        </div>
                        <div className="user-details">
                            <h4>{receiverName || 'PT'}</h4>
                            <span className="user-status">Đang hoạt động</span>
                        </div>
                    </div>
                    <button className="close-button" onClick={onClose}>
                        <i className="fas fa-times"></i>
                    </button>
                </div>

                <div className="chat-messages">
                    {loading && messages.length === 0 ? (
                        <div className="loading-messages">
                            <i className="fas fa-spinner fa-spin"></i>
                            <span>Đang tải tin nhắn...</span>
                        </div>
                    ) : messages.length === 0 ? (
                        <div className="no-messages">
                            <i className="fas fa-comments"></i>
                            <span>Chưa có tin nhắn nào</span>
                            <p>Bắt đầu cuộc trò chuyện với {receiverName || 'PT'}!</p>
                        </div>
                    ) : (
                        messages.map((message, index) => {
                            
                            const isOwnMessage = message.senderId === currentUserId;
                            const showDate = index === 0 || 
                                formatDate(messages[index - 1]?.timestamp) !== formatDate(message.timestamp);

                            return (
                                <div key={message.messageId || index}>
                                    {showDate && (
                                        <div className="message-date">
                                            {formatDate(message.timestamp)}
                                        </div>
                                    )}
                                    <div className={`message ${isOwnMessage ? 'own' : 'other'}`}>
                                        <div className="message-content">
                                            <p>{message.message}</p>
                                            <span className="message-time">
                                                {formatTime(message.timestamp)}
                                            </span>
                                        </div>
                                    </div>
                                </div>
                            );
                        })
                    )}
                    <div ref={messagesEndRef} />
                </div>

                {error && (
                    <div className="chat-error">
                        <i className="fas fa-exclamation-triangle"></i>
                        <span>{error}</span>
                    </div>
                )}

                <form className="chat-input-form" onSubmit={sendMessage}>
                    <div className="chat-input-container">
                        <input
                            type="text"
                            value={newMessage}
                            onChange={(e) => setNewMessage(e.target.value)}
                            placeholder="Nhập tin nhắn..."
                            disabled={loading}
                            className="chat-input"
                        />
                        <button 
                            type="submit" 
                            disabled={loading || !newMessage.trim()}
                            className="send-button"
                        >
                            {loading ? (
                                <i className="fas fa-spinner fa-spin"></i>
                            ) : (
                                <i className="fas fa-paper-plane"></i>
                            )}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default Chat;
