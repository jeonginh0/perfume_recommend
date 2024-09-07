import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import '../css/Login.css';
import googleLogo from '../img/Google.png';
import kakaoLogo from '../img/kakaotalk.png';
import naverLogo from '../img/naver.png';

const Login = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [emailError, setEmailError] = useState('');
  const [loginError, setLoginError] = useState('');
  const navigate = useNavigate();

  const isFormValid = email !== '' && password !== '';

  const validateEmail = (e) => {
    const emailValue = e.target.value;
    setEmail(emailValue);

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(emailValue)) {
      setEmailError('이메일 주소를 정확히 입력해주세요.');
    } else {
      setEmailError('');
    }
  };

  const handleLogin = async (e) => {
    e.preventDefault();
  
    if (!isFormValid) {
      setLoginError('이메일과 비밀번호를 모두 입력해 주세요.');
      return;
    }
  
    try {
      const response = await axios.post('http://58.235.71.202:8080/api/users/login', {
        email,
        password,
      }, {
        headers: {
          'Content-Type': 'application/json', // 요청 헤더 설정
        }
      });
  
      localStorage.setItem('token', response.data);
      navigate('/dashboard');
    } catch (error) {
      // 서버가 반환하는 에러 메시지와 상태 코드 출력
      console.error('로그인 에러:', error.response?.data || error.message);
      setLoginError(error.response?.data || '로그인에 실패했습니다.');
    }
  };
  

  const handleSocialLogin = async (provider) => {
   
  };

  return (
    <>
      <header className="login-header">
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
            <button className="social-button" onClick={() => handleSocialLogin('google')}>
              <img src={googleLogo} alt="Google" />
            </button>
            <button className="social-button" onClick={() => handleSocialLogin('kakao')}>
              <img src={kakaoLogo} alt="KakaoTalk" />
            </button>
            <button className="social-button" onClick={() => handleSocialLogin('naver')}>
              <img src={naverLogo} alt="Naver" />
            </button>
          </div>
        </div>
      </div>
    </>
  );
};

export default Login;
