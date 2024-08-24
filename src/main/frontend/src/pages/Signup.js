import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import '../css/Signup.css';

const Signup = () => {
  const [nickname, setNickname] = useState('');
  const [email, setEmail] = useState('');
  const [verificationCode, setVerificationCode] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [phoneNumber, setPhoneNumber] = useState('');

  const [nicknameError, setNicknameError] = useState('');
  const [emailError, setEmailError] = useState('');
  const [passwordError, setPasswordError] = useState('');
  const [confirmPasswordError, setConfirmPasswordError] = useState('');
  const [phoneNumberError, setPhoneNumberError] = useState('');

  const validateNickname = (e) => {
    const nicknameValue = e.target.value;
    setNickname(nicknameValue);

    if (nicknameValue === 'fragrance') {
      setNicknameError('중복된 닉네임이 있습니다.');
    } else {
      setNicknameError('');
    }
  };

  const validateEmail = (e) => {
    const emailValue = e.target.value;
    setEmail(emailValue);

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(emailValue)) {
      setEmailError('이메일을 정확히 입력해주세요.');
    } else {
      setEmailError('');
    }
  };

  const validatePassword = (e) => {
    const passwordValue = e.target.value;
    setPassword(passwordValue);

    const passwordRegex = /^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*]).{8,16}$/;
    if (!passwordRegex.test(passwordValue)) {
      setPasswordError('영문, 숫자, 특수문자 조합해서 입력해주세요. (8-16자)');
    } else {
      setPasswordError('');
    }
  };

  const validateConfirmPassword = (e) => {
    const confirmPasswordValue = e.target.value;
    setConfirmPassword(confirmPasswordValue);

    if (confirmPasswordValue !== password) {
      setConfirmPasswordError('비밀번호가 일치하지 않습니다.');
    } else {
      setConfirmPasswordError('');
    }
  };

  const validatePhoneNumber = (e) => {
    const phoneNumberValue = e.target.value;
    setPhoneNumber(phoneNumberValue);

    const phoneRegex = /^[0-9]{10,11}$/;
    if (!phoneRegex.test(phoneNumberValue)) {
      setPhoneNumberError('전화번호를 정확히 입력해주세요.');
    } else {
      setPhoneNumberError('');
    }
  };

  const isFormValid = !nicknameError && !emailError && !passwordError && !confirmPasswordError && !phoneNumberError && nickname && email && password && confirmPassword && phoneNumber;

  const handleSubmit = (e) => {
    e.preventDefault();
    if (isFormValid) {
      console.log('회원가입 진행');
      // 서버와 통신하여 회원가입 처리 로직 추가
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

      <div className="signup-container">
        <div className="signup-form">
          <h2>회원가입</h2>
          <form onSubmit={handleSubmit}>
            <div className="input-group">
              <label htmlFor="nickname">닉네임*</label>
              <input
                type="text"
                id="nickname"
                placeholder="예) fragrance"
                value={nickname}
                onChange={validateNickname}
                required
              />
              {nicknameError && <p className="error-text">{nicknameError}</p>}
            </div>

            <div className="input-group">
              <label htmlFor="email">이메일 주소*</label>
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
              <label htmlFor="verificationCode">인증번호 확인*</label>
              <input
                type="text"
                id="verificationCode"
                value={verificationCode}
                onChange={(e) => setVerificationCode(e.target.value)}
                required
              />
            </div>

            <div className="input-group">
              <label htmlFor="password">비밀번호*</label>
              <input
                type="password"
                id="password"
                placeholder="영문, 숫자, 특수문자 조합 8-16자"
                value={password}
                onChange={validatePassword}
                required
              />
              {passwordError && <p className="error-text">{passwordError}</p>}
            </div>

            <div className="input-group">
              <label htmlFor="confirmPassword">비밀번호 확인*</label>
              <input
                type="password"
                id="confirmPassword"
                placeholder="비밀번호를 다시 입력하세요."
                value={confirmPassword}
                onChange={validateConfirmPassword}
                required
              />
              {confirmPasswordError && <p className="error-text">{confirmPasswordError}</p>}
            </div>

            <div className="input-group">
              <label htmlFor="phoneNumber">전화번호*</label>
              <input
                type="text"
                id="phoneNumber"
                placeholder="010-1234-5678"
                value={phoneNumber}
                onChange={validatePhoneNumber}
                required
              />
              {phoneNumberError && <p className="error-text">{phoneNumberError}</p>}
            </div>
            <Link to="/signup_success">
            <button type="submit" className={`submit-button ${!isFormValid ? 'disabled' : ''}`} disabled={!isFormValid}>
              회원가입
            </button>
            </Link>
          </form>
        </div>
      </div>
    </>
  );
};

export default Signup;
