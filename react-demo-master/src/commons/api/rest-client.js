function performRequest(request, callback) {
    fetch(request)
        .then(function(response) {
            // Incearca sa obtii raspunsul ca text
            return response.text().then((text) => {
                try {
                    // Incearca sa parsezi raspunsul ca JSON
                    const json = JSON.parse(text);
                    callback(json, response.status, null);
                } catch (error) {
                    // Dacă raspunsul nu este JSON, trimite-l ca text
                    callback(text, response.status, null);
                }
            });
        })
        .catch(function(err) {
            // prinde orice altă eroare neașteptata si seteaza codul de eroare la 1
            callback(null, 1, err);
        });
}

module.exports = {
    performRequest
};
