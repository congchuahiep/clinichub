import { useEffect, useState } from "react";
import { Alert, Badge, Card, Container, Image, Spinner, Stack, Modal, Form, Button } from "react-bootstrap";
import cookie from "react-cookies";
import { useNavigate, useParams } from "react-router-dom";
import { authApis, endpoints } from "../configs/APIs";
import { MyUserContext } from "../configs/MyContexts";
import { SLOT_LABELS, STATUS_MAP } from "../utils/AppointmentUtils";
import { useAuth } from "../configs/AuthProvider";


const AppointmentDetail = () => {

  const { user } = useAuth();

  const { id } = useParams();
  const navigate = useNavigate();

  const [appointment, setAppointment] = useState(null);
  const [showDiagnosisModal, setShowDiagnosisModal] = useState(false);
  const [diagnosis, setDiagnosis] = useState({
    diseaseId: "",
    diagnosis: "",
    prescriptions: "",
    testResults: "",
    notes: ""
  });

  const [diagLoading, setDiagLoading] = useState(false);
  const [diagError, setDiagError] = useState("");
  const [diagSuccess, setDiagSuccess] = useState(false);

  const [loading, setLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState("");

  useEffect(() => {
    if (!cookie.load("token")) {
      navigate("/login");
      return;
    }
    const loadDetail = async () => {
      setLoading(true);
      setErrorMessage("");
      try {
        const res = await authApis().get(endpoints["appointment-detail"](id));
        setAppointment(res.data);
      } catch (e) {
        setErrorMessage("Không thể tải chi tiết lịch khám!");
      } finally {
        setLoading(false);
      }
    };
    loadDetail();
  }, [id, navigate]);

  // Hàm submit chẩn đoán
  const handleDiagnosisSubmit = async (e) => {
    e.preventDefault();
    setDiagLoading(true);
    setDiagError("");
    setDiagSuccess(false);
    try {
      await authApis().post(endpoints["appointment-diagnosis"](id), diagnosis);
      setDiagSuccess(true);
      setShowDiagnosisModal(false);
      // Reload lại chi tiết lịch hẹn để hiển thị medicalRecord mới
      const res = await authApis().get(endpoints["appointment-detail"](id));
      setAppointment(res.data);
    } catch (err) {
      setDiagError("Ghi chẩn đoán thất bại!");
    } finally {
      setDiagLoading(false);
    }
  };

  if (loading) {
    return (
      <Container className="py-5 text-center">
        <Spinner animation="border" variant="primary" />
      </Container>
    );
  }

  if (errorMessage) {
    return (
      <Container className="py-5">
        <Alert variant="danger">{errorMessage}</Alert>
      </Container>
    );
  }

  if (!appointment) return null;

  const { doctor, patient, medicalRecord } = appointment;

  return (
    <Container className="py-4" style={{ maxWidth: 700 }}>
      <h3 className="mb-4">Chi tiết lịch khám</h3>
      <Card>
        <Card.Header>
          <Stack direction="horizontal" gap={4}>
            <div>
              <i className="bi bi-calendar-week"></i>
              {" "}Ngày khám: {appointment.appointmentDate}
            </div>
            <div>
              <i className="bi bi-clock"></i>
              {" "}Giờ khám: {SLOT_LABELS[appointment.timeSlot - 1] || appointment.timeSlot}
            </div>
            <Badge className="ms-auto" bg={STATUS_MAP[appointment.status]?.variant || "secondary"}>
              {STATUS_MAP[appointment.status]?.label || appointment.status}
            </Badge>
          </Stack>
        </Card.Header>
        <Card.Body>
          {user && user?.userRole == "PATIENT" ?
            <Alert variant="success" className="h-100">
              <i className="bi bi-clipboard2-pulse-fill"></i> <b>Bác sĩ</b>
              <Stack gap={3} className="mt-2" direction="horizontal">
                <Image
                  width={64}
                  height={64}
                  src={doctor.avatar || "/no-avatar.jpg"}
                  style={{ objectFit: "cover" }}
                  roundedCircle
                />
                <div>
                  <b>BS. {doctor.lastName} {doctor.firstName}</b>
                  <div>
                    <i className="bi bi-envelope"></i> {doctor.email}
                  </div>
                  <div>
                    <i className="bi bi-telephone"></i> {doctor.phone}
                  </div>
                </div>
              </Stack>
            </Alert>
            :
            <Alert variant="warning" className="h-100">
              <i className="bi bi-person-fill"></i> <b>Bệnh nhân</b>
              <Stack gap={3} className="mt-2" direction="horizontal">
                <Image
                  width={64}
                  height={64}
                  src={patient.avatar || "/no-avatar.jpg"}
                  style={{ objectFit: "cover" }}
                  roundedCircle
                />
                <div>
                  <b>{patient.lastName} {patient.firstName}</b>
                  <div>
                    <i className="bi bi-envelope"></i> {patient.email}
                  </div>
                  <div>
                    <i className="bi bi-telephone"></i> {patient.phone}
                  </div>
                </div>
              </Stack>
            </Alert>
          }
          <hr />
          <div className="d-flex justify-content-between">
            <div>
              <b>Ngày tạo lịch hẹn: </b>
              {appointment.createdAt}
            </div>

            {/* FORM CHẨN ĐOÁN */}
            {/* Chỉ hiển thị nút nếu là bác sĩ, chưa có medicalRecord và status là scheduled */}
            {user && user?.userRole === "DOCTOR" && !medicalRecord && appointment.status === "scheduled" && (
              <>
                <Button variant="primary" onClick={() => setShowDiagnosisModal(true)}>
                  Ghi chẩn đoán
                </Button>
                <Modal show={showDiagnosisModal} onHide={() => setShowDiagnosisModal(false)} centered>
                  <Modal.Header closeButton>
                    <Modal.Title>Ghi chẩn đoán</Modal.Title>
                  </Modal.Header>
                  <Modal.Body>
                    <Form onSubmit={handleDiagnosisSubmit}>
                      <Form.Group className="mb-3">
                        <Form.Label>Mã bệnh (diseaseId)</Form.Label>
                        <Form.Control
                          type="number"
                          required
                          value={diagnosis.diseaseId}
                          onChange={e => setDiagnosis(d => ({ ...d, diseaseId: e.target.value }))}
                        />
                      </Form.Group>
                      <Form.Group className="mb-3">
                        <Form.Label>Chẩn đoán</Form.Label>
                        <Form.Control
                          required
                          value={diagnosis.diagnosis}
                          onChange={e => setDiagnosis(d => ({ ...d, diagnosis: e.target.value }))}
                        />
                      </Form.Group>
                      <Form.Group className="mb-3">
                        <Form.Label>Kê đơn</Form.Label>
                        <Form.Control
                          value={diagnosis.prescriptions}
                          onChange={e => setDiagnosis(d => ({ ...d, prescriptions: e.target.value }))}
                        />
                      </Form.Group>
                      <Form.Group className="mb-3">
                        <Form.Label>Kết quả xét nghiệm</Form.Label>
                        <Form.Control
                          value={diagnosis.testResults}
                          onChange={e => setDiagnosis(d => ({ ...d, testResults: e.target.value }))}
                        />
                      </Form.Group>
                      <Form.Group className="mb-3">
                        <Form.Label>Ghi chú</Form.Label>
                        <Form.Control
                          as="textarea"
                          rows={2}
                          value={diagnosis.notes}
                          onChange={e => setDiagnosis(d => ({ ...d, notes: e.target.value }))}
                        />
                      </Form.Group>
                      {diagError && <Alert variant="danger">{diagError}</Alert>}
                      <div className="d-flex justify-content-end gap-2">
                        <Button variant="secondary" onClick={() => setShowDiagnosisModal(false)}>
                          Huỷ
                        </Button>
                        <Button type="submit" variant="primary" disabled={diagLoading}>
                          {diagLoading ? "Đang lưu..." : "Lưu chẩn đoán"}
                        </Button>
                      </div>
                    </Form>
                  </Modal.Body>
                </Modal>
              </>
            )}
          </div>
          {appointment.note && (
            <>
              <div>
                <b>Ghi chú:</b> {appointment.note}
              </div>

            </>


          )}
          <hr />

          <h5>Hồ sơ khám bệnh</h5>
          {medicalRecord ? (
            <Card className="mb-2">
              <Card.Body>
                <div><b>Bệnh:</b> {medicalRecord.diseaseName}</div>
                <div><b>Chẩn đoán:</b> {medicalRecord.diagnosis}</div>
                <div><b>Ngày chẩn đoán:</b> {medicalRecord.diagnosisDate ? new Date(medicalRecord.diagnosisDate).toLocaleDateString() : ""}</div>
                <div><b>Kê đơn:</b> {medicalRecord.prescriptions}</div>
                <div><b>Kết quả xét nghiệm:</b> {medicalRecord.testResults || "Không có"}</div>
                <div><b>Ghi chú:</b> {medicalRecord.notes}</div>
              </Card.Body>
            </Card>
          ) : (
            <Alert variant="secondary">Chưa có hồ sơ khám bệnh cho lịch hẹn này.</Alert>
          )}
        </Card.Body>
      </Card>
    </Container>
  );
};

export default AppointmentDetail;