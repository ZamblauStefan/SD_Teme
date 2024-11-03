import {HOST} from '../../commons/hosts';
import RestApiClient from "../../commons/api/rest-client";


const endpoint = {
    user: '/user',
    userWithSync: '/userWithSync',  // Endpoint pentru sincronizare
    device: '/device' //Endpoint pentru dispozitive
};

// Inserare utilizator cu sincronizare
function insertUserWithSync(user, callback) {
    let request = new Request(HOST.users_api + '/user' + endpoint.userWithSync, {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(user)
    });
    RestApiClient.performRequest(request, callback);
}

function getUsers(callback) {
    let request = new Request(HOST.users_api + endpoint.user, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`,
            'Content-Type': 'application/json'},
    });
    console.log(request.url);
    //RestApiClient.performRequest(request, callback);
    RestApiClient.performRequest(request, (result, status, error) => {
        if (error) {
            console.error("Error during getUsers:", error);
        } else {
            callback(result);
        }
    });
}

// Functia pentru stergerea utilizatorului
function deleteUser(userId, callback) {
    let request = new Request(HOST.users_api + endpoint.user + '/' + userId, {
        method: 'DELETE'
    });
    RestApiClient.performRequest(request, callback);
}


function getUserById(params, callback){
    let request = new Request(HOST.users_api + endpoint.user + params.id, {
       method: 'GET'
    });

    console.log(request.url);
    RestApiClient.performRequest(request, callback);
}

function postUser(user, callback){
    let request = new Request(HOST.users_api + endpoint.user , {
        method: 'POST',
        headers : {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(user)
    });

    console.log("URL: " + request.url);

    RestApiClient.performRequest(request, callback);
}


function updateUser(user, callback) {
    let request = new Request(HOST.users_api + endpoint.user + '/' + user.id, {
        method: 'PUT',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(user)
    });

    console.log("URL: " + request.url);

    RestApiClient.performRequest(request, callback);
}


// Functie pentru a prelua dispozitivele asociate unui utilizator
function getDevicesByUserId(userId, callback) {
    let request = new Request(HOST.devices_api + endpoint.device + '/user/' + userId, {
        method: 'GET',
    });

    console.log("Request URL:", HOST.devices_api + endpoint.device + '/user/' + userId);

    //console.log("Request URL:", request.url);

    RestApiClient.performRequest(request, callback);
}

// Functia pentru a obtine toate dispozitivele
function getDevices(callback) {
    let request = new Request(HOST.devices_api + endpoint.device, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        }
    });

    console.log(request.url);
    RestApiClient.performRequest(request, callback);
}

// Functia pentru a actualiza un dispozitiv
function updateDevice(device, callback) {
    let request = new Request(HOST.devices_api + endpoint.device + '/' + device.id, {
        method: 'PUT',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(device)
    });

    console.log("URL: " + request.url);
    RestApiClient.performRequest(request, callback);
}

// Functia pentru stergerea unui dispozitiv
function deleteDevice(deviceId, callback) {
    let request = new Request(HOST.devices_api + endpoint.device + '/' + deviceId, {
        method: 'DELETE',
    });

    console.log("Delete Device Request URL:", request.url);
    RestApiClient.performRequest(request, callback);
}

// Inserare dispozitiv
function insertDevice(device, callback) {
    let request = new Request(HOST.devices_api +'/device' +'/insertDeviceForUser', {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(device),
    });

    console.log("Insert Device Request URL:", request.url);
    RestApiClient.performRequest(request, callback);
}

// Functia pentru inserarea unui dispozitiv fără userID
function insertDeviceNoUser(device, callback) {
    let request = new Request(HOST.devices_api + endpoint.device + "/insertDeviceNoUser", {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(device),
    });

    console.log("Insert Device Request URL:", request.url);
    RestApiClient.performRequest(request, callback);
}



export {
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
