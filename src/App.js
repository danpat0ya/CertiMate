import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';

// Components
import Navbar from './components/Navbar';

// Pages
import Home from './pages/Home';
import Study from './pages/Study';
import Calendar from './pages/Calendar';
import Community from './pages/Community';
import Profile from './pages/Profile';
import Register from './pages/Register';
import Login from './pages/Login';
import Admin from './pages/Admin';
import KakaoCallback from './pages/KakaoCallback';
import FindPassword from './pages/FindPassword'; // 🚨 우리가 만든 비밀번호 찾기 추가

function App() {
  return (
    <Router>
      <div className="min-h-screen bg-slate-50 text-slate-800 font-sans antialiased flex flex-col">
        <Navbar />
        <main className="flex-1 w-full flex flex-col">
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/study" element={<Study />} />
            <Route path="/calendar" element={<Calendar />} />
            <Route path="/community" element={<Community />} />
            <Route path="/profile" element={<Profile />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/admin" element={<Admin />} />
            {/* 🚨 비밀번호 찾기 라우터 연결 */}
            <Route path="/find-password" element={<FindPassword />} />
            <Route path="/auth/kakao/callback" element={<KakaoCallback />} />
          </Routes>
        </main>
      </div>
    </Router>
  );
}

export default App;