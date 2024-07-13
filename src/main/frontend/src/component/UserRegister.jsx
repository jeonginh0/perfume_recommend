// src/components/RegisterForm.js
import React, { useState } from 'react';
import axios from 'axios';

const RegisterForm = () => {
    const [formData, setFormData] = useState({
        nickname: '',
        email: '',
        password: '',
        phoneNumber: ''
    });

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prevState => ({
            ...prevState,
            [name]: value
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.post('http://localhost:8080/api/users/register', formData);
            console.log('Registration successful!', response.data);
            // 회원가입 성공 후 추가적인 처리 (예: 로그인 페이지로 리다이렉트)
        } catch (error) {
            console.error('Registration failed!', error.response.data);
            // 에러 처리 (예: 오류 메시지 표시)
        }
    };

    return (
        <div>
            <h2>회원가입</h2>
            <form onSubmit={handleSubmit}>
                <input type="text" name="nickname" placeholder="닉네임" value={formData.nickname} onChange={handleChange} required />
                <input type="email" name="email" placeholder="이메일" value={formData.email} onChange={handleChange} required />
                <input type="password" name="password" placeholder="비밀번호" value={formData.password} onChange={handleChange} required />
                <input type="tel" name="phoneNumber" placeholder="휴대폰번호" value={formData.phoneNumber} onChange={handleChange} required />
                <button type="submit">회원가입</button>
            </form>
        </div>
    );
};

export default RegisterForm;
