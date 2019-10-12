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
    whatsApp: bool,
    phoneNumber: '', // empty if whatsApp is false
    joinDate: '',
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
```
