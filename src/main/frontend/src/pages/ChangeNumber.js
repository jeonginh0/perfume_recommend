import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import '../css/ChangeNumber.css'; 
import Navbar from '../css/Navbar.js';

const ChangeNumber = () => {
    const [phoneNumber, setPhoneNumber] = useState('');
    const [verificationCode, setVerificationCode] = useState('');
    const [isVerified, setIsVerified] = useState(false);
    const [error, setError] = useState('');
    const [userId, setUserId] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchUserInfo = async () => {
            try {
                const token = localStorage.getItem('token');
                const response = await axios.get('http://localhost:8080/api/users/me', {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
                setUserId(response.data.id); // 사용자 ID 저장
            } catch (error) {
                console.error('사용자 정보를 가져오는 중 오류가 발생했습니다:', error);
            }
        };
        fetchUserInfo();
    }, []);

    const handleVerificationRequest = async () => {
        try {
            const token = localStorage.getItem('token');
            await axios.post(`http://localhost:8080/api/verify-phone`, { phoneNumber }, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });
            setError('');
        } catch (error) {
            console.error('인증 요청 오류:', error.response ? error.response.data : error.message);
            setError('유효하지 않은 전화번호입니다.');
        }
    };

    const handleVerificationCheck = async () => {
        try {
            const token = localStorage.getItem('token');
            const response = await axios.post(`http://localhost:8080/api/verify-code`, {
                phoneNumber,
                verificationCode
            }, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });
            if (response.data.verified) {
                setIsVerified(true);
                setError('');
            } else {
                setError('인증번호가 올바르지 않습니다.');
            }
        } catch (error) {
            console.error('인증 확인 오류:', error.response ? error.response.data : error.message);
            setError('인증번호가 올바르지 않습니다.');
        }
    };

    const handlePhoneNumberChange = async () => {
        try {
            const token = localStorage.getItem('token');
            if (!userId) {
                setError('사용자 정보를 불러오지 못했습니다.');
                return;
            }

            await axios.post(`http://localhost:8080/api/users/${userId}/phone`, { phoneNumber }, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });

            // 성공적으로 전화번호가 변경되면 마이페이지로 이동
            navigate('/mypage');
        } catch (error) {
            console.error('전화번호 변경 오류:', error.response ? error.response.data : error.message);
            setError('전화번호 변경에 문제가 발생했습니다.');
        }
    };

    return (
        <>
            <Navbar />
            <div className="phone-change-container">
                <h2>전화번호 변경</h2>
                <p>새로 변경할 휴대폰 번호를 입력해주세요. 입력한 휴대폰 번호로 인증번호가 발송됩니다.</p>
                <div className="p-input">
                    <p className="p-input-label">전화번호</p>
                    <div className="input-container">
                        <input 
                            type="text" 
                            value={phoneNumber} 
                            onChange={(e) => setPhoneNumber(e.target.value)} 
                            placeholder="예) 010-1234-5678"
                        />
                        <button onClick={handleVerificationRequest}>인증</button>
                    </div>
                    {error && <p className="error-message">{error}</p>}
                </div>
                <div className="p-input">
                    <p className="p-input-label">인증번호</p>
                    <div className="input-container">
                        <input 
                            type="text" 
                            value={verificationCode} 
                            onChange={(e) => setVerificationCode(e.target.value)} 
                            placeholder="인증번호 입력"
                        />
                        <button onClick={handleVerificationCheck}>확인</button>
                    </div>
                    {error && <p className="error-message">{error}</p>}
                </div>
                <button 
                    className="change-button" 
                    onClick={handlePhoneNumberChange}
                    disabled={!phoneNumber || !isVerified} // 인증 완료 후에만 버튼 활성화
                >
                    전화번호 변경하기
                </button>
            </div>
        </>
    );
};

export default ChangeNumber;
