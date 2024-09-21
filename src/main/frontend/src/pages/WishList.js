import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom'; // useNavigate import 추가
import Navbar from '../css/Navbar.js';
import '../css/WishList.css';
import { IoIosHeart } from 'react-icons/io';

const WishList = () => {
    const [wishlist, setWishlist] = useState([]);
    const [likedPerfumes, setLikedPerfumes] = useState([]); 
    const navigate = useNavigate(); // useNavigate 훅 사용

    const fetchWishlist = async () => {
        const token = localStorage.getItem('token');
        if (!token) {
            alert("로그인 후 이용해주세요.");
            return;
        }
    
        try {
            const response = await fetch(`http://localhost:8080/api/wishlist`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`, 
                },
            });
    
            const data = await response.json();
            if (!response.ok || !Array.isArray(data.perfumes)) {
                throw new Error('찜 목록을 가져오는 중 오류가 발생했습니다.');
            }
    
            setWishlist(data.perfumes); 
            setLikedPerfumes(data.perfumes.map(item => item.id)); // 향수 아이디 리스트를 업데이트
        } catch (error) {
            console.error('API 호출 중 오류:', error);
            setWishlist([]); // 오류 발생 시 빈 배열로 설정
        }
    };    
    
    const removeFromWishlist = async (perfumeId) => {
        const token = localStorage.getItem('token');
        if (!token) {
            alert("로그인 후 이용해주세요.");
            return;
        }
    
        console.log(`삭제하려는 perfumeId: ${perfumeId}`); 
    
        try {
            const response = await fetch(`http://localhost:8080/api/wishlist/remove?perfumeId=${perfumeId}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${token}`,
                },
            });
    
            if (!response.ok) {
                throw new Error('찜 삭제 중 오류가 발생했습니다.');
            }
    
            // 찜 목록에서 해당 향수 제거
            setLikedPerfumes(prevLiked => prevLiked.filter(id => id !== perfumeId));
            setWishlist(prevWishlist => prevWishlist.filter(item => item.id !== perfumeId));
            console.log("찜 삭제 성공");
        } catch (error) {
            console.error('API 호출 중 오류:', error);
        }
    };

    // 향수 아이템 클릭 시 상세 페이지로 이동
    const handlePerfumeClick = (perfume) => {
        navigate(`/perfumes/${encodeURIComponent(perfume.perfume)}`, { state: { perfume } });
    };

    useEffect(() => {
        fetchWishlist();
    }, []);

    return (
        <>
            <Navbar />
            <div className="wishlist-container">
                <h2>찜 목록</h2>
                <div className="perfume-grid">
                    {wishlist.length === 0 ? (
                        <p>찜한 향수가 없습니다.</p>
                    ) : (
                        wishlist.map((item) => (
                            item && ( 
                                <div 
                                    key={item.id} 
                                    className="perfume-item-2" 
                                    onClick={() => handlePerfumeClick(item)} // 아이템 클릭 시 상세 페이지로 이동
                                >
                                    <img src={item.image || "기본 이미지 URL"} alt={item.perfume || "향수 이미지"} />
                                    <p className="brand">{item.brand || "브랜드 정보 없음"}</p>
                                    <div className="perfume">{item.perfume || "향수 이름 없음"}</div>
                                    <p className="acode">
                                        {Array.isArray(item.acode) 
                                            ? item.acode.map(ac => `#${ac}`).join(' ') 
                                            : "어코드 없음"}
                                    </p>
                                    <div className="heart-icon" onClick={(e) => { 
                                        e.stopPropagation(); // 부모 요소로 이벤트 전파 방지
                                        removeFromWishlist(item.id);
                                    }}>
                                        <IoIosHeart size={25} color='#FC7979' /> 
                                    </div>
                                </div>
                            )
                        ))
                    )}
                </div>
            </div>
        </>
    );
};

export default WishList;
