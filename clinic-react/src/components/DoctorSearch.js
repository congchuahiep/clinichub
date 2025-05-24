import { useEffect, useState } from "react";
import { Badge, Button, Card, Col, Container, Image, Pagination, Row, Stack } from "react-bootstrap";
import APIs, { endpoints } from "../configs/APIs";

const DoctorSearch = () => {

  const [doctors, setDoctor] = useState([]);
  const [pageNumber, setPageNumber] = useState();
  const [pageSize, setPageSize] = useState();
  const [totalDoctor, setTotalDoctor] = useState();
  const [totalPage, setTotalPage] = useState();

  const loadDoctor = async (page) => {

    await APIs.get(endpoints.doctors, { params: { page }})
      .then((response) => {
        console.log(response);
        setDoctor(response.data.results);
        setPageNumber(response.data.pageNumber);
        setPageSize(response.data.pageSize);
        setTotalDoctor(response.data.totalElements);
        setTotalPage(response.data.totalPages);
      })
  }

  useEffect(() => {
    loadDoctor(pageSize);
  }, [])

  return (
    <>
      <Container>
        <Row>
          <Col md={3}>
            <Card>
              <Card.Body>
                <Card.Title>
                  Bộ lọc
                </Card.Title>
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
              onPageChange={loadDoctor}
            />
          </Col>
        </Row>
      </Container>
    </>
  )
}

const DoctorList = ({ doctors, pageNumber, pageSize, totalDoctor, totalPage, onPageChange }) => {

  const paginationItems = [];

  for (let number = 1; number <= totalPage; number++) {
    paginationItems.push(
      <Pagination.Item
        key={number}
        active={number === pageNumber}
      onClick={() => onPageChange(number)}
      >
        {number}
      </Pagination.Item>
    );
  }

  return (
    <Card>
      <Card.Body>
        <Stack gap={3} className="mb-3">
          {doctors.map((doctor) =>
            <Card key={doctor.doctorDTO.id}>
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
                  </Row>
                </Card.Body>
              </Container>
            </Card>
          )}
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

export default DoctorSearch;