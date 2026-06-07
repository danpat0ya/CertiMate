import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios'; 

const KakaoCallback = () => {
    const navigate = useNavigate();

    useEffect(() => {
        const code = new URL(window.location.href).searchParams.get("code");

        const sendKakaoCode = async () => {
            try {
                const response = await api.post('/auth/kakao', { code: code });
                console.log(response.data);
                alert("카카오 로그인에 성공했습니다!");
                
                // 🚨 [핵심] 카카오 로그인 성공 시에도 도장 및 시간 찍기!
                localStorage.setItem('isLoggedIn', 'true');
                localStorage.setItem('loginTime', new Date().getTime());
                
                window.location.href = '/'; // 홈으로 이동하며 상단바 갱신
            } catch (error) {
                console.error("카카오 로그인 실패:", error);
                alert("카카오 로그인에 실패했습니다.");
                navigate('/login');
            }
        };

        if (code) {
            sendKakaoCode();
        }
    }, [navigate]);

    return (
        <div className="min-h-screen flex items-center justify-center bg-[#EAECEF]">
            <h2 className="text-xl font-bold text-gray-600">카카오 로그인 처리 중입니다... 잠시만 기다려주세요. 🚀</h2>
        </div>
    );
};

export default KakaoCallback;