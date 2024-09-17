import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import Cookies from 'js-cookie';
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
      console.log('Token saved:', response.data); // 디버깅을 위한 로그
      navigate('/');
    } catch (error) {
      console.error('로그인 에러:', error.response?.data || error.message);
      setLoginError(error.response?.data || '로그인에 실패했습니다.');
    }
  };

  useEffect(() => {
    const queryParams = new URLSearchParams(window.location.search);
    const token = queryParams.get('token');

    if (token) {
      try {
        localStorage.setItem('token', token);
        console.log('Token saved in localStorage:', token);
        navigate('/');
      } catch (error) {
        console.error('Failed to save token:', error);
      }
    }
  }, [navigate]);

  const handleSocialLogin = async (provider) => {
    try {
      let loginUrl;
      if (provider === 'google') {
        const response = await axios.get('http://localhost:8080/api/v1/oauth2/google/url');
        loginUrl = response.data;
      } else if (provider === 'kakao') {
        const response = await axios.get('http://localhost:8080/api/v1/oauth2/kakao/url');
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

  // const handleSocialLogin = async (provider) => {
  //   try {
  //     let loginUrl;
  //     if (provider === 'google') {
  //       const response = await axios.get('http://localhost:8080/api/v1/oauth2/google/url');
  //       loginUrl = response.data;
  //     } else if (provider === 'kakao') {
  //       const response = await axios.get('http://localhost:8080/api/v1/oauth2/kakao/url');
  //       loginUrl = response.data;
  //     } else if (provider === 'naver') {
  //       const response = await axios.get('http://localhost:8080/api/v1/oauth2/naver/url');
  //       loginUrl = response.data;
  //     }
  //     window.location.href = loginUrl; // 서버로부터 받은 로그인 URL로 리디렉션
  //   } catch (error) {
  //     console.error('소셜 로그인 에러:', error.message);
  //   }
  // };
  //
  // const handleAuthCode = async (code) => {
  //   try {
  //     const response = await axios.get('http://localhost:8080/api/v1/oauth2/google', {
  //       params: { code }
  //     });
  //
  //     if (response.status === 200) {
  //       const token = response.data; // 서버에서 반환된 JWT
  //       localStorage.setItem('token', token);
  //       console.log('Token saved in localStorage:', token);
  //
  //       axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
  //       setTimeout(() => {
  //         navigate('/');
  //       }, 100);
  //     } else {
  //       console.error('로그인 요청 실패');
  //     }
  //   } catch (error) {
  //     console.error('로그인 실패:', error);
  //     console.error('Google 로그인 에러:', error.response?.data?.error || error.message);
  //   }
  // };

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
                    e.preventDefault(); // form 기본 동작을 막음
                    handleSocialLogin('google');
                  }}
              >
                <img src={googleLogo} alt="Google" />
              </button>
              {/* Kakao와 Naver 소셜 로그인 버튼 추가 예정 */}
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
