import React, { useState, useEffect } from 'react';
import { NavLink, useLocation, Link, useNavigate } from 'react-router-dom';
import '../css/Navbar.css';
import heartIcon from '../img/Heart_icon.png';
import profileIcon from '../img/Profile_icon.png';
import searchIcon from '../img/Search_icon.png';
import axios from 'axios'; 

const Navbar = () => {
  const location = useLocation(); // 현재 경로 얻기
  const navigate = useNavigate(); // 페이지 이동을 위한 네비게이트
  const [isMenuVisible, setIsMenuVisible] = useState(false); // 말풍선 메뉴 표시 여부
  const [isLoggedIn, setIsLoggedIn] = useState(false); // 로그인 상태
  const [nickname, setNickname] = useState(''); // 로그인된 유저 이름

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (token) {
      // 유저 정보를 불러오기 위한 axios 요청
      axios.get('http://localhost:8080/api/users/me', {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      })
      .then(response => {
        if (response.data && response.data.nickname) {
          setNickname(response.data.nickname); // 서버 응답에서 유저 이름 가져오기
          setIsLoggedIn(true);
        } else {
          console.error('유저 닉네임이 응답에 포함되어 있지 않습니다.');
          setIsLoggedIn(false);
        }
      })
      .catch(error => {
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
    <header className="perfume-header">
      <div className="logo-nav-container">
        <NavLink to="/">
          <div className="logo">fragrance</div>
        </NavLink>
        <nav>
          <ul className="nav-links">
            <li>
              <NavLink
                to="/perfume"
                className="nav-link"
              >
                Perfume
              </NavLink>
            </li>
            <li>
              <NavLink
                to="/community"
                className="nav-link"
              >
                Community
              </NavLink>
            </li>
            <li>
              <NavLink
                to="/recommend"
                className="nav-link"
              >
                Recommend
              </NavLink>
            </li>
          </ul>
        </nav>
      </div>
      <nav>
        <ul className="nav-icons">
          {isLoggedIn && (
            <Link to="/wishlist">
              <li><img src={heartIcon} alt="Heart icon" /></li>
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
                    <Link to="/" onClick={handleLogout}>로그아웃</Link>
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
          <li><img src={searchIcon} alt="Search icon" /></li>
        </ul>
      </nav>
    </header>
  );
};

export default Navbar;
