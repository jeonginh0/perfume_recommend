import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import '../css/ChangeNickname.css';
import Navbar from '../css/Navbar.js';

const ChangeNickname = () => {
    const [newNickname, setNewNickname] = useState('');
    const [error, setError] = useState('');
    const [userId, setUserId] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        // 사용자 정보를 가져와 ID를 설정
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
                setError('사용자 정보를 가져오는데 실패했습니다.');
            }
        };

        fetchUserInfo();
    }, []);

    const handleNicknameChange = async () => {
        try {
            const token = localStorage.getItem('token');
            if (!userId) {
                setError('사용자 정보를 불러오지 못했습니다.');
                return;
            }

            // URL 쿼리 파라미터로 newNickname 전달
            const response = await axios.post(
                `http://localhost:8080/api/users/nickname?newNickname=${newNickname}`,
                {},
                {
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json'
                    }
                }
            );

            console.log(response.data);

            // 성공적으로 닉네임이 변경되면 마이페이지로 이동
            navigate('/mypage');
        } catch (error) {
            console.error('닉네임 변경 오류:', error.response ? error.response.data : error.message);

            // 중복된 닉네임 오류 처리
            if (error.response && error.response.status === 500) {
                alert('이미 존재하는 닉네임입니다. 다른 닉네임을 입력해주세요.');
            } else {
                setError('중복된 닉네임이 있거나 요청에 문제가 있습니다.');
            }
        }
    };

    return (
        <>
            <Navbar />
            <div className="nickname-change-container">
                <h2>닉네임 변경</h2>
                <p className="nick-ch-p ">변경할 닉네임을 입력해주세요.</p>
                <div className="p-input">
                    <p className="p-input-nick">닉네임</p>
                    <div className="input-container">
                        <input 
                            type="text" 
                            value={newNickname} 
                            onChange={(e) => setNewNickname(e.target.value)} 
                            placeholder="예) fragrance"
                        />
                        {error && <p className="error-message">{error}</p>}
                    </div>
                </div>
                <button 
                    className="change-button" 
                    onClick={handleNicknameChange}
                    disabled={!newNickname} // 입력이 없을 때 버튼 비활성화
                >
                    닉네임 변경하기
                </button>
            </div>
        </>
    );
};

export default ChangeNickname;
