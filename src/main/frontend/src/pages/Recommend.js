import React, { useState, useEffect } from 'react';
import Navbar from '../css/Navbar.js';
import '../css/Recommend.css';
import { GoArrowLeft } from 'react-icons/go';
import axios from 'axios';
import { IoIosHeartEmpty, IoIosHeart } from 'react-icons/io';
import { useNavigate, useLocation } from 'react-router-dom';

const Recommend = () => {
    const [currentStep, setCurrentStep] = useState(1); // 현재 설문 단계
    const [selectedOptions, setSelectedOptions] = useState([]); // 선택된 옵션
    const [surveyResponses, setSurveyResponses] = useState([]); // 설문 응답 저장
    const [isLoading, setIsLoading] = useState(false); // 로딩 상태 관리
    const [isResultVisible, setIsResultVisible] = useState(false); // 결과 표시 상태
    const [recommendations, setRecommendations] = useState([]); // 추천 결과 저장
    const [errorMessage, setErrorMessage] = useState(''); // 오류 메시지 관리
    const [likedPerfumes, setLikedPerfumes] = useState([]); 
    const navigate = useNavigate();
    const location = useLocation();

    // 고정된 질문 순서 배열
    const questionOrder = ['category', 'season', 'situation', 'duration'];

    // 옵션 선택 시 호출되는 함수
    const handleOptionClick = (index) => {
        if (selectedOptions.includes(index)) {
            setSelectedOptions(selectedOptions.filter(item => item !== index));
        } else {
            setSelectedOptions([...selectedOptions, index]);
        }
    };

    // 다음 단계로 넘어갈 때 호출되는 함수
    const handleNextClick = () => {
        if (selectedOptions.length === 0) return; // 선택된 옵션이 없으면 넘어가지 않음

        const currentResponse = {
            questionType: getCurrentQuestionType(),
            response: getSelectedOptionLabels(), // 선택된 옵션의 라벨을 서버에 보냄
            weight: getCurrentQuestionWeight()
        };

        // 설문 응답에 추가 (순서를 보장하여 추가)
        setSurveyResponses((prevResponses) => {
            const updatedResponses = [...prevResponses];
            const index = questionOrder.indexOf(currentResponse.questionType);

            if (index !== -1) {
                // 순서에 맞게 배열에 저장
                updatedResponses[index] = currentResponse;
            }

            // 응답 배열을 항상 순서대로 정렬하고 빈 객체를 필터링하여 제거
            const sortedResponses = questionOrder
                .map(type => updatedResponses.find(response => response.questionType === type) || {})
                .filter(response => Object.keys(response).length !== 0);

            return sortedResponses;
        });

        if (currentStep < 4) {
            setCurrentStep(currentStep + 1);
            setSelectedOptions([]); // 다음 단계로 넘어갈 때 선택 초기화
        } else {
            setIsLoading(true); // 로딩 상태로 전환
        }
    };

    // surveyResponses가 업데이트된 후 submitSurveyResponses를 호출
    useEffect(() => {
        if (isLoading && currentStep === 4) {
            submitSurveyResponses(); // 설문 응답 제출
        }
    }, [surveyResponses, isLoading]);

    const handleBackClick = () => {
        if (currentStep > 1) {
            setCurrentStep(currentStep - 1);
            setSelectedOptions([]);
        }
    };

    const handleRetryClick = () => {
        setCurrentStep(1);
        setSelectedOptions([]);
        setSurveyResponses([]);
        setIsResultVisible(false);
    };

    // 각 질문의 타입 반환
    const getCurrentQuestionType = () => {
        switch (currentStep) {
            case 1: return 'category';
            case 2: return 'season';
            case 3: return 'situation';
            case 4: return 'duration';
            default: return '';
        }
    };

    // 각 질문의 가중치 반환
    const getCurrentQuestionWeight = () => {
        switch (currentStep) {
            case 1: return 7;
            case 2: return 5;
            case 3: return 3;
            case 4: return 10;
            default: return 10;
        }
    };

    // 선택된 옵션의 첫 번째 값만 반환
    const getSelectedOptionLabels = () => {
        const optionLabels = [
            ['Floral', 'Fruity', 'Woody', 'Musky', 'Spicy', 'Green', 'Citrus'],
            ['Spring', 'Summer', 'Autumn', 'Winter'],
            ['Everyday', 'Special Occasions', 'Work'],
            ['퍼퓸', '오 드 퍼퓸', '오 드 뚜왈렛', '오 드 코롱']
        ];

        return optionLabels[currentStep - 1][selectedOptions[0]];
    };

    // 1. 설문 응답을 세션에 저장하는 함수
const submitSurveyResponses = async () => {
    console.log('Submitting survey responses:', surveyResponses); // 저장된 설문 응답 확인

    // 응답 필터링: 응답과 가중치가 비어 있지 않은 경우만 남김
    const filteredResponses = surveyResponses.filter(response => {
        return response.response && response.weight && Object.keys(response).length !== 0;
    });

    console.log('Filtered responses:', filteredResponses); // 필터링 후 응답 확인

    // 빈 배열인지 확인 후 처리
    if (filteredResponses.length === 0) {
        setErrorMessage('설문 응답이 비어 있습니다.');
        setIsLoading(false);
        return;
    }

    try {
        const response = await axios.post(
            'http://localhost:8080/survey/response/guest',
            { responses: filteredResponses },
            {
                headers: {
                    'Content-Type': 'application/json'
                },
                withCredentials: true
            }  
        );
        if (response.status === 200) {
            const surveyResponseId = response.data; // 서버에서 반환된 ID
            console.log('SurveyResponse ID:', surveyResponseId);
            await requestPerfumeRecommendations(surveyResponseId); // ID를 넘겨서 추천 요청
            await fetchRecommendations(surveyResponseId); // ID를 넘겨서 추천 요청
        } else {
            setErrorMessage('설문 응답 제출 실패');
            setIsLoading(false);
        }
    } catch (error) {
        console.error('설문 응답 제출 중 오류 발생', error.response?.data);
        setErrorMessage('설문 응답 제출 중 오류 발생');
        setIsLoading(false);
    }
};



    // 향수 추천을 요청하는 함수
    const requestPerfumeRecommendations = async (surveyResponseId) => {
        try {
            console.log('향수 추천 요청 중...');
            const response = await axios.get(`http://localhost:8080/perfumes/recommend/guest?surveyResponseId=${surveyResponseId}`);

            if (response.status === 200) {
                console.log('향수 추천 요청 성공. 결과 조회 시작...');
                await fetchRecommendations();
            } else {
                setErrorMessage('향수 추천 요청 실패: 서버가 200 상태 코드를 반환하지 않았습니다.');
                console.error('향수 추천 요청 실패:', response);
                setIsLoading(false);
            }
        } catch (error) {
            console.error('향수 추천 요청 중 오류 발생:', error.response ? error.response.data : error.message);
            setErrorMessage(`향수 추천 요청 중 오류 발생: ${error.message}`);
            setIsLoading(false);
        }
    };


    // 3. 추천 결과 조회 함수
    const fetchRecommendations = async (surveyResponseId) => {
        try {
            const response = await axios.get(`http://localhost:8080/perfumes/recommend/guest/details?guestId=${surveyResponseId}`);
            if (response.status === 200) {
                setRecommendations(response.data);
                setIsLoading(false);
                setIsResultVisible(true);
                //sessionStorage.setItem('recommendations', JSON.stringify(response.data)); // 결과를 세션 스토리지에 저장
                console.log('추천된 향수 목록:', response.data);
            } else {
                setErrorMessage('추천 결과 조회 실패');
                setIsLoading(false);
            }
        } catch (error) {
            console.error('추천 결과 조회 중 오류 발생', error.response?.data);
            setErrorMessage('추천 결과 조회 중 오류 발생');
            setIsLoading(false);
        }
    };

    // 각 질문의 내용을 렌더링하는 함수
    const renderQuestion = () => {
        switch (currentStep) {
            case 1:
                return (
                    <>
                        <p className="step-title">어떤 향기 노트를 선호하시나요?* (복수 선택 가능)</p>
                        <div className="button-container">
                            {[
                                '꽃 향기 (예: 장미, 자스민)',
                                '과일 향기 (예: 사과, 베리)',
                                '우디 향기 (예: 샌들우드, 시더우드)',
                                '머스크 향기',
                                '스파이시 향기 (예: 시나몬, 페퍼)',
                                '그린 향기 (예: 풀잎, 허브)',
                                '시트러스 향기 (예: 레몬, 오렌지)'
                            ].map((label, index) => (
                                <button
                                    key={index}
                                    className={`button-option ${selectedOptions.includes(index) ? 'selected' : ''}`}
                                    onClick={() => handleOptionClick(index)}
                                >
                                    {index + 1}. {label}
                                </button>
                            ))}
                        </div>
                    </>
                );
            case 2:
                return (
                    <>
                        <p className="step-title">어떤 계절에 향수를 사용하는 것을 선호하시나요?* (복수 선택 가능)</p>
                        <div className="button-container">
                            {[
                                '봄 (예: 플로럴, 그린)',
                                '여름 (예: 시트러스, 아쿠아틱)',
                                '가을 (예: 우디, 스파이시)',
                                '겨울 (예: 머스크, 오리엔탈)'
                            ].map((label, index) => (
                                <button
                                    key={index}
                                    className={`button-option ${selectedOptions.includes(index) ? 'selected' : ''}`}
                                    onClick={() => handleOptionClick(index)}
                                >
                                    {index + 1}. {label}
                                </button>
                            ))}
                        </div>
                    </>
                );
            case 3:
                return (
                    <>
                        <p className="step-title">주로 언제 향수를 사용하는 것을 원하시나요?* (복수 선택 가능)</p>
                        <div className="button-container">
                            {[
                                '일상 생활',
                                '특별한 날',
                                '업무 중'
                            ].map((label, index) => (
                                <button
                                    key={index}
                                    className={`button-option ${selectedOptions.includes(index) ? 'selected' : ''}`}
                                    onClick={() => handleOptionClick(index)}
                                >
                                    {index + 1}. {label}
                                </button>
                            ))}
                        </div>
                    </>
                );
            case 4:
                return (
                    <>
                        <p className="step-title">어떤 향수 농도를 선호하시나요?* (복수 선택 가능)</p>
                        <div className="button-container">
                            {[
                                '퍼퓸 (가장 농도가 짙은 향): 8~12시간',
                                '오 드 퍼퓸 (은은하게 퍼지는 깊이있는 향): 6~8시간',
                                '오 드 뚜왈렛 (가벼운 농도로 부드러운 향): 3~5시간',
                                '오 드 코롱 (약한 농도로 가볍고 얕은 향): 1~2시간'
                            ].map((label, index) => (
                                <button
                                    key={index}
                                    className={`button-option ${selectedOptions.includes(index) ? 'selected' : ''}`}
                                    onClick={() => handleOptionClick(index)}
                                >
                                    {index + 1}. {label}
                                </button>
                            ))}
                        </div>
                    </>
                );
            default:
                return null;
        }
    };

    const renderLoading = () => (
        <div className="loading-container">
            <div className="spinner"></div>
            <p>당신에게 맞는 향수를 찾는 중입니다...</p>
        </div>
    );

    // 찜 토글 기능
    const toggleLike = async (event, perfume) => {
        event.stopPropagation();
        const perfumeId = perfume.id;
        
        if (!localStorage.getItem('token')) {
            alert("로그인 후 사용 가능합니다.");
            return;
        }
    
        // UI 즉시 업데이트
        setLikedPerfumes((prevLiked) => {
            if (prevLiked.includes(perfumeId)) {
                return prevLiked.filter(id => id !== perfumeId); // 해제된 경우
            } else {
                return [...prevLiked, perfumeId]; // 추가된 경우
            }
        });
    
        try {
            if (likedPerfumes.includes(perfumeId)) {
                // 찜 해제 요청
                await removeFromWishlist(perfumeId);
                console.log("찜 삭제 성공!");
            } else {
                // 찜 추가 요청
                await addToWishlist(perfumeId);
                console.log("찜 성공!");
            }
        } catch (error) {
            console.error('찜 상태 변경 중 오류가 발생했습니다:', error);
    
            // 에러 발생 시, 이전 상태로 롤백
            setLikedPerfumes((prevLiked) => {
                if (likedPerfumes.includes(perfumeId)) {
                    return [...prevLiked, perfumeId]; // 에러 발생 시 다시 추가
                } else {
                    return prevLiked.filter(id => id !== perfumeId); // 에러 발생 시 다시 해제
                }
            });
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
            const response = await fetch(`http://localhost:8080/api/wishlist?perfumeId=${perfumeId}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${token}`,
                },
            });
            if (!response.ok) {
                throw new Error('찜 삭제 중 오류가 발생했습니다.');
            }
            console.log("찜이 성공적으로 삭제되었습니다.");
        } catch (error) {
            console.error('API 호출 중 오류:', error);
        }
    };

    // 페이지가 마운트될 때 추천 결과가 전달되었는지 확인
    useEffect(() => {
        if (location.state?.fromDetail) {
            setRecommendations(location.state.recommendations);
            setIsResultVisible(true);
        }
    }, [location.state]);

    // 향수 상세페이지로 이동 시 추천 결과 상태를 함께 전달
    const handlePerfumeClick = (perfume) => {
        navigate(`/perfumes/${encodeURIComponent(perfume.perfume)}`, { 
            state: { 
                perfume, 
                fromDetail: true, 
                recommendations 
            } 
        });
    };  

    useEffect(() => {
        // 세션 저장된 추천 결과가 있으면 상태를 복원
        const savedRecommendations = sessionStorage.getItem('recommendations');
        if (savedRecommendations) {
            setRecommendations(JSON.parse(savedRecommendations));
            setIsResultVisible(true);
        }
    }, []);

    useEffect(() => {
        // 추천 결과가 갱신될 때마다 세션 스토리지에 저장
        if (isResultVisible) {
            sessionStorage.setItem('recommendations', JSON.stringify(recommendations));
        }
    }, [recommendations, isResultVisible]);

    const renderResults = () => (
        <div className="results-container">
            <h2>추천 향수</h2>
            <div className="perfume-grid">
                {recommendations.map((perfume, index) => (
                    <div 
                        className="perfume-item-2" 
                        key={index} 
                        onClick={() => handlePerfumeClick(perfume)} 
                    >
                        <img src={perfume.image} alt={perfume.perfume} />
                        <p className="brand-p">{perfume.brand}</p>
                        <p className="perfume-name-p">{perfume.perfume}</p>
                        <p>{perfume.type}</p>
                        <p className="acode">
                            {Array.isArray(perfume.acode) ? perfume.acode.map(ac => `#${ac}`).join(' ') : ''}
                        </p>
                        <div className="heart-icon" onClick={(event) => toggleLike(event, perfume)}>
                            {likedPerfumes.includes(perfume.id) ? <IoIosHeart size={25} color='#FC7979'/> : <IoIosHeartEmpty  size={25}/>}
                        </div>
                    </div>
                ))}
            </div>
            <button className="retry-button" onClick={handleRetryClick}>다시 추천받기</button>
        </div>
    );
    

    return (
        <>
            <Navbar />
            <div className="recommend-container">
                <div className="recommend-form">
                    {isLoading ? (
                        renderLoading()
                    ) : isResultVisible ? (
                        renderResults()
                    ) : (
                        <>
                            <p className="recommend-p">
                                나만의 특별한 향수를 찾기 위해 간단한 설문조사를 진행하세요.<br />
                                몇 가지 질문에 답하면, 당신의 취향과 라이프스타일에 맞는 향수를 추천해드립니다.
                            </p>

                            {errorMessage && <p className="error-message">{errorMessage}</p>}

                            {currentStep > 1 && (
                                <div className="back-button" onClick={handleBackClick}>
                                    <GoArrowLeft size={30} />
                                </div>
                            )}

                            <div className="progress-container">
                                <div className={`progress-step ${currentStep >= 1 ? 'active' : ''}`}>
                                    <div className="circle">01</div>
                                    <p>향기</p>
                                </div>
                                <div className={`progress-line ${currentStep >= 2 ? 'active' : ''}`}></div>
                                <div className={`progress-step ${currentStep >= 2 ? 'active' : ''}`}>
                                    <div className="circle">02</div>
                                    <p>라이프</p>
                                </div>
                                <div className={`progress-line ${currentStep >= 3 ? 'active' : ''}`}></div>
                                <div className={`progress-step ${currentStep >= 3 ? 'active' : ''}`}>
                                    <div className="circle">03</div>
                                    <p>계절</p>
                                </div>
                                <div className={`progress-line ${currentStep === 4 ? 'active' : ''}`}></div>
                                <div className={`progress-step ${currentStep === 4 ? 'active' : ''}`}>
                                    <div className="circle">04</div>
                                    <p>지속시간</p>
                                </div>
                            </div>

                            {renderQuestion()}

                            <button
                                className="next-button"
                                onClick={handleNextClick}
                                style={{ backgroundColor: selectedOptions.length === 0 ? '#D9D9D9' : '#0F0F0F' }}
                                disabled={selectedOptions.length === 0}
                            >
                                {currentStep < 4 ? '다음으로' : '향수 추천 받기'}
                            </button>
                        </>
                    )}
                </div>
            </div>
        </>
    );
};

export default Recommend;