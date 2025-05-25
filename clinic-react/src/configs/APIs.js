import axios from "axios";
import cookie from 'react-cookies'

const BASE_URL = process.env.REACT_APP_BASE_URL;

export const endpoints = {
    'patient-register': '/patient-register',
    'doctor-register': '/doctor-register',

    'login': '/login',
    'current-user': '/secure/profile',

    'hospitals': '/hospitals',
    'specialties': '/specialties',

    'doctors': '/doctors',
    'doctor-detail': (id) => `/doctors/${id}`,
    'doctor-reviews': (id) => `/doctors/${id}/reviews`,
    'doctor-check-review': (id) => `/secure/doctors/${id}/check-review`,

    'appointments': '/secure/appointments',
    'appointment-detail': (id) => `/secure/appointments/${id}`,
    'appointment-diagnosis': (id) => `/secure/appointments/${id}medical-records`,
    
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
