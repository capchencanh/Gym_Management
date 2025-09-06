import React, { useEffect, useRef, useState, useContext } from "react";
import Apis, { endpoints } from "../configs/Apis";
import { MyUserContext } from "../configs/Contexts";

const AIChatBox = () => {
    const user = useContext(MyUserContext);
    const [open, setOpen] = useState(false);
    const [messages, setMessages] = useState([
        { id: "welcome", role: "assistant", text: "Xin chào! Mình là trợ lý AI về tập luyện và dinh dưỡng. Bạn muốn hỏi gì?" }
    ]);
    const [input, setInput] = useState("");
    const [loading, setLoading] = useState(false);
    const listRef = useRef(null);

    useEffect(() => {
        if (listRef.current) {
            listRef.current.scrollTop = listRef.current.scrollHeight;
        }
    }, [messages, open]);

    const sendMessage = async (e) => {
        e.preventDefault();
        const content = input.trim();
        if (!content) return;
        const userMsg = { id: Date.now().toString(), role: "user", text: content };
        setMessages((prev) => [...prev, userMsg]);
        setInput("");
        setLoading(true);
        try {
            const res = await Apis.post(endpoints["ai-chat"], {
                question: content,
                userId: user?.id || null,
                userProfile: user ? {
                    name: user.name,
                    gender: user.gender,
                    height: user.height,
                    weight: user.weight,
                    birthdate: user.birthdate,
                    fitness_goal: user.fitness_goal
                } : null
            });
            const aiText = res.data?.answer || "Xin lỗi, hiện tại mình chưa có câu trả lời.";
            setMessages((prev) => [...prev, { id: `${userMsg.id}-ai`, role: "assistant", text: aiText }]);
        } catch (err) {
            setMessages((prev) => [...prev, { id: `${userMsg.id}-err`, role: "assistant", text: "Có lỗi xảy ra, vui lòng thử lại sau." }]);
        } finally {
            setLoading(false);
        }
    };

    return (
        <>
            <button
                className="btn btn-primary"
                style={{ position: "fixed", right: "24px", bottom: "24px", zIndex: 1050, borderRadius: "999px" }}
                onClick={() => setOpen(!open)}
            >
                {open ? "Đóng chat" : "Hỏi AI"}
            </button>

            {open && (
                <div
                    className="card shadow"
                    style={{ position: "fixed", right: "24px", bottom: "88px", width: "360px", maxHeight: "60vh", display: "flex", flexDirection: "column", zIndex: 1050 }}
                >
                    <div className="card-header" style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                        <span>Trợ lý AI</span>
                        <button className="btn btn-secondary btn-sm" onClick={() => setOpen(false)}>Đóng</button>
                    </div>
                    <div className="card-body" style={{ overflowY: "auto" }} ref={listRef}>
                        {messages.map((m) => (
                            <div key={m.id} style={{ marginBottom: "0.75rem" }}>
                                <div style={{ fontSize: "0.75rem", color: "#666" }}>{m.role === "user" ? "Bạn" : "AI"}</div>
                                <div className="p-2" style={{ background: m.role === "user" ? "#ecf0f1" : "#f8f9fa", border: "1px solid #e5e5e5", borderRadius: "8px" }}>
                                    {m.text}
                                </div>
                            </div>
                        ))}
                        {loading && <div className="text-muted">AI đang trả lời...</div>}
                    </div>
                    <form onSubmit={sendMessage} className="p-2" style={{ borderTop: "1px solid #e5e5e5" }}>
                        <div className="input-group">
                            <input
                                className="form-control"
                                placeholder="Đặt câu hỏi về bài tập, dinh dưỡng..."
                                value={input}
                                onChange={(e) => setInput(e.target.value)}
                            />
                            <button className="btn btn-primary" type="submit" disabled={loading}>Gửi</button>
                        </div>
                    </form>
                </div>
            )}
        </>
    );
};

export default AIChatBox;


