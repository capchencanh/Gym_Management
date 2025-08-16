import React from 'react';
import { Container, Row, Col, Card, Button } from 'react-bootstrap';
import { useUser } from '../configs/UserProvider';

const Home = () => {
  const user = useUser();

  const getWelcomeMessage = () => {
    if (!user) {
      return "Chào mừng đến với Gym Management";
    }
    return `Chào mừng ${user.name}!`;
  };

  return (
    <Container className="mt-4">
      <Row>
        <Col>
          <h1 className="text-center mb-4">{getWelcomeMessage()}</h1>
          <p className="text-center text-muted mb-5">
            Hệ thống quản lý gym hiện đại
          </p>
        </Col>
      </Row>

      <Row className="mb-4">
        <Col md={4} className="mb-3">
          <Card className="h-100 text-center">
            <Card.Body>
              <i className="fas fa-dumbbell fa-3x text-primary mb-3"></i>
              <Card.Title>Quản lý bài tập</Card.Title>
              <Card.Text>
                Theo dõi và ghi log các buổi tập luyện của bạn
              </Card.Text>
            </Card.Body>
          </Card>
        </Col>

        <Col md={4} className="mb-3">
          <Card className="h-100 text-center">
            <Card.Body>
              <i className="fas fa-apple-alt fa-3x text-success mb-3"></i>
              <Card.Title>Chế độ dinh dưỡng</Card.Title>
              <Card.Text>
                Quản lý chế độ ăn và gợi ý thực đơn phù hợp
              </Card.Text>
            </Card.Body>
          </Card>
        </Col>

        <Col md={4} className="mb-3">
          <Card className="h-100 text-center">
            <Card.Body>
              <i className="fas fa-users fa-3x text-info mb-3"></i>
              <Card.Title>Lớp tập luyện</Card.Title>
              <Card.Text>
                Đăng ký và tham gia các lớp tập với PT chuyên nghiệp
              </Card.Text>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      <Row>
        <Col className="text-center">
          <Button variant="primary" size="lg" className="me-3">
            Bắt đầu ngay
          </Button>
          <Button variant="outline-secondary" size="lg">
            Tìm hiểu thêm
          </Button>
        </Col>
      </Row>
    </Container>
  );
};

export default Home;
