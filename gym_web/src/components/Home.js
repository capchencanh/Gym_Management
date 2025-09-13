import React from 'react';
import { Link } from 'react-router-dom';
import { useUser } from '../configs/UserProvider';
import './Home.css';

/**
 * Home component
 */
const Home = () => {
  const user = useUser();

  const getWelcomeMessage = () => {
    if (!user) {
      return "Chào mừng đến với Gym Management";
    }
    return `Chào mừng ${user.name}!`;
  };
  const features = [
    {
      icon: "fas fa-dumbbell",
      title: "Quản lý bài tập",
      description: "Theo dõi và ghi log các buổi tập luyện của bạn",
      link: "/workout-log"
    },
    {
      icon: "fas fa-apple-alt",
      title: "Chế độ dinh dưỡng",
      description: "Quản lý chế độ ăn và gợi ý thực đơn phù hợp",
      link: "/nutrition"
    },
    {
      icon: "fas fa-users",
      title: "Lớp tập luyện",
      description: "Đăng ký và tham gia các lớp tập với PT chuyên nghiệp",
      link: "/classes"
    },
    {
      icon: "fas fa-tags",
      title: "Gói tập luyện",
      description: "Xem và đăng ký các gói tập phù hợp với nhu cầu",
      link: "/package"
    }
  ];

  return (
    <div className="page-container">
          <div className="page-header">
            <h1 className="page-title">
              <i className="fas fa-dumbbell me-2"></i>
              {getWelcomeMessage()}
            </h1>
            <p className="page-subtitle">
              Hệ thống quản lý gym hiện đại với giao diện thân thiện
            </p>
          </div>

          <div className="section">
            <div className="stats-grid">
              {features.map((feature, index) => (
                <div key={index} className="card feature-card">
                  <div className="card-body text-center">
                    <div className="feature-icon">
                      <i className={`${feature.icon} fa-3x`}></i>
                    </div>
                    <h4 className="feature-title">{feature.title}</h4>
                    <p className="feature-description">{feature.description}</p>
                    {feature.link && (
                      <Link to={feature.link} className="btn btn-primary">
                        Khám phá
                      </Link>
                    )}
                  </div>
                </div>
              ))}
            </div>
          </div>

          <div className="section">
            <div className="text-center">
              <div className="action-buttons">
                <Link to="/register" className="btn btn-primary btn-lg me-3">
                  <i className="fas fa-rocket me-2"></i>
                  Bắt đầu ngay
                </Link>
                <Link to="/about" className="btn btn-secondary btn-lg">
                  <i className="fas fa-info-circle me-2"></i>
                  Tìm hiểu thêm
                </Link>
              </div>
            </div>
          </div>
    </div>
  );
};

export default Home;
