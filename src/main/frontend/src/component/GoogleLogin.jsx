import React from 'react';
import {GoogleLogin, useGoogleLogin} from 'react-google-login';
import axios from "axios";


const GoogleLoginComponent = () => {
    const googleLogin = useGoogleLogin({
        onSuccess: async (tokenResponse) => {
            try {
                // Google에서 받은 액세스 토큰으로 사용자 정보 가져오기
                const userInfo = await axios.get(
                    'https://www.googleapis.com/oauth2/v3/userinfo',
                    { headers: { Authorization: `Bearer ${tokenResponse.access_token}` } }
                );

                // 백엔드로 ID 토큰 전송
                const response = await axios.post('http://localhost:8000/api/login/google-login', {
                    token: tokenResponse.access_token,
                });

                console.log('Login successful:', response.data);
                // 여기서 로그인 성공 후 처리 (예: 상태 업데이트, 리다이렉션 등)
            } catch (error) {
                console.error('Login failed:', error);
            }
        },
        onError: (error) => console.error('Login Failed:', error),
    });

    const responseErrorGoogle = (error) => {
        console.error('구글 로그인 오류:', error);
    };

    return (
        <GoogleLoButtongin
            clientId="685614494989-7vrchp7iuhaqmdgntr061dcnresj2q25.apps.googleusercontent.com" // 구글 API 콘솔에서 발급받은 클라이언트 ID
            buttonText="구글로 로그인"
            onSuccess={responseGoogle}
            onFailure={responseErrorGoogle}
            cookiePolicy={'single_host_origin'}
        />
    );
};

export default GoogleLoginComponent;

