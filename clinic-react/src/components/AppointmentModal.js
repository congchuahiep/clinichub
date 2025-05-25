import { useState, useEffect } from "react";
import { Modal, Button, Form, Alert, Toast, ToastContainer } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import cookie from "react-cookies";
import { authApis, endpoints } from "../configs/APIs";

const SLOT_LABELS = [
  "07:30 - 08:00", "08:00 - 08:30", "08:30 - 09:00", "09:00 - 09:30",
  "09:30 - 10:00", "10:00 - 10:30", "10:30 - 11:00", "13:00 - 13:30",
  "13:30 - 14:00", "14:00 - 14:30", "14:30 - 15:00", "15:00 - 15:30",
  "15:30 - 16:00", "16:00 - 16:30", "16:30 - 17:00", "17:00 - 17:30"
];

const AppointmentModal = ({ show, onHide, doctorName, doctorId }) => {

  const [appointment, setAppointment] = useState({
    doctorName: doctorName || "",
    doctorId: doctorId || "",
    appointmentDate: "",
    timeSlot: "",
    note: ""
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const [showToast, setShowToast] = useState(false);

  // Reset form khi doctorId hoặc show thay đổi
  useEffect(() => {
    setAppointment({
      doctorName: doctorName || "",
      doctorId: doctorId || "",
      appointmentDate: "",
      timeSlot: "",
      note: ""
    });
    setError("");
  }, [doctorId, show]);

  const handleChange = (field, value) => {
    setAppointment(prev => ({ ...prev, [field]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!cookie.load("token")) {
      navigate("/login");
      return;
    }
    setLoading(true);
    setError("");
    try {
      const formData = new FormData();
      formData.append("doctorId", appointment.doctorId);
      formData.append("appointmentDate", appointment.appointmentDate);
      formData.append("timeSlot", appointment.timeSlot);
      formData.append("note", appointment.note);

      await authApis().post(endpoints.appointments, formData);
      onHide();
      // Hiện thông báo thành công
      setShowToast(true);

    } catch (err) {
      console.log(err);
      setError("Đặt lịch thất bại!");
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <Modal show={show} onHide={onHide} centered>
        <Modal.Header closeButton>
          <Modal.Title>Đặt lịch khám</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form onSubmit={handleSubmit}>
            <Form.Group className="mb-3">
              <Form.Label>Bác sĩ</Form.Label>
              <Form.Control value={appointment.doctorName} disabled />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Ngày khám</Form.Label>
              <Form.Control
                type="date"
                required
                value={appointment.appointmentDate}
                onChange={e => handleChange("appointmentDate", e.target.value)}
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Ca khám</Form.Label>
              <Form.Select
                required
                value={appointment.timeSlot}
                onChange={e => handleChange("timeSlot", e.target.value)}
              >
                <option value="">Chọn ca khám</option>
                {SLOT_LABELS.map((label, idx) => (
                  <option key={idx + 1} value={idx + 1}>
                    {label}
                  </option>
                ))}
              </Form.Select>
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Ghi chú</Form.Label>
              <Form.Control
                as="textarea"
                rows={2}
                value={appointment.note}
                onChange={e => handleChange("note", e.target.value)}
              />
            </Form.Group>
            {error && <Alert variant="danger">{error}</Alert>}
            <div className="d-flex justify-content-end gap-2">
              <Button variant="secondary" onClick={onHide}>
                Huỷ
              </Button>
              <Button type="submit" variant="primary" disabled={loading}>
                {loading ? "Đang đặt..." : "Xác nhận đặt lịch"}
              </Button>
            </div>
          </Form>
        </Modal.Body>
      </Modal>
      <ToastContainer position="top-end" className="p-3">
        <Toast
          bg="success"
          onClose={() => setShowToast(false)}
          show={showToast}
          delay={3000}
          autohide
        >
          <Toast.Header>
            <strong className="me-auto">Thông báo</strong>
          </Toast.Header>
          <Toast.Body className="text-white">
            Đặt lịch thành công!
          </Toast.Body>
        </Toast>
      </ToastContainer>
    </>
  );
};

export default AppointmentModal;