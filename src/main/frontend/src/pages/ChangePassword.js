import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import '../css/ChangePassword.css';
import Navbar from '../css/Navbar.js';

const ChangePassword = () => {
    const [email, setEmail] = useState('');
    const [verificationCode, setVerificationCode] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [isVerified, setIsVerified] = useState(false);
    const [error, setError] = useState('');
    const [infoMessage, setInfoMessage] = useState('');
    const navigate = useNavigate();

    const handleEmailVerificationRequest = async () => {
        try {
            const token = localStorage.getItem('token');
            await axios.post(`http://localhost:8080/api/users/send-verification-code?email=${email}`, {}, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });
            setInfoMessage('인증번호가 이메일로 발송되었습니다.');
            setError('');
        } catch (error) {
            console.error('이메일 인증 요청 오류:', error.response ? error.response.data : error.message);
            setError('유효하지 않은 이메일 주소입니다.');
        }
    };

    const handleVerificationCheck = async () => {
        try {
            const token = localStorage.getItem('token');
            await axios.post(`http://localhost:8080/api/users/verify-code?email=${email}&code=${verificationCode}`, {}, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });
            setIsVerified(true);
            setInfoMessage('인증이 완료되었습니다. 이제 비밀번호를 변경할 수 있습니다.');
            setError('');
        } catch (error) {
            console.error('인증 확인 오류:', error.response ? error.response.data : error.message);
            setError('인증번호가 올바르지 않습니다.');
        }
    };

    const handlePasswordChange = async () => {
        try {
            const token = localStorage.getItem('token');
            if (newPassword !== confirmPassword) {
                setError('비밀번호가 일치하지 않습니다.');
                return;
            }
    
            await axios.post(`http://localhost:8080/api/users/password`, null, {
                params: {
                    newPassword: newPassword,
                    confirmPassword: confirmPassword
                },
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
    
            alert("비밀번호가 성공적으로 변경되었습니다.");
            navigate('/mypage'); // 비밀번호 변경 후 마이페이지로 이동
        } catch (error) {
            console.error('비밀번호 변경 오류:', error.response ? error.response.data : error.message);
            setError('비밀번호 변경에 문제가 발생했습니다.');
        }
    };    

    return (
        <>
            <Navbar />
            <div className="password-change-container">
                <h2>비밀번호 변경</h2>
                <p>이메일을 입력하고 인증번호를 받아주세요. 인증이 완료되면 비밀번호를 변경할 수 있습니다.</p>
                {!isVerified ? (
                    <div className="password-change-form">
                        <div className="p-input">
                            <p className="p-input-label">이메일</p>
                            <div className="input-container">
                                <div className="input-err">
                                    <input 
                                        type="text" 
                                        value={email} 
                                        onChange={(e) => setEmail(e.target.value)} 
                                        placeholder="예) fragrance@fragrance.co.kr"
                                    />
                                    {error && <p className="error-message">{error}</p>}
                                    {infoMessage && <p className="info-message">{infoMessage}</p>}
                                </div>
                                <button className="ch-num" onClick={handleEmailVerificationRequest}>인증</button>
                            </div>
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
                                <button className="ch-num" onClick={handleVerificationCheck}>확인</button>
                            </div>
                        </div>
                    </div>
                ) : (
                    <div className="password-change-form">
                        <div className="p-input">
                            <p className="p-input-label">신규 비밀번호</p>
                            <div className="input-container">
                                
                                <input 
                                    type="password" 
                                    value={newPassword} 
                                    onChange={(e) => setNewPassword(e.target.value)} 
                                    placeholder="신규 비밀번호 입력"
                                />
                            </div>
                        </div>
                        <div className="p-input">
                            <p className="p-input-label">신규 비밀번호 확인</p>
                            <div className="input-container">
                                <input 
                                    type="password" 
                                    value={confirmPassword} 
                                    onChange={(e) => setConfirmPassword(e.target.value)} 
                                    placeholder="비밀번호 확인"
                                />
                            </div>
                        </div>
                        <button 
                            className="change-button" 
                            onClick={handlePasswordChange}
                            disabled={!newPassword || newPassword !== confirmPassword}
                        >
                            비밀번호 변경하기
                        </button>
                    </div>
                )}
            </div>
        </>
    );
};

export default ChangePassword;
