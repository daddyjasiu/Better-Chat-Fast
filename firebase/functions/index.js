const functions = require("firebase-functions");

const admin = require("firebase-admin");
admin.initializeApp();

// Create and Deploy Your First Cloud Functions
// https://firebase.google.com/docs/functions/write-firebase-functions

const database = admin.firestore();

exports.matchmaker = functions.firestore.document("matchmaking/{userId}")
    .onCreate(async (snapshot, context) => {

        console.log("hej");

        const userId = context.params.userId;

        const user = database.collection('matchmaking').doc(userId);
        const room = database.collection('rooms').doc("XDDD")
        try {
            await database.runTransaction(async (t) => {
                const doc = await t.get(user)
                t.set(room, {user: userId});
            });

            console.log('Transaction success!');
        } catch (e) {
            console.log('Transaction failure:', e);
        }

    });

function generateRoomId() {
    const possibleChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    let roomId = "";
    for (let j = 0; j < 20; j++) roomId += possibleChars.charAt(Math.floor(Math.random() * possibleChars.length));
    return roomId;
}
