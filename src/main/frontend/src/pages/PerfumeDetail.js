import React, { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom'; // state에서 데이터를 받아오기 위해 사용
import Navbar from '../css/Navbar.js';
import '../css/PerfumeDetail.css';
import { IoIosHeartEmpty, IoIosHeart } from 'react-icons/io';
import { CgProfile } from "react-icons/cg"; // CgProfile import 추가
import { Link } from 'react-router-dom';

const PerfumeDetail = () => {
    const location = useLocation(); // useLocation을 통해 state를 받아옴
    const perfume = location.state?.perfume; // 전달된 perfume 데이터를 받음
    const [likedPerfumes, setLikedPerfumes] = useState([]); 

    // 로그인 상태 확인 후 찜한 향수 목록 가져오기
    useEffect(() => {
        const token = localStorage.getItem('token');
        if (token) {
            fetchLikedPerfumes();
        }
    }, []);

    // 찜한 향수 목록 가져오기
    const fetchLikedPerfumes = async () => {
        const token = localStorage.getItem('token');
        try {
            const response = await fetch('http://localhost:8080/api/wishlist', {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`,
                },
            });
            const data = await response.json();
            if (data && Array.isArray(data.perfumes)) {
                const perfumeIds = data.perfumes.map(item => item.id);
                setLikedPerfumes(perfumeIds);
            }
        } catch (error) {
            console.error('찜 목록 불러오기 실패:', error);
        }
    };

    // 찜 추가 기능
    const addToWishlist = async (perfumeId) => {
        const token = localStorage.getItem('token');
        if (!token) {
            alert("로그인 후 사용 가능합니다.");
            return;
        }
        try {
            const response = await fetch(`http://localhost:8080/api/wishlist?perfumeId=${perfumeId}`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                },
            });
            if (!response.ok) {
                throw new Error('찜 추가 중 오류가 발생했습니다.');
            }
            // 성공적으로 추가되면 상태 업데이트
            setLikedPerfumes(prevLiked => [...prevLiked, perfumeId]);
            console.log("찜이 성공적으로 추가되었습니다.");
        } catch (error) {
            console.error('API 호출 중 오류:', error);
        }
    };
    
    // 찜 해제 기능
    const removeFromWishlist = async (perfumeId) => {
        const token = localStorage.getItem('token');
        if (!token) {
            alert("로그인 후 사용 가능합니다.");
            return;
        }
        try {
            const response = await fetch(`http://localhost:8080/api/wishlist/remove?perfumeId=${perfumeId}`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                },
            });
            if (!response.ok) {
                throw new Error('찜 삭제 중 오류가 발생했습니다.');
            }
            // 성공적으로 삭제되면 상태 업데이트
            setLikedPerfumes(prevLiked => prevLiked.filter(id => id !== perfumeId));
            console.log("찜이 성공적으로 삭제되었습니다.");
        } catch (error) {
            console.error('API 호출 중 오류:', error);
        }
    };

    // 찜 토글 기능
    const toggleLike = async (id) => {
        const token = localStorage.getItem('token');
        if (!token) {
            alert("로그인 후 이용 가능합니다.");
            return;
        }

        if (likedPerfumes.includes(id)) {
            await removeFromWishlist(id);
        } else {
            await addToWishlist(id);
        }
    };

    if (!perfume) {
        return <div>해당 향수를 찾을 수 없습니다.</div>;
    }

    return (
        <>
            <Navbar />
            <div className="perfume-detail-container">
                <div className="perfume-detail">
                    <div className="perfume-detail-page">
                        <Link to="/"><p>프레그런스(Home)></p></Link>
                        <Link to="/perfume"><p>퍼퓸(Perfume)</p></Link>
                        <p>>향수 정보</p>
                    </div>
                    <img src={perfume.image} alt={perfume.perfume} />
                    <div className="perfume-info">
                        <h1>{perfume.brand}</h1>
                        <h2>{perfume.perfume}</h2>
                        <p>[부항률]<br/> - {perfume.duration}</p>
                        <p>[메인 어코드]<br/> - 탑 노트: {perfume.topnote}<br/>
                         - 미들 노트: {perfume.middlenote}<br/>
                         - 베이스 노트: {perfume.basenote}</p>
                        <p>[향 설명]<br/> - {perfume.description}</p>
                        <p>[용량]<br/> - {perfume.ml.join(', ')}</p>
                        <a href={perfume.pageurl} target="_blank" rel="noopener noreferrer">구매하러 가기</a>
                        <p className="acode">
                            {Array.isArray(perfume.acode) ? perfume.acode.map(ac => `#${ac}`).join(' ') : ''}
                        </p>
                        <div className="heart-icon-2" onClick={() => toggleLike(perfume.id)}>
                            {likedPerfumes.includes(perfume.id) ? <IoIosHeart size={25} color='#FC7979'/> : <IoIosHeartEmpty size={25}/>}
                        </div>
                    </div>
                </div>
            </div>
            <div className="post">
                <p className="post-p">댓글</p>
                <div className="post-input">
                    <input className="input-post"></input>
                    <button className="input-post-btn">등록하기</button>
                </div>
                <div className="post-container">
                    <div className="post-id">
                        <CgProfile size={30} />
                        <p className="post-name">닉네임</p>
                        <p className="comment">이 향수 강추합니다!</p>
                        <p className="date">2024.09.09</p>
                    </div>
                </div>
            </div>
        </>
    );
};

export default PerfumeDetail;
