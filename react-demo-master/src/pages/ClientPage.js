import React, { useEffect, useState } from 'react';
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';
import { getDevicesByUserId, runProducerScript } from '../user/api/user-api';
import { HOST } from '../commons/hosts';
import { Line, Bar } from 'react-chartjs-2';
import BackgroundImg from '../commons/images/Dalle2.webp';
import '../commons/styles/project-style.css';

const ClientPage = () => {
    const [devices, setDevices] = useState([]);
    const [selectedDeviceId, setSelectedDeviceId] = useState(null);
    const [scriptOutput, setScriptOutput] = useState('');
    const [socketMessages, setSocketMessages] = useState([]);
    const [alertMessages, setAlertMessages] = useState([]);
    const [chartData, setChartData] = useState({
        labels: [],
        datasets: [{
            label: 'Energy Consumption (kWh)',
            data: [],
            fill: false,
            borderColor: 'rgba(75,192,192,1)',
        }],
    });
    const [chartType, setChartType] = useState('line');
    const userName = localStorage.getItem('userName');

    useEffect(() => {
        const userId = localStorage.getItem('userId');
        if (userId) {
            getDevicesByUserId(userId, (response) => {
                if (Array.isArray(response)) {
                    setDevices(response);
                } else {
                    console.error("Unexpected response format:", response);
                }
            });
        } else {
            console.error("User ID not found in localStorage");
        }
    }, []);

    useEffect(() => {
        console.log("Initializing WebSocket connection...");
        const token = localStorage.getItem('token');
        console.log("Token trimis pentru WebSocket:", token);
        const socket = new SockJS(`${HOST.monitor_api}/ws?token=${token}`); // conectare cu token
        //const socket = new SockJS(`${HOST.monitor_api}/ws`); // Conectare fără token
        const stompClient = Stomp.over(socket);

        stompClient.connect({}, () => {
            console.log("Connected to WebSocket: monitorizare");

            // Mesaje de monitorizare
            stompClient.subscribe('/topic/monitor', (message) => {
                if (message.body) {
                    const parsedMessage = JSON.parse(message.body);
                    if (parsedMessage.deviceId === selectedDeviceId) {
                        console.log("Mesaj pentru dispozitivul selectat: ", parsedMessage);
                        setSocketMessages(prevMessages => [...prevMessages, parsedMessage].slice(-3));

                        // Actualizează datele pentru chart
                        setChartData((prevChartData) => {
                            const newLabels = [...prevChartData.labels, new Date(parsedMessage.timestamp * 1000).toLocaleTimeString()];
                            const newData = [...prevChartData.datasets[0].data, parsedMessage.hourlyValue];
                            return {
                                ...prevChartData,
                                labels: newLabels,
                                datasets: [{
                                    ...prevChartData.datasets[0],
                                    data: newData,
                                }]
                            };
                        });
                    }
                }
            });

            //alerte
            stompClient.subscribe('/topic/alerts', (message) => {
                console.log("Alertă primită: ", message.body);
                if (message.body) {
                    setAlertMessages(prevAlerts => {
                        const newAlerts = [...prevAlerts, message.body];
                        // Limităm lista la ultimele 3 alerte
                        return newAlerts.slice(-3);
                    });
                }
            });
        });

        return () => {
            if (stompClient) {
                stompClient.disconnect();
            }
        };
    }, [selectedDeviceId]);

    // Funcție pentru a prelua datele istorice când se selectează un dispozitiv nou
    useEffect(() => {
        const fetchChartData = async () => {
            try {
                if (selectedDeviceId) {
                    const token = localStorage.getItem('token');
                    const response = await fetch(`${HOST.monitor_api}/energy/getDeviceMeasurements/${selectedDeviceId}`, {
                        method: 'GET',
                        headers: {
                            'Authorization': `Bearer ${token}`,
                            'Content-Type': 'application/json',
                        },
                    });

                    if (response.ok) {
                        const data = await response.json();

                        setChartData({
                            labels: data.map(item => new Date(item.timestamp * 1000).toLocaleTimeString()),
                            datasets: [{
                                label: 'Energy Consumption (kWh)',
                                data: data.map(item => item.hourlyValue),
                                fill: false,
                                borderColor: 'rgba(75,192,192,1)',
                            }]
                        });
                    } else {
                        console.error("Failed to fetch device measurements:", response.statusText);
                    }
                }
            } catch (error) {
                console.error("Error fetching device measurements:", error);
            }
        };

        fetchChartData();
    }, [selectedDeviceId]);// Această funcție se va apela de fiecare dată când se schimba `selectedDeviceId`


    const handleRunProducer = () => {
        if (selectedDeviceId) {
            runProducerScript(selectedDeviceId, (response) => {
                if (response) {
                    setScriptOutput(response);
                } else {
                    setScriptOutput('Error running producer script.');
                }
            });
        } else {
            setScriptOutput('Please select a device first.');
        }
    };

    const handleRowClick = (deviceId) => {
        setSelectedDeviceId(deviceId);
    };

    const handleChartTypeChange = (event) => {
        setChartType(event.target.value);
    };

    return (
        <div className="client-page-container" style={{ backgroundImage: `url(${BackgroundImg})` }}>
            {/* Secțiunea Dashboard */}
            <div className="dashboard-section">
                <h1>Client Dashboard</h1>
                <p>Welcome, {userName}! Here are your devices:</p>
                {devices.length > 0 ? (
                    <table className="table table-striped table-bordered">
                        <thead>
                        <tr>
                            <th className="table-header">Description</th>
                            <th className="table-header">Address</th>
                            <th className="table-header">Max Hourly Energy Consumption</th>
                        </tr>
                        </thead>
                        <tbody>
                        {devices.map((device) => (
                            <tr
                                key={device.id}
                                onClick={() => handleRowClick(device.id)}
                                style={{
                                    backgroundColor: selectedDeviceId === device.id ? 'rgba(0, 123, 255, 0.2)' : 'transparent',
                                    cursor: 'pointer'
                                }}
                            >
                                <td>{device.description}</td>
                                <td>{device.address}</td>
                                <td>{device.maxHourlyEnergyConsumption}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                ) : (
                    <p>No devices available.</p>
                )}
                <button
                    onClick={handleRunProducer}
                    className="btn btn-primary"
                    style={{ marginTop: '20px' }}
                >
                    Run Producer Script for Selected Device
                </button>
                {scriptOutput && (
                    <div style={{ marginTop: '20px', color: 'yellow' }}>
                        <h3>Script Output:</h3>
                        <pre>{scriptOutput}</pre>
                    </div>
                )}
            </div>

            {/* Secțiunea Chart */}
            <div className="chart-section">
                <h2>Chart</h2>
                <div>
                    <select onChange={handleChartTypeChange} value={chartType} style={{ marginLeft: '20px' }}>
                        <option value="line">Line Chart</option>
                        <option value="bar">Bar Chart</option>
                    </select>
                </div>
                {chartType === 'line' ? (
                    <Line data={chartData} />
                ) : (
                    <Bar data={chartData} />
                )}
            </div>

            {/* Secțiunea Mesaje */}
            <div className="messages-section">
                <div className="message-box">
                    <h3>Messages from Monitor</h3>
                    {socketMessages.length > 0 ? (
                        <ul>
                            {socketMessages.filter(msg => msg.deviceId === selectedDeviceId).slice(-3).map((msg, index) => (
                                <li key={index}>{`Time: ${msg.timestamp}, Device ID: ${msg.deviceId}, Value: ${msg.hourlyValue}`}</li>
                            ))}
                        </ul>
                    ) : (
                        <p>No messages for the selected device.</p>
                    )}
                </div>
                <div className="message-box">
                    <h3>Alerts</h3>
                    {alertMessages.length > 0 ? (
                        <ul>
                            {alertMessages.map((msg, index) => (
                                <li key={index} style={{color: 'red'}}>{msg}</li>
                            ))}
                        </ul>
                    ) : (
                        <p>No alerts for the selected device.</p>
                    )}
                </div>
            </div>
        </div>
    );
};

export default ClientPage;
