import { BrowserRouter, Route, Routes } from "react-router-dom";
import Header from "./components/layouts/Header";
import Footer from "./components/layouts/Footer";
import Home from "./components/Home";
import Register from "./components/Register";
import PatientProfile from './components/PatientProfile.js';

import 'bootstrap/dist/css/bootstrap.min.css';
import { Container } from "react-bootstrap";
import Login from "./components/Login";
import { MyDispatcherContext, MyUserContext } from "./configs/MyContexts";
import { useReducer } from "react";
import MyUserReducer from "./reducers/MyUserReducer.js";
import Doctor from "./components/DoctorSearch.js";

function App() {

  const [user, dispatch] = useReducer(MyUserReducer, null);


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
              <Route path="/doctors" element={<Doctor />} /> 


            </Routes>
          </Container>

          <Footer />
        </BrowserRouter>
    </MyDispatcherContext.Provider>
    </MyUserContext.Provider>
  );
}

export default App;
