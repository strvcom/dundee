const functions = require('firebase-functions')
const admin = require("firebase-admin")


admin.initializeApp(functions.config().firebase)

exports.saveNewUser = functions.auth.user().onCreate(event => {
	const user = event.data
	const firestore = admin.firestore()
	return firestore.collection('users').doc(user.uid).set({
		email: user.email,
		uid: user.uid,
		creationTime: user.metadata.creationTime,
	})
})