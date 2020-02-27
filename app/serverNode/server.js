
const express = require('express')
const app = express()
const path = require('path')
const port = 3011

const pgClient = require('pg')
const sha1 = require('sha1')
var HashMap = require('hashmap');

const bodyParser = require('body-parser')
app.use(bodyParser.urlencoded({extended: true}))
app.use(bodyParser.json({limit: '10mb'}))

const MongoClient=require('mongodb').MongoClient;
const dsnMongoDB ='mongodb://localhost/';
const session = require('express-session')
const MongoDBStore = require('connect-mongodb-session')(session);





/**		Variable 	**/

var responseData = new Object();
var Theme = new Object();

/**		root 		**/
app.use(express.static('CERIGame'))


/**		Ecoute du port 3011		**/
var server = app.listen(port || 3011, function () {
  console.log('Le serveur ecoute le port '+ port +' !')
})

/**		Web Socker	**/
var io = require('socket.io').listen(server)
var socketClient = new HashMap()

io.on('connection', socket => {

	socket.emit('connection', {notif : "Bienvenue sur mon site !"})

	socket.on('login' , function(response) {

		socketClient.set(response.id,socket.id);

		socket.emit('isLog', {notif:"Vas tu réussir à me surpasser ??"})
		socket.broadcast.emit('connection', {notif: response.nom+" vient de se connecter."})
	})
	socket.on('logOut' ,  function(response) {
		socket.emit('isLog', {notif:"A la prochaine !"})
		socket.broadcast.emit('logOut', {notif: response.nom+" vient de se déconnecter."})
		socketClient.delete(response.id)
	})
	socket.on('defi', function(response) {
		MongoClient.connect("mongodb://localhost", {useUnifiedTopology: true, useNewUrlParser: true }, function(error, client) {
			if (error) throw error;
			const db = client.db('db');
	        db.collection("notification").insertOne({
	        	quiz : response.quiz,
	        	defier : response.defier,
	        	defiant : response.defiant,
	        	score : response.score,
	        	level : response.level
	        })
			client.close()
		})

		if(socketClient.has(response.defier)) {
			socket.to(socketClient.get(response.defier)).emit('challenge', {notif : "Vous venez d'être defier !"})
		}
	})
})


/**		Configuration session		**/
app.use(session({
    secret: 'U cant know',
    saveUninitialized: false,
    resave: false,
    store: new MongoDBStore({
        uri: 'mongodb://localhost/db',
        collection: 'SV_3011',
        touchAfter: 24 * 3600
    }),
    cookie: { maxAge: 24 * 360 * 1000 }
}))


/**		Catch requête de login   pedago **/
app.post('/login', function (req, res) {
	var username = req.body.username
	var password = sha1(req.body.password)

	var sql = "select * from fredouil.users where identifiant='"+ req.body.username+"';"

	var pool = new pgClient.Pool({username: 'uapv1400714', host: '127.0.0.1', database: 'etd', password: 'w5fE2D', port: 5432 }) 

	pool.connect(function(err, client, done) {
		if(err)  {console.log('Error connecting to pg server' + err.stack)}
		else {
			console.log('Connection established with pg db server')

			client.query(sql, (err, result) => {
				if(err){console.log('Erreur d’exécution de la requete' + err.stack)}	// et traitement du résultat
				else if((result.rows[0] != null) && (result.rows[0].motpasse == password)){

					var date = new Date();
					var dateFR = ("0" + (date.getDate())).slice(-2)+"/"+("0" + (date.getMonth() + 1)).slice(-2)+"/"+date.getFullYear()
					var hourUTC = new Date().toLocaleTimeString("fr", {hour12: false});

					responseData.date = dateFR
					responseData.hours = hourUTC

					req.session.isConnected = true
					responseData.data=result.rows[0].nom
					responseData.statusMsg='Connexion réussie : Bonjour '+result.rows[0].prenom
					responseData.nom = result.rows[0].prenom+" "+result.rows[0].nom
					responseData.msgHumeur = result.rows[0].humeur
					responseData.statusCo = true
					responseData.id = result.rows[0].id

					identifiant = username
					req.session.user = username

					client.query("update fredouil.users set statut = 1 where identifiant='"+ req.body.username+"';", (error,rsl) => {
						if(error){console.log('Erreur d’exécution de la requete' + error.stack)}
					})

					console.log(req.session.id +' expire dans '+req.session.cookie.maxAge) 
					
				 }
				 else{
				 	responseData.statusMsg="Echec de connexion, veuillez réessayer !"
				 	responseData.statusCo = false
					console.log('Connexion échouée : informations de connexion incorrecte')
				 }
				 res.send(responseData)
			})
			client.release()
		}
	})
})

/**		Catch requête de logOut  pedago **/
app.post('/logOut', function (req,res) {

	var sql = "update fredouil.users set statut = 0 where identifiant='"+ req.body.username+"';"

	var pool = new pgClient.Pool({username: 'uapv1400714', host: '127.0.0.1', database: 'etd', password: 'w5fE2D', port: 5432 }) 

	pool.connect(function(err, client, done) {
		client.query(sql, (error,rsl) => {
			if(error){console.log('Erreur d’exécution de la requete' + error.stack)}
			req.session.isConnected = false
			responseData.data = null
			responseData.statusMsg = "Déconnexion effectuer !"
			res.send(responseData)
		})
		client.release()
	})
})

/**		Catch requête de modifier l'humeur de l'utilisateur  pedago **/
app.post('/modifHumeur', function(req,res) {
	var sql = "update fredouil.users set humeur = '"+ req.body.humeur+"' where identifiant='"+ req.body.username+"';"

	var pool = new pgClient.Pool({username: 'uapv1400714', host: '127.0.0.1', database: 'etd', password: 'w5fE2D', port: 5432 }) 

	pool.connect(function(err, client, done) {
		client.query(sql, (error,rsl) => {
			if(error){console.log('Erreur d’exécution de la requete' + error.stack)}
		})
		client.release()
	})
})

/**		Catch requête de permettant de récupérer l'historique de l'utilisateur  pedago **/
app.post('/historique', function(req,res) {
	var sql = "select * from fredouil.users join fredouil.historique on fredouil.users.id = fredouil.historique.id_users where identifiant='"+ req.body.username+"';"
	var pool = new pgClient.Pool({username: 'uapv1400714', host: '127.0.0.1', database: 'etd', password: 'w5fE2D', port: 5432 }) 

	pool.connect(function(err, client, done) {
		client.query(sql, (error,rsl) => {
			if(error){console.log('Erreur d’exécution de la requete' + error.stack)}
			responseData.historique = rsl.rows;
			res.send(responseData)
		})
		client.release()
	})
})

/**		Catch requête de permettant de récupérer les thèmes stocké sur MongoDB   **/
app.post('/getTheme', function(req,res) {

	MongoClient.connect("mongodb://localhost", {useUnifiedTopology: true, useNewUrlParser: true }, function(error, client) {
		if (error) throw error;
		const db = client.db('db');
        db.collection("quizz").find().toArray(function (error, result) {
			if (error) throw error;

			result.forEach(function(reply,i) {
				Theme[i] = result[i].thème
			})
		res.send(Theme)
		})
		client.close()
	})
})

/**		Catch requête de permettant de récupérer le quizz d'un theme  MongoDB  **/
app.post('/getQuiz', function(req,res) {
	MongoClient.connect("mongodb://localhost", {useUnifiedTopology: true, useNewUrlParser: true }, function(error, client) {
		if (error) throw error;
		const db = client.db('db');
        db.collection("quizz").find({thème:req.body.theme}).toArray(function (error, result) {
			if (error) throw error;

			responseData.quiz = result[0].quizz
			res.send(responseData)
		})
		client.close()
	})
})

/**		Catch requête de permettant d'insert la partie de l'utilisateur dans historique   **/
app.post('/saveScore', function(req,res) {
	var sql = "select id from fredouil.users where identifiant='"+ req.body.username+"';"
	var pool = new pgClient.Pool({username: 'uapv1400714', host: '127.0.0.1', database: 'etd', password: 'w5fE2D', port: 5432 }) 

	var id_user;

	pool.connect(function(err, client, done) {
		client.query(sql, (error,rsl) => {
			if(error){console.log('Erreur d’exécution de la requete' + error.stack)}
			id_user = rsl.rows[0].id
			pool.connect(function(err, client, done) {

				sql = "insert into fredouil.historique (id_users, date, nbreponse, temps, score) values ('"+id_user+"', 'now()', '"+ req.body.nbreponse+"', '"+req.body.temps+"', '"+ req.body.score+"');"
				client.query(sql, (error,rsl) => {
					if(error){console.log('Erreur d’exécution de la requete' + error.stack)}
				})
			client.release()
			})
		})
		client.release()
	})
})

/**		Permet de recupérer la liste d'internaute connecté sur la base de donnée **/
app.post('/getInternaute', function(req, res) {
	var sql = "select id, identifiant from fredouil.users where statut = 1 order by identifiant ASC;"
	var pool = new pgClient.Pool({username: 'uapv1400714', host: '127.0.0.1', database: 'etd', password: 'w5fE2D', port: 5432 }) 

	pool.connect(function(err, client, done) {
		client.query(sql, (error,rsl) => {
			if(error){console.log('Erreur d’exécution de la requete' + error.stack)}
			responseData.listInternaute = rsl.rows
			res.send(responseData.listInternaute)
		})
		client.release()
	})
})

/**		Permet de recupérer la liste d'internaute présent sur la base de donnée **/
app.post('/getInternauteAll', function(req, res) {
	var sql = "select id, identifiant from fredouil.users order by identifiant ASC;"
	var pool = new pgClient.Pool({username: 'uapv1400714', host: '127.0.0.1', database: 'etd', password: 'w5fE2D', port: 5432 }) 

	pool.connect(function(err, client, done) {
		client.query(sql, (error,rsl) => {
			if(error){console.log('Erreur d’exécution de la requete' + error.stack)}
			responseData.listAll = rsl.rows
			res.send(responseData.listAll)
		})
		client.release()
	})
})

/**		Permet de recupérer la liste d'internaute présent sur la base de donnée **/
app.post('/getTop', function(req, res) {
	var sql = "select identifiant, score from fredouil.users join fredouil.historique on fredouil.users.id = fredouil.historique.id_users order by score DESC limit 10;"
	var pool = new pgClient.Pool({username: 'uapv1400714', host: '127.0.0.1', database: 'etd', password: 'w5fE2D', port: 5432 }) 

	pool.connect(function(err, client, done) {
		client.query(sql, (error,rsl) => {
			if(error){console.log('Erreur d’exécution de la requete' + error.stack)}
			responseData.topTen = rsl.rows
			res.send(responseData.topTen)
		})
		client.release()
	})
})

app.post('/getDefi', function(req, res) {
	MongoClient.connect("mongodb://localhost", {useUnifiedTopology: true, useNewUrlParser: true }, function(error, client) {
		if (error) throw error;
		const db = client.db('db');
        db.collection("notification").find({defier: req.body.id}).toArray(function (error, result) {
			if (error) throw error;

			console.log(result)
			console.log(req.body.id)
			responseData.defi = result
			res.send(responseData)
		})
		client.close()
	})
})
