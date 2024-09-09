import React, { useState, useEffect, useRef, useCallback } from 'react';
import Navbar from '../css/Navbar.js';
import '../css/Perfume.css';
import { IoIosHeartEmpty, IoIosHeart } from 'react-icons/io';
import { IoSearchSharp } from 'react-icons/io5';
import { Link } from 'react-router-dom'; // 상세 페이지 연동을 위한 Link 사용

const Perfume = () => {
    const [isDropdownOpen, setIsDropdownOpen] = useState(false); // 브랜드 드롭다운 열림 여부
    const [isDurationDropdownOpen, setIsDurationDropdownOpen] = useState(false); // 지속력 드롭다운 열림 여부
    const [selectedBrands, setSelectedBrands] = useState([]); // 선택된 브랜드들
    const [selectedDurations, setSelectedDurations] = useState([]); // 선택된 지속력 옵션들
    const [perfumes, setPerfumes] = useState([]); // 전체 향수 데이터
    const [filteredPerfumes, setFilteredPerfumes] = useState([]); // 필터된 향수 데이터
    const [likedPerfumes, setLikedPerfumes] = useState([]); // 좋아요한 향수 목록
    const [visibleCount, setVisibleCount] = useState(20); // 화면에 보여줄 향수 개수
    const [loading, setLoading] = useState(false); // 로딩 상태
    const [searchCompleted, setSearchCompleted] = useState(false); // 검색 완료 여부
    const [searchTerm, setSearchTerm] = useState(''); // 검색어 상태

    const loadMoreRef = useRef(null); // 더 많은 항목을 로드하기 위한 참조

    // 지속력 옵션 목록
    const durationOptions = [
        '퍼퓸',
        '오 드 퍼퓸',
        '오 드 뚜왈렛',
        '오 드 코롱'
    ];

    // 향수 데이터를 서버에서 가져오는 함수
    const fetchPerfumes = async (brands = [], durations = []) => {
        setLoading(true);
        try {
            // 필터된 데이터를 가져오기 위한 URL 설정
            let url = 'http://58.235.71.202:8080/perfumes/search/all';
            if (brands.length > 0 || durations.length > 0) {
                const brandParam = brands.length > 0 ? `brands=${brands.map(brand => encodeURIComponent(brand)).join(',')}` : '';
                const durationParam = durations.length > 0 ? `durations=${durations.join(',')}` : '';
                url = `http://58.235.71.202:8080/perfumes/search/filter?${brandParam}&${durationParam}`;
            }

            // 서버로부터 데이터 요청
            const response = await fetch(url);
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            const data = await response.json();
            setPerfumes(data); // 전체 향수 데이터 설정
            setFilteredPerfumes(data); // 초기 필터된 향수 설정
        } catch (error) {
            console.error('향수 데이터를 가져오는 중 오류가 발생했습니다:', error);
        }
        setLoading(false);
    };

    // 컴포넌트가 처음 로드될 때 향수 데이터를 가져옴
    useEffect(() => {
        fetchPerfumes();
    }, []);

    // 브랜드 드롭다운 열고 닫기
    const toggleDropdown = () => {
        setIsDropdownOpen(prev => !prev);
    };

    // 지속력 드롭다운 열고 닫기
    const toggleDurationDropdown = () => {
        setIsDurationDropdownOpen(prev => !prev);
    };

    // 브랜드 선택 시 상태 업데이트
    const handleBrandSelect = (brand) => {
        setSelectedBrands(prevSelected => {
            if (prevSelected.includes(brand)) {
                return prevSelected.filter(b => b !== brand); // 이미 선택된 브랜드는 제거
            } else {
                return [...prevSelected, brand]; // 선택된 브랜드 추가
            }
        });
    };

    // 지속력 선택 시 상태 업데이트
    const handleDurationSelect = (duration) => {
        setSelectedDurations(prevSelected =>
            prevSelected.includes(duration)
                ? prevSelected.filter(d => d !== duration) // 이미 선택된 지속력은 제거
                : [...prevSelected, duration] // 선택된 지속력 추가
        );
    };

    // 선택된 브랜드 제거
    const removeBrand = (brand) => {
        setSelectedBrands(prevSelected => prevSelected.filter(b => b !== brand));
    };

    // 선택된 지속력 제거
    const removeDuration = (duration) => {
        setSelectedDurations(prevSelected => prevSelected.filter(d => d !== duration));
    };

    // 좋아요 토글 기능
    const toggleLike = (id) => {
        setLikedPerfumes(prevLiked =>
            prevLiked.includes(id) ? prevLiked.filter(likedId => likedId !== id) : [...prevLiked, id]
        );
    };

    // 스크롤로 더 많은 향수를 로드하는 함수
    const loadMorePerfumes = useCallback((entries) => {
        const [entry] = entries;
        if (entry.isIntersecting && !loading) {
            setLoading(true);
            setTimeout(() => {
                setVisibleCount(prevCount => prevCount + 20); // 20개의 향수를 추가로 로드
                setLoading(false);
            }, 500);
        }
    }, [loading]);

    // 스크롤 감지를 위해 IntersectionObserver 사용
    useEffect(() => {
        const observer = new IntersectionObserver(loadMorePerfumes, {
            root: null,
            rootMargin: '0px',
            threshold: 1.0
        });

        if (loadMoreRef.current) {
            observer.observe(loadMoreRef.current);
        }

        return () => {
            if (loadMoreRef.current) {
                observer.unobserve(loadMoreRef.current);
            }
        };
    }, [loadMorePerfumes]);

    // 검색 버튼 클릭 시 향수를 필터링하여 가져오는 함수
    const handleSearch = () => {
        setSearchCompleted(true);
        fetchPerfumes(selectedBrands, selectedDurations); // 선택된 필터로 다시 요청
    };

    // 검색어 입력 처리
    const handleSearchInput = (event) => {
        setSearchTerm(event.target.value);
    };

    // 검색어 입력 후 Enter 또는 스페이스바 누르면 검색 실행
    const handleKeyPress = (event) => {
        if (event.key === 'Enter' || event.key === ' ') {
            handleSearch();
        }
    };

    // 검색 버튼 클릭 시 검색 실행
    const handleSearchClick = () => {
        handleSearch();
    };

    // 브랜드 목록을 중복 없이 정렬하여 가져옴
    const uniqueBrands = [...new Set(perfumes.map(perfume => perfume.brand.trim()))].sort();

    // ID 생성 함수: 브랜드와 향수명을 조합하여 고유한 ID를 생성
    const generateId = (brand, perfume) => {
        return `${brand}-${perfume}`.replace(/\s+/g, '-').toLowerCase();
    };

    return (
        <>
            <Navbar />

            <div className="brand-filter-container">
                <div className="brand-filter-header">
                    <h2>Brand</h2>
                </div>
                <div className="brand-filter-content">
                    <div className="Brand">
                        <div className="filter-item" onClick={toggleDropdown}>
                            브랜드 선택
                            <div className="dropdown-icon">▼</div>
                        </div>
                        <div className="selected-brands-container">
                            <div className="selected-brands">
                                {selectedBrands.map((brand, index) => (
                                    <div key={index} className="selected-brand-item">
                                        {brand} <span className="remove-brand" onClick={() => removeBrand(brand)}>x</span>
                                    </div>
                                ))}
                            </div>
                        </div>
                        {isDropdownOpen && (
                            <div className="dropdown-menu">
                                {uniqueBrands.map(brand => (
                                    <div key={brand} className="dropdown-item">
                                        <label>
                                            <input
                                                type="checkbox"
                                                checked={selectedBrands.includes(brand)}
                                                onChange={() => handleBrandSelect(brand)}
                                            />
                                            {brand}
                                        </label>
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>

                    <div className="duration">
                        <div className="filter-item" onClick={toggleDurationDropdown}>
                            지속력 선택
                            <div className="dropdown-icon">▼</div>
                        </div>
                        <div className="selected-brands-container">
                            <div className="selected-brands">
                                {selectedDurations.map(duration => (
                                    <div key={duration} className="selected-brand-item">
                                        {duration} <span className="remove-brand" onClick={() => removeDuration(duration)}>x</span>
                                    </div>
                                ))}
                            </div>
                        </div>
                        {isDurationDropdownOpen && (
                            <div className="dropdown-menu-d">
                                {durationOptions.map(duration => (
                                    <div key={duration} className="dropdown-item">
                                        <label>
                                            <input
                                                type="checkbox"
                                                checked={selectedDurations.includes(duration)}
                                                onChange={() => handleDurationSelect(duration)}
                                            />
                                            {duration}
                                        </label>
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>
                </div>
            </div>

            <div className="sub-header">
                <p className="perfume-number">총 {filteredPerfumes.length}개의 향수가 등록되어 있습니다.</p>
                <p className="perfume-name">Perfume</p>
                <div className="search-bar">
                    <input
                        type="text"
                        placeholder="Search"
                        value={searchTerm}
                        onChange={handleSearchInput}
                        onKeyPress={handleKeyPress}
                    />
                    <button onClick={handleSearchClick}><IoSearchSharp /></button>
                </div>
            </div>

            <div className="perfume-list">
                {filteredPerfumes.filter(perfume => {
                    const matchesSearchTerm = searchTerm.trim() === '' ||
                        perfume.brand.toLowerCase().includes(searchTerm.toLowerCase()) ||
                        perfume.perfume.toLowerCase().includes(searchTerm.toLowerCase()) ||
                        (Array.isArray(perfume.acode) && perfume.acode.some(ac => ac.toLowerCase().includes(searchTerm.toLowerCase())));

                    const matchesBrand = selectedBrands.length === 0 || selectedBrands.includes(perfume.brand.trim());
                    const matchesDuration = selectedDurations.length === 0 || selectedDurations.some(duration => perfume.duration === duration);

                    return matchesSearchTerm && matchesBrand && matchesDuration;
                }).slice(0, visibleCount).map(perfume => (
                    <div key={generateId(perfume.brand, perfume.perfume)} className="perfume-item-2">
                        <Link to={`/perfume/${generateId(perfume.brand, perfume.perfume)}`}>
                            <img src={perfume.image} alt={perfume.perfume} />
                            <p className="brand">{perfume.brand}</p>
                            <div className="perfume">{perfume.perfume}</div>
                            <p className="acode">
                                {Array.isArray(perfume.acode) ? perfume.acode.map(ac => `#${ac}`).join(' ') : ''}
                            </p>
                        </Link>
                        <div className="heart-icon" onClick={() => toggleLike(generateId(perfume.brand, perfume.perfume))}>
                            {likedPerfumes.includes(generateId(perfume.brand, perfume.perfume)) ? <IoIosHeart size={25} color='#FC7979'/> : <IoIosHeartEmpty  size={25}/>}
                        </div>
                    </div>
                ))}
            </div>

            {loading && (
                <div className="loading-spinner"></div>
            )}

            <div ref={loadMoreRef} style={{ height: '20px', margin: '10px 0' }}></div>
        </>
    );
};

export default Perfume;
