const fs = require('fs');
const https = require('https');
const bodyParser = require('body-parser')
const express = require('express');

const app = express();
app.use(bodyParser.urlencoded({limit:'50mb',extended: true}))

app.post('/', function(req,res) {

	let tabPho = []

	let namePho = Buffer.from(req.body.phoneme, 'base64').toString('ascii');
	tabPho.push(namePho);
    let dataWav = req.body.wav.replace('data:audio/wav; codecs=opus;base64,', '');
    //let buff = new Buffer(data, 'base64');
    fs.writeFile(namePho+".wav", dataWav, {encoding: 'base64'}, async function(err) {
        console.log('File created');
    });


    console.log(namePho);

    console.log(tabPho);


	sleep(5000, function() {
	   console.log("Wake up !")
	});



	fs.readFile('babrin.scores.txt', function (err , data) {
		if(err) throw err;

		const content = data;
		console.log(content.toString('UTF-8'));
	});
    res.send('Fini\n')




    res.on('finish', function() {
		removeFile(tabPho);
	});

	res.on('error', function() {
		removeFile(tabPho);
	});

});

options = {
	pfx: fs.readFileSync("ssl/crt.pfx"),
	passphrase: "Voicy2020"
}

const server = https.createServer(options, app);

server.listen(3211, () => {
    console.log("server starting on port : 3211");
});

function removeFile(tabPho) {
	tabPho.forEach(element => fs.unlinkSync(element+".wav"))
}
function print(param) {
	console.log(param);
}

function sleep(time, callback) {
    var stop = new Date().getTime();
    while(new Date().getTime() < stop + time) {
        ;
    }
    callback();
}


//curl --insecure --data "somedata" https://pedago.univ-avignon.fr:3211

