import React, { useState, useEffect } from 'react';
import axios from 'axios';

const UserList = () => {
    const [users, setUsers] = useState([]);

    useEffect(() => {
        fetchUsers();
    }, []);

    const fetchUsers = async () => {
        try {
            const response = await axios.get('http://localhost:8080/api/users');
            setUsers(response.data);
        } catch (error) {
            console.error('Error fetching users:', error);
        }
    };

    return (
        <div>
            <h2>User List</h2>
            <ul>
                {users.map(user => (
                    <li key={user.id}>
                        <strong>Nickname:</strong> {user.nickname}<br />
                        <strong>Email:</strong> {user.email}<br />
                        <strong>Phone Number:</strong> {user.phoneNumber}<br />
                        <strong>Preference Acode:</strong> {user.preferenceAcode}<br />
                        <strong>Preference Season:</strong> {user.preferenceSeason}<br />
                        <hr />
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default UserList;
