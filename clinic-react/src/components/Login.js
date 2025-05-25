import { useContext, useRef, useState } from "react";
import { Alert, Button, Card, Col, Form, Row, Spinner } from "react-bootstrap";
import Apis, { authApis, endpoints } from "../configs/APIs";
import MySpinner from "./layouts/MySpinner";
import { useNavigate } from "react-router-dom";
import cookie from 'react-cookies'
import { MyDispatcherContext } from "../configs/MyContexts";

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

  const [errorMessage, setErrorMessage] = useState();
  const [loading, setLoading] = useState(false);

  const nav = useNavigate();

  const dispatch = useContext(MyDispatcherContext);

  const setState = (value, field) => {
    setUser({ ...user, [field]: value });
  }

  const login = async (e) => {
    e.preventDefault();
    try {
      setLoading(true);
      let res = await Apis.post(endpoints['login'], {
        ...user
      });

      cookie.save('token', res.data.token);

      let u = await authApis().get(endpoints['current-user']);
      console.info(u.data);

      dispatch({
        "type": "login",
        "payload": u.data
      });

      nav("/");
    } catch (ex) {
      console.error(ex.response);
      setErrorMessage(ex.response.data.error)
    } finally {
      setLoading(false);
    }
  }

  return (
    <Card
      className="justify-content-center"
      style={{ maxWidth: 400, margin: "40px auto" }}
    >
      <Card.Body>
        <h3 className="text-center mt-4 mb-4">Đăng nhập</h3>
        {errorMessage && <Alert variant="danger">{errorMessage}</Alert>}
        <Form onSubmit={login}>
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