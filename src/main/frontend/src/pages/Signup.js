import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import '../css/Signup.css';

const Signup = () => {
  const [nickname, setNickname] = useState('');
  const [email, setEmail] = useState('');
  const [verificationCode, setVerificationCode] = useState('');
  const [inputCode, setInputCode] = useState(''); // 사용자가 입력한 인증번호
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [phoneNumber, setPhoneNumber] = useState('');

  const [nicknameError, setNicknameError] = useState('');
  const [emailError, setEmailError] = useState('');
  const [verificationMessage, setVerificationMessage] = useState(''); // 인증 메시지
  const [passwordError, setPasswordError] = useState('');
  const [confirmPasswordError, setConfirmPasswordError] = useState('');
  const [phoneNumberError, setPhoneNumberError] = useState('');

  const [isCodeSent, setIsCodeSent] = useState(false); // 인증번호 전송 여부
  const [isCodeVerified, setIsCodeVerified] = useState(false); // 인증번호 확인 여부
  const [isCodeValid, setIsCodeValid] = useState(false); // 인증번호 일치 여부

  const navigate = useNavigate();

  // 닉네임 유효성 검사
  const validateNickname = (e) => {
    const nicknameValue = e.target.value;
    setNickname(nicknameValue);

    if (nicknameValue === 'fragrance') {
      setNicknameError('중복된 닉네임이 있습니다.');
    } else {
      setNicknameError('');
    }
  };

  // 이메일 유효성 검사
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

  // 비밀번호 유효성 검사
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

  // 비밀번호 확인 유효성 검사
  const validateConfirmPassword = (e) => {
    const confirmPasswordValue = e.target.value;
    setConfirmPassword(confirmPasswordValue);

    if (confirmPasswordValue !== password) {
      setConfirmPasswordError('비밀번호가 일치하지 않습니다.');
    } else {
      setConfirmPasswordError('');
    }
  };

  // 전화번호 유효성 검사
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

  // 인증번호 전송 함수
  const handleSendVerificationCode = async () => {
    try {
      const response = await fetch(`http://58.235.71.202:8080/api/users/send-verification-code?email=${encodeURIComponent(email)}`, {
        method: 'POST',
      });

      if (response.ok) {
        setIsCodeSent(true);
        setVerificationMessage('인증번호가 발송되었습니다.');
      } else {
        setVerificationMessage('인증번호 전송에 실패했습니다.');
      }
    } catch (error) {
      setVerificationMessage('인증번호 전송 중 오류가 발생했습니다.');
    }
  };

  // 인증번호 확인 함수
  const handleVerifyCode = async () => {
    try {
      const response = await fetch(`http://58.235.71.202:8080/api/users/verify-code?email=${encodeURIComponent(email)}&code=${encodeURIComponent(inputCode)}`, {
        method: 'POST',
      });

      if (response.ok) {
        setIsCodeVerified(true);
        setIsCodeValid(true);
        setVerificationMessage('인증번호가 확인되었습니다.');
      } else {
        setIsCodeValid(false);
        setVerificationMessage('인증번호가 올바르지 않습니다.');
      }
    } catch (error) {
      setVerificationMessage('인증번호 확인 중 오류가 발생했습니다.');
    }
  };

  const isFormValid = !nicknameError && !emailError && !passwordError && !confirmPasswordError && !phoneNumberError && nickname && email && password && confirmPassword && phoneNumber && isCodeVerified;

  // 회원가입 제출 함수
  const handleSubmit = async (e) => {
    e.preventDefault();
    if (isFormValid) {
      try {
        const response = await fetch('http://58.235.71.202:8080/api/users/register', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            nickname,
            email,
            password,
            phoneNumber,
          }),
        });

        if (response.ok) {
          // 회원가입 성공 시 /signup_success로 이동
          navigate('/signup_success');
        } else {
          const errorData = await response.json();
          console.error('회원가입 실패:', errorData);
        }
      } catch (error) {
        console.error('에러 발생:', error);
      }
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
              <button type="button" onClick={handleSendVerificationCode} disabled={!email || emailError || isCodeSent}>
                인증번호 전송
              </button>
              {isCodeSent && <p>{verificationMessage}</p>}
            </div>

            <div className="input-group">
              <label htmlFor="verificationCode">인증번호 확인*</label>
              <input
                type="text"
                id="verificationCode"
                value={inputCode}
                onChange={(e) => setInputCode(e.target.value)}
                required
              />
              <button type="button" onClick={handleVerifyCode} disabled={!inputCode || isCodeValid}>
                확인
              </button>
              {verificationMessage && <p>{verificationMessage}</p>}
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

            <button type="submit" className={`submit-button ${!isFormValid ? 'disabled' : ''}`} disabled={!isFormValid}>
              회원가입
            </button>
          </form>
        </div>
      </div>
    </>
  );
};

export default Signup;
