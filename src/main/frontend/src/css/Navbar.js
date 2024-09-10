import React, { useState, useEffect } from 'react';
import { NavLink, useLocation, Link } from 'react-router-dom';
import '../css/Navbar.css';
import heartIcon from '../img/Heart_icon.png';
import profileIcon from '../img/Profile_icon.png';
import searchIcon from '../img/Search_icon.png';

const Navbar = () => {
  const location = useLocation(); // 현재 경로 얻기
  const [isMenuVisible, setIsMenuVisible] = useState(false); // 말풍선 메뉴 표시 여부
  const [isLoggedIn, setIsLoggedIn] = useState(false); // 로그인 상태
  const [nickname, setUserName] = useState(""); // 로그인된 유저 이름

  // 컴포넌트가 마운트될 때 로그인 상태 확인
  useEffect(() => {
    const token = localStorage.getItem('token');
    if (token) {
      // 가정: 토큰으로부터 유저 이름을 추출하는 로직
      const storedUserName = "name"; // 이 부분은 실제로 서버 응답에 기반해야 함
      setUserName(storedUserName);
      setIsLoggedIn(true);
    }
  }, []);

  // 프로필 아이콘 클릭 시 메뉴 표시/숨기기 토글
  const handleProfileClick = () => {
    setIsMenuVisible(!isMenuVisible);
  };

  // 로그아웃 처리
  const logoutUser = () => {
    localStorage.removeItem('token'); // 토큰 삭제
    setIsLoggedIn(false);
    setUserName("");
    setIsMenuVisible(false);
  };

  // 클릭한 링크가 현재 페이지와 같으면 새로 고침
  const handleNavLinkClick = (e, path) => {
    if (path === location.pathname) {
      window.location.reload();
    }
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
                exact
                to="/perfume"
                className="nav-link"
                activeClassName="active"
                onClick={(e) => handleNavLinkClick(e, "/perfume")}
              >
                Perfume
              </NavLink>
            </li>
            <li>
              <NavLink
                to="/community"
                className="nav-link"
                activeClassName="active"
                onClick={(e) => handleNavLinkClick(e, "/community")}
              >
                Community
              </NavLink>
            </li>
            <li>
              <NavLink
                to="/recommend"
                className="nav-link"
                activeClassName="active"
                onClick={(e) => handleNavLinkClick(e, "/recommend")}
              >
                Recommend
              </NavLink>
            </li>
          </ul>
        </nav>
      </div>
      <nav>
        <ul className="nav-icons">
          <Link to="/wishlist">
          <li><img src={heartIcon} alt="Heart icon" /></li>
          </Link>
          <li
            onClick={handleProfileClick} // 클릭 이벤트로 변경
            className="profile-icon-container"
          >
            <img src={profileIcon} alt="Profile icon" />

            {isMenuVisible && (
              <div className="profile-menu">
                {isLoggedIn ? (
                  <>
                    <p>{nickname}님</p>
                    <Link to="/" onClick={logoutUser}>로그아웃</Link>
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
