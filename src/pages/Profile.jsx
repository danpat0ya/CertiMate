import React from 'react';
import { 
  User, Award, BookOpen, Bookmark, 
  Settings, Bell, ChevronRight, PieChart,
  Calendar, CheckCircle2, Clock 
} from 'lucide-react';

const Profile = () => {
  const userInfo = {
    name: "유성찬",
    university: "서일대학교",
    major: "소프트웨어 개발",
    personality: "끊임없는 도전과 결정 장애",
    status: "3학년 재학 중 (2027년 1월 졸업 예정)"
  };

  return (
    <div className="min-h-screen bg-[#EAECEF] pb-20 font-sans">
      <header className="bg-white border-b border-gray-200 pt-16 pb-12 px-6">
        <div className="max-w-5xl mx-auto flex flex-col md:flex-row items-center gap-10">
          <div className="relative">
            <div className="w-32 h-32 rounded-[40px] bg-gradient-to-tr from-[#3478B8] to-[#3BAA7D] flex items-center justify-center text-white text-4xl font-black shadow-2xl shadow-[#3478B8]/20">
              {userInfo.name[0]}
            </div>
            <button className="absolute -bottom-2 -right-2 bg-white p-2 rounded-xl shadow-lg border border-gray-100 text-gray-400 hover:text-[#3478B8]">
              <Settings size={18} />
            </button>
          </div>
          
          <div className="flex-1 text-center md:text-left">
            <div className="flex items-center justify-center md:justify-start space-x-3 mb-2">
              <h2 className="text-3xl font-black text-[#4A4F58]">{userInfo.name}</h2>
              <span className="bg-[#3478B8]/10 text-[#3478B8] text-[10px] font-black px-2 py-1 rounded uppercase tracking-tighter">Premium Member</span>
            </div>
            <p className="text-[#3BAA7D] font-bold text-sm mb-4">{userInfo.university} | {userInfo.major}</p>
            <div className="flex flex-wrap justify-center md:justify-start gap-2">
              <span className="text-[11px] bg-gray-100 text-gray-500 px-3 py-1.5 rounded-lg font-bold"># {userInfo.personality}</span>
              <span className="text-[11px] bg-gray-100 text-gray-500 px-3 py-1.5 rounded-lg font-bold"># {userInfo.status}</span>
            </div>
          </div>

          <div className="flex space-x-4">
            <button className="p-4 bg-white border border-gray-200 rounded-2xl shadow-sm hover:shadow-md transition group">
              <Bell size={20} className="text-gray-400 group-hover:text-[#D9A23A]" />
            </button>
            <button className="px-8 py-4 bg-[#3478B8] text-white rounded-2xl font-black text-sm shadow-xl shadow-[#3478B8]/20 hover:bg-[#2e69a3] transition">
              프로필 수정
            </button>
          </div>
        </div>
      </header>

      <main className="max-w-5xl mx-auto px-6 mt-12 space-y-8">
        <div className="grid grid-cols-2 md:grid-cols-4 gap-6">
          <StatCard icon={<Award className="text-[#3478B8]" />} label="보유 자격증" value="1" />
          <StatCard icon={<Bookmark className="text-[#D9A23A]" />} label="스크랩 종목" value="12" />
          <StatCard icon={<PieChart className="text-[#3BAA7D]" />} label="CBT 정답률" value="84%" />
          <StatCard icon={<Clock className="text-gray-400" />} label="학습 시간" value="128h" />
        </div>

        <div className="grid lg:grid-cols-3 gap-8">
          <div className="lg:col-span-2 space-y-6">
            <section className="bg-white p-8 rounded-[32px] border border-gray-100 shadow-sm">
              <h3 className="text-lg font-black mb-6 flex items-center">
                <CheckCircle2 className="mr-3 text-[#3BAA7D]" size={20} /> 보유 자격증
              </h3>
              <div className="p-6 bg-gray-50 rounded-2xl border border-gray-100 flex justify-between items-center group cursor-pointer hover:border-[#3BAA7D] transition">
                <div>
                  <h4 className="font-bold text-[#4A4F58]">정보처리산업기사</h4>
                  <p className="text-[10px] text-gray-400 font-bold mt-1 uppercase text-left">Certified by Q-Net</p>
                </div>
                <div className="text-right">
                  <span className="text-[10px] font-black text-[#3BAA7D] uppercase bg-[#3BAA7D]/10 px-2 py-1 rounded">Verified</span>
                </div>
              </div>
            </section>

            <section className="bg-white p-8 rounded-[32px] border border-gray-100 shadow-sm">
              <h3 className="text-lg font-black mb-6 flex items-center">
                <Calendar className="mr-3 text-[#3478B8]" size={20} /> 준비 중인 시험 (D-Day)
              </h3>
              <div className="p-6 bg-[#3478B8]/5 border border-[#3478B8]/20 rounded-2xl flex justify-between items-center">
                <div className="flex items-center space-x-4">
                  <div className="w-1.5 h-10 bg-[#D9A23A] rounded-full"></div>
                  <div className="text-left">
                    <h4 className="font-bold text-[#4A4F58]">산업안전산업기사 (필기)</h4>
                    <p className="text-[10px] text-gray-400 font-bold mt-1 uppercase">Exam Date: 2026.05.20</p>
                  </div>
                </div>
                <div className="text-right">
                  <span className="text-xl font-black text-[#D9A23A]">D-11</span>
                </div>
              </div>
            </section>
          </div>

          <div className="space-y-6">
            <section className="bg-[#4A4F58] p-8 rounded-[32px] text-white shadow-xl text-left">
              <h3 className="text-lg font-bold mb-6 flex items-center">
                <Bookmark className="mr-3 text-[#D9A23A]" size={18} /> 최근 스크랩
              </h3>
              <div className="space-y-4">
                <ScrapItem title="리눅스마스터 2급" date="2일 전" />
                <ScrapItem title="데이터분석준전문가" date="5일 전" />
                <ScrapItem title="네트워크관리사 2급" date="1주 전" />
              </div>
              <button className="w-full mt-8 py-3 rounded-xl bg-white/10 hover:bg-white/20 text-[10px] font-black uppercase tracking-widest transition">
                전체 스크랩 보기
              </button>
            </section>

            {/* 에러가 발생했던 학습 리포트 섹션입니다. </section>으로 정확히 닫았습니다. */}
            <section className="bg-white p-8 rounded-[32px] border border-gray-100 shadow-sm text-left">
              <h3 className="text-lg font-black mb-4 flex items-center text-[#4A4F58]">
                <BookOpen className="mr-3 text-[#3478B8]" size={18} /> 학습 리포트
              </h3>
              <p className="text-[11px] text-gray-400 font-medium leading-relaxed mb-6">
                최근 **데이터베이스 정규화** 세션에서 높은 정답률을 기록하였습니다.
              </p>
              <div className="w-full bg-gray-100 h-2 rounded-full overflow-hidden">
                <div className="bg-[#3478B8] h-full w-[84%] transition-all duration-1000"></div>
              </div>
            </section>
          </div>
        </div>
      </main>
    </div>
  );
};

const StatCard = ({ icon, label, value }) => (
  <div className="bg-white p-6 rounded-[28px] border border-gray-100 text-center shadow-sm hover:shadow-md transition group">
    <div className="flex justify-center mb-3 group-hover:scale-110 transition-transform">{icon}</div>
    <p className="text-[9px] text-gray-400 font-black uppercase tracking-widest mb-1">{label}</p>
    <p className="text-2xl font-black text-[#4A4F58]">{value}</p>
  </div>
);

const ScrapItem = ({ title, date }) => (
  <div className="flex justify-between items-center group cursor-pointer">
    <span className="text-sm font-bold group-hover:text-[#D9A23A] transition-colors">{title}</span>
    <span className="text-[10px] text-gray-400 font-medium">{date}</span>
  </div>
);

export default Profile;