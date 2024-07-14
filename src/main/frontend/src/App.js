import React from 'react';
import './App.css';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Main from './component/Main'; // 컴포넌트의 경로를 정확히 맞추어야 합니다.
import UserRegister from "./component/UserRegister";

function App() {
    return (
        <Router>
            <div>
                <Routes>
                    <Route path="/" element={<Main />} />
                    <Route path="/register" element={<UserRegister/>} />
                </Routes>
            </div>
        </Router>
    );
}

export default App;
