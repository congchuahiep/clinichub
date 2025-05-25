import { BrowserRouter, Route, Routes } from "react-router-dom";
import Home from "./components/Home";
import Footer from "./components/layouts/Footer";
import Header from "./components/layouts/Header";
import PatientProfile from './components/PatientProfile.js';
import Register from "./components/Register";

import 'bootstrap-icons/font/bootstrap-icons.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import { useEffect, useReducer } from "react";
import { Container } from "react-bootstrap";
import cookie from 'react-cookies';
import DoctorDetail from "./components/DoctorDetail.js";
import Doctor from "./components/DoctorList.js";
import Login from "./components/Login";
import { authApis, endpoints } from "./configs/APIs.js";
import { MyDispatcherContext, MyUserContext } from "./configs/MyContexts";
import MyUserReducer from "./reducers/MyUserReducer.js";
import DoctorList from "./components/DoctorList.js";
import AppointmentList from "./components/AppointmentList.js";


function App() {

  const [user, dispatch] = useReducer(MyUserReducer, null);

  // Load lại người dùng khi token vẫn tồn tại
  useEffect(() => {
    const loadUser = async () => {
      const token = cookie.load("token");
      if (token) {
        try {
          const res = await authApis().get(endpoints["current-user"]);
          dispatch({ type: "login", payload: res.data });
        } catch (err) {
          // Token hết hạn hoặc lỗi, xóa token
          cookie.remove("token");
          dispatch({ type: "logout" });
        }
      }
    };
    loadUser();
  }, []);

  return (
    <MyUserContext.Provider value={user}>
      <MyDispatcherContext.Provider value={dispatch}>
        <BrowserRouter>
          <Header />

          <Container className="p-5">
            <Routes>
              <Route path="/" element={<Home />} />
              <Route path="/register" element={<Register />} />
              <Route path="/login" element={<Login />} />
              <Route path="/patient-profile/:patientId" element={<PatientProfile />} />
              <Route path="/doctors" element={<DoctorList />} />
              <Route path="/doctors/:id" element={<DoctorDetail />} />
              <Route path="/appointments" element={<AppointmentList />} />

            </Routes>
          </Container>

          <Footer />
        </BrowserRouter>
      </MyDispatcherContext.Provider>
    </MyUserContext.Provider>
  );
}

export default App;
