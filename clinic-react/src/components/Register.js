import { useRef, useState } from "react";
import { Alert, Button, Form, Tab, Tabs } from "react-bootstrap";
import Apis, { endpoints } from "../configs/APIs";
import MySpinner from "./layouts/MySpinner";
import { useNavigate } from "react-router-dom";

const RegisterForm = ({ userType }) => {
  const commonInfo = [
    { title: "Tên", field: "firstName", type: "text" },
    { title: "Họ và tên lót", field: "lastName", type: "text" },
    { title: "Số điện thoại", field: "phone", type: "tel" },
    { title: "Email", field: "email", type: "email" },
    { title: "Tên đăng nhập", field: "username", type: "text" },
    { title: "Mật khẩu", field: "password", type: "password" },
    { title: "Xác nhận mật khẩu", field: "confirm", type: "password" },
  ];

  const doctorExtraInfo = [
    { title: "Số giấy phép hành nghề", field: "licenseNumber", type: "text" },
    { title: "Chuyên khoa", field: "specialtyId", type: "number" },
    { title: "Ngày cấp", field: "issued", type: "date" },
    { title: "Ngày hết hạn", field: "expiry", type: "date" },
  ];

  const info = userType === "doctor" ? [...commonInfo, ...doctorExtraInfo] : commonInfo;

  const [user, setUser] = useState({});
  const avatar = useRef();
  const [msg, setMsg] = useState();
  const [loading, setLoading] = useState(false);
  const nav = useNavigate();

  const setState = (value, field) => {
    setUser({ ...user, [field]: value });
  };

  const register = async (e) => {
    e.preventDefault();

    if (user.password !== user.confirm) {
      setMsg("Mật khẩu KHÔNG khớp");
      return;
    }

    let form = new FormData();

    for (let key in user) {
      if (key !== "confirm") {
        if (key === "issued" || key === "expiry") {
          // Chuyển ngày sang ISO string, backend dễ parse
          form.append(key, new Date(user[key]).toISOString());
        } else {
          form.append(key, user[key]);
        }
      }
    }

    form.append("avatar", avatar.current.files[0]);
    form.append("userType", userType);

    try {
      setLoading(true);
      await Apis.post(endpoints["register"], form, {
        headers: { "Content-Type": "multipart/form-data" },
      });
      nav("/login");
    } catch (ex) {
      console.error(ex);
      setMsg("Có lỗi xảy ra khi đăng ký.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <h3 className="text-center text-success mt-1">
        ĐĂNG KÝ {userType === "patient" ? "Bệnh nhân" : "Bác sĩ"}
      </h3>

      {msg && <Alert variant="danger">{msg}</Alert>}

      <Form onSubmit={register}>
        {info.map((i) => (
          <Form.Control
            key={i.field}
            value={user[i.field] || ""}
            onChange={(e) => setState(e.target.value, i.field)}
            className="mt-3 mb-1"
            type={i.type}
            placeholder={i.title}
            required
          />
        ))}

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
    <Tabs defaultActiveKey="patient-register" id="fill-tab-example" className="mb-3" fill>
      <Tab eventKey="patient-register" title="Đăng ký bệnh nhân">
        <RegisterForm userType="patient" />
      </Tab>
      <Tab eventKey="doctor-register" title="Đăng ký bác sĩ">
        <RegisterForm userType="doctor" />
      </Tab>
    </Tabs>
  );
}

export default RegisterTabs;
