# The Schema for Cloud Firestore Database

## About

Even though Cloud Firestore is a NoSQL DB, it's still a good idea to have
a schema:)

## Schema

```
users: {
  userId: {
    fullName: '',
    email: '',
    isWhatsApp: bool,
    phoneNumber: '', // empty if isWhatsApp is false
    joinDate: '',
    postsActive: {},
    postsSold: {},
    postsBought: {},
    chats: {}
  }
},
postsActive: {
  postId: {
    ownerId: '',
    title: '',
    category: '',
    condition: '',
    description: '',
    imageUrl: '',
    price: 0.0,
    paymentOptions: []
    creationDate: 0,
  }
},
postsSold: {
  postId: {
    ownerId: '',
    buyerId: '',
    title: '',
    category: '',
    condition: '',
    description: '',
    imageUrl: '',
    price: 0.0,
    paymentOptions: []
    creationDate: 0,
    soldDate: 0
  }
},
chats: {
  chatId: {
    aliceId: '',
    bobId: '',
    postId: '',
    lastMessageId: '',
    seen : false
  }
},
messages: {
  chatId: {
    messageId: {
      receiverId: '',
      senderId: '',
      timestamp: '',
      text: '',
      type: '',
      seen: true
    }
  }
}
```