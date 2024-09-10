import React, { useEffect, useState } from 'react';
import Navbar from '../css/Navbar.js';
import '../css/WishList.css';

const WishList = () => {
    const [wishlist, setWishlist] = useState([]);

    const fetchWishlist = async () => {
        const token = localStorage.getItem('token');
        const userId = localStorage.getItem('userId');
        if (!token || !userId) {
            alert("로그인 후 이용해주세요.");
            return;
        }
    
        try {
            const response = await fetch(`http://localhost:8080/api/wishlist/${userId}`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`, // 이 부분에서 토큰을 올바르게 설정
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
    

    const addToWishlist = async (perfumeId) => {
        const token = localStorage.getItem('token');
        const userId = localStorage.getItem('userId');
        if (!token || !userId) {
            alert("로그인 후 이용해주세요.");
            return;
        }
    
        try {
            const response = await fetch(`http://localhost:8080/api/wishlist?userId=${userId}&perfumeId=${perfumeId}`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                },
            });
    
            if (!response.ok) {
                throw new Error('찜 추가 중 오류가 발생했습니다.');
            }
    
            console.log("찜 성공");
            fetchWishlist(); // 찜 추가 후 목록을 다시 불러옴
        } catch (error) {
            console.error('API 호출 중 오류:', error);
        }
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
                        wishlist.map(perfume => (
                            <div key={perfume.perfumeId} className="perfume-item-2">
                                <img src={perfume.image} alt={perfume.perfume} />
                                <p className="brand">{perfume.brand}</p>
                                <div className="perfume">{perfume.perfume}</div>
                            </div>
                        ))
                    )}
                </div>
            </div>
        </>
    );
};

export default WishList;
