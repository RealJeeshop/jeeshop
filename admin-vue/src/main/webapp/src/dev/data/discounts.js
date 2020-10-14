module.exports =
    [
        {
            "id": 2,
            "name": "Order - Free delivery - first order",
            "description": "Free delivery fee for first order",
            "disabled": false,
            "startDate": null,
            "endDate": null,
            "visible": true,
            "localizedPresentation": null,
            "voucherCode": null,
            "usesPerCustomer": null,
            "type": "SHIPPING_FEE_DISCOUNT_AMOUNT",
            "triggerRule": "ORDER_NUMBER",
            "applicableTo": "ORDER",
            "triggerValue": 1.0,
            "discountValue": 12.0,
            "rateType": false,
            "uniqueUse": true
        },
        {
            "id": 5,
            "name": "10 percent off",
            "description": "10 percent off for order amount of 100",
            "disabled": false,
            "startDate": null,
            "endDate": null,
            "visible": true,
            "localizedPresentation": null,
            "voucherCode": null,
            "usesPerCustomer": null,
            "type": "DISCOUNT_RATE",
            "triggerRule": "AMOUNT",
            "applicableTo": "ORDER",
            "triggerValue": 100.0,
            "discountValue": 10.0,
            "rateType": true,
            "uniqueUse": null
        }
    ]