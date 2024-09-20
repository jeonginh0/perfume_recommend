import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios'; // API 호출을 위한 axios 임포트
import '../css/Find-Password.css';
import Navbar from '../css/Navbar';
import qs from 'qs'; // query string 변환을 위한 라이브러리

const FindPassword = () => {
    const [email, setEmail] = useState('');
    const [emailError, setEmailError] = useState('');
    const [success, setSuccess] = useState(false); // 성공 여부 상태 저장
    const [maskedEmail, setMaskedEmail] = useState(''); // 마스킹된 이메일 저장
    const [errorMessage, setErrorMessage] = useState(''); // 오류 메시지 저장

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

    const maskEmail = (email) => {
        const [localPart, domainPart] = email.split('@');
        const maskedLocal = localPart[0] + '*'.repeat(localPart.length - 2) + localPart.slice(-1);
        return `${maskedLocal}@${domainPart}`;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!emailError && email !== '') {
            try {
                // 비밀번호 찾기 API 호출 (폼 데이터 형식으로 전송)
                const response = await axios.post('http://localhost:8080/api/users/forgot-password', 
                    qs.stringify({ email: email }), // email 값을 query string 형식으로 변환하여 전송
                    {
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded'
                        }
                    }
                );

                // 성공 시 메시지 처리 및 이메일 마스킹
                setMaskedEmail(maskEmail(email));
                setSuccess(true);
                setErrorMessage('');
            } catch (err) {
                // 오류 처리
                console.error('비밀번호 찾기 오류:', err.response || err);
                setErrorMessage('비밀번호를 찾을 수 없습니다. 다시 시도해 주세요.');
                setSuccess(false);
            }
        }
    };

    return (
        <>
            <Navbar />
            {!success ? (
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
                            <button type="submit" className={`submit-button ${!email || emailError ? 'disabled' : ''}`} disabled={!email || emailError}>
                                비밀번호 찾기
                            </button>
                        </form>
                        {errorMessage && <p className="error-text">{errorMessage}</p>}
                    </div>
                </div>
            ) : (
                <div className="find-password-container-2">
                    <div className="find-password-form">
                        <h2>임시 비밀번호 전송 완료</h2>
                        <p>이메일 주소</p>
                        <h3 className="masked-email">{maskedEmail}</h3>
                    </div>
                    <div className="button-group">
                            <button className="find-password-button" onClick={handleSubmit}>재전송</button>
                            <Link to="/login">
                                <button className="login">로그인</button>
                            </Link>
                        </div>
                </div>
            )}
        </>
    );
};

export default FindPassword;
