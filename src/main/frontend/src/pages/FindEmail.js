import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import '../css/Find-Email.css';

const FindEmail = () => {

    const [phoneNumber, setPhoneNumber] = useState('');
    const [error, setError] = useState('');

    const validatePhoneNumber = (e) => {
        const phone = e.target.value;
        setPhoneNumber(phone);

        const phoneRegex = /^[0-9]{10,11}$/;
        if (!phoneRegex.test(phone)) {
        setError('휴대폰 번호를 정확히 입력해주세요.');
        } else {
        setError('');
        }
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        if (!error && phoneNumber !== '') {
        // 이메일 찾기 로직을 여기에 추가하세요.
        console.log('이메일 찾기 요청:', phoneNumber);
        }
    };

    return (
        <>
            <header className="findemail-header">
                <div className="logo-nav-container">
                <Link to="/">
                    <div className="logo">fragrance</div>
                </Link>
                </div>
            </header>

            <div className="find-email-container">
             <div className="find-email-form">
                    <h2>이메일 찾기</h2>
                    <p>가입시 등록한 휴대폰 번호를 입력하면 이메일 주소를 알려드립니다.</p>
                    <form onSubmit={handleSubmit}>
                    <div className="input-group">
                        <label htmlFor="phone">휴대폰 번호</label>
                        <input
                            type="text"
                            id="phone"
                            placeholder="가입하신 휴대폰 번호"
                            value={phoneNumber}
                            onChange={validatePhoneNumber}
                            required
                        />
                        {error && <p className="error-text">{error}</p>}
                    </div>
                    </form>
                </div>
                <button type="submit" className={`submit-button ${!phoneNumber || error ? 'disabled' : ''}`} disabled={!phoneNumber || error}>
                        이메일 찾기
                </button>
            </div>
        </>
    );
};

export default FindEmail;