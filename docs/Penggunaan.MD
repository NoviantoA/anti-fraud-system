# API endpoints usage

Dokumen ini berisi contoh untuk semua endpoint yang dapat digunakan untuk berinteraksi dengan sistem. Di bawah ini Anda dapat menemukan
daftar semua proses yang dapat dilakukan menggunakan API.

- [Signup new user](#signup)
- [Login sebagai user yang terdaftar](#login)
- [Delete user yang terdaftar](#delete-user)
- [Get user list](#get-user-list)
- [Update role user](#update-user-role)
- [Update users access level](#update-user-access)
- [Post new transaction](#post-transaction)
- [Save ip mencurigakan](#save-suspicious-ip)
- [Delete ip mencurigakan](#delete-suspicious-ip)
- [Save pencurian card number](#save-stolen-card-number)
- [Delete pencurian card number](#delete-stolen-card-number)
- [Add transaction feedback](#add-transaction-feedback)
- [Get transaction history untuk nomor kartu yang diberikan](#get-transaction-history)

Daftar lengkap endpoint tersedia di tabel file utama [README](../README.md). Contoh berikut adalah
menggunakan format JSON.

## Signup

```
POST /api/auth/user
{
   "name": "<String value, not empty>",
   "username": "<String value, not empty>",
   "password": "<String value, not empty>"
}
```

Response:

```
{
    "id": 1,
    "name": "Novianto Anggoro",
    "username": "noviantoa",
    "role": "ADMINISTRATOR"
}
```

## Login

```
POST /api/auth/login
{
   "username": "<String value, not empty>",
   "password": "<String value, not empty>"
}
```

Response:

```
{
    "id": 1,
    "name": "Novianto Anggoro",
    "username": "noviantoa",
    "role": "ADMINISTRATOR"
}
```

_Catatan: Endpoints ini harus digunakan untuk mengotentikasi pengguna dengan otentikasi dasar http. Respons akan disimpan
di penyimpanan lokal browser._

## Delete user

```
DELETE /api/auth/user/{username}
```

Response:

```
{
   "username": "noviantoa",
   "status": "Deleted successfully!"
}
```

## Get user list

```
GET /api/auth/list
```

Response:

```
[
    {
        "id": <user1 id>,
        "name": "<user1 name>",
        "username": "<user1 username>",
        "role": "<user1 role>"
    },
     ...
    {
        "id": <userN id>,
        "name": "<userN name>",
        "username": "<userN username>",
        "role": "<userN role>"
    }
]
```

## Update user role

```
PUT /api/auth/role
{
   "username": "<String value, not empty>",
   "role": "<String value, not empty>"
}
```

Response:

```
{
   "id": <Long value, not empty>,
   "name": "<String value, not empty>",
   "username": "<String value, not empty>",
   "role": "<String value, not empty>"
}
```

## Update user access

```
PUT /api/auth/access
{
   "username": "<String value, not empty>",
   "operation": "<[LOCK, UNLOCK]>"  // menentukan apakah pengguna akan diaktifkan atau dinonaktifkan
}
```

Response:

```
{
    "status": "User <username> <[locked, unlocked]>!"
}
```

## Post transaction

```
POST /api/antifraud/transaction
{
  "amount": <Long>,
  "ip": "<String value, not empty>",
  "number": "<String value, not empty>",
  "region": "<String value, not empty>",
  "date": "yyyy-MM-ddTHH:mm:ss"
}
```

Response:

```
{
   "result": "ALLOWED",
   "info": "none"
}
```

## Save IP mencurigakan

```
POST /api/antifraud/suspicious-ip
{
  "ip": "<String value, not empty>"
}
```

Response:

```
{
   "id": "<Long value, not empty>",
   "ip": "<String value, not empty>"
}
```

## Delete IP mencurigakan

```
DELETE /api/antifraud/suspicious-ip/{ip}
```

Response:

```
{
   "status": "IP <ip address> successfully removed!"
}
```

## Save card number tercuri

```
POST /api/antifraud/stolencard
{
  "number": "<String value, not empty>"
}
```

Response:

```
{
   "id": "<Long value, not empty>",
   "number": "<String value, not empty>"
}
```

## Delete card number tercuri

```
DELETE /api/antifraud/stolencard/{number}
```

Response:

```
{
   "status": "Card <number> successfully removed!"
}
```

## Add transaction feedback

```
PUT /api/antifraud/transaction
{
   "transactionId": <Long>,
   "feedback": "<String>"
}
```

_FEED dapat berupa 'ALLOWED', 'MANUAL_PROCESSING' atau 'PROHIBITED'_.

Response:

```
{
  "transactionId": <Long>,
  "amount": <Long>,
  "ip": "<String value, not empty>",
  "number": "<String value, not empty>",
  "region": "<String value, not empty>",
  "date": "yyyy-MM-ddTHH:mm:ss",
  "result": "<String>",
  "feedback": "<String>"
}
```

## Get transaction history

```
GET /api/antifraud/history/{number}
```

Response:

```
[
    {
      "transactionId": <Long>,
      "amount": <Long>,
      "ip": "<String value, not empty>",
      "number": number,
      "region": "<String value, not empty>",
      "date": "yyyy-MM-ddTHH:mm:ss",
      "result": "<String>",
      "feedback": "<String>"
    },
     ...
    {
      "transactionId": <Long>,
      "amount": <Long>,
      "ip": "<String value, not empty>",
      "number": number,
      "region": "<String value, not empty>",
      "date": "yyyy-MM-ddTHH:mm:ss",
      "result": "<String>",
      "feedback": "<String>"
    }
]
```