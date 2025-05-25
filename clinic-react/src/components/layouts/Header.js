import { useContext, useEffect, useState } from "react";
import { Button, Col, Container, Dropdown, Form, Image, Nav, Navbar, NavDropdown, Row, Stack } from "react-bootstrap";
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
            {
              user &&
              <Nav.Link as={NavLink} to="/appointments" className="nav-link">
                Lịch khám bệnh
              </Nav.Link>
            }

          </Nav>
          <Nav>
            {user ?
              <Stack direction="horizontal" gap={3} className="align-items-center">
                <Dropdown align="end">
                  <Dropdown.Toggle
                    variant="light"
                    id="dropdown-basic"
                    className="d-flex align-items-center"
                    style={{ padding: "4px 12px" }}
                  >
                    <Image
                      width={32}
                      height={32}
                      src={user.avatar || "/no-avatar.jpg"}
                      className="bg-light me-2"
                      style={{ objectFit: "cover" }}
                      roundedCircle
                    />
                    <span>{user.lastName} {user.firstName}</span>
                  </Dropdown.Toggle>

                  <Dropdown.Menu>
                    <Dropdown.Item onClick={() => console.log(user)}>
                      Xem thông tin cá nhân
                    </Dropdown.Item>
                    <Dropdown.Item
                      onClick={() => dispatch({ "type": "logout" })}
                      className="text-danger"
                    >Đăng xuất</Dropdown.Item>
                  </Dropdown.Menu>
                </Dropdown>
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