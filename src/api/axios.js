import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8080/api', // 백엔드 주소
    withCredentials: true, // 🚨 쿠키(JWT)를 자동으로 실어 나르는 마법의 옵션
});

export default api;