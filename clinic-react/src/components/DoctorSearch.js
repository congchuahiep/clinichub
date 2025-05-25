import { useEffect, useState } from "react";
import { Badge, Button, Card, Col, Container, Form, Image, Pagination, Row, Spinner, Stack } from "react-bootstrap";
import APIs, { endpoints } from "../configs/APIs";
import RatingStars from "./RatingStars";

const DoctorSearch = () => {

  const [doctors, setDoctor] = useState([]);
  const [hospitals, setHospitals] = useState([]);
  const [specialties, setSpecialties] = useState([]);

  const [loading, setLoading] = useState(false);

  const [pageNumber, setPageNumber] = useState();
  const [pageSize, setPageSize] = useState();
  const [totalDoctor, setTotalDoctor] = useState();
  const [totalPage, setTotalPage] = useState();

  const [selectedHospital, setSelectedHospital] = useState(null);
  const [selectedSpecialty, setSelectedSpecialty] = useState(null);

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
        // TODO: Hiển thị lỗi
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
        // TODO: Hiển thị lỗi
      })
      .finally(() => {
        // TODO: Set cục loading
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
        // TODO: Hiển thị lỗi
      })
      .finally(() => {
        // TODO: Set cục loading
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

  return (
    <>
      <Container>
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

            <DoctorList
              doctors={doctors}
              pageNumber={pageNumber}
              pageSize={pageSize}
              totalDoctor={totalDoctor}
              totalPage={totalPage}
              onPageChange={(page) => loadDoctor(page, selectedHospital, selectedSpecialty)}
              loading={loading}
            />


          </Col>
        </Row>
      </Container>
    </>
  )
}

const DoctorList = ({ doctors, pageNumber, pageSize, totalDoctor, totalPage, onPageChange, loading }) => {

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
                    <Row className="align-items-center">
                      <Col style={{ flex: "0 0" }}>
                        <Image
                          width={180}
                          height={180}
                          src={doctor.doctorDTO.avatar || "/no-avatar.jpg"}
                          roundedCircle
                        />
                      </Col>
                      <Col className="flex-grow-1">
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
                      </Col>
                      <Col>
                        <Card.Text>
                          <RatingStars avgRating={doctor.avgRating} />
                        </Card.Text>
                      </Col>
                    </Row>
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

export default DoctorSearch;