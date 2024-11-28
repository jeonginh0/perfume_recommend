import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios'; // Axios를 사용하여 API 호출
import '../css/Find-Email.css';
import qs from 'qs';
import Navbar from '../css/Navbar'; // Navbar 컴포넌트 임포트

const FindEmail = () => {
    const [phoneNumber, setPhoneNumber] = useState('');
    const [error, setError] = useState('');
    const [email, setEmail] = useState(''); // 찾은 이메일을 저장할 상태
    const [success, setSuccess] = useState(false); // 성공 여부 상태 저장

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

    const maskEmail = (email) => {
        const [localPart, domainPart] = email.split('@');
        const maskedLocal = localPart[0] + '*'.repeat(localPart.length - 2) + localPart.slice(-1);
        return `${maskedLocal}@${domainPart}`;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!error && phoneNumber !== '') {
            try {
                // API 호출 (폼 데이터 형식으로 전송)
                const response = await axios.post('http://localhost:8080/api/users/find-email', 
                    qs.stringify({ phoneNumber: phoneNumber }), 
                    {
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded'
                        }
                    }
                );

                // 응답 데이터 확인
                const responseData = response.data;
                console.log('응답 데이터:', responseData);

                // 이메일 정보 추출 (예시: '해당 휴대폰 번호로 등록된 이메일: sm1348840@gmail.com'에서 이메일 주소만 추출)
                const emailMatch = responseData.match(/이메일:\s([^\s]+)/);

                if (emailMatch && emailMatch[1]) {
                    const extractedEmail = emailMatch[1];
                    setEmail(maskEmail(extractedEmail)); // 이메일 가리기 처리
                    setSuccess(true);  // 성공 상태 업데이트
                    setError('');  // 에러 메시지 초기화
                } else {
                    setError('이메일을 찾을 수 없습니다.');
                }
            } catch (err) {
                console.error('이메일 찾기 오류:', err.response || err);
                setError('이메일을 찾을 수 없습니다. 다시 시도해 주세요.');
            }
        }
    };

    return (
        <>
            <Navbar />
            {!success ? (
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
                            <button type="submit" className={`submit-button ${!phoneNumber || error ? 'disabled' : ''}`} disabled={!phoneNumber || error}>
                                이메일 찾기
                            </button>
                        </form>
                    </div>
                </div>
            ) : (
                <div className="find-email-container-2">
                    <div className="find-email-form">
                        <h2>이메일 찾기에 성공하였습니다.</h2>
                        <p>이메일 주소</p>
                        <h3 className="masked-email">{email}</h3>
                    </div>
                    <div className="button-group">
                        <Link to="/find_password">
                            <button className="find-password-button">비밀번호 찾기</button>
                        </Link>
                        <Link to="/login">
                            <button className="login">로그인</button>
                        </Link>
                    </div>
                </div>
            )}
        </>
    );
};

export default FindEmail;
