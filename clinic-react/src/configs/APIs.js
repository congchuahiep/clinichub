import axios from "axios";
import cookie from 'react-cookies';

const BASE_URL = process.env.REACT_APP_BASE_URL;

export const endpoints = {
    'patient-register': '/patient-register',
    'doctor-register': '/doctor-register',

    'login': '/login',
    'current-user': '/secure/profile',

    'health-profile': '/secure/health-records',
    'doctor-health-profile': (id) => `/secure/health-records/${id}`,
    'medical-records': '/secure/medical-records',

    'hospitals': '/hospitals',
    'specialties': '/specialties',
    'diseases': '/diseases',

    'doctors': '/doctors',
    'doctor-detail': (id) => `/doctors/${id}`,
    'doctor-reviews': (id) => `/doctors/${id}/reviews`,
    'doctor-check-review': (id) => `/secure/doctors/${id}/check-review`,
    'doctor-response': (id) => `/secure/reviews/${id}/response`,

    'appointments': '/secure/appointments',
    'check-taken-slots': '/secure/appointments/taken-slots',
    'appointment-detail': (id) => `/secure/appointments/${id}`,
    'appointment-diagnosis': (id) => `/secure/appointments/${id}/medical-records`,
    'appointment-cancel': (id) => `/secure/appointments/${id}/cancel`,
    'appointment-reschedule': (id) => `/secure/appointments/${id}/reschedule`,

    'review': (id) => `secure/doctors/${id}/reviews`
}

export const authApis = () => {
    return axios.create({
        baseURL: BASE_URL,
        headers: {
            'Authorization': `Bearer ${cookie.load('token')}`
        }
    })
}

export default axios.create({
    baseURL: BASE_URL
});
