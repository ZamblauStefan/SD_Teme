import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

function LoginPage() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const navigate = useNavigate();

    const handleLogin = async () => {
        // Corpul cererii de autentificare
        const requestBody = {
            username: username,
            password: password,
        };

        try {
            const response = await fetch('http://localhost:8081/user/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(requestBody),
            });

            if (response.ok) {
                const data = await response.json();
                console.log(data);  // Verifica daca `data` contine `userId`
                // Salveaza detalile userului in localStorage
                localStorage.setItem('userId', data.userId);
                localStorage.setItem('role', data.role); //salvam rolul
                localStorage.setItem('userName', data.name); //salvam numele
                localStorage.setItem('token', data.token); //salvam token (deocamdata nefolosit)

                // Redirectioneaza la pagina corespunzatoare rolului
                if (data.role === 'ADMIN') {
                    navigate('/admin');
                } else if (data.role === 'CLIENT') {
                    navigate('/client');
                }
            } else {
                alert('Autentificare esuata. Va rugam sa verificati credentialele.');
            }
        } catch (error) {
            console.error('Eroare la autentificare:', error);
        }
    };

    return (
        <div>
            <h1>Login Page</h1>
            <input
                type="text"
                placeholder="Username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
            />
            <input
                type="password"
                placeholder="Password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
            />
            <button onClick={handleLogin}>Login</button>
        </div>
    );
}

export default LoginPage;
