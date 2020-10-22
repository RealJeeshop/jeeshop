module.exports =
    [
        {
            "id": 1,
            "user": {
                "id": 1,
                "login": "admin@jeeshop.org",
                "firstname": "gerald",
                "lastname": "min",
                "gender": "m.",
                "phoneNumber": "",
                "address": null,
                "deliveryAddress": null,
                "birthDate": 1403042400000,
                "age": null,
                "creationDate": 1405807200000,
                "updateDate": null,
                "disabled": null,
                "activated": true,
                "preferredLocale": null,
                "newslettersSubscribed": null
            },
            "items": [{
                "id": 1,
                "productId": 1,
                "skuId": 1,
                "quantity": 1,
                "price": 142.0
            }],
            "discounts": [{
                "discountId": 1,
                "discountValue": 14.2
            }],
            "deliveryAddress": {
                "id": 1,
                "street": "Rue des lilas",
                "city": "Paris",
                "zipCode": "75001",
                "firstname": "Pierre",
                "lastname": "Durand",
                "gender": "male",
                "company": ""
            },
            "billingAddress": {
                "id": 1,
                "street": "Rue des lilas",
                "city": "Paris",
                "zipCode": "75001",
                "firstname": "Pierre",
                "lastname": "Durand",
                "gender": "male",
                "company": ""
            },
            "status": "CREATED",
            "creationDate": new Date().getTime(),
            "updateDate": new Date().getTime()
        }
    ]
