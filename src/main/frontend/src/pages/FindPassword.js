import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import '../css/Find-Password.css';

const FindPassword = () => {

    const [email, setEmail] = useState('');
    const [emailError, setEmailError] = useState('');

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

    const handleSubmit = (e) => {
        e.preventDefault();
        if (!emailError && email !== '') {
        // 비밀번호 찾기 로직을 여기에 추가하세요.
        console.log('비밀번호 찾기 요청:', email);
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

            <div className="find-password-container">
                <div className="find-password-form">
                    <h2>비밀번호 찾기</h2>
                    <p>가입시 등록한 이메일 주소를 입력하면, 입력한 이메일 주소로 임시 비밀번호를 전송해 드립니다.</p>
                    <form onSubmit={handleSubmit}>
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
                    </form>
                </div>
                <button type="submit" className={`submit-button ${!email || emailError ? 'disabled' : ''}`} disabled={!email || emailError}>
                    비밀번호 찾기
                </button>
            </div>
        </>
    );
};

export default FindPassword;