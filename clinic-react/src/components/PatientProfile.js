import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { Container, Row, Col, Form, Image, Spinner, Alert } from "react-bootstrap";
import axios from "axios";

const PatientProfile = () => {
  const { patientId } = useParams();
  const [patientInfo, setPatientInfo] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (!patientId) {
      setError("Chưa có ID bệnh nhân");
      setLoading(false);
      return;
    }

    setLoading(true);
    axios
      .get(`/api/patient-profile/health-records/${patientId}`)
      .then((res) => {
        setPatientInfo(res.data);
        setLoading(false);
      })
      .catch((err) => {
        console.error(err);
        setError(
          err.response?.data?.error || "Không thể tải thông tin bệnh nhân"
        );
        setLoading(false);
      });
  }, [patientId]);

  if (loading) return <Spinner animation="border" />;

  if (error) return <Alert variant="danger">{error}</Alert>;

  if (!patientInfo) return <p>Không tìm thấy thông tin bệnh nhân</p>;

  return (
    <Container fluid style={{ padding: "20px" }}>
      <Row>
        <Col xs={12} md={3} className="bg-light rounded p-4 d-flex flex-column align-items-center">
          <Image
            src={patientInfo.avatar || "https://via.placeholder.com/100"}
            roundedCircle
            alt="avatar"
            style={{ marginBottom: "20px", width: "100px", height: "100px" }}
          />
          <h5 className="mb-4">{patientInfo.firstName} {patientInfo.lastName}</h5>
          <div className="text-start w-100">
            <p><strong>Giới tính:</strong> {patientInfo.gender === "male" ? "Nam" : patientInfo.gender === "female" ? "Nữ" : "Khác"}</p>
            <p><strong>Ngày sinh:</strong> {new Date(patientInfo.birthDate).toLocaleDateString()}</p>
            <p><strong>Email:</strong> {patientInfo.email}</p>
            <p><strong>Điện thoại:</strong> {patientInfo.phone}</p>
            <p><strong>Địa chỉ:</strong> {patientInfo.address || "Chưa cập nhật"}</p>
          </div>
        </Col>

        <Col xs={12} md={9} className="bg-light rounded p-4">
          <h4 className="mb-4">Hồ sơ sức khỏe</h4>
          <Form>
            <Form.Group controlId="medicalHistory" className="mb-4">
              <Form.Label>Tiểu sử bệnh lý</Form.Label>
              <Form.Control
                as="textarea"
                rows={2}
                value={patientInfo.medicalHistory || ""}
                readOnly
              />
            </Form.Group>

            <Form.Group controlId="allergies" className="mb-4">
              <Form.Label>Dị ứng</Form.Label>
              <Form.Control
                as="textarea"
                rows={2}
                value={patientInfo.allergies || ""}
                readOnly
              />
            </Form.Group>

            <Form.Group controlId="chronicConditions" className="mb-4">
              <Form.Label>Tình trạng mãn tính</Form.Label>
              <Form.Control
                as="textarea"
                rows={2}
                value={patientInfo.chronicConditions || ""}
                readOnly
              />
            </Form.Group>

            <Row>
              <Col md={4}>
                <Form.Group controlId="weight" className="mb-3">
                  <Form.Label>Cân nặng (kg)</Form.Label>
                  <Form.Control type="text" value={patientInfo.weight || ""} readOnly />
                </Form.Group>
              </Col>
              <Col md={4}>
                <Form.Group controlId="height" className="mb-3">
                  <Form.Label>Chiều cao (cm)</Form.Label>
                  <Form.Control type="text" value={patientInfo.height || ""} readOnly />
                </Form.Group>
              </Col>
              <Col md={4}>
                <Form.Group controlId="bloodPressure" className="mb-3">
                  <Form.Label>Huyết áp</Form.Label>
                  <Form.Control type="text" value={patientInfo.bloodPressure || ""} readOnly />
                </Form.Group>
              </Col>
              <Col md={4}>
                <Form.Group controlId="bloodSugar" className="mb-3">
                  <Form.Label>Đường huyết</Form.Label>
                  <Form.Control type="text" value={patientInfo.bloodSugar || ""} readOnly />
                </Form.Group>
              </Col>
            </Row>
          </Form>
        </Col>
      </Row>
    </Container>
  );
};

export default PatientProfile;
