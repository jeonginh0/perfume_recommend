import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import Navbar from '../css/Navbar.js';
import '../css/PostDetail.css';
import { HiArrowLeft } from 'react-icons/hi';
import { CgProfile } from 'react-icons/cg'; // User icon for comments

const PostDetail = () => {
    const { postId } = useParams(); 
    const [post, setPost] = useState(null); // Post data
    const [comments, setComments] = useState([]); // List of comments
    const [newComment, setNewComment] = useState(''); // New comment input
    const [editCommentId, setEditCommentId] = useState(null); // ID of the comment being edited
    const [editCommentContent, setEditCommentContent] = useState(''); // Content of the comment being edited
    const [currentUserId, setCurrentUserId] = useState(null); // ID of the current logged-in user
    const [currentUserNickname, setCurrentUserNickname] = useState(''); // Nickname of the current user
    const [authorNickname, setAuthorNickname] = useState(''); // Author's nickname
    const [isLoggedIn, setIsLoggedIn] = useState(false); // Login status
    const [nickname, setNickname] = useState(''); // Logged-in user's nickname

    const navigate = useNavigate();

    useEffect(() => {
        const token = localStorage.getItem('token');
        if (token) {
            axios.get('http://localhost:8080/api/users/me', {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            })
            .then(response => {
                if (response.data && response.data.nickname) {
                    setNickname(response.data.nickname); 
                    setIsLoggedIn(true);
                } else {
                    console.error('유저 닉네임이 응답에 포함되어 있지 않습니다.');
                    setIsLoggedIn(false);
                }
            })
            .catch(error => {
                console.error('유저 정보를 가져오는데 실패했습니다.', error);
            });
        }
    }, []);

    useEffect(() => {
        const fetchPostAndComments = async () => {
            try {
                const postResponse = await axios.get(`http://localhost:8080/api/community/posts/${postId}`);
                setPost(postResponse.data);
                
                const authorResponse = await axios.get(`http://localhost:8080/api/users/nickname/${postResponse.data.userId}`);
                setAuthorNickname(authorResponse.data); 
                
                const storedComments = localStorage.getItem(`comments_${postId}`);
                if (storedComments) {
                    setComments(JSON.parse(storedComments));
                } else {
                    const commentsResponse = await axios.get(`http://localhost:8080/api/community/comments/${postId}`);
                    setComments(commentsResponse.data); 
                    localStorage.setItem(`comments_${postId}`, JSON.stringify(commentsResponse.data)); 
                }

                const token = localStorage.getItem('token');
                if (token) {
                    const userResponse = await axios.get('http://localhost:8080/api/users/me', {
                        headers: {
                            'Authorization': `Bearer ${token}`
                        }
                    });
                    setCurrentUserId(userResponse.data.id); 
                    setCurrentUserNickname(userResponse.data.nickname);
                }
            } catch (error) {
                console.error('게시글 또는 댓글 데이터를 가져오는데 실패했습니다.', error);
            }
        };
    
        fetchPostAndComments();
    }, [postId]);

    useEffect(() => {
        if (comments.length > 0) {
            localStorage.setItem(`comments_${postId}`, JSON.stringify(comments));
        }
    }, [comments, postId]);

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

            const updatedComments = [{ ...response.data, nickname: currentUserNickname }, ...comments];
            setComments(updatedComments);
            localStorage.setItem(`comments_${postId}`, JSON.stringify(updatedComments));
            setNewComment('');
        } catch (error) {
            console.error('Error submitting comment:', error);
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
                const updatedComments = comments.filter(comment => comment.id !== commentId);
                setComments(updatedComments);
                localStorage.setItem(`comments_${postId}`, JSON.stringify(updatedComments));
            } catch (error) {
                console.error('Error deleting comment:', error);
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

            const updatedComments = comments.map(comment => 
                comment.id === editCommentId ? { ...comment, content: editCommentContent } : comment
            );
            setComments(updatedComments);
            localStorage.setItem(`comments_${postId}`, JSON.stringify(updatedComments));
            setEditCommentId(null);
            setEditCommentContent('');
        } catch (error) {
            console.error('Error updating comment:', error);
            alert('댓글 수정 중 오류가 발생했습니다.');
        }
    };

    if (!post) {
        return <div>Loading...</div>;
    }

    // Function to remove HTML tags and return plain text
    const stripHTMLTags = (htmlString) => {
        const doc = new DOMParser().parseFromString(htmlString, 'text/html');
        return doc.body.textContent || "";
    };

    // Function to format date from array to 'yyyy. mm. dd.' format
    const formatDate = (dateArray) => {
        if (!dateArray || dateArray.length < 3) return '날짜 없음';
        const year = dateArray[0];
        const month = dateArray[1];
        const day = dateArray[2];
        return `${year}. ${month}. ${day}.`;
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
                <p>작성자: {authorNickname}</p>
                <p>작성일: {formatDate(post.createdAt)}</p> 
                <div className="post-content">
                    {stripHTMLTags(post.content)}
                </div>
                
                {currentUserId && post && String(currentUserId) === String(post.userId) && (
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
                )}

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
                            <div className="comment-content">
                                <div className="comment-au-con">
                                <CgProfile size={40} className="comment-icon" />
                                    <p className="comment-author">{comment.nickname || '사용자'}</p>
                                    <p>{comment.content}</p>
                                </div>
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
                                        <div className="comment-e-d">
                                            <button className="comment-e" onClick={() => handleCommentEdit(comment)}>수정</button>
                                            <button className="comment-d" onClick={() => handleCommentDelete(comment.id)}>삭제</button>
                                        </div>
                                    </>
                                )}
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </>
    );
};

export default PostDetail;
