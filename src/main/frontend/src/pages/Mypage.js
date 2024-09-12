import React, { useEffect, useState } from 'react';
import Navbar from '../css/Navbar.js';
import '../css/Mypage.css';
import { CgProfile } from "react-icons/cg";
import axios from 'axios';

const Mypage = () => {
    const [userInfo, setUserInfo] = useState({
        nickname: '',
        email: '',
        phoneNumber: ''
    });

    useEffect(() => {
        const fetchUserInfo = async () => {
            try {
                const token = localStorage.getItem('token');
                const response = await axios.get('http://localhost:8080/api/users/me', {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
                console.log(response.data);
                
                setUserInfo({
                    nickname: response.data.nickname,
                    email: response.data.email,
                    phoneNumber: response.data.phoneNumber
                });
            } catch (error) {
                console.error('사용자 정보를 가져오는 중 오류가 발생했습니다:', error);
            }
        };
    
        fetchUserInfo();
    }, []);

    return (
        <>
            <Navbar />
            <div className="mypage-container">
                <div className="mypage-form">
                    <h2>마이페이지</h2>
                    <div className="mypage-username">
                        <CgProfile size={45} />
                        <p className="username">{userInfo.nickname}</p>
                        <button className="ch-name-button">닉네임 변경</button>
                    </div>
                    <div className="mypage-email">
                        <p className="email-p">이메일</p>
                        <p className="email">{userInfo.email}</p>
                    </div>
                    <div className="mypage-number">
                        <p className="number-p">전화번호</p>
                        <p className="number">{userInfo.phoneNumber}</p>
                        <button className="ch-name-button">전화번호 변경</button>
                    </div>
                    <div className="mypage-password">
                        <p className="password-p">비밀번호</p>
                        <button className="ch-name-button">비밀번호 변경</button>
                    </div>
                    <div className="ses">
                        <p className="ses-p">회원 탈퇴</p>
                    </div>
                </div>
            </div>
        </>
    );
};

export default Mypage;
