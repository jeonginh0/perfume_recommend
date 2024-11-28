import React from 'react';
import GoogleLoginButton from './GoogleLogin';

const MainComponent = () => {
    const handleLoginSuccess = (response) => {
        // 로그인 성공 시 처리할 내용을 여기에 작성
        console.log('로그인 성공:', response);
    };

    const handleLoginFailure = (error) => {
        // 로그인 실패 시 처리할 내용을 여기에 작성
        console.error('로그인 실패:', error);
    };

    return (
        <div>
            <h1>구글 OAuth 2.0 로그인 테스트</h1>
            <GoogleLoginButton />
        </div>
    );
};

export default MainComponent;