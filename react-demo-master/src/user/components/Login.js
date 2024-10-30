import React, { useState } from 'react';
import { useHistory } from 'react-router-dom';

function Login() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const history = useHistory();

    const handleLogin = async (e) => {
        e.preventDefault();

        const loginData = { username, password };

        // Trimite cererea de login catre backend
        try {
            const response = await fetch('http://localhost:8081/api/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(loginData),
            });

            if (response.ok) {
                const data = await response.json();
                localStorage.setItem('role', data.role); // salvam rolul
                if (data.role === 'ADMIN') {
                    history.push('/admin');
                } else {
                    history.push('/client');
                }
            } else {
                alert('Autentificare esuata. Verifica credentialele.');
            }
        } catch (error) {
            console.error('Eroare la autentificare:', error);
            alert('Eroare la conectarea la server.');
        }
    };

    return (
        <div>
            <h2>Login</h2>
            <form onSubmit={handleLogin}>
                <label>
                    Username:
                    <input
                        type="text"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                    />
                </label>
                <br />
                <label>
                    Password:
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />
                </label>
                <br />
                <button type="submit">Login</button>
            </form>
        </div>
    );
}

export default Login;
