import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import './App.css';
import main_bg from './img/main_background.png';
import heartIcon from './img/Heart_icon.png';
import profileIcon from './img/Profile_icon.png';
import searchIcon from './img/Search_icon.png';
import note from './img/main_note.png';
import life from './img/main_life.png';
import season from './img/main_season.png';

function App() {
  const navigate = useNavigate();
  const [isLoggedIn, setIsLoggedIn] = useState(false); // 로그인 상태
  const [nickname, setNickname] = useState(''); // 로그인된 유저 이름
  const [isMenuVisible, setIsMenuVisible] = useState(false); // 프로필 메뉴 표시 여부

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (token) {
      // 유저 정보를 불러오기 위한 axios 요청
      axios
        .get('http://localhost:8080/api/users/me', {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        })
        .then((response) => {
          if (response.data && response.data.nickname) {
            setNickname(response.data.nickname); // 서버 응답에서 유저 이름 가져오기
            setIsLoggedIn(true);
          } else {
            console.error('유저 닉네임이 응답에 포함되어 있지 않습니다.');
            setIsLoggedIn(false);
          }
        })
        .catch((error) => {
          console.error('유저 정보를 가져오는데 실패했습니다.', error);
          handleLogout(); // 오류 발생 시 로그아웃 처리
        });
    }
  }, []);

  // 프로필 아이콘 클릭 시 메뉴 표시/숨기기 토글
  const handleProfileClick = () => {
    setIsMenuVisible(!isMenuVisible);
  };

  // 로그아웃 처리
  const handleLogout = () => {
    localStorage.removeItem('token');
    setIsLoggedIn(false);
    setNickname('');
    setIsMenuVisible(false);
    navigate('/login');
  };

  return (
    <div className="app">
      <div className="main-banner" style={{ backgroundImage: `url(${main_bg})` }}>
        <header className="main-header">
          <div className="logo-nav-container">
            <Link to="/">
              <div className="logo-white">fragrance</div>
            </Link>
            <nav>
              <ul className="nav-links-white">
                <Link to="/perfume">
                  <li>Perfume</li>
                </Link>
                <Link to="/community">
                  <li>Community</li>
                </Link>
                <Link to="/recommend">
                  <li>Recommend</li>
                </Link>
              </ul>
            </nav>
          </div>
          <nav>
            <ul className="nav-icons-white">
              {/* 찜 목록 아이콘: 로그인 상태일 때만 표시 */}
              {isLoggedIn && (
                <Link to="/wishlist">
                  <li>
                    <img src={heartIcon} alt="Heart icon" />
                  </li>
                </Link>
              )}
              <li
                onClick={handleProfileClick}
                className="profile-icon-container"
              >
                <img src={profileIcon} alt="Profile icon" />
                {isMenuVisible && (
                  <div className="profile-menu">
                    {isLoggedIn ? (
                      <>
                        <p>{nickname ? `${nickname}님` : '유저 이름 없음'}</p>
                        <Link to="/" onClick={handleLogout}>
                          로그아웃
                        </Link>
                        <Link to="/mypage">마이페이지</Link>
                      </>
                    ) : (
                      <>
                        <Link to="/signup">회원가입</Link>
                        <Link to="/login">로그인</Link>
                      </>
                    )}
                  </div>
                )}
              </li>
              <li>
                <img src={searchIcon} alt="Search icon" />
              </li>
            </ul>
          </nav>
        </header>
        <div className="banner-content">
          <p className="banner-content1">나의 라이프스타일을 담은 맞춤형 향수 추천</p>
          <p className="banner-content2">나만의 향수를 찾아보세요.</p>
          <Link to="/recommend">
            <button className="recommend-btn">향수 추천받기</button>
          </Link>
        </div>
      </div>

      <section className="recommendation-section">
        <h2 className="section-title">맞춤형 향수 추천</h2>
        <div className="recommendation-cards">
          <div className="card">
            <img src={note} alt="향기 노트" className="main_note" />
            <h3 className="card-title">향기 노트</h3>
            <p className="card-description">
              당신이 좋아하는 향기를 선택하세요. 꽃, 과일, 허브 등 다양한 향기 노트 중에서 선택하여 나만의 향수를 찾아보세요.
            </p>
          </div>
          <div className="card">
            <img src={life} alt="사용 목적" className="main_life" />
            <h3 className="card-title">사용 목적</h3>
            <p className="card-description">
              어떤 상황에서 사용할 향수를 찾고 있나요? 데일리, 특별한 날, 운동 후 등 사용 목적에 맞는 향수를 추천해드립니다.
            </p>
          </div>
          <div className="card">
            <img src={season} alt="계절" className="main_season" />
            <h3 className="card-title">계절</h3>
            <p className="card-description">
              계절에 따라 어울리는 향수를 선택해보세요. 봄의 상쾌한 꽃향기, 여름의 시원한 향기, 가을의 따뜻한 향기, 겨울의 깊고 포근한 향기를 추천합니다.
            </p>
          </div>
        </div>
      </section>
      <section className="button-section">
        <p className="section2-title">
          지금 바로 나만의 향수를 찾아보세요.
          <br />
          간단한 설문으로 몇 분 안에 맞춤형 향수를 추천받을 수 있습니다.
        </p>
        <Link to="/recommend">
          <button className="recommend-btn">향수 추천받기</button>
        </Link>
      </section>
    </div>
  );
}

export default App;
