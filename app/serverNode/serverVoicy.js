/**	Import module	**/

const fs = require('fs');
const readline = require('readline');
const https = require('https');
const bodyParser = require('body-parser')
const express = require('express');


/**	Variable d'environnement	**/
const PATH = "temp/";

/**	Init middleware express	**/
const app = express();
app.use(bodyParser.urlencoded({limit:'50mb',extended: true}))


/**	Catch en POST **/
app.post('/', async function(req,res) {

	let tabPho = [];
	let size = parseInt(req.body.size,10);


	/**	On sauvegarde toutes les wav qu'on reçoit de la requete	**/
	for(let o in req.body) {
		if(o.toString('ascii') == "size") continue;

		let format = PATH+o.toString('ascii').substring(0,o.toString('ascii').indexOf("."));
		tabPho.push(format);		
		saveFile(req.body[o],format);
	}


    /** Partie script **/
    console.log("Waiting 3s...")
	sleep(3000, function() {
	    console.log("Wake up !")
	});


	/**	Création de la réponse serveur **/

	let response = []
	for (let o in tabPho) {
		var output = await extractScore(tabPho[o]+".scores.txt");
		output = await extractLbl(tabPho[o]+".lbl",output);
		response.push(output);
	}

	res.send(response);

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


function saveFile(buffer, namePho) {
	let dataWav = buffer.replace('data:audio/wav; codecs=opus;base64,', '');
    fs.writeFile(namePho+".wav", dataWav, {encoding: 'base64'}, async function(err) {
        console.log('File created');
    });
}

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
function extractScore(namefile) {
	return new Promise(resolve => {
		const readInterface = readline.createInterface({
		    input: fs.createReadStream(namefile),
			  output: process.stdout,
			  terminal: false
		});

		console.log(namefile)

		var phoneme = new Object();
		phoneme.name = namefile.substring(namefile.indexOf("/")+1, namefile.indexOf("."));
		phoneme.phoneAll = [];
		phoneme.global = new Object();

		var countPhone = 0;

		readInterface.on('line',function(line) {
			
			if(line.includes("pause")) return;

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

		    }
		    if(line.includes("Global")) {

		    	let index = line.indexOf("(logVraisForce)");
		    	let subLine = line.substring(index+16);
		    	let ScoreContraint = subLine.substring(0, subLine.indexOf(" "));
		    	let ScoreNonContraint = subLine.substring(subLine.indexOf(" ")+20)

		    	phoneme.global.scoreContraint = ScoreContraint;
		    	phoneme.global.scoreNonContraint = ScoreNonContraint;

		    }
		});
		readInterface.on('close', () => {
			resolve(phoneme);
		})
	});


}
function extractLbl(namefile,response) {
	return new Promise(resolve => {
		
		const readInterface = readline.createInterface({
		    input: fs.createReadStream(namefile),
			  output: process.stdout,
			  terminal: false
		});

		var index = 0;
		readInterface.on('line',function(line) {
			if(line.includes('[new_sentence]') || line.includes('[pause]')) return;

			response.phoneAll[index].start = line.substring(0,line.indexOf(" "));
			subLine = line.substring(line.indexOf(" ")+1);
			response.phoneAll[index].end = subLine.substring(0,subLine.indexOf(" "));

			index++;			
		});
		readInterface.on('close', () => {
			resolve(response);
		})
	});
}