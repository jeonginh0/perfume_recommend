import React, { useState } from 'react';
import { NavLink, useLocation, Link } from 'react-router-dom';
import '../css/Navbar.css';
import heartIcon from '../img/Heart_icon.png';
import profileIcon from '../img/Profile_icon.png';
import searchIcon from '../img/Search_icon.png';

const Navbar = () => {
  const location = useLocation(); // 현재 경로 얻기
  const [isMenuVisible, setIsMenuVisible] = useState(false); // 말풍선 메뉴 표시 여부
  const [isLoggedIn, setIsLoggedIn] = useState(false); // 로그인 상태
  const [userName, setUserName] = useState(""); // 로그인된 유저 이름

  // 프로필 아이콘 클릭 시 메뉴 표시/숨기기 토글
  const handleProfileClick = () => {
    setIsMenuVisible(!isMenuVisible);
  };

  // 로그인 상태 업데이트
  const loginUser = (name) => {
    setIsLoggedIn(true);
    setUserName(name);
  };

  // 로그아웃 상태로 설정
  const logoutUser = () => {
    setIsLoggedIn(false);
    setUserName("");
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
          <li><img src={heartIcon} alt="Heart icon" /></li>
          <li
            onClick={handleProfileClick} // 클릭 이벤트로 변경
            className="profile-icon-container"
          >
            <img src={profileIcon} alt="Profile icon" />

            {isMenuVisible && (
              <div className="profile-menu">
                {isLoggedIn ? (
                  <>
                    <p>{userName}님</p>
                    <Link to="/logout" onClick={logoutUser}>로그아웃</Link>
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
