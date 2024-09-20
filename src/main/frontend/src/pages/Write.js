import React, { useState, useRef, useEffect } from 'react';
import ReactQuill from 'react-quill';
import 'react-quill/dist/quill.snow.css';
import { useNavigate } from 'react-router-dom';
import Navbar from '../css/Navbar.js';
import axios from 'axios'; 
import '../css/Write.css';
import { HiArrowLeft } from 'react-icons/hi'; 

const Write = () => {
    const [title, setTitle] = useState('');
    const [content, setContent] = useState(''); // 리치 텍스트 내용 저장
    const [nickname, setNickname] = useState(''); // 유저 이름 저장
    const quillRef = useRef(null); // Quill ref 생성
    const navigate = useNavigate(); 

    // 유저 정보 가져오기
    useEffect(() => {
        const fetchUserData = async () => {
            const token = localStorage.getItem('token');
            if (!token) {
                alert('로그인 후 이용해주세요.');
                return;
            }
            try {
                const response = await axios.get('http://localhost:8080/api/users/me', {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
                setNickname(response.data.nickname); // 유저 이름 저장
            } catch (error) {
                console.error('유저 정보를 가져오는 중 오류가 발생했습니다:', error);
            }
        };
        fetchUserData();
    }, []);

    const handleTitleChange = (e) => {
        setTitle(e.target.value);
    };

    const handleContentChange = (value) => {
        setContent(value); // Quill 에디터에서 입력된 내용을 저장
    };

    const handleSubmit = async () => {
        const token = localStorage.getItem('token');
        if (!token) {
            alert('로그인 후 이용해주세요.');
            return;
        }
    
        if (!title || !content) {
            alert('제목과 내용을 입력해주세요.');
            return;
        }
    
        try {
            const response = await axios.post('http://localhost:8080/api/community/posts', {
                title,
                content,
            }, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });
        
            console.log('요청 데이터:', { title, content });
            console.log('응답 데이터:', response.data);
        
            if (response.status === 200) {
                alert('글이 성공적으로 작성되었습니다.');
                navigate('/community');
            } else {
                throw new Error('글 작성 중 오류가 발생했습니다.');
            }
        } catch (error) {
            console.error('글 작성 중 오류가 발생했습니다:', error);
            alert('글 작성 중 오류가 발생했습니다.');
        }
        
    };

    return (
        <>
            <Navbar />
            <div className="write-container">
                <div className="write-form">
                    <HiArrowLeft 
                        size={35} 
                        onClick={() => navigate(`/community`)}
                        style={{ cursor: 'pointer' }} 
                    />
                    <h2>글 작성</h2>
                    <div className="form-group">
                        <label>제목</label>
                        <input
                            type="text"
                            value={title}
                            onChange={handleTitleChange}
                            placeholder="제목을 입력하세요"
                        />
                    </div>
                    <div className="form-group">
                        <label>내용</label>
                        <ReactQuill
                            ref={quillRef} // 이 ref는 quill 편집기와 연결됨
                            value={content}
                            onChange={handleContentChange}
                            theme="snow" // 기본 테마
                            placeholder="내용을 입력하세요"
                        />
                    </div>
                    <button className="submit-button" onClick={handleSubmit}>작성하기</button>
                </div>
            </div>
        </>
    );
};

export default Write;
