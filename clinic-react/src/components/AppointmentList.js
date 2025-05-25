import { useEffect, useState } from "react";
import { Alert, Badge, Button, Card, Col, Container, Image, Row, Spinner, Stack } from "react-bootstrap";
import cookie from "react-cookies";
import { useNavigate } from "react-router-dom";
import { SLOT_LABELS, STATUS_MAP } from "../utils/AppointmentUtils";
import { authApis, endpoints } from "../configs/APIs";
import { MyUserContext } from "../configs/MyContexts";
import { useAuth } from "../configs/AuthProvider";


const AppointmentList = () => {
  const { user } = useAuth();

  const navigate = useNavigate();
  const [appointments, setAppointments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [err, setErr] = useState("");

  useEffect(() => {
    if (!cookie.load("token")) {
      navigate("/login");
      return;
    }
    const loadAppointments = async () => {
      setLoading(true);
      setErr("");
      try {
        const res = await authApis().get(endpoints.appointments);
        console.log(res.data)
        setAppointments(res.data);
      } catch (e) {
        console.log(e)
        setErr("Không thể tải danh sách lịch khám!");
      } finally {
        setLoading(false);
      }
    };
    loadAppointments();
  }, [navigate]);

  if (loading) {
    return (
      <Container className="py-5 text-center">
        <Spinner animation="border" variant="primary" />
      </Container>
    );
  }

  if (err) {
    return (
      <Container className="py-5">
        <Alert variant="danger">{err}</Alert>
      </Container>
    );
  }

  return (
    <Container lassName="py-3 mx-auto" style={{ maxWidth: 700 }}>
      <h3 className="mb-4">Lịch Khám Bệnh</h3>
      {appointments.length === 0 ? (
        <Alert variant="info">Bạn chưa có lịch khám nào.</Alert>
      ) : (
        <Stack gap={3}>
          {appointments.map((appointment) => (
            <Card key={appointment.id}>
              <Card.Header>
                <Stack direction="horizontal" gap={5}>

                  <div>
                    <i class="bi bi-calendar-week"> </i>
                    {" "}Ngày khám: {appointment.appointmentDate}
                  </div>

                  <div>
                    <i class="bi bi-clock"></i>
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
                          <Alert variant="success" height={240}>
                            <i class="bi bi-clipboard2-pulse-fill"> </i>
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
                                  <i class="bi bi-envelope"> </i>
                                  {appointment.doctor.email}
                                </div>
                                <div>
                                  <i class="bi bi-telephone"> </i>
                                  {appointment.doctor.phone}
                                </div>
                              </div>
                            </Stack>
                          </Alert>
                        </Col>
                        :
                        <Col>
                          <Alert variant="warning">
                            <i class="bi bi-person-fill"></i>
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
                                  <i class="bi bi-envelope"> </i>
                                  {appointment.patient.email}
                                </div>
                                <div>
                                  <i class="bi bi-telephone"> </i>
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
                    <Button
                      className="ms-auto"
                      variant="outline-danger"
                      disabled={STATUS_MAP[appointment.status] == STATUS_MAP.completed}
                    >
                      Huỷ lịch hẹn
                    </Button>
                    <Button
                      onClick={() => navigate(`/appointments/${appointment.id}`)}
                    >
                      Xem chi tiết
                    </Button>

                  </Stack>
                </Stack>
              </Card.Body>
            </Card>
          ))}
        </Stack>
      )}
    </Container>
  );
};

export default AppointmentList;