import React, { useState, useEffect, useRef, useCallback } from 'react';
import { useNavigate } from 'react-router-dom'; 
import Navbar from '../css/Navbar.js';
import '../css/Perfume.css';
import { IoIosHeartEmpty, IoIosHeart } from 'react-icons/io';
import { IoSearchSharp } from 'react-icons/io5';

const Perfume = () => {
    const [isDropdownOpen, setIsDropdownOpen] = useState(false); 
    const [isDurationDropdownOpen, setIsDurationDropdownOpen] = useState(false); 
    const [selectedBrands, setSelectedBrands] = useState([]); 
    const [selectedDurations, setSelectedDurations] = useState([]); 
    const [perfumes, setPerfumes] = useState([]); 
    const [filteredPerfumes, setFilteredPerfumes] = useState([]); 
    const [likedPerfumes, setLikedPerfumes] = useState([]); 
    const [visibleCount, setVisibleCount] = useState(20); 
    const [loading, setLoading] = useState(false); 
    const [searchTerm, setSearchTerm] = useState('');

    const loadMoreRef = useRef(null); 
    const navigate = useNavigate(); 
    const dropdownRef = useRef(null); 
    const durationDropdownRef = useRef(null); 

    const durationOptions = [
        '퍼퓸',
        '오 드 퍼퓸',
        '오 드 뚜왈렛',
        '오 드 코롱'
    ];

    // 향수 데이터 가져오기
    const fetchPerfumes = async (brands = [], durations = []) => {
        setLoading(true);
        try {
            let url = 'http://localhost:8080/perfumes/search/all';
            if (brands.length > 0 || durations.length > 0) {
                const brandParam = brands.length > 0 ? `brands=${brands.map(brand => encodeURIComponent(brand)).join(',')}` : '';
                const durationParam = durations.length > 0 ? `durations=${durations.join(',')}` : '';
                url = `http://localhost:8080/perfumes/search/filter?${brandParam}&${durationParam}`;
            }

            const response = await fetch(url);
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            const data = await response.json();
            setPerfumes(data); 
            setFilteredPerfumes(data); 
        } catch (error) {
            console.error('향수 데이터를 가져오는 중 오류가 발생했습니다:', error);
        }
        setLoading(false);
    };

    // 찜한 향수 목록 가져오기
    const fetchLikedPerfumes = async () => {
        const token = localStorage.getItem('token');
        if (!token) {
            return;
        }
    
        try {
            const response = await fetch('http://localhost:8080/api/wishlist', {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`,
                },
            });
            if (!response.ok) {
                throw new Error('찜 목록을 불러오는 중 오류가 발생했습니다.');
            }
            const data = await response.json();
            
            // 응답 데이터가 객체 형태이므로, 원하는 값들을 추출
            if (data && Array.isArray(data.perfumes)) {
                const perfumeIds = data.perfumes.map(item => item.id); // perfumeId가 아니라 id로 수정
                setLikedPerfumes(perfumeIds); 
            }
        } catch (error) {
            console.error('찜 목록 불러오기 실패:', error);
        }
    };  

    useEffect(() => {
        fetchPerfumes();
        fetchLikedPerfumes();
    }, []);

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
            console.log("찜이 성공적으로 삭제되었습니다.");
        } catch (error) {
            console.error('API 호출 중 오류:', error);
        }
    };

    // 찜 토글 기능
    const toggleLike = async (event, perfume) => {
        event.stopPropagation()
        const perfumeId = perfume.id

        if (!localStorage.getItem('token')) {
            alert("로그인 후 사용 가능합니다.")
            return
        }

        // UI 즉시 업데이트
        setLikedPerfumes((prevLiked) => {
            if (prevLiked.includes(perfumeId)) {
                return prevLiked.filter(id => id !== perfumeId) // 해제된 경우
            } else {
                return [...prevLiked, perfumeId] // 추가된 경우
            }
        })

        try {
            if (likedPerfumes.includes(perfumeId)) {
                // 찜 해제 요청
                await removeFromWishlist(perfumeId) // 수정된 부분
                console.log("찜 삭제 성공!")
            } else {
                // 찜 추가 요청
                await addToWishlist(perfumeId)
                console.log("찜 성공!")
            }
        } catch (error) {
            console.error('찜 상태 변경 중 오류가 발생했습니다:', error)

            // 에러 발생 시, 이전 상태로 롤백
            setLikedPerfumes((prevLiked) => {
                if (likedPerfumes.includes(perfumeId)) {
                    return [...prevLiked, perfumeId] // 에러 발생 시 다시 추가
                } else {
                    return prevLiked.filter(id => id !== perfumeId) // 에러 발생 시 다시 해제
                }
            })
        }
    }


    // 선택한 브랜드 추가/제거
    const handleBrandSelect = (brand) => {
        setSelectedBrands(prevSelected => {
            if (prevSelected.includes(brand)) {
                return prevSelected.filter(b => b !== brand);
            } else {
                return [...prevSelected, brand];
            }
        });
    };

    // 선택한 지속력 추가/제거
    const handleDurationSelect = (duration) => {
        setSelectedDurations(prevSelected =>
            prevSelected.includes(duration)
                ? prevSelected.filter(d => d !== duration)
                : [...prevSelected, duration]
        );
    };

    const removeBrand = (brand) => {
        setSelectedBrands(prevSelected => prevSelected.filter(b => b !== brand));
    };

    const removeDuration = (duration) => {
        setSelectedDurations(prevSelected => prevSelected.filter(d => d !== duration));
    };

    const handlePerfumeClick = (perfume) => {
        navigate(`/perfumes/${encodeURIComponent(perfume.perfume)}`, { state: { perfume } });
    };

    // 무한 스크롤 로드 기능
    const loadMorePerfumes = useCallback((entries) => {
        const [entry] = entries;
        if (entry.isIntersecting && !loading) {
            setLoading(true);
            setTimeout(() => {
                setVisibleCount(prevCount => prevCount + 20); 
                setLoading(false);
            }, 500);
        }
    }, [loading]);

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

    const handleClickOutside = (event) => {
        if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
            setIsDropdownOpen(false);
        }
        if (durationDropdownRef.current && !durationDropdownRef.current.contains(event.target)) {
            setIsDurationDropdownOpen(false);
        }
    };

    useEffect(() => {
        document.addEventListener('mousedown', handleClickOutside);
        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, []);

    const handleSearch = () => {
        fetchPerfumes(selectedBrands, selectedDurations);
    };

    const handleSearchClick = () => {
        handleSearch();
    };

    const handleSearchInput = (event) => {
        setSearchTerm(event.target.value);
    };

    const handleKeyPress = (event) => {
        if (event.key === 'Enter' || event.key === ' ') {
            handleSearch();
        }
    };

    const toggleDropdown = () => {
        setIsDropdownOpen(prevState => !prevState); 
    };
    
    const toggleDurationDropdown = () => {
        setIsDurationDropdownOpen(prevState => !prevState); 
    };

    // 브랜드 리스트를 중복 없이 정렬하여 가져오기
    const uniqueBrands = [...new Set(perfumes.map(perfume => perfume.brand.trim()))].sort();

    return (
        <>
            <Navbar />
            <div className="brand-filter-container">
                <div className="brand-filter-header">
                    <h2>Brand</h2>
                </div>
                <div className="brand-filter-content">
                    <div className="Brand" ref={dropdownRef}>
                        <div className="filter-item" onClick={toggleDropdown}>
                            브랜드 선택
                            <div className="dropdown-icon">▼</div>
                        </div>
                        <div className="selected-brands-container">
                            <div className="selected-brands">
                                {selectedBrands.map(brand => (
                                    <div key={brand} className="selected-brand-item">
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
                    <div className="duration" ref={durationDropdownRef}>
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
                    <div key={perfume.id} className="perfume-item-2" onClick={() => handlePerfumeClick(perfume)}>
                        <img src={perfume.image} alt={perfume.perfume} />
                        <p className="brand">{perfume.brand}</p>
                        <div className="perfume">{perfume.perfume}</div>
                        <p className="acode">
                            {Array.isArray(perfume.acode) ? perfume.acode.map(ac => `#${ac}`).join(' ') : ''}
                        </p>
                        <div className="heart-icon" onClick={(event) => toggleLike(event, perfume)}>
                            {likedPerfumes.includes(perfume.id) ? <IoIosHeart size={25} color='#FC7979'/> : <IoIosHeartEmpty  size={25}/>}
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
