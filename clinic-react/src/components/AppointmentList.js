import { useEffect, useState } from "react";
import { Alert, Badge, Button, ButtonGroup, Card, Col, Container, Dropdown, DropdownButton, Form, Image, Modal, Placeholder, Row, Stack, Toast, ToastContainer, ToggleButton } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import { authApis, endpoints } from "../configs/APIs";
import { useAuth } from "../configs/AuthProvider";
import { SLOT_LABELS, STATUS_MAP } from "../utils/AppointmentUtils";
import Breadcrumbs from "./layouts/Breadcrumbs";


const AppointmentList = () => {
  const { user } = useAuth();


  const navigate = useNavigate();
  const [appointments, setAppointments] = useState([]);
  const [appointmentStatus, setAppointmentStatus] = useState("scheduled");
  const [selectedAppointment, setSelectedAppointment] = useState(null);

  const [loading, setLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState("");

  // Huỷ lịch hẹn
  const [showCancelModal, setShowCancelModal] = useState(false);
  const [cancelLoading, setCancelLoading] = useState(false);
  const [cancelError, setCancelError] = useState("");

  // Đổi lịch hẹn
  const [showRescheduleModal, setShowRescheduleModal] = useState(false);
  const [rescheduleLoading, setRescheduleLoading] = useState(false);
  const [rescheduleError, setRescheduleError] = useState("");
  const [rescheduleSuccess, setRescheduleSuccess] = useState(false);
  const [rescheduleDate, setRescheduleDate] = useState("");
  const [rescheduleSlot, setRescheduleSlot] = useState("");
  const [rescheduleTakenSlots, setRescheduleTakenSlots] = useState([]);
  const [rescheduleDoctorId, setRescheduleDoctorId] = useState(null);
  const [rescheduleAppointmentId, setRescheduleAppointmentId] = useState(null);

  useEffect(() => {
    const loadAppointments = async () => {
      setLoading(true);
      setErrorMessage("");
      try {
        const params = { status: appointmentStatus };
        const res = await authApis().get(endpoints.appointments, { params });
        console.log(res);
        setAppointments(res.data.results || res.data); // tuỳ API trả về
      } catch (e) {
        setErrorMessage("Không thể tải danh sách lịch khám!");
      } finally {
        setLoading(false);
      }
    };
    loadAppointments();
  }, [appointmentStatus]);


  const handleCancelAppointment = async () => {
    if (!selectedAppointment) return;
    setCancelLoading(true);
    setCancelError("");
    try {
      await authApis().post(endpoints["appointment-cancel"](selectedAppointment.id));
      setShowCancelModal(false);
      // Reload lại danh sách
      const params = { status: appointmentStatus };
      const res = await authApis().get(endpoints.appointments, { params });
      setAppointments(res.data.results || res.data);
    } catch (e) {
      console.log(e);
      setCancelError(e?.response?.data?.error || "Không thể đổi lịch hẹn.");
    } finally {
      setCancelLoading(false);
    }
  };

  // Hàm mở modal đổi lịch
  const handleOpenRescheduleModal = (appointment) => {
    setRescheduleDoctorId(appointment.doctor.id);
    setRescheduleAppointmentId(appointment.id);
    setRescheduleDate("");
    setRescheduleSlot("");
    setRescheduleError("");
    setShowRescheduleModal(true);
  };

  // Hàm fetch taken slots
  const fetchRescheduleTakenSlots = async (doctorId, date) => {
    if (!doctorId || !date) return;
    try {
      const params = { doctorId, date };
      const res = await authApis().get(endpoints["check-taken-slots"], { params });
      const slots = res.data.map(s => Number(s.replace("SLOT_", "")));
      setRescheduleTakenSlots(slots);
    } catch (e) {
      setRescheduleTakenSlots([]);
    }
  };

  // Khi ngày đổi lịch thay đổi, fetch lại taken slots
  useEffect(() => {
    if (showRescheduleModal && rescheduleDoctorId && rescheduleDate) {
      fetchRescheduleTakenSlots(rescheduleDoctorId, rescheduleDate);
    }
  }, [showRescheduleModal, rescheduleDoctorId, rescheduleDate]);

  // Hàm submit đổi lịch
  const handleReschedule = async (e) => {
    e.preventDefault();
    setRescheduleLoading(true);
    setRescheduleError("");
    try {

      const formData = new FormData();
      formData.append("newAppointmentDate", rescheduleDate);
      formData.append("newTimeSlot", rescheduleSlot);

      console.log(rescheduleDate)

      await authApis().post(endpoints["appointment-reschedule"](rescheduleAppointmentId), formData);

      setShowRescheduleModal(false);
      setRescheduleSuccess(true);
      // Reload lại danh sách
      const params = { status: appointmentStatus };
      const res = await authApis().get(endpoints.appointments, { params });
      setAppointments(res.data.results || res.data);
    } catch (e) {
      console.log(e);
      setRescheduleError(e?.response?.data?.error || "Không thể đổi lịch hẹn.");
    } finally {
      setRescheduleLoading(false);
    }
  };

  if (errorMessage) {
    return (
      <Container className="py-5">
        <Alert variant="danger">{errorMessage}</Alert>
      </Container>
    );
  }

  return (
    <Container className="py-3 mx-auto" style={{ maxWidth: 700 }}>
      <h3 className="mb-4">Lịch Khám Bệnh</h3>
      <Breadcrumbs />

      <Form className="mb-3">
        <div>
          {["scheduled", "completed", "cancelled", "rescheduled"].map(status => (
            <Form.Check
              inline
              key={status}
              label={
                {
                  scheduled: "Đã đặt",
                  completed: "Hoàn thành",
                  cancelled: "Đã huỷ",
                  rescheduled: "Đã dời lịch"
                }[status]
              }
              name="statusFilter"
              type="radio"
              id={`status-${status}`}
              value={status}
              checked={appointmentStatus === status}
              onChange={e => setAppointmentStatus(e.target.value)}
            />
          ))}
        </div>
      </Form>

      {appointments.length === 0 ? (
        <Alert variant="info">Bạn chưa có lịch khám nào.</Alert>
      ) : loading ?
        <Placeholder xs={6} />
        :
        (
          <Stack gap={3}>
            {appointments.map((appointment) => (
              <Card key={appointment.id}>
                <Card.Header>
                  <Stack direction="horizontal" gap={5}>

                    <div>
                      <i className="bi bi-calendar-week"> </i>
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
                  <Stack>
                    <Container className="p-0">
                      <Row>
                        {user.userRole == "PATIENT" ?
                          <Col>
                            <Alert variant={STATUS_MAP[appointment.status]?.variant || "secondary"} height={240}>
                              <i className="bi bi-clipboard2-pulse-fill"> </i>
                              <b>Bác sĩ</b>
                              <Stack gap={3} className="mt-2" direction="horizontal">
                                <Image
                                  width={64}
                                  height={64}
                                  src={appointment.doctor.avatar || "/no-avatar.jpg"}
                                  style={{ objectFit: "cover" }}
                                  roundedCircle
                                />
                                <div>
                                  <b>BS. {appointment.doctor.lastName} {appointment.doctor.firstName}</b>
                                  <div>
                                    <i className="bi bi-envelope"> </i>
                                    {appointment.doctor.email}
                                  </div>
                                  <div>
                                    <i className="bi bi-telephone"> </i>
                                    {appointment.doctor.phone}
                                  </div>
                                </div>
                              </Stack>
                            </Alert>
                          </Col>
                          :
                          <Col>
                            <Alert variant={STATUS_MAP[appointment.status]?.variant || "secondary"}>
                              <i className="bi bi-person-fill"></i>
                              <b>Bệnh nhân</b>
                              <Stack gap={3} className="mt-2" direction="horizontal">
                                <Image
                                  width={64}
                                  height={64}
                                  src={appointment.patient.avatar || "/no-avatar.jpg"}
                                  style={{ objectFit: "cover" }}
                                  roundedCircle
                                />
                                <div>
                                  <b>{appointment.patient.lastName} {appointment.patient.firstName}</b>
                                  <div>
                                    <i className="bi bi-envelope"> </i>
                                    {appointment.patient.email}
                                  </div>
                                  <div>
                                    <i className="bi bi-telephone"> </i>
                                    {appointment.patient.phone}
                                  </div>
                                </div>
                              </Stack>
                            </Alert>
                          </Col>
                        }
                      </Row>
                    </Container>

                    <Stack direction="horizontal" gap={2}>
                      <div>Ngày tạo lịch hẹn: {appointment.createdAt}</div>
                      <ButtonGroup className="ms-auto">
                        <Button
                          variant="outline-primary"
                          onClick={() => navigate(`/appointments/${appointment.id}`)}
                        >
                          Xem chi tiết
                        </Button>
                        {
                          STATUS_MAP[appointment.status] == STATUS_MAP.scheduled &&
                          <DropdownButton align="end" as={ButtonGroup} title="" id="bg-nested-dropdown">
                            { user.userRole == "PATIENT" &&
                              <Dropdown.Item
                                eventKey="1"
                                onClick={() => handleOpenRescheduleModal(appointment)}
                              >
                                Đổi lịch hẹn
                              </Dropdown.Item>
                            }
                            <Dropdown.Item
                              eventKey="2"
                              onClick={() => {
                                setSelectedAppointment(appointment);
                                setShowCancelModal(true);
                              }}
                            >
                              Huỷ lịch hẹn
                            </Dropdown.Item>
                          </DropdownButton>
                        }
                      </ButtonGroup>
                    </Stack>
                  </Stack>
                </Card.Body>
              </Card>
            ))}
          </Stack>
        )}

      {/* Modal xác nhận huỷ lịch hẹn */}
      <Modal show={showCancelModal} onHide={() => setShowCancelModal(false)} centered>
        <Modal.Header closeButton>
          <Modal.Title>Xác nhận huỷ lịch hẹn</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {cancelError && <Alert variant="danger">{cancelError}</Alert>}
          Bạn có chắc chắn muốn huỷ lịch hẹn này không?
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowCancelModal(false)}>
            Đóng
          </Button>
          <Button
            variant="danger"
            onClick={handleCancelAppointment}
            disabled={cancelLoading}
          >
            {cancelLoading ? "Đang huỷ..." : "Xác nhận huỷ"}
          </Button>
        </Modal.Footer>
      </Modal>

      {/* Modal đổi lịch hẹn */}
      <Modal show={showRescheduleModal} onHide={() => setShowRescheduleModal(false)} centered>
        <Modal.Header closeButton>
          <Modal.Title>Đổi lịch hẹn</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form onSubmit={handleReschedule}>
            <Form.Group className="mb-3">
              <Form.Label>Ngày khám mới</Form.Label>
              <Form.Control
                type="date"
                required
                value={rescheduleDate}
                min={new Date(Date.now() + 24 * 60 * 60 * 1000).toISOString().split('T')[0]}
                max={new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString().split('T')[0]}
                onChange={e => setRescheduleDate(e.target.value)}
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Ca khám mới</Form.Label>
              <ButtonGroup className="d-flex flex-wrap gap-2">
                {SLOT_LABELS.map((label, idx) => (
                  <ToggleButton
                    key={idx + 1}
                    id={`reschedule-slot-${idx + 1}`}
                    type="radio"
                    variant={rescheduleTakenSlots.includes(idx + 1) ? "outline-secondary" : "outline-primary"}
                    name="rescheduleTimeSlot"
                    value={idx + 1}
                    checked={rescheduleSlot === (idx + 1).toString()}
                    onChange={e => setRescheduleSlot(e.currentTarget.value)}
                    style={{ borderRadius: 5 }}
                    disabled={rescheduleTakenSlots.includes(idx + 1)}
                  >
                    {label}
                  </ToggleButton>
                ))}
              </ButtonGroup>
            </Form.Group>
            {rescheduleError && <Alert variant="danger">{rescheduleError}</Alert>}
            <div className="d-flex justify-content-end gap-2">
              <Button variant="secondary" onClick={() => setShowRescheduleModal(false)}>
                Huỷ
              </Button>
              <Button type="submit" variant="primary" disabled={rescheduleLoading || !rescheduleDate || !rescheduleSlot}>
                {rescheduleLoading ? "Đang đổi..." : "Xác nhận đổi lịch"}
              </Button>
            </div>
          </Form>
        </Modal.Body>
      </Modal>

      {/* Toast thông báo thành công */}
      <ToastContainer position="bottom-end" className="p-3" style={{ position: "fixed" }}>
        <Toast
          onClose={() => setRescheduleSuccess(false)}
          show={rescheduleSuccess}
          delay={3000}
          autohide
        >
          <Toast.Header>
            <strong className="me-auto">Thông báo</strong>
          </Toast.Header>
          <Toast.Body>
            ✅ Đổi lịch thành công!
          </Toast.Body>
        </Toast>
      </ToastContainer>
    </Container>
  );
};

export default AppointmentList;