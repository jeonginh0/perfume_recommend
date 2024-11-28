import React from 'react';
import { Link } from 'react-router-dom';
import '../css/SignupSuccess.css';
import Success from '../img/success.png';

const SignupSuccess = () => {
    return (
        <>
           <header className="signsuccess-header">
                <div className="logo-nav-container">
                <Link to="/">
                    <div className="logo">fragrance</div>
                </Link>
                </div>
            </header>

            <div className="signup-complete-container">
                <div className="success-icon">
                    <img src={Success} alt="Success Icon" />
                </div>
                <h2>로그인이 완료되었습니다.</h2>
                <Link to="/login">
                    <button className="go-login-button">
                        <p>메인 화면</p>
                    </button>
                </Link>
            </div>
        </>
    );
};

export default SignupSuccess;