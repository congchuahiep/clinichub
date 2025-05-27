import { useEffect, useRef, useState } from "react";
import { Alert, Button, Col, Container, Form, InputGroup, Row, Tab, Tabs } from "react-bootstrap";
import Apis, { endpoints } from "../configs/APIs";
import MySpinner from "./layouts/MySpinner";
import { useNavigate } from "react-router-dom";

const RegisterForm = ({ userType }) => {
  const [user, setUser] = useState({
    username: "",
    password: "",
    confirmPassword: "",
    email: "",
    phone: "",
    firstName: "",
    lastName: "",
    gender: "",
    birthDate: "",
    address: "",
    // Các trường cho bác sĩ
    licenseNumber: "",
    specialtyId: "",
    hospitalId: "",
    issuedDate: "",
    expiryDate: ""
  });
  const [hospitals, setHospitals] = useState([]);
  const [specialties, setSpecialties] = useState([]);
  const avatar = useRef();

  const [message, setMessage] = useState();
  const [loading, setLoading] = useState(false);
  const [validated, setValidated] = useState(false);
  const [fieldErrors, setFieldErrors] = useState({});

  const nav = useNavigate();

  const setState = (value, field) => {
    setUser(prev => ({ ...prev, [field]: value }));
  };

  const loadHospital = async () => {
    try {
      const res = await Apis.get(endpoints["hospitals"]);
      setHospitals(res.data);
    } catch (ex) {
      setMessage("Không thể tải danh sách bệnh viện");
    }
  };

  const loadSpecialty = async () => {
    try {
      const res = await Apis.get(endpoints["specialties"]);
      setSpecialties(res.data);
    } catch (ex) {
      setMessage("Không thể tải danh sách chuyên khoa");
    }
  };

  useEffect(() => {
    loadHospital();
    loadSpecialty();
  }, []);

  // Format ngày sang yyyy-MM-dd
  const formatDate = (dateValue) => {
    if (!dateValue) return "";
    if (typeof dateValue === "string") return dateValue;
    const yyyy = dateValue.getFullYear();
    const mm = (dateValue.getMonth() + 1).toString().padStart(2, "0");
    const dd = dateValue.getDate().toString().padStart(2, "0");
    return `${yyyy}-${mm}-${dd}`;
  };

  const handleRegister = async (e) => {
    e.preventDefault();

    // Kiểm tra mật khẩu xác nhận
    if (user.password !== user.confirmPassword) {
      setMessage("Mật khẩu KHÔNG khớp");
      setValidated(true);
      return;
    }

    const form = e.currentTarget;
    if (form.checkValidity() === false) {
      e.preventDefault();
      e.stopPropagation();
      setValidated(true);
      return;
    }

    setValidated(true);

    let formData = new FormData();

    // Thêm các trường chung
    formData.append("username", user.username);
    formData.append("password", user.password);
    formData.append("confirmPassword", user.password);
    formData.append("password", user.password);
    formData.append("email", user.email);
    formData.append("phone", user.phone);
    formData.append("firstName", user.firstName);
    formData.append("lastName", user.lastName);
    formData.append("gender", user.gender);
    formData.append("birthDate", formatDate(user.birthDate));
    formData.append("address", user.address || "");

    // Thêm avatar nếu có
    if (avatar.current && avatar.current.files.length > 0) {
      formData.append("avatar", avatar.current.files[0]);
    }

    // Nếu là bác sĩ, thêm các trường giấy phép
    if (userType === "doctor") {
      formData.append("licenseNumber", user.licenseNumber);
      formData.append("specialtyId", user.specialtyId);
      formData.append("hospitalId", user.hospitalId);
      formData.append("issuedDate", formatDate(user.issuedDate));
      formData.append("expiryDate", formatDate(user.expiryDate));
    }

    formData.append("userType", userType);

    try {
      setLoading(true);
      const apiEndpoint = userType === "patient" ? endpoints["patient-register"] : endpoints["doctor-register"];
      await Apis.post(apiEndpoint, formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });

      nav("/login?msg=Đăng ký thành công");
    } catch (ex) {
      let errorMessage = "";
      let errors = {};
      if (ex.response && ex?.response.data) {
        if (typeof ex.response.data === "object") {
          errors = ex.response.data;
          errorMessage = Object.entries(errors)
            .map(([field, msg]) => `${field}: ${msg}`)
            .join(" | ");
        } else {
          errorMessage = ex.response.data;
        }
      }
      console.log(ex);
      setMessage("Có lỗi xảy ra khi đăng ký: " + (errorMessage ? " " + errorMessage : ""));
      setFieldErrors(errors);
      setValidated(false);
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <h3 className="text-center mt-1">
        Đăng ký {userType === "patient" ? "bệnh nhân" : "bác sĩ"}
      </h3>

      <Form onSubmit={handleRegister}>
        <Form.Group className="mt-3 mb-1">
          <Form.Label>Tên đăng nhập</Form.Label>
          <Form.Control
            required
            value={user.username}
            onChange={e => setState(e.target.value, "username")}
            placeholder="Tên đăng nhập"
            isInvalid={!!fieldErrors.username}
          />
          <Form.Control.Feedback type="invalid">
            {fieldErrors.username}
          </Form.Control.Feedback>
        </Form.Group>

        <Form.Group className="mt-3 mb-1">
          <Form.Label>Mật khẩu</Form.Label>
          <Form.Control
            required
            type="password"
            value={user.password}
            onChange={e => setState(e.target.value, "password")}
            placeholder="Mật khẩu"
            isInvalid={!!fieldErrors.password}
          />
          <Form.Control.Feedback type="invalid">
            {fieldErrors.password}
          </Form.Control.Feedback>
        </Form.Group>

        <Form.Group className="mt-3 mb-1">
          <Form.Label>Xác nhận mật khẩu</Form.Label>
          <Form.Control required type="password" value={user.confirmPassword} onChange={e => setState(e.target.value, "confirmPassword")} placeholder="Nhập lại mật khẩu" />
          <Form.Control.Feedback type="invalid">
            {fieldErrors.confirmPassword}
          </Form.Control.Feedback>
        </Form.Group>

        <Form.Group className="mt-3 mb-1">
          <Form.Label>Email</Form.Label>
          <Form.Control required type="email" value={user.email} onChange={e => setState(e.target.value, "email")} placeholder="Email" />
        </Form.Group>

        <Form.Group className="mt-3 mb-1">
          <Form.Label>Số điện thoại</Form.Label>
          <Form.Control required value={user.phone} onChange={e => setState(e.target.value, "phone")} placeholder="Số điện thoại" />
        </Form.Group>

        <Form.Group className="mt-3 mb-1">
          <Form.Label>Họ và tên</Form.Label>
          <InputGroup>
            <Form.Control required value={user.lastName} onChange={e => setState(e.target.value, "lastName")} placeholder="Họ và tên lót" />
            <Form.Control required value={user.firstName} onChange={e => setState(e.target.value, "firstName")} placeholder="Tên" />
          </InputGroup>
        </Form.Group>

        <Form.Group className="mt-3 mb-1">
          <Form.Label>Giới tính</Form.Label>
          <Form.Select
            value={user.gender || ""}
            onChange={e => setState(e.target.value, "gender")}
            required
            isInvalid={!!fieldErrors.gender}
          >
            <option value="">Chọn giới tính</option>
            <option value="male">Nam</option>
            <option value="female">Nữ</option>
          </Form.Select>
          <Form.Control.Feedback type="invalid">
            {fieldErrors.gender}
          </Form.Control.Feedback>
        </Form.Group>

        <Form.Group className="mt-3 mb-1">
          <Form.Label>Ngày sinh</Form.Label>
          <Form.Control required value={user.birthDate} onChange={e => setState(e.target.value, "birthDate")} type="date" placeholder="Chọn ngày sinh của bạn" />
        </Form.Group>

        <Form.Group className="mt-3 mb-1">
          <Form.Label>Địa chỉ</Form.Label>
          <Form.Control value={user.address} onChange={e => setState(e.target.value, "address")} placeholder="Địa chỉ (không bắt buộc)" />
        </Form.Group>

        <Form.Group className="mt-3 mb-1">
          <Form.Label>Ảnh đại diện</Form.Label>
          <Form.Control
            ref={avatar}
            type="file"
            placeholder="Ảnh đại diện"
            accept="image/*"
            required
          />
        </Form.Group>

        {userType === "doctor" && (
          <>
            <Form.Group className="mt-3 mb-1">
              <Form.Label>Giấy phép hành nghề</Form.Label>
              <Form.Control required value={user.licenseNumber} onChange={e => setState(e.target.value, "licenseNumber")} placeholder="Giấy phép hành nghề..." />
            </Form.Group>

            <Form.Group className="mt-3 mb-1">
              <Form.Label>Chuyên khoa</Form.Label>
              <Form.Select
                value={user.specialtyId || ""}
                onChange={e => setState(e.target.value, "specialtyId")}
                required
              >
                <option value="" disabled>Chọn chuyên khoa</option>
                {specialties.map((specialty) =>
                  <option key={specialty.id} value={specialty.id}>{specialty.name}</option>
                )}
              </Form.Select>
            </Form.Group>

            <Form.Group className="mt-3 mb-1">
              <Form.Label>Bệnh viện</Form.Label>
              <Form.Select
                value={user.hospitalId || ""}
                onChange={e => setState(e.target.value, "hospitalId")}
                required
              >
                <option value="" disabled>Chọn bệnh viện</option>
                {hospitals.map((hospital) =>
                  <option key={hospital.id} value={hospital.id}>{hospital.name}</option>
                )}
              </Form.Select>
            </Form.Group>

            <Form.Group className="mt-3 mb-1">
              <Form.Label>Ngày cấp giấy phép</Form.Label>
              <Form.Control required value={user.issuedDate} type="date" onChange={e => setState(e.target.value, "issuedDate")} placeholder="Ngày cấp..." />
            </Form.Group>

            <Form.Group className="mt-3 mb-1">
              <Form.Label>Ngày giấy phép hết hạn</Form.Label>
              <Form.Control required value={user.expiryDate} type="date" onChange={e => setState(e.target.value, "expiryDate")} placeholder="Ngày hết hạn..." />
            </Form.Group>
          </>
        )}

        {message && <Alert variant="danger">{message}</Alert>}

        {loading ? (
          <MySpinner />
        ) : (
          <Button type="submit" variant="success" className="mt-3 mb-1">
            Đăng ký
          </Button>
        )}
      </Form>
    </>
  );
};

function RegisterTabs() {
  return (
    <Container>
      <Row className="justify-content-center">
        <Col md="auto" xl="6" className="rounded p-0 border">
          <Tabs defaultActiveKey="patient-register" id="fill-tab-example" className="mb-3" fill>
            <Tab eventKey="patient-register" title="Đăng ký bệnh nhân" className="p-3">
              <RegisterForm userType="patient" />
            </Tab>
            <Tab eventKey="doctor-register" title="Đăng ký bác sĩ" className="p-3">
              <RegisterForm userType="doctor" />
            </Tab>
          </Tabs>
        </Col>
      </Row>
    </Container>
  );
}

export default RegisterTabs;