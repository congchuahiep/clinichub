import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { Card, Container, Row, Col, Badge, Image, Stack, Button, Spinner, ListGroup, Alert, Form, InputGroup } from "react-bootstrap";
import APIs, { authApis, endpoints } from "../configs/APIs";
import RatingStars from "./RatingStars";
import Breadcrumbs from "./layouts/Breadcrumbs";
import AppointmentModal from "./AppointmentModal";
import ReviewForm from "./ReviewForm";

const DoctorDetail = () => {
  const { id } = useParams();

  const [doctor, setDoctor] = useState(null);
  const [reviews, setReviews] = useState([]);
  const [showModal, setShowModal] = useState(false);


  const [loading, setLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState();

  const [reviewStatus, setReviewStatus] = useState(null);


  const checkReviewStatus = async () => {
    await authApis().get(endpoints["doctor-check-review"](id))
      .then((res) => {
        setReviewStatus(res.data);
      })
      .catch((ex) => {
        console.log(ex);
      })
  }

  const loadDoctor = async () => {
    setLoading(true);

    await APIs.get(endpoints["doctor-detail"](id))
      .then((res) => {
        setDoctor(res.data);
      })
      .catch(err => {
        setDoctor(null);
        console.log(err)
        if (err.status === 404) setErrorMessage("[" + err.status + "]: Bác sĩ không tồn tại")
        else setErrorMessage("[" + err.status + "]: " + err)
      })
      .finally(() => {
        setLoading(false)
      })
  };

  const loadReviews = async () => {
    await APIs.get(endpoints["doctor-reviews"](id))
      .then((res) => {
        setReviews(res.data.results);
      })
      .catch(err => {
        console.log(err)
      })
  };

  // Lấy thông tin bác sĩ và đánh giá
  useEffect(() => {
    loadDoctor();
    loadReviews();
    checkReviewStatus();
  }, [id]);


  if (loading) {

    return (
      <Container className="py-5 text-center">
        <Spinner animation="border" variant="primary" />
      </Container>
    );
  } else if (!doctor) {
    return (
      <Container className="py-5 text-center">
        <Breadcrumbs customTitle={"Lỗi"} />
        <Alert>
          Lỗi: {errorMessage}
        </Alert>
      </Container>
    );
  }

  const { doctorDTO, doctorLicenseDTOSet, hospitalDTOSet, avgRating } = doctor;

  return (
    <Container className="py-4">
      <Breadcrumbs customTitle={`BS ${doctorDTO.lastName} ${doctorDTO.firstName}`} />
      <Row>
        <Col md={4}>
          <Card>
            <Card.Body className="text-center">
              <Image
                src={doctorDTO.avatar || "/no-avatar.jpg"}
                roundedCircle
                width={140}
                height={140}
                style={{ objectFit: "cover" }}
                className="mt-5 mb-3"
              />
              <Card.Title>
                <b>BS {doctorDTO.lastName} {doctorDTO.firstName}</b>
              </Card.Title>

              <div className="mb-2">
                <RatingStars avgRating={avgRating} />
              </div>

              <Card.Text className="text-start ps-5">
                <p className="mb-1">
                  <i className="bi bi-envelope me-2"></i>
                  {doctorDTO.email}
                </p>
                <p className="mb-1">
                  <i className="bi bi-telephone me-2"></i>
                  {doctorDTO.phone}
                </p>
                <p className="mb-1">
                  <i className={`bi ${doctorDTO.gender === "male" ? "bi-gender-male" : "bi-gender-female"} me-2`}></i>
                  {doctorDTO.gender === "male" ? "Nam" : "Nữ"}
                </p>
              </Card.Text>
              <Button variant="primary" className="w-100" onClick={() => setShowModal(true)}>
                Đặt lịch khám
              </Button>
            </Card.Body>
          </Card>
        </Col>

        <Col md={8}>
          <Stack gap={4}>
            <Card >
              <Card.Header>
                Thông tin hành nghề
              </Card.Header>
              <Card.Body>
                <div className="mb-2">
                  <b>Chuyên khoa:</b>{" "}
                  {doctorLicenseDTOSet.map(l => (
                    <Badge key={l.licenseNumber} bg="secondary" className="me-2">
                      {l.specialtyName}
                    </Badge>
                  ))}
                </div>
                <div className="mb-2">
                  <b>Số giấy phép:</b>{" "}
                  {doctorLicenseDTOSet.map(l => l.licenseNumber).join(", ")}
                </div>
                <div className="mb-2">
                  <b>Ngày cấp:</b>{" "}
                  {doctorLicenseDTOSet.map(l => l.issued).join(", ")}
                </div>
                <div className="mb-2">
                  <b>Ngày hết hạn:</b>{" "}
                  {doctorLicenseDTOSet.map(l => l.expiry).join(", ")}
                </div>
                <div className="mb-2">
                </div>
              </Card.Body>
            </Card>

            <Card>
              <Card.Header>
                Nơi làm việc
              </Card.Header>
              <Card.Body>
                <div>
                  {hospitalDTOSet.map(h => (
                    <div key={h.id} className="mb-1">
                      <span className="fw-bold">{h.name}</span><br />
                      <span>Địa chỉ: {h.address}</span><br />
                      <span>Điện thoại: {h.phone}</span>
                    </div>
                  ))}
                </div>
              </Card.Body>
            </Card>

            <Card>
              <Card.Header>
                Đánh giá của bệnh nhân
              </Card.Header>
              <Card.Body>
                {reviewStatus === 'ALLOWED' && (
                  <Card className="mb-4">
                    <Card.Body>
                      <ReviewForm doctorId={doctorDTO.id} onSuccess={loadReviews} />
                    </Card.Body>
                  </Card>
                )}


                {reviews.length === 0 ? (
                  <div className="text-muted">Chưa có đánh giá nào.</div>
                ) : (
                  <ListGroup variant="flush">
                    {reviews.map(r => (
                      <ListGroup.Item key={r.id}>
                        <Stack direction="horizontal" gap={3}>
                          <Image
                            src={r.patient.avatar || "/no-avatar.jpg"}
                            roundedCircle
                            width={48}
                            height={48}
                            style={{ objectFit: "cover" }}
                          />
                          <div className="flex-grow-1">
                            <Stack direction="horizontal" >
                              <b>{r.patient.lastName} {r.patient.firstName}</b>
                              <span className="ms-2 ms-auto">
                                <RatingStars avgRating={r.rating} showText={false} />
                              </span>
                            </Stack>
                            <div className="text-muted" style={{ fontSize: 13 }}>
                              {r.createdAt}
                            </div>
                            <div>{r.comment}</div>
                            {r.doctorResponse &&
                              <div className="mt-2 p-2 bg-light border rounded">
                                <b>Phản hồi từ bác sĩ:</b> {r.doctorResponse}
                                <div className="text-muted" style={{ fontSize: 12 }}>
                                  {r.doctorResponseDate}
                                </div>
                              </div>
                            }
                          </div>
                        </Stack>
                      </ListGroup.Item>
                    ))}
                  </ListGroup>
                )}
              </Card.Body>
            </Card>
          </Stack>
        </Col>
      </Row>
      <AppointmentModal
        show={showModal}
        onHide={() => setShowModal(false)}
        doctorId={doctorDTO.id}
        doctorName={doctorDTO.lastName + " " + doctorDTO.firstName}
      />
    </Container>
  );
};

export default DoctorDetail;