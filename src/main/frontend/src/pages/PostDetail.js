import React, { useEffect, useState } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom'; // useLocation 추가
import axios from 'axios';
import Navbar from '../css/Navbar.js';
import ReactQuill from 'react-quill';
import 'react-quill/dist/quill.snow.css';
import '../css/PostEdit.css';
import { HiArrowLeft } from 'react-icons/hi';

const PostEdit = () => {
    const { postId } = useParams(); // URL에서 postId를 가져옴
    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');
    const navigate = useNavigate();
    const location = useLocation(); // useLocation으로 state 접근

    useEffect(() => {
        const fetchPost = async () => {
            try {
                const response = await axios.get(`http://localhost:8080/api/community/posts/${postId}`);
                setTitle(response.data.title);
                setContent(response.data.content);
            } catch (error) {
                console.error('게시글 데이터를 가져오는데 실패했습니다.', error);
            }
        };

        fetchPost();
    }, [postId]);

    const handleTitleChange = (e) => {
        setTitle(e.target.value);
    };

    const handleContentChange = (value) => {
        setContent(value);
    };

    const handleSubmit = async () => {
        const token = localStorage.getItem('token');
        if (!token) {
            alert('로그인 후 이용해주세요.');
            return;
        }

        try {
            const response = await axios.put(`http://localhost:8080/api/community/posts/${postId}`, {
                title,
                content,
            }, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });

            if (response.status === 200) {
                alert('글이 성공적으로 수정되었습니다.');
                navigate(`/community/${postId}`); // 글 수정 후 해당 글의 상세 페이지로 이동
            } else {
                throw new Error('글 수정 중 오류가 발생했습니다.');
            }
        } catch (error) {
            console.error('글 수정 중 오류가 발생했습니다.', error);
            alert('글 수정 중 오류가 발생했습니다.');
        }
    };

    return (
        <>
            <Navbar />
            <div className="post-edit-container">
                <HiArrowLeft
                    size={35}
                    onClick={() => navigate(`/community/${postId}`)} // postId를 사용하여 상세 페이지로 이동
                    style={{ cursor: 'pointer' }}
                />
                <h2>글 수정</h2>
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
                        value={content}
                        onChange={handleContentChange}
                        theme="snow"
                        placeholder="내용을 입력하세요"
                    />
                </div>
                <button className="submit-button" onClick={handleSubmit}>수정하기</button>
            </div>
        </>
    );
};

export default PostEdit;