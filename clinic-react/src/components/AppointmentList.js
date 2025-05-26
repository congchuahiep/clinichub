import { useEffect, useState } from "react";
import { Alert, Badge, Button, ButtonGroup, Card, Col, Container, Dropdown, DropdownButton, Form, Image, Modal, Placeholder, Row, Stack } from "react-bootstrap";
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
      setCancelError("Huỷ lịch hẹn thất bại!");
    } finally {
      setCancelLoading(false);
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
                            <Dropdown.Item
                              eventKey="1"
                            >
                              Đổi lịch hẹn
                            </Dropdown.Item>
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
    </Container>
  );
};

export default AppointmentList;