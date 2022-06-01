const functions = require("firebase-functions");

const admin = require("firebase-admin");
admin.initializeApp();

// Create and Deploy Your First Cloud Functions
// https://firebase.google.com/docs/functions/write-firebase-functions

const database = admin.firestore();

exports.matchmaker = functions.region('europe-central2').firestore.document("matchmaking/{userId}")
    .onWrite(async (snapshot, context) => {

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
                    const room = database.collection('rooms').doc(userId);
                    t.set(room, {host: userId, client: user2.get("userId")});
                    t.delete(user1ref);
                    t.delete(user2ref);

                    const mainUser1 = database.collection('users').doc(userId);
                    const mainUser2 = database.collection('users').doc(user2.get("userId"));
                    t.update(mainUser1, {matchmakingState:"IN_ROOM", roomId: userId})
                    t.update(mainUser2, {matchmakingState:"IN_ROOM", roomId: userId})
                }
            });

            console.log('Transaction success!');
        } catch (e) {
            console.log('Transaction failure:', e);
        }

    });

exports.myCloudTimer = functions.region('europe-central2').firestore.document('rooms/{roomId}')
    .onCreate( (snapshot, context) => {
        const roomId = context.params.roomId;
        const timeInMs = snapshot.get("timeInMs")
        console.log("get "+snapshot.get("timeInMs"))

        try {
                //const doc = await room.get(room)
                //let timeInMs = room.get("timeInMs")
                let timeInSeconds = timeInMs / 1000;
                console.log('Cloud Timer was Started: ' + timeInSeconds);
                const totalTime = functionTimer(timeInSeconds,
                elapsedTime => {
                    room.update({ elapsedTime: elapsedTime });
                });
            console.log('Timer of ' + totalTime + ' has finished.');
             new Promise(resolve => setTimeout(resolve, 1000));

            console.log('Transaction success!');
        } catch (e) {
            console.log('Transaction failure:', e);
        }
    });

function functionTimer (seconds, call) {
    return new Promise((resolve, reject) => {
        if (seconds > 300) {
            reject('execution would take too long...');
            return;
        }
        let interval = setInterval(onInterval, 1000);
        let elapsedSeconds = 0;

        function onInterval () {
            if (elapsedSeconds >= seconds) {
                clearInterval(interval);
                call(0);
                resolve(elapsedSeconds);
                return;
            }
            call(seconds - elapsedSeconds);
            elapsedSeconds++;
        }
    });
}

exports.countdownTimerTest = functions.region('europe-central2').firestore.document('timer/{id}')
    .onCreate( (snapshot, context) => {
        const docId = context.params.id;
        const timerRef = database.collection('timer').doc(docId);
        const timeInMs = snapshot.get("timeInMs")
        console.log("get "+snapshot.get("timeInMs"))

        try {
            let timeInSeconds = timeInMs / 1000;
            console.log('Cloud Timer was Started: ' + timeInSeconds);
            
            while(timeInSeconds >= 0){
                timerRef.update({ timeLeft: timeInSeconds });
                sleep(1000);
                timeInSeconds--;
            }

            console.log('Timer of ' + timeInSeconds + ' has finished.');

        } catch (e) {
            console.log('Failure:', e);
        }
    });

    function sleep(ms) {
        return new Promise((resolve) => {
          setTimeout(resolve, ms);
        });
      }