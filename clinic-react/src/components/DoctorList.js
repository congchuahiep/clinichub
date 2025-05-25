import { useEffect, useState } from "react";
import { Alert, Badge, Button, Card, Col, Container, Form, Image, Pagination, Row, Spinner, Stack } from "react-bootstrap";
import APIs, { endpoints } from "../configs/APIs";
import RatingStars from "./RatingStars";
import { useNavigate } from "react-router-dom";
import Breadcrumbs from "./layouts/Breadcrumbs";
import AppointmentModal from "./AppointmentModal";
import { useAuth } from "../configs/AuthProvider";

const DoctorList = () => {

  const { user } = useAuth();
  const navigator = useNavigate();

  const [doctors, setDoctor] = useState([]);
  const [hospitals, setHospitals] = useState([]);
  const [specialties, setSpecialties] = useState([]);

  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState();

  const [pageNumber, setPageNumber] = useState();
  const [pageSize, setPageSize] = useState();
  const [totalDoctor, setTotalDoctor] = useState();
  const [totalPage, setTotalPage] = useState();

  const [selectedHospital, setSelectedHospital] = useState(null);
  const [selectedSpecialty, setSelectedSpecialty] = useState(null);

  const [showModal, setShowModal] = useState(false);
  const [selectedDoctorId, setSelectedDoctorId] = useState(null);
  const [selectedDoctorName, setSelectedDoctorName] = useState(null);

  const loadDoctor = async (page, hospitalId, specialtyId) => {
    setLoading(true);

    await APIs.get(endpoints.doctors, { params: { page, hospitalId, specialtyId } })
      .then((response) => {
        console.log(response);
        setDoctor(response.data.results);
        setPageNumber(response.data.pageNumber);
        setPageSize(response.data.pageSize);
        setTotalDoctor(response.data.totalElements);
        setTotalPage(response.data.totalPages);
      })
      .catch(ex => {
        console.log(ex);
        setErrorMessage(ex.response.data)
      })
      .finally(() => {
        setLoading(false);
      })
  }

  const loadHospitals = async () => {
    await APIs.get(endpoints.hospitals)
      .then((response) => {
        setHospitals(response.data)
      })
      .catch(ex => {
        console.log(ex);
        setErrorMessage("[" + ex.response.status + "] Không thể load được bệnh viện");
      })
  }

  const loadSpecialty = async () => {
    await APIs.get(endpoints.specialties)
      .then((response) => {
        console.log(response);
        setSpecialties(response.data);
      })
      .catch(ex => {
        console.log(ex);
        setErrorMessage("[" + ex.response.status + "] Không thể load được các chuyên khoa");
      })
  }

  useEffect(() => {
    loadDoctor(pageNumber);
    loadHospitals();
    loadSpecialty();
  }, [])

  useEffect(() => {
    loadDoctor(1, selectedHospital, selectedSpecialty);
  }, [selectedHospital, selectedSpecialty])

  const handleOpenAppointmentModal = (doctorId, doctorName) => {

    if (!user || user.userRole === "DOCTOR") {
      navigator("/login");
    }

    setSelectedDoctorId(doctorId);
    setSelectedDoctorName(doctorName);
    setShowModal(true);
  };

  return (
    <>
      <Container>
        <Breadcrumbs />
        <Row>
          {
            errorMessage &&
            <Alert variant="danger">
              Lỗi: {errorMessage}
            </Alert>
          }
        </Row>
        <Row>
          <Col md={3}>
            <Card className="sticky-top" style={{ top: 24 }} bg={"light"}>
              <Card.Body>
                <Card.Title>
                  Bộ lọc
                </Card.Title>

                <Form.Group className="my-3">
                  <div className="d-flex justify-content-between">
                    <Form.Label >Bệnh viện</Form.Label>
                    <i
                      className={`bi bi-x-circle ${selectedHospital ? 'text-danger' : 'text-secondary'}`}
                      disabled={!selectedHospital}
                      onClick={() => setSelectedHospital(null)}
                    />

                  </div>
                  <Form.Select
                    value={selectedHospital || ""}
                    onChange={(e) => setSelectedHospital(e.target.value)}
                  >
                    <option value="" disabled selected>Chọn bệnh viện</option>
                    {hospitals.map((hospital) =>
                      <option
                        key={hospital.id}
                        value={hospital.id}
                      >
                        {hospital.name}
                      </option>
                    )}
                  </Form.Select>
                </Form.Group>

                <Form.Group>
                  <div className="d-flex justify-content-between">
                    <Form.Label>Chuyên khoa</Form.Label>
                    <i
                      className={`bi bi-x-circle ${selectedSpecialty ? 'text-danger' : 'text-secondary'}`}
                      disabled={!selectedSpecialty}
                      onClick={() => setSelectedSpecialty(null)}
                    />
                  </div>
                  <Form.Select
                    value={selectedSpecialty || ""}
                    onChange={(e) => setSelectedSpecialty(e.target.value)}
                  >
                    <option value="" disabled selected>Chọn chuyên khoa</option>
                    {specialties.map((specialty) =>
                      <option
                        key={specialty.id}
                        value={specialty.id}
                      >
                        {specialty.name}
                      </option>
                    )}
                  </Form.Select>
                </Form.Group>


              </Card.Body>
            </Card>
          </Col>

          <Col md={9}>

            <DoctorListView
              doctors={doctors}
              pageNumber={pageNumber}
              pageSize={pageSize}
              totalDoctor={totalDoctor}
              totalPage={totalPage}
              onPageChange={(page) => loadDoctor(page, selectedHospital, selectedSpecialty)}
              loading={loading}
              handleOpenModal={handleOpenAppointmentModal}
            />


          </Col>
        </Row>
      </Container>

      <AppointmentModal
        show={showModal}
        onHide={() => setShowModal(false)}
        doctorId={selectedDoctorId}
        doctorName={selectedDoctorName}
      />
    </>
  )
}

const DoctorListView = ({ doctors, pageNumber, pageSize, totalDoctor, totalPage, onPageChange, loading, handleOpenModal }) => {

  const navigate = useNavigate();
  const { user } = useAuth();

  const paginationItems = [];

  for (let number = 1; number <= totalPage; number++) {
    paginationItems.push(
      <Pagination.Item
        key={number}
        active={number === pageNumber}
        onClick={() => {
          if (number !== pageNumber) return onPageChange(number)
        }}
      >
        {number}
      </Pagination.Item>
    );
  }

  return (
    <Card bg={"light"}>
      <Card.Body className="d-flex justify-content-center align-items-center" style={{ minHeight: 500 }}>
        {loading ? (
          <Spinner animation="border" variant="primary" />
        ) : totalDoctor === 0 ? (
          <div className="text-center w-100">
            <h5>Không có bác sĩ nào :'(</h5>
          </div>
        ) : (
          <Stack gap={3} className="mb-3">
            {doctors.map((doctor) =>
              <Card key={doctor.doctorDTO.id} >
                <Container fluid>
                  <Card.Body>
                    <Stack direction="horizontal" style={{ gap: "2rem" }}>
                      <Image
                        width={180}
                        height={180}
                        src={doctor.doctorDTO.avatar || "/no-avatar.jpg"}
                        style={{ objectFit: "cover" }}
                        roundedCircle
                      />

                      <div>
                        <Card.Title>
                          BS {doctor.doctorDTO.lastName} {doctor.doctorDTO.firstName}
                        </Card.Title>
                        <Card.Text>
                          Khoa khám: {doctor.doctorLicenseDTOSet.map((doctorLicense) =>
                            <Badge
                              key={doctorLicense.licenseNumber}
                              bg="secondary"> {doctorLicense.specialtyName}
                            </Badge>
                          )}
                        </Card.Text>
                        <Card.Text>
                          Bệnh viện: {doctor.hospitalDTOSet.map((hospital) => hospital.name).join(', ')}
                        </Card.Text>
                      </div>

                      <div className="ms-auto" style={{ width: 132 }}>
                        <Card.Text>
                          <RatingStars avgRating={doctor.avgRating} />
                        </Card.Text>
                        <Stack gap={2}>
                          <Button
                            variant="outline-success"
                            onClick={() => navigate(`/doctors/${doctor.doctorDTO.id}`)}
                          >
                            Xem chi tiết
                          </Button>

                          {!user || user?.userRole !== "DOCTOR" &&
                            <Button onClick={() => handleOpenModal(
                              doctor.doctorDTO.id,
                              doctor.doctorDTO.lastName + " " + doctor.doctorDTO.firstName
                            )}>
                              Đặt lịch khám
                            </Button>
                          }
                        </Stack>
                      </div>
                    </Stack>
                  </Card.Body>
                </Container>
              </Card>
            )}
          </Stack>
        )}
      </Card.Body>
      <Card.Footer className="d-flex justify-content-between align-items-center">
        <Card.Text className="mb-0">
          Tổng số kết quả tìm kiếm: {totalDoctor}
        </Card.Text>
        <Pagination className="mb-0">
          {paginationItems}
        </Pagination>
      </Card.Footer>
    </Card>
  )
}

export default DoctorList;