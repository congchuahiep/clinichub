import { useEffect, useState } from "react";
import { authApis, endpoints } from "../configs/APIs";
import { Container, Card, Row, Col, Spinner, Alert, Image, Table, Button, Form } from "react-bootstrap";
import { useNavigate, useParams } from "react-router-dom";

const PatientProfile = () => {
  const navigator = useNavigate();

  const { id } = useParams();

  const [user, setUser] = useState(null);
  const [health, setHealth] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [editMode, setEditMode] = useState(false);
  const [healthForm, setHealthForm] = useState({});
  const [saving, setSaving] = useState(false);
  const [saveError, setSaveError] = useState("");
  const [saveSuccess, setSaveSuccess] = useState(false);
  const [medicalRecords, setMedicalRecords] = useState([]);
  const [medicalLoading, setMedicalLoading] = useState(true);
  const [medicalError, setMedicalError] = useState("");

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      setError("");
      setMedicalLoading(true);
      setMedicalError("");
      try {
        const [userRes, healthRes, medicalRes] = await Promise.all([
          authApis().get(endpoints["current-user"]),
          authApis().get(endpoints["health-profile"]),
          authApis().get(endpoints["medical-records"])
        ]);
        setUser(userRes.data);
        setHealth(healthRes.data);
        setHealthForm(healthRes.data);
        setMedicalRecords(medicalRes.data.results || []);
      } catch (e) {
        console.log(e);
        setError("Không thể tải hồ sơ cá nhân!");
        setMedicalError("Không thể tải danh sách chẩn đoán!");
      } finally {
        setLoading(false);
        setMedicalLoading(false);
      }
    };
    fetchData();
  }, []);

  const handleHealthChange = (field, value) => {
    setHealthForm(prev => ({ ...prev, [field]: value }));
  };

  const handleSave = async () => {
    setSaving(true);
    setSaveError("");
    setSaveSuccess(false);
    try {
      await authApis().put(endpoints["health-profile"], healthForm);
      setHealth({ ...healthForm });
      setEditMode(false);
      setSaveSuccess(true);
    } catch (e) {
      console.log(e);
      setSaveError("Lưu thông tin thất bại!");
    } finally {
      setSaving(false);
      setTimeout(() => setSaveSuccess(false), 2000);
    }
  };

  if (loading)
    return (
      <Container className="py-5 text-center">
        <Spinner animation="border" />
      </Container>
    );

  if (error)
    return (
      <Container className="py-5">
        <Alert variant="danger">{error}</Alert>
      </Container>
    );

  if (!user) return null;

  return (
    <Container className="py-4" style={{ maxWidth: 700 }}>
      <h3 className="mb-4">Hồ sơ cá nhân</h3>
      <Card className="mb-4">
        <Card.Body>
          <Row>
            <Col xs={12} md={4} className="text-center mb-3 mb-md-0">
              <Image
                src={user.avatar || "/no-avatar.jpg"}
                roundedCircle
                width={168}
                height={168}
                style={{ objectFit: "cover" }}
              />
            </Col>
            <Col xs={12} md={8}>

              <h5>{user.lastName} {user.firstName}</h5>
              <div><b>Tên đăng nhập:</b> {user.username}</div>
              <div><b>Email:</b> {user.email}</div>
              <div><b>Số điện thoại:</b> {user.phone}</div>
              <div><b>Ngày sinh:</b> {user.birthDate}</div>
              <div><b>Giới tính:</b> {user.gender === "male" ? "Nam" : user.gender === "female" ? "Nữ" : "Khác"}</div>
              <div><b>Địa chỉ:</b> {user.address || "Chưa cập nhật"}</div>

            </Col>
          </Row>
        </Card.Body>
      </Card>

      <h4 className="mb-3">Hồ sơ sức khoẻ</h4>
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
                    value={healthForm.medicalHistory || ""}
                    variant=""
                    disabled={!editMode}
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
                    value={healthForm.allergies || ""}
                    disabled={!editMode}
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
                    value={healthForm.chronicConditions || ""}
                    disabled={!editMode}
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
                    value={healthForm.weight || ""}
                    disabled={!editMode}
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
                    value={healthForm.height || ""}
                    disabled={!editMode}
                    placeholder="Nhập chiều cao..."
                    onChange={e => handleHealthChange("height", e.target.value)}
                  />
                </td>
              </tr>
              <tr>
                <td><b>Huyết áp</b></td>
                <td>
                  <Form.Control
                    value={healthForm.bloodPressure || ""}
                    disabled={!editMode}
                    placeholder="Nhập huyết áp..."
                    onChange={e => handleHealthChange("bloodPressure", e.target.value)}
                  />
                </td>
              </tr>
              <tr>
                <td><b>Đường huyết</b></td>
                <td>
                  <Form.Control
                    value={healthForm.bloodSugar || ""}
                    disabled={!editMode}
                    placeholder="Nhập đường huyết..."
                    onChange={e => handleHealthChange("bloodSugar", e.target.value)}
                  />
                </td>
              </tr>
            </tbody>
          </Table>
          <div className="d-flex justify-content-end gap-2">
            {!editMode ? (
              <Button variant="primary" onClick={() => setEditMode(true)}>
                Chỉnh sửa
              </Button>
            ) : (
              <>
                <Button variant="secondary" onClick={() => { setEditMode(false); setHealthForm(health); }}>
                  Huỷ
                </Button>
                <Button variant="success" onClick={handleSave} disabled={saving}>
                  {saving ? "Đang lưu..." : "Lưu"}
                </Button>
              </>
            )}
          </div>
          {saveError && <Alert variant="danger" className="mt-2">{saveError}</Alert>}
          {saveSuccess && <Alert variant="success" className="mt-2">Lưu thành công!</Alert>}
        </Card.Body>
      </Card>

      <h4 className="mb-3 mt-4">Lịch sử chẩn đoán bệnh</h4>
      <Card>
        <Card.Body>
          {medicalLoading ? (
            <Spinner animation="border" />
          ) : medicalError ? (
            <Alert variant="danger">{medicalError}</Alert>
          ) : medicalRecords.length === 0 ? (
            <Alert variant="info">Chưa có lần chẩn đoán nào.</Alert>
          ) : (
            <Table bordered responsive>
              <thead>
                <tr>
                  <th className="text-end">Ngày chẩn đoán</th>
                  <th>Bệnh</th>
                  <th>Xét nghiệm</th>
                  <th>Ghi chú</th>
                  <th>Chi tiết</th>
                </tr>
              </thead>
              <tbody>
                {medicalRecords.map(record => (
                  <tr key={record.appointmentId}>
                    <td className="text-end">{record.diagnosisDate ? new Date(record.diagnosisDate).toLocaleDateString() : ""}</td>
                    <td>{record.diseaseName}</td>
                    <td>{record.testResults}</td>
                    <td>{record.notes}</td>
                    <td className="d-grid">
                      <Button size="sm" onClick={() => navigator(`/appointments/${record.appointmentId}`)}>
                        <i class="bi bi-info-circle-fill"></i>
                      </Button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </Table>
          )}
        </Card.Body>
      </Card>
    </Container>
  );
};

export default PatientProfile;