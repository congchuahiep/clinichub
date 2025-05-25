import { useState } from "react";
import { Alert, Button, Card, Form, Spinner } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../configs/AuthProvider";

const Login = () => {
  const info = [{
    title: "Tên đăng nhập",
    field: "username",
    type: "text"
  }, {
    title: "Mật khẩu",
    field: "password",
    type: "password"
  }];

  const [user, setUser] = useState({});
  const { login } = useAuth();

  const [errorMessage, setErrorMessage] = useState();
  const [loading, setLoading] = useState(false);

  const nav = useNavigate();

  const setState = (value, field) => {
    setUser({ ...user, [field]: value });
  }

  const handleLogin = async (e) => {
    e.preventDefault();
    setLoading(true);
    setErrorMessage(null);
    try {
      await login(user.username, user.password);
      nav("/");
    } catch (ex) {
      setErrorMessage(ex?.response?.data?.error || "Đăng nhập thất bại!");
    } finally {
      setLoading(false);
    }
  };

  return (
    <Card
      className="justify-content-center"
      style={{ maxWidth: 400, margin: "40px auto" }}
    >
      <Card.Body>
        <h3 className="text-center mt-4 mb-4">Đăng nhập</h3>
        {errorMessage && <Alert variant="danger">{errorMessage}</Alert>}
        <Form onSubmit={handleLogin}>
          {info.map(i =>
            <Form.Control
              value={user[i.field]}
              onChange={e => setState(e.target.value, i.field)}
              className="mt-3 mb-1"
              key={i.field}
              type={i.type}
              placeholder={i.title}
              required
            />)
          }
          <div className="d-grid">
            <Button type="submit" variant="primary" className="mt-3 mb-1">
              {loading === true
                ? <Spinner size="sm" />
                : <>Đăng nhập</>
              }
            </Button>
          </div>

        </Form>
      </Card.Body>
    </Card >
  )
}

export default Login;