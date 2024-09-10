import React, { useState } from 'react';
import { useLocation } from 'react-router-dom'; // state에서 데이터를 받아오기 위해 사용
import Navbar from '../css/Navbar.js';
import '../css/PerfumeDetail.css';
import { IoIosHeartEmpty, IoIosHeart } from 'react-icons/io';
import { IoSearchSharp } from 'react-icons/io5';
import { CgProfile } from "react-icons/cg";
import { Link } from 'react-router-dom';

const PerfumeDetail = () => {
    const location = useLocation(); // useLocation을 통해 state를 받아옴
    const perfume = location.state?.perfume; // 전달된 perfume 데이터를 받음
    const [likedPerfumes, setLikedPerfumes] = useState([]); 

    const toggleLike = (id) => {
        setLikedPerfumes(prevLiked =>
            prevLiked.includes(id) ? prevLiked.filter(likedId => likedId !== id) : [...prevLiked, id]
        );
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
                        <Link to="/">
                        <p>프레그런스(Home)></p>
                        </Link>
                        <Link to="/perfume">
                         <p>퍼퓸(Perfume)</p>
                        </Link>
                        <p>>향수 정보</p>
                    </div>
                    <img src={perfume.image} alt={perfume.perfume} />
                    <div className="perfume-info">
                        <h1>{perfume.brand}</h1>
                        <h2>{perfume.perfume}</h2>
                        <p>[부항률]<br/> - {perfume.duration}</p>
                        <p>[메인 어코드]<br/> - 탑 노트: {perfume.topnote}<br/>
                         - 미들 노트: {perfume.middlenote}<br/>
                         - 베이스 노트: {perfume.basenote}
                        </p>
                        <p>[향 설명]<br/> - {perfume.description}</p>
                        <p>[용량]<br/> - {perfume.ml.join(', ')}</p>
                        <a href={perfume.pageurl} target="_blank" rel="noopener noreferrer">구매하러 가기</a>
                        <p className="acode">
                            {Array.isArray(perfume.acode) ? perfume.acode.map(ac => `#${ac}`).join(' ') : ''}
                        </p>
                        <div className="heart-icon-2" onClick={() => toggleLike(perfume.perfume)}>
                            {likedPerfumes.includes(perfume.perfume) ? <IoIosHeart size={25} color='#FC7979'/> : <IoIosHeartEmpty  size={25}/>}
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
