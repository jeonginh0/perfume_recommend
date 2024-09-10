import {BrowserRouter, Routes, Route} from 'react-router-dom';
import ReactDOM from 'react-dom/client';
import reportWebVitals from './reportWebVitals';

import './index.css';
import App from './App';

import React from 'react';
import Login from './pages/Login';
import FindEmail from './pages/FindEmail';
import FindPassword from './pages/FindPassword';
import Signup from './pages/Signup';
import SignupSuccess from './pages/SignupSuccess';
import Perfume from './pages/Perfume';
import Recommend from './pages/Recommend';
import Mypage from './pages/Mypage';
import Community from './pages/Community';
import SigninSuccess from './pages/SigninSuccess';
import PerfumeDetail from './pages/PerfumeDetail';
import WishList from './pages/WishList';

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
        <Route path="/perfumes/:id" element={<PerfumeDetail />} />
        <Route path='/wishlist' element={<WishList />} />
      </Routes>
    </BrowserRouter>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
