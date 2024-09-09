import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom'; // URL 파라미터를 가져오기 위해 사용
import Navbar from '../css/Navbar.js'; // 필요에 따라 네비게이션 바 추가
import '../css/PerfumeDetail.css'; // 상세 페이지 스타일링

const PerfumeDetail = () => {
    const { id } = useParams(); // URL에서 id를 가져옴
    const [perfume, setPerfume] = useState(null); // 해당 향수 데이터 저장
    const [loading, setLoading] = useState(true); // 로딩 상태

    // 향수 데이터를 서버에서 가져오는 함수
    useEffect(() => {
        const fetchPerfume = async () => {
            try {
                const response = await fetch(`http://58.235.71.202:8080/perfumes/${id}`);
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                const data = await response.json();
                setPerfume(data);
                setLoading(false);
            } catch (error) {
                console.error('향수 데이터를 가져오는 중 오류가 발생했습니다:', error);
                setLoading(false);
            }
        };

        fetchPerfume();
    }, [id]);

    if (loading) {
        return <div>Loading...</div>;
    }

    if (!perfume) {
        return <div>해당 향수를 찾을 수 없습니다.</div>;
    }

    return (
        <>
            <Navbar />
            <div className="perfume-detail-container">
                <div className="perfume-detail">
                    <img src={perfume.image} alt={perfume.perfume} />
                    <div className="perfume-info">
                        <h1>{perfume.brand}</h1>
                        <h2>{perfume.perfume}</h2>
                        <p><strong>지속력:</strong> {perfume.duration}</p>
                        <p><strong>노트:</strong></p>
                        <ul>
                            <li><strong>Top:</strong> {perfume.topnote}</li>
                            <li><strong>Middle:</strong> {perfume.middlenote}</li>
                            <li><strong>Base:</strong> {perfume.basenote}</li>
                        </ul>
                        <p><strong>설명:</strong> {perfume.description}</p>
                        <p><strong>용량:</strong> {perfume.ml.join(', ')}</p>
                        <a href={perfume.pageurl} target="_blank" rel="noopener noreferrer">공식 페이지에서 구매하기</a>
                    </div>
                </div>
            </div>
        </>
    );
};

export default PerfumeDetail;
