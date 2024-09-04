import React, { useState, useEffect, useRef, useCallback } from 'react';
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
    const [searchCompleted, setSearchCompleted] = useState(false);
    const [searchTerm, setSearchTerm] = useState('');

    const loadMoreRef = useRef(null);

    const durationOptions = [
        '퍼퓸',
        '오 드 퍼퓸',
        '오 드 뚜왈렛',
        '오 드 코롱'
    ];

    const fetchPerfumes = async (brands = [], durations = []) => {
        setLoading(true);
        try {
            let url = 'http://58.235.71.202:8080/perfumes/search/all';
            if (brands.length > 0 || durations.length > 0) {
                const brandParam = brands.length > 0 ? `brands=${brands.map(brand => encodeURIComponent(brand)).join(',')}` : '';
                const durationParam = durations.length > 0 ? `durations=${durations.join(',')}` : '';
                url = `http://58.235.71.202:8080/perfumes/search/filter?${brandParam}&${durationParam}`;
            }

            const response = await fetch(url);
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            const data = await response.json();
            setPerfumes(data);
            setFilteredPerfumes(data); // 초기 필터된 향수 설정
        } catch (error) {
            console.error('향수 데이터를 가져오는 중 오류가 발생했습니다:', error);
        }
        setLoading(false);
    };

    useEffect(() => {
        fetchPerfumes();
    }, []);

    const toggleDropdown = () => {
        setIsDropdownOpen(prev => !prev);
    };

    const toggleDurationDropdown = () => {
        setIsDurationDropdownOpen(prev => !prev);
    };

    const handleBrandSelect = (brand) => {
        setSelectedBrands(prevSelected => {
            if (prevSelected.includes(brand)) {
                return prevSelected.filter(b => b !== brand);
            } else {
                return [...prevSelected, brand];
            }
        });
    };

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

    const toggleLike = (id) => {
        setLikedPerfumes(prevLiked =>
            prevLiked.includes(id) ? prevLiked.filter(likedId => likedId !== id) : [...prevLiked, id]
        );
    };

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

    const handleSearch = () => {
        setSearchCompleted(true);
        fetchPerfumes(selectedBrands, selectedDurations);
    };

    const handleSearchInput = (event) => {
        setSearchTerm(event.target.value);
    };

    const handleKeyPress = (event) => {
        if (event.key === 'Enter' || event.key === ' ') {
            handleSearch();
        }
    };

    const handleSearchClick = () => {
        handleSearch();
    };

    const uniqueBrands = [...new Set(perfumes.map(perfume => perfume.brand.trim()))].sort();

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
                    <div key={perfume.id} className="perfume-item-2">
                        <img src={perfume.image} alt={perfume.perfume} />
                        <p className="brand">{perfume.brand}</p>
                        <div className="perfume">{perfume.perfume}</div>
                        <p className="acode">
                            {Array.isArray(perfume.acode) ? perfume.acode.map(ac => `#${ac}`).join(' ') : ''}
                        </p>
                        <div className="heart-icon" onClick={() => toggleLike(perfume.id)}>
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
