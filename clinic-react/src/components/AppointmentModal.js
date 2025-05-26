import { useState, useEffect } from "react";
import { Modal, Button, Form, Alert, Toast, ToastContainer, ButtonGroup, ToggleButton } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import cookie from "react-cookies";
import { authApis, endpoints } from "../configs/APIs";
import { SLOT_LABELS } from "../utils/AppointmentUtils";

const AppointmentModal = ({ show, onHide, doctorName, doctorId }) => {

  const today = new Date();
  const tomorrow = new Date(today);
  tomorrow.setDate(today.getDate() + 1);

  const oneMonthLater = new Date(today);
  oneMonthLater.setDate(today.getDate() + 30); // đơn giản, không cần check tháng

  // Định dạng YYYY-MM-DD
  const formatDate = (date) => date.toISOString().split('T')[0];
  const minDate = formatDate(tomorrow);
  const maxDate = formatDate(oneMonthLater);

  const navigate = useNavigate();

  const [appointment, setAppointment] = useState({
    doctorName: doctorName || "",
    doctorId: doctorId || "",
    appointmentDate: "",
    timeSlot: "",
    note: ""
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const [showToast, setShowToast] = useState(false);

  const [selectedDate, setSelectedDate] = useState(today);
  const [takenSlots, setTakenSlots] = useState([]);

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

  const loadTakenSlots = async () => {
    if (!doctorId || !selectedDate) {
      return;
    }

    const params = {
      doctorId: doctorId,
      date: selectedDate
    }

    await authApis().get(endpoints["check-taken-slots"], { params })
      .then((res) => {
        console.log(res);
        const slots = res.data.map(s => Number(s.replace("SLOT_", "")));
        setTakenSlots(slots);
      })
      .catch((e) => {
        console.log(e);
      })
  }

  useEffect(() => {
    loadTakenSlots();
  }, [doctorId, selectedDate]);

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

      console.log(formData.get("doctorId"));

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
                min={minDate}
                max={maxDate}
                onChange={e => {
                  handleChange("appointmentDate", e.target.value)
                  setSelectedDate(e.target.value)
                }}
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Ca khám</Form.Label>
              <ButtonGroup className="d-flex flex-wrap gap-2">
                {SLOT_LABELS.map((label, idx) => (
                  <ToggleButton
                    key={idx + 1}
                    id={`slot-${idx + 1}`}
                    type="radio"
                    variant={takenSlots.includes(idx + 1) ? "outline-secondary" : "outline-primary"}
                    name="timeSlot"
                    value={idx + 1}
                    checked={appointment.timeSlot === (idx + 1).toString()}
                    onChange={e => handleChange("timeSlot", e.currentTarget.value)}
                    style={{ borderRadius: 5 }}
                    disabled={takenSlots.includes(idx + 1)}
                  >
                    {label}
                  </ToggleButton>
                ))}
                <Form.Text id="passwordHelpBlock" muted>
                  Các ca khám bị mờ nghĩa rằng bạn hoặc bác sĩ đang bận lịch khám khác vào ca này
                </Form.Text>
              </ButtonGroup>
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
      <ToastContainer position="bottom-end" className="p-3" style={{ position: "fixed" }}>
        <Toast
          bg=""
          onClose={() => setShowToast(false)}
          show={showToast}
          delay={3000}
          autohide
        >
          <Toast.Header>
            <strong className="me-auto">Thông báo</strong>
          </Toast.Header>
          <Toast.Body>
            ✅ Đặt lịch thành công!
          </Toast.Body>
        </Toast>
      </ToastContainer>
    </>
  );
};

export default AppointmentModal;