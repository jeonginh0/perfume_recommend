import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import '../css/Login.css';
import googleLogo from '../img/Google.png';
import kakaoLogo from '../img/kakaotalk.png';
import naverLogo from '../img/naver.png';

const Login = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [emailError, setEmailError] = useState('');
  const [loginError, setLoginError] = useState('');

  const registeredUser = {
    email: 'user@example.com',
    password: 'password123'
  };

  const isFormValid = email !== '' && password !== '';

  const validateEmail = (e) => {
    const emailValue = e.target.value;
    setEmail(emailValue);

    // 이메일 형식 검증
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(emailValue)) {
      setEmailError('이메일 주소를 정확히 입력해주세요.');
    } else {
      setEmailError('');
    }
  };

  const handleLogin = (e) => {
    e.preventDefault();

    // 이메일과 비밀번호 검증
    if (email === registeredUser.email && password === registeredUser.password) {
      setLoginError('');
      // 로그인 성공 시 http://localhost:3000/로 리다이렉트
      window.location.href = 'http://localhost:3000/';
    } else {
      setLoginError('이메일 또는 비밀번호가 일치하지 않습니다.');
    }
  };

  return (
    <>
      <header className="header">
        <div className="logo-nav-container">
          <Link to="/">
            <div className="logo">fragrance</div>
          </Link>
        </div>
      </header>

      <div className="login-container">
        <div className="login-form">
          <h2>로그인</h2>
          <form onSubmit={handleLogin}>
            <div className="input-group">
              <label htmlFor="email">이메일 주소</label>
              <input
                type="email"
                id="email"
                placeholder="예) fragrance@fragrance.co.kr"
                value={email}
                onChange={validateEmail}
                required
              />
              {emailError && <p className="error-text">{emailError}</p>}
            </div>
            <div className="input-group">
              <label htmlFor="password">비밀번호</label>
              <input
                type="password"
                id="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>
            {loginError && <p className="error-text">{loginError}</p>}
            <div className="checkbox-group">
              <input type="checkbox" id="auto-login" />
              <label htmlFor="auto-login">자동 로그인</label>
            </div>
            <button
              type="submit"
              className={`login-button ${isFormValid ? 'active' : ''}`}
              disabled={!isFormValid}
            >
              로그인
            </button>
          </form>
          <div className="login-options">
            <Link to="/find_email">
              <p className="signup">이메일 찾기</p> | 
            </Link>
            <Link to="/find_password">
              <p className="signup">비밀번호 찾기</p> | 
            </Link>
            <Link to="/signup">
              <p className="signup">회원가입</p>
            </Link>
          </div>
          <div className="social-login">
            <button className="social-button">
              <img src={googleLogo} alt="Google" />
            </button>
            <button className="social-button">
              <img src={kakaoLogo} alt="KakaoTalk" />
            </button>
            <button className="social-button">
              <img src={naverLogo} alt="Naver" />
            </button>
          </div>
        </div>
      </div>
    </>
  );
};

export default Login;
