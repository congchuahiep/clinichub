import { useEffect, useState } from "react";
import { Alert, Badge, Card, Container, Image, Spinner, Stack, Modal, Form, Button, Table } from "react-bootstrap";
import cookie from "react-cookies";
import { useNavigate, useParams } from "react-router-dom";
import { authApis, endpoints } from "../configs/APIs";
import { SLOT_LABELS, STATUS_MAP } from "../utils/AppointmentUtils";
import { useAuth } from "../configs/AuthProvider";
import { AsyncPaginate } from "react-select-async-paginate";
import Breadcrumbs from "./layouts/Breadcrumbs";


const AppointmentDetail = () => {

  const { user } = useAuth();

  const { id } = useParams();
  const navigate = useNavigate();

  const [appointment, setAppointment] = useState(null);

  const [showDiagnosisModal, setShowDiagnosisModal] = useState(false);
  const [diagnosis, setDiagnosis] = useState({
    diseaseId: "",
    diagnosis: "",
    prescriptions: "",
    testResults: "",
    notes: ""
  });
  const [diseases, setDiseases] = useState([]);

  const [diagLoading, setDiagLoading] = useState(false);
  const [diagError, setDiagError] = useState("");
  const [diagSuccess, setDiagSuccess] = useState(false);

  const [loading, setLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState("");

  const [healthProfile, setHealthProfile] = useState(null);
  const [healthProfileEditMode, setHealthProfileEditMode] = useState(false);
  const [healthProfileForm, setHealthProfileForm] = useState({});
  const [savingHealthProfile, setSavingHealthProfile] = useState(false);
  const [saveError, setSaveHealthProfileError] = useState("");
  const [saveHealthProfileSuccess, setSaveHealthProfileSuccess] = useState(false);

  useEffect(() => {
    const loadDiseases = async () => {

    }
  }, [])

  useEffect(() => {
    const loadDetail = async () => {
      setLoading(true);
      setErrorMessage("");
      try {
        const appointmentResponse = await authApis().get(endpoints["appointment-detail"](id));
        const appointmentData = appointmentResponse.data
        setAppointment(appointmentResponse.data);

        const healthProfileResponse = await authApis().get(endpoints["doctor-health-profile"](appointmentData.patient.id));
        console.log(healthProfileResponse);
        setHealthProfileForm(healthProfileResponse.data);
      } catch (e) {
        setErrorMessage("Không thể tải chi tiết lịch khám!");
      } finally {
        setLoading(false);
      }
    };
    loadDetail();
  }, [id, navigate]);

  // Hàm submit chẩn đoán
  const handleDiagnosisSubmit = async (e) => {
    e.preventDefault();
    setDiagLoading(true);
    setDiagError("");
    setDiagSuccess(false);
    try {
      await authApis().post(endpoints["appointment-diagnosis"](id), diagnosis);
      setDiagSuccess(true);
      setShowDiagnosisModal(false);
      // Reload lại chi tiết lịch hẹn để hiển thị medicalRecord mới
      const res = await authApis().get(endpoints["appointment-detail"](id));
      setAppointment(res.data);
    } catch (err) {
      setDiagError("Ghi chẩn đoán thất bại!");
    } finally {
      setDiagLoading(false);
    }
  };

  const handleHealthChange = (field, value) => {
    setHealthProfileForm(prev => ({ ...prev, [field]: value }));
  };

  const handleSave = async () => {
    setSavingHealthProfile(true);
    setSaveHealthProfileError("");
    setSaveHealthProfileSuccess(false);
    try {
      await authApis().put(endpoints["doctor-health-profile"](appointment.patient.id), healthProfileForm);
      setHealthProfile({ ...healthProfileForm });
      setHealthProfileEditMode(false);
      setSaveHealthProfileSuccess(true);
    } catch (e) {
      console.log(e);
      setSaveHealthProfileError("Lưu thông tin thất bại!");
    } finally {
      setSavingHealthProfile(false);
      setTimeout(() => setSaveHealthProfileSuccess(false), 2000);
    }
  };

  if (loading) {
    return (
      <Container className="py-5 text-center">
        <Spinner animation="border" variant="primary" />
      </Container>
    );
  }

  if (errorMessage) {
    return (
      <Container className="py-5">
        <Alert variant="danger">{errorMessage}</Alert>
      </Container>
    );
  }

  if (!appointment) return null;

  const { doctor, patient, medicalRecord } = appointment;

  return (
    <Container className="py-4" style={{ maxWidth: 700 }}>
      <h3 className="mb-4">Chi tiết lịch khám</h3>
      <Breadcrumbs customTitle={"Lịch khám ngày: " + appointment.appointmentDate} />

      <Card>
        <Card.Header>
          <Stack direction="horizontal" gap={4}>
            <div>
              <i className="bi bi-calendar-week"></i>
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
          {user && user?.userRole == "PATIENT" ?
            <Alert variant={STATUS_MAP[appointment.status]?.variant || "secondary"} className="h-100">
              <i className="bi bi-clipboard2-pulse-fill"></i> <b>Bác sĩ</b>
              <Stack gap={3} className="mt-2" direction="horizontal">
                <Image
                  width={64}
                  height={64}
                  src={doctor.avatar || "/no-avatar.jpg"}
                  style={{ objectFit: "cover" }}
                  roundedCircle
                />
                <div>
                  <b>BS. {doctor.lastName} {doctor.firstName}</b>
                  <div>
                    <i className="bi bi-envelope"></i> {doctor.email}
                  </div>
                  <div>
                    <i className="bi bi-telephone"></i> {doctor.phone}
                  </div>
                </div>
              </Stack>
            </Alert>
            :
            <Alert variant={STATUS_MAP[appointment.status]?.variant || "secondary"} className="h-100">
              <i className="bi bi-person-fill"></i> <b>Bệnh nhân</b>
              <Stack gap={3} className="mt-2" direction="horizontal">
                <Image
                  width={64}
                  height={64}
                  src={patient.avatar || "/no-avatar.jpg"}
                  style={{ objectFit: "cover" }}
                  roundedCircle
                />
                <div>
                  <b>{patient.lastName} {patient.firstName}</b>
                  <div>
                    <i className="bi bi-envelope"></i> {patient.email}
                  </div>
                  <div>
                    <i className="bi bi-telephone"></i> {patient.phone}
                  </div>
                </div>
              </Stack>
              <p className="mb-2 mt-4"><b>Hồ sơ sức khoẻ</b></p>
              <Card>
                <Card.Body>
                  <Table bordered>
                    <tbody>
                      <tr>
                        <td><b>Tiền sử bệnh</b></td>
                        <td>
                          <Form.Control
                            as="textarea"
                            rows={1}
                            value={healthProfileForm.medicalHistory || ""}
                            variant=""
                            disabled={!healthProfileEditMode}
                            placeholder="Nhập tiền sử bệnh (nếu có)..."
                            onChange={e => handleHealthChange("medicalHistory", e.target.value)}
                          />
                        </td>
                      </tr>
                      <tr>
                        <td><b>Dị ứng</b></td>
                        <td>
                          <Form.Control
                            as="textarea"
                            rows={1}
                            value={healthProfileForm.allergies || ""}
                            disabled={!healthProfileEditMode}
                            placeholder="Nhập dị ứng (nếu có)..."
                            onChange={e => handleHealthChange("allergies", e.target.value)}
                          />
                        </td>
                      </tr>
                      <tr>
                        <td><b>Bệnh mãn tính</b></td>
                        <td>
                          <Form.Control
                            as="textarea"
                            rows={1}
                            value={healthProfileForm.chronicConditions || ""}
                            disabled={!healthProfileEditMode}
                            placeholder="Nhập bệnh mãn tính (nếu có)..."
                            onChange={e => handleHealthChange("chronicConditions", e.target.value)}
                          />
                        </td>
                      </tr>
                      <tr>
                        <td><b>Cân nặng (kg)</b></td>
                        <td>
                          <Form.Control
                            type="number"
                            value={healthProfileForm.weight || ""}
                            disabled={!healthProfileEditMode}
                            placeholder="Nhập cân nặng..."
                            onChange={e => handleHealthChange("weight", e.target.value)}
                          />
                        </td>
                      </tr>
                      <tr>
                        <td><b>Chiều cao (cm)</b></td>
                        <td>
                          <Form.Control
                            type="number"
                            value={healthProfileForm.height || ""}
                            disabled={!healthProfileEditMode}
                            placeholder="Nhập chiều cao..."
                            onChange={e => handleHealthChange("height", e.target.value)}
                          />
                        </td>
                      </tr>
                      <tr>
                        <td><b>Huyết áp</b></td>
                        <td>
                          <Form.Control
                            value={healthProfileForm.bloodPressure || ""}
                            disabled={!healthProfileEditMode}
                            placeholder="Nhập huyết áp..."
                            onChange={e => handleHealthChange("bloodPressure", e.target.value)}
                          />
                        </td>
                      </tr>
                      <tr>
                        <td><b>Đường huyết</b></td>
                        <td>
                          <Form.Control
                            value={healthProfileForm.bloodSugar || ""}
                            disabled={!healthProfileEditMode}
                            placeholder="Nhập đường huyết..."
                            onChange={e => handleHealthChange("bloodSugar", e.target.value)}
                          />
                        </td>
                      </tr>
                    </tbody>
                  </Table>
                  <div className="d-flex justify-content-end gap-2">
                    {!healthProfileEditMode ? (
                      <Button variant="primary" onClick={() => setHealthProfileEditMode(true)}>
                        Chỉnh sửa
                      </Button>
                    ) : (
                      <>
                        <Button variant="secondary" onClick={() => { setHealthProfileEditMode(false); setHealthProfileForm(healthProfile); }}>
                          Huỷ
                        </Button>
                        <Button variant="success" onClick={handleSave} disabled={savingHealthProfile}>
                          {savingHealthProfile ? "Đang lưu..." : "Lưu"}
                        </Button>
                      </>
                    )}
                  </div>
                  {saveError && <Alert variant="danger" className="mt-2">{saveError}</Alert>}
                  {saveHealthProfileSuccess && <Alert variant="success" className="mt-2">Lưu thành công!</Alert>}
                </Card.Body>
              </Card>
            </Alert>
          }
          <hr />
          <div className="d-flex justify-content-between">
            <div>
              <b>Ngày tạo lịch hẹn: </b>
              {appointment.createdAt}
            </div>

            {/* FORM CHẨN ĐOÁN */}
            {/* Chỉ hiển thị nút nếu là bác sĩ, chưa có medicalRecord và status là scheduled */}
            {user && user?.userRole === "DOCTOR" && !medicalRecord && appointment.status === "scheduled" && (
              <>
                <Button variant="primary" onClick={() => setShowDiagnosisModal(true)}>
                  Ghi chẩn đoán
                </Button>
                <Modal show={showDiagnosisModal} onHide={() => setShowDiagnosisModal(false)} centered>
                  <Modal.Header closeButton>
                    <Modal.Title>Ghi chẩn đoán</Modal.Title>
                  </Modal.Header>
                  <Modal.Body>
                    <Form onSubmit={handleDiagnosisSubmit}>
                      <Form.Group className="mb-3">
                        <Form.Label>Chọn loại bệnh</Form.Label>
                        <AsyncPaginate
                          value={
                            diseases.find(d => d.id === Number(diagnosis.diseaseId))
                              ? {
                                value: diagnosis.diseaseId,
                                label: diseases.find(d => d.id === Number(diagnosis.diseaseId))?.name
                              }
                              : null
                          }
                          loadOptions={
                            async (search, loadedOptions, { page }) => {
                              const params = { page: page || 1, pageSize: 20 };

                              if (search) params.kw = search;
                              const res = await authApis().get(endpoints.diseases, { params });
                              // Lưu lại danh sách bệnh để dùng cho label
                              setDiseases(prev => {
                                const ids = new Set(prev.map(d => d.id));
                                return [...prev, ...res.data.results.filter(d => !ids.has(d.id))];
                              });

                              return {
                                options: res.data.results.map(d => ({
                                  value: d.id,
                                  label: d.name
                                })),
                                hasMore: res.data.pageNumber < res.data.totalPages,
                                additional: { page: (res.data.pageNumber || 1) + 1 }
                              };
                            }}
                          onChange={option => setDiagnosis(d => ({ ...d, diseaseId: option?.value || "" }))}
                          placeholder="Nhập tên bệnh để tìm kiếm..."
                          isClearable
                          additional={{ page: 1 }}
                        />
                      </Form.Group>
                      <Form.Group className="mb-3">
                        <Form.Label>Chẩn đoán</Form.Label>
                        <Form.Control
                          required
                          value={diagnosis.diagnosis}
                          onChange={e => setDiagnosis(d => ({ ...d, diagnosis: e.target.value }))}
                        />
                      </Form.Group>
                      <Form.Group className="mb-3">
                        <Form.Label>Kê đơn</Form.Label>
                        <Form.Control
                          value={diagnosis.prescriptions}
                          onChange={e => setDiagnosis(d => ({ ...d, prescriptions: e.target.value }))}
                        />
                      </Form.Group>
                      <Form.Group className="mb-3">
                        <Form.Label>Kết quả xét nghiệm</Form.Label>
                        <Form.Control
                          value={diagnosis.testResults}
                          onChange={e => setDiagnosis(d => ({ ...d, testResults: e.target.value }))}
                        />
                      </Form.Group>
                      <Form.Group className="mb-3">
                        <Form.Label>Ghi chú</Form.Label>
                        <Form.Control
                          as="textarea"
                          rows={2}
                          value={diagnosis.notes}
                          onChange={e => setDiagnosis(d => ({ ...d, notes: e.target.value }))}
                        />
                      </Form.Group>
                      {diagError && <Alert variant="danger">{diagError}</Alert>}
                      <div className="d-flex justify-content-end gap-2">
                        <Button variant="secondary" onClick={() => setShowDiagnosisModal(false)}>
                          Huỷ
                        </Button>
                        <Button type="submit" variant="primary" disabled={diagLoading}>
                          {diagLoading ? "Đang lưu..." : "Lưu chẩn đoán"}
                        </Button>
                      </div>
                    </Form>
                  </Modal.Body>
                </Modal>
              </>
            )}
          </div>
          {appointment.note && (
            <>
              <div>
                <b>Ghi chú:</b> {appointment.note}
              </div>

            </>


          )}
          <hr />

          <h5>Chẩn đoán</h5>
          {medicalRecord ? (

            <Table bordered>
              <tbody>
                <tr>
                  <td><b>Bệnh</b></td>
                  <td>{medicalRecord.diseaseName}</td>
                </tr>
                <tr>
                  <td><b>Chẩn đoán</b></td>
                  <td>{medicalRecord.diagnosis}</td>
                </tr>
                <tr>
                  <td><b>Ngày chẩn đoán</b></td>
                  <td>{medicalRecord.diagnosisDate ? new Date(medicalRecord.diagnosisDate).toLocaleDateString() : ""}</td>
                </tr>
                <tr>
                  <td><b>Kê đơn</b></td>
                  <td>{medicalRecord.prescriptions}</td>
                </tr>
                <tr>
                  <td><b>Kết quả xét nghiệm</b></td>
                  <td>{medicalRecord.testResults || "Không có"}</td>
                </tr>
                <tr>
                  <td><b>Ghi chú</b></td>
                  <td>{medicalRecord.notes}</td>
                </tr>
              </tbody>
            </Table>

          ) : (
            <Alert variant="secondary">Chưa có chẩn đoán khám bệnh cho lịch hẹn này.</Alert>
          )}
        </Card.Body>
      </Card>
    </Container>
  );
};

export default AppointmentDetail;