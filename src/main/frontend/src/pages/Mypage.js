import React from 'react';
import Navbar from '../css/Navbar.js';
import '../css/Mypage.css';
import { CgProfile } from "react-icons/cg";

const Mypage = () => {

    return (
        <>
            <Navbar/>
            <div className="mypage-container">
                <div className="mypage-form">
                    <h2>마이페이지</h2>
                    <div className="mypage-username">
                        <CgProfile size={45} />
                        <p className="username">닉네임</p>
                        <button className="ch-name-button">닉네임 변경</button>
                    </div>
                    <div className="mypage-email">
                        <p className="email-p">이메일</p>
                        <p className="email">fragrance@fragrance.co.kr</p>
                    </div>
                    <div className="mypage-number">
                        <p className="number-p">전화번호</p>
                        <p className="number">010-1234-5678</p>
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