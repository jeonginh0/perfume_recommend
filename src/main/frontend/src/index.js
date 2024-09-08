import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';
import reportWebVitals from './reportWebVitals';
import {BrowserRouter, Routes} from "react-router-dom";

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
    <BrowserRouter>
        <Routes>
            <Route path='/' element={<App />} />
            <Route path='/Login' element={<Login />} />
            <Route path='/find_email' element={<FindEmail />} />
            <Route path='/find_password' element={<FindPassword />} />
            <Route path='/signup' element={<Signup />} />
            <Route path='/signup_success' element={<SignupSuccess />} />
            <Route path='/perfume' element={<Perfume />} />
            <Route path='/recommend' element={<Recommend />} />
            <Route path='/mypage' element={<Mypage />} />
            <Route path='/community' element={<Community />} />
            <Route path='/signin_success' element={<SigninSuccess />} />
        </Routes>
    </BrowserRouter>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
