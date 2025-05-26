import { Container, Row, Col, Card, Button } from "react-bootstrap";
import { useNavigate } from "react-router-dom";

const Home = () => {
  const navigate = useNavigate();

  return (
    <Container className="py-5">
      <Row className="my-5">
        <Col>
          <h1 className="text-center mb-3">Chào mừng đến với ClinicHub</h1>
          <p className="text-center fs-5 text-muted">
            Nền tảng đặt lịch khám bệnh trực tuyến hiện đại, nhanh chóng và tiện lợi cho bệnh nhân và bác sĩ.
          </p>
        </Col>
      </Row>

      <Row className="my-5">
        <Col md={4}>
          <Card className="h-100 shadow-sm">
            <Card.Body>
              <Card.Title>Đặt lịch khám dễ dàng</Card.Title>
              <Card.Text>
                Đặt lịch khám với bác sĩ chuyên khoa chỉ với vài thao tác. Xem lịch trống, chọn ca khám phù hợp và nhận xác nhận ngay lập tức.
              </Card.Text>
            </Card.Body>
          </Card>
        </Col>
        <Col md={4}>
          <Card className="h-100 shadow-sm">
            <Card.Body>
              <Card.Title>Quản lý hồ sơ sức khoẻ</Card.Title>
              <Card.Text>
                Theo dõi hồ sơ sức khoẻ, lịch sử khám bệnh, kết quả xét nghiệm và đơn thuốc mọi lúc, mọi nơi.
              </Card.Text>
            </Card.Body>
          </Card>
        </Col>
        <Col md={4}>
          <Card className="h-100 shadow-sm">
            <Card.Body>
              <Card.Title>Kết nối với đội ngũ bác sĩ uy tín</Card.Title>
              <Card.Text>
                Danh sách bác sĩ đa dạng, thông tin minh bạch, đánh giá thực tế từ bệnh nhân giúp bạn an tâm lựa chọn.
              </Card.Text>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default Home;