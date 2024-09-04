import React from 'react';
import { NavLink, useLocation, Link } from 'react-router-dom';
import '../css/Navbar.css';
import heartIcon from '../img/Heart_icon.png';
import profileIcon from '../img/Profile_icon.png';
import searchIcon from '../img/Search_icon.png';

const Navbar = () => {
  const location = useLocation(); // 현재 경로 얻기

  const handleNavLinkClick = (e, path) => {
    // 클릭한 링크가 현재 페이지와 같으면 새로 고침
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
          <Link to="/login">
              <li><img src={profileIcon} alt="Profile icon" /></li>
          </Link>
          <li><img src={searchIcon} alt="Search icon" /></li>
        </ul>
      </nav>
    </header>
  );
};

export default Navbar;
