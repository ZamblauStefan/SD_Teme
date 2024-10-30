import React, { useEffect, useState } from 'react';
import { getUsers, updateUser } from '../user/api/user-api'; // Importa metodele pentru det si update user

function AdminPage() {
    const [users, setUsers] = useState([]);

    // Preluam utilizatorii din backend la incarcarea componentei
    useEffect(() => {
        console.log("AdminPage useEffect triggered");
        getUsers((response) => {
            console.log("Users response:", response); // Verifica daca se primeste datele corect din backend
            if (response && response.data) {
                setUsers(response.data);
                console.log("Users set in state:", response.data); // Log pentru datele setate
            }
        });
    }, []);

    // Functia de tratare a modificarilor in tabel
    const handleInputChange = (e, userId, field) => {
        const value = e.target.value;
        setUsers(prevUsers =>
            prevUsers.map(user =>
                user.id === userId ? { ...user, [field]: value } : user
            )
        );
    };

    // Functia pentru actualizarea utilizatorului în backend
    const handleBlur = (user) => {
        updateUser(user, (response, status) => {
            if (status === 200 || status === 201) {
                console.log("User updated successfully");
            } else {
                console.error("Failed to update user");
            }
        });
    };

    return (
        <div>
            <h1>Admin Page</h1>
            <p>Aici doar adminul poate avea acces.</p>
            <table>
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Role</th>
                    <th>Password</th>
                </tr>
                </thead>
                <tbody>
                {users.map((user) => (
                    <tr key={user.id}>
                        <td>
                            <input
                                type="text"
                                value={user.name}
                                onChange={(e) => handleInputChange(e, user.id, 'name')}
                                onBlur={() => handleBlur(user)}
                            />
                        </td>
                        <td>
                            <input
                                type="text"
                                value={user.role}
                                onChange={(e) => handleInputChange(e, user.id, 'role')}
                                onBlur={() => handleBlur(user)}
                            />
                        </td>
                        <td>
                            <input
                                type="password"
                                value={user.password}
                                onChange={(e) => handleInputChange(e, user.id, 'password')}
                                onBlur={() => handleBlur(user)}
                            />
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
}

export default AdminPage;
