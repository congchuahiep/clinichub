import { BrowserRouter, Route, Routes } from "react-router-dom";
import Home from "./components/Home";
import Header from "./components/layouts/Header";
import PatientProfile from './components/PatientProfile.js';
import Register from "./components/Register";

import 'bootstrap-icons/font/bootstrap-icons.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import { Container } from "react-bootstrap";
import AppointmentDetail from "./components/AppointmentDetail.js";
import AppointmentList from "./components/AppointmentList.js";
import DoctorDetail from "./components/DoctorDetail.js";
import DoctorList from "./components/DoctorList.js";
import Login from "./components/Login";
import { AuthProvider } from "./configs/AuthProvider.js";
import PrivateRoute from "./configs/PrivateRoute.js";


function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Header />

        <Container className="p-5">
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/register" element={<Register />} />
            <Route path="/login" element={<Login />} />

            <Route path="/profile" element={<PatientProfile />} />
            <Route path="/patient-profile/:id" element={<PatientProfile />} />

            <Route path="/doctors" element={<DoctorList />} />
            <Route path="/doctors/:id" element={<DoctorDetail />} />
            <Route path="/appointments" element={<PrivateRoute><AppointmentList /></PrivateRoute>} />
            <Route path="/appointments/:id" element={<PrivateRoute><AppointmentDetail /></PrivateRoute>} />

          </Routes>
        </Container>

      </AuthProvider>
    </BrowserRouter >
  );
}

export default App;
