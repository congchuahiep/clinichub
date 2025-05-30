import { useEffect, useRef, useState } from "react";
import { Alert, Badge, Button, Card, Col, Container, Form, Image, InputGroup, Pagination, Row, Spinner, Stack } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import APIs, { endpoints } from "../configs/APIs";
import { useAuth } from "../configs/AuthProvider";
import AppointmentModal from "./AppointmentModal";
import Breadcrumbs from "./layouts/Breadcrumbs";
import RatingStars from "./RatingStars";

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

  const [searchDoctorName, setSearchDoctorName] = useState("");
  const searchTimeout = useRef(null);

  const loadDoctor = async (page, hospitalId, specialtyId, doctorName) => {
    try {
      setLoading(true);
      const response = await APIs.get(endpoints.doctors, {
        params: { page, hospitalId, specialtyId, doctorName }
      });
      setDoctor(response.data.results);
      setPageNumber(response.data.pageNumber);
      setPageSize(response.data.pageSize);
      setTotalDoctor(response.data.totalElements);
      setTotalPage(response.data.totalPages);
    } catch (ex) {
      setErrorMessage("Không thể tải lên danh sách các bác sĩ")
    } finally {
      setLoading(false);
    }
  }

  const loadHospitals = async () => {
    await APIs.get(endpoints.hospitals)
      .then((response) => {
        setHospitals(response.data)
      })
      .catch(ex => {
        console.log(ex);
        setErrorMessage("[" + ex.code + "] Không thể load được bệnh viện");
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
        setErrorMessage("[" + ex.code + "] Không thể load được các chuyên khoa");
      })
  }

  useEffect(() => {
    loadDoctor(pageNumber);
    loadHospitals();
    loadSpecialty();
  }, [])

  useEffect(() => {
    loadDoctor(1, selectedHospital, selectedSpecialty, searchDoctorName);
  }, [selectedHospital, selectedSpecialty]);

  const handleOpenAppointmentModal = (doctorId, doctorName) => {
    if (!user || user.userRole === "DOCTOR") {
      navigator("/login");
    }

    setSelectedDoctorId(doctorId);
    setSelectedDoctorName(doctorName);
    setShowModal(true);
  };

  if (errorMessage) {
    return (
      <Container className="py-5">
        <Alert variant="danger">{errorMessage}</Alert>
      </Container>
    );
  }

  return (
    <>
      <Container>
        <h3 className="mb-4">Tìm kiếm bác sĩ</h3>
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
              onPageChange={(page) => loadDoctor(page, selectedHospital, selectedSpecialty, searchDoctorName)}
              loading={loading}
              handleOpenModal={handleOpenAppointmentModal}
              searchDoctorName={searchDoctorName}
              setSearchDoctorName={setSearchDoctorName}
              searchTimeout={searchTimeout}
              selectedHospital={selectedHospital}
              selectedSpecialty={selectedSpecialty}
              loadDoctor={loadDoctor}
            />


          </Col>
        </Row>
      </Container>
      {user?.userRole == "PATIENT" &&
        <AppointmentModal
          show={showModal}
          onHide={() => setShowModal(false)}
          doctorId={selectedDoctorId}
          doctorName={selectedDoctorName}
        />
      }
    </>
  )
}

const DoctorListView = ({
  doctors,
  pageNumber,
  pageSize,
  totalDoctor,
  totalPage,
  onPageChange,
  loading,
  handleOpenModal,
  searchDoctorName,
  loadDoctor,
  setSearchDoctorName,
  searchTimeout,
  selectedHospital,
  selectedSpecialty
}) => {

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
      <Card.Body className="" style={{ minHeight: 500 }}>

        <Stack gap={3} className="mb-3">
          <Form className="mb-3">
            <InputGroup>
              <InputGroup.Text>
                <i class="bi bi-search"></i>
              </InputGroup.Text>
              <Form.Control
                type="text"
                placeholder="Tìm kiếm bác sĩ theo tên..."
                value={searchDoctorName}
                onChange={e => {
                  setSearchDoctorName(e.target.value);
                  if (searchTimeout.current) clearTimeout(searchTimeout.current);
                  searchTimeout.current = setTimeout(() => {
                    loadDoctor(1, selectedHospital, selectedSpecialty, e.target.value);
                  }, 500); // debounce 500ms
                }}
              />
            </InputGroup>
          </Form>

          <div className="d-flex justify-content-center align-items-center">
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
                                  bg="secondary"
                                  className="me-1"
                                >
                                  {doctorLicense.specialtyName}
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
          </div>
        </Stack>
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