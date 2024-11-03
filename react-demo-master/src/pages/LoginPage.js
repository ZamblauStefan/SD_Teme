import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import BackgroundImg from '../commons/images/image0_0.jpg';

const backgroundStyle = {
    backgroundImage: `url(${BackgroundImg})`,
    backgroundPosition: 'center',
    backgroundSize: 'cover',
    backgroundRepeat: 'no-repeat',
    width: '100vw',
    height: '100vh',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
};

const formContainerStyle = {
    backgroundColor: 'rgba(255, 255, 255, 0.8)',
    padding: '20px',
    borderRadius: '10px',
    boxShadow: '0 4px 8px rgba(0, 0, 0, 0.1)',
    textAlign: 'center',
    width: '300px',
};

const inputStyle = {
    width: '100%',
    padding: '10px',
    margin: '10px 0',
    borderRadius: '5px',
    border: '1px solid #ccc',
};

const buttonStyle = {
    width: '100%',
    padding: '10px',
    borderRadius: '5px',
    border: 'none',
    backgroundColor: '#007bff',
    color: 'white',
    cursor: 'pointer',
    margin: '10px 0',
};


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
                console.log("User ID saved:", data.userId);

                localStorage.setItem('role', data.role); //salvam rolul
                console.log("Role saved:", data.role);

                localStorage.setItem('userName', data.name); //salvam numele
                console.log("User Name saved:", data.name);

                localStorage.setItem('token', data.token); //salvam token (deocamdata nefolosit)
                console.log("Token saved:", data.token);

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
        <div style={backgroundStyle}>
            <div style={formContainerStyle}>
                <h1>Login</h1>
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
        </div>
    );
}

            export default LoginPage;
