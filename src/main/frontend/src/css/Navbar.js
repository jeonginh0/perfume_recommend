import React, { useState, useEffect } from 'react';
import { NavLink, Link, useNavigate } from 'react-router-dom';
import { FiMenu } from 'react-icons/fi';
import '../css/Navbar.css';
import heartIcon from '../img/Heart_icon.png';
import profileIcon from '../img/Profile_icon.png';
import searchIcon from '../img/Search_icon.png';
import axios from 'axios'; 
import { CgProfile } from "react-icons/cg";
import { SlArrowRight } from "react-icons/sl";
import { FaRegHeart } from "react-icons/fa";
import { BsPower } from "react-icons/bs";
import { VscClose } from "react-icons/vsc";

const Navbar = () => {
  const navigate = useNavigate(); 
  const [isMenuVisible, setIsMenuVisible] = useState(false); 
  const [isLoggedIn, setIsLoggedIn] = useState(false); 
  const [nickname, setNickname] = useState(''); 
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false); // 모바일 메뉴 상태

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (token) {
      axios.get('http://localhost:8080/api/users/me', {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      })
      .then(response => {
        if (response.data && response.data.nickname) {
          setNickname(response.data.nickname); 
          setIsLoggedIn(true);
        } else {
          setIsLoggedIn(false);
        }
      })
      .catch(error => {
        console.error('유저 정보를 가져오는데 실패했습니다.', error);
        handleLogout(); 
      });
    }
  }, []);

  const handleProfileClick = () => {
    setIsMenuVisible(!isMenuVisible);
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    setIsLoggedIn(false);
    setNickname('');
    setIsMenuVisible(false);
    navigate('/login');
  };

  const toggleMobileMenu = () => {
    setIsMobileMenuOpen(!isMobileMenuOpen);
  };

  const closeMobileMenu = () => {
    setIsMobileMenuOpen(false); // 메뉴 닫기
  };

  const handleProtectedRoute = (path) => {
    if (!isLoggedIn) {
      alert('로그인 후 이용 가능합니다.'); 
      navigate('/login');   
    } else {
      navigate(path);      
    }
  };

  return (
    <header className="perfume-header">
      <div className="logo-nav-container">
        <NavLink to="/">
          <div className="logo-nav">fragrance</div>
        </NavLink>
        <nav>
          <ul className={`nav-links ${isMobileMenuOpen ? 'active' : ''}`}>
            <li>
              <NavLink to="/perfume" className="nav-link">Perfume</NavLink>
            </li>
            <li>
              <NavLink to="/community" className="nav-link">Community</NavLink>
            </li>
            <li>
              <NavLink to="/recommend" className="nav-link">Recommend</NavLink>
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
          <li onClick={handleProfileClick} className="profile-icon-container">
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
        <button className="hamburger-menu" onClick={toggleMobileMenu}>
          <FiMenu size={30} />
        </button>
      </nav>
      {isMobileMenuOpen && (
        <>
          <div className="overlay" onClick={closeMobileMenu}></div>
          <div className="mobile-menu">
            <div className="mobile-menu-header">
              <button className="close-menu-btn" onClick={closeMobileMenu}>
                <VscClose size={30} color='gray'/>
              </button>
            </div>
            <div className="mobile-menu-top">
              {isLoggedIn ? (
                <>
                <div className="mobile-name">
                  <CgProfile size={30} />
                  <p>{nickname ? `${nickname}님` : '유저 이름 없음'}</p>
                </div>
                <div className="mobile-menu-icons">
                  <div className="icon-item">
                    <Link to="/wishlist">
                    <FaRegHeart size={30} color="gray"/>
                      <p>찜 목록</p>
                    </Link>
                  </div>
                  <div className="icon-item">
                    <Link to="/mypage">
                      <CgProfile size={30} color="gray" />
                      <p>마이페이지</p>
                    </Link>
                  </div>
                </div>
                </>
              ) : (
                <>
                <div className="mobile-name">
                  <CgProfile size={30} color='gray'/>
                  <Link to="/login" className="mobile-login">
                    <p>로그인 해주세요</p> <SlArrowRight size={12} color='gray'/>
                  </Link>
                </div>
                <div className="mobile-menu-icons">
                  <div className="icon-item">
                    <div onClick={() => handleProtectedRoute('/wishlist')}>
                      <FaRegHeart size={30} color="gray"/>
                      <p>찜 목록</p>
                    </div>
                  </div>
                  <div className="icon-item">
                    <div onClick={() => handleProtectedRoute('/mypage')}>
                      <CgProfile size={30} color="gray" />
                      <p>마이페이지</p>
                    </div>
                  </div>
                </div>
                </>
              )}
              <div className="search-bar">
                <img src={searchIcon} alt="Search icon" />
              </div>
            </div>
            <ul className="mobile-menu-links">
              <li><NavLink to="/perfume">Perfume</NavLink></li>
              <li><NavLink to="/community">Community</NavLink></li>
              <li><NavLink to="/recommend">Recommend</NavLink></li>
            </ul>
            <div className="logout-section">
              {isLoggedIn ? (
                <Link to="/" onClick={handleLogout}>
                  <BsPower size={17} color='#adadad'/>
                  <span>로그아웃</span>
                </Link>
              ) : (
                <Link to="/login">
                  <BsPower size={17} color='#adadad'/>
                  <span>로그인</span>
                </Link>
              )}
            </div>
          </div>
        </>
      )}
    </header>
  );
};

export default Navbar;
