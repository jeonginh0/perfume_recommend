import React from 'react';
import { Link } from 'react-router-dom';
import '../css/WishList.css';
import Navbar from '../css/Navbar.js';


const WishList = () => {
    return (
        <>
            <Navbar />

            <div className="wishlist-container">
                <h2>찜 목록</h2>
                <div className="perfume-grid">
                    <div className="perfume-item-2">
                    </div>
                </div>
            </div>
        </>
    );
};

export default WishList;