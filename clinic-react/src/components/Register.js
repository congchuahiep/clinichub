import { useEffect, useRef, useState } from "react";
import { Alert, Button, Col, Container, Form, InputGroup, Row, Tab, Tabs } from "react-bootstrap";
import Apis, { endpoints } from "../configs/APIs";
import MySpinner from "./layouts/MySpinner";
import { useNavigate } from "react-router-dom";

const RegisterForm = ({ userType }) => {
  const commonInfo = [
    { title: "Tên", field: "firstName", type: "text", required: true },
    { title: "Họ và tên lót", field: "lastName", type: "text", required: true },
    { title: "Số điện thoại", field: "phone", type: "tel", required: true },
    { title: "Email", field: "email", type: "email", required: true },
    { title: "Tên đăng nhập", field: "username", type: "text", required: true },
    { title: "Mật khẩu", field: "password", type: "password", required: true },
    { title: "Xác nhận mật khẩu", field: "confirmPassword", type: "password", required: true },
    { title: "Ngày sinh", field: "birthDate", type: "date", required: true },
  ];

  const doctorExtraInfo = [
    { title: "Số giấy phép hành nghề", field: "licenseNumber", type: "text", required: true },
    { title: "Chuyên khoa", field: "specialtyId", type: "number", required: true },
    { title: "Ngày cấp", field: "issuedDate", type: "date", required: true },
    { title: "Ngày hết hạn", field: "expiryDate", type: "date", required: true },
    { title: "Bệnh viện khám", field: "hospitalId", type: "text", required: true },
  ];

  const info = userType === "doctor" ? [...commonInfo, ...doctorExtraInfo] : commonInfo;

  const [user, setUser] = useState({});
  const [hospitals, setHospitals] = useState([]);
  const [specialties, setSpecialties] = useState([]);
  const avatar = useRef();

  const [message, setMessage] = useState();
  const [loading, setLoading] = useState(false);
  const [validated, setValidated] = useState(false);

  const nav = useNavigate();

  const setState = (value, field) => {
    setUser({ ...user, [field]: value });
  };

  const loadHospital = async () => {
    await Apis.get(endpoints["hospitals"])
      .then((response) => {
        console.log(response);
        setHospitals(response.data);
      })
      .catch((ex) => {
        console.error(ex);
      })
  }

  const loadSpecialty = async () => {
    await Apis.get(endpoints["specialties"])
      .then((response) => {
        console.log(response);
        setSpecialties(response.data);
      })
      .catch((ex) => {
        console.error(ex);
      })
  }

  useEffect(() => {
    loadHospital();
    loadSpecialty();
  }, []);

  // Format ngày sang yyyy-MM-dd
  const formatDate = (dateValue) => {
    if (!dateValue) return "";
    if (typeof dateValue === "string") return dateValue; // nếu đã là string thì giữ nguyên
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
      return;
    }

    const form = e.currentTarget;
    if (form.checkValidity() === false) {
      e.preventDefault();
      e.stopPropagation();
    }

    setValidated(true);

    let formData = new FormData();

    for (let key in user) {
      if (key !== "confirm") {
        if (key === "birthDate" || key === "issued" || key === "expiry") {
          formData.append(key, formatDate(user[key]));
        } else {
          formData.append(key, user[key]);
        }
      }
    }

    if (avatar.current.files.length > 0) {
      formData.append("avatar", avatar.current.files[0]);
    }

    formData.append("userType", userType);

    try {
      setLoading(true);
      const apiEndpoint = userType === "patient" ? endpoints["patient-register"] : endpoints["doctor-register"];
      await Apis.post(apiEndpoint, formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });
      nav("/login");

    } catch (ex) {
      console.error(ex);

      const errorMessage =
        ex.response && ex.response.data
          ? JSON.stringify(ex.response.data)
          : "";

      setMessage("Có lỗi xảy ra khi đăng ký." + (errorMessage ? " " + errorMessage : ""));
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <h3 className="text-center mt-1">
        Đăng ký {userType === "patient" ? "bệnh nhân" : "bác sĩ"}
      </h3>

      {message && <Alert variant="danger">{message}</Alert>}

      <Form noValidate validated={validated} onSubmit={handleRegister}>
        {/* {info.map((i) => (
          <Form.Control
            key={i.field}
            value={user[i.field] || ""}
            onChange={(e) => setState(e.target.value, i.field)}
            className="mt-3 mb-1"
            type={i.type}
            placeholder={i.title}
            required={i.required}
          />
        ))} */}

        <Form.Group className="mt-3 mb-1">
          <Form.Label>Họ và tên</Form.Label>
          <InputGroup>
            <Form.Control required value={user.lastName} placeholder="Họ" />
            <Form.Control required value={user.firstName} placeholder="Tên" />
          </InputGroup>
          <Form.Control.Feedback type="invalid">Hãy ghi đầy đủ họ tên của bạn!</Form.Control.Feedback>
        </Form.Group>

        <Form.Group className="mt-3 mb-1">
          <Form.Label>Số điện thoại</Form.Label>
          <Form.Control required value={user.phone} placeholder="Số điện thoại" />
        </Form.Group>

        <Form.Group className="mt-3 mb-1">
          <Form.Label>Email</Form.Label>
          <Form.Control required type="email" value={user.email} placeholder="Email" />
        </Form.Group>

        <Form.Group className="mt-3 mb-1">
          <Form.Label>Tên đăng nhập</Form.Label>
          <Form.Control required value={user.username} placeholder="Tên đăng nhập" />
        </Form.Group>

        <Form.Group className="mt-3 mb-1">
          <Form.Label>Mật khẩu</Form.Label>
          <Form.Control required type="password" value={user.password} placeholder="Mật khẩu" />
        </Form.Group>

        <Form.Group className="mt-3 mb-1">
          <Form.Label>Xác nhận mật khẩu</Form.Label>
          <Form.Control required type="password" value={user.confirmPassword} placeholder="Nhập lại mật khẩu" />
        </Form.Group>

        <Form.Group className="mt-3 mb-1">
          <Form.Label>Ngày sinh</Form.Label>
          <Form.Control required value={user.birthDate} type="date" placeholder="Chọn ngày sinh của bạn" />
        </Form.Group>

        <Form.Group className="mt-3 mb-1">
          <Form.Label>Giới tính</Form.Label>
          <Form.Select
            value={user.gender || ""}
            onChange={(e) => setState(e.target.value, "gender")}
            required
          >
            <option value=""><i>Chọn giới tính</i></option>
            <option value="male">Nam</option>
            <option value="female">Nữ</option>
          </Form.Select>
        </Form.Group>
        
        <Form.Control
          ref={avatar}
          className="mt-3 mb-1"
          type="file"
          placeholder="Ảnh đại diện"
          required
        />

        {userType === "doctor" && <>

          <Form.Group className="mt-3 mb-1">
            <Form.Label>Bệnh viện</Form.Label>
            <Form.Select
              value={user.hospitalId || ""}
              onChange={(e) => setState(e.target.value, "hospitalId")}
              required
            >
              <option value="" disabled>Chọn bệnh viện</option>
              {hospitals.map((hospital) =>
                <option
                  key={hospital.id}
                  value={hospital.id}
                >
                  {hospital.name}
                </option>
              )}
            </Form.Select>
            <Form.Control.Feedback type="invalid">Hãy chọn bệnh viện bạn làm!</Form.Control.Feedback>
          </Form.Group>

          <Form.Group className="mt-3 mb-1">
            <Form.Label>Chuyên khoa</Form.Label>
            <Form.Select
              value={user.specialtyId || ""}
              onChange={(e) => setState(e.target.value, "specialtyId")}
              required
            >
              <option value="" disabled>Chọn chuyên khoa</option>
              {specialties.map((specialty) =>
                <option
                  key={specialty.id}
                  value={specialty.id}
                >
                  {specialty.name}
                </option>
              )}
            </Form.Select>
            <Form.Control.Feedback type="invalid">Hãy chọn chuyên khoa của bạn!</Form.Control.Feedback>
          </Form.Group>
        </>}

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
    <Container >

      <Row className="justify-content-center">
        <Col md="auto" xl="6" className="rounded p-0 border">
          <Tabs defaultActiveKey="patient-register" id="fill-tab-example" className="mb-3" fill>
            <Tab eventKey="patient-register" title="Đăng ký bệnh nhân" className="p-3" >
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
