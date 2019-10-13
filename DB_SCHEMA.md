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
    creationTime: '',
  }
},
posts: {
  postId: {
    sold: bool,
    ownerId: '',
    ownerName: '',
    title: '',
    category: '',
    condition: '',
    description: '',
    imageUrl: '',
    price: 0.0,
    paymentOptions: [],
    creationDate: '',
    soldDate: '', // empty is sold is false
  }
},
```
