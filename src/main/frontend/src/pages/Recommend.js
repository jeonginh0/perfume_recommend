import React, { useState } from 'react';
import Navbar from '../css/Navbar.js';
import '../css/Recommend.css';
import { GoArrowLeft } from 'react-icons/go'; // react-icons에서 GoArrowLeft 아이콘 임포트

const Recommend = () => {
    const [currentStep, setCurrentStep] = useState(1); // 현재 설문 단계
    const [selectedOptions, setSelectedOptions] = useState([]); // 선택된 옵션
    const [isLoading, setIsLoading] = useState(false); // 로딩 상태 관리
    const [isResultVisible, setIsResultVisible] = useState(false); // 결과 표시 상태

    const handleOptionClick = (index) => {
        if (selectedOptions.includes(index)) {
            setSelectedOptions(selectedOptions.filter(item => item !== index));
        } else {
            setSelectedOptions([...selectedOptions, index]);
        }
    };

    const handleNextClick = () => {
        if (selectedOptions.length === 0) return; // 선택된 옵션이 없으면 넘어가지 않음

        if (currentStep < 4) {
            setCurrentStep(currentStep + 1);
            setSelectedOptions([]); // 다음 단계로 넘어갈 때 선택 초기화
        } else {
            setIsLoading(true); // 로딩 상태로 전환
            // 로딩 상태를 2초 동안 유지한 후 결과를 표시
            setTimeout(() => {
                setIsLoading(false);
                setIsResultVisible(true);
            }, 2000);
        }
    };

    const handleBackClick = () => {
        if (currentStep > 1) {
            setCurrentStep(currentStep - 1);
            setSelectedOptions([]); // 이전 단계로 넘어갈 때 선택 초기화
        }
    };

    const handleRetryClick = () => {
        setCurrentStep(1);
        setSelectedOptions([]);
        setIsResultVisible(false);
    };

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
            <p>해인님에게 맞는 향수를 찾는 중입니다...</p>
        </div>
    );

    const renderResults = () => (
        <div className="results-container">
            <h2>추천 향수</h2>
            <div className="perfume-grid">
                {/* 예시 향수 결과 */}
                {['Diptyque', 'Byredo', 'Diptyque', 'Byredo', 'Diptyque', 'Diptyque'].map((brand, index) => (
                    <div className="perfume-item" key={index}>
                        <img src="perfume_image_url" alt="향수 이미지" />
                        <p>{brand}</p>
                        <p>오 드 퍼퓸</p>
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
                    {/* 로딩 상태 */}
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

                            {/* 화살표 버튼: 첫 번째 단계가 아닌 경우에만 표시 */}
                            {currentStep > 1 && (
                                <div className="back-button" onClick={handleBackClick}>
                                    <GoArrowLeft size={30} /> {/* GoArrowLeft 아이콘 사용 */}
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
