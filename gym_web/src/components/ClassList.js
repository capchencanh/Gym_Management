import React, { useEffect, useState, useContext } from "react";
import Apis from "../configs/Apis";
import { MyUserContext } from "../configs/Contexts";

const ClassList = () => {
  const [classes, setClasses] = useState([]);
  const user = useContext(MyUserContext);

  useEffect(() => {
    const loadData = async () => {
      try {
       
        const [classRes, enrolledRes] = await Promise.all([
          Apis.get("/api/classes"),
          user?.id ? Apis.get(`/api/enrollments/user/${user.id}`) : Promise.resolve({ data: [] })
        ]);

        const enrolledClassIds = enrolledRes.data.map(e => e.classId);

        
        setClasses(classRes.data.map(c => ({
          ...c,
          enrolled: enrolledClassIds.includes(c.id)
        })));
      } catch (err) {
        console.error("Lỗi load data:", err);
      }
    };

    if (user) {
      loadData();
    }
  }, [user]);

  const enrollClass = async (classId) => {
    try {
      await Apis.post("/api/enrollments", {
        userId: user?.id,
        classId: classId,
      });
      alert("Tham gia lớp thành công!");

     
      setClasses((prev) =>
        prev.map((c) =>
          c.id === classId ? { ...c, enrolled: true } : c
        )
      );
    } catch (err) {
      console.error("Lỗi khi tham gia:", err);
      alert("Không thể tham gia lớp!");
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
          <div key={c.id} className="col-md-4 mb-3">
            <div className="card shadow-sm">
              <div className="card-body">
                <h5 className="card-title">{c.name}</h5>
                <p className="card-text">{c.description}</p>
                <p><strong>Lịch:</strong> {c.schedule}</p>
                <p><strong>PT:</strong> {c.trainerName}</p>
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
