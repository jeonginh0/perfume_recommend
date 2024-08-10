import React, {useEffect, useState} from 'react';
import axios from 'axios';
import './App.css';
import main_bg from './img/main_background.png';
import heartIcon from './img/Heart_icon.png';
import profileIcon from './img/Profile_icon.png';
import searchIcon from './img/Search_icon.png';

function App() {
  // const [hello, setHello] = useState('');

  // const api = axios.create({
  //       baseURL: 'http://localhost:8080'
  // });

  // useEffect(() => {
  //     api.get('/api/hello')
  //       .then(response => setHello(response.data))
  //       .catch(error => console.log(error))
  // }, []);

  return (
    <div className="app">
      <div className="main-banner" style={{ backgroundImage: `url(${main_bg})` }}>
        <header className="header">
          <div className="logo-nav-container">
            <div className="logo">fragrance</div>
            <nav>
              <ul className="nav-links">
                <li>Perfume</li>
                <li>Community</li>
                <li>Recommend</li>
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
        <div className="banner-content">
          <p className='banner-content1'>나의 라이프스타일을 담은 맞춤형 향수 추천</p>
          <p className='banner-content2'>나만의 향수를 찾아보세요.</p>
          <button className="recommend-btn">향수 추천받기</button>
        </div>
      </div>
    </div>
  );
}

export default App;