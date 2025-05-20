import Tab from 'react-bootstrap/Tab';
import Tabs from 'react-bootstrap/Tabs';

function FillExample() {
  return (
    <Tabs
      defaultActiveKey="profile"
      id="fill-tab-example"
      className="mb-3"
      fill
    >
      <Tab eventKey="patient-register" title="Đăng ký bệnh nhân">
        tab content for patient
      </Tab>
      <Tab eventKey="doctor-register" title="Đăng ký bác sĩ">
        tab content for patient
      </Tab>
    </Tabs>
  );
}

export default FillExample;