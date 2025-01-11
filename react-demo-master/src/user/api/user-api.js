import { HOST } from '../../commons/hosts';
import RestApiClient from "../../commons/api/rest-client";

const endpoint = {
    user: '/user',
    userWithSync: '/user/userWithSync',  // Endpoint pentru sincronizare
    device: '/device' // Endpoint pentru dispozitive
};

const getTokenHeader = () => {
    const token = localStorage.getItem('token');
    return token ? { 'Authorization': `Bearer ${token}` } : {};
};

// Inserare utilizator cu sincronizare
function insertUserWithSync(user, callback) {

    const token = localStorage.getItem('token');
    console.log("JWT Token transmis:", token); // Verificam token-ul

    let request = new Request(HOST.users_api + endpoint.userWithSync, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`,
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(user),
    });
    RestApiClient.performRequest(request, callback);
}

function getUsers(callback) {
    let request = new Request(HOST.users_api + endpoint.user, {
        method: 'GET',
        headers: {
            ...getTokenHeader(),
            'Content-Type': 'application/json',
        },
    });
    console.log(request.url);

    RestApiClient.performRequest(request, (result, status, error) => {
        if (error) {
            console.error("Error during getUsers:", error);
        } else {
            callback(result);
        }
    });
}

// Funcția pentru stergerea utilizatorului
function deleteUser(userId, callback) {
    const token = localStorage.getItem('token'); // Token JWT din localStorage

    console.log("Delete User Request:", {
        url: request.url,
        method: request.method,
        headers: request.headers
    });
    console.log("JWT Token for DELETE:", localStorage.getItem('token'));

    let request = new Request(HOST.users_api + endpoint.user + '/' + userId, {
        method: 'DELETE',
        headers: {
            'Authorization': `Bearer ${token}`, // Adăugăm tokenul în header
        },
    });

    console.log("Delete User Request URL:", request.url);
    RestApiClient.performRequest(request, callback);
}

function getUserById(params, callback) {
    let request = new Request(HOST.users_api + endpoint.user + params.id, {
        method: 'GET',
        headers: getTokenHeader(),
    });

    console.log(request.url);
    RestApiClient.performRequest(request, callback);
}

function postUser(user, callback) {
    let request = new Request(HOST.users_api + endpoint.user, {
        method: 'POST',
        headers: {
            ...getTokenHeader(),
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(user),
    });

    console.log("URL: " + request.url);
    RestApiClient.performRequest(request, callback);
}

function updateUser(user, callback) {
    let request = new Request(HOST.users_api + endpoint.user + '/' + user.id, {
        method: 'PUT',
        headers: {
            ...getTokenHeader(),
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(user),
    });

    console.log("URL: " + request.url);
    RestApiClient.performRequest(request, callback);
}

// Funcție pentru a prelua dispozitivele asociate unui utilizator
function getDevicesByUserId(userId, callback) {
    let request = new Request(HOST.devices_api + endpoint.device + '/user/' + userId, {
        method: 'GET',
        headers: getTokenHeader(),
    });

    console.log("Request URL:", request.url);
    RestApiClient.performRequest(request, callback);
}

// Funcția pentru a obține toate dispozitivele
function getDevices(callback) {
    const token = localStorage.getItem('token');
    let request = new Request(HOST.devices_api + endpoint.device, {
        method: 'GET',
        credentials: 'include',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json',
        },
    });

    console.log(request.url);
    RestApiClient.performRequest(request, callback);
}

// Funcția pentru a actualiza un dispozitiv
function updateDevice(device, callback) {
    let request = new Request(HOST.devices_api + endpoint.device + '/' + device.id, {
        method: 'PUT',
        headers: {
            ...getTokenHeader(),
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(device),
    });

    console.log("URL: " + request.url);
    RestApiClient.performRequest(request, callback);
}

// Funcția pentru stergerea unui dispozitiv
function deleteDevice(deviceId, callback) {
    let request = new Request(HOST.devices_api + endpoint.device + '/' + deviceId, {
        method: 'DELETE',
        headers: getTokenHeader(),
    });

    console.log("Delete Device Request URL:", request.url);
    RestApiClient.performRequest(request, callback);
}

// Inserare dispozitiv
function insertDevice(device, callback) {
    let request = new Request(HOST.devices_api + '/device/insertDeviceForUser', {
        method: 'POST',
        headers: {
            ...getTokenHeader(),
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(device),
    });

    console.log("Insert Device Request URL:", request.url);
    RestApiClient.performRequest(request, callback);
}

// Funcția pentru inserarea unui dispozitiv fără userID
function insertDeviceNoUser(device, callback) {
    let request = new Request(HOST.devices_api + endpoint.device + "/insertDeviceNoUser", {
        method: 'POST',
        headers: {
            ...getTokenHeader(),
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(device),
    });

    console.log("Insert Device Request URL:", request.url);
    RestApiClient.performRequest(request, callback);
}

// Funcția pentru rularea scriptului Producer cu un anumit deviceId
function runProducerScript(deviceId, callback) {
    let request = new Request(HOST.users_api + endpoint.user + "/run-producer/" + deviceId, {
        method: 'POST',
        headers: {
            ...getTokenHeader(),
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
    });

    console.log("Run Producer Script Request URL:", request.url);
    RestApiClient.performRequest(request, callback);
}

export {
    runProducerScript,
    insertUserWithSync,
    deleteUser,
    getUsers,
    getUserById,
    postUser,
    updateUser,
    getDevicesByUserId,
    getDevices,
    updateDevice,
    deleteDevice,
    insertDevice,
    insertDeviceNoUser
};
