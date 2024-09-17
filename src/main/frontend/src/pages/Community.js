import React, { useState, useEffect } from 'react';
import '../css/Community.css';
import Navbar from '../css/Navbar.js';
import { IoSearchSharp } from 'react-icons/io5';
import { FiChevronsLeft, FiChevronLeft, FiChevronRight, FiChevronsRight } from 'react-icons/fi';
import { LuPencilLine } from 'react-icons/lu';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const Community = () => {
    const navigate = useNavigate();
    const [posts, setPosts] = useState([]);
    const [users, setUsers] = useState({}); // 사용자 정보를 저장할 상태
    const [currentPage, setCurrentPage] = useState(1);
    const postsPerPage = 10;
    const pageGroupSize = 10;

    useEffect(() => {
        const fetchPosts = async () => {
            try {
                const response = await axios.get('http://localhost:8080/api/community/posts');
                setPosts(response.data);
                console.log('응답 데이터:', response.data);
    
                // 게시물에 포함된 userId 목록 추출
                const userIds = [...new Set(response.data.map((post) => post.userId))];
    
                userIds.forEach(async (userId) => {
                    if (!users[userId]) {
                        try {
                            const userResponse = await axios.get(`http://localhost:8080/api/users/nickname/${userId}`);
                            setUsers((prevUsers) => ({
                                ...prevUsers,
                                [userId]: userResponse.data,
                            }));
                        } catch (error) {
                            console.error('사용자 이름을 가져오는데 실패했습니다.', error);
                        }
                    }
                });
            } catch (error) {
                console.error('게시글 데이터를 가져오는데 실패했습니다.', error);
            }
        };
    
        fetchPosts();
    }, [users]);
    
    // 날짜 포맷 함수 수정
    const formatDate = (dateArray) => {
        if (!dateArray || dateArray.length < 3) return '날짜 없음'; // dateArray가 없을 때 처리
        
        const year = dateArray[0]; // 연도
        const month = dateArray[1]; // 월 (0부터 시작하므로 +1 필요)
        const day = dateArray[2]; // 일
        
        return `${year}. ${month}. ${day}.`; // 'yyyy. mm. dd.' 형식으로 변환
    };

    // 게시글 클릭 시 상세 페이지로 이동하는 함수
    const handlePostClick = (postId) => {
        navigate(`/community/${postId}`); // 해당 글 ID를 기반으로 상세 페이지로 이동
    };

    const totalPages = Math.ceil(posts.length / postsPerPage);
    const indexOfLastPost = currentPage * postsPerPage;
    const indexOfFirstPost = indexOfLastPost - postsPerPage;
    const currentPosts = posts.slice(indexOfFirstPost, indexOfLastPost);
    const currentPageGroup = Math.ceil(currentPage / pageGroupSize);
    const startPage = (currentPageGroup - 1) * pageGroupSize + 1;
    const endPage = Math.min(startPage + pageGroupSize - 1, totalPages);

    const handlePageChange = (pageNumber) => {
        if (pageNumber >= 1 && pageNumber <= totalPages) {
            setCurrentPage(pageNumber);
        }
    };

    const handleFirstPage = () => setCurrentPage(1);
    const handlePreviousPage = () => setCurrentPage((prevPage) => Math.max(prevPage - 1, 1));
    const handleNextPage = () => setCurrentPage((prevPage) => Math.min(prevPage + 1, totalPages));
    const handlePreviousPageGroup = () => setCurrentPage(Math.max(1, startPage - pageGroupSize));
    const handleNextPageGroup = () => setCurrentPage(Math.min(startPage + pageGroupSize, totalPages));
    const handleLastPage = () => setCurrentPage(totalPages);

    const renderPagination = () => {
        const pageNumbers = [];
        for (let i = startPage; i <= endPage; i++) {
            pageNumbers.push(
                <button
                    key={i}
                    className={`pagination-button ${i === currentPage ? 'active' : ''}`}
                    onClick={() => handlePageChange(i)}
                >
                    {i}
                </button>
            );
        }
        return pageNumbers;
    };

    // 로그인 여부를 확인하고, 비로그인 상태에서는 글작성 페이지로 가지 않음
    const goToWritePage = () => {
        const token = localStorage.getItem('token'); // 로그인 토큰 확인
        if (!token) {
            alert('로그인 후 이용해주세요.'); // 비로그인 상태에서 알림 표시
            navigate('/login'); // 로그인 페이지로 이동
        } else {
            navigate('/write'); // 로그인 상태에서만 글쓰기 페이지로 이동
        }
    };

    return (
        <>
            <Navbar />
            <div className="Community-container">
                <div className="Community-form">
                    <div className="Community-header">
                        <p>Community</p>
                    </div>
                    <div className="community-hd">
                        <div className="post-size">
                            <p>총 {posts.length}개의 글이 있습니다.</p>
                        </div>
                        <div className="search-bar">
                            <input
                                type="text"
                                placeholder="Search"
                            />
                            <button><IoSearchSharp /></button>
                        </div>
                    </div>

                    <div className="community-table">
                        <div className="table-header">
                            <div className="header-number">번호</div>
                            <div className="header-title">제목</div>
                            <div className="header-author">작성자</div>
                            <div className="header-date">날짜</div>
                        </div>

                        {currentPosts.map((post, index) => (
                            <div 
                                className="table-row" 
                                key={post.id} 
                                onClick={() => handlePostClick(post.id)} // 게시글 클릭 시 상세 페이지로 이동
                                style={{ cursor: 'pointer' }} // 커서를 포인터로 변경
                            >
                                <div className="row-number">{index + 1 + (currentPage - 1) * postsPerPage}</div>
                                <div className="row-title">{post.title}</div>
                                <div className="row-author">{users[post.userId] || "익명"}</div> {/* 사용자 이름 표시 */}
                                <div className="row-date">{formatDate(post.createdAt)}</div>
                            </div>
                        ))}
                    </div>

                    <div className="write-button-container" onClick={goToWritePage}>
                        <LuPencilLine size={15} />
                        <p>글쓰기</p>
                    </div>

                    <div className="pagination">
                        <button onClick={handleFirstPage}><FiChevronsLeft /></button>
                        <button onClick={handlePreviousPage}><FiChevronLeft /></button>
                        {renderPagination()}
                        <button onClick={handleNextPage}><FiChevronRight /></button>
                        <button onClick={handleLastPage}><FiChevronsRight /></button>
                    </div>
                </div>
            </div>
        </>
    );
};

export default Community;
