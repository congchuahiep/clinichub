import { useContext, useEffect, useState } from "react";
import { Button, Col, Container, Form, Nav, Navbar, NavDropdown, Row } from "react-bootstrap";
import { Link, useNavigate } from "react-router-dom";
import Apis, { endpoints } from "../../configs/APIs";
import { MyDispatcher, MyDispatcherContext, MyUserContext } from "../../configs/MyContexts";

const Header = () => {
  const [kw, setKw] = useState();
  const nav = useNavigate();
  const user = useContext(MyUserContext);
  const dispatch = useContext(MyDispatcherContext);

  const search = (e) => {
    e.preventDefault();
    nav(`/?kw=${kw}`);
  }

  // const handleLogout = () => {
  //   dispatch({ "type": "logout " });
  // }

  return (
    <Navbar expand="lg" className="bg-body-tertiary">
      <Container>
        <Navbar.Brand href="#home">Phòng khám K&H</Navbar.Brand>
        <Navbar.Toggle aria-controls="basic-navbar-nav" />
        <Navbar.Collapse id="basic-navbar-nav">
          <Nav className="me-auto">
            <Link to="/" className="nav-link">Trang chủ</Link>
          </Nav>
          <Form inline onSubmit={search}>
            <Row>
              {user ?
                <Col>
                  <Button variant="light" onClick={() => dispatch({ "type": "logout" })}>Đăng xuất</Button>
                </Col>
              :
                <Col>
                  <Link to="/login" className="p-2">Đăng nhập</Link>
                  <Link to="/register">Đăng ký</Link>
                </Col>
              }
            </Row>
          </Form>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  )
}

export default Header;