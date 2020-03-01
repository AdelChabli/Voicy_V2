const fs = require('fs');
// const Promise = require('promise');
const readline = require('readline');
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


	// sleep(5000, function() {
	//    console.log("Wake up !")
	// });

	formatData("babrin.scores.txt").then(function(response){
		res.send(response);	
	});

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

function sleep(time, callback) {
    var stop = new Date().getTime();
    while(new Date().getTime() < stop + time) {
        ;
    }
    callback();
}
function formatData(namefile) {
	return new Promise(resolve => {
		const readInterface = readline.createInterface({
		    input: fs.createReadStream(namefile),
			  output: process.stdout,
			  terminal: false
		});

		console.log(namefile)

		var phoneme = new Object();
		phoneme.name = namefile.substring(0, namefile.indexOf("."));
		phoneme.phoneAll = [];
		phoneme.global = new Object();

		var countPhone = 0;

		readInterface.on('line',function(line) {
			
			if(line.includes("pause")) return;

			//console.log(line);

		    if(line.includes("Average for")) {

		    	let phoneText = line.substring(line.indexOf('[')+1, line.indexOf(']')); 

		    	phoneme.phoneAll[countPhone] = new Object();
		    	phoneme.phoneAll[countPhone].phone = phoneText;

				let index = line.indexOf("(logVraisForce)");
		    	let subLine = line.substring(index+16);
		    	let ScoreContraint = subLine.substring(0, subLine.indexOf(" "));
		    	let ScoreNonContraint = subLine.substring(subLine.indexOf(" ")+20)

		    	phoneme.phoneAll[countPhone].AC = ScoreContraint;
		    	phoneme.phoneAll[countPhone].NC = ScoreNonContraint;

		    	countPhone++;
		    	// console.log("Phone ==> Score contraint : "+ ScoreContraint+ " || Score non contraint : "+ ScoreNonContraint );

		    }
		    if(line.includes("Global")) {

		    	let index = line.indexOf("(logVraisForce)");
		    	let subLine = line.substring(index+16);
		    	let ScoreContraint = subLine.substring(0, subLine.indexOf(" "));
		    	let ScoreNonContraint = subLine.substring(subLine.indexOf(" ")+20)

		    	phoneme.global.scoreContraint = ScoreContraint;
		    	phoneme.global.scoreNonContraint = ScoreNonContraint;

		    	// console.log("Global ===> Score contraint : "+ ScoreContraint +" || Score non contraint : "+ ScoreNonContraint);
		    }
		});
		readInterface.on('close', () => {
			resolve(phoneme);
		})
	});


}


//curl --insecure --data "somedata" https://pedago.univ-avignon.fr:3211

