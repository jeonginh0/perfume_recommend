import React from 'react';
import { NavLink } from 'react-router-dom';
import '../css/Navbar.css';
import heartIcon from '../img/Heart_icon.png';
import profileIcon from '../img/Profile_icon.png';
import searchIcon from '../img/Search_icon.png';

const Navbar = () => {
  return (
    <header className="perfume-header">
      <div className="logo-nav-container">
        <NavLink to="/">
          <div className="logo">fragrance</div>
        </NavLink>
        <nav>
          <ul className="nav-links">
            <li>
              <NavLink exact to="/perfume" className="nav-link" activeClassName="active">
                Perfume
              </NavLink>
            </li>
            <li>
              <NavLink to="/community" className="nav-link" activeClassName="active">
                Community
              </NavLink>
            </li>
            <li>
              <NavLink to="/recommend" className="nav-link" activeClassName="active">
                Recommend
              </NavLink>
            </li>
          </ul>
        </nav>
      </div>
      <nav>
        <ul className="nav-icons">
          <li><img src={heartIcon} alt="Heart icon" /></li>
          <li><img src={profileIcon} alt="Profile icon" /></li>
          <li><img src={searchIcon} alt="Search icon" /></li>
        </ul>
      </nav>
    </header>
  );
};

export default Navbar;
