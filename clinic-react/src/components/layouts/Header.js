import { useContext, useEffect, useState } from "react";
import { Button, Col, Container, Form, Image, Nav, Navbar, NavDropdown, Row, Stack } from "react-bootstrap";
import { useNavigate, NavLink } from "react-router-dom";
import Apis, { endpoints } from "../../configs/APIs";
import { MyDispatcher, MyDispatcherContext, MyUserContext } from "../../configs/MyContexts";

const Header = () => {
  const nav = useNavigate();
  const user = useContext(MyUserContext);
  const dispatch = useContext(MyDispatcherContext);

  return (
    <Navbar expand="lg" className="bg-body-tertiary">
      <Container>
        <Navbar.Brand href="#home">Clinic Hub</Navbar.Brand>
        <Navbar.Toggle aria-controls="basic-navbar-nav" />
        <Navbar.Collapse id="basic-navbar-nav">
          <Nav className="me-auto" variant="underline">
            <Nav.Link as={NavLink} to="/" className="nav-link">Trang chủ</Nav.Link>
            <Nav.Link as={NavLink} to="/doctors" className="nav-link">Tìm kiếm bác sĩ</Nav.Link>
          </Nav>
          <Nav>
            {user ?
              <Stack direction="horizontal" gap={3} className="align-items-center">
                <Image
                  width={32}
                  height={32}
                  src={user.avatar || "/no-avatar.jpg"}
                  className="bg-light"
                  style={{ objectFit: "cover" }}
                  roundedCircle
                  onClick={() => { console.log(user) }}
                />
                <div>
                {user.lastName} {user.firstName}
                </div>
                <Button variant="outline-danger" onClick={() => dispatch({ "type": "logout" })}>
                  Đăng xuất
                </Button>
              </Stack>
              :
              <>
                <Button as={NavLink} to="/login" variant="outline-primary" className="me-2">
                  Đăng nhập
                </Button>
                <Button as={NavLink} to="/register" variant="primary">
                  Đăng ký
                </Button>
              </>
            }
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  )
}

export default Header;