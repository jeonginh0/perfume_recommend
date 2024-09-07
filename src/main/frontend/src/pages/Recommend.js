import React, { useState, useEffect } from 'react';
import Navbar from '../css/Navbar.js';
import '../css/Recommend.css';
import { GoArrowLeft } from 'react-icons/go';
import axios from 'axios';

const Recommend = () => {
    const [currentStep, setCurrentStep] = useState(1); // 현재 설문 단계
    const [selectedOptions, setSelectedOptions] = useState([]); // 선택된 옵션
    const [surveyResponses, setSurveyResponses] = useState([]); // 설문 응답 저장
    const [isLoading, setIsLoading] = useState(false); // 로딩 상태 관리
    const [isResultVisible, setIsResultVisible] = useState(false); // 결과 표시 상태
    const [recommendations, setRecommendations] = useState([]); // 추천 결과 저장
    const [errorMessage, setErrorMessage] = useState(''); // 오류 메시지 관리

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

        setSurveyResponses(prevResponses => {
            const updatedResponses = [...prevResponses, currentResponse];
            return updatedResponses;
        });

        if (currentStep < 4) {
            setCurrentStep(currentStep + 1);
            setSelectedOptions([]); // 다음 단계로 넘어갈 때 선택 초기화
        } else {
            setIsLoading(true); // 로딩 상태로 전환
            submitSurveyResponses(); // 설문 응답 제출
        }
    };

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
            case 2: return 'situation';
            case 3: return 'season';
            case 4: return 'duration';
            default: return '';
        }
    };

    // 각 질문의 가중치 반환
    const getCurrentQuestionWeight = () => {
        switch (currentStep) {
            case 1: return 0.7;
            case 2: return 0.5;
            case 3: return 0.3;
            case 4: return 1.0;
            default: return 1.0;
        }
    };

    // 선택된 옵션의 첫 번째 값만 반환
    const getSelectedOptionLabels = () => {
        const optionLabels = [
            ['Floral', 'Fruity', 'Woody', 'Musky', 'Spicy', 'Green', 'Citrus'],
            ['Everyday', 'Special Occasions', 'Work'],
            ['Spring', 'Summer', 'Autumn', 'Winter'],
            ['퍼퓸', '오 드 퍼퓸', '오 드 뚜왈렛', '오 드 코롱']
        ];

        return optionLabels[currentStep - 1][selectedOptions[0]];
    };

    // 설문 응답 제출 함수
    const submitSurveyResponses = async () => {
        console.log('Submitting survey responses:', surveyResponses); // 저장된 설문 응답 확인
        try {
            const response = await axios.post('http://58.235.71.202:8080/survey/response/guest', { responses: surveyResponses });
            if (response.status === 200) {
                fetchRecommendations();
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

    // 추천 결과 조회 함수
    const fetchRecommendations = async () => {
        try {
            const response = await axios.get('http://58.235.71.202:8080/perfumes/recommend/guest/details');
            if (response.status === 200) {
                setRecommendations(response.data);
                setIsLoading(false);
                setIsResultVisible(true);
                console.log('추천된 향수 목록:', response.data); // 추천 결과 확인
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
            case 3:
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

    const renderResults = () => (
        <div className="results-container">
            <h2>추천 향수</h2>
            <div className="perfume-grid">
                {recommendations.map((perfume, index) => (
                    <div className="perfume-item" key={index}>
                        <img src={perfume.imageUrl} alt="향수 이미지" />
                        <p>{perfume.brand}</p>
                        <p>{perfume.type}</p>
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
