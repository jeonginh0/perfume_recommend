import React, {useEffect, useState} from 'react';
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

      <section className="recommendation-section">
        <h2 className="section-title">맞춤형 향수 추천</h2>
        <div className="recommendation-cards">
          <div className="card">
            <img src={note} alt="향기 노트" className="main_note"/>
            <h3 className="card-title">향기 노트</h3>
            <p className="card-description">
              당신이 좋아하는 향기를 선택하세요. 꽃, 과일, 허브 등 다양한 향기 노트 중에서 선택하여 나만의 향수를 찾아보세요.
            </p>
          </div>
          <div className="card">
            <img src={life} alt="사용 목적" className="main_life"/>
            <h3 className="card-title">사용 목적</h3>
            <p className="card-description">
              어떤 상황에서 사용할 향수를 찾고 있나요? 데일리, 특별한 날, 운동 후 등 사용 목적에 맞는 향수를 추천해드립니다.
            </p>
          </div>
          <div className="card">
            <img src={season} alt="계절" className="main_season"/>
            <h3 className="card-title">계절</h3>
            <p className="card-description">
              계절에 따라 어울리는 향수를 선택해보세요. 봄의 상쾌한 꽃향기, 여름의 시원한 향기, 가을의 따뜻한 향기, 겨울의 깊고 포근한 향기를 추천합니다.
            </p>
          </div>
        </div>
      </section>
    </div>
  );
}

export default App;