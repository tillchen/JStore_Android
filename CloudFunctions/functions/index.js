const functions = require('firebase-functions');

exports.onNewPostAdded = functions.firestore
    .document('posts/{postId}')
    .onCreate((snap, context) => {
        const newPost = snap.data();
        const title = newPost.title;
        const ownerName = newPost.ownerName;
        const category = newPost.category;
    });