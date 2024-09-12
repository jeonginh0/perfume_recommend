import React, { useEffect, useState } from 'react';
import Navbar from '../css/Navbar.js';
import '../css/WishList.css';
import { IoIosHeartEmpty, IoIosHeart } from 'react-icons/io'; // 좋아요 아이콘 가져오기

const WishList = () => {
    const [wishlist, setWishlist] = useState([]);

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

            if (!response.ok) {
                throw new Error('찜 목록을 가져오는 중 오류가 발생했습니다.');
            }

            const data = await response.json();
            setWishlist(data);
        } catch (error) {
            console.error('API 호출 중 오류:', error);
        }
    };

    const removeFromWishlist = async (perfumeId) => {
        const token = localStorage.getItem('token');
        if (!token) {
            alert("로그인 후 이용해주세요.");
            return;
        }

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
            setWishlist(prevWishlist => prevWishlist.filter(item => item.perfume?.id !== perfumeId));
            console.log("찜 삭제 성공");
        } catch (error) {
            console.error('API 호출 중 오류:', error);
        }
    };

    const toggleLike = (perfume) => {
        const perfumeId = perfume.id;
        removeFromWishlist(perfumeId);
    };

    // 컴포넌트가 마운트될 때 찜 목록을 가져옴
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
                            item.perfume && item.perfume.id && ( // perfume과 id가 존재하는 경우에만 렌더링
                                <div key={item.perfume.id} className="perfume-item-2">
                                    <img src={item.perfume.image || "기본 이미지 URL"} alt={item.perfume.perfume || "향수 이미지"} />
                                    <p className="brand">{item.perfume.brand || "브랜드 정보 없음"}</p>
                                    <div className="perfume">{item.perfume.perfume || "향수 이름 없음"}</div>
                                    <p className="acode">
                                        {Array.isArray(item.perfume.acode)
                                            ? item.perfume.acode.map(ac => `#${ac}`).join(' ') // 어코드 앞에 #을 붙이고 띄어쓰기 추가
                                            : "어코드 없음"}
                                    </p>
                                    <div className="heart-icon" onClick={() => toggleLike(item.perfume)}>
                                        <IoIosHeart size={25} color='#FC7979' /> {/* 찜 목록에서는 항상 빨간 하트를 표시 */}
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
