const functions = require("firebase-functions");

const admin = require("firebase-admin");
admin.initializeApp();

// Create and Deploy Your First Cloud Functions
// https://firebase.google.com/docs/functions/write-firebase-functions

const database = admin.firestore();

exports.matchmaker = functions.region('europe-central2').firestore.document("matchmaking/{userId}")
    .onWrite(async (snapshot, context) => {

        console.log("hej");

        const userId = context.params.userId;
        const user1ref = database.collection('matchmaking').doc(userId);
        const users = database.collectionGroup('matchmaking');

        try {
            await database.runTransaction(async (t) => {
                const doc = await t.get(users);
                let user2 = null;
                doc.forEach(user=>{
                    if(user.get("userId") !== context.params.userId){
                        console.log("user.userId")
                        console.log(user.get("userId"));
                        user2 = user;
                    }

                });
                if(user2 !== null){
                    const user2ref = database.collection('matchmaking').doc(user2.get("userId"))
                    const room = database.collection('rooms').doc();
                    t.set(room, {userId1: userId, userId2: user2.get("userId")});

                    t.delete(user1ref);
                    t.delete(user2ref);

                    const mainUser1 = database.collection('users').doc(userId);
                    const mainUser2 = database.collection('users').doc(user2.get("userId"));
                    t.update(mainUser1, {matchmakingState:"IN_ROOM"})
                    t.update(mainUser2, {matchmakingState:"IN_ROOM"})

                }
            });

            console.log('Transaction success!');
        } catch (e) {
            console.log('Transaction failure:', e);
        }

    });
