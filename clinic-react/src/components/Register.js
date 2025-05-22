import { useRef, useState } from "react";
import { Alert, Button, Col, Container, Form, Row, Tab, Tabs } from "react-bootstrap";
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
    { title: "Ngày cấp", field: "issued", type: "date", required: true },
    { title: "Ngày hết hạn", field: "expiry", type: "date", required: true },
  ];

  const info = userType === "doctor" ? [...commonInfo, ...doctorExtraInfo] : commonInfo;

  const [user, setUser] = useState({});
  const avatar = useRef();

  const [message, setMessage] = useState();
  const [loading, setLoading] = useState(false);

  const nav = useNavigate();

  const setState = (value, field) => {
    setUser({ ...user, [field]: value });
  };

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
    if (user.password !== user.confirm) {
      setMessage("Mật khẩu KHÔNG khớp");
      return;
    }

    // Kiểm tra các trường bắt buộc
    for (const field of commonInfo) {
      if (field.required && !user[field.field]) {
        setMessage(`Vui lòng nhập trường "${field.title}"`);
        return;
      }
    }
    if (userType === "doctor") {
      for (const field of doctorExtraInfo) {
        if (field.required && !user[field.field]) {
          setMessage(`Vui lòng nhập trường "${field.title}"`);
          return;
        }
      }
    }
    if (!user.gender) {
      setMessage("Vui lòng chọn giới tính");
      return;
    }

    let form = new FormData();

    for (let key in user) {
      if (key !== "confirm") {
        if (key === "birthDate" || key === "issued" || key === "expiry") {
          form.append(key, formatDate(user[key]));
        } else {
          form.append(key, user[key]);
        }
      }
    }

    if (avatar.current.files.length > 0) {
      form.append("avatar", avatar.current.files[0]);
    }

    form.append("userType", userType);

    try {
      setLoading(true);
      // Gọi đúng API backend
      const apiEndpoint = userType === "patient" ? endpoints["patient-register"] : endpoints["doctor-register"];
      await Apis.post(apiEndpoint, form, {
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

      <Form onSubmit={handleRegister}>
        {info.map((i) => (
          <Form.Control
            key={i.field}
            value={user[i.field] || ""}
            onChange={(e) => setState(e.target.value, i.field)}
            className="mt-3 mb-1"
            type={i.type}
            placeholder={i.title}
            required={i.required}
          />
        ))}

        <Form.Group className="mt-3 mb-1">
          <Form.Label>Giới tính</Form.Label>
          <Form.Select
            value={user.gender || ""}
            onChange={(e) => setState(e.target.value, "gender")}
            required
          >
            <option value="">-- Chọn giới tính --</option>
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
