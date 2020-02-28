const fs = require('fs');
const https = require('https');
const bodyParser = require('body-parser')
const express = require('express');

const app = express();
app.use(bodyParser.urlencoded({extended: true}))


app.post('/', function(req,res) {

    console.log(req.body.phoneme)


/*    let data = req.wav;
    //let buff = new Buffer(data, 'base64');
    fs.writeFile('tmp.wav', data, {encoding: 'base64'}, async function(err) {
        console.log('File created');
    });*/


    res.send('Fini\n')

});

options = {
	pfx: fs.readFileSync("ssl/crt.pfx"),
	passphrase: "Voicy2020"
}

const server = https.createServer(options, app);

server.listen(3211, () => {
    console.log("server starting on port : 3211");
});







//curl --insecure --data "somedata" https://pedago.univ-avignon.fr:3211

