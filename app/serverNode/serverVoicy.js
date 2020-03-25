/**	Import module	**/

const fs = require('fs');
const readline = require('readline');
const https = require('https');
const bodyParser = require('body-parser')
const express = require('express');


/**	Variable d'environnement	**/
const PATH = "input/";

/**	Init middleware express	**/
const app = express();
app.use(bodyParser.urlencoded({limit:'50mb',extended: true}))


/**	Catch en POST **/
app.post('/', async function(req,res) {

	let listnameFile = [];	//tableau de stockage
	let textExercice = "";	//String contenant le texte prononcé dans le cas d'un exercice de lecture
	let textTemporaite = [] // Permet de remettre les phrases dans l'ordre
	let size = parseInt(req.body.size,10);	//nombre d'élément attendu
	let type = req.body.type;	//type d'exercice attendu

/*	console.log("Type d'exercice = "+type);
	console.log(req.body);*/

	if(type == "") {res.status(400).send("Erreur de requete")}

	/**	On sauvegarde toutes les wav qu'on reçoit de la requete	**/
	for(let o in req.body) {
		if(o.toString('ascii') == "size") continue;
		if(o.toString('ascii') == "type") continue;

		if(type == "phrase") {
			if(o.toString('ascii').includes("input")) {
				let nameFile = PATH+o.toString('ascii');
				listnameFile.push(nameFile);		
				saveFile(req.body[o],nameFile);
			}else if(o.toString('ascii').includes("textScript")) {
				let index = parseInt(o.toString('ascii').substring(10),10);
				if(index != 1) textTemporaite[index-1] = req.body[o].toLowerCase(); // Uniquement dans le contexte des phrases de Mr Seguin basé sur lex.phon
				else textTemporaite[index-1] = req.body[o];
			}
		} else if(type == "logatome") {
			let nameFile = PATH+o.toString('ascii');
			listnameFile.push(nameFile);		
			saveFile(req.body[o],nameFile);
		}
	}

	console.log(textTemporaite);
	for(let i of textTemporaite) {
		textExercice += i;
		textExercice += " ";
	}
	textExercice = await formatCase(textExercice);
	console.log(textExercice);
	return;

    /** Partie script **/



	/**	Création de la réponse serveur **/

	let response = []
	for (let o in listnameFile) {
		var output = await extractScore(listnameFile[o]+".scores.txt");
		output = await extractLbl(listnameFile[o]+".lbl",output);
		response.push(output);
	}

	res.send(response);

    res.on('finish', function() {
		removeFile(listnameFile);
	});

	res.on('error', function() {
		removeFile(listnameFile);
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


function saveFile(buffer, nameFile) {
	let dataWav = buffer.replace('data:audio/wav; codecs=opus;base64,', '');
    fs.writeFile(nameFile+".wav", dataWav, {encoding: 'base64'}, async function(err) {
        console.log('File created');
    });
}

function removeFile(listnameFile) {
	listnameFile.forEach(element => fs.unlinkSync(element+".wav"))
}

function sleep(time, callback) {
    var stop = new Date().getTime();
    while(new Date().getTime() < stop + time) {
        ;
    }
    callback();
}
function extractScore(nameFile) {
	return new Promise(resolve => {
		const readInterface = readline.createInterface({
		    input: fs.createReadStream(nameFile),
			  output: process.stdout,
			  terminal: false
		});

		console.log(nameFile)

		var phoneme = new Object();
		phoneme.name = nameFile.substring(nameFile.indexOf("/")+1, nameFile.indexOf("."));
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
function extractLbl(nameFile,response) {
	return new Promise(resolve => {
		
		const readInterface = readline.createInterface({
		    input: fs.createReadStream(nameFile),
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

function formatCase(textExercice) {
	return new Promise(resolve => {
		textExercice = textExercice.replace(/'/g, '_');
		textExercice = textExercice.replace(/-/g, '_');
		textExercice = textExercice.replace(/é/g, 'e1');
		textExercice = textExercice.replace(/è/g, 'e2');
		textExercice = textExercice.replace(/ê/g, 'e3');
		textExercice = textExercice.replace(/ç/g, 'c5');
		textExercice = textExercice.replace(/î/g, 'i3');
		textExercice = textExercice.replace(/à/g, 'a2');

		resolve(textExercice);
	});
}