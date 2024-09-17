import React, { useState, useEffect } from 'react';
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
      const response = await axios.post('http://localhost:8080/api/users/login', {
        email,
        password,
      }, {
        headers: {
          'Content-Type': 'application/json',
        }
      });

      localStorage.setItem('token', response.data);
      console.log('Token saved:', response.data);
      navigate('/');
    } catch (error) {
      console.error('로그인 에러:', error.response?.data || error.message);
      setLoginError(error.response?.data || '로그인에 실패했습니다.');
    }
  };

  useEffect(() => {
    const queryParams = new URLSearchParams(window.location.search);
    const code = queryParams.get('code');

    if (code) {
      const fetchToken = async () => {
        try {
          const response = await axios.post(
            'https://kauth.kakao.com/oauth/token',
            null,
            {
              params: {
                grant_type: 'authorization_code',
                client_id: 'YOUR_CLIENT_ID', 
                redirect_uri: 'http://localhost:3000/login',  
                code: code,
                client_secret: 'YOUR_CLIENT_SECRET' 
              },
              headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
              }
            }
          );

          const { access_token } = response.data;
          localStorage.setItem('token', access_token);
          console.log('Token saved:', access_token);
          navigate('/');
        } catch (error) {
          console.error('토큰 요청 실패:', error);
        }
      };
      
      fetchToken();
    }
  }, [navigate]);

  const handleSocialLogin = async (provider) => {
    try {
      let loginUrl;
      if (provider === 'kakao') {
        loginUrl = `https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=YOUR_CLIENT_ID&redirect_uri=http://localhost:3000/login`;
      } else if (provider === 'google') {
        const response = await axios.get('http://localhost:8080/api/v1/oauth2/google/url');
        loginUrl = response.data;
      } else if (provider === 'naver') {
        const response = await axios.get('http://localhost:8080/api/v1/oauth2/naver/url');
        loginUrl = response.data;
      }
      window.location.href = loginUrl;
    } catch (error) {
      console.error('소셜 로그인 에러:', error.message);
    }
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
            <button
              className="social-button"
              onClick={(e) => {
                e.preventDefault();
                handleSocialLogin('google');
              }}
            >
              <img src={googleLogo} alt="Google" />
            </button>
            <button
              className="social-button"
              onClick={(e) => {
                e.preventDefault();
                handleSocialLogin('kakao');
              }}
            >
              <img src={kakaoLogo} alt="KakaoTalk" />
            </button>
            <button
              className="social-button"
              onClick={(e) => {
                e.preventDefault();
                handleSocialLogin('naver');
              }}
            >
              <img src={naverLogo} alt="Naver" />
            </button>
          </div>
        </div>
      </div>
    </>
  );
};

export default Login;
