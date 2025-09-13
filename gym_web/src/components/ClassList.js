import React, { useEffect, useState, useContext } from "react";
import Apis from "../configs/Apis";
import { MyUserContext } from "../configs/Contexts";

const ClassList = () => {
  const [classes, setClasses] = useState([]);
  const user = useContext(MyUserContext);

  useEffect(() => {
    const loadData = async () => {
      try {
        const userId = user?.id || user?.user_id;
        const [classRes, enrolledRes] = await Promise.all([
          Apis.get("/api/classes"),
          userId ? Apis.get(`/api/enrollments/user/${userId}`) : Promise.resolve({ data: [] })
        ]);

        const enrolledClassIds = (enrolledRes.data || []).map(e => e.classId || e.class_id);

        setClasses((classRes.data || []).map(c => ({
          ...c,
          enrolled: enrolledClassIds.includes(c.id || c.class_id)
        })));
      } catch (err) {
      }
    };

    if (user) {
      loadData();
    }
  }, [user]);

  const enrollClass = async (classId) => {
    try {
      const userId = user?.id || user?.user_id;
      const payload = { userId: userId, classId: classId };
      await Apis.post("/api/enrollments", payload);
      const enrolledRes = await Apis.get(`/api/enrollments/user/${userId}`);
      const enrolledIds = (enrolledRes.data || []).map(e => e.classId || e.class_id);
      setClasses((prev) => prev.map(c => ({
        ...c,
        enrolled: enrolledIds.includes(c.id || c.class_id)
      })));
      alert("Tham gia lớp thành công!");
    } catch (err) {
      if (err.response && err.response.status === 409) {
        alert("Bạn đã tham gia lớp này rồi.");
        setClasses((prev) => prev.map((c) => ((c.id || c.class_id) === classId ? { ...c, enrolled: true } : c)));
      } else if (err.response && err.response.status === 200) {
        alert(err.response.data?.message || "Không thể tham gia lớp.");
        setClasses((prev) => prev.map((c) => ((c.id || c.class_id) === classId ? { ...c, enrolled: true } : c)));
      } else {
        alert("Không thể tham gia lớp!");
      }
    }
  };

  if (!user) {
    return <p className="text-center mt-4">Vui lòng đăng nhập để xem và tham gia lớp.</p>;
  }

  return (
    <div className="container mt-4">
      <h2>Danh sách lớp tập</h2>
      <div className="row">
        {classes.map((c) => (
          <div key={c.id || c.class_id} className="col-md-4 mb-3">
            <div className="card shadow-sm">
              <div className="card-body">
                <h5 className="card-title">{c.name}</h5>
                <p className="card-text">{c.description}</p>
                <p><strong>Lịch:</strong> {c.schedule}</p>
                <p><strong>PT:</strong> {c.trainerName || 'Chưa phân công'}</p>
                <p><strong>Giá:</strong> {c.price} VND</p>
                <button
                  className="btn btn-primary"
                  onClick={() => enrollClass(c.id)}
                  disabled={c.enrolled}
                >
                  {c.enrolled ? "Đã tham gia" : "Tham gia"}
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default ClassList;
