import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import Navbar from '../css/Navbar.js';
import '../css/PostDetail.css';
import { HiArrowLeft } from 'react-icons/hi'; 

const PostDetail = () => {
    const { postId } = useParams(); // URL에서 postId를 가져옴
    const [post, setPost] = useState(null);
    const [comments, setComments] = useState([]); // 댓글 리스트 저장
    const [newComment, setNewComment] = useState(''); // 새 댓글 저장
    const [editCommentId, setEditCommentId] = useState(null); // 수정 중인 댓글 ID
    const [editCommentContent, setEditCommentContent] = useState(''); // 수정 중인 댓글 내용
    const navigate = useNavigate();

    useEffect(() => {
        const fetchPostAndComments = async () => {
            try {
                const postResponse = await axios.get(`http://localhost:8080/api/community/posts/${postId}`);
                setPost(postResponse.data);

                const commentsResponse = await axios.get(`http://localhost:8080/api/community/comments/${postId}`);
                setComments(commentsResponse.data); // 댓글 리스트 설정
            } catch (error) {
                console.error('게시글 또는 댓글 데이터를 가져오는데 실패했습니다.', error);
            }
        };

        fetchPostAndComments();
    }, [postId]);

    const handleCommentSubmit = async () => {
        const token = localStorage.getItem('token');
        if (!token) {
            alert('로그인 후 이용해주세요.');
            return;
        }

        if (newComment.trim() === '') {
            alert('댓글 내용을 입력해주세요.');
            return;
        }

        try {
            const response = await axios.post(`http://localhost:8080/api/community/comments/${postId}`, {
                content: newComment,
            }, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });
            setComments([response.data, ...comments]); // 새로운 댓글을 배열의 앞쪽에 추가
            setNewComment('');
        } catch (error) {
            console.error('댓글 작성 중 오류가 발생했습니다.', error);
            alert('댓글 작성 중 오류가 발생했습니다.');
        }
    };

    const handleCommentDelete = async (commentId) => {
        const token = localStorage.getItem('token');
        if (!token) {
            alert('로그인 후 이용해주세요.');
            return;
        }

        if (window.confirm('댓글을 삭제하시겠습니까?')) {
            try {
                await axios.delete(`http://localhost:8080/api/community/comments/delete/${commentId}`, {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
                setComments(comments.filter(comment => comment.id !== commentId)); // 댓글 삭제
            } catch (error) {
                console.error('댓글 삭제 중 오류가 발생했습니다.', error);
                alert('댓글 삭제 중 오류가 발생했습니다.');
            }
        }
    };

    const handleCommentEdit = (comment) => {
        setEditCommentId(comment.id);
        setEditCommentContent(comment.content);
    };

    const handleCommentUpdate = async () => {
        const token = localStorage.getItem('token');
        if (!token) {
            alert('로그인 후 이용해주세요.');
            return;
        }

        try {
            await axios.put(`http://localhost:8080/api/community/comments/update/${editCommentId}`, {
                content: editCommentContent,
            }, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });
            setComments(comments.map(comment => 
                comment.id === editCommentId ? { ...comment, content: editCommentContent } : comment
            ));
            setEditCommentId(null);
            setEditCommentContent('');
        } catch (error) {
            console.error('댓글 수정 중 오류가 발생했습니다.', error);
            alert('댓글 수정 중 오류가 발생했습니다.');
        }
    };

    if (!post) {
        return <div>Loading...</div>;
    }

    // HTML 태그 제거하고 순수 텍스트만 추출
    const stripHTMLTags = (htmlString) => {
        const doc = new DOMParser().parseFromString(htmlString, 'text/html');
        return doc.body.textContent || "";
    };

    // 날짜 포맷 함수 (community.js에서 사용한 것과 동일)
    const formatDate = (dateArray) => {
        if (!dateArray || dateArray.length < 3) return '날짜 없음'; // dateArray가 없을 때 처리
        
        const year = dateArray[0]; // 연도
        const month = dateArray[1] + 1; // 월 (0부터 시작하므로 +1 필요)
        const day = dateArray[2]; // 일
        
        return `${year}. ${month}. ${day}.`; // 'yyyy. mm. dd.' 형식으로 변환
    };

    return (
        <>
            <Navbar />
            <div className="post-detail-container">
                <HiArrowLeft 
                    size={35} 
                    onClick={() => navigate('/community')} 
                    style={{ cursor: 'pointer' }} 
                />
                <h2>{post.title}</h2>
                <p>작성자: {post.author}</p>
                <p>작성일: {formatDate(post.createdAt)}</p> {/* 날짜 포맷 함수 적용 */}
                <div className="post-content">
                    {stripHTMLTags(post.content)}
                </div>
                
                <div className="edit-delete">
                    <button onClick={() => 
                        navigate(`/edit/${postId}`, 
                        { state: { previousPage: `/community/posts/${postId}` } })}>
                        수정하기
                    </button>

                    <button onClick={async () => {
                        const token = localStorage.getItem('token');
                        if (!token) {
                            alert('로그인 후 이용해주세요.');
                            return;
                        }

                        if (window.confirm('정말 삭제하시겠습니까?')) {
                            try {
                                await axios.delete(`http://localhost:8080/api/community/${postId}`, {
                                    headers: {
                                        'Authorization': `Bearer ${token}`
                                    }
                                });
                                alert('게시글이 삭제되었습니다.');
                                navigate('/community');
                            } catch (error) {
                                console.error('게시글 삭제 중 오류가 발생했습니다.', error);
                                alert('게시글 삭제 중 오류가 발생했습니다.');
                            }
                        }
                    }} style={{ color: 'red' }}>삭제하기</button>
                </div>

                {/* 댓글 리스트 */}
                <div className="comments-section">
                    <h3>댓글</h3>
                    <div className="new-comment-section">
                    <input 
                        type="text" 
                        value={newComment} 
                        onChange={(e) => setNewComment(e.target.value)} 
                        placeholder="댓글을 입력하세요" 
                    />
                    <button onClick={handleCommentSubmit}>작성하기</button>
                </div>
                    {comments.map(comment => (
                        <div key={comment.id} className="comment">
                            {editCommentId === comment.id ? (
                                <>
                                    <input 
                                        type="text" 
                                        value={editCommentContent} 
                                        onChange={(e) => setEditCommentContent(e.target.value)}
                                    />
                                    <button onClick={handleCommentUpdate}>수정 완료</button>
                                </>
                            ) : (
                                <>
                                    <p>{comment.content}</p>
                                    <div className="comment-e-d">
                                        <button className="comment-e" onClick={() => handleCommentEdit(comment)}>수정</button>
                                        <button className="comment-d"onClick={() => handleCommentDelete(comment.id)}>삭제</button>
                                    </div>
                                </>
                            )}
                        </div>
                    ))}
                </div>

                
            </div>
        </>
    );
};

export default PostDetail;
