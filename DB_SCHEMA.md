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
    creationDate: '',
  }
},
posts: {
  postId: {
    postId: '',
    sold: bool,
    ownerId: '', // email
    ownerName: '',
    whatsApp: bool,
    phoneNumber: '', // empty if whatsApp is false
    title: '',
    category: '',
    condition: '',
    description: '',
    imageUrl: '',
    price: 0.00,
    paymentOptions: [],
    creationDate: '',
    soldDate: '', // empty is sold is false
  }
},
```
