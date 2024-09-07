import React, { useState } from 'react';
import '../css/Community.css';
import Navbar from '../css/Navbar.js';
import { IoSearchSharp } from 'react-icons/io5';
import { FiChevronsLeft, FiChevronLeft, FiChevronRight, FiChevronsRight } from 'react-icons/fi';

const Community = () => {

    // 더미
    const posts = Array.from({ length: 120 }, (_, index) => ({
        number: 120 - index,
        title: `게시글 제목 ${120 - index}`,
        author: `작성자 ${index % 10 + 1}`,
        date: `2024-07-${(index % 31) + 1}` 
    }));

    const [currentPage, setCurrentPage] = useState(1);
    const postsPerPage = 10; 
    const pageGroupSize = 10; 

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

    // 첫 페이지, 이전 페이지, 다음 페이지, 마지막 페이지 버튼 핸들러
    const handleFirstPage = () => setCurrentPage(1);
    const handlePreviousPage = () => setCurrentPage((prevPage) => Math.max(prevPage - 1, 1));
    const handleNextPage = () => setCurrentPage((prevPage) => Math.min(prevPage + 1, totalPages));

    // <<, >> 그룹 이동 핸들러
    const handlePreviousPageGroup = () => {
        const previousGroupPage = Math.max(1, startPage - pageGroupSize);
        setCurrentPage(previousGroupPage); // 이전 그룹의 첫 페이지로 이동
    };

    const handleNextPageGroup = () => {
        const nextGroupPage = Math.min(startPage + pageGroupSize, totalPages);
        setCurrentPage(nextGroupPage); // 다음 그룹의 첫 페이지로 이동
    };

    const handleLastPage = () => setCurrentPage(totalPages);

    // 페이지네이션 버튼 생성
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

                        {currentPosts.map((post) => (
                            <div className="table-row" key={post.number}>
                                <div className="row-number">{post.number}</div>
                                <div className="row-title">{post.title}</div>
                                <div className="row-author">{post.author}</div>
                                <div className="row-date">{post.date}</div>
                            </div>
                        ))}
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
